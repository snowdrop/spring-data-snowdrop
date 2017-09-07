package org.jboss.data.hibernatesearch.config.hibernatesearch;

import org.jboss.data.hibernatesearch.config.Fruit;
import org.jboss.data.hibernatesearch.repository.HibernateSearchRepository;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface FruitHibernateSearchRepository extends HibernateSearchRepository<Fruit, Long> {
  Fruit findByName(String name);
}
