package me.snowdrop.data.hibernatesearch.config.jpa;

import me.snowdrop.data.hibernatesearch.config.Fruit;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FruitRepository extends PagingAndSortingRepository<Fruit, Long> {
}
