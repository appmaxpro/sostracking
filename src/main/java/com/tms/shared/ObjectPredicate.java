package com.tms.shared;

public interface ObjectPredicate<V> {

    boolean test(long id, V value);
}
