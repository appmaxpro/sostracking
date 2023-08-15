/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2023 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.tms.mapper.types;

import com.tms.mapper.Property;
import com.tms.mapper.TypeAdapter;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class SimpleAdapter implements TypeAdapter<Object> {

  @Override
  public Object adapt(Object value, Class<?> type, Type genericType, Property property) {

    if (value == null || (value instanceof String && "".equals(((String) value).trim()))) {
      return adaptNull(value, type, genericType, property);
    }

    // handle Boolean first to avoid constructor as in that case equal comparison
    // using (==) with Boolean.TRUE, Boolean.FALSE will fail.
    if (type == Boolean.TYPE || type == Boolean.class) {
      return Boolean.valueOf(value.toString());
    }

    // try a constructor with exact value type
    try {
      return type.getConstructor(new Class<?>[] {value.getClass()})
          .newInstance(new Object[] {value});
    } catch (Exception e) {
    }

    if (type == String.class) {
      if (value == null || value instanceof String) {
        return value;
      }
      return value.toString();
    }

    if (type == byte[].class && value instanceof String) {
      return ((String) value).getBytes();
    }

    if (type == Character.TYPE || type == Character.class)
      return Character.valueOf(value.toString().charAt(0));

    if (type == Byte.TYPE || type == Byte.class) return Byte.valueOf(value.toString());

    if (type == Short.TYPE || type == Short.class) return Short.valueOf(value.toString());

    if (type == Integer.TYPE || type == Integer.class) {
      if (value instanceof Number) {
        return ((Number) value).intValue();
      }
      return Integer.valueOf(value.toString());
    }

    if (type == Long.TYPE || type == Long.class) return Long.valueOf(value.toString());

    if (type == Float.TYPE || type == Float.class) return Float.valueOf(value.toString());

    if (type == Double.TYPE || type == Double.class) return Double.valueOf(value.toString());

    if (type == Date.class) return Date.valueOf(value.toString());

    if (type == Time.class) return Time.valueOf(value.toString());

    if (type == Timestamp.class) return Timestamp.valueOf(value.toString());

    return value;
  }

  public Object adaptNull(Object value, Class<?> type, Type genericType, Property property) {

    if (isNullable(type, property)) return null;

    if (type == boolean.class) return false;

    if (type == int.class) return 0;

    if (type == long.class) return 0L;

    if (type == double.class) return 0.0;

    if (type == short.class) return 0.0F;

    if (type == char.class) return ' ';

    return null;
  }

  private boolean isNullable(Class<?> type, Property property) {
    if (type.isPrimitive()) {
      return false;
    }
    return property == null || property.isNullable();
    /*
    if (property == null || annotations.length == 0) {
      return true;
    }

    for (Annotation annotation : annotations) {
      if (annotation instanceof Column) {
        return ((Column) annotation).nullable();
      }
    }

    return false;

     */
  }
}
