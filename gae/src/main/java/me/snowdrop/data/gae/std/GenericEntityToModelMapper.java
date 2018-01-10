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

package me.snowdrop.data.gae.std;

import java.lang.reflect.Field;
import java.util.Map;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

/**
 * Very simple / basic generic (field based) mapper.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GenericEntityToModelMapper implements EntityToModelMapper {

    private static final Map<Class<?>, Field> idFieldCache = new ConcurrentReferenceHashMap<>(256);

    private static Field getIdField(Class<?> entityClass) {
        return idFieldCache.computeIfAbsent(entityClass, aClass -> {
            Field field = ReflectionUtils.findField(aClass, IdFieldFilter.ID_FF);
            if (field == null) {
                throw new IllegalArgumentException(String.format("Entity class %s is missing @Id field!", aClass.getName()));
            }
            field.setAccessible(true);
            return field;
        });
    }

    @Override
    public <T> T toModel(Class<T> entityClass, Entity entity) {
        try {
            T instance = entityClass.newInstance();

            Field idField = getIdField(entityClass);
            Key key = entity.getKey();
            Object id = (key.getName() != null ? key.getName() : key.getId());
            idField.set(instance, id);

            Map<String, Object> properties = entity.getProperties();
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                Field field = ReflectionUtils.findRequiredField(entityClass, entry.getKey());
                ReflectionUtils.setField(field, instance, entry.getValue());
            }

            return instance;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> Entity toEntity(Class<T> entityClass, T model) {
        try {
            String kind = getKind(entityClass);
            Field idField = getIdField(entityClass);
            Object id = idField.get(model);

            Entity entity;
            if (id == null) {
                entity = new Entity(kind);
            } else {
                entity = (id instanceof Number) ?
                    new Entity(kind, Number.class.cast(id).longValue()) :
                    new Entity(kind, (String) id);
            }

            org.springframework.util.ReflectionUtils.doWithFields(
                entityClass,
                field -> {
                    org.springframework.util.ReflectionUtils.makeAccessible(field);
                    entity.setProperty(field.getName(), org.springframework.util.ReflectionUtils.getField(field, model));
                },
                TransientFieldFilter.TRANSIENT_FF
            );

            return entity;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getKind(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }

    private static class IdFieldFilter implements org.springframework.util.ReflectionUtils.FieldFilter {
        static final IdFieldFilter ID_FF = new IdFieldFilter();

        @Override
        public boolean matches(Field field) {
            return field.getAnnotation(Id.class) == null;
        }
    }

    private static class TransientFieldFilter extends IdFieldFilter {
        static final TransientFieldFilter TRANSIENT_FF = new TransientFieldFilter();

        @Override
        public boolean matches(Field field) {
            return super.matches(field) && field.getAnnotation(Transient.class) == null;
        }
    }
}
