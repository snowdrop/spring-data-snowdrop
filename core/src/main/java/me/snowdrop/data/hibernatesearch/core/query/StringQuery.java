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

package me.snowdrop.data.hibernatesearch.core.query;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class StringQuery<T> extends BaseQuery<T> {
  private final String query;

  public StringQuery(Class<T> entityClass, String query) {
    super(entityClass);
    this.query = query;
  }

  @Override
  void apply(AbstractQueryAdapter<T, ?, ?> adapter) {
    adapter.string(this);
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
