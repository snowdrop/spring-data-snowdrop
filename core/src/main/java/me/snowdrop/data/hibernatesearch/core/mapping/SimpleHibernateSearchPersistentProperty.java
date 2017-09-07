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

package me.snowdrop.data.hibernatesearch.core.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.SimpleTypeHolder;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SimpleHibernateSearchPersistentProperty<T> extends AnnotationBasedPersistentProperty<HibernateSearchPersistentProperty>
  implements HibernateSearchPersistentProperty {

  public SimpleHibernateSearchPersistentProperty(Field field, PropertyDescriptor propertyDescriptor, PersistentEntity<?, HibernateSearchPersistentProperty> owner, SimpleTypeHolder simpleTypeHolder) {
    super(field, propertyDescriptor, owner, simpleTypeHolder);
  }

  @Override
  protected Association<HibernateSearchPersistentProperty> createAssociation() {
    return null;
  }
}