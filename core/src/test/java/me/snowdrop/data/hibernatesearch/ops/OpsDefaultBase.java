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

import org.junit.Test;
import org.springframework.data.domain.PageRequest;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class OpsDefaultBase extends OpsTestsBase {

  @Test
  public void testDefaults() {
    assertSize(repository.findAll(), 6);
    assertSize(repository.findAll(new PageRequest(1, 3)), 3);
  }

  @Test
  public void testFindByNameNot() {
    assertSize(repository.findByNameNot("doug"), 5);
  }

  @Test
  public void testFindByNumberBetween() {
    assertSize(repository.findByNumberBetween(-5, 11), 2);
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
    assertSize(repository.findByNumberAfter(20), 2);
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
  public void testFindByTextStartingWith() {
    assertSize(repository.findByTextStartingWith("read"), 1);
  }

  @Test
  public void testFindByTextContaining() {
    assertSize(repository.findByTextContaining("good"), 3);
  }

  @Test
  public void testFindByTextNotContaining() {
    assertSize(repository.findByTextNotContaining("running"), 4);
  }

  @Test
  public void testFindByNameIn() {
    assertSize(repository.findByNameIn(Collections.singleton("barb")), 1);
  }

  @Test
  public void testFindByNameNotIn() {
    assertSize(repository.findByNameNotIn(Collections.singleton("carl")), 5);
  }

  @Test
  public void testFindByBuulTrue() {
    assertSize(repository.findByBuulTrue(), 3);
  }

  @Test
  public void testFindByBuulFalse() {
    assertSize(repository.findByBuulFalse(), 3);
  }

}
