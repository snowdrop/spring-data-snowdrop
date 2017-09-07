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

package me.snowdrop.data.hibernatesearch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;

/**
 * @author Ales Justin
 */
public class DatasourceMapperForTest implements DatasourceMapper {
  private Map<Serializable, Object> map = new HashMap<>();

  @Override
  public <T> T get(Class<T> entityClass, Serializable id) {
    Object entity = map.get(id);
    return entity != null ? entityClass.cast(entity) : null;
  }

  public void put(AbstractEntity entity) {
    map.put(entity.getId(), entity);
  }

  public void clear() {
    map.clear();
  }
}