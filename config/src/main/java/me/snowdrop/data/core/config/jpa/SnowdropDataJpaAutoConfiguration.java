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

package me.snowdrop.data.core.config.jpa;

import javax.persistence.EntityManagerFactory;

import me.snowdrop.data.core.orm.JpaDatasourceMapper;
import me.snowdrop.data.core.spi.DatasourceMapper;
import org.hibernate.search.Search;
import org.hibernate.search.spi.SearchIntegrator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
@ConditionalOnClass({
  EntityManagerFactory.class,
  SearchIntegrator.class,
  DatasourceMapper.class,
  Search.class,
  EntityManagerFactoryUtils.class
})
@AutoConfigureAfter({HibernateJpaAutoConfiguration.class})
public class SnowdropDataJpaAutoConfiguration {

  @Bean(name = "datasourceMapper")
  @ConditionalOnMissingBean(DatasourceMapper.class)
  @ConditionalOnBean(EntityManagerFactory.class)
  public DatasourceMapper createJpaDatasourceMapper(EntityManagerFactory emf) {
    return new JpaDatasourceMapper(emf);
  }

}