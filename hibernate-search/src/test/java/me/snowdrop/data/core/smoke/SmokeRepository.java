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

package me.snowdrop.data.core.smoke;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import me.snowdrop.data.core.annotations.Query;
import me.snowdrop.data.core.repository.SnowdropRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface SmokeRepository extends SnowdropRepository<SmokeEntity, String> {
  List<SmokeEntity> findByType(String type);

  SmokeEntity findByName(String name);

  List<SmokeEntity> findByNameAndType(String name, String type);

  List<SmokeEntity> findByNameOrType(String name, String type);

  @Query("(+type:?0)")
  List<SmokeEntity> findByTypeQuery(String type);

  List<SmokeEntity> findByNameViaNamedQuery(String name);

  Stream<SmokeEntity> findByTypeIn(Collection<String> types);

  @Async
  Future<List<SmokeEntity>> findByTypeAfter(String after);

  Optional<SmokeEntity> findByNameBefore(String name);

  boolean existsByType(String type);

  Set<SmokeEntity> findByNameAfter(String name);

  void deleteByName(String name);
}
