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

package me.snowdrop.data.core.ops;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import me.snowdrop.data.core.repository.SnowdropRepository;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.PageRequest;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class OpsDefaultBase extends OpsTestBase {

    @Test
    public void testDefaults() {
        Assume.assumeTrue(
            "This test is only relevant on standalone Hibernate Search repositories",
            repository instanceof SnowdropRepository
        );
        @SuppressWarnings("unchecked")
        SnowdropRepository<SimpleEntity, Long> snowdropRepository = (SnowdropRepository<SimpleEntity, Long>) repository;
        assertSize(snowdropRepository.findAll(), 7);
        assertSize(snowdropRepository.findAll(PageRequest.of(1, 3)), 3);
    }

    @Test
    public void testCountBy() {
        Assert.assertEquals(3L, repository.countByColor("red"));
    }

    @Test
    public void testDupNot() {
        assertSize(repository.findByPokeNotNullAndTextNotLike("good"), 2);
    }

    @Test
    public void testOrAnd() {
        assertSize(repository.findByPokeNotNullAndTextNotLikeOrNumber("good", 0), 3);
        assertSize(repository.findByPokeNotNullAndTextNotLikeOrNumberAndPoke("good", 0, "bzzz"), 2);
        assertSize(repository.findByPokeNotNullAndTextNotLikeOrNumberOrPoke("good", 0, "army"), 4);
        assertSize(repository.findByPokeNotNullAndTextNotLikeAndNumberOrPoke("good", 10, "army"), 2);
    }

    @Test
    public void testIsNull() {
        assertSize(repository.findByPokeIsNull(), 3);
    }

    @Test
    public void testIsNotNull() {
        assertSize(repository.findByPokeIsNotNull(), 4);
    }

    @Test
    public void testFindByNameNot() {
        assertSize(repository.findByNameNot("doug"), 6);
    }

    @Test
    public void testFindByNumberBetween() {
        assertSize(repository.findByNumberBetween(-5, 11), 3);
    }

    @Test
    public void testFindByNumberLessThan() {
        assertSize(repository.findByNumberLessThan(0), 2);
    }

    @Test
    public void testFindByNumberBefore() {
        assertSize(repository.findByNumberBefore(10), 4);
    }

    @Test
    public void testFindByNumberGreaterThan() {
        assertSize(repository.findByNumberGreaterThan(5), 3);
    }

    @Test
    public void testFindByNumberAfter() {
        assertSize(repository.findByNumberAfter(20), 1);
    }

    @Test
    public void testFindByTextRegex() {
        assertSize(repository.findByTextRegex("like[s]?"), 2);
    }

    @Test
    public void testFindByTextLike() {
        assertSize(repository.findByTextLike("read"), 1);
    }

    @Test
    public void testFindByTextNotLike() {
        assertSize(repository.findByTextNotLike("good"), 4);
    }

    @Test
    public void testFindByTextStartingWith() {
        assertSize(repository.findByTextStartingWith("read"), 1);
    }

    @Test
    public void testFindByTextEndingWith() {
        assertSize(repository.findByTextEndingWith("king"), 1);
    }

    @Test
    public void testFindByTextContaining() {
        assertSize(repository.findByTextContaining("good"), 3);
    }

    @Test
    public void testFindByTextNotContaining() {
        assertSize(repository.findByTextNotContaining("running"), 5);
    }

    @Test
    public void testFindByNameIn() {
        assertSize(repository.findByNameIn(Collections.singleton("barb")), 1);
    }

    @Test
    public void testFindByNameNotIn() {
        assertSize(repository.findByNameNotIn(Collections.singleton("carl")), 6);
    }

    @Test
    public void testFindByBuulTrue() {
        assertSize(repository.findByBuulTrue(), 3);
    }

    @Test
    public void testFindByBuulFalse() {
        assertSize(repository.findByBuulFalse(), 4);
    }

    @Test
    public void testNestedProps() {
        assertSize(repository.findByAddressZipcode(1360), 2);
    }

    @Test
    public void testNonDefaultCompositeFieldName() {
        assertSize(repository.findByName("ann"), 1);
    }

    @Test
    public void testBridgeDefinedField() {
//    assertSize(repository.findByBridge_Custom_Name("ann"), 1);
    }

    @Test
    public void testBridgeDefinedDynamicField() {
//    assertSize(repository.findByBridge_Custom_DynamicName("ann"), 1);
    }

    @Test
    public void testNestedComplexFieldName() {
        assertSize(repository.findByContainedName("Frank"), 1);
    }

    @Test
    public void testMisleadingFieldType() {
        // Should match "42", since the numbers are indexed as strings
        assertSize(repository.findByContainedNumberBetween(4, 5), 1);
    }

    @Test
    public void testOptional() {
        Optional<SimpleEntity> optional = repository.findByNumberBetweenOrderByHero(9, 11);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(new Long(4), optional.get().getId());

        optional = repository.findByNumberBetweenOrderByHero(1234, 2000);
        Assert.assertFalse(optional.isPresent());

        try {
            repository.findByNumberBetweenOrderByHero(-5, 15);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(IncorrectResultSizeDataAccessException.class, e.getClass());
        }
    }

    @Test
    public void testStream() {
        try (Stream<SimpleEntity> stream = repository.findByColor("red")) {
            Assert.assertEquals(3, stream.count());
        }
    }

    @Test
    public void testGeoLocation() {
        assertSize(repository.findByLocationWithin(24.0, 31.5, 55), 1);
        assertSize(repository.findByLocationWithin(new Location(24.0, 31.5), 55), 1);
    }

    @Test
    public void testProjection() {
        SimpleProjection sp = repository.findByNameOrderByColor("ann");
        Assert.assertEquals("ann", sp.getName());
        Assert.assertEquals(-20, sp.getNumber());

        List<SimpleProjection> projections = repository.findByNameOrderByHero("ann");
        Assert.assertEquals(1, projections.size());
        Assert.assertEquals("ann", projections.get(0).getName());
        Assert.assertEquals(-20, projections.get(0).getNumber());

        projections = repository.findByName("ann", SimpleProjection.class);
        Assert.assertEquals(1, projections.size());
        Assert.assertEquals("ann", projections.get(0).getName());
        Assert.assertEquals(-20, projections.get(0).getNumber());

        List<HeroProjection> hps = repository.findByHero("Spiderman"); // TODO fix to lowercase
        Assert.assertEquals(1, hps.size());
        Assert.assertEquals("Spiderman [red]", hps.get(0).getInfo());
    }
}
