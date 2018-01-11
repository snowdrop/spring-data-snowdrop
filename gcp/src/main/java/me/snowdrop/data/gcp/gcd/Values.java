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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyValue;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class Values {
    public static Value toValue(Object value) {
        if (value == null) {
            return new NullValue();
        } else if (value instanceof Long) {
            return new LongValue((Long) value);
        } else if (value instanceof Double) {
            return new DoubleValue((Double) value);
        } else if (value instanceof Boolean) {
            return new BooleanValue((Boolean) value);
        } else if (value instanceof Key) {
            return new KeyValue((Key) value);
        } else if (value instanceof Entity) {
            return new EntityValue((Entity) value);
        } else if (value instanceof String) {
            return new StringValue((String) value);
        } else if (value instanceof Collection) {
            //noinspection unchecked
            return new ListValue(new ArrayList<>((Collection) value));
        } else if (value instanceof Date) {
            return new TimestampValue(Timestamp.of((Date) value));
        } else {
            throw new IllegalArgumentException("Value type is not supported: " + value);
        }
    }
}
