package me.snowdrop.data.hibernatesearch.core.mapping;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchPersistentEntity<T> extends BasicPersistentEntity<T, HibernateSearchPersistentProperty>
  implements PersistentEntity<T, HibernateSearchPersistentProperty> {

  public HibernateSearchPersistentEntity(TypeInformation<T> information) {
    super(information);
  }
}