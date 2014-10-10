package org.hisp.dhis.reportsheet;

/*
 * Copyright (c) 2004-2011, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import org.hisp.dhis.common.BaseNameableObject;

/**
 * @author Tran Thanh Tri
 */
@SuppressWarnings( "serial" )
public class Bookmark
    extends BaseNameableObject
{
    public static final String CHART = "CHART";

    public static final String COMPLETED_REPORT = "COMPLETED_REPORT";

    private String contain;

    private String type;

    private String username;

    private String extraContain;

    // -------------------------------------------------------------------------
    // support method
    // -------------------------------------------------------------------------

    public boolean isChart()
    {
        return this.type.equals( CHART );
    }

    public boolean isCompletedReport()
    {
        return this.type.equals( COMPLETED_REPORT );
    }

    public boolean hasExtraContain()
    {
        return this.extraContain!=null && this.extraContain!=""; 
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        Bookmark other = (Bookmark) obj;
        if ( id != other.getId() )
            return false;
        return true;
    }

    // -------------------------------------------------------------------------
    // getter and setter
    // -------------------------------------------------------------------------

    public String getContain()
    {
        return contain;
    }

    public void setContain( String contain )
    {
        this.contain = contain;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getExtraContain()
    {
        return extraContain;
    }

    public void setExtraContain( String extraContain )
    {
        this.extraContain = extraContain;
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

}
