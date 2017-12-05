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

package me.snowdrop.data.hibernatesearch.repository.support;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

import me.snowdrop.data.hibernatesearch.core.HibernateSearchOperations;
import me.snowdrop.data.hibernatesearch.core.mapping.HibernateSearchPersistentProperty;
import me.snowdrop.data.hibernatesearch.repository.HibernateSearchCrudRepository;
import me.snowdrop.data.hibernatesearch.repository.extension.RepositoryHibernateSearchExtension;
import me.snowdrop.data.hibernatesearch.repository.query.HibernateSearchPartQuery;
import me.snowdrop.data.hibernatesearch.repository.query.HibernateSearchQueryMethod;
import me.snowdrop.data.hibernatesearch.repository.query.HibernateSearchStringQuery;
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
public class HibernateSearchRepositoryFactory extends ExtendingRepositoryFactorySupport {
    private HibernateSearchOperations hibernateSearchOperations;

    public HibernateSearchRepositoryFactory(HibernateSearchOperations hibernateSearchOperations) {
        this.hibernateSearchOperations = hibernateSearchOperations;
        addRepositoryProxyPostProcessor(new HibernateSearchRepositoryProxyPostProcessor());
    }

    private Class<?> getExactRepositoryBaseClass(RepositoryMetadata metadata) {
        if (HibernateSearchCrudRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            return SimpleCrudRepository.class;
        } else {
            return SimpleRepository.class;
        }
    }

    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        PersistentEntity<?, HibernateSearchPersistentProperty> persistentEntity =
            hibernateSearchOperations.getMappingContext().getPersistentEntity(domainClass);
        return new MappingHibernateSearchEntityInformation(persistentEntity);
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation info) {
        return getTargetRepositoryViaReflection(info, hibernateSearchOperations, getEntityInformation(info.getDomainType()));
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
        return Optional.of(new HibernateSearchQueryLookupStrategy());
    }

    @Override
    protected Class<?> getRepositoryExtensionInterface() {
        return RepositoryHibernateSearchExtension.class;
    }

    private class HibernateSearchQueryLookupStrategy implements QueryLookupStrategy {

        @Override
        public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {

            HibernateSearchQueryMethod queryMethod = new HibernateSearchQueryMethod(method, metadata, factory);
            String namedQueryName = queryMethod.getNamedQueryName();

            if (namedQueries.hasQuery(namedQueryName)) {
                String namedQuery = namedQueries.getQuery(namedQueryName);
                return new HibernateSearchStringQuery(queryMethod, hibernateSearchOperations, namedQuery);
            } else if (queryMethod.hasAnnotatedQuery()) {
                return new HibernateSearchStringQuery(queryMethod, hibernateSearchOperations, queryMethod.getAnnotatedQuery());
            }
            return new HibernateSearchPartQuery(queryMethod, hibernateSearchOperations);
        }
    }
}
