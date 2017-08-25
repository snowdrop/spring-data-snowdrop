package org.jboss.data.hibernatesearch.repository;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@NoRepositoryBean
public interface HibernateSearchCrudRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {
}
