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

import java.util.Optional;

import me.snowdrop.data.core.ops.SnowdropOperations;
import me.snowdrop.data.core.repository.SnowdropCrudRepository;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractSnowdropCrudRepository<T, ID> extends AbstractSnowdropRepository<T, ID> implements SnowdropCrudRepository<T, ID> {
    public AbstractSnowdropCrudRepository(SnowdropOperations snowdropOperations, SnowdropEntityInformation<T, ID> entityInformation) {
        super(snowdropOperations, entityInformation);
    }

    @Override
    public <S extends T> S save(S entity) {
        return snowdropOperations.save(getEntityInformation(), entity);
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return snowdropOperations.saveAll(getEntityInformation(), entities);
    }

    @Override
    public Optional<T> findById(ID id) {
        return snowdropOperations.findById(getEntityInformation(), id);
    }

    @Override
    public boolean existsById(ID id) {
        return snowdropOperations.existsById(getEntityInformation(), id);
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        return snowdropOperations.findAllById(getEntityInformation(), ids);
    }

    @Override
    public void deleteById(ID id) {
        snowdropOperations.deleteById(getEntityInformation(), id);
    }

    @Override
    public void delete(T entity) {
        snowdropOperations.delete(getEntityInformation(), entity);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        snowdropOperations.deleteAll(getEntityInformation(), entities);
    }

    @Override
    public void deleteAll() {
        snowdropOperations.deleteAll(getEntityInformation());
    }
}
