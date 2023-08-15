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

import com.tms.model.Model;
import com.tms.mapper.ModelMapper;
import com.tms.mapper.Property;
import com.tms.mapper.TypeAdapter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ListAdapter implements TypeAdapter<List<?>> {

  @SuppressWarnings("unchecked")
  @Override
  public Object adapt(Object value, Class<?> type, Type genericType, Property property) {
    final Class<?> fieldType =
        (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
    final List<Object> val = new ArrayList<>();
    for (Object obj : (Collection<?>) value) {
      if (MapAdapter.isModelMap(fieldType, obj)) {
        val.add(ModelMapper.toBean(fieldType.asSubclass(Model.class), (Map<String, Object>) obj));
      } else {
        val.add(obj);
      }
    }
    return val;
  }
}
