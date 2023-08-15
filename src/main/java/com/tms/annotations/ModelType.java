package com.tms.annotations;

public @interface ModelType {


    String refModel() default "";

    int cacheSize() default 0;
}
