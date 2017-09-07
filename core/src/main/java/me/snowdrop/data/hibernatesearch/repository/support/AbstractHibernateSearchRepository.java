package me.snowdrop.data.hibernatesearch.repository.support;

import java.io.Serializable;

import me.snowdrop.data.hibernatesearch.core.HibernateSearchOperations;
import me.snowdrop.data.hibernatesearch.core.query.BaseQuery;
import me.snowdrop.data.hibernatesearch.repository.HibernateSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.EntityInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractHibernateSearchRepository<T, ID extends Serializable> implements HibernateSearchRepository<T, ID> {

  private HibernateSearchOperations hibernateSearchOperations;
  private EntityInformation<T, ID> entityInformation;

  protected Class<T> entityClass;

  public AbstractHibernateSearchRepository(HibernateSearchOperations hibernateSearchOperations, HibernateSearchEntityInformation<T, ID> entityInformation) {
    this.hibernateSearchOperations = hibernateSearchOperations;
    this.entityInformation = entityInformation;
    this.entityClass = entityInformation.getJavaType();
  }

  @Override
  public Iterable<T> findAll() {
    BaseQuery query = new BaseQuery(getEntityClass());
    return hibernateSearchOperations.findAll(query);
  }

  @Override
  public long count() {
    BaseQuery countQuery = new BaseQuery(getEntityClass());
    return hibernateSearchOperations.count(countQuery);
  }

  @Override
  public Iterable<T> findAll(Sort sort) {
    BaseQuery query = new BaseQuery(getEntityClass());
    query.setSort(sort);
    return hibernateSearchOperations.findAll(query);
  }

  @Override
  public Page<T> findAll(Pageable pageable) {
    BaseQuery query = new BaseQuery(getEntityClass());
    query.setPageable(pageable);
    return hibernateSearchOperations.findPageable(query);
  }

  @Override
  public Class<T> getEntityClass() {
    return entityClass;
  }
}
