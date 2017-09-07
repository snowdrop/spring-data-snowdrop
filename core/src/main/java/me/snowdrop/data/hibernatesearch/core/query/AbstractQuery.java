package me.snowdrop.data.hibernatesearch.core.query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractQuery implements Query {
  private Class<?> entityClass;
  private Pageable pageable;
  private Sort sort;

  public AbstractQuery(Class<?> entityClass) {
    this.entityClass = entityClass;
  }

  public Class<?> getEntityClass() {
    return entityClass;
  }

  public Pageable getPageable() {
    return pageable;
  }

  public void setPageable(Pageable pageable) {
    this.pageable = pageable;
  }

  public Sort getSort() {
    return sort;
  }

  public void setSort(Sort sort) {
    this.sort = sort;
  }
}
