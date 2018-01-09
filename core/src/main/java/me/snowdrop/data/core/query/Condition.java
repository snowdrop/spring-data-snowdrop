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
public class Condition {
    private final Property property;
    private final OperationKey key;
    private final Object value;

    private boolean negating;
    private Float boost = Float.NaN;

    Condition(Property property, OperationKey key) {
        this(property, key, null);
    }

    Condition(Property property, OperationKey key, Object value) {
        this.property = property;
        this.key = key;
        this.value = value;
    }

    public Property getProperty() {
        return property;
    }

    public boolean isNegating() {
        return negating;
    }

    public void setNegating(boolean negating) {
        this.negating = negating;
    }

    public Float getBoost() {
        return boost;
    }

    public void setBoost(Float boost) {
        this.boost = boost;
    }

    public OperationKey getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Condition{" +
            "property=" + property +
            ", key=" + key +
            ", value=" + value +
            '}';
    }

}
