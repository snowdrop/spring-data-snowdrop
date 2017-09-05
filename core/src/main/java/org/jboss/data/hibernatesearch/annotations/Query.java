package org.jboss.data.hibernatesearch.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Query {

  /**
   * HibernateSearch query to be used when executing query. May contain placeholders eg. ?0
   *
   * @return
   */
  String value() default "";

  /**
   * Named Query Named looked up by repository.
   *
   * @return
   */
  String name() default "";
}
