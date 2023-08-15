package com.tms.shared;

public interface ObjectVisitor<V> {

    void visit(long id, V value);
}
