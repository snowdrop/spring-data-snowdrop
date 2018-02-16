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

import java.util.HashMap;
import java.util.Map;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import me.snowdrop.data.gcp.AbstractEntityToModelMapper;

/**
 * Very simple / basic generic (field based) mapper.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GcdEntityToModelMapper extends AbstractEntityToModelMapper<Entity, FullEntity<IncompleteKey>, FullEntity.Builder<IncompleteKey>> {
    @Override
    protected Object getId(Entity entity) {
        Key key = entity.getKey();
        return (key.getName() != null ? key.getName() : key.getId());
    }

    @Override
    protected Map<String, Object> getProperties(Entity entity) {
        Map<String, Object> map = new HashMap<>();
        for (String name : entity.getNames()) {
            map.put(name, entity.getValue(name));
        }
        return map;
    }

    @Override
    protected FullEntity.Builder<IncompleteKey> createBuilder(String kind, Object id) {
        if (id == null) {
            return FullEntity.newBuilder(IncompleteKey.newBuilder(DatastoreUtils.fetchProjectId(), kind).build());
        } else {
            KeyFactory factory = DatastoreUtils.newKeyFactory().setKind(kind);
            Key key = (id instanceof Number) ? factory.newKey(Number.class.cast(id).longValue()) : factory.newKey((String) id);
            return FullEntity.newBuilder(key);
        }
    }

    @Override
    protected void setProperty(FullEntity.Builder<IncompleteKey> builder, String field, Object value) {
        builder.set(field, Values.toValue(value));
    }

    @Override
    protected FullEntity<IncompleteKey> toEntity(FullEntity.Builder<IncompleteKey> builder) {
        return builder.build();
    }
}
