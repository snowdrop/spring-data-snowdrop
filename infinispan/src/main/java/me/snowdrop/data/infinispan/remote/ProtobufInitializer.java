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

import java.util.Objects;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ProtobufInitializer {
    private static final Logger log = LoggerFactory.getLogger(ProtobufInitializer.class);

    private final RemoteCacheManager remoteCacheManager;

    private boolean failIfError = true;
    private Throwable startError;
    private String fileName;
    private ProtoSchemaBuilder builder;

    public ProtobufInitializer(RemoteCacheManager remoteCacheManager, String fileName) {
        Objects.requireNonNull(remoteCacheManager);
        Objects.requireNonNull(fileName);
        this.remoteCacheManager = remoteCacheManager;
        this.builder = new ProtoSchemaBuilder();
        this.fileName = fileName;
        this.builder.fileName(fileName);
    }

    public ProtobufInitializer setFailIfError(boolean failIfError) {
        this.failIfError = failIfError;
        return this;
    }

    public ProtobufInitializer addClasses(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            builder.addClass(clazz);
        }
        return this;
    }

    public void start() throws Exception {
        try {
            startInternal();
        } catch (Throwable t) {
            log.warn("Failed to start: " + t);
            startError = t;
            if (failIfError) {
                throw t;
            }
        }
    }

    protected void startInternal() throws Exception {
        SerializationContext serializationContext = ProtoStreamMarshaller.getSerializationContext(remoteCacheManager);
        String protoFile = builder.build(serializationContext);

        //initialize server-side serialization context
        RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
        metadataCache.put(fileName, protoFile);

        Object error = metadataCache.get(fileName + ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
        if (error != null) {
            throw new IllegalStateException("Protobuf metadata failed: " + error);
        }
    }

    public void stop() {
        if (startError == null) {
            RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
            metadataCache.remove(fileName);
        }
    }

}
