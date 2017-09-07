package me.snowdrop.data.hibernatesearch.repository;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@NoRepositoryBean
public interface HibernateSearchRepository<T, ID extends Serializable> extends Repository<T, ID> {

  /**
   * Returns all instances of the type.
   *
   * @return all entities
   */
  Iterable<T> findAll();

  /**
   * Returns the number of entities available.
   *
   * @return the number of entities
   */
  long count();

  /**
   * Returns all entities sorted by the given options.
   *
   * @param sort the sort
   * @return all entities sorted by the given options
   */
  Iterable<T> findAll(Sort sort);

  /**
   * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
   *
   * @param pageable the pageable
   * @return a page of entities
   */
  Page<T> findAll(Pageable pageable);

  Class<T> getEntityClass();
}
