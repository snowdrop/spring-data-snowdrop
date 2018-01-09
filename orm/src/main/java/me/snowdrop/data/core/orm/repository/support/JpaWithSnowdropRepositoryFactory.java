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

import javax.persistence.EntityManager;

import me.snowdrop.data.core.repository.support.SnowdropRepositoryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.query.EvaluationContextProvider;

public class JpaWithSnowdropRepositoryFactory extends JpaRepositoryFactory {

    private final SnowdropRepositoryFactory snowdropRepositoryFactory;

    public JpaWithSnowdropRepositoryFactory(
        EntityManager entityManager, SnowdropRepositoryFactory snowdropRepositoryFactory) {
        super(entityManager);
        this.snowdropRepositoryFactory = snowdropRepositoryFactory;
    }

    @Override
    public void setEvaluationContextProvider(EvaluationContextProvider evaluationContextProvider) {
        super.setEvaluationContextProvider(evaluationContextProvider);
        snowdropRepositoryFactory.setEvaluationContextProvider(evaluationContextProvider);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        super.setBeanClassLoader(classLoader);
        snowdropRepositoryFactory.setBeanClassLoader(classLoader);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);
        snowdropRepositoryFactory.setBeanFactory(beanFactory);
    }

    @Override
    protected RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
        return super.getRepositoryFragments(metadata).append(getSnowdropFragments(metadata));
    }

    private RepositoryFragments getSnowdropFragments(RepositoryMetadata metadata) {
        return snowdropRepositoryFactory.getExtensionFragments(metadata);
    }
}
