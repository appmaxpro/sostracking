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

import com.tms.utils.primitive.PrimitiveLongIntMap;
import com.tms.utils.primitive.PrimitiveLongIntVisitor;
import com.tms.utils.primitive.hopscotch.HopScotchHashingAlgorithm.Monitor;

import static com.tms.utils.primitive.hopscotch.HopScotchHashingAlgorithm.DEFAULT_HASHING;

public class PrimitiveLongIntHashMap extends AbstractLongHopScotchCollection<int[]> implements PrimitiveLongIntMap
{
    private final int[] transport = new int[1];
    private final Monitor monitor;

    public PrimitiveLongIntHashMap( Table<int[]> table, Monitor monitor )
    {
        super( table );
        this.monitor = monitor;
    }

    @Override
    public int put( long key, int value )
    {
        return unpack( HopScotchHashingAlgorithm.put( table, monitor, DEFAULT_HASHING, key, pack( value ), this ) );
    }

    @Override
    public boolean containsKey( long key )
    {
        return HopScotchHashingAlgorithm.get( table, monitor, DEFAULT_HASHING, key ) != null;
    }

    @Override
    public int get( long key )
    {
        return unpack( HopScotchHashingAlgorithm.get( table, monitor, DEFAULT_HASHING, key ) );
    }

    @Override
    public int remove( long key )
    {
        return unpack( HopScotchHashingAlgorithm.remove( table, monitor, DEFAULT_HASHING, key ) );
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

    private int[] pack( int value )
    {
        transport[0] = value;
        return transport;
    }

    private int unpack( int[] result )
    {
        return result != null ? result[0] : LongKeyIntValueTable.NULL;
    }

    @Override
    public <E extends Exception> void visitEntries( PrimitiveLongIntVisitor<E> visitor ) throws E
    {
        long nullKey = table.nullKey();
        int capacity = table.capacity();
        for ( int i = 0; i < capacity; i++ )
        {
            int[] value = table.value( i );
            if ( value != null )
            {
                long key = table.key( i );
                if ( key != nullKey && visitor.visited( key, value[0] ) )
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
            PrimitiveLongIntHashMap that = (PrimitiveLongIntHashMap) other;
            LongIntEquality equality = new LongIntEquality( that );
            visitEntries( equality );
            return equality.isEqual();
        }
        return false;
    }

    private static class LongIntEquality implements PrimitiveLongIntVisitor<RuntimeException>
    {
        private PrimitiveLongIntHashMap other;
        private boolean equal = true;

        LongIntEquality( PrimitiveLongIntHashMap that )
        {
            this.other = that;
        }

        @Override
        public boolean visited( long key, int value )
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

    private static class HashCodeComputer implements PrimitiveLongIntVisitor<RuntimeException>
    {
        private int hash = 1337;

        @Override
        public boolean visited( long key, int value ) throws RuntimeException
        {
            hash += DEFAULT_HASHING.hashSingleValueToInt( key + DEFAULT_HASHING.hashSingleValueToInt( value ) );
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
