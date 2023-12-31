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

import com.tms.utils.primitive.PrimitiveLongCollections;

public class TableKeyIterator<VALUE> extends PrimitiveLongCollections.PrimitiveLongBaseIterator
{
    protected final Table<VALUE> stable;
    protected final AbstractHopScotchCollection<VALUE> collection;
    protected final long nullKey;
    protected final int version;
    private final int max;
    private int i;

    TableKeyIterator( Table<VALUE> table, AbstractHopScotchCollection<VALUE> collection )
    {
        this.stable = table;
        this.collection = collection;
        this.nullKey = stable.nullKey();
        this.max = stable.capacity();
        this.version = stable.version();
    }

    protected boolean isVisible( int index, long key )
    {
        return key != nullKey;
    }

    @Override
    protected boolean fetchNext()
    {
        while ( i < max )
        {
            int index = i++;
            long key = stable.key( index );
            if ( isVisible( index, key ) )
            {
                return next( key );
            }
        }
        return false;
    }
}
