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

package me.snowdrop.data.hibernatesearch.ops;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
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
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.Spatial;
import org.hibernate.search.annotations.Store;
import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoMessage;
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
@Entity
@Table(name = "simple")
@ProtoDoc("@Indexed")
@ProtoMessage(name = "SimpleEntity")
public class SimpleEntity implements AbstractEntity<Long> {
    @Id
    @DocumentId
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ProtoDoc("@Field(index = Index.NO, store=Store.NO)")
    @ProtoField(number = 1, required = true)
    public Long id;

    @Field(store = Store.NO)
    @SortableField(forField = "identity.name")
    @Field(name = "identity.name")
    @Field(name = "bridge", bridge = @FieldBridge(impl = MyCustomFieldBridge.class))
    @ProtoDoc("@Field @SortableField")
    @ProtoField(number = 2)
    public String name;

    @Field(store = Store.NO)
    @ProtoDoc("@Field(store=Store.NO)")  // TODO add , analyze=Analyze.YES
    @ProtoField(number = 3)
    public String text;

    @Field(store = Store.NO)
    @Field(name = "var")
    @ProtoDoc("@Field(store=Store.NO)")
    @ProtoField(number = 4, defaultValue = "0")
    public int number;

    @Field(name = "boool", store = Store.NO)
    @ProtoDoc("@Field(store=Store.NO)")
    @ProtoField(number = 5, defaultValue = "false")
    public boolean buul;

    @Field(store = Store.NO)
    @SortableField
    @ProtoDoc("@Field(store=Store.NO) @SortableField") // TODO add , analyze=Analyze.YES
    @ProtoField(number = 6)
    public String hero;

    @Field(store = Store.NO)
    @SortableField
    @ProtoDoc("@Field(store=Store.NO) @SortableField")
    @ProtoField(number = 7)
    public String color;

    @Spatial
    @Embedded
    public Location location;

    @IndexedEmbedded
    @Embedded
    @ProtoDoc("@Field(store=Store.NO)")
    @ProtoField(number = 8)
    public Address address;

    @IndexedEmbedded(prefix = "containedList.somePrefix_")
    @OneToMany
    public List<ContainedEntity> contained;

    @Field(store = Store.NO, indexNullAs = Field.DEFAULT_NULL_TOKEN)
    @ProtoDoc("@Field(store=Store.NO)")
    @ProtoField(number = 9)
    public String poke;
}
