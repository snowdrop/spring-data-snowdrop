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

import static me.snowdrop.data.core.config.infinispan.remote.InfinispanRemoteTestUtils.checkForServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.snowdrop.data.core.TestsAction;
import me.snowdrop.data.core.crud.CEntity;
import me.snowdrop.data.core.ops.OpsTestsActionBase;
import me.snowdrop.data.core.ops.SimpleEntity;
import me.snowdrop.data.infinispan.remote.EntityToRemoteCacheMapper;
import me.snowdrop.data.infinispan.remote.ProtobufInitializer;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
public class InfinispanRemoteConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ProtobufInitializer createProtobufInitializer(RemoteCacheManager remoteCacheManager) {
        ProtobufInitializer pbi = new ProtobufInitializer(remoteCacheManager, "test.proto");
        pbi.setFailIfError(false).addClasses(SimpleEntity.class, CEntity.class);
        return pbi;
    }

    @Bean
    public TestsAction testsAction(RemoteCacheManager rcm, EntityToRemoteCacheMapper entityToCacheMapper) {
        return new InfinispanRemoteOpsTestsAction(rcm, entityToCacheMapper);
    }

    private class InfinispanRemoteOpsTestsAction extends OpsTestsActionBase {
        private RemoteCacheManager rcm;
        private EntityToRemoteCacheMapper entityToRemoteCacheMapper;

        public InfinispanRemoteOpsTestsAction(RemoteCacheManager rcm, EntityToRemoteCacheMapper entityToRemoteCacheMapper) {
            this.rcm = rcm;
            this.entityToRemoteCacheMapper = entityToRemoteCacheMapper;
        }

        protected Map getCache() {
            if (InfinispanRemoteTestUtils.isSeverRunning()) {
                return entityToRemoteCacheMapper.getRemoteCache(SimpleEntity.class);
            } else {
                return new HashMap();
            }
        }

        @Override
        public void setUp() {
            checkForServer(rcm);

            super.setUp();
        }

        protected void setUp(List<SimpleEntity> entities) {
            for (SimpleEntity entity : entities) {
                //noinspection unchecked
                getCache().put(entity.getId(), entity);
            }
        }

        public void tearDown() {
            getCache().clear();
        }
    }
}
