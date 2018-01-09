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

/**
 * @author Ales Justin
 */
public class OrCriteria<Q> extends Criteria<Q> {
    private Criteria<Q> left;
    private Criteria<Q> right;

    public OrCriteria(Criteria<Q> left, Criteria<Q> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Q apply(OpsCriteriaConverter<Q> converter) {
        return converter.or(this);
    }

    public Criteria<Q> getLeft() {
        return left;
    }

    public Criteria<Q> getRight() {
        return right;
    }
}
