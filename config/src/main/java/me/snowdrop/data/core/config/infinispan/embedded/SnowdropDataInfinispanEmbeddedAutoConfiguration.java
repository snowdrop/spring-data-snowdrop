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

import me.snowdrop.data.core.spi.DatasourceMapper;
import me.snowdrop.data.infinispan.embedded.EntityToCacheMapper;
import me.snowdrop.data.infinispan.embedded.InfinispanEmbeddedDatasourceMapper;
import org.hibernate.search.spi.SearchIntegrator;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.SearchManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
@ConditionalOnClass({
    SearchManager.class,
    CacheQuery.class,
    SearchIntegrator.class,
    EntityToCacheMapper.class,
    DatasourceMapper.class
})
@AutoConfigureAfter({CacheAutoConfiguration.class})
public class SnowdropDataInfinispanEmbeddedAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EntityToCacheMapper.class)
    @ConditionalOnBean(EmbeddedCacheManager.class)
    public EntityToCacheMapper createDefaultEntityToCacheMapper(final EmbeddedCacheManager cacheManager) {
        return new EntityToCacheMapper() {
            public <ID, T> Cache<ID, T> getCache(Class<T> entityClass) {
                return cacheManager.getCache(); // always return default cache
            }
        };
    }

    @Bean(name = "datasourceMapper")
    @ConditionalOnMissingBean(DatasourceMapper.class)
    @ConditionalOnBean(EntityToCacheMapper.class)
    public DatasourceMapper createInfinispanDatasourceMapper(EntityToCacheMapper entityToCacheMapper) {
        return new InfinispanEmbeddedDatasourceMapper(entityToCacheMapper);
    }

}
