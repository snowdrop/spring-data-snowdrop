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

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

/**
 * @author Ales Justin
 */
public class OrderConverter {

  public Sort convert(org.springframework.data.domain.Sort sort) {
    return new Sort(toArray(convertToSortFields(sort)));
  }

  private SortField[] toArray(List<SortField> sortFields) {
    return sortFields.toArray(new SortField[sortFields.size()]);
  }

  private List<SortField> convertToSortFields(org.springframework.data.domain.Sort sort) {
    List<SortField> sortFields = new ArrayList<>();
    for (org.springframework.data.domain.Sort.Order order : sort) {
      sortFields.add(convertToSortField(order));
    }
    return sortFields;
  }

  private SortField convertToSortField(org.springframework.data.domain.Sort.Order order) {
    boolean reverse = order.getDirection() == org.springframework.data.domain.Sort.Direction.DESC;
    return new SortField(order.getProperty(), SortField.Type.STRING, reverse);   // TODO: find appropriate SortField type
  }
}
