package org.jboss.data.hibernatesearch.core.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SimpleHibernateSearchMappingContext<T> extends AbstractMappingContext<HibernateSearchPersistentEntity<?>, HibernateSearchPersistentProperty> {
  @Override
  protected <T> HibernateSearchPersistentEntity<?> createPersistentEntity(TypeInformation<T> typeInformation) {
    return new HibernateSearchPersistentEntity<>(typeInformation);
  }

  @Override
  protected HibernateSearchPersistentProperty createPersistentProperty(Field field, PropertyDescriptor descriptor, HibernateSearchPersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
    return new SimpleHibernateSearchPersistentProperty<>(field, descriptor, owner, simpleTypeHolder);
  }
}
