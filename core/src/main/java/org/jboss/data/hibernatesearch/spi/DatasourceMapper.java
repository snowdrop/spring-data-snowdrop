package org.jboss.data.hibernatesearch.spi;

import java.io.Serializable;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface DatasourceMapper {
  <T> T get(Class<T> entityClass, Serializable id);
}
