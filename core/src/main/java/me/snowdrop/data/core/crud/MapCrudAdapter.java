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

package me.snowdrop.data.core.crud;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import me.snowdrop.data.core.spi.AbstractCrudAdapter;
import org.springframework.data.repository.core.EntityInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class MapCrudAdapter<T, ID> extends AbstractCrudAdapter<T, ID> {
    private final Map<ID, T> map;

    public MapCrudAdapter(EntityInformation<T, ID> entityInformation, Map<ID, T> map) {
        super(entityInformation);
        this.map = map;
    }

    @Override
    protected void save(ID id, T entity) {
        map.put(id, entity);
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        Function<? super S, Map.Entry<ID, S>> fn = s -> {
            ID id = getIdentifier(s);
            return new AbstractMap.SimpleEntry<>(id, s);
        };
        Map<ID, S> tMap = StreamSupport
            .stream(entities.spliterator(), false)
            .map(fn)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
                )
            );
        map.putAll(tMap);
        return entities;
    }

    protected abstract Map<ID, T> findAll(Set<ID> keys);

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        Set<ID> keys = StreamSupport.stream(ids.spliterator(), false).collect(Collectors.toSet());
        return findAll(keys).values();
    }

    @Override
    protected T find(ID id) {
        return map.get(id);
    }

    @Override
    public void deleteById(ID id) {
        map.remove(id);
    }

    @Override
    public void deleteAll() {
        map.clear();
    }
}
