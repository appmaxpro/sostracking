/*
 * Copyright (c) 2018-2020 "Graph Foundation,"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * This file is part of ONgDB.
 *
 * ONgDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.tms.utils.primitive.hopscotch;

import com.tms.utils.primitive.PrimitiveLongLongMap;
import com.tms.utils.primitive.PrimitiveLongLongVisitor;

public class PrimitiveLongLongHashMap extends AbstractLongHopScotchCollection<long[]> implements PrimitiveLongLongMap
{
    private final long[] transport = new long[1];
    private final HopScotchHashingAlgorithm.Monitor monitor;

    public PrimitiveLongLongHashMap( Table<long[]> table, HopScotchHashingAlgorithm.Monitor monitor )
    {
        super( table );
        this.monitor = monitor;
    }

    @Override
    public long put( long key, long value )
    {
        return unpack( HopScotchHashingAlgorithm.put( table, monitor, HopScotchHashingAlgorithm.DEFAULT_HASHING, key, pack( value ), this ) );
    }

    @Override
    public boolean containsKey( long key )
    {
        return HopScotchHashingAlgorithm.get( table, monitor, HopScotchHashingAlgorithm.DEFAULT_HASHING, key ) != null;
    }

    @Override
    public long get( long key )
    {
        return unpack( HopScotchHashingAlgorithm.get( table, monitor, HopScotchHashingAlgorithm.DEFAULT_HASHING, key ) );
    }

    @Override
    public long remove( long key )
    {
        return unpack( HopScotchHashingAlgorithm.remove( table, monitor, HopScotchHashingAlgorithm.DEFAULT_HASHING, key ) );
    }

    @Override
    public int size()
    {
        return table.size();
    }

    @Override
    public String toString()
    {
        return table.toString();
    }

    private long[] pack( long value )
    {
        transport[0] = value;
        return transport;
    }

    private long unpack( long[] result )
    {
        return result != null ? result[0] : LongKeyIntValueTable.NULL;
    }

    @Override
    public <E extends Exception> void visitEntries( PrimitiveLongLongVisitor<E> visitor ) throws E
    {
        long nullKey = table.nullKey();
        int capacity = table.capacity();
        for ( int i = 0; i < capacity; i++ )
        {
            long key = table.key( i );
            if ( key != nullKey )
            {
                long[] value = table.value( i );
                if ( value != null && visitor.visited( key, value[0] ) )
                {
                    return;
                }
            }
        }
    }

    @SuppressWarnings( "EqualsWhichDoesntCheckParameterClass" ) // yes it does
    @Override
    public boolean equals( Object other )
    {
        if ( typeAndSizeEqual( other ) )
        {
            PrimitiveLongLongHashMap that = (PrimitiveLongLongHashMap) other;
            LongLongEquality equality = new LongLongEquality( that );
            visitEntries( equality );
            return equality.isEqual();
        }
        return false;
    }

    private static class LongLongEquality implements PrimitiveLongLongVisitor<RuntimeException>
    {
        private PrimitiveLongLongHashMap other;
        private boolean equal = true;

        LongLongEquality( PrimitiveLongLongHashMap that )
        {
            this.other = that;
        }

        @Override
        public boolean visited( long key, long value )
        {
            equal = other.get( key ) == value;
            return !equal;
        }

        public boolean isEqual()
        {
            return equal;
        }
    }

    @Override
    public int hashCode()
    {
        HashCodeComputer hash = new HashCodeComputer();
        visitEntries( hash );
        return hash.hashCode();
    }

    private static class HashCodeComputer implements PrimitiveLongLongVisitor<RuntimeException>
    {
        private int hash = 1337;

        @Override
        public boolean visited( long key, long value ) throws RuntimeException
        {
            hash += HopScotchHashingAlgorithm.DEFAULT_HASHING.hashSingleValueToInt( key + HopScotchHashingAlgorithm.DEFAULT_HASHING.hashSingleValueToInt( value ) );
            return false;
        }

        @Override
        public int hashCode()
        {
            return hash;
        }

        @Override
        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }
            HashCodeComputer that = (HashCodeComputer) o;
            return hash == that.hash;
        }
    }
}
