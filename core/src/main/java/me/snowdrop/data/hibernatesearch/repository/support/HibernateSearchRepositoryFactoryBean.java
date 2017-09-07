package me.snowdrop.data.hibernatesearch.repository.support;

import java.io.Serializable;

import me.snowdrop.data.hibernatesearch.core.HibernateSearchOperations;
import me.snowdrop.data.hibernatesearch.core.HibernateSearchTemplate;
import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import org.hibernate.search.spi.SearchIntegrator;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends RepositoryFactoryBeanSupport<T, S, ID> {

  private SearchIntegrator searchIntegrator;
  private DatasourceMapper datasourceMapper;
  private HibernateSearchOperations hibernateSearchOperations;

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
    this.searchIntegrator = searchIntegrator;
  }

  public void setDatasourceMapper(DatasourceMapper datasourceMapper) {
    this.datasourceMapper = datasourceMapper;
  }

  public void setHibernateSearchOperations(HibernateSearchOperations hibernateSearchOperations) {
    this.hibernateSearchOperations = hibernateSearchOperations;
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() {
    if (hibernateSearchOperations == null) {
      Assert.notNull(searchIntegrator, "SearchIntegrator must be configured!");
      Assert.notNull(datasourceMapper, "DatasourceMapper must be configured!");

      hibernateSearchOperations = new HibernateSearchTemplate(searchIntegrator, datasourceMapper);
    }

    setMappingContext(hibernateSearchOperations.getMappingContext());

    super.afterPropertiesSet();
  }

  @Override
  protected RepositoryFactorySupport createRepositoryFactory() {
    return new HibernateSearchRepositoryFactory(hibernateSearchOperations);
  }
}
