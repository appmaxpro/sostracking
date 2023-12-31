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
package com.tms.utils.unsafe;

import static com.tms.utils.OsBeanUtil.VALUE_UNAVAILABLE;
import static com.tms.utils.OsBeanUtil.getCommittedVirtualMemory;
import static com.tms.utils.OsBeanUtil.getFreePhysicalMemory;
import static com.tms.utils.OsBeanUtil.getTotalPhysicalMemory;

public class NativeMemoryAllocationRefusedError extends Error
{
    private final long attemptedAllocationSizeBytes;
    private final long alreadyAllocatedBytes;

    NativeMemoryAllocationRefusedError( long size, long alreadyAllocatedBytes, Throwable cause )
    {
        super( cause );
        this.attemptedAllocationSizeBytes = size;
        this.alreadyAllocatedBytes = alreadyAllocatedBytes;
    }

    @Override
    public String getMessage()
    {
        String message = super.getMessage();
        StringBuilder sb = new StringBuilder();
        sb.append( "Failed to allocate " ).append( attemptedAllocationSizeBytes ).append( " bytes. " );
        sb.append( "So far " ).append( alreadyAllocatedBytes );
        sb.append( " bytes have already been successfully allocated. " );
        sb.append( "The system currently has " );
        appendBytes( sb, getTotalPhysicalMemory() ).append( " total physical memory, " );
        appendBytes( sb, getCommittedVirtualMemory() ).append( " committed virtual memory, and " );
        appendBytes( sb, getFreePhysicalMemory() ).append( " free physical memory. " );
        sb.append( "Relevant system properties: " );
        appendSysProp( sb, "java.vm.name" );
        appendSysProp( sb.append( ", " ), "java.vm.vendor" );
        appendSysProp( sb.append( ", " ), "os.arch" );

        if ( getCause() instanceof OutOfMemoryError )
        {
            sb.append( ". The allocation was refused by the operating system" );
        }

        if ( message != null )
        {
            sb.append( ": " ).append( message );
        }
        else
        {
            sb.append( '.' );
        }
        return sb.toString();
    }

    private StringBuilder appendBytes( StringBuilder sb, long bytes )
    {
        if ( bytes == VALUE_UNAVAILABLE )
        {
            sb.append( "(?) bytes" );
        }
        else
        {
            sb.append( bytes ).append( " bytes" );
        }
        return sb;
    }

    private void appendSysProp( StringBuilder sb, String sysProp )
    {
        sb.append( '"' ).append( sysProp ).append( "\" = \"" ).append( System.getProperty( sysProp ) ).append( '"' );
    }
}
