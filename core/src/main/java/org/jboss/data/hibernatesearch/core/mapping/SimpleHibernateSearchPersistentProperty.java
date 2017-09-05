package org.jboss.data.hibernatesearch.core.mapping;

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