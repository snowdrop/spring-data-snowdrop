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

package me.snowdrop.data.hibernatesearch.ops;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import me.snowdrop.data.hibernatesearch.annotations.TargetField;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface Ops {
    @TargetField(property = "dummy1", field = "aaa")
    @TargetField(property = "dummy2", field = "bbb")
    long countByColor(String color);

    List<SimpleEntity> findByPokeNotNullAndTextNotLike(String text);

    List<SimpleEntity> findByPokeIsNull();

    List<SimpleEntity> findByPokeIsNotNull();

    @TargetField(property = "name", field = "identity.name")
    List<SimpleEntity> findByNameNot(String notName);

    List<SimpleEntity> findByNumberBetween(int min, int max);

    List<SimpleEntity> findByNumberLessThan(int number);

    List<SimpleEntity> findByNumberBefore(int number);

    List<SimpleEntity> findByNumberGreaterThan(int number);

    List<SimpleEntity> findByNumberAfter(int number);

    List<SimpleEntity> findByTextRegex(String text);

    List<SimpleEntity> findByTextLike(String text);

    List<SimpleEntity> findByTextNotLike(String text);

    List<SimpleEntity> findByTextStartingWith(String text);

    List<SimpleEntity> findByTextEndingWith(String text);

    List<SimpleEntity> findByTextContaining(String text);

    List<SimpleEntity> findByTextNotContaining(String text);

    @TargetField(property = "name", field = "identity.name")
    List<SimpleEntity> findByNameIn(Collection<String> names);

    @TargetField(property = "name", field = "identity.name")
    List<SimpleEntity> findByNameNotIn(Collection<String> names);

    List<SimpleEntity> findByBuulTrue();

    List<SimpleEntity> findByBuulFalse();

    Stream<SimpleEntity> findByColor(String color);

    Optional<SimpleEntity> findByNumberBetweenOrderByHero(int min, int max);

    List<SimpleEntity> findByAddressZipcode(int zipcode);

    List<SimpleEntity> findByLocationWithin(double latitude, double longitude, double distance);

    List<SimpleProjection> findByNameOrderByHero(String name);

    <T> List<T> findByName(String name, Class<T> type);

    List<HeroProjection> findByHero(String hero);

    List<SimpleEntity> findFirst2ByNumberAfter(int number, Sort sort);

    @TargetField(property = "name", field = "identity.name")
    List<SimpleEntity> findByNameNot(String notName, Sort sort);

    List<SimpleEntity> findByNumberBetween(int min, int max, Sort sort);

    List<SimpleEntity> findByNumberLessThan(int number, Sort sort);

    List<SimpleEntity> findByNumberBefore(int number, Sort sort);

    List<SimpleEntity> findByNumberGreaterThan(int number, Sort sort);

    List<SimpleEntity> findByNumberAfter(int number, Sort sort);

    List<SimpleEntity> findByTextRegex(String text, Sort sort);

    List<SimpleEntity> findByTextLike(String text, Sort sort);

    List<SimpleEntity> findByTextStartingWith(String text, Sort sort);

    List<SimpleEntity> findByTextContaining(String text, Sort sort);

    List<SimpleEntity> findByTextNotContaining(String text, Sort sort);

    @TargetField(property = "name", field = "identity.name")
    List<SimpleEntity> findByNameIn(Collection<String> names, Sort sort);

    @TargetField(property = "name", field = "identity.name")
    List<SimpleEntity> findByNameNotIn(Collection<String> names, Sort sort);

    List<SimpleEntity> findByBuulTrue(Sort sort);

    List<SimpleEntity> findByBuulFalse(Sort sort);

    @TargetField(property = "name", field = "identity.name")
    List<SimpleEntity> findByColorOrderByNameAsc(String color);

    @TargetField(property = "name", field = "identity.name")
    List<SimpleEntity> findByColorOrderByNameDesc(String color);

    @TargetField(property = "name", field = "identity.name")
    List<SimpleEntity> findByName(String name);

//  List<SimpleEntity> findByBridge_Custom_Name(String name);

//  List<SimpleEntity> findByBridge_Custom_DynamicName(String name);

    @TargetField(property = "contained.name", field = "containedList.somePrefix_containedName")
    List<SimpleEntity> findByContainedName(String containedName);

    @TargetField(property = "contained.number", field = "containedList.somePrefix_numberAsText")
    List<SimpleEntity> findByContainedNumberBetween(int min, int max);
}
