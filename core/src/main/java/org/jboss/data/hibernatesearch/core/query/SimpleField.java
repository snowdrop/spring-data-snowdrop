package org.jboss.data.hibernatesearch.core.query;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SimpleField implements Field {
  private final String name;

  public SimpleField(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
