package org.traccar.model.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ManyToMany {

    String name() default "";

    Class<?> target();

    boolean lazy() default false;

    String table() default "";

    String col1() default "";

    String col2() default "";
}
