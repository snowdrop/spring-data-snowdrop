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

package me.snowdrop.data.core.query.lucene;

import java.util.Map;
import java.util.Set;

import org.hibernate.search.engine.metadata.impl.DocumentFieldMetadata;
import org.hibernate.search.engine.metadata.impl.PropertyMetadata;
import org.hibernate.search.engine.metadata.impl.TypeMetadata;
import org.hibernate.search.engine.spi.EntityIndexBinding;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class EntityMetadataContext {
    private final EntityIndexBinding binding;
    private final Map<String, String> targetFields;

    EntityMetadataContext(EntityIndexBinding binding, Map<String, String> targetFields) {
        this.binding = binding;
        this.targetFields = targetFields;
    }

    String getFieldName(String property) {
        TypeMetadata typeMetadata = binding.getDocumentBuilder().getTypeMetadata();
        // try real property
        PropertyMetadata propertyMetadataForProperty = typeMetadata.getPropertyMetadataForProperty(property);
        if (propertyMetadataForProperty != null) {
            Set<DocumentFieldMetadata> fieldsForProperty = propertyMetadataForProperty.getFieldMetadataSet();
            switch (fieldsForProperty.size()) {
                case 0:
                    throw new IllegalArgumentException("No fields found for property: " + property);
                case 1:
                    return fieldsForProperty.iterator().next().getAbsoluteName();
                default:
                    String field = targetFields.getOrDefault(property, property);
                    for (DocumentFieldMetadata fd : fieldsForProperty) {
                        if (field.equals(fd.getAbsoluteName())) {
                            return field;
                        }
                    }
                    throw new IllegalArgumentException(String.format("No such field '%s' found on property: %s", field, property));
            }
        }
        // handle nested / inner
        // first check for hint
        String hint = targetFields.getOrDefault(property, property);
        DocumentFieldMetadata field = typeMetadata.getDocumentFieldMetadataFor(hint);
        if (field != null) {
            return field.getAbsoluteName();
        }
        throw new IllegalArgumentException("No field found for property: " + property);
    }
}
