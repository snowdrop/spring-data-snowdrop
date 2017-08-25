package org.jboss.data.hibernatesearch.repository.config;

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
import org.springframework.data.repository.query.QueryLookupStrategy;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(HibernateSearchRepositoriesRegistrar.class)
public @interface EnableHibernateSearchRepositories {

  String[] value() default {};

  String[] basePackages() default {};

  Class<?>[] basePackageClasses() default {};

  ComponentScan.Filter[] includeFilters() default {};

  ComponentScan.Filter[] excludeFilters() default {};

  String repositoryImplementationPostfix() default "Impl";

  String namedQueriesLocation() default "";

  QueryLookupStrategy.Key queryLookupStrategy() default QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

  Class<?> repositoryFactoryBeanClass() default HibernateSearchRepositoryFactoryBean.class;

  Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

  /**
   * Configures the name of the {@link SearchIntegrator} bean definition to be used to create repositories
   * discovered through this annotation. Defaults to {@code searchIntegrator}.
   *
   * @return
   */
  String searchIntegratorRef() default "searchIntegrator";
}
