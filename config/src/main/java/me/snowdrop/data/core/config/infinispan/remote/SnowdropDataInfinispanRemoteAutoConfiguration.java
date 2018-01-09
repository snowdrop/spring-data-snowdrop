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

package me.snowdrop.data.core.config.infinispan.remote;

import me.snowdrop.data.core.spi.DatasourceMapper;
import me.snowdrop.data.infinispan.remote.EntityToRemoteCacheMapper;
import me.snowdrop.data.infinispan.remote.InfinispanRemoteDatasourceMapper;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
@ConditionalOnClass({
    Search.class,
    RemoteCache.class,
    RemoteCacheManager.class,
    EntityToRemoteCacheMapper.class,
    DatasourceMapper.class
})
@EnableConfigurationProperties(InfinispanRemoteProperties.class)
public class SnowdropDataInfinispanRemoteAutoConfiguration {

    private InfinispanRemoteProperties properties;

    public SnowdropDataInfinispanRemoteAutoConfiguration(InfinispanRemoteProperties properties) {
        this.properties = properties;
    }

    @Bean(destroyMethod = "stop")
    @ConditionalOnMissingBean(RemoteCacheManager.class)
    public RemoteCacheManager createRemoteCacheManager() {
        ConfigurationBuilder clientBuilder = new ConfigurationBuilder();
        clientBuilder.addServer()
            .host(properties.getHost())
            .port(properties.getPort())
            .marshaller(new ProtoStreamMarshaller());

        return new RemoteCacheManager(clientBuilder.build());
    }

    @Bean
    @ConditionalOnMissingBean(EntityToRemoteCacheMapper.class)
    @ConditionalOnBean(RemoteCacheManager.class)
    public EntityToRemoteCacheMapper createDefaultEntityToCacheMapper(final RemoteCacheManager cacheManager) {
        return new EntityToRemoteCacheMapper() {
            public <ID, T> RemoteCache<ID, T> getRemoteCache(Class<T> entityClass) {
                return cacheManager.getCache(); // always return default cache
            }
        };
    }

    @Bean(name = "datasourceMapper")
    @ConditionalOnMissingBean(DatasourceMapper.class)
    @ConditionalOnBean(EntityToRemoteCacheMapper.class)
    public DatasourceMapper createInfinispanDatasourceMapper(EntityToRemoteCacheMapper entityToCacheMapper) {
        return new InfinispanRemoteDatasourceMapper(entityToCacheMapper);
    }

}
