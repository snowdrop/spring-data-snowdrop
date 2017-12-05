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

package me.snowdrop.data.hibernatesearch.orm;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import me.snowdrop.data.hibernatesearch.core.query.lucene.LuceneQueryAdapter;
import me.snowdrop.data.hibernatesearch.spi.CrudAdapter;
import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import me.snowdrop.data.hibernatesearch.spi.QueryAdapter;
import me.snowdrop.data.hibernatesearch.util.Integers;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.hcore.util.impl.ContextHelper;
import org.hibernate.search.spi.SearchIntegrator;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class JpaDatasourceMapper implements DatasourceMapper {

    private EntityManagerFactory emf;

    public JpaDatasourceMapper(EntityManagerFactory emf) {
        Assert.notNull(emf, "Null EntityManagerFactory!");
        this.emf = emf;
    }

    private EntityManager getEntityManager() {
        EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
        if (em == null) {
            return emf.createEntityManager();
        }
        return em;
    }

    public <T> QueryAdapter<T> createQueryAdapter(Class<T> entityClass) {
        return new OrmQueryAdapter<>();
    }

    public <T, ID> CrudAdapter<T, ID> createCrudAdapter(EntityInformation<T, ID> ei) {
        return new OrmCrudAdapter<>(ei.getJavaType());
    }

    private class OrmQueryAdapter<T> extends LuceneQueryAdapter<T> {
        private SearchIntegrator searchIntegrator;

        private FullTextQuery fullTextQuery;
        private EntityManager entityManager;

        private void close() {
            if (entityManager != null) {
                entityManager.close();
            }
        }

        protected SearchIntegrator getSearchIntegrator() {
            if (searchIntegrator == null) {
                searchIntegrator = ContextHelper.getSearchIntegratorBySF(emf.unwrap(SessionFactoryImplementor.class));
            }
            return searchIntegrator;
        }

        protected void applyQueryImpl(Query query) {
            EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
            if (em == null) {
                entityManager = emf.createEntityManager();
                em = entityManager;
            }
            FullTextSession fullTextSession = Search.getFullTextSession(em.unwrap(Session.class));
            fullTextQuery = fullTextSession.createFullTextQuery(query, entityClass);
        }

        protected void setSort(Sort sort) {
            fullTextQuery.setSort(sort);
        }

        protected void setFirstResult(long firstResult) {
            fullTextQuery.setFirstResult(Integers.safeCast(firstResult));
        }

        protected void setMaxResults(long maxResults) {
            fullTextQuery.setMaxResults(Integers.safeCast(maxResults));
        }

        protected long size() {
            try {
                return fullTextQuery.getResultSize();
            } finally {
                close();
            }
        }

        protected T single() {
            try {
                //noinspection unchecked
                return (T) fullTextQuery.uniqueResult();
            } catch (NonUniqueResultException ex) {
                throw new IncorrectResultSizeDataAccessException(ex.getMessage(), 1);
            } finally {
                close();
            }
        }

        protected List<T> list() {
            try {
                //noinspection unchecked
                return fullTextQuery.getResultList();
            } finally {
                close();
            }
        }

        protected Stream<T> stream() {
            //noinspection unchecked
            Stream<T> stream = fullTextQuery.stream();
            return stream.onClose(this::close);
        }
    }

    private class OrmCrudAdapter<T, ID> implements CrudAdapter<T, ID> {
        private final SimpleJpaRepository<T, ID> repository;

        public OrmCrudAdapter(Class<T> entityType) {
            repository = new SimpleJpaRepository<>(entityType, getEntityManager());
        }

        public <S extends T> S save(S entity) {
            return repository.save(entity);
        }

        public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
            return repository.saveAll(entities);
        }

        public Optional<T> findById(ID id) {
            return repository.findById(id);
        }

        public boolean existsById(ID id) {
            return repository.existsById(id);
        }

        public Iterable<T> findAllById(Iterable<ID> ids) {
            return repository.findAllById(ids);
        }

        public void deleteById(ID id) {
            repository.deleteById(id);
        }

        public void delete(T entity) {
            repository.delete(entity);
        }

        public void deleteAll(Iterable<? extends T> entities) {
            repository.deleteAll(entities);
        }

        public void deleteAll() {
            repository.deleteAll();
        }
    }
}
