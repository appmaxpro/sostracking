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

import com.tms.utils.primitive.PrimitiveIntCollection;
import com.tms.utils.primitive.PrimitiveIntCollections;
import com.tms.utils.primitive.PrimitiveIntIterator;
import com.tms.utils.primitive.PrimitiveIntVisitor;

public abstract class AbstractIntHopScotchCollection<VALUE> extends AbstractHopScotchCollection<VALUE>
        implements PrimitiveIntCollection
{
    public AbstractIntHopScotchCollection( Table<VALUE> table )
    {
        super( table );
    }

    @Override
    public PrimitiveIntIterator iterator()
    {
        final TableKeyIterator<VALUE> longIterator = new TableKeyIterator<>( table, this );
        return new PrimitiveIntCollections.PrimitiveIntBaseIterator()
        {
            @Override
            protected boolean fetchNext()
            {
                return longIterator.hasNext() && next( (int) longIterator.next() );
            }
        };
    }

    @Override
    public <E extends Exception> void visitKeys( PrimitiveIntVisitor<E> visitor ) throws E
    {
        int capacity = table.capacity();
        long nullKey = table.nullKey();
        for ( int i = 0; i < capacity; i++ )
        {
            long key = table.key( i );
            if ( key != nullKey && visitor.visited( (int) key ) )
            {
                return;
            }
        }
    }
}
