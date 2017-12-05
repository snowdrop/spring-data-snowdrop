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

import java.util.Collections;

import me.snowdrop.data.hibernatesearch.repository.HibernateSearchRepository;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class OpsSortBase extends OpsTestBase {

  @Test
  public void testFindAllSort() {
    Assume.assumeTrue(
            "This test is only relevant on standalone Hibernate Search repositories",
            repository instanceof HibernateSearchRepository
    );
    @SuppressWarnings("unchecked")
    HibernateSearchRepository<SimpleEntity, Long> hibernateSearchRepository = (HibernateSearchRepository<SimpleEntity, Long>) repository;
    assertIds(hibernateSearchRepository.findAll(Sort.by("color", "name")), 4, 6, 5, 7, 1, 2, 3);
  }

  @Test
  public void testFindTop() {
    assertIds(repository.findFirst2ByNumberAfter(5, Sort.by("hero")), 6, 4);
  }

  @Test
  public void testFindByNameNot() {
    assertIds(repository.findByNameNot("doug", Sort.by(Sort.Direction.DESC, "hero")), 1, 2, 5, 3, 6, 7);
  }

  @Test
  public void testFindByNumberBetween() {
    assertIds(repository.findByNumberBetween(-5, 11, Sort.by(Sort.Order.by("hero").nullsFirst())), 7, 4, 3);
  }

  @Test
  public void testFindByNumberLessThan() {
    assertIds(repository.findByNumberLessThan(0, Sort.by("hero")), 2, 1);
  }

  @Test
  public void testFindByNumberBefore() {
      assertIds(repository.findByNumberBefore(10, Sort.by(Sort.Order.by("color"), Sort.Order.desc("hero"))), 7, 1, 2, 3);
  }

  @Test
  public void testFindByNumberGreaterThan() {
    assertIds(repository.findByNumberGreaterThan(5, Sort.by("hero")), 6, 4, 5);
  }

  @Test
  public void testFindByNumberAfter() {
      assertIds(repository.findByNumberAfter(20, Sort.by("hero")), 6);
  }

  @Test
  public void testFindByTextRegx() {
    assertIds(repository.findByTextRegex("like[s]?", Sort.by("hero")), 4, 1);
  }

  @Test
  public void testFindByTextLike() {
    assertIds(repository.findByTextLike("re", Sort.by("hero")), 6, 1);
  }

  @Test
  public void testFindByTextStartingWith() {
    assertIds(repository.findByTextStartingWith("re", Sort.by("hero")), 6, 1);
  }

  @Test
  public void testFindByTextContaining() {
    assertIds(repository.findByTextContaining("good", Sort.by("hero")), 6, 3, 1);
  }

  @Test
  public void testFindByTextNotContaining() {
    assertIds(repository.findByTextNotContaining("running", Sort.by(Sort.Direction.DESC, "hero")), 1, 2, 4, 6, 7);
  }

  @Test
  public void testFindByNameIn() {
    assertIds(repository.findByNameIn(Collections.singleton("barb"), Sort.by("hero")), 2);
  }

  @Test
  public void testFindByNameNotIn() {
    assertIds(repository.findByNameNotIn(Collections.singleton("carl"), Sort.by(Sort.Order.by("hero").nullsLast())), 6, 4, 5, 2, 1, 7);
  }

  @Test
  public void testFindByBuulTrue() {
    assertIds(repository.findByBuulTrue(Sort.by("hero")), 3, 2, 1);
  }

  @Test
  public void testFindByBuulFalse() {
    assertIds(repository.findByBuulFalse(Sort.by("hero")), 7, 6, 4, 5);
  }

  @Test
  public void testOrderBy() {
    assertIds(repository.findByColorOrderByNameAsc("red"), 1, 2, 3);
    assertIds(repository.findByColorOrderByNameDesc("red"), 3, 2, 1);
  }
}
