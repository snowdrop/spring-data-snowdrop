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

package me.snowdrop.data.hibernatesearch.ops;

import java.util.List;

import me.snowdrop.data.hibernatesearch.DatasourceMapperForTest;
import me.snowdrop.data.hibernatesearch.TestUtils;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class OpsTestsAction extends OpsTestsActionBase {
  private DatasourceMapperForTest datasourceMapper;

  public OpsTestsAction(DatasourceMapperForTest datasourceMapper) {
    this.datasourceMapper = datasourceMapper;
  }

  protected void setUp(List<SimpleEntity> entities) {
    TestUtils.preindexEntities(datasourceMapper, entities.toArray(new SimpleEntity[0]));
  }

  public void tearDown() {
    TestUtils.purgeAll(datasourceMapper, SimpleEntity.class);
  }
}
