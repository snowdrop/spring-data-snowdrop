package me.snowdrop.data.hibernatesearch.core.query;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class StringQuery extends AbstractQuery {
  private final String query;

  public StringQuery(Class<?> entityClass, String query) {
    super(entityClass);
    this.query = query;
  }

  public String getQuery() {
    return query;
  }

  public String[] getFields() {
    Set<String> fields = new LinkedHashSet<>();
    int p = 0;
    while ((p = query.indexOf(':', p)) != -1) {
      fields.add(parseField(p - 1));
      p++;
    }
    return fields.toArray(new String[fields.size()]);
  }

  private String parseField(int p) {
    StringBuilder field = new StringBuilder();
    while (p >= 0 && Character.isLetterOrDigit(query.charAt(p))) {
      field.insert(0, query.charAt(p));
      p--;
    }
    return field.toString();
  }
}
