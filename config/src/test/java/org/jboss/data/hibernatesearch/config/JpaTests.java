package org.jboss.data.hibernatesearch.config;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SpringBootTest(classes = {JpaConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class JpaTests {
  @Test
  @Ignore // TODO
  public void testDefault() {

  }
}
