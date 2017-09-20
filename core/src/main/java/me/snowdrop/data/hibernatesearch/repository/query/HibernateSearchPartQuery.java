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

package me.snowdrop.data.hibernatesearch.repository.query;

import me.snowdrop.data.hibernatesearch.core.HibernateSearchOperations;
import me.snowdrop.data.hibernatesearch.core.mapping.HibernateSearchPersistentProperty;
import me.snowdrop.data.hibernatesearch.core.query.CriteriaQuery;
import me.snowdrop.data.hibernatesearch.repository.query.parser.HibernateSearchQueryCreator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.parser.PartTree;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchPartQuery extends AbstractHibernateSearchRepositoryQuery {
  private final PartTree tree;
  private final MappingContext<?, HibernateSearchPersistentProperty> mappingContext;

  public HibernateSearchPartQuery(QueryMethod queryMethod, HibernateSearchOperations hibernateSearchOperations) {
    super(queryMethod, hibernateSearchOperations);
    this.tree = new PartTree(queryMethod.getName(), queryMethod.getEntityInformation().getJavaType());
    this.mappingContext = hibernateSearchOperations.getMappingContext();
  }

  @Override
  public Object execute(Object[] parameters) {
    Class<?> entityClass = getQueryMethod().getEntityInformation().getJavaType();
    ParametersParameterAccessor accessor = new ParametersParameterAccessor(getQueryMethod().getParameters(), parameters);
    CriteriaQuery query = new HibernateSearchQueryCreator(entityClass, tree, accessor, mappingContext).createQuery();

    Pageable pageable = accessor.getPageable();
    query.setPageable(pageable);
    query.setMaxResults(tree.getMaxResults());
    query.setDistinct(tree.isDistinct());

    if (getQueryMethod().isSliceQuery()) {
      return hibernateSearchOperations.findSlice(query);
    } else if (getQueryMethod().isPageQuery()) {
      return hibernateSearchOperations.findPageable(query);
    } else if (getQueryMethod().isStreamQuery()) {
      return hibernateSearchOperations.stream(query);
    } else if (getQueryMethod().isCollectionQuery()) {
      return hibernateSearchOperations.findAll(query);
    } else if (tree.isCountProjection()) {
      return hibernateSearchOperations.count(query);
    } else {
      return hibernateSearchOperations.findSingle(query);
    }
  }
}
