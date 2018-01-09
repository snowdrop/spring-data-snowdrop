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

package me.snowdrop.data.core.config.infinispan.embedded.smoke;

import me.snowdrop.data.core.TestUtils;
import me.snowdrop.data.core.config.Fruit;
import me.snowdrop.data.infinispan.embedded.EntityToCacheMapper;
import me.snowdrop.data.core.config.infinispan.embedded.smoke.repository.StandaloneInfinispanSnowdropFruitRepository;
import org.infinispan.Cache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SpringBootTest(classes = InfinispanSmokeConfiguration.class, properties = "debug=false")
@RunWith(SpringRunner.class)
public class InfinispanSmokeTest {
  @Autowired
  StandaloneInfinispanSnowdropFruitRepository hsRepository;

  @Autowired
  EntityToCacheMapper entityToCacheMapper;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    Cache cache = entityToCacheMapper.getCache(Fruit.class);

    Fruit fruit = new Fruit(1L, "Cherry");
    cache.put(fruit.getId(), fruit);
    fruit = new Fruit(2L, "Apple");
    cache.put(fruit.getId(), fruit);
    fruit = new Fruit(3L, "Banana");
    cache.put(fruit.getId(), fruit);
  }

  @After
  public void tearDown() {
    Cache cache = entityToCacheMapper.getCache(Fruit.class);
    cache.clear();
  }

  @Test
  public void testDefault() {
    Assert.assertNotNull(hsRepository);

    Assert.assertEquals(3, hsRepository.count());

    Assert.assertEquals(3, TestUtils.size(hsRepository.findAll()));

    Fruit apple = hsRepository.findByName("Apple");
    Assert.assertNotNull(apple);
    Assert.assertEquals("Apple", apple.getName());
  }
}
