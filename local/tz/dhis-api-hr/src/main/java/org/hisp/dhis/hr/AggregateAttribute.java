/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.hr;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.common.AbstractNameableObject;
import org.hisp.dhis.common.IdentifiableObject;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */

public class AggregateAttribute
	extends AbstractNameableObject
	{
	
	private int id;
	
	private String name;

    private DataElement dataelement;
    
    private Set<AttributeOptions> attributeOptions = new HashSet<AttributeOptions>();
    
    private Set<Criteria> criterias = new HashSet<Criteria>();

 // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public AggregateAttribute()
    {
    }
    
    public AggregateAttribute(String name)
    {
    	this.name = name;
    }
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
    
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof AggregateAttribute) )
        {
            return false;
        }

        final AggregateAttribute other = (AggregateAttribute) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public int getId()
    {
        return id;
    }
    
    public void setId( int id )
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }
    
    public void setDataelement(DataElement dataelement )
    {
    	this.dataelement = dataelement;
    }
    
    public DataElement getDataelement()
    {
    	return dataelement;
    }
    
    public Set<AttributeOptions> getAttributeOptions()
    {
        return attributeOptions;
    }

    public void setAttributeOptions( Set<AttributeOptions> attributeOptions )
    {
        this.attributeOptions = attributeOptions;
    }
    
    public Set<Criteria> getCriterias()
    {
        return criterias;
    }

    public void setCriterias( Set<Criteria> criterias )
    {
        this.criterias = criterias;
    }
}
