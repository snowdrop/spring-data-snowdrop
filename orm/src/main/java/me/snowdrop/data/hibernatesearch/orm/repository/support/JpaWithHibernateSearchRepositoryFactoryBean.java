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
package me.snowdrop.data.hibernatesearch.orm.repository.support;

import java.util.Collection;
import java.util.Collections;

import javax.persistence.EntityManager;

import me.snowdrop.data.hibernatesearch.core.HibernateSearchOperations;
import me.snowdrop.data.hibernatesearch.core.HibernateSearchTemplate;
import me.snowdrop.data.hibernatesearch.orm.JpaDatasourceMapper;
import me.snowdrop.data.hibernatesearch.orm.repository.config.JpaRepositoryFactoryBeanHibernateSearchPostProcessor;
import me.snowdrop.data.hibernatesearch.repository.support.HibernateSearchRepositoryFactory;
import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class JpaWithHibernateSearchRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
        extends JpaRepositoryFactoryBean<T, S, ID> {
  @Autowired(required = false)
  private Collection<JpaRepositoryFactoryBeanHibernateSearchPostProcessor> postProcessors = Collections.emptyList();

  public JpaWithHibernateSearchRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
    super(repositoryInterface);
  }

  @Override
  protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
    DatasourceMapper datasourceMapper = new JpaDatasourceMapper(entityManager.getEntityManagerFactory());
    HibernateSearchOperations operations = new HibernateSearchTemplate(datasourceMapper);
    HibernateSearchRepositoryFactory hibernateSearchRepositoryFactory = new HibernateSearchRepositoryFactory(operations);

    postProcessors.forEach(p -> p.postProcess(hibernateSearchRepositoryFactory));

    return new JpaWithHibernateSearchRepositoryFactory(entityManager, hibernateSearchRepositoryFactory);
  }
}
