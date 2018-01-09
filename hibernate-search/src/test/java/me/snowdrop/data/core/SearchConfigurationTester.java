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

package me.snowdrop.data.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.search.cfg.SearchMapping;
import org.hibernate.search.cfg.spi.SearchConfiguration;
import org.hibernate.search.cfg.spi.SearchConfigurationBase;
import org.hibernate.search.engine.service.classloading.impl.DefaultClassLoaderService;
import org.hibernate.search.engine.service.classloading.spi.ClassLoaderService;
import org.hibernate.search.engine.service.spi.Service;
import org.hibernate.search.spi.DefaultInstanceInitializer;
import org.hibernate.search.spi.InstanceInitializer;
import org.hibernate.search.testsupport.TestConstants;

/**
 * Manually defines the configuration.
 *
 * @author Emmanuel Bernard
 * @author Ales Justin
 */
public class SearchConfigurationTester extends SearchConfigurationBase implements SearchConfiguration {

    private final Map<String, Class<?>> classes;
    private final Properties properties;
    private final HashMap<Class<? extends Service>, Object> providedServices;
    private final InstanceInitializer initializer;
    private SearchMapping programmaticMapping;
    private boolean transactionsExpected = true;
    private boolean indexMetadataComplete = true;
    private boolean deleteByTermEnforced = false;
    private boolean idProvidedImplicit = false;
    private boolean multitenancyEnabled = false;
    private ClassLoaderService classLoaderService;
    private boolean enableJPAAnnotationsProcessing;

    public static SearchConfigurationTester noTestDefaults() {
        return new SearchConfigurationTester(new Properties());
    }

    public SearchConfigurationTester() {
        this(DefaultInstanceInitializer.DEFAULT_INITIALIZER, false);
    }

    public SearchConfigurationTester(Properties properties) {
        this(DefaultInstanceInitializer.DEFAULT_INITIALIZER, false, properties);
    }

    public SearchConfigurationTester(InstanceInitializer init, boolean expectsJPAAnnotations) {
        this(init, expectsJPAAnnotations, new Properties());
    }

    private SearchConfigurationTester(InstanceInitializer init, boolean expectsJPAAnnotations, Properties properties) {
        this.initializer = init;
        this.enableJPAAnnotationsProcessing = expectsJPAAnnotations;
        this.classes = new HashMap<>();
        this.properties = properties;
        this.providedServices = new HashMap<>();
        this.classLoaderService = new DefaultClassLoaderService();
        addProperty("hibernate.search.default.directory_provider", "ram");
        addProperty("hibernate.search.lucene_version", TestConstants.getTargetLuceneVersion().toString());
    }

    public SearchConfigurationTester addProperty(String key, String value) {
        properties.setProperty(key, value);
        return this;
    }

    public SearchConfigurationTester addClass(Class<?> indexed) {
        classes.put(indexed.getName(), indexed);
        return this;
    }

    public SearchConfigurationTester addClasses(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            addClass(clazz);
        }
        return this;
    }

    @Override
    public Iterator<Class<?>> getClassMappings() {
        return classes.values().iterator();
    }

    @Override
    public Class<?> getClassMapping(String name) {
        return classes.get(name);
    }

    @Override
    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public ReflectionManager getReflectionManager() {
        return null;
    }

    @Override
    public SearchMapping getProgrammaticMapping() {
        return programmaticMapping;
    }

    public SearchConfigurationTester setProgrammaticMapping(SearchMapping programmaticMapping) {
        this.programmaticMapping = programmaticMapping;
        return this;
    }

    @Override
    public Map<Class<? extends Service>, Object> getProvidedServices() {
        return providedServices;
    }

    public void addProvidedService(Class<? extends Service> serviceRole, Object service) {
        providedServices.put(serviceRole, service);
    }

    @Override
    public boolean isTransactionManagerExpected() {
        return this.transactionsExpected;
    }

    public void setTransactionsExpected(boolean transactionsExpected) {
        this.transactionsExpected = transactionsExpected;
    }

    @Override
    public InstanceInitializer getInstanceInitializer() {
        return initializer;
    }

    @Override
    public boolean isIndexMetadataComplete() {
        return indexMetadataComplete;
    }

    public void setIndexMetadataComplete(boolean indexMetadataComplete) {
        this.indexMetadataComplete = indexMetadataComplete;
    }

    public void setDeleteByTermEnforced(boolean deleteByTermEnforced) {
        this.deleteByTermEnforced = deleteByTermEnforced;
    }

    @Override
    public boolean isDeleteByTermEnforced() {
        return deleteByTermEnforced;
    }

    @Override
    public boolean isIdProvidedImplicit() {
        return idProvidedImplicit;
    }

    public SearchConfigurationTester setIdProvidedImplicit(boolean idProvidedImplicit) {
        this.idProvidedImplicit = idProvidedImplicit;
        return this;
    }

    public boolean isMultitenancyEnabled() {
        return multitenancyEnabled;
    }

    public SearchConfigurationTester setMultitenancyEnabled(boolean multitenancyEnabled) {
        this.multitenancyEnabled = multitenancyEnabled;
        return this;
    }

    @Override
    public ClassLoaderService getClassLoaderService() {
        return classLoaderService;
    }

    public void setClassLoaderService(ClassLoaderService classLoaderService) {
        this.classLoaderService = classLoaderService;
    }

    public boolean isJPAAnnotationsProcessingEnabled() {
        return enableJPAAnnotationsProcessing;
    }

    public void setEnableJPAAnnotationsProcessing(boolean enableJPAAnnotationsProcessing) {
        this.enableJPAAnnotationsProcessing = enableJPAAnnotationsProcessing;
    }

}
