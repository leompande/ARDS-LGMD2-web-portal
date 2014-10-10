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
package org.hisp.dhis.linelisting.linelistdataelementmapping;

import java.io.Serializable;

/**
 * @author Mithilesh Kumar Thakur
 * 
 * @version LineListDataElementMapping.java Oct 12, 2010 12:28:49 PM
 */
@SuppressWarnings( "serial" )
public class LineListDataElementMapping
    implements Serializable
{
    public static final String DOT_SEPARATOR = ".";

    public static final String COLON_SEPARATOR = ":";

    /**
     * The unique identifier for this LineListDataElementMapping.
     */
    private int id;

    /**
     * The Data Element and Category Combo LineListDataElementMapping.
     */
    private String dataElementExpression;

    /**
     * The Data Line List Group, Element and Option LineListDataElementMapping.
     */
    private String lineListExpression;

    /**
     * A description of the LineListDataElementMapping.
     */
    private String description;

    /**
     * A reference to the LineList Groups, Elements and Options in the
     * LineListDataElementMapping.
     */
    // private String lineListExpression;
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    /**
     * Default empty LineListDataElementMapping
     */
    public LineListDataElementMapping()
    {
    }

    /**
     * Constructor with all the parameters.
     * 
     * @param dataElementExpression The dataElementExpression as a String
     * @param description A description of the LineListDataElementMapping.
     * @param lineListGroupsInExpression A reference to the DataElements in the
     *        LineListDataElementMapping.
     */
    public LineListDataElementMapping( String expression, String description, String lineListExpression )
    {
        this.dataElementExpression = expression;
        this.description = description;
        this.lineListExpression = lineListExpression;
    }

    // -------------------------------------------------------------------------
    // Equals and hashCode
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int PRIME = 31;

        int result = 1;

        result = PRIME * result + ((description == null) ? 0 : description.hashCode());

        result = PRIME * result + ((dataElementExpression == null) ? 0 : dataElementExpression.hashCode());

        result = PRIME * result + ((lineListExpression == null) ? 0 : lineListExpression.hashCode());

        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( obj == null )
        {
            return false;
        }

        if ( getClass() != obj.getClass() )
        {
            return false;
        }

        final LineListDataElementMapping other = (LineListDataElementMapping) obj;

        if ( description == null )
        {
            if ( other.description != null )
            {
                return false;
            }
        }
        else if ( !description.equals( other.description ) )
        {
            return false;
        }

        if ( dataElementExpression == null )
        {
            if ( other.dataElementExpression != null )
            {
                return false;
            }
        }
        else if ( !dataElementExpression.equals( other.dataElementExpression ) )
        {
            return false;
        }

        if ( lineListExpression == null )
        {
            if ( other.lineListExpression != null )
            {
                return false;
            }
        }
        else if ( !lineListExpression.equals( other.lineListExpression ) )
        {
            return false;
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /*
     * public Set<LineListGroup> getLineListGroupsInExpression() { return
     * lineListGroupsInExpression; }
     * 
     * public void setDataElementsInExpression( Set<LineListGroup>
     * lineListGroupsInExpression ) { this.lineListGroupsInExpression =
     * lineListGroupsInExpression; }
     */

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getDataElementExpression()
    {
        return dataElementExpression;
    }

    public void setDataElementExpression( String expression )
    {
        this.dataElementExpression = expression;
    }

    public String getLineListExpression()
    {
        return lineListExpression;
    }

    public void setLineListExpression( String lineListExpression )
    {
        this.lineListExpression = lineListExpression;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }
}
