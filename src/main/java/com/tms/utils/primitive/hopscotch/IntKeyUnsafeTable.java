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

import com.tms.utils.memory.MemoryAllocationTracker;
import com.tms.utils.unsafe.UnsafeUtil;

public class IntKeyUnsafeTable<VALUE> extends UnsafeTable<VALUE>
{
    public IntKeyUnsafeTable( int capacity, VALUE valueMarker, MemoryAllocationTracker allocationTracker )
    {
        super( capacity, 4, valueMarker, allocationTracker );
    }

    @Override
    protected long internalKey( long keyAddress )
    {
        return UnsafeUtil.getInt( keyAddress );
    }

    @Override
    protected void internalPut( long keyAddress, long key, VALUE value )
    {
        assert (int)key == key : "Illegal key " + key + ", it's bigger than int";

        // We can "safely" cast to int here, assuming that this call trickles in via a PrimitiveIntCollection
        UnsafeUtil.putInt( keyAddress, (int) key );
    }

    @Override
    protected Table<VALUE> newInstance( int newCapacity )
    {
        return new IntKeyUnsafeTable<>( newCapacity, valueMarker, allocationTracker );
    }
}
