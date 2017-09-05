package org.jboss.data.hibernatesearch.repository.support;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.UUID;

import org.jboss.data.hibernatesearch.core.HibernateSearchOperations;
import org.jboss.data.hibernatesearch.core.mapping.HibernateSearchPersistentEntity;
import org.jboss.data.hibernatesearch.repository.query.HibernateSearchPartQuery;
import org.jboss.data.hibernatesearch.repository.query.HibernateSearchQueryMethod;
import org.jboss.data.hibernatesearch.repository.query.HibernateSearchStringQuery;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchRepositoryFactory extends RepositoryFactorySupport {
  private HibernateSearchOperations hibernateSearchOperations;

  public HibernateSearchRepositoryFactory(HibernateSearchOperations hibernateSearchOperations) {
    this.hibernateSearchOperations = hibernateSearchOperations;
  }

  @Override
  public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> aClass) {
    TypeInformation<T> typeInformation = ClassTypeInformation.from(aClass);
    HibernateSearchPersistentEntity<T> persistentEntity = new HibernateSearchPersistentEntity<>(typeInformation);
    return new MappingHibernateSearchEntityInformation<>(persistentEntity);
  }

  @Override
  protected Object getTargetRepository(RepositoryInformation info) {
    return getTargetRepositoryViaReflection(info, hibernateSearchOperations, getEntityInformation(info.getDomainType()));
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

  @Override
  protected QueryLookupStrategy getQueryLookupStrategy(QueryLookupStrategy.Key key, EvaluationContextProvider evaluationContextProvider) {
    return new HibernateSearchQueryLookupStrategy();
  }

  private class HibernateSearchQueryLookupStrategy implements QueryLookupStrategy {

    @Override
    public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {

      HibernateSearchQueryMethod queryMethod = new HibernateSearchQueryMethod(method, metadata, factory);
      String namedQueryName = queryMethod.getNamedQueryName();

      if (namedQueries.hasQuery(namedQueryName)) {
        String namedQuery = namedQueries.getQuery(namedQueryName);
        return new HibernateSearchStringQuery(queryMethod, hibernateSearchOperations, namedQuery);
      } else if (queryMethod.hasAnnotatedQuery()) {
        return new HibernateSearchStringQuery(queryMethod, hibernateSearchOperations, queryMethod.getAnnotatedQuery());
      }
      return new HibernateSearchPartQuery(queryMethod, hibernateSearchOperations);
    }
  }
}
