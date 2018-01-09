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

package me.snowdrop.data.core.config.jpa;

import me.snowdrop.data.core.TestsAction;
import me.snowdrop.data.core.config.Fruit;
import me.snowdrop.data.core.ops.SimpleEntity;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
public class JpaConfiguration {
  private static final Class<?>[] CLASSES = {Fruit.class, SimpleEntity.class};

  @Autowired
  EntityManagerFactory entityManagerFactory;

  @Bean
  public TestsAction testsAction() {
    return TestsAction.NOOP;
  }

  @Bean
  public BuildSearchIndex buildSearchIndex() {
    return new BuildSearchIndex();
  }

  public class BuildSearchIndex implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
      try {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
          FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
          fullTextEntityManager.createIndexer(CLASSES).startAndWait();
        } finally {
          entityManager.close();
        }
      } catch (InterruptedException e) {
        System.out.println("An error occurred trying to build the search index: " + e.toString());
      }
    }
  }

}
