package org.jboss.data.hibernatesearch.ops;

import java.util.Collection;
import java.util.List;

import org.jboss.data.hibernatesearch.repository.HibernateSearchRepository;

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
  List<SimpleEntity> findByTextLike(String text);
  List<SimpleEntity> findByTextStartingWith(String text);
  List<SimpleEntity> findByTextContaining(String text);
  List<SimpleEntity> findByNameIn(Collection<String> names);
  List<SimpleEntity> findByNameNotIn(Collection<String> names);
  List<SimpleEntity> findByBuulTrue();
  List<SimpleEntity> findByBuulFalse();
}
