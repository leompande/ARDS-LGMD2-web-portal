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
import java.util.Date;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@SuppressWarnings("serial")
public class LineListDataValue
    implements Serializable
{

    /**
     * The unique and auto-generated record number for the LineListing Group / Program Value Table
     */
    private int recordNumber;

    /**
     * The period for which the values are to be saved for the specific  LineListing Group
     */
    private Period period;

    /**
     * The period for which the values are to be saved for the specific LineListing Group
     */
    /**
     * The organisation unit the values are saved for the specific LineListing Group
     */
    private OrganisationUnit source;

    private String storedBy;

    private Date timestamp;

    private Map<String, String> lineListValues;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public LineListDataValue()
    {
        // TODO Auto-generated constructor stub
    }

    public LineListDataValue (Period period, OrganisationUnit source,Map<String, String>  lineListValues)
    {
        this.period = period;
        this.source = source;
        this.lineListValues =lineListValues;
    }

    public LineListDataValue (Period period, OrganisationUnit source,Map<String, String>  lineListValues, String storedBy)
    {
        this.period = period;
        this.source = source;
        this.lineListValues =lineListValues;
        this.storedBy = storedBy;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public int getRecordNumber()
    {
        return recordNumber;
    }

    public void setRecordNumber( int recordNumber )
    {
        this.recordNumber = recordNumber;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    public OrganisationUnit getSource()
    {
        return source;
    }

    public void setSource( OrganisationUnit source )
    {
        this.source = source;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public Map<String, String>  getLineListValues()
    {
        return lineListValues;
    }

    public void setLineListValues(Map<String, String>  lineListValues )
    {
        this.lineListValues = lineListValues;
    }

}