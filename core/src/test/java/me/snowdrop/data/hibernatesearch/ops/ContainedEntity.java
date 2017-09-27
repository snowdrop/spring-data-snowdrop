/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package me.snowdrop.data.hibernatesearch.ops;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.snowdrop.data.hibernatesearch.AbstractEntity;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.springframework.data.annotation.Id;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "contained")
public class ContainedEntity implements AbstractEntity {
  @Id
  @DocumentId
  @javax.persistence.Id
  @GeneratedValue
  private Long id;
  @Field(name = "containedName")
  private String name;
  @Field(name = "numberAsText", bridge = @FieldBridge(impl = IntegerBridge.class))
  private Integer number;
}
