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

package me.snowdrop.data.core.repository.query;

import java.util.Optional;

import me.snowdrop.data.core.ops.SnowdropOperations;
import me.snowdrop.data.core.query.BaseQuery;
import me.snowdrop.data.core.spi.Query;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionInformation;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ResultProcessor;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractSnowdropRepositoryQuery implements RepositoryQuery {
    private final SnowdropQueryMethod queryMethod;
    protected final SnowdropOperations snowdropOperations;

    public AbstractSnowdropRepositoryQuery(SnowdropQueryMethod queryMethod, SnowdropOperations snowdropOperations) {
        this.queryMethod = queryMethod;
        this.snowdropOperations = snowdropOperations;
    }

    public SnowdropQueryMethod getQueryMethod() {
        return queryMethod;
    }

    protected abstract boolean isModify(Query<?> query);

    protected abstract boolean isExistsProjection(Query<?> query);

    protected abstract boolean isCountProjection(Query<?> query);

    protected abstract BaseQuery<?> createQuery(ParametersParameterAccessor accessor);

    protected Class<?> getProjection(ParametersParameterAccessor accessor) {
        Optional<Class<?>> dp = accessor.getDynamicProjection();
        if (dp.isPresent()) {
            return dp.get();
        }
        Class<?> returnType = getQueryMethod().getReturnedObjectType();
        if (returnType.isInterface()) {
            return returnType;
        }
        // TODO - handle DTO
        return null;
    }

    @Override
    public Object execute(Object[] parameters) {
        ParametersParameterAccessor accessor = new ParametersParameterAccessor(getQueryMethod().getParameters(), parameters);
        ResultProcessor resultProcessor = getQueryMethod().getResultProcessor().withDynamicProjection(accessor);
        Object source = executeInternal(accessor);
        return resultProcessor.processResult(source);
    }

    private Object executeInternal(ParametersParameterAccessor accessor) {
        BaseQuery<?> query = createQuery(accessor);

        Class<?> projectionClass = getProjection(accessor);
        if (projectionClass != null) {
            ProjectionInformation pi = getQueryMethod().getFactory().getProjectionInformation(projectionClass);
            query.setProjectionInformation(pi);
        }

        Sort sort = query.getSort();
        if (sort == null) {
            sort = accessor.getSort();
        } else {
            sort = sort.and(accessor.getSort());
        }
        query.setSort(sort);

        if (query.getPageable() == null) {
            query.setPageable(accessor.getPageable());
        }

        if (isModify(query)) {
            throw new UnsupportedOperationException("Hibernate Search repository support is read-only!");
        } else if (getQueryMethod().isSliceQuery()) {
            return snowdropOperations.findSlice(query);
        } else if (getQueryMethod().isPageQuery()) {
            return snowdropOperations.findPageable(query);
        } else if (getQueryMethod().isStreamQuery()) {
            return snowdropOperations.stream(query);
        } else if (getQueryMethod().isCollectionQuery()) {
            return snowdropOperations.findAll(query);
        } else if (isCountProjection(query)) {
            return snowdropOperations.count(query);
        } else if (isExistsProjection(query)) {
            return (snowdropOperations.count(query) > 0);
        } else if ((projectionClass != null) || getQueryMethod().isQueryForEntity()) {
            return snowdropOperations.findSingle(query);
        } else {
            throw new IllegalArgumentException("Invalid query type.");
        }
    }
}
