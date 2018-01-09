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

package me.snowdrop.data.core.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ProcessBean;

import me.snowdrop.data.core.spi.DatasourceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.cdi.CdiRepositoryBean;
import org.springframework.data.repository.cdi.CdiRepositoryExtensionSupport;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SnowdropRepositoryExtension extends CdiRepositoryExtensionSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnowdropRepositoryExtension.class);
    private final Map<Set<Annotation>, Bean<DatasourceMapper>> dsMap = new HashMap<>();

    public SnowdropRepositoryExtension() {
        LOGGER.info("Activating CDI extension for Spring Data Hibernate Search repositories.");
    }

    @SuppressWarnings("unchecked")
    <T> void processBean(@Observes ProcessBean<T> processBean) {
        Bean<T> bean = processBean.getBean();
        for (Type type : bean.getTypes()) {
            if (type instanceof Class<?> && DatasourceMapper.class.isAssignableFrom((Class<?>) type)) {
                Set<Annotation> qualifiers = new HashSet<>(bean.getQualifiers());
                if (bean.isAlternative() || dsMap.containsKey(qualifiers) == false) {
                    LOGGER.debug("Discovered '{}' with qualifiers {}.", DatasourceMapper.class.getName(), qualifiers);
                    dsMap.put(qualifiers, ((Bean<DatasourceMapper>) bean));
                }
            }
        }
    }

    void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
        for (Map.Entry<Class<?>, Set<Annotation>> entry : getRepositoryTypes()) {
            Class<?> repositoryType = entry.getKey();
            Set<Annotation> qualifiers = entry.getValue();

            CdiRepositoryBean<?> repositoryBean = createRepositoryBean(repositoryType, qualifiers, beanManager);
            LOGGER.info("Registering bean for '{}' with qualifiers {}.", repositoryType.getName(), qualifiers);

            registerBean(repositoryBean);
            afterBeanDiscovery.addBean(repositoryBean);
        }
    }

    private <T> CdiRepositoryBean<T> createRepositoryBean(Class<T> repositoryType, Set<Annotation> qualifiers, BeanManager beanManager) {
        Bean<DatasourceMapper> dsBean = dsMap.get(qualifiers);
        if (dsBean == null) {
            throw new UnsatisfiedResolutionException(String.format("Unable to resolve a bean for '%s' with qualifiers %s.", DatasourceMapper.class.getName(), qualifiers));
        }

        return new SnowdropRepositoryBean<>(dsBean, qualifiers, repositoryType, beanManager, getCustomImplementationDetector());
    }
}
