package org.jboss.data.hibernatesearch.ops;

import java.util.Collections;

import org.junit.Test;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class OpsSortTests extends OpsTestsBase {

  @Test
  public void testFindAllSort() {
    assertIds(repository.findAll(new Sort("color")), 4, 6, 5, 1, 2, 3);
  }

  @Test
  public void testFindByNameNot() {
    assertIds(repository.findByNameNot("doug", new Sort(Sort.Direction.DESC, "hero")), 1, 2, 5, 3, 6);
  }

  @Test
  public void testFindByNumberBetween() {
    assertIds(repository.findByNumberBetween(-5, 11, new Sort("hero")), 4, 3);
  }

  @Test
  public void testFindByNumberLessThan() {
    assertIds(repository.findByNumberLessThan(0, new Sort("hero")), 2, 1);
  }

  @Test
  public void testFindByNumberBefore() {
    assertIds(repository.findByNumberBefore(10, new Sort(new Sort.Order("color"), new Sort.Order(Sort.Direction.DESC, "hero"))), 4, 1, 2, 3);
  }

  @Test
  public void testFindByNumberGreaterThan() {
    assertIds(repository.findByNumberGreaterThan(5, new Sort("hero")), 6, 4, 5);
  }

  @Test
  public void testFindByNumberAfter() {
    assertIds(repository.findByNumberAfter(20, new Sort("hero")), 6, 5);
  }

  @Test
  public void testFindByTextRegx() {
    assertIds(repository.findByTextRegex("like[s]?", new Sort("hero")), 4, 1);
  }

  @Test
  public void testFindByTextLike() {
    assertIds(repository.findByTextLike("re", new Sort("hero")), 6, 1);
  }

  @Test
  public void testFindByTextStartingWith() {
    assertIds(repository.findByTextStartingWith("re", new Sort("hero")), 6, 1);
  }

  @Test
  public void testFindByTextContaining() {
    assertIds(repository.findByTextContaining("good", new Sort("hero")), 6, 3, 1);
  }

  @Test
  public void testFindByTextNotContaining() {
    assertIds(repository.findByTextNotContaining("running", new Sort(Sort.Direction.DESC, "hero")), 1, 2, 4, 6);
  }

  @Test
  public void testFindByNameIn() {
    assertIds(repository.findByNameIn(Collections.singleton("barb"), new Sort("hero")), 2);
  }

  @Test
  public void testFindByNameNotIn() {
    assertIds(repository.findByNameNotIn(Collections.singleton("carl"), new Sort("hero")), 6, 4, 5, 2, 1);
  }

  @Test
  public void testFindByBuulTrue() {
    assertIds(repository.findByBuulTrue(new Sort("hero")), 3, 2, 1);
  }

  @Test
  public void testFindByBuulFalse() {
    assertIds(repository.findByBuulFalse(new Sort("hero")), 6, 4, 5);
  }

}
