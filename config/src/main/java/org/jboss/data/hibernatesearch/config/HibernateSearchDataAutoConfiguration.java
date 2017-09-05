package org.jboss.data.hibernatesearch.config;

import javax.persistence.EntityManager;

import org.hibernate.search.jpa.Search;
import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.spi.DatasourceMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
@ConditionalOnClass(SearchIntegrator.class)
@AutoConfigureAfter({HibernateJpaAutoConfiguration.class})
public class HibernateSearchDataAutoConfiguration {

  @Bean(destroyMethod = "close", name = "searchIntegrator")
  @ConditionalOnBean(EntityManager.class)
  public SearchIntegrator createSearchIntegrator(EntityManager entityManager) {
    return Search.getFullTextEntityManager(entityManager).getSearchFactory().unwrap(SearchIntegrator.class);
  }

  @Bean(name = "datasourceMapper")
  @ConditionalOnMissingBean(DatasourceMapper.class)
  @ConditionalOnBean(EntityManager.class)
  public DatasourceMapper createDatasourceMapper(EntityManager em) {
    return new JpaDatasourceMapper(em);
  }

}