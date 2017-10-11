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
package me.snowdrop.data.repository.extension.support;

import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class ExtendingRepositoryFactorySupport extends RepositoryFactorySupport {

  public RepositoryFragments getExtensionFragments(RepositoryMetadata metadata) {
    Class<?> repositoryInterface = metadata.getRepositoryInterface();
    List<RepositoryFragment<?>> result = new ArrayList<>();

    Class<?> extensionInterface = getRepositoryExtensionInterface();

    for (Class<?> extendedInterface : repositoryInterface.getInterfaces()) {
      if (extensionInterface.isAssignableFrom(extendedInterface)) {
        result.add(createExtensionFragment(extendedInterface));
      }
    }

    if (result.isEmpty()) {
      return RepositoryFragments.empty();
    }
    else {
      return RepositoryFragments.from(result);
    }
  }

  @Override
  protected final RepositoryMetadata getRepositoryMetadata(Class<?> interfaze) {
    if (getRepositoryExtensionInterface().isAssignableFrom(interfaze)) {
      return getRepositoryExtensionMetadata(interfaze);
    }
    else {
      return getMainRepositoryMetadata(interfaze);
    }
  }

  protected abstract Class<?> getRepositoryExtensionInterface();

  protected RepositoryMetadata getMainRepositoryMetadata(Class<?> repositoryInterface) {
    return super.getRepositoryMetadata(repositoryInterface);
  }

  protected RepositoryMetadata getRepositoryExtensionMetadata(Class<?> extensionInterface) {
    return new DefaultRepositoryExtensionMetadata(extensionInterface);
  }

  private <T> RepositoryFragment<T> createExtensionFragment(Class<T> fragmentInterface) {
    T repository = getRepository(fragmentInterface);
    return RepositoryFragment.implemented(fragmentInterface, repository);
  }

}
