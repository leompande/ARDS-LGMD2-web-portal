package org.hisp.dhis.common;

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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lars Helge Overland
 */
public class QueryItem
{
    public static final String OPTION_SEP = ";";
    
    public static final Map<String, String> OPERATOR_MAP = new HashMap<String, String>() { {
        put( "eq", "=" );
        put( "gt", ">" );
        put( "ge", ">=" );
        put( "lt", "<" );
        put( "le", "<=" );
        put( "ne", "!=" );
        put( "like", "like" );
        put( "in", "in" );
    } };
    
    private IdentifiableObject item;

    private String operator;

    private String filter;
    
    private boolean numeric;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public QueryItem( IdentifiableObject item )
    {
        this.item = item;
    }
    
    public QueryItem( IdentifiableObject item, String operator, String filter, boolean numeric )
    {
        this.item = item;
        this.operator = operator;
        this.filter = filter;
        this.numeric = numeric;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean hasFilter()
    {
        return operator != null && !operator.isEmpty() && filter != null && !filter.isEmpty();
    }
    
    public String getSqlOperator()
    {
        if ( operator == null )
        {
            return null;
        }
        
        return OPERATOR_MAP.get( operator.toLowerCase() );
    }
    
    public String getSqlFilter( String encodedFilter )
    {
        if ( operator == null || encodedFilter == null )
        {
            return null;
        }
                
        if ( operator.equalsIgnoreCase( "like" ) )
        {
            return "'%" + encodedFilter + "%'";
        }
        else if ( operator.equalsIgnoreCase( "in" ) )
        {
            String[] split = encodedFilter.split( OPTION_SEP );
            
            final StringBuffer buffer = new StringBuffer( "(" );        
            
            for ( String el : split )
            {
                buffer.append( "'" ).append( el ).append( "'," );
            }
            
            return buffer.deleteCharAt( buffer.length() - 1 ).append( ")" ).toString();
        }
        
        return "'" + encodedFilter + "'";
    }
    
    public String getItemId()
    {
        return item.getUid();
    }
    
    @Override
    public String toString()
    {
        return "[Item: " + item + ", operator: " + operator + ", filter: " + filter + "]";
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public IdentifiableObject getItem()
    {
        return item;
    }

    public void setItem( IdentifiableObject item )
    {
        this.item = item;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator( String operator )
    {
        this.operator = operator;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter( String filter )
    {
        this.filter = filter;
    }

    public boolean isNumeric()
    {
        return numeric;
    }

    public void setNumeric( boolean numeric )
    {
        this.numeric = numeric;
    }
}
