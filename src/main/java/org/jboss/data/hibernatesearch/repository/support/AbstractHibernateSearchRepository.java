package org.jboss.data.hibernatesearch.repository.support;

import java.io.Serializable;
import java.util.Collections;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.engine.spi.HSQuery;
import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.repository.HibernateSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.EntityInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractHibernateSearchRepository<T, ID extends Serializable> implements HibernateSearchRepository<T, ID> {

  private SearchIntegrator searchIntegrator;
  private EntityInformation<T, ID> entityInformation;

  protected Class<T> entityClass;

  public AbstractHibernateSearchRepository(SearchIntegrator searchIntegrator, HibernateSearchEntityInformation<T, ID> entityInformation) {
    this.searchIntegrator = searchIntegrator;
    this.entityInformation = entityInformation;
    this.entityClass = entityInformation.getJavaType();
  }

  protected abstract String idAsString(ID id);

  private QueryBuilder getQueryBuilder() {
    return searchIntegrator.buildQueryBuilder().forEntity(getEntityClass()).get();
  }

  private HSQuery getHSQuery(Query query) {
    return searchIntegrator.createHSQuery().luceneQuery(query).targetedEntities(Collections.singletonList(getEntityClass()));
  }

  @Override
  public Iterable<T> findAll() {
    return null;
  }

  @Override
  public long count() {
    QueryBuilder queryBuilder = getQueryBuilder();
    Query query = queryBuilder.all().createQuery();
    HSQuery hsQuery = getHSQuery(query);
    return hsQuery.queryResultSize();
  }

  @Override
  public Iterable<T> findAll(Sort sort) {
    return null;
  }

  @Override
  public Page<T> findAll(Pageable pageable) {
    return null;
  }

  @Override
  public Class<T> getEntityClass() {
    return entityClass;
  }
}
