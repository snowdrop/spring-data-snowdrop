package org.jboss.data.hibernatesearch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jboss.data.hibernatesearch.spi.DatasourceMapper;

/**
 * @author Ales Justin
 */
public class DatasourceMapperForTest implements DatasourceMapper {
  private Map<Serializable, Object> map = new HashMap<>();

  @Override
  public <T> T get(Class<T> entityClass, Serializable id) {
    Object entity = map.get(id);
    return entity != null ? entityClass.cast(entity) : null;
  }

  public void put(AbstractEntity entity) {
    map.put(entity.getId(), entity);
  }

  public void clear() {
    map.clear();
  }
}