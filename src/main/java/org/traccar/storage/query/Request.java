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


import java.util.LinkedList;
import java.util.List;

public class Request {

    private final Columns columns;
    private final Condition condition;
    private final Order order;

    private final Integer offset;

    private final Integer limit;

    public Request(Columns columns) {
        this(columns, null, null);
    }

    public Request(Condition condition) {
        this(null, condition, null);
    }

    public Request(Columns columns, Condition condition) {
        this(columns, condition, null);
    }

    public Request(RequestBuilder builder) {
        this.columns = builder.columns;
        this.condition = builder.buildCondition();
        this.order = builder.order;
        this.offset = builder.offset;
        this.limit = builder.limit;
    }

    public Request(Columns columns, Order order) {
        this(columns, null, order);
    }

    public Request(Columns columns, Condition condition, Order order) {
        this.columns = columns;
        this.condition = condition;
        this.order = order;
        this.offset = null;
        this.limit = null;
    }

    public Columns getColumns() {
        return columns;
    }

    public Condition getCondition() {
        return condition;
    }

    public Order getOrder() {
        return order;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public static RequestBuilder builder(Columns columns) {
        return new RequestBuilder().setColumns(columns);
    }

    public static RequestBuilder builder() {
        return new RequestBuilder().setColumns(Columns.ALL);
    }

    public static class RequestBuilder {
        private List<Condition> conditions = new LinkedList();
        private Columns columns;
        private Integer offset;
        private Integer limit;

        private Order order;

        private boolean isOr;

        public RequestBuilder setColumns(Columns columns) {
            this.columns = columns;
            return this;
        }

        public RequestBuilder withOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public RequestBuilder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public RequestBuilder withLimit(Integer offset, Integer limit) {
            this.offset = offset;
            this.limit = limit;
            return this;
        }

        public RequestBuilder or(Condition cond) {
            if (isOr) {
                conditions.add(cond);
            } else {

                if (!conditions.isEmpty()) {
                    Condition merge = Condition.merge(conditions);
                    cond = new Condition.Or(merge, cond);
                    conditions.clear();
                }
                conditions.add(cond);
            }
            isOr = true;
            return this;
        }

        public RequestBuilder and(Condition cond) {
            if (!isOr) {
                conditions.add(cond);
            } else {

                if (!conditions.isEmpty()) {
                    Condition merge = Condition.mergeOr(conditions);
                    cond = new Condition.And(merge, cond);
                    conditions.clear();
                }
                conditions.add(cond);
            }
            isOr = false;
            return this;
        }

        public RequestBuilder order(String column,
                                    boolean descending) {

            order = new Order(column, descending, 0);
            return this;
        }

        public RequestBuilder order(String column) {
            return order(column, false);
        }

        Condition buildCondition() {
            if (!conditions.isEmpty()) {
                return isOr ? Condition.mergeOr(conditions)
                        : Condition.merge(conditions);
            }
            return null;
        }

        public Request build() {
            return new Request(this);
        }
    }

}
