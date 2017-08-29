package org.jboss.data.hibernatesearch.smoke;

import java.util.List;

import org.jboss.data.hibernatesearch.annotations.Query;
import org.jboss.data.hibernatesearch.repository.HibernateSearchRepository;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface SmokeRepository extends HibernateSearchRepository<SmokeEntity, String> {
  List<SmokeEntity> findByType(String type);

  SmokeEntity findByName(String name);

  SmokeEntity findByNameAndType(String name, String type);

  List<SmokeEntity> findByNameOrType(String name, String type);

  @Query("(+type:?0)")
  List<SmokeEntity> findByTypeQuery(String type);
}
