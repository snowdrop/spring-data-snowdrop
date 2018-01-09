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

package me.snowdrop.data.core.config.infinispan.embedded;

import java.util.List;

import me.snowdrop.data.core.TestsAction;
import me.snowdrop.data.core.config.Fruit;
import me.snowdrop.data.infinispan.embedded.EntityToCacheMapper;
import me.snowdrop.data.core.crud.CEntity;
import me.snowdrop.data.core.ops.OpsTestsActionBase;
import me.snowdrop.data.core.ops.SimpleEntity;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.Index;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
public class InfinispanConfiguration {

    @Bean(destroyMethod = "stop")
    public EmbeddedCacheManager createCacheManager() {
        GlobalConfigurationBuilder globalCfg = new GlobalConfigurationBuilder();
        globalCfg.globalJmxStatistics().allowDuplicateDomains(true).disable(); // get rid of this?

        ConfigurationBuilder cacheCfg = new ConfigurationBuilder();
        cacheCfg.jmxStatistics().disable();
        cacheCfg.indexing()
            .index(Index.ALL)
            .addIndexedEntity(Fruit.class)
            .addIndexedEntity(CEntity.class)
            .addIndexedEntity(SimpleEntity.class)
            .addProperty("default.directory_provider", "local-heap")
            .addProperty("lucene_version", "LUCENE_CURRENT");

        return new DefaultCacheManager(globalCfg.build(), cacheCfg.build());
    }

    @Bean
    public TestsAction testsAction(EntityToCacheMapper entityToCacheMapper) {
        return new InfinispanOpsTestsAction(entityToCacheMapper.getCache(SimpleEntity.class));
    }

    private class InfinispanOpsTestsAction extends OpsTestsActionBase {
        private Cache cache;

        public InfinispanOpsTestsAction(Cache cache) {
            this.cache = cache;
        }

        protected void setUp(List<SimpleEntity> entities) {
            for (SimpleEntity entity : entities) {
                //noinspection unchecked
                cache.put(entity.getId(), entity);
            }
        }

        public void tearDown() {
            cache.clear();
        }
    }
}
