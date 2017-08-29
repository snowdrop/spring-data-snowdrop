package org.jboss.data.hibernatesearch.core.query;

import java.util.Collections;

import org.apache.lucene.search.Sort;
import org.hibernate.search.query.engine.spi.HSQuery;
import org.hibernate.search.spi.SearchIntegrator;
import org.springframework.data.domain.Pageable;

/**
 * @author Ales Justin
 */
public class QueryConverter {

  private LuceneQueryBuilder queryBuilder;
  private SearchIntegrator searchIntegrator;

  private FilterConverter filterConverter;
  private OrderConverter orderConverter;

  public QueryConverter(SearchIntegrator searchIntegrator, Class<?> entityClass) {
    this.searchIntegrator = searchIntegrator;
    this.queryBuilder = new LuceneQueryBuilder(searchIntegrator.buildQueryBuilder().forEntity(entityClass).get());
    this.filterConverter = new FilterConverter(queryBuilder);
    this.orderConverter = new OrderConverter();
  }

  public HSQuery convert(CriteriaQuery query) {
    HSQuery hsQuery = createHSQuery(query);
    addPagingToQuery(hsQuery, query);
    addSortToQuery(hsQuery, query);
    return hsQuery;
  }

  public HSQuery string(StringQuery query) {
    return null;
  }

  public HSQuery query(Query query) {
    HSQuery hsQuery = createHSQuery(query.getEntityClass(), queryBuilder.matchAll());
    addPagingToQuery(hsQuery, query);
    addSortToQuery(hsQuery, query);
    return hsQuery;
  }

  private void addPagingToQuery(HSQuery hsQuery, Query query) {
    Pageable pageable = query.getPageable();
    if (pageable != null) {
      // TODO
    }
  }

  private void addSortToQuery(HSQuery hsQuery, Query query) {
    org.springframework.data.domain.Sort querySort = query.getSort();
    if (querySort != null) {
      Sort sort = orderConverter.convert(querySort);
      hsQuery.sort(sort);
    }
  }

  private HSQuery createHSQuery(Class<?> entityClass, org.apache.lucene.search.Query query) {
    return searchIntegrator.createHSQuery().luceneQuery(query).targetedEntities(Collections.singletonList(entityClass));
  }

  private HSQuery createHSQuery(CriteriaQuery query) {
    return createHSQuery(query.getEntityClass(), createLuceneQuery(query));
  }

  private org.apache.lucene.search.Query createLuceneQuery(CriteriaQuery query) {
    return filterConverter.convert(query.getCriteria());
  }
}
