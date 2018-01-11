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

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public final class Filters {
    public static final org.springframework.util.ReflectionUtils.FieldFilter ID_FF = new IdFieldFilter();
    public static final org.springframework.util.ReflectionUtils.FieldFilter TRANSIENT_FF = new TransientFieldFilter();

    private static class IdFieldFilter implements org.springframework.util.ReflectionUtils.FieldFilter {
        @Override
        public boolean matches(Field field) {
            return field.getAnnotation(Id.class) != null;
        }
    }

    private static class TransientFieldFilter extends IdFieldFilter {
        @Override
        public boolean matches(Field field) {
            return !super.matches(field) && field.getAnnotation(Transient.class) == null;
        }
    }
}
