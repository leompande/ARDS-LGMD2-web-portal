package org.hisp.dhis.customvalue;

/*
 * Copyright (c) 2004-2009, University of Oslo
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

import java.io.Serializable;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;

/**
 * @author Latifov Murodillo Abdusamadovich
 * 
 * @version $Id$
 */
public class CustomValue
    implements Serializable
{
    /**
     * The unique identifier for this CustomValue
     */
    private int id;

    /**
     * Dataset object value to be assigned to
     */
    private DataSet dataSet;

    /**
     * DataElement object value to be assigned to
     */
    private DataElement dataElement;

    /**
     * DataElementCategoryOptionCombo object value to be assigned to
     */
    private DataElementCategoryOptionCombo optionCombo;

    /**
     * Custom value
     */
    private String customValue;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public CustomValue()
    {
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

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public DataElementCategoryOptionCombo getOptionCombo()
    {
        return optionCombo;
    }

    public void setOptionCombo( DataElementCategoryOptionCombo optionCombo )
    {
        this.optionCombo = optionCombo;
    }

    public String getCustomValue()
    {
        return customValue;
    }

    public void setCustomValue( String customValue )
    {
        this.customValue = customValue;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        
        int result = 1;
        
        result = prime * result + ( ( customValue == null) ? 0 : customValue.hashCode() );
        result = prime * result + ( ( dataElement == null) ? 0 : dataElement.hashCode() );
        result = prime * result + ( ( dataSet == null) ? 0 : dataSet.hashCode() );
        result = prime * result + ( ( optionCombo == null) ? 0 : optionCombo.hashCode() );
        
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
        
        final CustomValue other = (CustomValue) object;
        
        if ( customValue == null )
        {
            if ( other.customValue != null )
            {
                return false;
            }
        }        
        else if ( !customValue.equals( other.customValue ) )
        {
            return false;
        }
        
        if ( dataElement == null )
        {
            if ( other.dataElement != null )
            {
                return false;
            }
        }
        else if ( !dataElement.equals( other.dataElement ) )
        {
            return false;
        }
        
        if ( dataSet == null )
        {
            if ( other.dataSet != null )
            {
                return false;
            }
        }
        else if ( !dataSet.equals( other.dataSet ) )
        {
            return false;
        }
        
        if ( optionCombo == null )
        {
            if ( other.optionCombo != null )
            {
                return false;
            }
        }
        else if ( !optionCombo.equals( other.optionCombo ) )
        {
            return false;
        }
        
        return true;
    }
}
