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
package me.snowdrop.data.core.orm.repository.config;

import me.snowdrop.data.core.repository.support.SnowdropRepositoryFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.query.QueryLookupStrategy;

public class JpaRepositoryFactoryBeanSnowdropPostProcessor {

  private NamedQueries namedQueries;

  private QueryLookupStrategy.Key queryLookupStrategyKey;

  public void setNamedQueries(NamedQueries namedQueries) {
    this.namedQueries = namedQueries;
  }

  public void setQueryLookupStrategyKey(QueryLookupStrategy.Key queryLookupStrategyKey) {
    this.queryLookupStrategyKey = queryLookupStrategyKey;
  }

  public void postProcess(SnowdropRepositoryFactory factory) {
    if (namedQueries != null) {
      factory.setNamedQueries(namedQueries);
    }
    if (queryLookupStrategyKey != null) {
      factory.setQueryLookupStrategyKey(queryLookupStrategyKey);
    }
  }
}
