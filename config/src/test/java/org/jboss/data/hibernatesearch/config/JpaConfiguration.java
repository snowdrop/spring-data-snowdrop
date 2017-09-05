package org.jboss.data.hibernatesearch.config;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
public class JpaConfiguration {
  @Bean
  public EntityManager createEntityManager() {
    return Persistence.createEntityManagerFactory("hs").createEntityManager();
  }
}
