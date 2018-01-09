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

package me.snowdrop.data.core.ops;

import java.util.Optional;
import java.util.stream.Stream;

import me.snowdrop.data.core.mapping.SnowdropPersistentProperty;
import me.snowdrop.data.core.repository.support.SnowdropEntityInformation;
import me.snowdrop.data.core.spi.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.mapping.context.MappingContext;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface SnowdropOperations {

    /**
     * Get mapping context.
     * 
     * @return the mapping context
     */
    MappingContext<?, SnowdropPersistentProperty> getMappingContext();

    /**
     * Returns the number of entities available.
     *
     * @param query the query
     * @return the number of entities
     */
    <T> long count(Query<T> query);

    /**
     * Returns optinal single instance of the type.
     *
     * @param query the query
     * @return matching optional entity
     */
    <T> T findSingle(Query<T> query);

    /**
     * Returns all instances of the type.
     *
     * @param query the query
     * @return all entities
     */
    <T> Iterable<T> findAll(Query<T> query);

    /**
     * Returns a {@link Slice} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param query the query
     * @return a slice of entities
     */
    <T> Slice<T> findSlice(Query<T> query);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param query the query
     * @return a page of entities
     */
    <T> Page<T> findPageable(Query<T> query);

    /**
     * Returns a stream of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param query the query
     * @return a stream of entities
     */
    <T> Stream<T> stream(Query<T> query);

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return the saved entity will never be {@literal null}.
     */
    <S extends T, T, ID> S save(SnowdropEntityInformation<T, ID> ei, S entity);

    /**
     * Saves all given entities.
     *
     * @param entities must not be {@literal null}.
     * @return the saved entities will never be {@literal null}.
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    <S extends T, T, ID> Iterable<S> saveAll(SnowdropEntityInformation<T, ID> ei, Iterable<S> entities);

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal Optional#empty()} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}.
     */
    <T, ID> Optional<T> findById(SnowdropEntityInformation<T, ID> ei, ID id);

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@code id} is {@literal null}.
     */
    <T, ID> boolean existsById(SnowdropEntityInformation<T, ID> ei, ID id);

    /**
     * Returns all instances of the type with the given IDs.
     *
     * @param ids the ids
     * @return find all by ids
     */
    <T, ID> Iterable<T> findAllById(SnowdropEntityInformation<T, ID> ei, Iterable<ID> ids);

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
     */
    <T, ID> void deleteById(SnowdropEntityInformation<T, ID> ei, ID id);

    /**
     * Deletes a given entity.
     *
     * @param entity the entity
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    <S extends T, T, ID> void delete(SnowdropEntityInformation<T, ID> ei, S entity);

    /**
     * Deletes the given entities.
     *
     * @param entities the entities
     * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
     */
    <S extends T, T, ID> void deleteAll(SnowdropEntityInformation<T, ID> ei, Iterable<S> entities);

    /**
     * Deletes all entities managed by the repository.
     */
    <T, ID> void deleteAll(SnowdropEntityInformation<T, ID> ei);
}
