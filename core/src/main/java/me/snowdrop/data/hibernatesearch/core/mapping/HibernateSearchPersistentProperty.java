package me.snowdrop.data.hibernatesearch.core.mapping;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mapping.PersistentProperty;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface HibernateSearchPersistentProperty extends PersistentProperty<HibernateSearchPersistentProperty> {
  enum PropertyToFieldNameConverter implements Converter<HibernateSearchPersistentProperty, String> {
    INSTANCE;

    public String convert(HibernateSearchPersistentProperty source) {
      return source.getName();
    }
  }
}