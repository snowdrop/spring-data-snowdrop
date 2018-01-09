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

package me.snowdrop.data.core.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;

/**
 * Converts Criteria to queries.
 *
 * @author Ales Justin
 */
public abstract class AbstractCriteriaConverter<Q> implements OpsCriteriaConverter<Q> {

    private final QueryBuilder<Q> queryBuilder;

    public AbstractCriteriaConverter(QueryBuilder<Q> queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    protected abstract String getFieldName(Property property);

    public Q convert(Criteria<Q> root) {
        return root.apply(this);
    }

    public Q and(AndCriteria<Q> andCriteria) {
        List<Q> queries = new ArrayList<>();
        for (Condition condition : andCriteria.conditions()) {
            String fieldName = getFieldName(condition.getProperty());
            Q subQuery = convert(condition, fieldName);
            if (condition.isNegating()) {
                subQuery = queryBuilder.not(subQuery);
            }
            if (!Float.isNaN(condition.getBoost())) {
                subQuery = queryBuilder.boost(subQuery, condition.getBoost());
            }
            queries.add(subQuery);
        }
        return queryBuilder.all(queries);
    }

    public Q or(OrCriteria<Q> orCriteria) {
        Q leftQuery = orCriteria.getLeft().apply(this);
        Q rightQuery = orCriteria.getRight().apply(this);
        return queryBuilder.any(Arrays.asList(leftQuery, rightQuery));
    }

    private Q convert(Condition condition, String fieldName) {
        Object value = condition.getValue();

        switch (condition.getKey()) {
            case EQUALS:
                return queryBuilder.equal(fieldName, value);
            case NULL:
                return queryBuilder.isNull(fieldName);
            case IN:
                return queryBuilder.in(fieldName, (Collection<?>) value);
            case GREATER:
                return queryBuilder.greaterThan(fieldName, value);
            case GREATER_EQUAL:
                return queryBuilder.greaterThanOrEqual(fieldName, value);
            case LESS:
                return queryBuilder.lessThan(fieldName, value);
            case LESS_EQUAL:
                return queryBuilder.lessThanOrEqual(fieldName, value);
            case BETWEEN:
                Object[] ranges = (Object[]) value;
                return queryBuilder.between(fieldName, ranges[0], ranges[1]);
            case CONTAINS:
                return queryBuilder.contains(fieldName, value);
            case STARTS_WITH:
                return queryBuilder.startsWith(fieldName, value);
            case ENDS_WITH:
                return queryBuilder.endsWith(fieldName, value);
            case REGEXP:
                return queryBuilder.reqexp(fieldName, String.valueOf(value));
            case WITHIN:
                Object[] params = (Object[]) value;
                Double latitude = (Double) params[0];
                Double longitude = (Double) params[1];
                Distance distance = (Distance) params[2];
                double distanceInKm = toKm(distance);
                return queryBuilder.spatial(fieldName, latitude, longitude, distanceInKm);
            default:
                throw new IllegalArgumentException("Unsupported operator " + condition.getKey());
        }
    }

    public static double toKm(Distance distance) {
        Metric metric = distance.getMetric();
        if (Metrics.KILOMETERS.equals(metric)) {
            return distance.getValue();
        } else if (Metrics.MILES.equals(metric)) {
            return distance.getValue() * 1.609344;
        } else {
            throw new IllegalArgumentException("Unknown metric: " + metric);
        }
    }
}
