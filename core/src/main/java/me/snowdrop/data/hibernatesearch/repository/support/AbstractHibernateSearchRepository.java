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

package me.snowdrop.data.hibernatesearch.repository.support;

import me.snowdrop.data.hibernatesearch.core.HibernateSearchOperations;
import me.snowdrop.data.hibernatesearch.core.query.BaseQuery;
import me.snowdrop.data.hibernatesearch.repository.HibernateSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractHibernateSearchRepository<T, ID> implements HibernateSearchRepository<T, ID> {
  protected final HibernateSearchOperations hibernateSearchOperations;
  private final Class<T> entityClass;

  public AbstractHibernateSearchRepository(HibernateSearchOperations hibernateSearchOperations, HibernateSearchEntityInformation<T, ID> entityInformation) {
    this.hibernateSearchOperations = hibernateSearchOperations;
    this.entityClass = entityInformation.getJavaType();
  }

  @Override
  public Iterable<T> findAll() {
    BaseQuery<T> query = new BaseQuery<>(getEntityClass());
    return hibernateSearchOperations.findAll(query);
  }

  @Override
  public long count() {
    BaseQuery<T> countQuery = new BaseQuery<>(getEntityClass());
    return hibernateSearchOperations.count(countQuery);
  }

  @Override
  public Iterable<T> findAll(Sort sort) {
    BaseQuery<T> query = new BaseQuery<>(getEntityClass());
    query.setSort(sort);
    return hibernateSearchOperations.findAll(query);
  }

  @Override
  public Page<T> findAll(Pageable pageable) {
    BaseQuery<T> query = new BaseQuery<>(getEntityClass());
    query.setPageable(pageable);
    return hibernateSearchOperations.findPageable(query);
  }

  @Override
  public Class<T> getEntityClass() {
    return entityClass;
  }
}
