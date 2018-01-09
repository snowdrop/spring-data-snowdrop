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

package me.snowdrop.data.gae.sdk;


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
class FilterCriteriaConverter implements OpsCriteriaConverter<Query.Filter> {
    static final FilterCriteriaConverter INSTANCE = new FilterCriteriaConverter();

    @Override
    public Query.Filter convert(Criteria<Query.Filter> criteria) {
        return criteria.apply(this);
    }

    @Override
    public Query.Filter and(AndCriteria<Query.Filter> andCriteria) {
        List<Query.Filter> filters = new ArrayList<>();
        for (Condition condition : andCriteria.conditions()) {
            filters.add(new Query.FilterPredicate(condition.getProperty().getName(), convert(condition), condition.getValue()));
        }
        return new Query.CompositeFilter(Query.CompositeFilterOperator.AND, filters);
    }

    @Override
    public Query.Filter or(OrCriteria<Query.Filter> orCriteria) {
        List<Query.Filter> filters = new ArrayList<>();
        filters.add(convert(orCriteria.getLeft()));                
        filters.add(convert(orCriteria.getRight()));                
        return new Query.CompositeFilter(Query.CompositeFilterOperator.OR, filters);
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
