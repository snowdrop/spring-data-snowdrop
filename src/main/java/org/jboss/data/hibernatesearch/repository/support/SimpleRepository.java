package org.jboss.data.hibernatesearch.repository.support;

import java.io.Serializable;

import org.hibernate.search.spi.SearchIntegrator;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SimpleRepository<T, ID extends Serializable> extends AbstractHibernateSearchRepository<T, ID> {
  public SimpleRepository(SearchIntegrator searchIntegrator, HibernateSearchEntityInformation<T, ID> entityInformation) {
    super(searchIntegrator, entityInformation);
  }

  @Override
  protected String idAsString(ID id) {
    return id != null ? String.valueOf(id) : null;
  }
}
