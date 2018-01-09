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
package me.snowdrop.data.core.config.infinispan.remote.crud;

import me.snowdrop.data.core.config.infinispan.embedded.SnowdropDataInfinispanEmbeddedAutoConfiguration;
import me.snowdrop.data.core.config.infinispan.remote.InfinispanRemoteConfiguration;
import me.snowdrop.data.core.config.jpa.SnowdropDataJpaAutoConfiguration;
import me.snowdrop.data.core.crud.SimpleCrudRepository;
import me.snowdrop.data.core.repository.config.EnableSnowdropRepositories;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(InfinispanRemoteConfiguration.class)
@EnableAutoConfiguration(exclude = {
        SnowdropDataInfinispanEmbeddedAutoConfiguration.class,
        SnowdropDataJpaAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
@EnableSnowdropRepositories(basePackageClasses = SimpleCrudRepository.class)
public class InfinispanRemoteCrudConfiguration {
}
