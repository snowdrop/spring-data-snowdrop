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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.DatastoreReaderWriter;
import com.google.cloud.datastore.KeyFactory;
import org.springframework.core.NamedThreadLocal;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class DatastoreUtils {
    private static final ThreadLocal<DatastoreReaderWriter> DRW = new NamedThreadLocal<>("GCD Datastore Reader Writer");
    private static Datastore datastore;

    public static String fetchProjectId() {
        return ProjectIdProviders.projectId();
    }

    /**
     * Should be used to push GCD Transaction as DatastoreReaderWriter.
     */
    public static void setDatastoreReaderWriter(DatastoreReaderWriter drw) {
        DRW.set(drw);
    }

    public static void clearDatastoreReaderWriter() {
        DRW.remove();
    }

    private static Datastore fetchDatastore() {
        if (datastore == null) {
            synchronized (DatastoreUtils.class) {
                if (datastore == null) {
                    DatastoreOptions.Builder builder = DatastoreOptions.newBuilder();
                    builder.setProjectId(fetchProjectId());
                    DatastoreOptions options = builder.build();
                    datastore = options.getService();
                }
            }
        }
        return datastore;
    }

    public static DatastoreReaderWriter getDatastoreReaderWriter() {
        DatastoreReaderWriter drw = DRW.get();
        return drw != null ? drw : fetchDatastore();
    }

    public static KeyFactory newKeyFactory() {
        return fetchDatastore().newKeyFactory();
    }
}
