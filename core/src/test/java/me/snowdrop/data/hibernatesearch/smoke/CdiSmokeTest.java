/*
 * Copyright 2018 Red Hat, Inc, and individual contributors.
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

package me.snowdrop.data.hibernatesearch.smoke;

import me.snowdrop.data.hibernatesearch.DatasourceMapperTester;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CdiSmokeTest extends SmokeTestBase {

    private static WeldContainer container;

    SmokeRepository repository;

    DatasourceMapperTester datasourceMapper;

    protected SmokeRepository getRepository() {
        return repository;
    }

    protected DatasourceMapperTester getDatasourceMapper() {
        return datasourceMapper;
    }

    @BeforeClass
    public static void initialize() {
        Weld weld = new Weld();
        container = weld.initialize();
    }

    @Before
    public void setUp() {
        datasourceMapper = container.select(DatasourceMapperTester.class).get();
        repository = container.select(SmokeRepository.class).get();

        super.setUp();
    }

    @AfterClass
    public static void shutdown() {
        container.shutdown();
    }
}
