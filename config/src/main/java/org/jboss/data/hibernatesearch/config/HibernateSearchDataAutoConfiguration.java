package org.jboss.data.hibernatesearch.config;

import java.io.Closeable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
import org.springframework.orm.jpa.SharedEntityManagerCreator;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
@ConditionalOnClass(SearchIntegrator.class)
@AutoConfigureAfter({HibernateJpaAutoConfiguration.class})
public class HibernateSearchDataAutoConfiguration {

  private EntityManager entityManager;

  private synchronized EntityManager getEntityManager(EntityManagerFactory emf) {
    if (entityManager == null) {
      entityManager = SharedEntityManagerCreator.createSharedEntityManager(emf);
    }
    return entityManager;
  }

  @Bean(destroyMethod = "close")
  public Closeable entityManagerCloser() {
    return () -> {
      if (entityManager != null) {
        entityManager.close();
      }
    };
  }

  @Bean(destroyMethod = "close", name = "searchIntegrator")
  @ConditionalOnBean(EntityManagerFactory.class)
  public SearchIntegrator createSearchIntegrator(EntityManagerFactory emf) {
    return Search.getFullTextEntityManager(getEntityManager(emf)).getSearchFactory().unwrap(SearchIntegrator.class);
  }

  @Bean(name = "datasourceMapper")
  @ConditionalOnMissingBean(DatasourceMapper.class)
  @ConditionalOnBean(EntityManagerFactory.class)
  public DatasourceMapper createDatasourceMapper(EntityManagerFactory emf) {
    return new JpaDatasourceMapper(getEntityManager(emf));
  }

}