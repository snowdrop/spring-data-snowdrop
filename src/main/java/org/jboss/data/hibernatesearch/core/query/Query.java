package org.jboss.data.hibernatesearch.core.query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface Query {
  Class<?> getEntityClass();

  Pageable getPageable();

  Sort getSort();
}
