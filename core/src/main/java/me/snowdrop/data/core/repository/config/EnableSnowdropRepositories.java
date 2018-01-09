/*
 * Copyright 2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.snowdrop.data.core.repository.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.snowdrop.data.core.repository.support.SnowdropRepositoryFactoryBean;
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
@Import(SnowdropRepositoriesRegistrar.class)
public @interface EnableSnowdropRepositories {

  String[] value() default {};

  String[] basePackages() default {};

  Class<?>[] basePackageClasses() default {};

  ComponentScan.Filter[] includeFilters() default {};

  ComponentScan.Filter[] excludeFilters() default {};

  String repositoryImplementationPostfix() default "Impl";

  String namedQueriesLocation() default "";

  QueryLookupStrategy.Key queryLookupStrategy() default QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

  Class<?> repositoryFactoryBeanClass() default SnowdropRepositoryFactoryBean.class;

  Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

  /**
   * Configures the name of the {@link DatasourceMapper} bean definition to be used to create repositories
   * discovered through this annotation. Defaults to {@code datasourceMapper}.
   */
  String datasourceMapperRef() default "datasourceMapper";

}
