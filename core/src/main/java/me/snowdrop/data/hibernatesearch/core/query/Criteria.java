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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Distance;
import org.springframework.util.Assert;

/**
 * Criteria is the central class when constructing queries. It follows more or less a fluent API style, which allows to
 * easily chain together multiple criteria.
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Franck Marchand
 * @author Ales Justin
 */
public class Criteria {

    @Override
    public String toString() {
        return "Criteria{" +
            "property=" + property.getName() +
            ", boost=" + boost +
            ", negating=" + negating +
            ", queryCriteria=" + StringUtils.join(queryCriteria, '|') +
            '}';
    }

    public static final String WILDCARD = "*";
    public static final String CRITERIA_VALUE_SEPERATOR = " ";

    private static final String OR_OPERATOR = " OR ";
    private static final String AND_OPERATOR = " AND ";

    private Property property;
    private float boost = Float.NaN;
    private boolean negating = false;

    private List<Criteria> criteriaChain = new ArrayList<>(1);

    private Set<CriteriaEntry> queryCriteria = new LinkedHashSet<>();

    public Criteria() {
    }

    /**
     * Creates a new Criteria with provided property name
     *
     * @param propertyName the property name
     */
    public Criteria(String propertyName) {
        this(new SimpleProperty(propertyName));
    }

    /**
     * Creates a new Criteria for the given field
     *
     * @param property the property
     */
    public Criteria(Property property) {
        Assert.notNull(property, "Field for criteria must not be null");
        Assert.hasText(property.getName(), "Field.name for criteria must not be null/empty");
        this.criteriaChain.add(this);
        this.property = property;
    }

    protected Criteria(List<Criteria> criteriaChain, String fieldname) {
        this(criteriaChain, new SimpleProperty(fieldname));
    }

    protected Criteria(List<Criteria> criteriaChain, Property property) {
        Assert.notNull(criteriaChain, "CriteriaChain must not be null");
        Assert.notNull(property, "Field for criteria must not be null");
        Assert.hasText(property.getName(), "Field.name for criteria must not be null/empty");

        this.criteriaChain.addAll(criteriaChain);
        this.criteriaChain.add(this);
        this.property = property;
    }

    /**
     * Static factory method to create a new Criteria for propertyName with given name
     *
     * @param propertyName the propertyName
     * @return new criteria instance
     */
    public static Criteria where(String propertyName) {
        return where(new SimpleProperty(propertyName));
    }

    /**
     * Static factory method to create a new Criteria for provided field
     *
     * @param property the prooperty
     * @return new criteria instance
     */
    public static Criteria where(Property property) {
        return new Criteria(property);
    }

    /**
     * Chain using {@code AND}
     *
     * @param property
     * @return new criteria instance
     */
    public Criteria and(Property property) {
        return new Criteria(this.criteriaChain, property);
    }

    /**
     * Chain using {@code AND}
     *
     * @param propertyName the property name
     * @return new criteria instance
     */
    public Criteria and(String propertyName) {
        return new Criteria(this.criteriaChain, propertyName);
    }

    /**
     * Chain using {@code AND}
     *
     * @param criteria
     * @return new criteria instance
     */
    public Criteria and(Criteria criteria) {
        this.criteriaChain.add(criteria);
        return this;
    }

    /**
     * Chain using {@code AND}
     *
     * @param criterias
     * @return new criteria instance
     */
    public Criteria and(Criteria... criterias) {
        this.criteriaChain.addAll(Arrays.asList(criterias));
        return this;
    }

    /**
     * Chain using {@code OR}
     *
     * @param property
     * @return new criteria instance
     */
    public Criteria or(Property property) {
        return new OrCriteria(this.criteriaChain, property);
    }

    /**
     * Chain using {@code OR}
     *
     * @param criteria
     * @return new criteria instance
     */
    public Criteria or(Criteria criteria) {
        Assert.notNull(criteria, "Cannot chain 'null' criteria.");

        Criteria orConnectedCritiera = new OrCriteria(this.criteriaChain, criteria.getProperty());
        orConnectedCritiera.queryCriteria.addAll(criteria.queryCriteria);
        return orConnectedCritiera;
    }

    /**
     * Chain using {@code OR}
     *
     * @param fieldName
     * @return new criteria instance
     */
    public Criteria or(String fieldName) {
        return or(new SimpleProperty(fieldName));
    }

    /**
     * Crates new CriteriaEntry without any wildcards
     *
     * @param o
     * @return new criteria instance
     */
    public Criteria is(Object o) {
        queryCriteria.add(new CriteriaEntry(OperationKey.EQUALS, o));
        return this;
    }

    /**
     * Crates new CriteriaEntry without any wildcards
     *
     * @param o
     * @return new criteria instance
     */
    public Criteria isNot(Object o) {
        queryCriteria.add(new CriteriaEntry(OperationKey.NOT_EQUALS, o));
        return this;
    }

    /**
     * Crates new CriteriaEntry for is_null
     *
     * @return new criteria instance
     */
    public Criteria isNull() {
        queryCriteria.add(new CriteriaEntry(OperationKey.NULL));
        return this;
    }

    /**
     * Crates new CriteriaEntry for is_empty
     *
     * @return new criteria instance
     */
    public Criteria isEmpty() {
        queryCriteria.add(new CriteriaEntry(OperationKey.EMPTY));
        return this;
    }

    /**
     * Crates new CriteriaEntry with leading and trailing wildcards <br/>
     * <strong>NOTE: </strong> mind your schema as leading wildcards may not be supported and/or execution might be slow.
     *
     * @param s
     * @return new criteria instance
     */
    public Criteria contains(String s) {
        assertNoBlankInWildcardedQuery(s, true, true);
        queryCriteria.add(new CriteriaEntry(OperationKey.CONTAINS, s));
        return this;
    }

    public Criteria notContains(String s) {
        assertNoBlankInWildcardedQuery(s, true, true);
        queryCriteria.add(new CriteriaEntry(OperationKey.NOT_CONTAINS, s));
        return this;
    }

    /**
     * Crates new CriteriaEntry with trailing wildcard
     *
     * @param s
     * @return new criteria instance
     */
    public Criteria startsWith(String s) {
        assertNoBlankInWildcardedQuery(s, true, false);
        queryCriteria.add(new CriteriaEntry(OperationKey.STARTS_WITH, s));
        return this;
    }

    /**
     * Crates new CriteriaEntry with leading wildcard <br />
     * <strong>NOTE: </strong> mind your schema and execution times as leading wildcards may not be supported.
     *
     * @param s
     * @return new criteria instance
     */
    public Criteria endsWith(String s) {
        assertNoBlankInWildcardedQuery(s, false, true);
        queryCriteria.add(new CriteriaEntry(OperationKey.ENDS_WITH, s));
        return this;
    }

    /**
     * Crates new CriteriaEntry with trailing -
     *
     * @return new criteria instance
     */
    public Criteria not() {
        this.negating = true;
        return this;
    }

    /**
     * Crates new CriteriaEntry with trailing ~
     *
     * @param s
     * @return new criteria instance
     */
    public Criteria fuzzy(String s) {
        queryCriteria.add(new CriteriaEntry(OperationKey.FUZZY, s));
        return this;
    }

    /**
     * Crates new CriteriaEntry allowing native expressions
     *
     * @param s
     * @return new criteria instance
     */
    public Criteria regexp(String s) {
        queryCriteria.add(new CriteriaEntry(OperationKey.REGEXP, s));
        return this;
    }

    /**
     * Boost positive hit with given factor. eg. ^2.3
     *
     * @param boost
     * @return new criteria instance
     */
    public Criteria boost(float boost) {
        if (boost < 0) {
            throw new InvalidDataAccessApiUsageException("Boost must not be negative.");
        }
        this.boost = boost;
        return this;
    }

    /**
     * Crates new CriteriaEntry for {@code RANGE [lowerBound TO upperBound]}
     *
     * @param lowerBound
     * @param upperBound
     * @return new criteria instance
     */
    public Criteria between(Object lowerBound, Object upperBound) {
        if (lowerBound == null && upperBound == null) {
            throw new InvalidDataAccessApiUsageException("Range [* TO *] is not allowed");
        }

        queryCriteria.add(new CriteriaEntry(OperationKey.BETWEEN, new Object[]{lowerBound, upperBound}));
        return this;
    }

    /**
     * Crates new CriteriaEntry for {@code RANGE [* TO upperBound]}
     *
     * @param upperBound
     * @return new criteria instance
     */
    public Criteria lessThanEqual(Object upperBound) {
        if (upperBound == null) {
            throw new InvalidDataAccessApiUsageException("UpperBound can't be null");
        }
        queryCriteria.add(new CriteriaEntry(OperationKey.LESS_EQUAL, upperBound));
        return this;
    }

    public Criteria lessThan(Object upperBound) {
        if (upperBound == null) {
            throw new InvalidDataAccessApiUsageException("UpperBound can't be null");
        }
        queryCriteria.add(new CriteriaEntry(OperationKey.LESS, upperBound));
        return this;
    }

    /**
     * Crates new CriteriaEntry for {@code RANGE [lowerBound TO *]}
     *
     * @param lowerBound
     * @return new criteria instance
     */
    public Criteria greaterThanEqual(Object lowerBound) {
        if (lowerBound == null) {
            throw new InvalidDataAccessApiUsageException("LowerBound can't be null");
        }
        queryCriteria.add(new CriteriaEntry(OperationKey.GREATER_EQUAL, lowerBound));
        return this;
    }

    public Criteria greaterThan(Object lowerBound) {
        if (lowerBound == null) {
            throw new InvalidDataAccessApiUsageException("LowerBound can't be null");
        }
        queryCriteria.add(new CriteriaEntry(OperationKey.GREATER, lowerBound));
        return this;
    }

    /**
     * Crates new CriteriaEntry for multiple values {@code (arg0 arg1 arg2 ...)}
     *
     * @param values
     * @return new criteria instance
     */
    public Criteria in(Object... values) {
        return in(toCollection(values));
    }

    /**
     * Crates new CriteriaEntry for multiple values {@code (arg0 arg1 arg2 ...)}
     *
     * @param values the collection containing the values to match against
     * @return new criteria instance
     */
    public Criteria in(Iterable<?> values) {
        Assert.notNull(values, "Collection of 'in' values must not be null");
        queryCriteria.add(new CriteriaEntry(OperationKey.IN, values));
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

    public Criteria notIn(Object... values) {
        return notIn(toCollection(values));
    }

    public Criteria notIn(Iterable<?> values) {
        Assert.notNull(values, "Collection of 'NotIn' values must not be null");
        queryCriteria.add(new CriteriaEntry(OperationKey.NOT_IN, values));
        return this;
    }

    /**
     * Creates new CriteriaEntry for {@code location WITHIN distance}
     *
     * @param latitude  {@link org.springframework.data.geo.Point} latitude
     * @param longitude {@link org.springframework.data.geo.Point} longitude
     * @param distance  {@link org.springframework.data.geo.Distance} distance
     * @return new criteria instance Criteria the chaind criteria with the new 'within' criteria included.
     */
    public Criteria within(Double latitude, Double longitude, Distance distance) {
        Assert.notNull(latitude, "Latitude must not be null");
        Assert.notNull(longitude, "Longitude must not be null");
        Assert.notNull(distance, "Distance must not be null");
        queryCriteria.add(new CriteriaEntry(OperationKey.WITHIN, new Object[]{latitude, longitude, distance}));
        return this;
    }

    /**
     * Creates new CriteriaEntry for {@code location Box bounding box}
     *
     * @param boundingBox bounding box(left top corner + right bottom corner)
     * @return new criteria instance Criteria the chaind criteria with the new 'boundingBox' criteria included.
     */
    public Criteria boundedBy(Box boundingBox) {
        Assert.notNull(boundingBox, "boundingBox value for boundedBy criteria must not be null");
        queryCriteria.add(new CriteriaEntry(OperationKey.BBOX, new Object[]{boundingBox.getFirst(), boundingBox.getSecond()}));
        return this;
    }

    /**
     * Creates new CriteriaEntry for bounding box created from points
     *
     * @param topLeftGeohash     left top corner of bounding box as geohash
     * @param bottomRightGeohash right bottom corner of bounding box as geohash
     * @return new criteria instance Criteria the chaind criteria with the new 'boundedBy' criteria included.
     */
    public Criteria boundedBy(String topLeftGeohash, String bottomRightGeohash) {
        Assert.isTrue(StringUtils.isNotBlank(topLeftGeohash), "topLeftGeohash must not be empty");
        Assert.isTrue(StringUtils.isNotBlank(bottomRightGeohash), "bottomRightGeohash must not be empty");
        queryCriteria.add(new CriteriaEntry(OperationKey.BBOX, new Object[]{topLeftGeohash, bottomRightGeohash}));
        return this;
    }

    private void assertNoBlankInWildcardedQuery(String searchString, boolean leadingWildcard, boolean trailingWildcard) {
        if (StringUtils.contains(searchString, CRITERIA_VALUE_SEPERATOR)) {
            throw new InvalidDataAccessApiUsageException("Cannot constructQuery '" + (leadingWildcard ? "*" : "") + "\""
                + searchString + "\"" + (trailingWildcard ? "*" : "") + "'. Use expression or multiple clauses instead.");
        }
    }

    /**
     * Property targeted by this Criteria
     *
     * @return new criteria instance
     */
    public Property getProperty() {
        return this.property;
    }

    public Set<CriteriaEntry> getQueryCriteriaEntries() {
        return Collections.unmodifiableSet(this.queryCriteria);
    }

    /**
     * Conjunction to be used with this criteria (AND | OR)
     *
     * @return new criteria instance
     */
    public String getConjunctionOperator() {
        return AND_OPERATOR;
    }

    public List<Criteria> getCriteriaChain() {
        return Collections.unmodifiableList(this.criteriaChain);
    }

    public boolean isNegating() {
        return this.negating;
    }

    public boolean isAnd() {
        return AND_OPERATOR == getConjunctionOperator();
    }

    public boolean isOr() {
        return OR_OPERATOR == getConjunctionOperator();
    }

    public float getBoost() {
        return this.boost;
    }

    static class OrCriteria extends Criteria {

        public OrCriteria() {
            super();
        }

        public OrCriteria(Property property) {
            super(property);
        }

        public OrCriteria(List<Criteria> criteriaChain, Property property) {
            super(criteriaChain, property);
        }

        public OrCriteria(List<Criteria> criteriaChain, String fieldname) {
            super(criteriaChain, fieldname);
        }

        public OrCriteria(String fieldname) {
            super(fieldname);
        }

        @Override
        public String getConjunctionOperator() {
            return OR_OPERATOR;
        }
    }

    public enum OperationKey {
        EQUALS, NOT_EQUALS, CONTAINS, NOT_CONTAINS, STARTS_WITH, ENDS_WITH, REGEXP, BETWEEN, FUZZY, IN, NOT_IN, WITHIN, BBOX, NEAR, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL, NULL, EMPTY;
    }

    public static class CriteriaEntry {

        private final OperationKey key;
        private final Object value;

        CriteriaEntry(OperationKey key) {
            this(key, null);
        }

        CriteriaEntry(OperationKey key, Object value) {
            this.key = key;
            this.value = value;
        }

        public OperationKey getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "CriteriaEntry{" +
                "key=" + key +
                ", value=" + value +
                '}';
        }
    }
}
