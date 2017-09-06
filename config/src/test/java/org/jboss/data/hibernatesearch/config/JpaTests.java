package org.jboss.data.hibernatesearch.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SpringBootTest(classes = JpaConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
public class JpaTests {
  @Autowired
  JpaTestsRepository repository;

  @Test
  //  @Ignore
  public void testDefault() {
    Assert.assertNotNull(repository);
  }
}
