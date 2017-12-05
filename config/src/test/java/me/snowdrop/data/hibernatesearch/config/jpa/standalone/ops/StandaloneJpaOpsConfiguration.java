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
package me.snowdrop.data.hibernatesearch.config.jpa.standalone.ops;

import me.snowdrop.data.hibernatesearch.TestedRepository;
import me.snowdrop.data.hibernatesearch.config.infinispan.embedded.HibernateSearchDataInfinispanEmbeddedAutoConfiguration;
import me.snowdrop.data.hibernatesearch.config.infinispan.remote.HibernateSearchDataInfinispanRemoteAutoConfiguration;
import me.snowdrop.data.hibernatesearch.config.jpa.JpaConfiguration;
import me.snowdrop.data.hibernatesearch.config.jpa.standalone.ops.repository.hibernatesearch.StandaloneJpaHibernateSearchOpsRepository;
import me.snowdrop.data.hibernatesearch.config.jpa.standalone.ops.repository.jpa.StandaloneJpaOpsRepository;
import me.snowdrop.data.hibernatesearch.ops.Ops;
import me.snowdrop.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Import(JpaConfiguration.class)
@EnableAutoConfiguration(exclude = {
    HibernateSearchDataInfinispanEmbeddedAutoConfiguration.class,
    HibernateSearchDataInfinispanRemoteAutoConfiguration.class
})
@EnableJpaRepositories(basePackageClasses = StandaloneJpaOpsRepository.class)
@EnableHibernateSearchRepositories(basePackageClasses = StandaloneJpaHibernateSearchOpsRepository.class)
public class StandaloneJpaOpsConfiguration {

  @Bean
  public TestedRepository<Ops> testedRepository(StandaloneJpaHibernateSearchOpsRepository repository) {
    return new TestedRepository<>(repository);
  }

}
