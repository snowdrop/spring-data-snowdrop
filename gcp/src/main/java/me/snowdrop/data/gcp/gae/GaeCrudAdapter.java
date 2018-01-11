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

package me.snowdrop.data.gcp.gae;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import me.snowdrop.data.core.spi.CrudAdapter;
import me.snowdrop.data.gcp.EntityToModelMapper;
import org.springframework.data.repository.core.EntityInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GaeCrudAdapter<T, ID> implements CrudAdapter<T, ID> {
    private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    private final EntityInformation<T, ID> entityInformation;
    private final EntityToModelMapper<Entity, Entity> entityToModelMapper;

    public GaeCrudAdapter(EntityInformation<T, ID> ei, EntityToModelMapper<Entity, Entity> entityToModelMapper) {
        this.entityInformation = ei;
        this.entityToModelMapper = entityToModelMapper;
    }

    protected Class<T> getEntityType() {
        return entityInformation.getJavaType();
    }

    protected Key getKey(ID id) {
        String kind = entityToModelMapper.getKind(entityInformation.getJavaType());
        if (id instanceof Number) {
            return KeyFactory.createKey(kind, Number.class.cast(id).longValue());
        } else if (id instanceof String) {
            return KeyFactory.createKey(kind, (String) id);
        } else {
            throw new IllegalArgumentException("Can only handle Number and String ids in GAE!");
        }
    }

    protected void setId(Key key, T entity) {
        Object id = (key.getName() != null ? key.getName() : key.getId());
        entityToModelMapper.setId(getEntityType(), entity, id);
    }

    @Override
    public <S extends T> S save(S entity) {
        Key key = datastoreService.put(entityToModelMapper.toEntity(getEntityType(), entity));
        setId(key, entity);
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        List<Entity> list = new ArrayList<>();
        List<S> models = new ArrayList<>();
        for (S entity : entities) {
            list.add(entityToModelMapper.toEntity(getEntityType(), entity));
            models.add(entity);
        }
        List<Key> keys = datastoreService.put(list);
        for (int i = 0; i < keys.size(); i++) {
            setId(keys.get(i), models.get(i));
        }
        return entities;
    }

    @Override
    public Optional<T> findById(ID id) {
        try {
            Entity entity = datastoreService.get(getKey(id));
            return Optional.of(entityToModelMapper.toModel(entityInformation.getJavaType(), entity));
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        List<Key> keys = new ArrayList<>();
        Map<Key, Entity> map = datastoreService.get(keys);
        return map.values().stream().map(v -> entityToModelMapper.toModel(getEntityType(), v)).collect(Collectors.toList());
    }

    @Override
    public void delete(T entity) {
        deleteById(entityInformation.getRequiredId(entity));
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        List<Key> keys = new ArrayList<>();
        for (T entity : entities) {
            keys.add(getKey(entityInformation.getRequiredId(entity)));
        }
        datastoreService.delete(keys);
    }

    @Override
    public void deleteById(ID id) {
        datastoreService.delete(getKey(id));
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Cannot deleteAll in GAE!");
    }
}
