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

package me.snowdrop.data.core.smoke;

import me.snowdrop.data.core.DatasourceMapperTester;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class SpringSmokeTestBase extends SmokeTestBase {

    @Autowired
    SmokeRepository repository;

    @Autowired
    DatasourceMapperTester datasourceMapper;

    protected SmokeRepository getRepository() {
        return repository;
    }

    protected DatasourceMapperTester getDatasourceMapper() {
        return datasourceMapper;
    }
}
