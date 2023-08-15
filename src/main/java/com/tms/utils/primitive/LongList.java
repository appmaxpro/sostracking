package com.tms.utils.primitive;

import java.util.function.LongConsumer;
import java.util.function.LongPredicate;

public interface LongList extends PrimitiveCollection, PrimitiveLongIterable{

    void add( long item );

    long get( int position );
    boolean contains(long value);

    int indexOf(long value);

    boolean removeValue(long item);

    long remove(int index);

    void removeRange(int off, int len);

    long[] toArray();

    void visit(LongPredicate predicate);

    void forEach(LongConsumer consumer);
}
