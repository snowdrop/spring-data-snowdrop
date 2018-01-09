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

import me.snowdrop.data.core.query.AndCriteria;
import me.snowdrop.data.core.query.Condition;
import me.snowdrop.data.core.query.Criteria;
import me.snowdrop.data.core.query.CriteriaConverter;
import me.snowdrop.data.core.query.OrCriteria;
import org.infinispan.query.dsl.Expression;
import org.infinispan.query.dsl.FilterConditionContext;
import org.infinispan.query.dsl.FilterConditionEndContext;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryBuilder;
import org.infinispan.query.dsl.QueryFactory;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class InfinispanCriteriaConverter implements CriteriaConverter<Query> {
    private final QueryFactory queryFactory;
    private final QueryBuilder queryBuilder;
    private boolean usedQB;

    public InfinispanCriteriaConverter(QueryFactory queryFactory, QueryBuilder queryBuilder) {
        this.queryFactory = queryFactory;
        this.queryBuilder = queryBuilder;
    }

    public Query convert(Criteria<Query> root) {
        return convertInternal(root).toBuilder().build();
    }

    private FilterConditionContext convertInternal(Criteria<Query> current) {
        if (current instanceof OrCriteria) {
            OrCriteria<Query> orCriteria = (OrCriteria<Query>) current;
            return or(orCriteria);
        } else {
            AndCriteria<Query> andCriteria = (AndCriteria<Query>) current;
            return and(andCriteria);
        }
    }

    private FilterConditionContext and(AndCriteria<Query> andCriteria) {
        List<Condition> conditions = andCriteria.conditions();
        FilterConditionContext first = fromCondition(conditions.get(0));
        for (int i = 1; i < conditions.size(); i++) {
            first = first.and(fromCondition(conditions.get(i)));
        }
        return first;
    }

    private FilterConditionContext or(OrCriteria<Query> orCriteria) {
        FilterConditionContext left = convertInternal(orCriteria.getLeft());
        return left.or(convertInternal(orCriteria.getRight()));
    }

    // TODO -- boost
    private FilterConditionContext fromCondition(Condition condition) {
        Expression property = Expression.property(condition.getProperty().getName());
        FilterConditionEndContext having;
        if (!usedQB) {
            usedQB = true;
            if (condition.isNegating()) {
                having = queryBuilder.not().having(property);
            } else {
                having = queryBuilder.having(property);
            }
        } else {
            if (condition.isNegating()) {
                having = queryFactory.not().having(property);
            } else {
                having = queryFactory.having(property);
            }
        }
        return convert(condition, having);
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
