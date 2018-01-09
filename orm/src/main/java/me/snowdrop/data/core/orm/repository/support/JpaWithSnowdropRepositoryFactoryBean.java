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
package me.snowdrop.data.core.orm.repository.support;

import java.util.Collection;
import java.util.Collections;

import javax.persistence.EntityManager;

import me.snowdrop.data.core.ops.SnowdropOperations;
import me.snowdrop.data.core.ops.SnowdropTemplate;
import me.snowdrop.data.core.orm.JpaDatasourceMapper;
import me.snowdrop.data.core.orm.repository.config.JpaRepositoryFactoryBeanSnowdropPostProcessor;
import me.snowdrop.data.core.repository.support.SnowdropRepositoryFactory;
import me.snowdrop.data.core.spi.DatasourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class JpaWithSnowdropRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
    extends JpaRepositoryFactoryBean<T, S, ID> {
    @Autowired(required = false)
    private Collection<JpaRepositoryFactoryBeanSnowdropPostProcessor> postProcessors = Collections.emptyList();

    public JpaWithSnowdropRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        DatasourceMapper datasourceMapper = new JpaDatasourceMapper(entityManager.getEntityManagerFactory());
        SnowdropOperations operations = new SnowdropTemplate(datasourceMapper);
        SnowdropRepositoryFactory snowdropRepositoryFactory = new SnowdropRepositoryFactory(operations);

        postProcessors.forEach(p -> p.postProcess(snowdropRepositoryFactory));

        return new JpaWithSnowdropRepositoryFactory(entityManager, snowdropRepositoryFactory);
    }
}
