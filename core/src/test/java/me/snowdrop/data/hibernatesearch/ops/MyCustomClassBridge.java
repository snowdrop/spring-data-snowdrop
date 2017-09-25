/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package me.snowdrop.data.hibernatesearch.ops;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.MetadataProvidingFieldBridge;
import org.hibernate.search.bridge.spi.FieldMetadataBuilder;
import org.hibernate.search.bridge.spi.FieldType;

public class MyCustomClassBridge implements MetadataProvidingFieldBridge {
  @Override
  public void configureFieldMetadata(String name, FieldMetadataBuilder builder) {
    builder.field(name + ".custom", FieldType.OBJECT);
    builder.field(name + ".custom.name", FieldType.STRING);
  }

  @Override
  public void set(String fieldName, Object value, Document document, LuceneOptions luceneOptions) {
    SimpleEntity entity = (SimpleEntity) value;
    String name = entity.getName();
    luceneOptions.addFieldToDocument(fieldName + ".custom.name", name, document);
    luceneOptions.addFieldToDocument(fieldName + ".custom.dynamicName", name, document);
  }
}