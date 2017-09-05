package org.jboss.data.hibernatesearch.repository.config;

import org.jboss.data.hibernatesearch.repository.support.HibernateSearchRepositoryFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {
  @Override
  protected String getModulePrefix() {
    return "hibernatesearch";
  }

  @Override
  public String getRepositoryFactoryClassName() {
    return HibernateSearchRepositoryFactoryBean.class.getName();
  }

  @Override
  public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
    AnnotationAttributes attributes = config.getAttributes();
    builder.addPropertyReference("searchIntegrator", attributes.getString("searchIntegratorRef"));
    builder.addPropertyReference("datasourceMapper", attributes.getString("datasourceMapperRef"));
  }
}
