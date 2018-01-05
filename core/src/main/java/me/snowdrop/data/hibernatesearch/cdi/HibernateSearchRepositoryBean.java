/*
 * Copyright 2018 Red Hat, Inc, and individual contributors.
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

package me.snowdrop.data.hibernatesearch.cdi;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import me.snowdrop.data.hibernatesearch.core.HibernateSearchOperations;
import me.snowdrop.data.hibernatesearch.core.HibernateSearchTemplate;
import me.snowdrop.data.hibernatesearch.repository.support.HibernateSearchRepositoryFactory;
import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import org.springframework.data.repository.cdi.CdiRepositoryBean;
import org.springframework.data.repository.config.CustomRepositoryImplementationDetector;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchRepositoryBean<T> extends CdiRepositoryBean<T> {

    private final Bean<DatasourceMapper> dsBean;

    /**
     * Creates a new {@link HibernateSearchRepositoryBean}.
     *
     * @param mapper         must not be {@literal null}.
     * @param qualifiers     must not be {@literal null}.
     * @param repositoryType must not be {@literal null}.
     * @param beanManager    must not be {@literal null}.
     * @param detector       detector for the custom {@link org.springframework.data.repository.Repository} implementations
     *                       {@link CustomRepositoryImplementationDetector}, can be {@literal null}.
     */
    public HibernateSearchRepositoryBean(Bean<DatasourceMapper> mapper, Set<Annotation> qualifiers,
                                         Class<T> repositoryType, BeanManager beanManager, CustomRepositoryImplementationDetector detector) {

        super(qualifiers, repositoryType, beanManager, Optional.of(detector));

        Assert.notNull(mapper, "Cannot create repository with 'null' DatasourceMapper.");
        this.dsBean = mapper;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.cdi.CdiRepositoryBean#create(javax.enterprise.context.spi.CreationalContext, java.lang.Class, java.util.Optional)
     */
    @Override
    protected T create(CreationalContext<T> creationalContext, Class<T> repositoryType, Optional<Object> customImplementation) {
        DatasourceMapper mapper = getDependencyInstance(dsBean, DatasourceMapper.class);
        HibernateSearchOperations hsOperations = new HibernateSearchTemplate(mapper);
        HibernateSearchRepositoryFactory factory = new HibernateSearchRepositoryFactory(hsOperations);
        factory.setNamedQueries(CdiUtils.findNamedQueries(repositoryType));

        return customImplementation //
            .map(o -> factory.getRepository(repositoryType, RepositoryComposition.RepositoryFragments.just(o))) //
            .orElseGet(() -> factory.getRepository(repositoryType));
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.cdi.CdiRepositoryBean#getScope()
     */
    @Override
    public Class<? extends Annotation> getScope() {
        return dsBean.getScope();
    }
}
