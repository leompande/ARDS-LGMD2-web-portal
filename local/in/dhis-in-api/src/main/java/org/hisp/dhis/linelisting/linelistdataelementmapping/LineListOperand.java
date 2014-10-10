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
 * @version LineListOperand.java Oct 12, 2010 12:39:07 PM
 */
@SuppressWarnings("serial")
public class LineListOperand
    implements Serializable, Comparable<LineListOperand>
{
    public static final String DOT_SEPARATOR = ".";

    public static final String COLON_SEPARATOR = ":";

    private String id;

    private int lineListGroupId;

    private int lineListElementId;

    private int lineListOptionId;

    private String operandName;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public LineListOperand()
    {
    }

    public LineListOperand( int lineListGroupId, int lineListElementId, int lineListOptionId )
    {
        this.id = lineListGroupId + COLON_SEPARATOR + lineListElementId + DOT_SEPARATOR + lineListOptionId;
        this.lineListGroupId = lineListGroupId;
        this.lineListElementId = lineListElementId;
        this.lineListOptionId = lineListOptionId;
    }

    public LineListOperand( int lineListGroupId, int lineListElementId, int lineListOptionId, String operandName )
    {
        this.id = lineListGroupId + COLON_SEPARATOR + lineListElementId + DOT_SEPARATOR + lineListOptionId;
        this.lineListGroupId = lineListGroupId;
        this.lineListElementId = lineListElementId;
        this.lineListOptionId = lineListOptionId;
        this.operandName = operandName;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;

        int result = 1;

        result = prime * result + lineListGroupId;
        result = prime * result + lineListElementId;
        result = prime * result + lineListOptionId;

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

        final LineListOperand other = (LineListOperand) object;

        return lineListGroupId == other.lineListGroupId && lineListElementId == other.lineListElementId
            && lineListOptionId == other.lineListOptionId;
    }

    @Override
    public String toString()
    {
        return "[LineListGroupId: " + lineListGroupId + ", LineListElementId: " + lineListElementId
            + ", LineListOptionId: " + lineListOptionId + "]";
    }

    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public int getLineListGroupId()
    {
        return lineListGroupId;
    }

    public void setLineListGroupId( int lineListGroupId )
    {
        this.lineListGroupId = lineListGroupId;
    }

    public int getLineListElementId()
    {
        return lineListElementId;
    }

    public void setLineListElementId( int lineListElementId )
    {
        this.lineListElementId = lineListElementId;
    }

    public int getLineListOptionId()
    {
        return lineListOptionId;
    }

    public void setLineListOptionId( int lineListOptionId )
    {
        this.lineListOptionId = lineListOptionId;
    }

    public String getOperandName()
    {
        return operandName;
    }

    public void setOperandName( String operandName )
    {
        this.operandName = operandName;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // compareTo
    // -------------------------------------------------------------------------

    public int compareTo( LineListOperand other )
    {
        if ( this.getLineListGroupId() != other.getLineListGroupId() )
        {
            return this.getLineListGroupId() - other.getLineListGroupId();
        }

        if ( this.getLineListElementId() != other.getLineListElementId() )
        {
            return this.getLineListElementId() - other.getLineListElementId();
        }

        return this.getLineListOptionId() - other.getLineListOptionId();
    }

}
