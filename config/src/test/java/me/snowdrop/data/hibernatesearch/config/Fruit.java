package me.snowdrop.data.hibernatesearch.config;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.springframework.data.annotation.Id;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Indexed
@Entity
@Table(name = "fruit")
public class Fruit {
  @Id
  @DocumentId
  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Field
  String name;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
