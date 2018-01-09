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

import java.util.List;
import java.util.stream.Stream;

import me.snowdrop.data.core.query.AbstractQueryAdapter;
import me.snowdrop.data.core.query.CriteriaConverter;
import me.snowdrop.data.core.query.QueryHelper;
import me.snowdrop.data.core.query.StringQuery;
import me.snowdrop.data.core.util.Integers;
import org.infinispan.query.dsl.Expression;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryBuilder;
import org.infinispan.query.dsl.QueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class InfinispanRemoteQueryAdapter<T> extends AbstractQueryAdapter<T, Query, Void> {

    private final QueryFactory queryFactory;
    private QueryBuilder queryBuilder;
    private Query query;

    public InfinispanRemoteQueryAdapter(QueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    protected void initialize(me.snowdrop.data.core.spi.Query<T> query) {
        queryBuilder = queryFactory.from(query.getEntityClass());

        String[] fields = getFields(query);
        if (fields != null) {
            Expression[] projection = new Expression[fields.length];
            for (int i = 0; i < fields.length; i++) {
                projection[i] = Expression.property(fields[i]);
            }
            queryBuilder = queryBuilder.select(projection);
        }

        super.initialize(query);
    }

    @Override
    protected String getFieldName(String property) {
        return property;
    }

    @Override
    protected QueryHelper<Query, Void> createQueryHelper() {
        return new InfinispanQueryHelper(queryBuilder);
    }

    @Override
    protected CriteriaConverter<Query> createCriteriaConverter() {
        return new InfinispanCriteriaConverter(queryFactory, queryBuilder);
    }

    @Override
    protected long size() {
        return query.getResultSize();
    }

    @Override
    protected List<T> list() {
        return query.list();
    }

    @Override
    protected Stream<T> stream() {
        return toStream(list());
    }

    @Override
    protected void applyQuery(me.snowdrop.data.core.spi.Query<T> query) {
        // we need to sort before creating query
        Sort sort = query.getSort();
        if (sort == null || sort.isUnsorted()) {
            Pageable pageable = query.getPageable();
            if (pageable != null && pageable.isPaged()) {
                sort = pageable.getSort();
            }
        }
        if (sort != null && sort.isSorted()) {
            queryHelper.convert(sort);
        }
    }

    @Override
    protected void applyQueryImpl(Query query) {
        this.query = query;
    }

    @Override
    protected void setSort(Void handle) {
        // to late
    }

    @Override
    protected void setFirstResult(long firstResult) {
        query.startOffset(firstResult);
    }

    @Override
    protected void setMaxResults(long maxResults) {
        query.maxResults(Integers.safeCast(maxResults));
    }

    @Override
    protected void setProjections(String[] fields) {
    }

    @Override
    protected void string(StringQuery<T> stringQuery) {
        applyQuery(stringQuery);
        final Query query = queryFactory.create(stringQuery.getQuery());
        fillQuery(stringQuery, query);
    }
}
