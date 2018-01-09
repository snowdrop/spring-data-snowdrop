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

import me.snowdrop.data.core.query.AbstractQueryAdapter;
import me.snowdrop.data.core.query.CriteriaConverter;
import me.snowdrop.data.core.query.QueryHelper;
import me.snowdrop.data.core.query.StringQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.spi.IndexedTypeIdentifier;
import org.hibernate.search.spi.SearchIntegrator;
import org.hibernate.search.spi.impl.PojoIndexedTypeIdentifier;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class LuceneQueryAdapter<T> extends AbstractQueryAdapter<T, Query, Sort> {
    private EntityMetadataContext emc;
    private QueryBuilder queryBuilder;

    protected void initialize(me.snowdrop.data.core.spi.Query<T> query) {
        Class<T> entityClass = query.getEntityClass();

        emc = new EntityMetadataContext(
            getSearchIntegrator().getIndexBinding(getIndexedTypeIdentifier(entityClass)),
            query.getTargetFields()
        );
        queryBuilder = getSearchIntegrator().buildQueryBuilder().forEntity(entityClass).get();

        super.initialize(query);
    }

    protected String getFieldName(String property) {
        return emc.getFieldName(property);
    }

    protected me.snowdrop.data.core.query.QueryBuilder<Query> createQueryBuilder() {
        return new LuceneQueryBuilder(queryBuilder);
    }

    protected QueryHelper<Query, Sort> createQueryHelper() {
        return new LuceneQueryHelper(emc, queryBuilder);
    }

    protected CriteriaConverter<Query> createCriteriaConverter() {
        return new LuceneCriteriaConverter(emc, createQueryBuilder());
    }

    protected IndexedTypeIdentifier getIndexedTypeIdentifier(Class<?> entityClass) {
        return PojoIndexedTypeIdentifier.convertFromLegacy(entityClass);
    }

    protected abstract SearchIntegrator getSearchIntegrator();

    protected void string(StringQuery<T> stringQuery) {
        try {
            String[] fields = stringQuery.getFields();
            IndexedTypeIdentifier iti = getIndexedTypeIdentifier(stringQuery.getEntityClass());
            Analyzer analyzer = getSearchIntegrator().getAnalyzer(iti);
            QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
            Query luceneQuery = parser.parse(stringQuery.getQuery());
            fillQuery(stringQuery, luceneQuery);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}