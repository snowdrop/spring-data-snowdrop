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

package me.snowdrop.data.core.repository.config;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import me.snowdrop.data.core.repository.SnowdropCrudRepository;
import me.snowdrop.data.core.repository.SnowdropRepository;
import me.snowdrop.data.core.repository.support.SnowdropRepositoryFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SnowdropRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {
    private static Class<? extends Annotation> INDEXED;

    static {
        try {
            ClassLoader classLoader = SnowdropRepository.class.getClassLoader();
            //noinspection unchecked
            INDEXED = (Class<? extends Annotation>) classLoader.loadClass("org.hibernate.search.annotations.Indexed");
        } catch (Exception ignored) {
        }
    }

    @Override
    protected String getModulePrefix() {
        return "snowdrop";
    }

    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return (INDEXED != null ? Collections.singleton(INDEXED) : Collections.emptySet());
    }

    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Arrays.asList(SnowdropRepository.class, SnowdropCrudRepository.class);
    }

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return SnowdropRepositoryFactoryBean.class.getName();
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
