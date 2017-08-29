package org.jboss.data.hibernatesearch.core.query;

import java.util.Collections;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
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
    return createHSQuery(query);
  }

  public HSQuery string(StringQuery query) {
    try {
      String[] fields = query.getFields();
      QueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
      org.apache.lucene.search.Query luceneQuery = parser.parse(query.getQuery());
      return createHSQuery(query, luceneQuery);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public HSQuery query(Query query) {
    return createHSQuery(query, queryBuilder.matchAll());
  }

  private void addSortToQuery(HSQuery hsQuery, Query query) {
    addSortToQuery(hsQuery, query.getSort());
  }

  private void addSortToQuery(HSQuery hsQuery, org.springframework.data.domain.Sort sort) {
    if (sort != null) {
      Sort hsSort = orderConverter.convert(sort);
      hsQuery.sort(hsSort);
    }
  }

  private void addPagingToQuery(HSQuery hsQuery, Query query) {
    Pageable pageable = query.getPageable();
    if (pageable != null) {
      org.springframework.data.domain.Sort sort = pageable.getSort();
      if (query.getSort() == null && sort != null) {
        addSortToQuery(hsQuery, sort);
      }
      hsQuery.firstResult(pageable.getOffset());
      hsQuery.maxResults(pageable.getPageSize());
    }
  }

  private HSQuery createHSQuery(Query query, org.apache.lucene.search.Query luceneQuery) {
    HSQuery hsQuery = searchIntegrator.createHSQuery().luceneQuery(luceneQuery).targetedEntities(Collections.singletonList(query.getEntityClass()));
    addSortToQuery(hsQuery, query);
    addPagingToQuery(hsQuery, query);
    return hsQuery;
  }

  private HSQuery createHSQuery(CriteriaQuery query) {
    return createHSQuery(query, createLuceneQuery(query));
  }

  private org.apache.lucene.search.Query createLuceneQuery(CriteriaQuery query) {
    return filterConverter.convert(query.getCriteria());
  }
}
