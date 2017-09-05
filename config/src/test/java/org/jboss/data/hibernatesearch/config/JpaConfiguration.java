package org.jboss.data.hibernatesearch.config;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.search.jpa.Search;
import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
@EnableHibernateSearchRepositories
public class JpaConfiguration {
  @PersistenceContext
  EntityManager entityManager;

  @Bean(destroyMethod = "close")
  public SearchIntegrator searchIntegrator() {
    return Search.getFullTextEntityManager(entityManager).getSearchFactory().unwrap(SearchIntegrator.class);
  }
}
