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
import java.util.stream.Collectors;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.geo.Distance;
import org.springframework.util.Assert;

/**
 * @author Ales Justin
 */
public abstract class Criteria<Q> {

    @Override
    public String toString() {
        return "Criteria{" +
            ", conditions=" + conditions.stream().map(Condition::toString).collect(Collectors.joining("|")) +
            '}';
    }

    private Property property;
    private List<Condition> conditions = new ArrayList<>();

    public Criteria() {
    }

    public Criteria(String property) {
        this.property = new SimpleProperty(property);
    }

    public abstract Q apply(OpsCriteriaConverter<Q> converter);

    public List<Condition> conditions() {
        return conditions;
    }

    private Condition getCondition() {
        if (conditions.size() != 1) {
            throw new IllegalArgumentException("Invalid conditions size: " + conditions);
        }
        return conditions.get(0);
    }

    /**
     * Chain using {@code AND}
     *
     * @param criteria criteria
     * @return new criteria instance
     */
    public Criteria<Q> and(Criteria<Q> criteria) {
        Assert.notNull(criteria, "Cannot chain 'null' criteria.");
        this.conditions.addAll(criteria.conditions());
        return this;
    }

    /**
     * Chain using {@code OR}
     *
     * @param criteria criteria
     * @return new criteria instance
     */
    public Criteria<Q> or(Criteria<Q> criteria) {
        Assert.notNull(criteria, "Cannot chain 'null' criteria.");
        return new OrCriteria<>(this, criteria);
    }

    // --- OPS!

    /**
     * Crates new CriteriaEntry without any wildcards
     *
     * @param o value
     * @return new criteria instance
     */
    public Criteria<Q> is(Object o) {
        conditions.add(new Condition(property, OperationKey.EQUALS, o));
        return this;
    }

    /**
     * Crates new CriteriaEntry for is_null
     *
     * @return new criteria instance
     */
    public Criteria<Q> isNull() {
        conditions.add(new Condition(property, OperationKey.NULL));
        return this;
    }

    /**
     * Crates new CriteriaEntry for is_empty
     *
     * @return new criteria instance
     */
    public Criteria<Q> isEmpty() {
        conditions.add(new Condition(property, OperationKey.EMPTY));
        return this;
    }

    /**
     * Crates new CriteriaEntry with leading and trailing wildcards <br/>
     * <strong>NOTE: </strong> mind your schema as leading wildcards may not be supported and/or execution might be slow.
     *
     * @param s string
     * @return new criteria instance
     */
    public Criteria<Q> contains(String s) {
        conditions.add(new Condition(property, OperationKey.CONTAINS, s));
        return this;
    }

    /**
     * Crates new CriteriaEntry with trailing wildcard
     *
     * @param s string
     * @return new criteria instance
     */
    public Criteria<Q> startsWith(String s) {
        conditions.add(new Condition(property, OperationKey.STARTS_WITH, s));
        return this;
    }

    /**
     * Crates new CriteriaEntry with leading wildcard <br />
     * <strong>NOTE: </strong> mind your schema and execution times as leading wildcards may not be supported.
     *
     * @param s string
     * @return new criteria instance
     */
    public Criteria<Q> endsWith(String s) {
        conditions.add(new Condition(property, OperationKey.ENDS_WITH, s));
        return this;
    }

    /**
     * Crates new CriteriaEntry with trailing -
     *
     * @return new criteria instance
     */
    public Criteria<Q> not() {
        getCondition().setNegating(true);
        return this;
    }

    /**
     * Crates new CriteriaEntry with trailing ~
     *
     * @param s string
     * @return new criteria instance
     */
    public Criteria<Q> fuzzy(String s) {
        conditions.add(new Condition(property, OperationKey.FUZZY, s));
        return this;
    }

    /**
     * Crates new CriteriaEntry allowing native expressions
     *
     * @param s string
     * @return new criteria instance
     */
    public Criteria<Q> regexp(String s) {
        conditions.add(new Condition(property, OperationKey.REGEXP, s));
        return this;
    }

    /**
     * Boost positive hit with given factor. eg. ^2.3
     *
     * @param boost boost
     * @return new criteria instance
     */
    public Criteria<Q> boost(float boost) {
        if (boost < 0) {
            throw new InvalidDataAccessApiUsageException("Boost must not be negative.");
        }
        getCondition().setBoost(boost);
        return this;
    }

    /**
     * Crates new CriteriaEntry for {@code RANGE [lowerBound TO upperBound]}
     *
     * @param lowerBound lb
     * @param upperBound ub
     * @return new criteria instance
     */
    public Criteria<Q> between(Object lowerBound, Object upperBound) {
        if (lowerBound == null && upperBound == null) {
            throw new InvalidDataAccessApiUsageException("Range [* TO *] is not allowed");
        }

        conditions.add(new Condition(property, OperationKey.BETWEEN, new Object[]{lowerBound, upperBound}));
        return this;
    }

    /**
     * Crates new CriteriaEntry for {@code RANGE [* TO upperBound]}
     *
     * @param upperBound ub
     * @return new criteria instance
     */
    public Criteria<Q> lessThanEqual(Object upperBound) {
        if (upperBound == null) {
            throw new InvalidDataAccessApiUsageException("UpperBound can't be null");
        }
        conditions.add(new Condition(property, OperationKey.LESS_EQUAL, upperBound));
        return this;
    }

    public Criteria<Q> lessThan(Object upperBound) {
        if (upperBound == null) {
            throw new InvalidDataAccessApiUsageException("UpperBound can't be null");
        }
        conditions.add(new Condition(property, OperationKey.LESS, upperBound));
        return this;
    }

    /**
     * Crates new CriteriaEntry for {@code RANGE [lowerBound TO *]}
     *
     * @param lowerBound lb
     * @return new criteria instance
     */
    public Criteria<Q> greaterThanEqual(Object lowerBound) {
        if (lowerBound == null) {
            throw new InvalidDataAccessApiUsageException("LowerBound can't be null");
        }
        conditions.add(new Condition(property, OperationKey.GREATER_EQUAL, lowerBound));
        return this;
    }

    public Criteria<Q> greaterThan(Object lowerBound) {
        if (lowerBound == null) {
            throw new InvalidDataAccessApiUsageException("LowerBound can't be null");
        }
        conditions.add(new Condition(property, OperationKey.GREATER, lowerBound));
        return this;
    }

    /**
     * Crates new CriteriaEntry for multiple values {@code (arg0 arg1 arg2 ...)}
     *
     * @param values values
     * @return new criteria instance
     */
    public Criteria<Q> in(Object... values) {
        return in(toCollection(values));
    }

    /**
     * Crates new CriteriaEntry for multiple values {@code (arg0 arg1 arg2 ...)}
     *
     * @param values the collection containing the values to match against
     * @return new criteria instance
     */
    public Criteria<Q> in(Iterable<?> values) {
        Assert.notNull(values, "Collection of 'in' values must not be null");
        conditions.add(new Condition(property, OperationKey.IN, values));
        return this;
    }

    private List<Object> toCollection(Object... values) {
        if (values.length == 0 || (values.length > 1 && values[1] instanceof Collection)) {
            throw new InvalidDataAccessApiUsageException("At least one element "
                + (values.length > 0 ? ("of argument of type " + values[1].getClass().getName()) : "")
                + " has to be present.");
        }
        return Arrays.asList(values);
    }

    /**
     * Creates new CriteriaEntry for {@code location WITHIN distance}
     *
     * @param latitude  {@link org.springframework.data.geo.Point} latitude
     * @param longitude {@link org.springframework.data.geo.Point} longitude
     * @param distance  {@link Distance} distance
     * @return new criteria instance AbstractCriteria<Q> the chaind criteria with the new 'within' criteria included.
     */
    public Criteria<Q> within(Double latitude, Double longitude, Distance distance) {
        Assert.notNull(latitude, "Latitude must not be null");
        Assert.notNull(longitude, "Longitude must not be null");
        Assert.notNull(distance, "Distance must not be null");
        conditions.add(new Condition(property, OperationKey.WITHIN, new Object[]{latitude, longitude, distance}));
        return this;
    }
}
