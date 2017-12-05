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

import java.lang.reflect.Method;

import me.snowdrop.data.hibernatesearch.annotations.Query;
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

    public boolean hasAnnotatedQuery() {
        return this.queryAnnotation != null;
    }

    public String getAnnotatedQuery() {
        return (String) AnnotationUtils.getValue(queryAnnotation, "value");
    }

    Method getMethod() {
        return method;
    }
}
