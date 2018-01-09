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

package me.snowdrop.data.core.query.lucene;

import me.snowdrop.data.core.query.QueryHelper;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.sort.SortContext;
import org.hibernate.search.query.dsl.sort.SortFieldContext;

/**
 * Converts Criteria to Lucene queries
 *
 * @author Ales Justin
 */
public class LuceneQueryHelper implements QueryHelper<Query, Sort> {
    private final EntityMetadataContext entityMetadataContext;
    private final QueryBuilder queryBuilder;

    public LuceneQueryHelper(EntityMetadataContext entityMetadataContext, QueryBuilder queryBuilder) {
        this.entityMetadataContext = entityMetadataContext;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public Sort convert(org.springframework.data.domain.Sort sort) {
        SortContext context = queryBuilder.sort();
        SortFieldContext currentContext = null;
        for (org.springframework.data.domain.Sort.Order order : sort) {
            String fieldName = entityMetadataContext.getFieldName(order.getProperty());
            if (currentContext == null) {
                currentContext = context.byField(fieldName);
            } else {
                currentContext = currentContext.andByField(fieldName);
            }
            org.springframework.data.domain.Sort.NullHandling nullHandling = order.getNullHandling();
            if (nullHandling == org.springframework.data.domain.Sort.NullHandling.NULLS_FIRST) {
                currentContext = currentContext.onMissingValue().sortFirst();
            } else if (nullHandling == org.springframework.data.domain.Sort.NullHandling.NULLS_LAST) {
                currentContext = currentContext.onMissingValue().sortLast();
            }
            boolean desc = (order.getDirection() == org.springframework.data.domain.Sort.Direction.DESC);
            if (desc) {
                currentContext = currentContext.desc();
            } else {
                currentContext = currentContext.asc();
            }
        }
        return (currentContext != null) ? currentContext.createSort() : null;
    }

    @Override
    public Query matchAll() {
        return queryBuilder.all().createQuery();
    }
}
