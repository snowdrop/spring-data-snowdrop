package org.jboss.data.hibernatesearch.repository.support;

import java.io.Serializable;

import org.jboss.data.hibernatesearch.core.HibernateSearchOperations;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SimpleRepository<T, ID extends Serializable> extends AbstractHibernateSearchRepository<T, ID> {
  public SimpleRepository(HibernateSearchOperations hibernateSearchOperations, HibernateSearchEntityInformation<T, ID> entityInformation) {
    super(hibernateSearchOperations, entityInformation);
  }
}
