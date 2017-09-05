package org.jboss.data.hibernatesearch.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.jboss.data.hibernatesearch.AbstractEntity;
import org.springframework.data.annotation.Id;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Indexed
public class SimpleEntity implements AbstractEntity {
  @Id
  @DocumentId
  private Long id;
  @Field(store = Store.NO)
  private String name;
  @Field(store = Store.NO)
  private String text;
  @Field(store = Store.NO)
  private int number;
  @Field(store = Store.NO)
  private boolean buul;
  @Field(store = Store.NO)
  private String hero;
  @Field(store = Store.NO)
  private String color;
}
