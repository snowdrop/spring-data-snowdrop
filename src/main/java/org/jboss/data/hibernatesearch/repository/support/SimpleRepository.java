package org.jboss.data.hibernatesearch.repository.support;

import java.io.Serializable;

import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.spi.DatasourceMapper;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SimpleRepository<T, ID extends Serializable> extends AbstractHibernateSearchRepository<T, ID> {
  public SimpleRepository(SearchIntegrator searchIntegrator, DatasourceMapper datasourceMapper, HibernateSearchEntityInformation<T, ID> entityInformation) {
    super(searchIntegrator, datasourceMapper, entityInformation);
  }
}
