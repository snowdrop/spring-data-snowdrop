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

package me.snowdrop.data.core.config;

import me.snowdrop.data.core.repository.config.SnowdropRepositoryConfigExtension;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.data.repository.config.RepositoryBeanDefinitionParser;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SnowdropNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        RepositoryConfigurationExtension extension = new SnowdropRepositoryConfigExtension();
        RepositoryBeanDefinitionParser parser = new RepositoryBeanDefinitionParser(extension);

        registerBeanDefinitionParser("repositories", parser);
    }
}
