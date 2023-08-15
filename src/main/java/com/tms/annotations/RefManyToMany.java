package com.tms.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RefManyToMany {

    String mappedBy() default "";

    Class target();

    String table() default "";
    String col1() default "";
    String col2() default "";

}
