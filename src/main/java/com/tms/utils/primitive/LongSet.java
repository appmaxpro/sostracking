package com.tms.utils.primitive;

import java.util.function.LongConsumer;
import java.util.function.LongPredicate;

public interface LongSet extends PrimitiveLongSet{

    long[] toArray();

    boolean addAll( PrimitiveLongIterator values );

    void visit(LongPredicate predicate);

    void forEach(LongConsumer consumer);
}
