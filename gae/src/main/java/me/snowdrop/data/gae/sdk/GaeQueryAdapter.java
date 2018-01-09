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

package me.snowdrop.data.gae.sdk;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import me.snowdrop.data.core.query.AbstractQueryAdapter;
import me.snowdrop.data.core.query.CriteriaConverter;
import me.snowdrop.data.core.query.Projection;
import me.snowdrop.data.core.query.QueryHelper;
import me.snowdrop.data.core.query.StringQuery;
import me.snowdrop.data.core.util.Integers;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GaeQueryAdapter<T> extends AbstractQueryAdapter<T, Query, Void> {

    private final Class<T> entityClass;
    private final EntityToModelMapper entityToModelMapper;

    private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private final FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
    private Query query;

    public GaeQueryAdapter(Class<T> entityClass, EntityToModelMapper entityToModelMapper) {
        this.entityClass = entityClass;
        this.entityToModelMapper = entityToModelMapper;
    }

    @Override
    protected String getFieldName(String property) {
        return property;
    }

    @Override
    protected QueryHelper<Query, Void> createQueryHelper() {
        String kind = entityToModelMapper.getKind(entityClass);
        return new GaeQueryHelper(kind);
    }

    @Override
    protected CriteriaConverter<Query> createCriteriaConverter() {
        String kind = entityToModelMapper.getKind(entityClass);
        return new GaeCriteriaConverter(kind);
    }

    @Override
    protected long size() {
        return datastoreService.prepare(query).countEntities(fetchOptions);
    }

    @Override
    protected List<T> list() {
        List<Entity> entities = datastoreService.prepare(query).asList(fetchOptions);
        return entities.stream().map(e -> entityToModelMapper.toModel(entityClass, e)).collect(Collectors.toList());
    }

    @Override
    protected Stream<T> stream() {
        return toStream(list());
    }

    @Override
    protected void applyQueryImpl(Query query) {
        this.query = query;
    }

    @Override
    protected void addSortToQuery(Sort sort) {
        for (Sort.Order order : sort) {
            query.addSort(
                order.getProperty(),
                order.isAscending() ? Query.SortDirection.ASCENDING : Query.SortDirection.DESCENDING
            );
        }
    }

    @Override
    protected void setSort(Void handle) {
    }

    @Override
    protected void setFirstResult(long firstResult) {
        fetchOptions.offset(Integers.safeCast(firstResult));
    }

    @Override
    protected void setMaxResults(long maxResults) {
        fetchOptions.limit(Integers.safeCast(maxResults));
    }

    @Override
    protected void setProjections(Projection[] projections) {
        for (Projection p : projections) {
            query.addProjection(new PropertyProjection(p.getName(), p.getType()));
        }
    }

    @Override
    protected void string(StringQuery<T> stringQuery) {
        throw new UnsupportedOperationException("String query is not supported in GAE!");
    }
}
