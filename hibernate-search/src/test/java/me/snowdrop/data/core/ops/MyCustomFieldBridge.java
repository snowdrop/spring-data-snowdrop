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
package me.snowdrop.data.core.ops;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.MetadataProvidingFieldBridge;
import org.hibernate.search.bridge.spi.FieldMetadataBuilder;
import org.hibernate.search.bridge.spi.FieldType;

public class MyCustomFieldBridge implements MetadataProvidingFieldBridge {
  @Override
  public void configureFieldMetadata(String name, FieldMetadataBuilder builder) {
    builder.field(name + ".custom", FieldType.OBJECT);
    builder.field(name + ".custom.name", FieldType.STRING);
  }

  @Override
  public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
    luceneOptions.addFieldToDocument(name + ".custom.name", (String) value, document);
    luceneOptions.addFieldToDocument(name + ".custom.dynamicName", (String) value, document);
  }
}
