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

import me.snowdrop.data.hibernatesearch.spi.QueryAdapter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Sort;
import org.hibernate.search.spi.SearchIntegrator;
import org.springframework.data.domain.Pageable;

/**
 * @author Ales Justin
 */
public class QueryConverter {

  private LuceneQueryBuilder queryBuilder;
  private SearchIntegrator searchIntegrator;

  private CriteriaConverter criteriaConverter;
  private OrderConverter orderConverter;

  public QueryConverter(SearchIntegrator searchIntegrator, Class<?> entityClass) {
    this.searchIntegrator = searchIntegrator;
    this.queryBuilder = new LuceneQueryBuilder(searchIntegrator.buildQueryBuilder().forEntity(entityClass).get());
    this.criteriaConverter = new CriteriaConverter(queryBuilder);
    this.orderConverter = new OrderConverter();
  }

  public void convert(QueryAdapter queryAdapter, CriteriaQuery query) {
    fillQuery(queryAdapter, query);
  }

  public void string(QueryAdapter queryAdapter, StringQuery query) {
    try {
      String[] fields = query.getFields();
      QueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
      org.apache.lucene.search.Query luceneQuery = parser.parse(query.getQuery());
      fillQuery(queryAdapter, query, luceneQuery);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public void query(QueryAdapter queryAdapter, Query query) {
    fillQuery(queryAdapter, query, queryBuilder.matchAll());
  }

  private void addSortToQuery(QueryAdapter queryAdapter, Query query) {
    addSortToQuery(queryAdapter, query.getSort());
  }

  private void addSortToQuery(QueryAdapter queryAdapter, org.springframework.data.domain.Sort sort) {
    if (sort != null) {
      Sort hsSort = orderConverter.convert(sort);
      queryAdapter.setSort(hsSort);
    }
  }

  private void addPagingToQuery(QueryAdapter queryAdapter, Query query) {
    Pageable pageable = query.getPageable();
    if (pageable != null) {
      org.springframework.data.domain.Sort sort = pageable.getSort();
      if (query.getSort() == null && sort != null) {
        addSortToQuery(queryAdapter, sort);
      }
      queryAdapter.setFirstResult(pageable.getOffset());
      queryAdapter.setMaxResults(pageable.getPageSize());
    }
  }

  private void fillQuery(QueryAdapter queryAdapter, Query query, org.apache.lucene.search.Query luceneQuery) {
    queryAdapter.applyLuceneQuery(searchIntegrator, luceneQuery);
    addSortToQuery(queryAdapter, query);
    addPagingToQuery(queryAdapter, query);
  }

  private void fillQuery(QueryAdapter queryAdapter, CriteriaQuery query) {
    fillQuery(queryAdapter, query, createLuceneQuery(query));
  }

  private org.apache.lucene.search.Query createLuceneQuery(CriteriaQuery query) {
    return criteriaConverter.convert(query.getCriteria());
  }
}
