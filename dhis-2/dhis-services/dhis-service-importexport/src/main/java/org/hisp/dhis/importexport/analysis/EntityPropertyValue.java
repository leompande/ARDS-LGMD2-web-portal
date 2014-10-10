package org.hisp.dhis.importexport.analysis;

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

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class EntityPropertyValue
    implements Comparable<EntityPropertyValue>
{
    private String entity;

    private String property;

    private String value;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EntityPropertyValue( Class<?> entity, String property, String value )
    {
        this.entity = entity.getSimpleName();
        this.property = property;
        this.value = value;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getEntity()
    {
        return entity;
    }

    public void setEntity( String entity )
    {
        this.entity = entity;
    }

    public String getProperty()
    {
        return property;
    }

    public void setProperty( String property )
    {
        this.property = property;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals, toString, compareTo
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;        
        
        int result = 1;
        
        result = prime * result + ( ( entity == null ) ? 0 : entity.hashCode() );
        result = prime * result + ( ( property == null ) ? 0 : property.hashCode() );
        result = prime * result + ( ( value == null ) ? 0 : value.hashCode() );
        
        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null )
        {
            return false;
        }
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        final EntityPropertyValue other = (EntityPropertyValue) object;
        
        if ( entity == null )
        {
            if ( other.entity != null )
            {
                return false;
            }
        }
        else if ( !entity.equals( other.entity ) )
        {
            return false;
        }
        
        if ( property == null )
        {
            if ( other.property != null )
            {
                return false;
            }
        }
        else if ( !property.equals( other.property ) )
        {
            return false;
        }
        
        if ( value == null )
        {
            if ( other.value != null )
            {
                return false;
            }
        }
        else if ( !value.equals( other.value ) )
        {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString()
    {
        return "[" + entity + ", " + property + ", " + value + "]";
    }

    public int compareTo( EntityPropertyValue other )
    {
        if ( this == other )
        {
            return 0;
        }
        
        int compare1 = compareTo( entity, other.entity );
        int compare2 = compareTo( property, other.property );
        int compare3 = compareTo( value, other.value );
        
        return ( compare1 != 0 ) ? compare1 : ( compare2 != 0 ) ? compare2 : compare3;
    }
    
    private int compareTo( String string1, String string2 )
    {
        if ( string1 == null && string2 == null )
        {
            return 0;
        }
        
        if ( string1 == null )
        {
            return -1;
        }
        
        if ( string2 == null )
        {
            return 1;
        }
        
        return string1.compareTo( string2 );
    }
}
