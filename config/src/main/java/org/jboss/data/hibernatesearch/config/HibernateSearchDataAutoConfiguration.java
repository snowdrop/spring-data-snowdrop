package org.jboss.data.hibernatesearch.config;

import javax.persistence.EntityManager;

import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.spi.DatasourceMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
@ConditionalOnClass(SearchIntegrator.class)
public class HibernateSearchDataAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(DatasourceMapper.class)
  @ConditionalOnBean(EntityManager.class)
  public DatasourceMapper createJpaDatasourceMapper(EntityManager em) {
    return new JpaDatasourceMapper(em);
  }
}