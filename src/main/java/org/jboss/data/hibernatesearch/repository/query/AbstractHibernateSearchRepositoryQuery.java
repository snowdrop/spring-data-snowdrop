package org.jboss.data.hibernatesearch.repository.query;

import org.jboss.data.hibernatesearch.core.HibernateSearchOperations;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractHibernateSearchRepositoryQuery implements RepositoryQuery {
  private final QueryMethod queryMethod;
  protected final HibernateSearchOperations hibernateSearchOperations;

  public AbstractHibernateSearchRepositoryQuery(QueryMethod queryMethod, HibernateSearchOperations hibernateSearchOperations) {
    this.queryMethod = queryMethod;
    this.hibernateSearchOperations = hibernateSearchOperations;
  }

  @Override
  public QueryMethod getQueryMethod() {
    return queryMethod;
  }
}
