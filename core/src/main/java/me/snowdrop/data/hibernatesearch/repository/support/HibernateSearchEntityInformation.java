package me.snowdrop.data.hibernatesearch.repository.support;

import java.io.Serializable;

import org.springframework.data.repository.core.EntityInformation;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface HibernateSearchEntityInformation<T, ID extends Serializable> extends EntityInformation<T, ID> {
}
