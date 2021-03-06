/*
 * Copyright 2018 Red Hat, Inc, and individual contributors.
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

package me.snowdrop.data.gcp.gae;


import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Query;
import me.snowdrop.data.core.query.AndCriteria;
import me.snowdrop.data.core.query.Condition;
import me.snowdrop.data.core.query.Criteria;
import me.snowdrop.data.core.query.OperationKey;
import me.snowdrop.data.core.query.OpsCriteriaConverter;
import me.snowdrop.data.core.query.OrCriteria;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class GaeFilterCriteriaConverter implements OpsCriteriaConverter<Query.Filter> {
    static final GaeFilterCriteriaConverter INSTANCE = new GaeFilterCriteriaConverter();

    @Override
    public Query.Filter convert(Criteria criteria) {
        return criteria.apply(this);
    }

    @Override
    public Query.Filter and(AndCriteria andCriteria) {
        List<Condition> conditions = andCriteria.conditions();
        if (conditions.size() == 1) {
            return toPredicate(conditions.get(0));
        } else {
            List<Query.Filter> filters = new ArrayList<>();
            for (Condition condition : conditions) {
                filters.add(toPredicate(condition));
            }
            return new Query.CompositeFilter(Query.CompositeFilterOperator.AND, filters);
        }
    }

    @Override
    public Query.Filter or(OrCriteria orCriteria) {
        List<Query.Filter> filters = new ArrayList<>();
        filters.add(convert(orCriteria.getLeft()));                
        filters.add(convert(orCriteria.getRight()));                
        return new Query.CompositeFilter(Query.CompositeFilterOperator.OR, filters);
    }

    private Query.Filter toPredicate(Condition condition) {
        return new Query.FilterPredicate(condition.getProperty().getName(), convert(condition), condition.getValue());
    }

    private Query.FilterOperator convert(Condition condition) {
        OperationKey key = condition.getKey();
        switch (key) {
            case EQUALS:
                return condition.isNegating() ? Query.FilterOperator.NOT_EQUAL : Query.FilterOperator.EQUAL;
            case IN:
                return Query.FilterOperator.IN;
            case GREATER:
                return Query.FilterOperator.GREATER_THAN;
            case GREATER_EQUAL:
                return Query.FilterOperator.GREATER_THAN_OR_EQUAL;
            case LESS:
                return Query.FilterOperator.LESS_THAN;
            case LESS_EQUAL:
                return Query.FilterOperator.LESS_THAN_OR_EQUAL;
            default:
                throw new IllegalArgumentException("Unsupported operator " + condition.getKey());
        }
    }
}
