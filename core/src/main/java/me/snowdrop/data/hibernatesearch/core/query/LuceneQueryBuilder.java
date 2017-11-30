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

import java.util.Arrays;
import java.util.Collection;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.Sort;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.RangeMatchingContext;
import org.hibernate.search.query.dsl.TermMatchingContext;
import org.hibernate.search.query.dsl.Unit;
import org.hibernate.search.query.dsl.sort.SortContext;
import org.hibernate.search.query.dsl.sort.SortFieldContext;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 * @author Ales Justin
 */
public class LuceneQueryBuilder {

    private final EntityMetadataContext entityMetadataContext;
    private final QueryBuilder queryBuilder;

    public LuceneQueryBuilder(EntityMetadataContext entityMetadataContext, QueryBuilder queryBuilder) {
        this.entityMetadataContext = entityMetadataContext;
        this.queryBuilder = queryBuilder;
    }

    public Sort convert(org.springframework.data.domain.Sort sort) {
        SortContext context = queryBuilder.sort();
        SortFieldContext currentContext = null;
        for (org.springframework.data.domain.Sort.Order order : sort) {
            String fieldName = entityMetadataContext.getFieldName(order.getProperty());
            if (currentContext == null) {
                currentContext = context.byField(fieldName);
            } else {
                currentContext = currentContext.andByField(fieldName);
            }
            org.springframework.data.domain.Sort.NullHandling nullHandling = order.getNullHandling();
            if (nullHandling == org.springframework.data.domain.Sort.NullHandling.NULLS_FIRST) {
                currentContext = currentContext.onMissingValue().sortFirst();
            } else if (nullHandling == org.springframework.data.domain.Sort.NullHandling.NULLS_LAST) {
                currentContext = currentContext.onMissingValue().sortLast();
            }
            boolean desc = (order.getDirection() == org.springframework.data.domain.Sort.Direction.DESC);
            if (desc) {
                currentContext = currentContext.desc();
            } else {
                currentContext = currentContext.asc();
            }
        }
        return (currentContext != null) ? currentContext.createSort() : null;
    }

    public Query matchAll() {
        return queryBuilder.all().createQuery();
    }

    public Query all(Collection<Query> subQueries) {
        BooleanJunction<BooleanJunction> bool = queryBuilder.bool();
        for (Query subQuery : subQueries) {
            bool.must(subQuery);
        }
        return bool.createQuery();
    }

    public Query any(Query... subQueries) {
        return any(Arrays.asList(subQueries));
    }

    public Query any(Collection<Query> subQueries) {
        BooleanJunction<BooleanJunction> bool = queryBuilder.bool();
        for (Query subQuery : subQueries) {
            bool.should(subQuery);
        }
        return bool.createQuery();
    }

    public Query in(String fieldName, Collection<?> values) {
        BooleanJunction<BooleanJunction> bool = queryBuilder.bool();
        for (Object value : values) {
            bool.should(equal(fieldName, value));
        }
        return bool.createQuery();
    }

    public Query notIn(String fieldName, Collection<?> values) {
        BooleanJunction<BooleanJunction> bool = queryBuilder.bool();
        for (Object value : values) {
            bool.must(equal(fieldName, value)).not();
        }
        return bool.createQuery();
    }

    public Query not(Query query) {
        return queryBuilder.bool().must(query).not().createQuery();
    }

    public Query equal(String fieldName, Object value) {
        return keywordOnField(fieldName)
            .matching(value)
            .createQuery();
    }

    public Query notEqual(String fieldName, Object value) {
        return not(equal(fieldName, value));
    }

    public Query isNull(String fieldName) {
        return queryBuilder.keyword().onField(fieldName).matching(null).createQuery();
    }

    public Query isEmpty(String fieldName) {
        throw new UnsupportedOperationException("IS_EMPTY is not supported");
    }

    public Query greaterThan(String fieldName, Object value) {
        return rangeOnField(fieldName)
            .above(value).excludeLimit()
            .createQuery();
    }

    public Query greaterThanOrEqual(String fieldName, Object value) {
        return rangeOnField(fieldName)
            .above(value)
            .createQuery();
    }

    public Query lessThan(String fieldName, Object value) {
        return rangeOnField(fieldName)
            .below(value).excludeLimit()
            .createQuery();
    }

    public Query lessThanOrEqual(String fieldName, Object value) {
        return rangeOnField(fieldName)
            .below(value)
            .createQuery();
    }

    public Query between(String fieldName, Object min, Object max) {
        return rangeOnField(fieldName).from(min).to(max).createQuery();
    }

    public Query fuzzy(String fieldName, Object value) {
        return new FuzzyQuery(new Term(fieldName, StringUtils.toString(value)));
    }

    public Query contains(String fieldName, Object value) {
        return wildcard(fieldName, "*" + value + "*");
    }

    public Query notContains(String fieldName, Object value) {
        BooleanJunction<BooleanJunction> bool = queryBuilder.bool();
        bool.must(contains(fieldName, value)).not();
        return bool.createQuery();
    }

    public Query startsWith(String fieldName, Object value) {
        return wildcard(fieldName, value + "*");
    }

    public Query endsWith(String fieldName, Object value) {
        return wildcard(fieldName, "*" + value);
    }

    public Query reqexp(String fieldName, String reqexp) {
        return new RegexpQuery(new Term(fieldName, reqexp));
    }

    public Query spatial(String fieldName, double latitude, double longitude, double distance) {
        return queryBuilder
            .spatial()
            .onField(fieldName)
            .within(distance, Unit.KM)
            .ofLatitude(latitude)
            .andLongitude(longitude)
            .createQuery();
    }

    private Query wildcard(String fieldName, String value) {
        return queryBuilder
            .keyword()
            .wildcard()
            .onField(fieldName)
            .matching(value)
            .createQuery();
    }

    private TermMatchingContext keywordOnField(String fieldName) {
        return queryBuilder
            .keyword().onField(fieldName);
    }

    private RangeMatchingContext rangeOnField(String fieldName) {
        return queryBuilder
            .range().onField(fieldName);
    }
}
