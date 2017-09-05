package org.jboss.data.hibernatesearch.ops;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.DatasourceMapperForTest;
import org.jboss.data.hibernatesearch.TestUtils;
import org.jboss.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
public class OpsTestsBase {

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

    SimpleEntity entity = new SimpleEntity(1L, "ann", "Does Ann like good red apples?", -20, true, "Superman", "red");
    entities.add(entity);
    entity = new SimpleEntity(2L, "barb", "Why is Barb dancing twist?", -10, true, "Spiderman", "red");
    entities.add(entity);
    entity = new SimpleEntity(3L, "carl", "Carl is good at running and jumping.", 0, true, "Flash", "red");
    entities.add(entity);
    entity = new SimpleEntity(4L, "doug", "Doug likes to sleeps.", 10, false, "Batman", "black");
    entities.add(entity);
    entity = new SimpleEntity(5L, "eva", "Eva is running in circles.", 20, false, "Ironman", "gold");
    entities.add(entity);
    entity = new SimpleEntity(6L, "fanny", "Fanny is reading a good book.", 30, false, "Aquaman", "blue");
    entities.add(entity);

    TestUtils.preindexEntities(searchIntegrator, datasourceMapper, entities.toArray(new SimpleEntity[0]));
  }

  @After
  public void tearDown() {
    TestUtils.purgeAll(searchIntegrator, datasourceMapper, SimpleEntity.class);
  }

  protected void assertSize(Iterable<SimpleEntity> iter, int expectedSize) {
    Assert.assertEquals(expectedSize, TestUtils.size(iter));
  }

  protected void assertIds(Iterable<SimpleEntity> iter, long... ids) {
    List<SimpleEntity> list = TestUtils.toList(iter);
    Assert.assertEquals(list.size(), ids.length);
    for (int i = 0; i < list.size(); i++) {
      Assert.assertEquals(list.get(i).getId(), new Long(ids[i]));
    }
  }

}
