/*
 * Copyright 2018 Red Hat, Inc, and individual contributors.
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

package me.snowdrop.data.gcp.gcd;

import java.util.Iterator;
import java.util.List;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.StructuredQuery;
import me.snowdrop.data.core.query.Projection;
import me.snowdrop.data.gcp.EntityToModelMapper;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface QueryHandle {
    <T> List<T> toList(Class<T> entityClass, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> mapper);

    <T> Iterator<T> toIterator(Class<T> entityClass, EntityToModelMapper<Entity, FullEntity<IncompleteKey>> mapper);

    void setFilter(StructuredQuery.Filter filter);

    void sort(Sort sort);

    void setFirstResult(long firstResult);

    void setMaxResults(long maxResults);

    void setProjections(Projection[] projections);
}
