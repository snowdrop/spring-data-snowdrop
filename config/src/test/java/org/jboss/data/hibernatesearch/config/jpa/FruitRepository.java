package org.jboss.data.hibernatesearch.config.jpa;

import org.jboss.data.hibernatesearch.config.Fruit;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FruitRepository extends PagingAndSortingRepository<Fruit, Long> {
}
