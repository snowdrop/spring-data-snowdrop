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

package me.snowdrop.data.hibernatesearch.config;

import java.util.List;
import java.util.stream.Stream;

import me.snowdrop.data.hibernatesearch.core.query.AbstractQueryAdapter;
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

  @Override
  public <T> QueryAdapter<T> createQueryAdapter(Class<T> entityClass) {
    Cache<?, T> cache = entityToCacheMapper.getCache(entityClass);
    if (cache == null) {
      throw new IllegalArgumentException("No cache mapped to entity class: " + entityClass);
    }
    SearchManager searchManager = Search.getSearchManager(cache);
    return new InfinispanQueryAdapter<>(searchManager);
  }

  private class InfinispanQueryAdapter<T> extends AbstractQueryAdapter<T> {
    private final SearchManager searchManager;
    private CacheQuery<T> cacheQuery;

    public InfinispanQueryAdapter(SearchManager searchManager) {
      this.searchManager = searchManager;
    }

    @Override
    protected long size() {
      return cacheQuery.getResultSize();
    }

    @Override
    protected List<T> list() {
      return cacheQuery.list();
    }

    @Override
    protected Stream<T> stream() {
      ResultIterator<T> iterator = cacheQuery.iterator(new FetchOptions()); // lazy
      Stream<T> stream = toStream(iterator);
      return stream.onClose(iterator::close);
    }

    @Override
    protected SearchIntegrator getSearchIntegrator() {
      return searchManager.unwrap(SearchIntegrator.class);
    }

    @Override
    protected void applyLuceneQuery(Query query) {
      cacheQuery = searchManager.getQuery(query, entityClass);
    }

    @Override
    protected void setSort(Sort sort) {
      cacheQuery.sort(sort);
    }

    @Override
    protected void setFirstResult(long firstResult) {
      cacheQuery.firstResult(Integers.safeCast(firstResult));
    }

    @Override
    protected void setMaxResults(long maxResults) {
      cacheQuery.maxResults(Integers.safeCast(maxResults));
    }
  }
}
