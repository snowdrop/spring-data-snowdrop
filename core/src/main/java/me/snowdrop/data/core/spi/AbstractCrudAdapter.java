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

package me.snowdrop.data.core.spi;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.core.EntityInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractCrudAdapter<T, ID> implements CrudAdapter<T, ID> {
    protected final EntityInformation<T, ID> entityInformation;

    public AbstractCrudAdapter(EntityInformation<T, ID> entityInformation) {
        this.entityInformation = entityInformation;
    }

    protected ID getIdentifier(T entity) {
        return entityInformation.getRequiredId(entity);
    }

    protected abstract void save(ID id, T entity);

    public <S extends T> S save(S entity) {
        save(getIdentifier(entity), entity);
        return entity;
    }

    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    protected abstract T find(ID id);

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(find(id));
    }

    public boolean existsById(ID id) {
        return (find(id) != null);
    }

    public Iterable<T> findAllById(Iterable<ID> ids) {
        Set<T> entities = new LinkedHashSet<>();
        for (ID id : ids) {
            T entity = find(id);
            if (entity != null) {
                entities.add(entity);
            }
        }
        return entities;
    }

    public void delete(T entity) {
        deleteById(getIdentifier(entity));
    }

    public void deleteAll(Iterable<? extends T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }
}
