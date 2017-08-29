package org.jboss.data.hibernatesearch.smoke;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Indexed
public class SmokeEntity implements AbstractEntity {
  @Id
  @DocumentId
  private String id;
  @Field(store = Store.NO)
  private String name;
  @Field(store = Store.YES)
  private String type;
}
