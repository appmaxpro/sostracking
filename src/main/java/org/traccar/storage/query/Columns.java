/*
 * Copyright 2022 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.storage.query;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.traccar.storage.QueryIgnore;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Columns {

    private static final LoadingCache<ColumnsKey, List<String>> MAPPER_CACHE =
            CacheBuilder.newBuilder().maximumSize(1000)
                    .weakKeys()
                    .build(CacheLoader.from(Columns::getAllColumns));

    public static final Columns ALL = new All();

    private static class ColumnsKey {
        private final String type;
        private final Class<?> entity;

        public ColumnsKey(Class<?> entity, String type) {
            this.type = type;
            this.entity = entity;
        }

        public String getType() {
            return type;
        }

        public Class<?> getEntity() {
            return entity;
        }

        @Override
        public int hashCode() {
            return entity.hashCode() ^ type.hashCode();
        }

        public boolean equals(Object other) {
            if (other == this)
                return true;
            if (other instanceof ColumnsKey) {
                ColumnsKey myOther = (ColumnsKey) other;
                return myOther.entity.equals(entity)
                        && myOther.type.equals(type);
            }
            return false;
        }
    }


    public abstract List<String> getColumns(Class<?> clazz, String type);

    protected static List<String> getAllColumns(ColumnsKey key) {
        return getAllColumns(key.getEntity(), key.getType());
    }

    protected static List<String> getAllColumns(Class<?> clazz, String type) {
        List<String> columns = new LinkedList<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            int parameterCount = type.equals("set") ? 1 : 0;
            if (method.getName().startsWith(type) && method.getParameterTypes().length == parameterCount
                    && !method.isAnnotationPresent(QueryIgnore.class)
                    && !method.getName().equals("getClass")) {
                columns.add(Introspector.decapitalize(method.getName().substring(3)));
            }
        }
        return columns;
    }

    public static class All extends Columns {
        @Override
        public List<String> getColumns(Class<?> clazz, String type) {
            return Collections.unmodifiableList(MAPPER_CACHE.getUnchecked(new ColumnsKey(clazz, type)));
        }
    }

    public static class Include extends Columns {
        private final List<String> columns;

        public Include(String... columns) {
            this.columns = Arrays.stream(columns).collect(Collectors.toList());
        }

        @Override
        public List<String> getColumns(Class<?> clazz, String type) {
            return columns;
        }
    }

    public static class Exclude extends Columns {
        private final Set<String> columns;

        public Exclude(String... columns) {
            this.columns = Arrays.stream(columns).collect(Collectors.toSet());
        }

        @Override
        public List<String> getColumns(Class<?> clazz, String type) {
            return MAPPER_CACHE.getUnchecked(new ColumnsKey(clazz, type))
                    .stream()
                    .filter(column -> !columns.contains(column))
                    .collect(Collectors.toList());
        }
    }

}
