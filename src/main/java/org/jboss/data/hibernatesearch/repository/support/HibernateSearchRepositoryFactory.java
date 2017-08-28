package org.jboss.data.hibernatesearch.repository.support;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.core.mapping.HibernateSearchPersistentEntity;
import org.jboss.data.hibernatesearch.spi.DatasourceMapper;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchRepositoryFactory extends RepositoryFactorySupport {
  private SearchIntegrator searchIntegrator;
  private DatasourceMapper datasourceMapper;

  public HibernateSearchRepositoryFactory(SearchIntegrator searchIntegrator, DatasourceMapper datasourceMapper) {
    this.searchIntegrator = searchIntegrator;
    this.datasourceMapper = datasourceMapper;
  }

  @Override
  public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> aClass) {
    TypeInformation<T> typeInformation = ClassTypeInformation.from(aClass);
    HibernateSearchPersistentEntity<T> persistentEntity = new HibernateSearchPersistentEntity<>(typeInformation);
    return new MappingHibernateSearchEntityInformation<>(persistentEntity);
  }

  @Override
  protected Object getTargetRepository(RepositoryInformation info) {
    return getTargetRepositoryViaReflection(info, searchIntegrator, datasourceMapper, getEntityInformation(info.getDomainType()));
  }

  @Override
  protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
    if (Integer.class.isAssignableFrom(metadata.getIdType()) || Long.class.isAssignableFrom(metadata.getIdType())
      || Double.class.isAssignableFrom(metadata.getIdType())) {
      return SimpleRepository.class;
    } else if (metadata.getIdType() == String.class) {
      return SimpleRepository.class;
    } else if (metadata.getIdType() == UUID.class) {
      return SimpleRepository.class;
    } else {
      throw new IllegalArgumentException("Unsupported ID type " + metadata.getIdType());
    }
  }
}
