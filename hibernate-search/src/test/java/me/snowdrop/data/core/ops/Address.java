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

package me.snowdrop.data.core.ops;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.search.annotations.Field;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoMessage;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Embeddable
@ProtoMessage(name = "Address")
public class Address implements Serializable {
  @Field
  @ProtoField(number = 10, required = true)
  public int zipcode;

  @Field
  @ProtoField(number = 20)
  public String city;

  @Field
  @ProtoField(number = 30)
  public String country;
}
