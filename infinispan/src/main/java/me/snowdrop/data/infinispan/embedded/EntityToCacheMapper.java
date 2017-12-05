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

package me.snowdrop.data.infinispan.embedded;

import org.infinispan.Cache;

/**
 * We need to know how to map entity classes to their caches.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface EntityToCacheMapper {
  /**
   * Map entity class to its cache.
   *
   * @param entityClass the entity class
   * @return cache which holds entity class's (indexed) data
   */
  <ID, T> Cache<ID, T> getCache(Class<T> entityClass);
}
