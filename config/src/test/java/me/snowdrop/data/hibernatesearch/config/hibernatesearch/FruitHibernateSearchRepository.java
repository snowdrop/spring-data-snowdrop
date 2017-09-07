package me.snowdrop.data.hibernatesearch.config.hibernatesearch;

import me.snowdrop.data.hibernatesearch.config.Fruit;
import me.snowdrop.data.hibernatesearch.repository.HibernateSearchRepository;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface FruitHibernateSearchRepository extends HibernateSearchRepository<Fruit, Long> {
  Fruit findByName(String name);
}
