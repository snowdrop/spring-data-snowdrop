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
package me.snowdrop.data.core.orm.repository.config;

import me.snowdrop.data.core.repository.config.SnowdropRepositoryConfigExtension;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.NamedQueriesBeanDefinitionBuilder;
import org.springframework.util.StringUtils;

public class JpaWithSnowdropRegistrar implements ImportBeanDefinitionRegistrar {

  private static final String NAMED_QUERIES_LOCATION = "namedQueriesLocation";
  private static final String QUERY_LOOKUP_STRATEGY = "queryLookupStrategy";

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importMetadata, BeanDefinitionRegistry registry) {
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(
            importMetadata.getAnnotationAttributes(JpaWithSnowdropConfiguration.class.getName(), false));
    if (attributes == null) {
      throw new IllegalArgumentException(
              "@JpaWithSnowdropConfiguration is not present on importing class " + importMetadata.getClassName());
    }
    registry.registerBeanDefinition(
            JpaRepositoryFactoryBeanSnowdropPostProcessor.class.getName(),
            buildPostProcessorDefinition(importMetadata, attributes)
    );
  }

  private BeanDefinition buildPostProcessorDefinition(AnnotationMetadata importMetadata, AnnotationAttributes attributes) {
    SnowdropRepositoryConfigExtension extension = new SnowdropRepositoryConfigExtension();
    BeanDefinitionBuilder builder = BeanDefinitionBuilder
            .rootBeanDefinition(JpaRepositoryFactoryBeanSnowdropPostProcessor.class);

    builder.getRawBeanDefinition().setSource(importMetadata);

    builder.addPropertyValue("queryLookupStrategyKey", attributes.get(QUERY_LOOKUP_STRATEGY));

    NamedQueriesBeanDefinitionBuilder definitionBuilder = new NamedQueriesBeanDefinitionBuilder(
            extension.getDefaultNamedQueryLocation());

    String namedQueriesLocation = attributes.getString(NAMED_QUERIES_LOCATION);
    if (StringUtils.hasText(namedQueriesLocation)) {
      definitionBuilder.setLocations(namedQueriesLocation);
    }
    builder.addPropertyValue("namedQueriesLocation", definitionBuilder.build(importMetadata));

    return builder.getBeanDefinition();
  }
}
