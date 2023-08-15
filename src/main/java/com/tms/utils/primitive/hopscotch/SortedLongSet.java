package com.tms.utils.primitive.hopscotch;


import com.tms.utils.collection.primitive.*;
import com.tms.utils.primitive.*;
import org.traccar.tms.utils.collection.primitive.*;

import java.util.function.IntPredicate;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;

public class SortedLongSet implements LongSet {

    private LongList sortedList;
    private PrimitiveLongSet longSet ;

    public SortedLongSet(int size)
    {
        sortedList = Primitive.sortedLongList(size);
        longSet = Primitive.longSet(size);
    }

    @Override
    public boolean add( long value )
    {

        if (longSet.add(value)) {
            sortedList.add(value);
            return true;
        }
        return false;
    }

    @Override
    public long[] toArray() {
        return sortedList.toArray();
    }

    @Override
    public boolean addAll( PrimitiveLongIterator values )
    {
        boolean changed = false;
        while ( values.hasNext() )
        {
            var next =  values.next();
            if (longSet.add(next)) {
                sortedList.add(next);
                changed |= true;
            }
        }
        return changed;
    }

    @Override
    public void visit(LongPredicate predicate) {
        sortedList.visit(predicate);
    }

    @Override
    public void forEach(LongConsumer consumer) {
        sortedList.forEach(consumer);
    }

    @Override
    public boolean contains( long value )
    {
        return longSet.contains(value);
    }

    /**
     * Prefer using {@link #contains(long)} - this method is identical and required by the {@link IntPredicate} interface
     *
     * @param value the input argument
     * @return true if the input argument matches the predicate, otherwise false
     */
    @Override
    public boolean test( long value )
    {
        return longSet.test(value);
    }

    @Override
    public boolean remove( long value )
    {
        if (longSet.remove(value)) {
            sortedList.removeValue(value);
            return true;
        }
        return false;
    }

    @SuppressWarnings( "EqualsWhichDoesntCheckParameterClass" ) // yes it does
    @Override
    public boolean equals( Object other )
    {
        if ( other instanceof SortedLongSet)
        {
            SortedLongSet that = (SortedLongSet) other;
            return longSet.equals(that.longSet);
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return longSet.isEmpty();
    }

    @Override
    public void clear() {
        sortedList.clear();
        longSet.clear();
    }

    @Override
    public int size() {
        return longSet.size();
    }

    @Override
    public void close() {
        longSet.close();
        sortedList.close();
    }

    @Override
    public <E extends Exception> void visitKeys(PrimitiveLongVisitor<E> visitor) throws E {
        longSet.visitKeys(visitor);
    }

    @Override
    public PrimitiveLongIterator iterator() {
        return sortedList.iterator();
    }
}
