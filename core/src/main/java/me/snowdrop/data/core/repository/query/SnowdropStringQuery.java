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

package me.snowdrop.data.core.repository.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.snowdrop.data.core.ops.SnowdropOperations;
import me.snowdrop.data.core.query.BaseQuery;
import me.snowdrop.data.core.query.StringQuery;
import me.snowdrop.data.core.spi.Query;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.format.support.DefaultFormattingConversionService;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SnowdropStringQuery extends AbstractSnowdropRepositoryQuery {
  private static final Pattern PARAMETER_PLACEHOLDER = Pattern.compile("\\?(\\d+)");
  private final String query;

  public SnowdropStringQuery(SnowdropQueryMethod queryMethod, SnowdropOperations snowdropOperations, String queryString) {
    super(queryMethod, snowdropOperations);
    this.query = queryString;
  }

  private final GenericConversionService conversionService = new DefaultFormattingConversionService();

  protected boolean isModify(Query<?> query) {
    return false;
  }

  protected boolean isExistsProjection(Query<?> query) {
    return false;
  }

  protected boolean isCountProjection(Query<?> query) {
    return false; // TODO Lucene count string query?
  }

  protected BaseQuery<?> createQuery(ParametersParameterAccessor parameterAccessor) {
    String queryString = replacePlaceholders(this.query, parameterAccessor);
    Class<?> entityClass = getQueryMethod().getEntityInformation().getJavaType();
    return new StringQuery<>(entityClass, queryString);
  }

  private String replacePlaceholders(String input, ParametersParameterAccessor accessor) {
    Matcher matcher = PARAMETER_PLACEHOLDER.matcher(input);
    String result = input;
    while (matcher.find()) {
      String group = matcher.group();
      int index = Integer.parseInt(matcher.group(1));
      result = result.replace(group, getParameterWithIndex(accessor, index));
    }
    return result;
  }

  private String getParameterWithIndex(ParametersParameterAccessor accessor, int index) {
    Object parameter = accessor.getBindableValue(index);
    if (parameter == null) {
      return "null";
    }
    if (conversionService.canConvert(parameter.getClass(), String.class)) {
      return conversionService.convert(parameter, String.class);
    }
    return parameter.toString();
  }
}
