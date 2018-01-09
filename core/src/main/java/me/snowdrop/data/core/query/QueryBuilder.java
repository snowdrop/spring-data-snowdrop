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

package me.snowdrop.data.core.query;

import java.util.Collection;

/**
 * @author Ales Justin
 */
public interface QueryBuilder<Q> {
    Q boost(Q query, Float boost);

    Q all(Collection<Q> subQueries);

    Q any(Collection<Q> subQueries);

    Q not(Q query);

    Q in(String fieldName, Collection<?> values);

    Q equal(String fieldName, Object value);

    Q isNull(String fieldName);

    Q isEmpty(String fieldName);

    Q greaterThan(String fieldName, Object value);

    Q greaterThanOrEqual(String fieldName, Object value);

    Q lessThan(String fieldName, Object value);

    Q lessThanOrEqual(String fieldName, Object value);

    Q between(String fieldName, Object min, Object max);

    Q fuzzy(String fieldName, Object value);

    Q contains(String fieldName, Object value);

    Q startsWith(String fieldName, Object value);

    Q endsWith(String fieldName, Object value);

    Q reqexp(String fieldName, String reqexp);

    Q spatial(String fieldName, double latitude, double longitude, double distance);
}
