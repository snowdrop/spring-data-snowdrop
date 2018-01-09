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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import me.snowdrop.data.core.mapping.SnowdropPersistentProperty;
import me.snowdrop.data.core.mapping.SimpleSnowdropMappingContext;
import me.snowdrop.data.core.query.BaseQuery;
import me.snowdrop.data.core.repository.support.SnowdropEntityInformation;
import me.snowdrop.data.core.spi.CrudAdapter;
import me.snowdrop.data.core.spi.DatasourceMapper;
import me.snowdrop.data.core.spi.Query;
import me.snowdrop.data.core.spi.QueryAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.mapping.context.MappingContext;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SnowdropTemplate implements SnowdropOperations {
    private final DatasourceMapper datasourceMapper;
    private MappingContext<?, SnowdropPersistentProperty> mappingContext;

    public SnowdropTemplate(DatasourceMapper datasourceMapper) {
        this.datasourceMapper = datasourceMapper;
    }

    private <T> List<T> findAllInternal(Query<T> query) {
        QueryAdapter<T> queryAdapter = createQueryAdapter(query);
        return queryAdapter.list(query);
    }

    @Override
    public synchronized MappingContext<?, SnowdropPersistentProperty> getMappingContext() {
        if (mappingContext == null) {
            mappingContext = new SimpleSnowdropMappingContext<>();
        }
        return mappingContext;
    }

    private <T> QueryAdapter<T> createQueryAdapter(Query<T> query) {
        return datasourceMapper.createQueryAdapter(query.getEntityClass());
    }

    private <T, ID> CrudAdapter<T, ID> createCrudAdapter(SnowdropEntityInformation<T, ID> ei) {
        return datasourceMapper.createCrudAdapter(ei);
    }

    private <T> long total(Query<T> query) {
        return count(new BaseQuery<>(query.getEntityClass()));
    }

    @Override
    public <T> long count(Query<T> countQuery) {
        QueryAdapter<T> queryAdapter = createQueryAdapter(countQuery);
        return queryAdapter.size(countQuery);
    }

    @Override
    public <T> T findSingle(Query<T> query) {
        QueryAdapter<T> queryAdapter = createQueryAdapter(query);
        return queryAdapter.single(query);
    }

    @Override
    public <T> Iterable<T> findAll(Query<T> allQuery) {
        return findAllInternal(allQuery);
    }

    @Override
    public <T> Slice<T> findSlice(Query<T> query) {
        List<T> list = findAllInternal(query);
        Pageable pageable = query.getPageable();
        if (pageable != null) {
            boolean hasNext = ((pageable.getPageNumber() + 1) * pageable.getPageSize() < total(query));
            return new SliceImpl<T>(list, query.getPageable(), hasNext);
        } else {
            return new SliceImpl<T>(list);
        }
    }

    @Override
    public <T> Page<T> findPageable(Query<T> query) {
        List<T> list = findAllInternal(query);
        Pageable pageable = query.getPageable();
        if (pageable != null) {
            return new PageImpl<T>(list, query.getPageable(), total(query));
        } else {
            return new PageImpl<T>(list);
        }
    }

    @Override
    public <T> Stream<T> stream(Query<T> query) {
        QueryAdapter<T> queryAdapter = createQueryAdapter(query);
        return queryAdapter.stream(query);
    }

    @Override
    public <S extends T, T, ID> S save(SnowdropEntityInformation<T, ID> ei, S entity) {
        CrudAdapter<T, ID> crudAdapter = createCrudAdapter(ei);
        return crudAdapter.save(entity);
    }

    @Override
    public <S extends T, T, ID> Iterable<S> saveAll(SnowdropEntityInformation<T, ID> ei, Iterable<S> entities) {
        CrudAdapter<T, ID> crudAdapter = createCrudAdapter(ei);
        return crudAdapter.saveAll(entities);
    }

    @Override
    public <T, ID> Optional<T> findById(SnowdropEntityInformation<T, ID> ei, ID id) {
        CrudAdapter<T, ID> crudAdapter = createCrudAdapter(ei);
        return crudAdapter.findById(id);
    }

    @Override
    public <T, ID> boolean existsById(SnowdropEntityInformation<T, ID> ei, ID id) {
        CrudAdapter<T, ID> crudAdapter = createCrudAdapter(ei);
        return crudAdapter.existsById(id);
    }

    @Override
    public <T, ID> Iterable<T> findAllById(SnowdropEntityInformation<T, ID> ei, Iterable<ID> ids) {
        CrudAdapter<T, ID> crudAdapter = createCrudAdapter(ei);
        return crudAdapter.findAllById(ids);
    }

    @Override
    public <T, ID> void deleteById(SnowdropEntityInformation<T, ID> ei, ID id) {
        CrudAdapter<T, ID> crudAdapter = createCrudAdapter(ei);
        crudAdapter.deleteById(id);
    }

    @Override
    public <S extends T, T, ID> void delete(SnowdropEntityInformation<T, ID> ei, S entity) {
        CrudAdapter<T, ID> crudAdapter = createCrudAdapter(ei);
        crudAdapter.delete(entity);
    }

    @Override
    public <S extends T, T, ID> void deleteAll(SnowdropEntityInformation<T, ID> ei, Iterable<S> entities) {
        CrudAdapter<T, ID> crudAdapter = createCrudAdapter(ei);
        crudAdapter.deleteAll(entities);
    }

    @Override
    public <T, ID> void deleteAll(SnowdropEntityInformation<T, ID> ei) {
        CrudAdapter<T, ID> crudAdapter = createCrudAdapter(ei);
        crudAdapter.deleteAll();
    }
}
