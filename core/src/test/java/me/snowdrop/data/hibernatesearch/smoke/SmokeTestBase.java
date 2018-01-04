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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import me.snowdrop.data.hibernatesearch.DatasourceMapperTester;
import me.snowdrop.data.hibernatesearch.TestUtils;
import me.snowdrop.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class SmokeTestBase {

    @Autowired
    SmokeRepository repository;

    @Autowired
    DatasourceMapperTester datasourceMapper;

    @Before
    public void setUp() {
        SmokeEntity[] entities = new SmokeEntity[4];

        SmokeEntity entity = new SmokeEntity();
        entity.setId("1");
        entity.setName("aa");
        entity.setType("foo");
        entities[0] = entity;

        entity = new SmokeEntity();
        entity.setId("2");
        entity.setName("bb");
        entity.setType("bar");
        entities[1] = entity;

        entity = new SmokeEntity();
        entity.setId("3");
        entity.setName("cc");
        entity.setType("foo");
        entities[2] = entity;

        entity = new SmokeEntity();
        entity.setId("4");
        entity.setName("dd");
        entity.setType("baz");
        entities[3] = entity;

        TestUtils.preindexEntities(datasourceMapper, entities);
    }

    @After
    public void tearDown() {
        TestUtils.purgeAll(datasourceMapper, SmokeEntity.class);
    }

    @Test
    public void testDefaultRepository() {
        Assert.assertNotNull(repository);

        List<SmokeEntity> all = TestUtils.toList(repository.findAll());
        Assert.assertEquals(4L, all.size());

        Assert.assertEquals(4L, repository.count());

        Iterable<SmokeEntity> sorted = repository.findAll(Sort.by(Sort.Direction.DESC, "name"));
        Assert.assertEquals("4", sorted.iterator().next().getId());

        Pageable pageable = PageRequest.of(1, 2, Sort.by(Sort.Order.by("type")));
        Page<SmokeEntity> pageables = repository.findAll(pageable);
        Assert.assertEquals(2, pageables.getNumberOfElements());
    }

    @Test
    public void testSmokeRepository() throws Exception {
        Assert.assertEquals(2, repository.findByType("foo").size());

        SmokeEntity byName = repository.findByName("bb");
        Assert.assertNotNull(byName);
        Assert.assertEquals("2", byName.getId());

        List<SmokeEntity> byNameAndType = repository.findByNameAndType("cc", "foo");
        Assert.assertEquals(1, byNameAndType.size());
        Assert.assertEquals("3", byNameAndType.get(0).getId());

        List<SmokeEntity> byTypeQuery = repository.findByTypeQuery("foo");
        Assert.assertEquals(2, byTypeQuery.size());

        List<SmokeEntity> byNameOrType = repository.findByNameOrType("aa", "bar");
        Assert.assertEquals(2, byNameOrType.size());

        List<SmokeEntity> byNamed = repository.findByNameViaNamedQuery("dd");
        Assert.assertEquals(1, byNamed.size());

        try (Stream<SmokeEntity> stream = repository.findByTypeIn(Collections.singleton("foo"))) {
            Assert.assertEquals(2, stream.count());
        }

        Future<List<SmokeEntity>> async = repository.findByTypeAfter("cqq");
        List<SmokeEntity> list = async.get();
        Assert.assertEquals(2, list.size());

        Optional<SmokeEntity> optional = repository.findByNameBefore("az");
        Assert.assertEquals("aa", optional.get().getName());
        optional = repository.findByNameBefore("00"); // should be before "aa"
        Assert.assertFalse(optional.isPresent());

        Assert.assertTrue(repository.existsByType("foo"));
        Assert.assertFalse(repository.existsByType("zwy"));

        Set<SmokeEntity> set = repository.findByNameAfter("bz");
        Assert.assertEquals(2, set.size());

        try {
            repository.findByNameBefore("bz"); // should be 2
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(IncorrectResultSizeDataAccessException.class, e.getClass());
        }

        try {
            repository.deleteByName("bz");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(UnsupportedOperationException.class, e.getClass());
        }
    }
}
