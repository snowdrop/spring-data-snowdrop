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

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import me.snowdrop.data.hibernatesearch.spi.Query;
import me.snowdrop.data.hibernatesearch.spi.QueryAdapter;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.StreamUtils;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractQueryAdapter<T, Q, S> implements QueryAdapter<T> {
    protected Class<T> entityClass;

    protected QueryHelper<Q, S> queryHelper;
    protected CriteriaConverter<Q> criteriaConverter;

    protected abstract QueryHelper<Q, S> createQueryHelper();

    protected abstract CriteriaConverter<Q> createCriteriaConverter();

    protected void initialize() {
    }

    protected void initialize(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
        this.entityClass = query.getEntityClass();

        initialize();

        this.queryHelper = createQueryHelper();
        this.criteriaConverter = createCriteriaConverter();

        BaseQuery<T> baseQuery = (BaseQuery<T>) query;
        baseQuery.apply(this);
    }

    protected abstract long size();

    protected abstract List<T> list();

    protected abstract Stream<T> stream();

    protected T single() {
        List<T> list = list();
        switch (list.size()) {
            case 0:
                return null;
            case 1:
                return list.get(0);
            default:
                throw new IncorrectResultSizeDataAccessException(String.format("Found %s results, expected 1.", list.size()), 1);
        }
    }

    protected Stream<T> toStream(Iterable<T> iterable) {
        return toStream(iterable.iterator());
    }

    protected Stream<T> toStream(Iterator<T> iterator) {
        return StreamUtils.createStreamFromIterator(iterator);
    }

    public long size(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
        initialize(query);
        return size();
    }

    public T single(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
        initialize(query);
        return single();
    }

    public List<T> list(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
        initialize(query);
        return list();
    }

    public Stream<T> stream(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
        initialize(query);
        return stream();
    }

    protected void applyQuery(Query<T> query) {
        // do nothing
    }

    protected abstract void applyQueryImpl(Q result);

    protected abstract void setSort(S sort);

    protected abstract void setFirstResult(long firstResult);

    protected abstract void setMaxResults(long maxResults);

    void convert(CriteriaQuery<T> query) {
        fillQuery(query);
        // apply/override max results
        if (query.getMaxResults() != null) {
            setMaxResults(query.getMaxResults());
        }
    }

    protected abstract void string(StringQuery<T> stringQuery);

    void query(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
        applyQuery(query);
        fillQuery(query, queryHelper.matchAll());
    }

    protected void fillQuery(me.snowdrop.data.hibernatesearch.spi.Query<T> query, Q queryImpl) {
        applyQueryImpl(queryImpl);
        addSortToQuery(query);
        addPagingToQuery(query);
    }

    private void fillQuery(CriteriaQuery<T> query) {
        applyQuery(query);
        fillQuery(query, createQueryImpl(query));
    }

    private Q createQueryImpl(CriteriaQuery<T> query) {
        Criteria criteria = query.getCriteria();
        if (criteria == null) {
            return queryHelper.matchAll();
        } else {
            return criteriaConverter.convert(criteria);
        }
    }

    protected void addSortToQuery(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
        addSortToQuery(query.getSort());
    }

    protected void addSortToQuery(org.springframework.data.domain.Sort sort) {
        if (sort != null) {
            S hsSort = queryHelper.convert(sort);
            setSort(hsSort);
        }
    }

    protected void addPagingToQuery(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
        Pageable pageable = query.getPageable();
        if (pageable != null && pageable.isPaged()) {
            org.springframework.data.domain.Sort sort = pageable.getSort();
            if (query.getSort() == null && sort.isSorted()) {
                addSortToQuery(sort);
            }
            setFirstResult(pageable.getOffset());
            setMaxResults(pageable.getPageSize());
        }
    }
}