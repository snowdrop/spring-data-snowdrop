package org.jboss.data.hibernatesearch.repository.query;

import java.lang.reflect.Method;

import org.jboss.data.hibernatesearch.annotations.Query;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchQueryMethod extends QueryMethod {

  private final Query queryAnnotation;

  public HibernateSearchQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
    super(method, metadata, factory);
    this.queryAnnotation = method.getAnnotation(Query.class);
  }

  public boolean hasAnnotatedQuery() {
    return this.queryAnnotation != null;
  }

  public String getAnnotatedQuery() {
    return (String) AnnotationUtils.getValue(queryAnnotation, "value");
  }
}
