package me.snowdrop.data.hibernatesearch.config;

import java.io.Serializable;

import javax.persistence.EntityManager;

import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class JpaDatasourceMapper implements DatasourceMapper {

  private EntityManager em;

  public JpaDatasourceMapper(EntityManager em) {
    Assert.notNull(em, "Null EntityManager!");
    this.em = em;
  }

  @Override
  public <T> T get(Class<T> entityClass, Serializable id) {
    return em.find(entityClass, id);
  }
}
