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

import org.infinispan.query.dsl.FilterConditionBeginContext;
import org.infinispan.query.dsl.FilterConditionContext;
import org.infinispan.query.dsl.FilterConditionContextQueryBuilder;

/**
 * Hack around matchAll.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
final class DummyFilterConditionContext implements FilterConditionContext {
    public static final FilterConditionContext INSTANCE = new DummyFilterConditionContext();

    private DummyFilterConditionContext() {
    }

    @Override
    public FilterConditionBeginContext and() {
        return null;
    }

    @Override
    public FilterConditionContextQueryBuilder and(FilterConditionContext rightCondition) {
        return null;
    }

    @Override
    public FilterConditionBeginContext or() {
        return null;
    }

    @Override
    public FilterConditionContextQueryBuilder or(FilterConditionContext rightCondition) {
        return null;
    }

    @Override
    public org.infinispan.query.dsl.QueryBuilder toBuilder() {
        return null;
    }
}
