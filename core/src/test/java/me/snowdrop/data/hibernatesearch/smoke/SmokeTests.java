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

package me.snowdrop.data.hibernatesearch.smoke;

import java.util.List;

import me.snowdrop.data.hibernatesearch.DatasourceMapperForTest;
import me.snowdrop.data.hibernatesearch.TestUtils;
import me.snowdrop.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.hibernate.search.spi.SearchIntegrator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class SmokeTests {

  @Configuration
  @EnableHibernateSearchRepositories
  public static class Config {
    @Bean(destroyMethod = "close")
    public SearchIntegrator searchIntegrator() {
      return TestUtils.createSearchIntegrator(SmokeEntity.class);
    }

    @Bean
    public DatasourceMapperForTest datasourceMapper() {
      return TestUtils.createDatasourceMapper();
    }
  }

  @Autowired
  SmokeRepository repository;

  @Autowired
  SearchIntegrator searchIntegrator;

  @Autowired
  DatasourceMapperForTest datasourceMapper;

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

    TestUtils.preindexEntities(searchIntegrator, datasourceMapper, entities);
  }

  @After
  public void tearDown() {
    TestUtils.purgeAll(searchIntegrator, datasourceMapper, SmokeEntity.class);
  }

  @Test
  public void testDefaultRepository() {
    Assert.assertNotNull(repository);

    List<SmokeEntity> all = TestUtils.toList(repository.findAll());
    Assert.assertEquals(4L, all.size());

    Assert.assertEquals(4L, repository.count());

    Iterable<SmokeEntity> sorted = repository.findAll(new Sort(Sort.Direction.DESC, "name"));
    Assert.assertEquals("4", sorted.iterator().next().getId());

    Pageable pageable = new PageRequest(1, 2, new Sort(new Sort.Order("type")));
    Page<SmokeEntity> pageables = repository.findAll(pageable);
    Assert.assertEquals(2, pageables.getTotalElements());
  }

  @Test
  public void testSmokeRepository() {
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
  }
}
