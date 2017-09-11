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

import me.snowdrop.data.hibernatesearch.spi.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CriteriaQuery<T> extends AbstractQuery<T> {

  private Criteria criteria;

  private CriteriaQuery(Class<T> entityClass) {
    super(entityClass);
  }

  public CriteriaQuery(Class<T> entityClass, Criteria criteria) {
    this(entityClass, criteria, null);
  }

  public CriteriaQuery(Class<T> entityClass, Criteria criteria, Pageable pageable) {
    super(entityClass);
    Assert.notNull(criteria, "Criteria must not be null!");
    this.criteria = criteria;
    if (pageable != null) {
      setPageable(pageable);
      setSort(pageable.getSort());
    }
  }

  public static <U> Query<U> fromQuery(CriteriaQuery<U> source) {
    //noinspection unchecked
    return fromQuery(source, new CriteriaQuery(source.getEntityClass()));
  }

  public static <S extends CriteriaQuery> S fromQuery(CriteriaQuery source, S destination) {
    if (source == null || destination == null) {
      return null;
    }

    if (source.getCriteria() != null) {
      destination.addCriteria(source.getCriteria());
    }

    if (source.getSort() != null) {
      destination.setSort(source.getSort());
    }

    return destination;
  }

  @SuppressWarnings("unchecked")
  public final <S extends CriteriaQuery> S addCriteria(Criteria criteria) {
    Assert.notNull(criteria, "Cannot add null criteria.");
    if (this.criteria == null) {
      this.criteria = criteria;
    } else {
      this.criteria.and(criteria);
    }
    return (S) this;
  }

  public Criteria getCriteria() {
    return criteria;
  }
}
