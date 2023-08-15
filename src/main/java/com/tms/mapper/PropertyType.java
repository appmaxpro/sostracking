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

public enum PropertyType {
  STRING,
  TEXT,
  BOOLEAN,
  INTEGER,
  LONG,
  DOUBLE,
  DECIMAL,
  DATE,
  TIME,
  DATETIME,
  BINARY,
  IMAGE,
  ENUM,
  ONE_TO_ONE,
  MANY_TO_ONE,
  ONE_TO_MANY,
  MANY_TO_MANY,
  GEOMETRY,
  MAP,
  LIST,
  SET,
  INT_ARR,
  LONG_ARR;

  public static PropertyType get(String value) {
    assert value != null;
    try {
      return PropertyType.valueOf(value);
    } catch (Exception e) {
      if (value.equals("INT")) return PropertyType.INTEGER;
      else if (value.equals("FLOAT")) return PropertyType.DOUBLE;
      else if (value.equals("BIGDECIMAL")) return PropertyType.DECIMAL;
      else if (value.equals("LOCALDATE")) return PropertyType.DATE;
      else if (value.equals("LOCALTIME")) return PropertyType.TIME;
      else if (value.equals("LOCALDATETIME")) return PropertyType.DATETIME;
      else if (value.equals("CALENDAR")) return PropertyType.DATETIME;
      else if (value.equals("ZONEDDATETIME")) return PropertyType.DATETIME;
      else if (value.equals("BYTE[]") || value.equals("IMAGE")) return PropertyType.BINARY;
      else if (value.equals("INT[]")) return PropertyType.INT_ARR;
      else if (value.equals("LONG[]")) return PropertyType.LONG_ARR;
      /*
      else if (value.endsWith("POINT") || value.endsWith("LINE") || value.endsWith("POLYGON")) {
        return PropertyType.GEOMETRY;
      }*/
      else if (value.equals("MAP")) return PropertyType.MAP;
      else if (value.equals("SET")) return PropertyType.SET;
      else if (value.equals("LIST")) return PropertyType.LIST;
    }
    return null;
  }
}
