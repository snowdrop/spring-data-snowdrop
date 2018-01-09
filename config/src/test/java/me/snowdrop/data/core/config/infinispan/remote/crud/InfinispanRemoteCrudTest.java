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

package me.snowdrop.data.core.config.infinispan.remote.crud;

import me.snowdrop.data.core.config.infinispan.remote.InfinispanRemoteTestUtils;
import me.snowdrop.data.core.crud.CEntity;
import me.snowdrop.data.core.crud.CrudTestBase;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SpringBootTest(classes = InfinispanRemoteCrudConfiguration.class)
@RunWith(SpringRunner.class)
public class InfinispanRemoteCrudTest extends CrudTestBase {

    @Autowired
    private RemoteCacheManager remoteCacheManager;

    @Before
    public void checkForServer() {
        InfinispanRemoteTestUtils.checkForServer(remoteCacheManager);
    }

    @Override
    protected CEntity create() {
        return new CEntity(1, "foobar");
    }
}
