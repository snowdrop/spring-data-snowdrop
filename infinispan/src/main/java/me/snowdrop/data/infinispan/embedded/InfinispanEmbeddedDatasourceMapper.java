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

package me.snowdrop.data.infinispan.embedded;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import me.snowdrop.data.hibernatesearch.core.query.lucene.LuceneQueryAdapter;
import me.snowdrop.data.hibernatesearch.crud.MapCrudAdapter;
import me.snowdrop.data.hibernatesearch.spi.CrudAdapter;
import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import me.snowdrop.data.hibernatesearch.spi.QueryAdapter;
import me.snowdrop.data.hibernatesearch.util.Integers;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.spi.SearchIntegrator;
import org.infinispan.AdvancedCache;
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
public class InfinispanEmbeddedDatasourceMapper implements DatasourceMapper {

    private final EntityToCacheMapper entityToCacheMapper;

    public InfinispanEmbeddedDatasourceMapper(EntityToCacheMapper entityToCacheMapper) {
        Assert.notNull(entityToCacheMapper, "Null EntityToCacheMapper!");
        this.entityToCacheMapper = entityToCacheMapper;
    }

    private <T, ID> Cache<ID, T> getCache(Class<T> entityClass) {
        Cache<ID, T> cache = entityToCacheMapper.getCache(entityClass);
        if (cache == null) {
            throw new IllegalArgumentException("No cache mapped to entity class: " + entityClass);
        }
        return cache;
    }

    public <T> QueryAdapter<T> createQueryAdapter(Class<T> entityClass) {
        SearchManager searchManager = Search.getSearchManager(getCache(entityClass));
        return new InfinispanEmbeddedQueryAdapter<>(searchManager);
    }

    public <T, ID> CrudAdapter<T, ID> createCrudAdapter(EntityInformation<T, ID> ei) {
        Cache<ID, T> cache = getCache(ei.getJavaType());
        return new InfinispanEmbeddedCrudAdapter<>(ei, cache);
    }

    private class InfinispanEmbeddedQueryAdapter<T> extends LuceneQueryAdapter<T> {
        private final SearchManager searchManager;
        private CacheQuery<T> cacheQuery;

        public InfinispanEmbeddedQueryAdapter(SearchManager searchManager) {
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

        protected void applyQueryImpl(Query query) {
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

    private class InfinispanEmbeddedCrudAdapter<T, ID> extends MapCrudAdapter<T, ID> {
        private final Cache<ID, T> cache;

        public InfinispanEmbeddedCrudAdapter(EntityInformation<T, ID> ei, Cache<ID, T> cache) {
            super(ei, cache);
            this.cache = cache;
        }

        @Override
        protected Map<ID, T> findAll(Set<ID> keys) {
            AdvancedCache<ID, T> advancedCache = cache.getAdvancedCache();
            return advancedCache.getAll(keys);
        }
    }

}
