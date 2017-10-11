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

package me.snowdrop.data.hibernatesearch.config.smoke.jpa.standalone;

import me.snowdrop.data.hibernatesearch.TestUtils;
import me.snowdrop.data.hibernatesearch.config.HibernateSearchDataInfinispanAutoConfiguration;
import me.snowdrop.data.hibernatesearch.config.smoke.Fruit;
import me.snowdrop.data.hibernatesearch.config.smoke.jpa.JpaConfiguration;
import me.snowdrop.data.hibernatesearch.config.smoke.repository.standalone.hibernatesearch.FruitStandaloneHibernateSearchRepository;
import me.snowdrop.data.hibernatesearch.config.smoke.repository.standalone.jpa.FruitStandaloneJpaRepository;
import me.snowdrop.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SpringBootTest(classes = JpaConfiguration.class, properties = "debug=false")
@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = HibernateSearchDataInfinispanAutoConfiguration.class)
@EnableJpaRepositories(basePackageClasses = FruitStandaloneJpaRepository.class)
@EnableHibernateSearchRepositories(basePackageClasses = FruitStandaloneHibernateSearchRepository.class)
public class StandaloneJpaTests {
  @Autowired
  FruitStandaloneHibernateSearchRepository hsRepository;

  @Autowired
  FruitStandaloneJpaRepository jpaRepository;

  @Test
  public void testDefault() {
    Assert.assertNotNull(hsRepository);
    Assert.assertNotNull(jpaRepository);

    Assert.assertEquals(3, hsRepository.count());
    Assert.assertEquals(3, jpaRepository.count());

    Assert.assertEquals(3, TestUtils.size(hsRepository.findAll()));
    Assert.assertEquals(3, TestUtils.size(jpaRepository.findAll()));

    // Ask for a lowercase match, which would only work with Hibernate Search, not with the JPQL 'equals'
    Fruit apple = hsRepository.findByName("apple");
    Assert.assertNotNull(apple);
    Assert.assertEquals("Apple", apple.getName());
  }
}
