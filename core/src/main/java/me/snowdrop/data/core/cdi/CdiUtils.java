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

import java.io.IOException;
import java.util.Properties;

import me.snowdrop.data.core.repository.config.SnowdropRepositoryConfigExtension;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.support.PropertiesBasedNamedQueries;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class CdiUtils {
    static NamedQueries findNamedQueries(Class<?> repositoryClass) {
        try {
            RepositoryConfigurationExtension config = new SnowdropRepositoryConfigExtension();
            String location = config.getDefaultNamedQueryLocation();

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(repositoryClass.getClassLoader());
            ResourceArrayPropertyEditor editor = new ResourceArrayPropertyEditor(resolver, null);
            editor.setAsText(location);
            Resource[] resources = (Resource[]) editor.getValue();

            PropertiesFactoryBean pfb = new PropertiesFactoryBean();
            pfb.setSingleton(false);
            pfb.setLocations(resources);
            pfb.setFileEncoding("UTF-8");
            Properties properties = pfb.getObject();

            return new PropertiesBasedNamedQueries(properties);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
