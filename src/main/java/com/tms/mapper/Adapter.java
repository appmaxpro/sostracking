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
package com.tms.mapper;

import com.tms.mapper.types.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

public class Adapter {

  private static SimpleAdapter simpleAdapter = new SimpleAdapter();
  private static ListAdapter listAdapter = new ListAdapter();
  private static SetAdapter setAdapter = new SetAdapter();
  private static MapAdapter mapAdapter = new MapAdapter();
  private static JavaTimeAdapter javaTimeAdapter = new JavaTimeAdapter();
  private static EnumAdapter enumAdapter = new EnumAdapter();

  private static DecimalAdapter decimalAdapter = new DecimalAdapter();

  private static TypeAdapter primitiveLongArrayAdapter = LongArrayAdapter.PRIMITIVE_LONG_ARRAY_ADAPTER;
  private static TypeAdapter longArrayAdapter = LongArrayAdapter.LONG_ARRAY_ADAPTER;

  public static Object adapt(Object value, Class<?> type, Type genericType, Property property) {
    /*
    if (annotations == null) {
      annotations = new Annotation[] {};
    }

     */

    if (type.isEnum()) {
      return enumAdapter.adapt(value, type, genericType, property);
    }

    if (type == long[].class) {
        return primitiveLongArrayAdapter.adapt(value, type, genericType, property);
    }

    if (type == Long[].class) {
      return longArrayAdapter.adapt(value, type, genericType, property);
    }

      // one2many
    if (value instanceof Collection && List.class.isAssignableFrom(type)) {
      return listAdapter.adapt(value, type, genericType, property);
    }

    // many2many
    if (value instanceof Collection && Set.class.isAssignableFrom(type)) {
      return setAdapter.adapt(value, type, genericType, property);
    }



    // many2one
    if (value instanceof Map) {
      return mapAdapter.adapt(value, type, genericType, property);
    }

    // collection of simple types
    if (value instanceof Collection) {
      Collection<Object> all = value instanceof Set ? new HashSet<>() : new ArrayList<>();
      for (Object item : (Collection<?>) value) {
        all.add(adapt(item, type, genericType, property));
      }
      return all;
    }

    // must be after adapting collections
    if (type.isInstance(value)) {
      return value;
    }

    if (javaTimeAdapter.isJavaTimeObject(type)) {
      return javaTimeAdapter.adapt(value, type, genericType, property);
    }

    if (BigDecimal.class.isAssignableFrom(type)) {
      return decimalAdapter.adapt(value, type, genericType, property);
    }

    return simpleAdapter.adapt(value, type, genericType, property);
  }
}
