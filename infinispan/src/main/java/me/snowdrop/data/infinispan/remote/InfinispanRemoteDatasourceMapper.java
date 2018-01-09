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

package me.snowdrop.data.infinispan.remote;

import me.snowdrop.data.core.spi.CrudAdapter;
import me.snowdrop.data.core.spi.DatasourceMapper;
import me.snowdrop.data.core.spi.QueryAdapter;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.query.dsl.QueryFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class InfinispanRemoteDatasourceMapper implements DatasourceMapper {

    private final EntityToRemoteCacheMapper entityToCacheMapper;

    public InfinispanRemoteDatasourceMapper(EntityToRemoteCacheMapper entityToCacheMapper) {
        Assert.notNull(entityToCacheMapper, "Null EntityToCacheMapper!");
        this.entityToCacheMapper = entityToCacheMapper;
    }

    private <T, ID> RemoteCache<ID, T> getCache(Class<T> entityClass) {
        RemoteCache<ID, T> cache = entityToCacheMapper.getRemoteCache(entityClass);
        if (cache == null) {
            throw new IllegalArgumentException("No remote cache mapped to entity class: " + entityClass);
        }
        return cache;
    }

    public <T> QueryAdapter<T> createQueryAdapter(Class<T> entityClass) {
        RemoteCache<?, T> cache = getCache(entityClass);
        QueryFactory qf = org.infinispan.client.hotrod.Search.getQueryFactory(cache);
        return new InfinispanRemoteQueryAdapter<>(qf);
    }

    public <T, ID> CrudAdapter<T, ID> createCrudAdapter(EntityInformation<T, ID> ei) {
        RemoteCache<ID, T> cache = getCache(ei.getJavaType());
        return new InfinispanRemoteCrudAdapter<T, ID>(ei, cache);
    }
}
