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

package me.snowdrop.data.hibernatesearch.core.query;

import java.util.Set;

import org.hibernate.search.engine.metadata.impl.DocumentFieldMetadata;
import org.hibernate.search.engine.spi.EntityIndexBinding;
import org.hibernate.search.metadata.FieldDescriptor;
import org.hibernate.search.metadata.IndexedTypeDescriptor;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class EntityMetadataContext {
  private final EntityIndexBinding binding;
  private final IndexedTypeDescriptor indexedTypeDescriptor;

  EntityMetadataContext(EntityIndexBinding binding, IndexedTypeDescriptor indexedTypeDescriptor) {
    this.binding = binding;
    this.indexedTypeDescriptor = indexedTypeDescriptor;
  }

  private DocumentFieldMetadata getDocumentFieldMetadata(String fieldName) {
    return binding.getDocumentBuilder().getTypeMetadata().getDocumentFieldMetadataFor(fieldName);
  }

  private FieldDescriptor getFieldDescriptor(String property) {
    Set<FieldDescriptor> fieldsForProperty = indexedTypeDescriptor.getFieldsForProperty(property);
    if (fieldsForProperty.size() != 1) {
      // TODO - we could pass in some hint from Query in case of multiple fields, on which field to use
      throw new IllegalArgumentException("Invalid fields size: " + fieldsForProperty);
    }
    return fieldsForProperty.iterator().next();
  }

  String getFieldName(String property) {
    DocumentFieldMetadata dfmd = getDocumentFieldMetadata(property);
    if (dfmd != null) {
      return dfmd.getAbsoluteName();
    }

    FieldDescriptor fieldDescriptor = getFieldDescriptor(property);
    return fieldDescriptor.getName();
  }
}
