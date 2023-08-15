package com.tms.mapper.types;

import com.tms.mapper.Property;
import com.tms.mapper.TypeAdapter;

import java.lang.reflect.Type;
import java.util.List;

public class IntArrayAdapter implements TypeAdapter<long[]> {

    @Override
    public Object adapt(Object value, Class<?> actualType, Type genericType, Property property) {
        if (actualType == int[].class) {

            if (value instanceof List) {

            }

            int[] ids = new int[0];
        }
        if (actualType == Integer[].class) {

        }

        return null;
    }
}