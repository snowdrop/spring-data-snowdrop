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

package me.snowdrop.data.core.repository.query.parser;

import java.util.Collection;
import java.util.Iterator;

import me.snowdrop.data.core.mapping.SnowdropPersistentProperty;
import me.snowdrop.data.core.query.AndCriteria;
import me.snowdrop.data.core.query.Criteria;
import me.snowdrop.data.core.query.CriteriaQuery;
import me.snowdrop.data.core.repository.SnowdropRepository;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
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
@SuppressWarnings("unchecked")
public class SnowdropQueryCreator extends AbstractQueryCreator<CriteriaQuery<?>, CriteriaQuery<?>> {
    private static Class<?> COORDINATES_CLASS;

    static {
        try {
            ClassLoader classLoader = SnowdropRepository.class.getClassLoader();
            COORDINATES_CLASS = classLoader.loadClass("org.hibernate.search.spatial.Coordinates");
        } catch (Exception ignored) {
        }
    }

    private final Class<?> entityClass;
    private final MappingContext<?, SnowdropPersistentProperty> context;

    public SnowdropQueryCreator(Class<?> entityClass, PartTree tree, ParameterAccessor parameters, MappingContext<?, SnowdropPersistentProperty> context) {
        super(tree, parameters);
        this.entityClass = entityClass;
        this.context = context;
    }

    @Override
    protected CriteriaQuery<?> create(Part part, Iterator<Object> iterator) {
        PersistentPropertyPath<SnowdropPersistentProperty> path = context.getPersistentPropertyPath(part.getProperty());
        return new CriteriaQuery(entityClass, from(part, new AndCriteria(path.toDotPath(SnowdropPersistentProperty.PropertyToFieldNameConverter.INSTANCE)), iterator));
    }

    @Override
    protected CriteriaQuery<?> and(Part part, CriteriaQuery<?> base, Iterator<Object> iterator) {
        if (base == null) {
            return create(part, iterator);
        }
        PersistentPropertyPath<SnowdropPersistentProperty> path = context.getPersistentPropertyPath(part.getProperty());
        return base.addCriteria(from(part, new AndCriteria(path.toDotPath(SnowdropPersistentProperty.PropertyToFieldNameConverter.INSTANCE)), iterator));
    }

    @Override
    protected CriteriaQuery<?> or(CriteriaQuery<?> base, CriteriaQuery<?> query) {
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

    private Criteria from(Part part, Criteria criteria, Iterator<?> parameters) {
        Part.Type type = part.getType();

        switch (type) {
            case IS_NULL:
                return criteria.isNull();
            case IS_NOT_NULL:
                return criteria.isNull().not();
            case IS_EMPTY:
                return criteria.isEmpty();
            case IS_NOT_EMPTY:
                return criteria.isEmpty().not();
            case TRUE:
                return criteria.is(true);
            case FALSE:
                return criteria.is(false);
            case NEGATING_SIMPLE_PROPERTY:
                return criteria.is(parameters.next()).not();
            case REGEX:
                return criteria.regexp(parameters.next().toString());
            case LIKE:
            case STARTING_WITH:
                return criteria.startsWith(parameters.next().toString());
            case NOT_LIKE:
                return criteria.startsWith(parameters.next().toString()).not();
            case ENDING_WITH:
                return criteria.endsWith(parameters.next().toString());
            case CONTAINING:
                return criteria.contains(parameters.next().toString());
            case NOT_CONTAINING:
                return criteria.contains(parameters.next().toString()).not();
            case AFTER:
            case GREATER_THAN:
                return criteria.greaterThan(parameters.next());
            case GREATER_THAN_EQUAL:
                return criteria.greaterThanEqual(parameters.next());
            case BEFORE:
            case LESS_THAN:
                return criteria.lessThan(parameters.next());
            case LESS_THAN_EQUAL:
                return criteria.lessThanEqual(parameters.next());
            case BETWEEN:
                return criteria.between(parameters.next(), parameters.next());
            case IN:
                return criteria.in(asArray(parameters.next()));
            case NOT_IN:
                return criteria.in(asArray(parameters.next())).not();
            case SIMPLE_PROPERTY:
                return criteria.is(parameters.next());
            case WITHIN: {
                Number latitude;
                Number longitude;

                Object first = parameters.next();

                Double[] values = readCoordinates(first);
                if (values != null) {
                    latitude = values[0];
                    longitude = values[1];
                } else if (first instanceof Number) {
                    latitude = (Number) first;
                    Object second = parameters.next();
                    if (!(second instanceof Number)) {
                        throw new IllegalArgumentException("Illegal second parameter type: " + second);
                    }
                    longitude = (Number) second;
                } else {
                    throw new IllegalArgumentException("Illegal first parameter type: " + first);
                }

                Object distance = new Distance(0.001, Metrics.KILOMETERS); // 1m accurate
                if (parameters.hasNext()) {
                    distance = parameters.next();
                    if (distance instanceof Number) {
                        distance = new Distance(Number.class.cast(distance).doubleValue(), Metrics.KILOMETERS);
                    }
                }
                if (distance instanceof Distance) {
                    return criteria.within(latitude.doubleValue(), longitude.doubleValue(), (Distance) distance);
                }
            }
            case NEAR: {
                // TODO
            }

            default:
                throw new InvalidDataAccessApiUsageException("Illegal criteria found '" + type + "'.");
        }
    }

    private static Double[] readCoordinates(Object parameter) {
        if (COORDINATES_CLASS != null && COORDINATES_CLASS.isInstance(parameter)) {
            try {
                Double[] values = new Double[2];
                values[0] = (Double) COORDINATES_CLASS.getMethod("getLatitude").invoke(parameter);
                values[1] = (Double) COORDINATES_CLASS.getMethod("getLongitude").invoke(parameter);
                return values;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
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
