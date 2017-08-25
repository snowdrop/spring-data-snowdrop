package org.jboss.data.hibernatesearch.smoke;

import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.TestUtils;
import org.jboss.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
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
public class SmokeTest {

  @Configuration
  @EnableHibernateSearchRepositories
  public static class Config {
    @Bean(destroyMethod = "close")
    public SearchIntegrator searchIntegrator() {
      return TestUtils.createSearchIntegrator(SmokeEntity.class);
    }
  }

  @Autowired
  SmokeRepository repository;

  @Autowired
  SearchIntegrator searchIntegrator;

  @Before
  public void setUp() {
    SmokeEntity[] entities = new SmokeEntity[1];

    SmokeEntity entity = new SmokeEntity();
    entity.setId("1");
    entity.setType("foo");
    entities[0] = entity;

    TestUtils.preindexEntities(searchIntegrator, entities);
  }

  @Test
  public void testSmokeRepositry() {
    Assert.assertNotNull(repository);
    Assert.assertEquals(1L, repository.count());
  }
}
