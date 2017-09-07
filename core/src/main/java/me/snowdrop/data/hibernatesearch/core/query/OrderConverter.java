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
