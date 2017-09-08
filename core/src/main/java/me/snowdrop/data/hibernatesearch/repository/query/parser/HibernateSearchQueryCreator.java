/*
 * Copyright 2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.snowdrop.data.hibernatesearch.repository.query.parser;

import java.util.Collection;
import java.util.Iterator;

import me.snowdrop.data.hibernatesearch.core.mapping.HibernateSearchPersistentProperty;
import me.snowdrop.data.hibernatesearch.core.query.Criteria;
import me.snowdrop.data.hibernatesearch.core.query.CriteriaQuery;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.context.PersistentPropertyPath;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

/**
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Franck Marchand
 * @author Artur Konczak
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchQueryCreator extends AbstractQueryCreator<CriteriaQuery, CriteriaQuery> {

  private final Class<?> entityClass;
  private final MappingContext<?, HibernateSearchPersistentProperty> context;

  public HibernateSearchQueryCreator(Class<?> entityClass, PartTree tree, ParameterAccessor parameters, MappingContext<?, HibernateSearchPersistentProperty> context) {
    super(tree, parameters);
    this.entityClass = entityClass;
    this.context = context;
  }

  @Override
  protected CriteriaQuery create(Part part, Iterator<Object> iterator) {
    PersistentPropertyPath<HibernateSearchPersistentProperty> path = context.getPersistentPropertyPath(part.getProperty());
    return new CriteriaQuery(entityClass, from(part, new Criteria(path.toDotPath(HibernateSearchPersistentProperty.PropertyToFieldNameConverter.INSTANCE)), iterator));
  }

  @Override
  protected CriteriaQuery and(Part part, CriteriaQuery base, Iterator<Object> iterator) {
    if (base == null) {
      return create(part, iterator);
    }
    PersistentPropertyPath<HibernateSearchPersistentProperty> path = context.getPersistentPropertyPath(part.getProperty());
    return base.addCriteria(from(part, new Criteria(path.toDotPath(HibernateSearchPersistentProperty.PropertyToFieldNameConverter.INSTANCE)), iterator));
  }

  @Override
  protected CriteriaQuery or(CriteriaQuery base, CriteriaQuery query) {
    return new CriteriaQuery(base.getEntityClass(), base.getCriteria().or(query.getCriteria()));
  }

  @Override
  protected CriteriaQuery complete(CriteriaQuery query, Sort sort) {
    if (query == null) {
      return null;
    }
    query.setSort(sort);
    return query;
  }

  private Criteria from(Part part, Criteria instance, Iterator<?> parameters) {
    Part.Type type = part.getType();

    Criteria criteria = instance;
    if (criteria == null) {
      criteria = new Criteria();
    }
    switch (type) {
      case TRUE:
        return criteria.is(true);
      case FALSE:
        return criteria.is(false);
      case NEGATING_SIMPLE_PROPERTY:
        return criteria.isNot(parameters.next());
      case REGEX:
        return criteria.regexp(parameters.next().toString());
      case LIKE:
      case STARTING_WITH:
        return criteria.startsWith(parameters.next().toString());
      case ENDING_WITH:
        return criteria.endsWith(parameters.next().toString());
      case CONTAINING:
        return criteria.contains(parameters.next().toString());
      case NOT_CONTAINING:
        return criteria.notContains(parameters.next().toString());
      case GREATER_THAN:
        return criteria.greaterThan(parameters.next());
      case AFTER:
      case GREATER_THAN_EQUAL:
        return criteria.greaterThanEqual(parameters.next());
      case LESS_THAN:
        return criteria.lessThan(parameters.next());
      case BEFORE:
      case LESS_THAN_EQUAL:
        return criteria.lessThanEqual(parameters.next());
      case BETWEEN:
        return criteria.between(parameters.next(), parameters.next());
      case IN:
        return criteria.in(asArray(parameters.next()));
      case NOT_IN:
        return criteria.notIn(asArray(parameters.next()));
      case SIMPLE_PROPERTY:
      case WITHIN: {
        Object firstParameter = parameters.next();
        Object secondParameter;
        if (type == Part.Type.SIMPLE_PROPERTY) {
          if (part.getProperty().getType() != Point.class) {
            return criteria.is(firstParameter);
          } else {
            // it means it's a simple find with exact geopoint matching (e.g. findByLocation)
            // and because HibernateSearch does not have any kind of query with just a geopoint
            // as argument we use a "geo distance" query with a distance of one meter.
            secondParameter = ".001km";
          }
        } else {
          secondParameter = parameters.next();
        }

        if (firstParameter instanceof Point && secondParameter instanceof Distance) {
          return criteria.within((Point) firstParameter, (Distance) secondParameter);
        }

        if (firstParameter instanceof String && secondParameter instanceof String) {
          return criteria.within((String) firstParameter, (String) secondParameter);
        }
      }
      case NEAR: {
        Object firstParameter = parameters.next();

        if (firstParameter instanceof Box) {
          return criteria.boundedBy((Box) firstParameter);
        }

        Object secondParameter = parameters.next();

        if (firstParameter instanceof Point && secondParameter instanceof Distance) {
          return criteria.within((Point) firstParameter, (Distance) secondParameter);
        }

        if (firstParameter instanceof String && secondParameter instanceof String) {
          return criteria.within((String) firstParameter, (String) secondParameter);
        }
      }

      default:
        throw new InvalidDataAccessApiUsageException("Illegal criteria found '" + type + "'.");
    }
  }

  private Object[] asArray(Object o) {
    if (o instanceof Collection) {
      return ((Collection<?>) o).toArray();
    } else if (o.getClass().isArray()) {
      return (Object[]) o;
    }
    return new Object[]{o};
  }
}
