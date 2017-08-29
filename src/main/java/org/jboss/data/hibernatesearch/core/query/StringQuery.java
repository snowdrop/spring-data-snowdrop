package org.jboss.data.hibernatesearch.core.query;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class StringQuery extends AbstractQuery {
  private final String query;

  public StringQuery(Class<?> entityClass, String query) {
    super(entityClass);
    this.query = query;
  }

  public String getQuery() {
    return query;
  }
}
