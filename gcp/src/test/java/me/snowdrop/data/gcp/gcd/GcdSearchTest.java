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

package me.snowdrop.data.gcp.gcd;

import java.io.File;

import me.snowdrop.data.gcp.SearchTestBase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GcdSearchTest extends SearchTestBase {

    @Deployment
    public static Archive<?> getDeployment() {
        WebArchive war = getBaseDeployment();
        war.addPackage(GcdDatasourceMapper.class.getPackage());
        war.addClasses(GcdSearchConfiguration.class);

        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");
        war.addAsLibraries(pom.resolve("com.google.cloud:google-cloud-datastore").withTransitivity().as(File.class));

        return war;
    }

    @Override
    protected Class<?> getConfigurationClass() {
        return GcdSearchConfiguration.class;
    }

    @Override
    protected void assertOrOps() {
        // ignore -- OR not yet supported
    }
}
