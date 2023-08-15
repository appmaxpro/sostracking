package org.traccar.model.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface Entity {
    String name() default "";

    boolean cache() default false;

}
