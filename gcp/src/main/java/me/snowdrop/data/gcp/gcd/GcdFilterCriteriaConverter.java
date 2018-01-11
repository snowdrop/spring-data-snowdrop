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

package me.snowdrop.data.gcp.gcd;


import java.util.List;

import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.Value;
import me.snowdrop.data.core.query.AndCriteria;
import me.snowdrop.data.core.query.Condition;
import me.snowdrop.data.core.query.Criteria;
import me.snowdrop.data.core.query.OperationKey;
import me.snowdrop.data.core.query.OpsCriteriaConverter;
import me.snowdrop.data.core.query.OrCriteria;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class GcdFilterCriteriaConverter implements OpsCriteriaConverter<StructuredQuery.Filter> {
    static final GcdFilterCriteriaConverter INSTANCE = new GcdFilterCriteriaConverter();

    @Override
    public StructuredQuery.Filter convert(Criteria criteria) {
        return criteria.apply(this);
    }

    @Override
    public StructuredQuery.Filter and(AndCriteria andCriteria) {
        List<Condition> conditions = andCriteria.conditions();
        if (conditions.size() == 1) {
            return toPredicate(conditions.get(0));
        } else {
            StructuredQuery.Filter first = toPredicate(conditions.get(0));
            StructuredQuery.Filter[] others = new StructuredQuery.Filter[conditions.size() - 1];
            for (int i = 1; i < conditions.size(); i++) {
                others[i - 1] = toPredicate(conditions.get(i));
            }
            return StructuredQuery.CompositeFilter.and(first, others);
        }
    }

    @Override
    public StructuredQuery.Filter or(OrCriteria orCriteria) {
        throw new UnsupportedOperationException("GCD currently only supports AND filters.");
    }

    private StructuredQuery.Filter toPredicate(Condition condition) {
        if (condition.isNegating()) {
            throw new IllegalArgumentException("Cannot negate condition in GCD.");
        }

        String property = condition.getProperty().getName();
        Value<?> value = Values.toValue(condition.getValue());

        OperationKey key = condition.getKey();
        switch (key) {
            case EQUALS:
                return StructuredQuery.PropertyFilter.eq(property, value);
            case GREATER:
                return StructuredQuery.PropertyFilter.gt(property, value);
            case GREATER_EQUAL:
                return StructuredQuery.PropertyFilter.ge(property, value);
            case LESS:
                return StructuredQuery.PropertyFilter.lt(property, value);
            case LESS_EQUAL:
                return StructuredQuery.PropertyFilter.le(property, value);
            case NULL:
                return StructuredQuery.PropertyFilter.isNull(property);
            default:
                throw new IllegalArgumentException("Unsupported operator " + condition.getKey());
        }
    }
}
