package com.tms.utils.primitive.hopscotch;

import com.tms.utils.primitive.PrimitiveLongCollections;
import com.tms.utils.primitive.PrimitiveLongIterator;
import com.tms.utils.primitive.LongList;

import java.util.Arrays;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;

public class SortedLongList implements LongList
{
    private static final int DEFAULT_SIZE = 8;
    protected volatile int mod;
    private long[] data;
    private int size;

    public SortedLongList()
    {
        this( DEFAULT_SIZE );
    }

    public SortedLongList( int size )
    {
        data = new long[size];
    }

    @Override
    public void add( long item )
    {
        if(size == data.length) {
            expandList();
        }
        mod++;
        // Add the new item
        int index = Arrays.binarySearch(data, 0, size, item);
        if(index < 0) { index = -index - 1; }
        if(index > size) { index = size; }
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = item;
        size++;

    }

    @Override
    public long get( int position )
    {
        if ( position >= size )
        {
            throw new IndexOutOfBoundsException( "Requested element: " + position + ", list size: " + size );
        }
        return data[position];
    }

    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    @Override
    public void clear()
    {
        size = 0;
        data = PrimitiveLongCollections.EMPTY_LONG_ARRAY;
        mod++;
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public void close()
    {
        clear();
    }

    @Override
    public boolean contains(long value) {
        int index = Arrays.binarySearch(data, 0, size, value);
        return (index > -1 && index < size);
    }

    @Override
    public int indexOf(long value) {
        int idx = Arrays.binarySearch(data, 0, this.size, value);
        if(idx > -1) {
            long val = data[idx];
            while(idx > 0 && data[idx - 1] == val) { idx--; }
        }
        return idx > -1 ? idx : -1;
    }

    public int binarySearch(long value) {
        int idx = Arrays.binarySearch(data, 0, this.size, value);
        return idx;
    }

    @Override
    public long remove(int index) {
        if(index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException(index + " of [0, " + size + ")");
        }
        long item = data[index];

        removeRange(index, 1);

        return item;
    }

    @Override
    public void removeRange(int off, int len) {
        if(off < 0 || off + len > size) {
            throw new ArrayIndexOutOfBoundsException((off < 0 ? off : off + len) + " of [0, " + size + ")");
        }
        mod++;
        // Shift all elements above the remove element to fill the empty index
        // Copy down the remaining upper half of the array if the item removed was not the last item in the array
        if(off + len < size) {
            System.arraycopy(data, off + len, data, off, size - (off + len));
        }
        // Decrease the size because we removed items
        size -= len;
    }

    @Override
    public boolean removeValue(long value) {
        int index = Arrays.binarySearch(data, 0, size, value);
        if(index > -1 && index < size) {
            mod++;
            System.arraycopy(data, index + 1, data, index, size - index - 1);
            size--;
            return true;
        }
        return false;
    }

    @Override
    public PrimitiveLongIterator iterator()
    {
        return new SortedLongListIterator();
    }

    @Override
    public long[] toArray()
    {
        return Arrays.copyOf(data, size );
    }

    @Override
    public void visit(LongPredicate predicate) {
        for(int index = 0; index < size; index++) {
            if (!predicate.test(data[index]))
                break;
        }
    }

    @Override
    public void forEach(LongConsumer consumer) {
        for(int index = 0; index < size; index++) {
            consumer.accept(data[index]);
        }
    }

    private final void expandList() {
        // Expand the size by 1.5x
        int len = this.data.length;
        expandList(len + (len >> 1));
    }


    private final void expandList(int totalSize) {
        long[] oldData = this.data;
        // Expand the size by the number of new elements + 4
        // +4 rather than +1 to prevent constantly expanding a small list
        this.data = new long[totalSize + 4];
        System.arraycopy(oldData, 0, this.data, 0, size);
    }


    private class SortedLongListIterator extends PrimitiveLongCollections.PrimitiveLongBaseIterator
    {
        int cursor;

        @Override
        protected boolean fetchNext()
        {
            return cursor < size && next( data[cursor++] );
        }
    }
}
