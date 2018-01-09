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

import java.util.Optional;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class UnsupportedCrudAdapter<T, ID> implements CrudAdapter<T, ID> {
    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("CRUD is not supported with this repository type.");
    }

    @Override
    public <S extends T> S save(S entity) {
        throw unsupported();
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        throw unsupported();
    }

    @Override
    public Optional<T> findById(ID id) {
        throw unsupported();
    }

    @Override
    public boolean existsById(ID id) {
        throw unsupported();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        throw unsupported();
    }

    @Override
    public void deleteById(Object id) {
        throw unsupported();
    }

    @Override
    public void delete(T entity) {
        throw unsupported();
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        throw unsupported();
    }

    @Override
    public void deleteAll() {
        throw unsupported();
    }
}
