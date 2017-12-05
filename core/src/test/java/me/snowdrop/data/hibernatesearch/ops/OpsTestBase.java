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

import me.snowdrop.data.hibernatesearch.TestUtils;
import me.snowdrop.data.hibernatesearch.TestedRepository;
import me.snowdrop.data.hibernatesearch.TestsAction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class OpsTestBase {

    @Autowired
    TestedRepository<Ops> testedRepository;

    Ops repository;

    @Autowired
    TestsAction testsAction;

    @Before
    public void setUp() {
        testsAction.setUp();
        repository = testedRepository.getRepository();
    }

    @After
    public void tearDown() {
        testsAction.tearDown();
    }

    protected void assertSize(Iterable<SimpleEntity> iter, int expectedSize) {
        Assert.assertEquals("Invalid result size.", expectedSize, TestUtils.size(iter));
    }

    protected void assertIds(Iterable<SimpleEntity> iter, long... ids) {
        List<SimpleEntity> list = TestUtils.toList(iter);
        Assert.assertEquals("Invalid result size.", ids.length, list.size());
        for (int i = 0; i < list.size(); i++) {
            Assert.assertEquals("Invalid order, wrong id.", new Long(ids[i]), list.get(i).getId());
        }
    }
}
