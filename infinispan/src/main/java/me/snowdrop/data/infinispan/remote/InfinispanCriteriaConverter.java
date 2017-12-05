/*
 * Copyright 2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.snowdrop.data.infinispan.remote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.snowdrop.data.hibernatesearch.core.query.Criteria;
import me.snowdrop.data.hibernatesearch.core.query.CriteriaConverter;
import org.infinispan.query.dsl.Expression;
import org.infinispan.query.dsl.FilterConditionBeginContext;
import org.infinispan.query.dsl.FilterConditionContext;
import org.infinispan.query.dsl.FilterConditionEndContext;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryBuilder;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class InfinispanCriteriaConverter implements CriteriaConverter<Query> {
    private final QueryBuilder queryBuilder;

    public InfinispanCriteriaConverter(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public Query convert(Criteria root) {
        FilterConditionContext first = null;
        List<Criteria> criteriaChain = root.getCriteriaChain();
        for (int i = 0; i < criteriaChain.size(); i++) {
            Criteria criteria = criteriaChain.get(i);
            String fieldName = criteria.getProperty().getName();
            Expression property = Expression.property(fieldName);
            boolean isNegating = criteria.isNegating();
            List<Criteria.CriteriaEntry> entries = new ArrayList<>(criteria.getQueryCriteriaEntries());
            for (int j = 0; j < entries.size(); j++) {
                Criteria.CriteriaEntry entry = entries.get(j);
                FilterConditionBeginContext begin = (first == null ? queryBuilder : (FilterConditionBeginContext) first);
                if (isNegating) {
                    begin = begin.not();
                }
                FilterConditionEndContext context = begin.having(property);
                FilterConditionContext next = convert(entry, context);
                if (j < entries.size() - 1 || i < criteriaChain.size() - 1) {
                    if (criteria.isOr()) {
                        begin = next.or();
                    } else {
                        begin = next.and();
                    }
                    first = (FilterConditionContext) begin;
                } else {
                    first = next;
                }
            }
        }
        return first.toBuilder().build();
    }

    private FilterConditionContext convert(Criteria.CriteriaEntry entry, FilterConditionEndContext context) {
        Object value = entry.getValue();

        switch (entry.getKey()) {
            case EQUALS:
                return context.equal(value);
            case NULL:
                return context.isNull();
            case IN:
                return context.in((Collection<?>) value);
            case GREATER:
                return context.gt(value);
            case GREATER_EQUAL:
                return context.gte(value);
            case LESS:
                return context.lt(value);
            case LESS_EQUAL:
                return context.lte(value);
            case BETWEEN:
                Object[] ranges = (Object[]) value;
                return context.between(ranges[0], ranges[1]);
            case CONTAINS:
                return context.like("%" + value + "%");
            case STARTS_WITH:
                return context.like("%" + value + "%"); // TODO -- add test just for this
            case ENDS_WITH:
                return context.like("%" + value + "%"); // TODO -- add test just for this
            default:
                throw new IllegalArgumentException("Unsupported operator " + entry.getKey());
        }
    }
}
