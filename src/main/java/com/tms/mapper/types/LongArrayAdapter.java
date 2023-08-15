package com.tms.mapper.types;

import com.tms.mapper.Property;
import com.tms.mapper.TypeAdapter;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public class LongArrayAdapter implements TypeAdapter<long[]> {
    public static LongArrayAdapter PRIMITIVE_LONG_ARRAY_ADAPTER = new LongArrayAdapter(true);
    public static LongArrayAdapter LONG_ARRAY_ADAPTER = new LongArrayAdapter(false);

    private final boolean primitive;

    LongArrayAdapter(boolean primitive) {
        this.primitive = primitive;
    }


    @Override
    public Object adapt(Object value, Class<?> actualType, Type genericType, Property property) {
        if (value instanceof Collection) {
            Collection valueList = (Collection) value;
            if (primitive) {
                long[] ids = new long[valueList.size()];
                int index = 0;
                for (Object val : valueList) {
                    if (val instanceof Number) {
                        ids[index] = ((Number) val).longValue();
                    } else if (val != null){
                        ids[index] = Long.parseLong(val.toString());
                    }
                    index++;
                }
                return ids;
            } else {
                Long[] ids = new Long[valueList.size()];
                ids = (Long[])valueList.toArray(ids);
                return ids;
            }

        }
        else if (value instanceof Long[]) {
            if (primitive) {
                Long[] valueArray = (Long[]) value;
                long[] ids = new long[valueArray.length];
                for(int index = 0; index < ids.length; index++) {
                    ids[index] = valueArray[index] != null ? valueArray[index] : 0;
                }
                return ids;
            }
            return value;
        }
        else if (value instanceof long[]) {
            if (!primitive) {
                long[] valueArray = (long[]) value;
                Long[] ids = new Long[valueArray.length];
                for(int index = 0; index < ids.length; index++) {
                    ids[index] = valueArray[index] != 0 ? valueArray[index] : null;
                }
                return ids;
            }
            return value;
        }

        return null;
    }
}
