/*
 * Copyright 2018 Red Hat, Inc, and individual contributors.
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

package me.snowdrop.data.core.smoke;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.snowdrop.data.core.AbstractEntity;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.Store;
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
public class SmokeEntity implements AbstractEntity<String> {
  @Id
  @DocumentId
  private String id;
  @Field(store = Store.NO)
  @SortableField
  private String name;
  @Field(store = Store.YES)
  @SortableField
  private String type;
}
