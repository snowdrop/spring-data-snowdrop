package org.jboss.data.hibernatesearch.repository.query;

import org.jboss.data.hibernatesearch.core.HibernateSearchOperations;
import org.jboss.data.hibernatesearch.core.mapping.HibernateSearchPersistentProperty;
import org.jboss.data.hibernatesearch.core.query.CriteriaQuery;
import org.jboss.data.hibernatesearch.repository.query.parser.HibernateSearchQueryCreator;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.util.StreamUtils;

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

    if (getQueryMethod().isPageQuery()) {
      query.setPageable(accessor.getPageable());
      return hibernateSearchOperations.findPageable(query);
    } else if (getQueryMethod().isStreamQuery()) {
      return StreamUtils.createStreamFromIterator(hibernateSearchOperations.stream(query));
    } else if (getQueryMethod().isCollectionQuery()) {
      query.setPageable(accessor.getPageable());
      return hibernateSearchOperations.findAll(query);
    } else if (tree.isCountProjection()) {
      return hibernateSearchOperations.count(query);
    } else {
      return hibernateSearchOperations.findSingle(query);
    }
  }
}
