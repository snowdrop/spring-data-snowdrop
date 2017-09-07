package me.snowdrop.data.hibernatesearch.repository.config;

import java.lang.annotation.Annotation;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

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
