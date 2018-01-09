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
package me.snowdrop.data.core.config.jpa.standalone.ops.repository.sd;

import me.snowdrop.data.core.annotations.TargetField;
import me.snowdrop.data.core.ops.Ops;
import me.snowdrop.data.core.ops.SimpleEntity;
import me.snowdrop.data.core.repository.SnowdropRepository;
import org.springframework.data.domain.Sort;

public interface StandaloneJpaSnowdropOpsRepository
        extends SnowdropRepository<SimpleEntity, Long>, Ops {

    @TargetField(property = "name", field = "identity.name")
    Iterable<SimpleEntity> findAll(Sort sort);
}
