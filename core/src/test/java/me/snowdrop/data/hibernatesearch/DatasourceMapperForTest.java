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

package me.snowdrop.data.hibernatesearch;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import me.snowdrop.data.hibernatesearch.core.query.AbstractHSQueryAdapter;
import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import me.snowdrop.data.hibernatesearch.spi.QueryAdapter;
import org.hibernate.search.spi.SearchIntegrator;

/**
 * @author Ales Justin
 */
public class DatasourceMapperForTest<T> extends AbstractHSQueryAdapter<T> implements DatasourceMapper, Closeable {
  private final SearchIntegrator searchIntegrator;

  private Map<Serializable, Object> map = new HashMap<>();

  public DatasourceMapperForTest(SearchIntegrator searchIntegrator) {
    this.searchIntegrator = searchIntegrator;
  }

  public SearchIntegrator getSearchIntegrator() {
    return searchIntegrator;
  }

  @Override
  public void close() throws IOException {
    searchIntegrator.close();
  }

  @Override
  public <U> QueryAdapter<U> createQueryAdapter(Class<U> entityClass) {
    //noinspection unchecked
    return (QueryAdapter<U>) this;
  }

  public void put(AbstractEntity entity) {
    map.put(entity.getId(), entity);
  }

  public void clear() {
    map.clear();
  }

  protected T get(Class<T> entityClass, Serializable id) {
    Object entity = map.get(id);
    return entity != null ? entityClass.cast(entity) : null;
  }
}