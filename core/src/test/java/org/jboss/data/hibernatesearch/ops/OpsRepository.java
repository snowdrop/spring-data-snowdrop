package org.jboss.data.hibernatesearch.ops;

import java.util.Collection;
import java.util.List;

import org.jboss.data.hibernatesearch.repository.HibernateSearchRepository;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface OpsRepository extends HibernateSearchRepository<SimpleEntity, Long> {
  List<SimpleEntity> findByNameNot(String notName);
  List<SimpleEntity> findByNumberBetween(int min, int max);
  List<SimpleEntity> findByNumberLessThan(int number);
  List<SimpleEntity> findByNumberBefore(int number);
  List<SimpleEntity> findByNumberGreaterThan(int number);
  List<SimpleEntity> findByNumberAfter(int number);

  List<SimpleEntity> findByTextRegex(String text);
  List<SimpleEntity> findByTextLike(String text);
  List<SimpleEntity> findByTextStartingWith(String text);
  List<SimpleEntity> findByTextContaining(String text);

  List<SimpleEntity> findByTextNotContaining(String text);
  List<SimpleEntity> findByNameIn(Collection<String> names);
  List<SimpleEntity> findByNameNotIn(Collection<String> names);
  List<SimpleEntity> findByBuulTrue();
  List<SimpleEntity> findByBuulFalse();

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

  List<SimpleEntity> findByNameIn(Collection<String> names, Sort sort);

  List<SimpleEntity> findByNameNotIn(Collection<String> names, Sort sort);

  List<SimpleEntity> findByBuulTrue(Sort sort);

  List<SimpleEntity> findByBuulFalse(Sort sort);
}
