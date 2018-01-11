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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.GqlQuery;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.ProjectionEntityQuery;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import me.snowdrop.data.core.query.Projection;
import me.snowdrop.data.core.util.Integers;
import me.snowdrop.data.gcp.EntityToModelMapper;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class QueryHandles {
    static Datastore getDatastore() {
        return DatastoreUtils.getDatastore();
    }

    static <T> List<T> toList(Class<T> entityClass, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> mapper, QueryResults<Entity> qr) {
        List<T> list = new ArrayList<>();
        while (qr.hasNext()) {
            list.add(mapper.toModel(entityClass, qr.next()));
        }
        return list;
    }

    static <T> Iterator<T> toIterator(Class<T> entityClass, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> mapper, QueryResults<Entity> qr) {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return qr.hasNext();
            }

            @Override
            public T next() {
                return mapper.toModel(entityClass, qr.next());
            }
        };
    }

    static class EntityQueryHandle implements QueryHandle {
        private EntityQuery.Builder builder;

        public EntityQueryHandle(String kind) {
            builder = Query.newEntityQueryBuilder().setKind(kind);
        }

        @Override
        public <T> List<T> toList(Class<T> entityClass, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> mapper) {
            QueryResults<Entity> qr = getDatastore().run(builder.build());
            return QueryHandles.toList(entityClass, mapper, qr);
        }

        @Override
        public <T> Iterator<T> toIterator(Class<T> entityClass, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> mapper) {
            QueryResults<Entity> qr = getDatastore().run(builder.build());
            return QueryHandles.toIterator(entityClass, mapper, qr);
        }

        @Override
        public void setFilter(StructuredQuery.Filter filter) {
            builder.setFilter(filter);
        }

        @Override
        public void sort(Sort sort) {
            for (Sort.Order order : sort) {
                builder.addOrderBy(
                    order.isAscending() ?
                        StructuredQuery.OrderBy.asc(order.getProperty()) :
                        StructuredQuery.OrderBy.desc(order.getProperty())
                );
            }
        }

        @Override
        public void setFirstResult(long firstResult) {
            builder.setOffset(Integers.safeCast(firstResult));
        }

        @Override
        public void setMaxResults(long maxResults) {
            builder.setLimit(Integers.safeCast(maxResults));
        }

        @Override
        public void setProjections(Projection[] projections) {
        }
    }

    static class ProjectionQueryHandle implements QueryHandle {
        private ProjectionEntityQuery.Builder builder;
        private List<String> fields = new ArrayList<>();

        public ProjectionQueryHandle(String kind) {
            builder = Query.newProjectionEntityQueryBuilder().setKind(kind);
        }

        @Override
        public <T> List<T> toList(Class<T> entityClass, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> mapper) {
            List<T> list = new ArrayList<>();
            QueryResults<ProjectionEntity> qr = getDatastore().run(builder.build());
            while (qr.hasNext()) {
                list.add(toEntity(qr.next()));
            }
            return list;
        }

        @Override
        public <T> Iterator<T> toIterator(Class<T> entityClass, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> mapper) {
            QueryResults<ProjectionEntity> qr = getDatastore().run(builder.build());
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return qr.hasNext();
                }

                @Override
                public T next() {
                    return toEntity(qr.next());
                }
            };
        }

        private <T> T toEntity(ProjectionEntity result) {
            Object[] array = new Object[fields.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = result.getValue(fields.get(i)).get();
            }
            //noinspection unchecked
            return (T) array; // hack?
        }

        @Override
        public void setFilter(StructuredQuery.Filter filter) {
            builder.setFilter(filter);
        }

        @Override
        public void sort(Sort sort) {
            for (Sort.Order order : sort) {
                builder.addOrderBy(
                    order.isAscending() ?
                        StructuredQuery.OrderBy.asc(order.getProperty()) :
                        StructuredQuery.OrderBy.desc(order.getProperty())
                );
            }
        }

        @Override
        public void setFirstResult(long firstResult) {
            builder.setOffset(Integers.safeCast(firstResult));
        }

        @Override
        public void setMaxResults(long maxResults) {
            builder.setLimit(Integers.safeCast(maxResults));
        }

        @Override
        public void setProjections(Projection[] projections) {
            for (Projection p : projections) {
                String name = p.getName();
                builder.addProjection(name);
                fields.add(name);
            }
        }
    }

    static class StringQueryHandle implements QueryHandle {
        private GqlQuery.Builder<Entity> builder;

        public StringQueryHandle(String gql) {
            builder = Query.newGqlQueryBuilder(Query.ResultType.ENTITY, gql);
        }

        @Override
        public <T> List<T> toList(Class<T> entityClass, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> mapper) {
            QueryResults<Entity> qr = getDatastore().run(builder.build());
            return QueryHandles.toList(entityClass, mapper, qr);
        }

        @Override
        public <T> Iterator<T> toIterator(Class<T> entityClass, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> mapper) {
            QueryResults<Entity> qr = getDatastore().run(builder.build());
            return QueryHandles.toIterator(entityClass, mapper, qr);
        }

        @Override
        public void setFilter(StructuredQuery.Filter filter) {
        }

        @Override
        public void sort(Sort sort) {
        }

        @Override
        public void setFirstResult(long firstResult) {
        }

        @Override
        public void setMaxResults(long maxResults) {
        }

        @Override
        public void setProjections(Projection[] projections) {
        }
    }
}
