package org.jboss.data.hibernatesearch.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.engine.spi.EntityInfo;
import org.hibernate.search.query.engine.spi.HSQuery;
import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.core.mapping.HibernateSearchPersistentProperty;
import org.jboss.data.hibernatesearch.core.mapping.SimpleHibernateSearchMappingContext;
import org.jboss.data.hibernatesearch.spi.DatasourceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.util.CloseableIterator;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchTemplate implements HibernateSearchOperations {
  private final SearchIntegrator searchIntegrator;
  private final DatasourceMapper datasourceMapper;
  private MappingContext<?, HibernateSearchPersistentProperty> mappingContext;

  public HibernateSearchTemplate(SearchIntegrator searchIntegrator, DatasourceMapper datasourceMapper) {
    this.searchIntegrator = searchIntegrator;
    this.datasourceMapper = datasourceMapper;
  }

  private QueryBuilder getQueryBuilder(Class<?> entityClass) {
    return searchIntegrator.buildQueryBuilder().forEntity(entityClass).get();
  }

  private HSQuery getHSQuery(Class<?> entityClass, Query query) {
    return searchIntegrator.createHSQuery().luceneQuery(query).targetedEntities(Collections.singletonList(entityClass));
  }

  private <T> List<T> findAllInternal(org.jboss.data.hibernatesearch.core.query.Query allQuery) {
    Class<?> entityClass = allQuery.getEntityClass();
    QueryBuilder queryBuilder = getQueryBuilder(entityClass);
    Query query = queryBuilder.all().createQuery(); // TODO -- criteria, query string, ...
    HSQuery hsQuery = getHSQuery(entityClass, query); // TODO -- sort, paging
    List<EntityInfo> entityInfos = hsQuery.queryEntityInfos();
    List<T> entities = new ArrayList<>();
    for (EntityInfo ei : entityInfos) {
      //noinspection unchecked
      entities.add(datasourceMapper.get((Class<T>) entityClass, ei.getId()));
    }
    return entities;
  }

  @Override
  public synchronized MappingContext<?, HibernateSearchPersistentProperty> getMappingContext() {
    if (mappingContext == null) {
      mappingContext = new SimpleHibernateSearchMappingContext<>();
    }
    return mappingContext;
  }

  @Override
  public <T> long count(org.jboss.data.hibernatesearch.core.query.Query countQuery) {
    QueryBuilder queryBuilder = getQueryBuilder(countQuery.getEntityClass());
    Query query = queryBuilder.all().createQuery();
    HSQuery hsQuery = getHSQuery(countQuery.getEntityClass(), query); // TODO -- sort, paging ?
    return hsQuery.queryResultSize();
  }

  @Override
  public <T> T findSingle(org.jboss.data.hibernatesearch.core.query.Query query) {
    List<T> list = findAllInternal(query);
    return (list.isEmpty() ? null : list.get(0));
  }

  @Override
  public <T> Iterable<T> findAll(org.jboss.data.hibernatesearch.core.query.Query allQuery) {
    return findAllInternal(allQuery);
  }

  @Override
  public <T> Page<T> findPageable(org.jboss.data.hibernatesearch.core.query.Query query) {
    return new PageImpl<T>(findAllInternal(query));
  }

  @Override
  public <T> Iterator<T> stream(org.jboss.data.hibernatesearch.core.query.Query query) {
    //noinspection unchecked
    return (Iterator<T>) findAllInternal(query).iterator();
  }
}
