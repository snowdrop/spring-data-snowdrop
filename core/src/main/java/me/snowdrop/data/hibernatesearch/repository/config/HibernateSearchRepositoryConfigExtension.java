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

package me.snowdrop.data.hibernatesearch.repository.config;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import me.snowdrop.data.hibernatesearch.repository.HibernateSearchCrudRepository;
import me.snowdrop.data.hibernatesearch.repository.HibernateSearchRepository;
import me.snowdrop.data.hibernatesearch.repository.support.HibernateSearchRepositoryFactoryBean;
import org.hibernate.search.annotations.Indexed;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {
    @Override
    protected String getModulePrefix() {
        return "hibernatesearch";
    }

    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Collections.singletonList(Indexed.class);
    }

    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Arrays.asList(HibernateSearchRepository.class, HibernateSearchCrudRepository.class);
    }

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return HibernateSearchRepositoryFactoryBean.class.getName();
    }

    @Override
    public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {
        Element element = config.getElement();
        builder.addPropertyReference("datasourceMapper", element.getAttribute("datasource-mapper-ref"));
    }

    @Override
    public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
        AnnotationAttributes attributes = config.getAttributes();
        builder.addPropertyReference("datasourceMapper", attributes.getString("datasourceMapperRef"));
    }
}
