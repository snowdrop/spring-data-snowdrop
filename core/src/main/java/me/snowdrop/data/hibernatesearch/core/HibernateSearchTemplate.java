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

import java.util.List;
import java.util.stream.Stream;

import me.snowdrop.data.hibernatesearch.core.mapping.HibernateSearchPersistentProperty;
import me.snowdrop.data.hibernatesearch.core.mapping.SimpleHibernateSearchMappingContext;
import me.snowdrop.data.hibernatesearch.core.query.BaseQuery;
import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import me.snowdrop.data.hibernatesearch.spi.Query;
import me.snowdrop.data.hibernatesearch.spi.QueryAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

  private <T> long total(Query<T> query) {
    return count(new BaseQuery<>(query.getEntityClass()));
  }

  @Override
  public <T> long count(Query<T> countQuery) {
    QueryAdapter<T> queryAdapter = datasourceMapper.createQueryAdapter();
    return queryAdapter.size(countQuery);
  }

  @Override
  public <T> T findSingle(Query<T> query) {
    QueryAdapter<T> queryAdapter = datasourceMapper.createQueryAdapter();
    return queryAdapter.single(query);
  }

  @Override
  public <T> Iterable<T> findAll(Query<T> allQuery) {
    return findAllInternal(allQuery);
  }

  @Override
  public <T> Slice<T> findSlice(Query<T> query) {
    List<T> list = findAllInternal(query);
    Pageable pageable = query.getPageable();
    if (pageable != null) {
      boolean hasNext = ((pageable.getPageNumber() + 1) * pageable.getPageSize() < total(query));
      return new SliceImpl<T>(list, query.getPageable(), hasNext);
    } else {
      return new SliceImpl<T>(list);
    }
  }

  @Override
  public <T> Page<T> findPageable(Query<T> query) {
    List<T> list = findAllInternal(query);
    Pageable pageable = query.getPageable();
    if (pageable != null) {
      return new PageImpl<T>(list, query.getPageable(), total(query));
    } else {
      return new PageImpl<T>(list);
    }
  }

  @Override
  public <T> Stream<T> stream(Query<T> query) {
    QueryAdapter<T> queryAdapter = datasourceMapper.createQueryAdapter();
    return queryAdapter.stream(query);
  }
}
