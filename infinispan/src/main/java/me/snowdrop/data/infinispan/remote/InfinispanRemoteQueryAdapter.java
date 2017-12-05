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

import me.snowdrop.data.hibernatesearch.core.query.AbstractQueryAdapter;
import me.snowdrop.data.hibernatesearch.core.query.CriteriaConverter;
import me.snowdrop.data.hibernatesearch.core.query.QueryHelper;
import me.snowdrop.data.hibernatesearch.core.query.StringQuery;
import me.snowdrop.data.hibernatesearch.util.Integers;
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
    protected void initialize() {
        queryBuilder = queryFactory.from(entityClass);
    }

    @Override
    protected QueryHelper<Query, Void> createQueryHelper() {
        return new InfinispanQueryHelper(queryBuilder);
    }

    @Override
    protected CriteriaConverter<Query> createCriteriaConverter() {
        return new InfinispanCriteriaConverter(queryBuilder);
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
    protected void applyQuery(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
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
    protected void string(StringQuery<T> stringQuery) {
        applyQuery(stringQuery);
        final Query query = queryFactory.create(stringQuery.getQuery());
        fillQuery(stringQuery, query);
    }
}
