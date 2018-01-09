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

package me.snowdrop.data.infinispan.remote;

import me.snowdrop.data.core.query.QueryHelper;
import org.infinispan.query.dsl.Expression;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryBuilder;
import org.infinispan.query.dsl.SortOrder;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class InfinispanQueryHelper implements QueryHelper<Query, Void> {
    private final QueryBuilder queryBuilder;

    public InfinispanQueryHelper(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    @Override
    public Void convert(Sort sort) {
        for (Sort.Order order : sort) {
            queryBuilder.orderBy(
                Expression.property(order.getProperty()),
                order.isAscending() ? SortOrder.ASC : SortOrder.DESC
            );
        }
        return null;
    }

    public Query matchAll() {
        return queryBuilder.build();
    }
}
