package org.hisp.dhis.importexport.comparator;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Comparator;

import org.hisp.dhis.importexport.ImportObject;

/**
 * @author Lars Helge Overland
 */
public class ImportObjectComparator
    implements Comparator<ImportObject>
{
    @Override
    public int compare( ImportObject o0, ImportObject o1 )
    {
        if ( o0 == null || o0.getClassName() == null )
        {
            return 1;
        }
        
        if ( o1 == null || o1.getClassName() == null )
        {
            return -1;
        }
        
        if ( !o0.getClassName().equalsIgnoreCase( o1.getClassName() ) )
        {
            return o0.getClassName().compareToIgnoreCase( o1.getClassName() );
        }
        
        if ( o0.getObject() == null )
        {
            return 1;
        }
        
        if ( o1.getObject() == null )
        {
            return -1;
        }
        
        return o0.getObject().getName().compareToIgnoreCase( o1.getObject().getName() );
    }
}
