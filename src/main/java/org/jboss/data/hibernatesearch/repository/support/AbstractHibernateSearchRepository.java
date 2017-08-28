package org.jboss.data.hibernatesearch.repository.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.engine.spi.EntityInfo;
import org.hibernate.search.query.engine.spi.HSQuery;
import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.repository.HibernateSearchRepository;
import org.jboss.data.hibernatesearch.spi.DatasourceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.EntityInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractHibernateSearchRepository<T, ID extends Serializable> implements HibernateSearchRepository<T, ID> {

  private SearchIntegrator searchIntegrator;
  private DatasourceMapper datasourceMapper;
  private EntityInformation<T, ID> entityInformation;

  protected Class<T> entityClass;

  public AbstractHibernateSearchRepository(SearchIntegrator searchIntegrator, DatasourceMapper datasourceMapper, HibernateSearchEntityInformation<T, ID> entityInformation) {
    this.searchIntegrator = searchIntegrator;
    this.datasourceMapper = datasourceMapper;
    this.entityInformation = entityInformation;
    this.entityClass = entityInformation.getJavaType();
  }

  private QueryBuilder getQueryBuilder() {
    return searchIntegrator.buildQueryBuilder().forEntity(getEntityClass()).get();
  }

  private HSQuery getHSQuery(Query query) {
    return searchIntegrator.createHSQuery().luceneQuery(query).targetedEntities(Collections.singletonList(getEntityClass()));
  }

  @Override
  public Iterable<T> findAll() {
    QueryBuilder queryBuilder = getQueryBuilder();
    Query query = queryBuilder.all().createQuery();
    HSQuery hsQuery = getHSQuery(query);
    List<EntityInfo> entityInfos = hsQuery.queryEntityInfos();
    List<T> entities = new ArrayList<>();
    for (EntityInfo ei : entityInfos) {
      entities.add(datasourceMapper.get(getEntityClass(), ei.getId()));
    }
    return entities;
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
