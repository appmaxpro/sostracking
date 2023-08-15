package com.tms.utils;

import java.math.BigDecimal;

public class ValueRef<T> {
    public static <T> ValueRef of(T value) {
        return new ValueRef(value);
    }

    public static ValueRef<Long> ofLong(Long value) {
        return new LongValueRef(value);
    }

    public static ValueRef<Double> ofDouble(Double value) {
        return new DoubleValueRef(value);
    }

    public static ValueRef<Integer> ofInt(Integer value) {
        return new IntValueRef(value);
    }

    public static ValueRef<BigDecimal> ofDecimal(BigDecimal value) {
        return new DecimalValueRef(value);
    }

    private T value;
    public ValueRef(T value) {
        this.value = value;
    }

    public T get(){
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void add(T value) {
        throw new UnsupportedOperationException();
    }

    private static class IntValueRef extends ValueRef<Integer> {

        public IntValueRef(Integer value) {
            super(value);
        }

        @Override
        public void add(Integer value) {
            if (value != null) {
                if (get() != null) {
                    set(get() + value);
                } else {
                    set(value);
                }
            }
        }
    }

    private static class LongValueRef extends ValueRef<Long> {

        public LongValueRef(Long value) {
            super(value);
        }

        @Override
        public void add(Long value) {
            if (value != null) {
                if (get() != null) {
                    set(get() + value);
                } else {
                    set(value);
                }
            }
        }
    }

    private static class DoubleValueRef extends ValueRef<Double> {

        public DoubleValueRef(Double value) {
            super(value);
        }

        @Override
        public void add(Double value) {
            if (value != null) {
                if (get() != null) {
                    set(get() + value);
                } else {
                    set(value);
                }
            }
        }
    }

    private static class DecimalValueRef extends ValueRef<BigDecimal> {

        public DecimalValueRef(BigDecimal value) {
            super(value);
        }

        @Override
        public void add(BigDecimal value) {
            if (value != null) {
                if (get() != null) {
                    set(get().add(value));
                } else {
                    set(value);
                }
            }
        }
    }

}
