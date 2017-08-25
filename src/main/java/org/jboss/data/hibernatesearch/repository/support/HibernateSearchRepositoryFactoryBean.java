package org.jboss.data.hibernatesearch.repository.support;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.core.mapping.HibernateSearchPersistentEntity;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends RepositoryFactoryBeanSupport<T, S, ID> {

  private SearchIntegrator searchIntegrator;

  /**
   * Creates a new {@link HibernateSearchRepositoryFactory} for the given repository interface.
   *
   * @param repositoryInterface must not be {@literal null}.
   */
  public HibernateSearchRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
    super(repositoryInterface);
  }

  /**
   * Configures the {@link SearchIntegrator} to be used to create Hibernatesearch repositories.
   *
   * @param searchIntegrator the search integrator to set
   */
  public void setSearchIntegrator(SearchIntegrator searchIntegrator) {
    // setMappingContext(null); // TODO
    this.searchIntegrator = searchIntegrator;
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() {
    Assert.notNull(searchIntegrator, "SearchIntegrator must be configured!");
    super.afterPropertiesSet();
  }

  @Override
  protected RepositoryFactorySupport createRepositoryFactory() {
    return new HibernateSearchRepositoryFactory(searchIntegrator);
  }
}
