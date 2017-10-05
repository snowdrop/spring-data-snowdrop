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

package me.snowdrop.data.hibernatesearch.repository.query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import me.snowdrop.data.hibernatesearch.annotations.Query;
import me.snowdrop.data.hibernatesearch.annotations.QueryHint;
import me.snowdrop.data.hibernatesearch.annotations.QueryHints;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchQueryMethod extends QueryMethod {

  private final Method method;
  private final Query queryAnnotation;

  public HibernateSearchQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
    super(method, metadata, factory);
    this.method = method;
    this.queryAnnotation = method.getAnnotation(Query.class);
  }

  public QueryHints getQueryHints() {
    QueryHints queryHints = method.getAnnotation(QueryHints.class);
    if (queryHints == null) {
      QueryHint queryHint = method.getAnnotation(QueryHint.class);
      if (queryHint != null) {
        queryHints = new QueryHintsImpl(queryHint);
      }
    }
    return queryHints;
  }

  public boolean hasAnnotatedQuery() {
    return this.queryAnnotation != null;
  }

  public String getAnnotatedQuery() {
    return (String) AnnotationUtils.getValue(queryAnnotation, "value");
  }

  private static class QueryHintsImpl implements QueryHints {
    private QueryHint queryHint;

    public QueryHintsImpl(QueryHint queryHint) {
      this.queryHint = queryHint;
    }

    @Override
    public QueryHint[] value() {
      return new QueryHint[]{queryHint};
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return QueryHints.class;
    }
  }
}
