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

import org.hisp.dhis.common.AbstractNameableObject;
import org.hisp.dhis.common.IdentifiableObject;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

@SuppressWarnings( "serial" )
public class Attribute 
	extends AbstractNameableObject
{
    private int id;

    private String name;

    private String description;
    
    private String caption;
    
    private boolean compulsory = false;

    private boolean isUnique = false;

    private boolean history = false;
    
    private DataType dataType;
    
    private InputType inputType;
    
    private Set<AttributeOptions> attributeOptions = new HashSet<AttributeOptions>();
    
    private AttributeGroup attributeGroup;
    
    private Set<DataValues> dataValues = new HashSet<DataValues>();
    
    private Set<History> histories = new HashSet<History>();
    
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Attribute()
    {
    }
    
    public Attribute(String name, DataType dataType, InputType inputType)
    {
    	this.name = name;
    	this.inputType = inputType;
    	this.dataType = dataType;
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

        if ( !(o instanceof Attribute) )
        {
            return false;
        }

        final Attribute other = (Attribute) o;

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

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }
    
    public String getCaption()
    {
        return caption;
    }

    public void setCaption( String caption )
    {
        this.caption = caption;
    }
    
    public Boolean getCompulsory()
    {
        return compulsory;
    }

    public void setCompulsory( Boolean compulsory )
    {
        this.compulsory = compulsory;
    }
    
    public Boolean getIsUnique()
    {
        return isUnique;
    }

    public void setIsUnique( Boolean isUnique )
    {
        this.isUnique = isUnique;
    } 
    
    public Boolean getHistory()
    {
        return history;
    }

    public void setHistory( Boolean history )
    {
        this.history = history;
    }
    
    public DataType getDataType()
    {
        return dataType;
    }

    public void setDataType( DataType dataType )
    {
        this.dataType = dataType;
    }
    
    public InputType getInputType()
    {
        return inputType;
    }

    public void setInputType( InputType inputType )
    {
        this.inputType = inputType;
    }
    
    public Set<AttributeOptions> getAttributeOptions()
    {
        return attributeOptions;
    }

    public void setAttributeOptions( Set<AttributeOptions> attributeOptions )
    {
        this.attributeOptions = attributeOptions;
    }
    
    public AttributeGroup getAttributeGroup()
    {
        return attributeGroup;
    }

    public void setAttributeGroup( AttributeGroup attributeGroup )
    {
        this.attributeGroup = attributeGroup;
    }    
    
    public Set<DataValues> getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( Set<DataValues> dataValues )
    {
        this.dataValues = dataValues;
    }  
    
    public Set<History> getHistories()
    {
        return histories;
    }

    public void setHistories( Set<History> histories )
    {
        this.histories = histories;
    }  
}

