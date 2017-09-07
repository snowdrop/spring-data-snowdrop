package me.snowdrop.data.hibernatesearch.core;

import java.util.Iterator;

import me.snowdrop.data.hibernatesearch.core.mapping.HibernateSearchPersistentProperty;
import me.snowdrop.data.hibernatesearch.core.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.mapping.context.MappingContext;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface HibernateSearchOperations {

  MappingContext<?, HibernateSearchPersistentProperty> getMappingContext();

  /**
   * Returns the number of entities available.
   *
   * @param query the query
   * @return the number of entities
   */
  <T> long count(Query query);

  /**
   * Returns all instances of the type.
   *
   * @param query the query
   * @return matching entity or null
   */
  <T> T findSingle(Query query);

  /**
   * Returns all instances of the type.
   *
   * @param query the query
   * @return all entities
   */
  <T> Iterable<T> findAll(Query Query);

  /**
   * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
   *
   * @param query the query
   * @return a page of entities
   */
  <T> Page<T> findPageable(Query query);

  <T> Iterator<T> stream(Query query);
}
