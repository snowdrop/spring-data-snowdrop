package org.jboss.data.hibernatesearch.config;

import org.jboss.data.hibernatesearch.config.hibernatesearch.FruitHibernateSearchRepository;
import org.jboss.data.hibernatesearch.config.jpa.FruitRepository;
import org.jboss.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SpringBootTest(classes = JpaConfiguration.class, properties = "debug=true")
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@EnableHibernateSearchRepositories(basePackageClasses = FruitHibernateSearchRepository.class)
@EnableJpaRepositories(basePackageClasses = FruitRepository.class)
public class JpaTests {
    @Autowired
    FruitHibernateSearchRepository repository;

    @Autowired
    FruitRepository fruitRepository;

    @Test
    //  @Ignore
    public void testDefault() {
        Assert.assertNotNull(repository);
    }
}
