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

import java.util.Map;
import java.util.Set;

import me.snowdrop.data.core.crud.MapCrudAdapter;
import org.infinispan.client.hotrod.RemoteCache;
import org.springframework.data.repository.core.EntityInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class InfinispanRemoteCrudAdapter<T, ID> extends MapCrudAdapter<T, ID> {
    private final RemoteCache<ID, T> cache;

    public InfinispanRemoteCrudAdapter(EntityInformation<T, ID> ei, RemoteCache<ID, T> cache) {
        super(ei, cache);
        this.cache = cache;
    }

    @Override
    protected Map<ID, T> findAll(Set<ID> keys) {
        return cache.getAll(keys);
    }
}
