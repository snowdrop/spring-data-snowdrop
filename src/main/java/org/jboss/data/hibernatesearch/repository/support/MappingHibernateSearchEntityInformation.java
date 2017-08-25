package org.jboss.data.hibernatesearch.repository.support;

import java.io.Serializable;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.support.PersistentEntityInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class MappingHibernateSearchEntityInformation<T, ID extends Serializable> extends PersistentEntityInformation<T, ID> implements HibernateSearchEntityInformation<T, ID> {
  public MappingHibernateSearchEntityInformation(PersistentEntity<T, ?> entity) {
    super(entity);
  }
}
