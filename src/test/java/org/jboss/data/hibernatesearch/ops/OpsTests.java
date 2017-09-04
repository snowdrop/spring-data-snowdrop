package org.jboss.data.hibernatesearch.ops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.DatasourceMapperForTest;
import org.jboss.data.hibernatesearch.TestUtils;
import org.jboss.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class OpsTests {

  @Configuration
  @EnableHibernateSearchRepositories
  public static class Config {
    @Bean(destroyMethod = "close")
    public SearchIntegrator searchIntegrator() {
      return TestUtils.createSearchIntegrator(SimpleEntity.class);
    }

    @Bean
    public DatasourceMapperForTest datasourceMapper() {
      return TestUtils.createDatasourceMapper();
    }
  }

  @Autowired
  OpsRepository repository;

  @Autowired
  SearchIntegrator searchIntegrator;

  @Autowired
  DatasourceMapperForTest datasourceMapper;

  @Before
  public void setUp() {
    List<SimpleEntity> entities = new ArrayList<>();

    SimpleEntity entity = new SimpleEntity(1L, "ann", "Ann likes good red apples.", -20, true);
    entities.add(entity);
    entity = new SimpleEntity(2L, "barb", "Barb is dancing twist.", -10, true);
    entities.add(entity);
    entity = new SimpleEntity(3L, "carl", "Carl is jumping.", 0, true);
    entities.add(entity);
    entity = new SimpleEntity(4L, "doug", "Doug sleeps.", 10, false);
    entities.add(entity);
    entity = new SimpleEntity(5L, "eva", "Eva is running in circles.", 20, false);
    entities.add(entity);
    entity = new SimpleEntity(6L, "fanny", "Fanny is reading a good book.", 30, false);
    entities.add(entity);

    TestUtils.preindexEntities(searchIntegrator, datasourceMapper, entities.toArray(new SimpleEntity[0]));
  }

  @After
  public void tearDown() {
    TestUtils.purgeAll(searchIntegrator, datasourceMapper, SimpleEntity.class);
  }

  private void assertSize(List<SimpleEntity> list, int expectedSize){
    Assert.assertEquals(expectedSize, list.size());
  }

  @Test
  public void testFindByNameNot() {
    assertSize(repository.findByNameNot("doug"), 5);
  }

  @Test
  public void testFindByNumberBetween() {
    assertSize(repository.findByNumberBetween(-5, 11), 2);
  }

  @Test
  public void testFindByNumberLessThan() {
    assertSize(repository.findByNumberLessThan(0), 2);
  }

  @Test
  public void testFindByNumberBefore() {
    assertSize(repository.findByNumberBefore(10), 4);
  }

  @Test
  public void testFindByNumberGreaterThan() {
    assertSize(repository.findByNumberGreaterThan(5), 3);
  }

  @Test
  public void testFindByNumberAfter() {
    assertSize(repository.findByNumberAfter(20), 2);
  }

  @Test
  public void testFindByTextLike() {
    assertSize(repository.findByTextLike("read"), 1);
  }

  @Test
  public void testFindByTextStartingWith() {
    assertSize(repository.findByTextStartingWith("read"), 1);
  }

  @Test
  public void testFindByTextContaining() {
    assertSize(repository.findByTextContaining("good"), 2);
  }

  @Test
  public void testFindByNameIn() {
    assertSize(repository.findByNameIn(Collections.singleton("barb")), 1);
  }

  @Test
  public void testFindByNameNotIn() {
    assertSize(repository.findByNameNotIn(Collections.singleton("carl")), 5);
  }

  @Test
  public void testFindByBuulTrue() {
    assertSize(repository.findByBuulTrue(), 3);
  }

  @Test
  public void testFindByBuulFalse() {
    assertSize(repository.findByBuulFalse(), 3);
  }

}
