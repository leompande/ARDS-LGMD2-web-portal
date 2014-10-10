package org.hisp.dhis.linelisting;

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
import java.util.ArrayList;
import java.util.Collection;

//import org.hisp.dhis.common.AbstractIdentifiableObject;

@SuppressWarnings("serial")
//public class LineListElement extends AbstractIdentifiableObject
public class LineListElement
    
    implements Serializable
{
    public static final String TYPE_STRING = "string";

    public static final String TYPE_INT = "int";

    public static final String TYPE_BOOL = "bool";
    
    public static final String TYPE_DATE = "date";
    
    /**
     * The unique identifier for this LineListing Element / Entry Element
     */
    private int id;

    /**
     * Name of Element / Entry Element. Required and unique.
     */
    private String name;
    
    /**
     * Short Name of Element / Entry Element. Required and unique.
     */
    private String shortName;
    
    
    /**
     * Description of the LineListing Element.
     */
    private String description;
    
    /**
     * Data Type of the Line List Element - Used to determine what type of data
     * is required to be collected: Possible data types could be Text, Number, Yes-No
     * Date, Time, 
     */    
    private String dataType;
    
    /**
     * Data Collection format of the Line List Element at Data Entry Screen - Possible 
     * presentation types could be: Combo box, text field, radio button
     */
    private String presentationType;
    
    /**
     * Options of the presentation type - Used for displaying the options in combo
     * box presenation
     */
    private Collection<LineListOption> lineListElementOptions = new ArrayList<LineListOption>();
    
    /**
     * Indicating position in the custom sort order.
     */
    
    private Integer sortOrder;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public LineListElement()
    {
    }
    
    public LineListElement(String name)
    {
        this.name = name;
    }
    
    public LineListElement(String name, String dataType, String presentationType)
    {
        this.name = name;
        this.dataType = dataType;
        this.presentationType = presentationType;
    }
    
    public LineListElement (String name, String shortName, String dataType, String presentationType)
    {
        this.name = name;
        this.shortName = shortName;
        this.dataType = dataType;
        this.presentationType = presentationType;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
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

        if ( !(o instanceof LineListElement) )
        {
            return false;
        }

        final LineListElement other = (LineListElement) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

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

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getDataType()
    {
        return dataType;
    }

    public void setDataType( String dataType )
    {
        this.dataType = dataType;
    }

    public String getPresentationType()
    {
        return presentationType;
    }

    public void setPresentationType( String presentationType )
    {
        this.presentationType = presentationType;
    }

    public Collection<LineListOption> getLineListElementOptions()
    {
        return lineListElementOptions;
    }

    public void setLineListElementOptions( Collection<LineListOption> lineListElementOptions )
    {
        this.lineListElementOptions = lineListElementOptions;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    public String getCode()
    {
        return shortName;
    }

    public void setCode( String shortName )
    {
        this.shortName = shortName;
    }
    
    public String getAlternativeName()
    {
        return getShortName();
    }
    
    public void setAlternativeName( String alternativeName )
    {
        throw new UnsupportedOperationException( "Cannot set alternativename on LineListElement: " + alternativeName );
    }

}
