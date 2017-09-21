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

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import me.snowdrop.data.hibernatesearch.spi.QueryAdapter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.spi.IndexedTypeIdentifier;
import org.hibernate.search.spi.SearchIntegrator;
import org.hibernate.search.spi.impl.PojoIndexedTypeIdentifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.StreamUtils;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractQueryAdapter<T> implements QueryAdapter<T> {
  protected Class<T> entityClass;

  private LuceneQueryBuilder queryBuilder;
  private CriteriaConverter criteriaConverter;
  private OrderConverter orderConverter = OrderConverter.INSTANCE;

  protected void initialize(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
    this.entityClass = query.getEntityClass();
    this.queryBuilder = new LuceneQueryBuilder(getSearchIntegrator().buildQueryBuilder().forEntity(entityClass).get());
    this.criteriaConverter = new CriteriaConverter(queryBuilder);

    BaseQuery<T> baseQuery = (BaseQuery<T>) query;
    baseQuery.apply(this);
  }

  protected abstract long size();

  protected abstract List<T> list();

  protected abstract Stream<T> stream();

  protected T single() {
    List<T> list = list();
    switch (list.size()) {
      case 0:
        return null;
      case 1:
        return list.get(0);
      default:
        throw new IncorrectResultSizeDataAccessException(String.format("Found %s results, expected 1.", list.size()), 1);
    }
  }

  protected Stream<T> toStream(Iterable<T> iterable) {
    return toStream(iterable.iterator());
  }

  protected Stream<T> toStream(Iterator<T> iterator) {
    return StreamUtils.createStreamFromIterator(iterator);
  }

  public long size(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
    initialize(query);
    return size();
  }

  public T single(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
    initialize(query);
    return single();
  }

  public List<T> list(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
    initialize(query);
    return list();
  }

  public Stream<T> stream(me.snowdrop.data.hibernatesearch.spi.Query<T> query) {
    initialize(query);
    return stream();
  }

  protected abstract SearchIntegrator getSearchIntegrator();

  protected abstract void applyLuceneQuery(Query query);

  protected abstract void setSort(Sort sort);

  protected abstract void setFirstResult(int firstResult);

  protected abstract void setMaxResults(int maxResults);

  void convert(CriteriaQuery query) {
    fillQuery(query);
    // apply/override max results
    if (query.getMaxResults() != null) {
      setMaxResults(query.getMaxResults());
    }
  }

  void string(StringQuery query) {
    try {
      String[] fields = query.getFields();
      IndexedTypeIdentifier iti = PojoIndexedTypeIdentifier.convertFromLegacy(entityClass);
      Analyzer analyzer = getSearchIntegrator().getAnalyzer(iti);
      QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
      org.apache.lucene.search.Query luceneQuery = parser.parse(query.getQuery());
      fillQuery(query, luceneQuery);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  void query(me.snowdrop.data.hibernatesearch.spi.Query query) {
    fillQuery(query, queryBuilder.matchAll());
  }

  private void fillQuery(me.snowdrop.data.hibernatesearch.spi.Query query, org.apache.lucene.search.Query luceneQuery) {
    applyLuceneQuery(luceneQuery);
    addSortToQuery(query);
    addPagingToQuery(query);
  }

  private void fillQuery(CriteriaQuery query) {
    fillQuery(query, createLuceneQuery(query));
  }

  private org.apache.lucene.search.Query createLuceneQuery(CriteriaQuery query) {
    return criteriaConverter.convert(query.getCriteria());
  }

  private void addSortToQuery(me.snowdrop.data.hibernatesearch.spi.Query query) {
    addSortToQuery(query.getSort());
  }

  private void addSortToQuery(org.springframework.data.domain.Sort sort) {
    if (sort != null) {
      Sort hsSort = orderConverter.convert(sort);
      setSort(hsSort);
    }
  }

  private void addPagingToQuery(me.snowdrop.data.hibernatesearch.spi.Query query) {
    Pageable pageable = query.getPageable();
    if (pageable != null) {
      org.springframework.data.domain.Sort sort = pageable.getSort();
      if (query.getSort() == null && sort != null) {
        addSortToQuery(sort);
      }
      setFirstResult(pageable.getOffset());
      setMaxResults(pageable.getPageSize());
    }
  }
}