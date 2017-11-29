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

package me.snowdrop.data.hibernatesearch.config.infinispan;

import java.util.List;
import java.util.stream.Stream;

import me.snowdrop.data.hibernatesearch.core.query.AbstractQueryAdapter;
import me.snowdrop.data.hibernatesearch.spi.AbstractCrudAdapter;
import me.snowdrop.data.hibernatesearch.spi.CrudAdapter;
import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import me.snowdrop.data.hibernatesearch.spi.QueryAdapter;
import me.snowdrop.data.hibernatesearch.util.Integers;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.spi.SearchIntegrator;
import org.infinispan.Cache;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.FetchOptions;
import org.infinispan.query.ResultIterator;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class InfinispanDatasourceMapper implements DatasourceMapper {

    private final EntityToCacheMapper entityToCacheMapper;

    public InfinispanDatasourceMapper(EntityToCacheMapper entityToCacheMapper) {
        Assert.notNull(entityToCacheMapper, "Null EntityToCacheMapper!");
        this.entityToCacheMapper = entityToCacheMapper;
    }

    private <T> Cache<?, T> getCache(Class<T> entityClass) {
        Cache<?, T> cache = entityToCacheMapper.getCache(entityClass);
        if (cache == null) {
            throw new IllegalArgumentException("No cache mapped to entity class: " + entityClass);
        }
        return cache;
    }

    public <T> QueryAdapter<T> createQueryAdapter(Class<T> entityClass) {
        SearchManager searchManager = Search.getSearchManager(getCache(entityClass));
        return new InfinispanQueryAdapter<>(searchManager);
    }

    public <T, ID> CrudAdapter<T, ID> createCrudAdapter(EntityInformation<T, ID> ei) {
        @SuppressWarnings("unchecked")
        Cache<ID, T> cache = (Cache<ID, T>) getCache(ei.getJavaType());
        return new InfinispanCrudAdapter<>(ei, cache);
    }

    private class InfinispanQueryAdapter<T> extends AbstractQueryAdapter<T> {
        private final SearchManager searchManager;
        private CacheQuery<T> cacheQuery;

        public InfinispanQueryAdapter(SearchManager searchManager) {
            this.searchManager = searchManager;
        }

        protected long size() {
            return cacheQuery.getResultSize();
        }

        protected List<T> list() {
            return cacheQuery.list();
        }

        protected Stream<T> stream() {
            ResultIterator<T> iterator = cacheQuery.iterator(new FetchOptions()); // lazy
            Stream<T> stream = toStream(iterator);
            return stream.onClose(iterator::close);
        }

        protected SearchIntegrator getSearchIntegrator() {
            return searchManager.unwrap(SearchIntegrator.class);
        }

        protected void applyLuceneQuery(Query query) {
            cacheQuery = searchManager.getQuery(query, entityClass);
        }

        protected void setSort(Sort sort) {
            cacheQuery.sort(sort);
        }

        protected void setFirstResult(long firstResult) {
            cacheQuery.firstResult(Integers.safeCast(firstResult));
        }

        protected void setMaxResults(long maxResults) {
            cacheQuery.maxResults(Integers.safeCast(maxResults));
        }
    }

    private class InfinispanCrudAdapter<T, ID> extends AbstractCrudAdapter<T, ID> {
        private final Cache<ID, T> store;

        public InfinispanCrudAdapter(EntityInformation<T, ID> ei, Cache<ID, T> store) {
            super(ei);
            this.store = store;
        }

        protected void save(ID id, T entity) {
            store.put(id, entity);
        }

        protected T find(ID id) {
            return store.get(id);
        }

        public void deleteById(ID id) {
            store.remove(id);
        }

        public void deleteAll() {
            store.clear();
        }
    }

}
