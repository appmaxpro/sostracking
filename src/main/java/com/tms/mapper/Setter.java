package com.tms.mapper;

public interface Setter<T, V> {
    void apply(T t, V v);
}
