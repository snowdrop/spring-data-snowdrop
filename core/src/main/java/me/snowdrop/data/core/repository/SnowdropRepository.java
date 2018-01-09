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

package me.snowdrop.data.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@NoRepositoryBean
public interface SnowdropRepository<T, ID> extends Repository<T, ID> {

  /**
   * Returns all instances of the type.
   *
   * @return all entities
   */
  Iterable<T> findAll();

  /**
   * Returns the number of entities available.
   *
   * @return the number of entities
   */
  long count();

  /**
   * Returns all entities sorted by the given options.
   *
   * @param sort the sort
   * @return all entities sorted by the given options
   */
  Iterable<T> findAll(Sort sort);

  /**
   * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
   *
   * @param pageable the pageable
   * @return a page of entities
   */
  Page<T> findAll(Pageable pageable);

  Class<T> getEntityClass();
}
