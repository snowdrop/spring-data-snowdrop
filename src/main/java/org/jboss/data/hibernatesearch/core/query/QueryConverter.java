package org.jboss.data.hibernatesearch.core.query;

import java.util.Collections;

import org.apache.lucene.search.Sort;
import org.hibernate.search.query.engine.spi.HSQuery;
import org.hibernate.search.spi.SearchIntegrator;

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
    addSortToQuery(hsQuery, query);
    return hsQuery;
  }

  private void addSortToQuery(HSQuery hsQuery, CriteriaQuery query) {
    org.springframework.data.domain.Sort querySort = query.getSort();
    if (querySort != null) {
      Sort sort = orderConverter.convert(querySort);
      hsQuery.sort(sort);
    }
  }

  private HSQuery createHSQuery(CriteriaQuery query) {
    return searchIntegrator.createHSQuery().luceneQuery(createLuceneQuery(query)).targetedEntities(Collections.singletonList(query.getEntityClass()));
  }

  private org.apache.lucene.search.Query createLuceneQuery(CriteriaQuery query) {
    return filterConverter.convert(query.getCriteria());
  }
}
