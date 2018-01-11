/*
 * Copyright 2018 Red Hat, Inc, and individual contributors.
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

package me.snowdrop.data.gcp.gcd;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import me.snowdrop.data.core.query.AbstractQueryAdapter;
import me.snowdrop.data.core.query.CriteriaConverter;
import me.snowdrop.data.core.query.Projection;
import me.snowdrop.data.core.query.QueryHelper;
import me.snowdrop.data.core.query.StringQuery;
import me.snowdrop.data.gcp.EntityToModelMapper;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GcdQueryAdapter<T> extends AbstractQueryAdapter<T, QueryHandle, Void> {

    private final Class<T> entityClass;
    private final EntityToModelMapper<Entity, FullEntity<IncompleteKey>> entityToModelMapper;

    private QueryHandle handle;

    public GcdQueryAdapter(Class<T> entityClass, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> entityToModelMapper) {
        this.entityClass = entityClass;
        this.entityToModelMapper = entityToModelMapper;
    }

    @Override
    protected void initialize(me.snowdrop.data.core.spi.Query<T> query) {
        Class<T> entityClass = query.getEntityClass();
        String kind = entityToModelMapper.getKind(entityClass);
        Projection[] p = getProjections(query);
        handle = (p != null ? new QueryHandles.ProjectionQueryHandle(kind) : new QueryHandles.EntityQueryHandle(kind));

        super.initialize(query);
    }

    @Override
    protected String getFieldName(String property) {
        return property;
    }

    @Override
    protected QueryHelper<QueryHandle, Void> createQueryHelper() {
        return new GcdQueryHelper(handle);
    }

    @Override
    protected CriteriaConverter<QueryHandle> createCriteriaConverter() {
        return new GcdCriteriaConverter(handle);
    }

    @Override
    protected long size() {
        return list().size();
    }

    @Override
    protected List<T> list() {
        return handle.toList(entityClass, entityToModelMapper);
    }

    @Override
    protected Stream<T> stream() {
        Iterator<T> iterator = handle.toIterator(entityClass, entityToModelMapper);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

    @Override
    protected void applyQueryImpl(QueryHandle handle) {
        this.handle = handle;
    }

    @Override
    protected void setSort(Void handle) {
    }

    @Override
    protected void setFirstResult(long firstResult) {
        handle.setFirstResult(firstResult);
    }

    @Override
    protected void setMaxResults(long maxResults) {
        handle.setMaxResults(maxResults);
    }

    @Override
    protected void setProjections(Projection[] projections) {
        handle.setProjections(projections);
    }

    @Override
    protected void string(StringQuery<T> stringQuery) {
        fillQuery(stringQuery, new QueryHandles.StringQueryHandle(stringQuery.getQuery()));
    }
}
