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

package me.snowdrop.data.core.repository.support;

import me.snowdrop.data.core.ops.SnowdropOperations;
import me.snowdrop.data.core.query.BaseQuery;
import me.snowdrop.data.core.repository.SnowdropRepository;
import me.snowdrop.data.core.repository.query.TargetFieldsUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractSnowdropRepository<T, ID> implements SnowdropRepository<T, ID> {
    protected final SnowdropOperations snowdropOperations;
    protected final SnowdropEntityInformation<T, ID> entityInformation;

    public AbstractSnowdropRepository(SnowdropOperations snowdropOperations, SnowdropEntityInformation<T, ID> entityInformation) {
        this.snowdropOperations = snowdropOperations;
        this.entityInformation = entityInformation;
    }

    protected SnowdropEntityInformation<T, ID> getEntityInformation() {
        return entityInformation;
    }

    protected BaseQuery<T> createBaseQuery() {
        BaseQuery<T> query = new BaseQuery<>(getEntityClass());
        fillQuery(query);
        return query;
    }

    protected void fillQuery(BaseQuery<T> query) {
        MethodInvocation mi = SnowdropRepositoryProxyPostProcessor.INFO.get();
        if (mi != null) {
            query.setTargetFields(TargetFieldsUtils.getTargetFieldsMap(mi.getMethod()));
        }
    }

    @Override
    public Iterable<T> findAll() {
        BaseQuery<T> query = createBaseQuery();
        return snowdropOperations.findAll(query);
    }

    @Override
    public long count() {
        BaseQuery<T> countQuery = createBaseQuery();
        return snowdropOperations.count(countQuery);
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        BaseQuery<T> query = createBaseQuery();
        query.setSort(sort);
        return snowdropOperations.findAll(query);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        BaseQuery<T> query = createBaseQuery();
        query.setPageable(pageable);
        return snowdropOperations.findPageable(query);
    }

    @Override
    public Class<T> getEntityClass() {
        return entityInformation.getJavaType();
    }
}
