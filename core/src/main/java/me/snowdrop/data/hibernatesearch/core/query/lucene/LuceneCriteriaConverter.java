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

package me.snowdrop.data.hibernatesearch.core.query.lucene;

import me.snowdrop.data.hibernatesearch.core.query.AbstractCriteriaConverter;
import me.snowdrop.data.hibernatesearch.core.query.Property;
import me.snowdrop.data.hibernatesearch.core.query.QueryBuilder;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

/**
 * Converts Criteria to Lucene queries
 *
 * @author Ales Justin
 */
public class LuceneCriteriaConverter extends AbstractCriteriaConverter<Query, Sort> {

    private final EntityMetadataContext entityMetadataContext;

    public LuceneCriteriaConverter(EntityMetadataContext entityMetadataContext, QueryBuilder<Query, Sort> queryBuilder) {
        super(queryBuilder);
        this.entityMetadataContext = entityMetadataContext;
    }

    @Override
    protected String getFieldName(Property property) {
        return entityMetadataContext.getFieldName(property.getName());
    }
}
