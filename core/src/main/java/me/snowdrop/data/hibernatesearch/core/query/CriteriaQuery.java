package me.snowdrop.data.hibernatesearch.core.query;

import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CriteriaQuery extends AbstractQuery {

  private Criteria criteria;

  private CriteriaQuery(Class<?> entityClass) {
    super(entityClass);
  }

  public CriteriaQuery(Class<?> entityClass, Criteria criteria) {
    this(entityClass, criteria, null);
  }

  public CriteriaQuery(Class<?> entityClass, Criteria criteria, Pageable pageable) {
    super(entityClass);
    Assert.notNull(criteria, "Criteria must not be null!");
    this.criteria = criteria;
    if (pageable != null) {
      setPageable(pageable);
      setSort(pageable.getSort());
    }
  }

  public static final Query fromQuery(CriteriaQuery source) {
    return fromQuery(source, new CriteriaQuery(source.getEntityClass()));
  }

  public static <T extends CriteriaQuery> T fromQuery(CriteriaQuery source, T destination) {
    if (source == null || destination == null) {
      return null;
    }

    if (source.getCriteria() != null) {
      destination.addCriteria(source.getCriteria());
    }

    if (source.getSort() != null) {
      destination.setSort(source.getSort());
    }

    return destination;
  }

  @SuppressWarnings("unchecked")
  public final <T extends CriteriaQuery> T addCriteria(Criteria criteria) {
    Assert.notNull(criteria, "Cannot add null criteria.");
    if (this.criteria == null) {
      this.criteria = criteria;
    } else {
      this.criteria.and(criteria);
    }
    return (T) this;
  }

  public Criteria getCriteria() {
    return criteria;
  }
}
