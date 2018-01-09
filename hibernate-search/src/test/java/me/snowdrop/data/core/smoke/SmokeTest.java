/*
 * Copyright 2018 Red Hat, Inc, and individual contributors.
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

package me.snowdrop.data.core.smoke;

import me.snowdrop.data.core.DatasourceMapperTester;
import me.snowdrop.data.core.TestUtils;
import me.snowdrop.data.core.repository.config.EnableSnowdropRepositories;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class SmokeTest extends SpringSmokeTestBase {

    @Configuration
    @EnableSnowdropRepositories
    @EnableAsync
    public static class Config {
        @Bean(destroyMethod = "close")
        public DatasourceMapperTester datasourceMapper() {
            return TestUtils.createDatasourceMapper(SmokeEntity.class);
        }
    }
}
