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

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

import me.snowdrop.data.core.mapping.SnowdropPersistentProperty;
import me.snowdrop.data.core.ops.SnowdropOperations;
import me.snowdrop.data.core.repository.SnowdropCrudRepository;
import me.snowdrop.data.core.repository.extension.RepositorySnowdropExtension;
import me.snowdrop.data.core.repository.query.SnowdropPartQuery;
import me.snowdrop.data.core.repository.query.SnowdropQueryMethod;
import me.snowdrop.data.core.repository.query.SnowdropStringQuery;
import me.snowdrop.data.repository.extension.support.ExtendingRepositoryFactorySupport;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SnowdropRepositoryFactory extends ExtendingRepositoryFactorySupport {
    private SnowdropOperations snowdropOperations;

    public SnowdropRepositoryFactory(SnowdropOperations snowdropOperations) {
        this.snowdropOperations = snowdropOperations;
        addRepositoryProxyPostProcessor(new SnowdropRepositoryProxyPostProcessor());
    }

    private Class<?> getExactRepositoryBaseClass(RepositoryMetadata metadata) {
        if (SnowdropCrudRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            return SimpleCrudRepository.class;
        } else {
            return SimpleRepository.class;
        }
    }

    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        PersistentEntity<?, SnowdropPersistentProperty> persistentEntity =
            snowdropOperations.getMappingContext().getPersistentEntity(domainClass);
        return new MappingSnowdropEntityInformation(persistentEntity);
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation info) {
        return getTargetRepositoryViaReflection(info, snowdropOperations, getEntityInformation(info.getDomainType()));
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (Integer.class.isAssignableFrom(metadata.getIdType()) || Long.class.isAssignableFrom(metadata.getIdType())
            || Double.class.isAssignableFrom(metadata.getIdType())) {
            return getExactRepositoryBaseClass(metadata);
        } else if (metadata.getIdType() == String.class) {
            return getExactRepositoryBaseClass(metadata);
        } else if (metadata.getIdType() == UUID.class) {
            return getExactRepositoryBaseClass(metadata);
        } else {
            throw new IllegalArgumentException("Unsupported ID type " + metadata.getIdType());
        }
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key, EvaluationContextProvider evaluationContextProvider) {
        return Optional.of(new SnowdropQueryLookupStrategy());
    }

    @Override
    protected Class<?> getRepositoryExtensionInterface() {
        return RepositorySnowdropExtension.class;
    }

    private class SnowdropQueryLookupStrategy implements QueryLookupStrategy {

        @Override
        public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {

            SnowdropQueryMethod queryMethod = new SnowdropQueryMethod(method, metadata, factory);
            String namedQueryName = queryMethod.getNamedQueryName();

            if (namedQueries.hasQuery(namedQueryName)) {
                String namedQuery = namedQueries.getQuery(namedQueryName);
                return new SnowdropStringQuery(queryMethod, snowdropOperations, namedQuery);
            } else if (queryMethod.hasAnnotatedQuery()) {
                return new SnowdropStringQuery(queryMethod, snowdropOperations, queryMethod.getAnnotatedQuery());
            }
            return new SnowdropPartQuery(queryMethod, snowdropOperations);
        }
    }
}
