package org.jboss.data.hibernatesearch.repository.config;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.data.hibernatesearch.repository.support.HibernateSearchRepositoryFactoryBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.query.QueryLookupStrategy;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

  @Override
  protected Class<? extends Annotation> getAnnotation() {
    return EnableHibernateSearchRepositories.class;
  }

  @Override
  protected RepositoryConfigurationExtension getExtension() {
    return new HibernateSearchRepositoryConfigExtension();
  }
}
