package org.jboss.data.hibernatesearch.repository.support;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.data.repository.core.support.PersistentEntityInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface HibernateSearchEntityInformation<T, ID extends Serializable> extends EntityInformation<T, ID> {
}
