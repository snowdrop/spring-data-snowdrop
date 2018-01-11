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

package me.snowdrop.data.gcp;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.data.util.ReflectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractEntityToModelMapper<E, U, B> implements EntityToModelMapper<E, U> {

    private static final Map<Class<?>, Field> idFieldCache = new ConcurrentReferenceHashMap<>(256);

    protected static Field getIdField(Class<?> entityClass) {
        return idFieldCache.computeIfAbsent(entityClass, aClass -> {
            Field field = ReflectionUtils.findField(aClass, Filters.ID_FF);
            if (field == null) {
                throw new IllegalArgumentException(String.format("Entity class %s is missing @Id field!", aClass.getName()));
            }
            field.setAccessible(true);
            return field;
        });
    }

    public <T> void setId(Class<T> entityClass, T model, Object id) {
        try {
            Field idField = getIdField(entityClass);
            ReflectionUtils.setField(idField, model, id);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    protected abstract Object getId(E entity);

    protected abstract Map<String, Object> getProperties(E entity);

    public <T> T toModel(Class<T> entityClass, E entity) {
        try {
            T instance = entityClass.newInstance();

            Field idField = getIdField(entityClass);
            Object id = getId(entity);
            idField.set(instance, id);

            Map<String, Object> properties = getProperties(entity);
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                Field field = ReflectionUtils.findRequiredField(entityClass, entry.getKey());
                ReflectionUtils.setField(field, instance, entry.getValue());
            }

            return instance;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    protected abstract B createBuilder(String kind, Object id);

    protected abstract void setProperty(B builder, String field, Object value);

    protected abstract U toEntity(B builder);

    @Override
    public <T> U toEntity(Class<T> entityClass, T model) {
        try {
            String kind = getKind(entityClass);
            Field idField = getIdField(entityClass);
            Object id = idField.get(model);

            B builder = createBuilder(kind, id);

            org.springframework.util.ReflectionUtils.doWithFields(
                entityClass,
                field -> {
                    org.springframework.util.ReflectionUtils.makeAccessible(field);
                    setProperty(builder, field.getName(), org.springframework.util.ReflectionUtils.getField(field, model));
                },
                Filters.TRANSIENT_FF
            );

            return toEntity(builder);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getKind(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }
}
