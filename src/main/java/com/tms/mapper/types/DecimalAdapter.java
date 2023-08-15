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
import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalAdapter implements TypeAdapter<BigDecimal> {

  @Override
  public Object adapt(Object value, Class<?> actualType, Type genericType, Property property) {

    Integer scale = null;
    Boolean nullable = null;
    if (property != null) {
      scale = property.getScale();
      nullable = property.isNullable();
    }

    boolean empty =
        value == null || (value instanceof String && "".equals(((String) value).trim()));
    if (empty) {
      return Boolean.TRUE.equals(nullable) ? null : BigDecimal.ZERO;
    }

    if (value instanceof BigDecimal) {
      return adjust((BigDecimal) value, scale);
    }
    return adjust(new BigDecimal(value.toString()), scale);
  }

  private BigDecimal adjust(BigDecimal value, Integer scale) {
    if (scale != null) {
      return value.setScale(scale, RoundingMode.HALF_UP);
    }
    return value;
  }
}
