package com.tms.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

class AnnotationFieldInfo {
    final Field field;
    final Annotation[] annotations;

    public AnnotationFieldInfo(Field field, Annotation[] annotations) {
        this.field = field;
        this.annotations = annotations;
    }
}
