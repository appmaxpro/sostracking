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

import com.tms.model.types.ValueEnum;
import com.tms.mapper.Property;
import com.tms.mapper.TypeAdapter;

import java.lang.reflect.Type;

public class EnumAdapter implements TypeAdapter<Enum<?>> {

  @Override
  @SuppressWarnings("unchecked")
  public Object adapt(Object value, Class<?> actualType, Type genericType, Property property) {
    if (value == null) {
      return null;
    }
    if (!actualType.isEnum()) {
      throw new IllegalArgumentException("Given type is not enum: " + actualType.getName());
    }
    if (value instanceof Enum) {
      return value;
    }
    return ValueEnum.class.isAssignableFrom(actualType)
        ? ValueEnum.of(actualType.asSubclass(Enum.class), value)
        : Enum.valueOf(actualType.asSubclass(Enum.class), value.toString());
  }
}
