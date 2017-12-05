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

package me.snowdrop.data.hibernatesearch.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;

/**
 * Converts Criteria to queries.
 *
 * @author Ales Justin
 */
public abstract class AbstractCriteriaConverter<Q, S> implements CriteriaConverter<Q> {

    private final QueryBuilder<Q, S> queryBuilder;

    public AbstractCriteriaConverter(QueryBuilder<Q, S> queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public Q convert(Criteria criteria) {
        List<Q> shouldQueryList = new LinkedList<>();
        List<Q> mustNotQueryList = new LinkedList<>();
        List<Q> mustQueryList = new LinkedList<>();

        ListIterator<Criteria> chainIterator = criteria.getCriteriaChain().listIterator();

        Q firstQuery = null;
        boolean negateFirstQuery = false;

        while (chainIterator.hasNext()) {
            Criteria chainedCriteria = chainIterator.next();
            Q queryFragmentForCriteria = processCriteriaEntries(chainedCriteria);
            if (queryFragmentForCriteria != null) {
                if (firstQuery == null) {
                    firstQuery = queryFragmentForCriteria;
                    negateFirstQuery = chainedCriteria.isNegating();
                    continue;
                }
                if (chainedCriteria.isOr()) {
                    shouldQueryList.add(queryFragmentForCriteria);
                } else if (chainedCriteria.isNegating()) {
                    mustNotQueryList.add(queryFragmentForCriteria);
                } else {
                    mustQueryList.add(queryFragmentForCriteria);
                }
            }
        }

        if (firstQuery != null) {
            if (!shouldQueryList.isEmpty() && mustNotQueryList.isEmpty() && mustQueryList.isEmpty()) {
                shouldQueryList.add(0, firstQuery);
            } else {
                if (negateFirstQuery) {
                    mustNotQueryList.add(0, firstQuery);
                } else {
                    mustQueryList.add(0, firstQuery);
                }
            }
        }

        List<Q> queries = new ArrayList<>();
        if (!shouldQueryList.isEmpty()) {
            queries.add(queryBuilder.any(shouldQueryList));
        }
        if (!mustNotQueryList.isEmpty()) {
            queries.add(queryBuilder.not(queryBuilder.any(mustNotQueryList)));
        }
        if (!mustQueryList.isEmpty()) {
            queries.add(queryBuilder.all(mustQueryList));
        }
        return queryBuilder.all(queries);
    }

    protected Q processCriteriaEntries(Criteria criteria) {
        List<Q> queries = new ArrayList<>();
        for (Criteria.CriteriaEntry criteriaEntry : criteria.getQueryCriteriaEntries()) {
            Q subQuery = processCriteriaEntry(criteria.getProperty(), criteriaEntry);
            if (!Float.isNaN(criteria.getBoost())) {
                subQuery = queryBuilder.boost(subQuery, criteria.getBoost());
            }
            queries.add(subQuery);
        }
        return queryBuilder.all(queries);
    }

    protected abstract String getFieldName(Property property);

    protected Q processCriteriaEntry(Property property, Criteria.CriteriaEntry criteriaEntry) {
        Object value = criteriaEntry.getValue();

        String fieldName = getFieldName(property);

        switch (criteriaEntry.getKey()) {
            case EQUALS:
                return queryBuilder.equal(fieldName, value);
            case NULL:
                return queryBuilder.isNull(fieldName);
            case EMPTY: // TODO
                return queryBuilder.isEmpty(fieldName);
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
                return queryBuilder.reqexp(fieldName, (String) value);
            case FUZZY:
                return queryBuilder.fuzzy(fieldName, value);
            case WITHIN:
                Object[] params = (Object[]) value;
                Double latitude = (Double) params[0];
                Double longitude = (Double) params[1];
                Distance distance = (Distance) params[2];
                double distanceInKm = toKm(distance);
                return queryBuilder.spatial(fieldName, latitude, longitude, distanceInKm);
            default:
                throw new IllegalArgumentException("Unknown operator " + criteriaEntry.getKey());
        }
    }

    private static double toKm(Distance distance) {
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
