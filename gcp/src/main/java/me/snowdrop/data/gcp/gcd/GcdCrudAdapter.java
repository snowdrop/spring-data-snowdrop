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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.cloud.datastore.DatastoreReaderWriter;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import me.snowdrop.data.core.spi.CrudAdapter;
import me.snowdrop.data.gcp.EntityToModelMapper;
import org.springframework.data.repository.core.EntityInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GcdCrudAdapter<T, ID> implements CrudAdapter<T, ID> {
    private final EntityInformation<T, ID> entityInformation;
    private final EntityToModelMapper<Entity, FullEntity<IncompleteKey>> entityToModelMapper;

    public GcdCrudAdapter(EntityInformation<T, ID> ei, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> entityToModelMapper) {
        this.entityInformation = ei;
        this.entityToModelMapper = entityToModelMapper;
    }

    protected DatastoreReaderWriter getDRW() {
        return DatastoreUtils.getDatastoreReaderWriter();
    }

    protected Class<T> getEntityType() {
        return entityInformation.getJavaType();
    }

    protected Key getKey(ID id) {
        String kind = entityToModelMapper.getKind(entityInformation.getJavaType());
        KeyFactory factory = DatastoreUtils.newKeyFactory().setKind(kind);
        if (id instanceof Number) {
            return factory.newKey(Number.class.cast(id).longValue());
        } else if (id instanceof String) {
            return factory.newKey((String) id);
        } else {
            throw new IllegalArgumentException("Can only handle Number and String ids in GAE!");
        }
    }

    protected void setId(Key key, T model) {
        Object id = key.getNameOrId();
        entityToModelMapper.setId(getEntityType(), model, id);
    }

    @Override
    public <S extends T> S save(S entity) {
        Entity e = getDRW().put(entityToModelMapper.toEntity(getEntityType(), entity));
        setId(e.getKey(), entity);
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        List<FullEntity> list = new ArrayList<>();
        List<S> models = new ArrayList<>();
        for (S entity : entities) {
            list.add(entityToModelMapper.toEntity(getEntityType(), entity));
            models.add(entity);
        }
        List<Entity> es = getDRW().put(list.toArray(new FullEntity[list.size()]));
        for (int i = 0; i < es.size(); i++) {
            setId(es.get(i).getKey(), models.get(i));
        }
        return entities;
    }

    @Override
    public Optional<T> findById(ID id) {
        Entity entity = getDRW().get(getKey(id));
        return Optional.ofNullable(entityToModelMapper.toModel(entityInformation.getJavaType(), entity));
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        List<Key> keys = new ArrayList<>();
        for (ID id : ids) {
            keys.add(getKey(id));
        }
        List<Entity> results = getDRW().fetch(keys.toArray(new Key[keys.size()]));
        return results.stream().map(v -> entityToModelMapper.toModel(getEntityType(), v)).collect(Collectors.toList());
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
        getDRW().delete(keys.toArray(new Key[keys.size()]));
    }

    @Override
    public void deleteById(ID id) {
        getDRW().delete(getKey(id));
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Cannot deleteAll in GCD!");
    }
}
