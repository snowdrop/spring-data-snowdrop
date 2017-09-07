package org.jboss.data.hibernatesearch.core.query;

import java.util.Arrays;
import java.util.Collection;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.WildcardQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.RangeMatchingContext;
import org.hibernate.search.query.dsl.TermMatchingContext;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class LuceneQueryBuilder {

  private QueryBuilder queryBuilder;

  public LuceneQueryBuilder(QueryBuilder queryBuilder) {
    this.queryBuilder = queryBuilder;
  }

  public Query matchAll() {
    return queryBuilder.all().createQuery();
  }

  public Query all(Collection<Query> subQueries) {
    BooleanJunction<BooleanJunction> bool = queryBuilder.bool();
    for (Query subQuery : subQueries) {
      bool.must(subQuery);
    }
    return bool.createQuery();
  }

  public Query any(Query... subQueries) {
    return any(Arrays.asList(subQueries));
  }

  public Query any(Collection<Query> subQueries) {
    BooleanJunction<BooleanJunction> bool = queryBuilder.bool();
    for (Query subQuery : subQueries) {
      bool.should(subQuery);
    }
    return bool.createQuery();
  }

  public Query in(String fieldName, Collection<?> values) {
    BooleanJunction<BooleanJunction> bool = queryBuilder.bool();
    for (Object value : values) {
      bool.should(equal(fieldName, value));
    }
    return bool.createQuery();
  }

  public Query notIn(String fieldName, Collection<?> values) {
    BooleanJunction<BooleanJunction> bool = queryBuilder.bool();
    for (Object value : values) {
      bool.must(equal(fieldName, value)).not();
    }
    return bool.createQuery();
  }

  public Query notEqual(String fieldName, Object value) {
    return any(
      lessThan(fieldName, value),
      greaterThan(fieldName, value)
    );
  }

  public Query not(Query query) {
    return queryBuilder.bool().must(query).not().createQuery();
  }

  public Query equal(String fieldName, Object value) {
    return keywordOnField(fieldName)
      .matching(value)
      .createQuery();
  }

  private TermMatchingContext keywordOnField(String fieldName) {
    return queryBuilder
      .keyword().onField(fieldName)
      .ignoreFieldBridge();
  }

  public Query greaterThan(String fieldName, Object value) {
    return rangeOnField(fieldName)
      .above(value).excludeLimit()
      .createQuery();
  }

  public Query greaterThanOrEqual(String fieldName, Object value) {
    return rangeOnField(fieldName)
      .above(value)
      .createQuery();
  }

  public Query lessThan(String fieldName, Object value) {
    return rangeOnField(fieldName)
      .below(value).excludeLimit()
      .createQuery();
  }

  public Query lessThanOrEqual(String fieldName, Object value) {
    return rangeOnField(fieldName)
      .below(value)
      .createQuery();
  }

  public Query between(String fieldName, Object min, Object max) {
    BooleanJunction<BooleanJunction> bool = queryBuilder.bool();
    bool.must(greaterThanOrEqual(fieldName, min)).must(lessThanOrEqual(fieldName, max));
    return bool.createQuery();
  }

  public Query fuzzy(String fieldName, Object value) {
    return new FuzzyQuery(new Term(fieldName, StringUtils.toString(value)));
  }

  public Query contains(String fieldName, Object value) {
    return new WildcardQuery(new Term(fieldName, "*" + value + "*"));
  }

  public Query notContains(String fieldName, Object value) {
    BooleanJunction<BooleanJunction> bool = queryBuilder.bool();
    bool.must(contains(fieldName, value)).not();
    return bool.createQuery();
  }

  public Query startsWith(String fieldName, Object value) {
    return new WildcardQuery(new Term(fieldName, value + "*"));
  }

  public Query endsWith(String fieldName, Object value) {
    return new WildcardQuery(new Term(fieldName, "*" + value));
  }

  public Query reqexp(String fieldName, String reqexp) {
    return new RegexpQuery(new Term(fieldName, reqexp));
  }

  private RangeMatchingContext rangeOnField(String fieldName) {
    return queryBuilder
      .range().onField(fieldName)
      .ignoreFieldBridge();
  }
}
