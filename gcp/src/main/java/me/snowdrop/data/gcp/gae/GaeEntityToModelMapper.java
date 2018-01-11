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

import java.util.Map;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import me.snowdrop.data.gcp.AbstractEntityToModelMapper;

/**
 * Very simple / basic generic (field based) mapper.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GaeEntityToModelMapper extends AbstractEntityToModelMapper<Entity, Entity, Entity> {

    @Override
    protected Object getId(Entity entity) {
        Key key = entity.getKey();
        return (key.getName() != null ? key.getName() : key.getId());
    }

    @Override
    protected Map<String, Object> getProperties(Entity entity) {
        return entity.getProperties();
    }

    @Override
    protected Entity createBuilder(String kind, Object id) {
        if (id == null) {
            return new Entity(kind);
        } else {
            return (id instanceof Number) ?
                new Entity(kind, Number.class.cast(id).longValue()) :
                new Entity(kind, (String) id);
        }
    }

    @Override
    protected void setProperty(Entity entity, String field, Object value) {
        entity.setProperty(field, value);
    }

    @Override
    protected Entity toEntity(Entity builder) {
        return builder;
    }
}
