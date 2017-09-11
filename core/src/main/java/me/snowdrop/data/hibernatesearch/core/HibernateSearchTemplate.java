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

package me.snowdrop.data.hibernatesearch.core;

import java.util.Iterator;
import java.util.List;

import me.snowdrop.data.hibernatesearch.core.mapping.HibernateSearchPersistentProperty;
import me.snowdrop.data.hibernatesearch.core.mapping.SimpleHibernateSearchMappingContext;
import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import me.snowdrop.data.hibernatesearch.spi.Query;
import me.snowdrop.data.hibernatesearch.spi.QueryAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mapping.context.MappingContext;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchTemplate implements HibernateSearchOperations {
  private final DatasourceMapper datasourceMapper;
  private MappingContext<?, HibernateSearchPersistentProperty> mappingContext;

  public HibernateSearchTemplate(DatasourceMapper datasourceMapper) {
    this.datasourceMapper = datasourceMapper;
  }

  private <T> List<T> findAllInternal(Query<T> query) {
    QueryAdapter<T> queryAdapter = datasourceMapper.createQueryAdapter();
    return queryAdapter.list(query);
  }

  @Override
  public synchronized MappingContext<?, HibernateSearchPersistentProperty> getMappingContext() {
    if (mappingContext == null) {
      mappingContext = new SimpleHibernateSearchMappingContext<>();
    }
    return mappingContext;
  }

  @Override
  public <T> long count(Query<T> countQuery) {
    QueryAdapter<T> queryAdapter = datasourceMapper.createQueryAdapter();
    return queryAdapter.size(countQuery);
  }

  @Override
  public <T> T findSingle(Query<T> query) {
    List<T> list = findAllInternal(query);
    return (list.isEmpty() ? null : list.get(0));
  }

  @Override
  public <T> Iterable<T> findAll(Query<T> allQuery) {
    return findAllInternal(allQuery);
  }

  @Override
  public <T> Page<T> findPageable(Query<T> query) {
    return new PageImpl<T>(findAllInternal(query));
  }

  @Override
  public <T> Iterator<T> stream(Query<T> query) {
    //noinspection unchecked
    return (Iterator<T>) findAllInternal(query).iterator();
  }
}
