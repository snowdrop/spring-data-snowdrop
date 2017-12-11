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

import java.util.Collection;
import java.util.List;

import me.snowdrop.data.hibernatesearch.core.query.AndCriteria;
import me.snowdrop.data.hibernatesearch.core.query.Condition;
import me.snowdrop.data.hibernatesearch.core.query.Criteria;
import me.snowdrop.data.hibernatesearch.core.query.CriteriaConverter;
import me.snowdrop.data.hibernatesearch.core.query.OrCriteria;
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

    public Query convert(Criteria<Query> root) {
        FilterConditionContext first = convert(queryBuilder, root);
        return first.toBuilder().build();
    }

    public FilterConditionContext convert(FilterConditionBeginContext first, Criteria<Query> current) {
        if (current instanceof OrCriteria) {
            OrCriteria<Query> orCriteria = (OrCriteria<Query>) current;
            return fromOrCriteria(first, orCriteria);
        } else {
            AndCriteria<Query> andCriteria = (AndCriteria<Query>) current;
            return fromAndCriteria(first, andCriteria);
        }
    }

    private FilterConditionContext fromOrCriteria(FilterConditionBeginContext begin, OrCriteria<Query> orCriteria) {
        Criteria<Query> left = orCriteria.getLeft();
        FilterConditionContext first = convert(begin, left);
        Criteria<Query> right = orCriteria.getRight();
        begin = first.or();
        return convert(begin, right);
    }

    private FilterConditionContext fromAndCriteria(FilterConditionBeginContext begin, AndCriteria<Query> andCriteria) {
        FilterConditionContext first = null;
        List<Condition> conditions = andCriteria.conditions();
        for (int i = 0; i < conditions.size(); i++) {
            Condition condition = conditions.get(i);
            if (condition.isNegating()) {
                begin = begin.not();
            }
            if (!Float.isNaN(condition.getBoost())) {
                // TODO boost
            }
            Expression property = Expression.property(condition.getProperty().getName());
            FilterConditionEndContext having = begin.having(property);
            FilterConditionContext next = convert(condition, having);
            if (i < conditions.size() - 1) {
                begin = next.and();
            } else {
                first = next;
            }
        }
        return first;
    }

    private FilterConditionContext convert(Condition entry, FilterConditionEndContext context) {
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
