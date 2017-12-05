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

package me.snowdrop.data.hibernatesearch.repository.query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import me.snowdrop.data.hibernatesearch.annotations.TargetField;
import me.snowdrop.data.hibernatesearch.annotations.TargetFields;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class TargetFieldsUtils {
    public static Map<String, String> getTargetFieldsMap(HibernateSearchQueryMethod queryMethod) {
        return getTargetFieldsMap(queryMethod.getMethod());
    }

    public static Map<String, String> getTargetFieldsMap(Method method) {
        TargetFields targetFields = getTargetFields(method);
        if (targetFields == null) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        for (TargetField targetField : targetFields.value()) {
            map.put(targetField.property(), targetField.field());
        }
        return Collections.unmodifiableMap(map);
    }

    public static TargetFields getTargetFields(Method method) {
        TargetFields targetFields = method.getAnnotation(TargetFields.class);
        if (targetFields == null) {
            TargetField targetField = method.getAnnotation(TargetField.class);
            if (targetField != null) {
                targetFields = new TargetFieldsImpl(targetField);
            }
        }
        return targetFields;
    }

    private static class TargetFieldsImpl implements TargetFields {
        private TargetField targetField;

        public TargetFieldsImpl(TargetField targetField) {
            this.targetField = targetField;
        }

        @Override
        public TargetField[] value() {
            return new TargetField[]{targetField};
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return TargetFields.class;
        }
    }

}
