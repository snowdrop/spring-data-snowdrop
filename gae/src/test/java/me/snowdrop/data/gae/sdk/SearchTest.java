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

package me.snowdrop.data.gae.sdk;

import java.io.File;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@RunWith(Arquillian.class)
public class SearchTest {

    private AnnotationConfigApplicationContext context;
    private GaeSearchRepository repository;

    @Deployment
    public static Archive<?> getDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class);
        war.addPackage(EntityToModelMapper.class.getPackage());
        war.addClasses(GaeSearchRepository.class, SearchData.class, SearchConfiguration.class);
        war.addAsWebInfResource("appengine-web.xml");
        war.addAsResource("datastore-indexes.xml");

        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");
        war.addAsLibraries(pom.resolve("com.google.appengine:appengine-api-1.0-sdk").withTransitivity().as(File.class));
        war.addAsLibraries(pom.resolve("me.snowdrop.data:spring-data-snowdrop-core").withTransitivity().as(File.class));

        return war;
    }

    @Before
    public void setUp() {
        context = new AnnotationConfigApplicationContext(SearchConfiguration.class);
        context.start();

        repository = context.getBean(GaeSearchRepository.class);

        repository.save(new SearchData(10, "Baz"));
    }

    @After
    public void tearDown() {
        context.stop();
        context.close();
    }

    @Test
    public void testDefaultOps() {
        Assert.assertNotNull(repository);

        List<SearchData> sds = repository.findByFooNot(20);
        assertDefaultOps(sds);

        sds = repository.findByBar("Baz");
        assertDefaultOps(sds);

        sds = repository.findByFooAndBar(10, "Baz");
        assertDefaultOps(sds);

        sds = repository.findByFooOrBar(10, "Qqq");
        assertDefaultOps(sds);

        sds = repository.findByFooOrBar(30, "Baz");
        assertDefaultOps(sds);
    }

    private void assertDefaultOps(List<SearchData> sds) {
        Assert.assertEquals(1, sds.size());
        SearchData sd = sds.get(0);
        Assert.assertEquals(10, sd.getFoo());
        Assert.assertEquals("Baz", sd.getBar());
    }
}
