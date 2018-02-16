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

import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.springframework.cloud.gcp.core.DefaultGcpProjectIdProvider;
import org.springframework.cloud.gcp.core.GcpProjectIdProvider;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class ProjectIdProviders {
    private static final Logger log = Logger.getLogger(ProjectIdProviders.class.getName());
    private static String projectId;

    static String projectId() {
        if (projectId == null) {
            synchronized (ProjectIdProviders.class) {
                if (projectId == null) {
                    projectId = getProjectId();
                    log.info("Using projectId: " + projectId);
                }
            }
        }
        return projectId;
    }

    private static String getProjectId() {
        ServiceLoader<GcpProjectIdProvider> loader = ServiceLoader.load(GcpProjectIdProvider.class);
        for (GcpProjectIdProvider provider : loader) {
            return provider.getProjectId();
        }
        GcpProjectIdProvider provider = new DefaultGcpProjectIdProvider();
        return provider.getProjectId();
    }
}
