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

import java.util.Map;
import java.util.Objects;

import me.snowdrop.data.hibernatesearch.spi.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class BaseQuery<T> implements Query<T> {
    private Class<T> entityClass;
    private Pageable pageable;
    private Sort sort;
    private Map<String, String> targetFields;

    public BaseQuery(Class<T> entityClass) {
        Objects.requireNonNull(entityClass);
        this.entityClass = entityClass;
    }

    void apply(AbstractQueryAdapter<T, ?, ?> adapter) {
        adapter.query(this);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Map<String, String> getTargetFields() {
        return targetFields;
    }

    public void setTargetFields(Map<String, String> targetFields) {
        this.targetFields = targetFields;
    }
}
