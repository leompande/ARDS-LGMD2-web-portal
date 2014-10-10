package org.hisp.dhis.patientreport;


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

public class TabularReportColumn
{
    public static String PREFIX_META_DATA = "meta";
    public static String PREFIX_IDENTIFIER_TYPE = "iden";
    public static String PREFIX_FIXED_ATTRIBUTE = "fixedAttr";
    public static String PREFIX_PATIENT_ATTRIBUTE = "attr";
    public static String PREFIX_DATA_ELEMENT = "de";
    public static String PREFIX_NUMBER_DATA_ELEMENT = "numberDe";

    private String prefix;

    private String identifier;

    private boolean hidden;

    private String query;

    private String operator;

    private String name;

    private boolean dateType;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public TabularReportColumn()
    {
    }

    public TabularReportColumn( String prefix, String identifier, String name, boolean hidden, String operator,
        String query )
    {
        this.prefix = prefix;
        this.identifier = identifier;
        this.name = name;
        this.hidden = hidden;
        this.query = query;
        this.operator = operator;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean hasQuery()
    {
        return (operator != null && !operator.isEmpty()) || (query != null && !query.isEmpty());
    }

    public Integer getIdentifierAsInt()
    {
        return identifier != null ? Integer.parseInt( identifier ) : null;
    }

    public boolean isMeta()
    {
        return PREFIX_META_DATA.equals( prefix );
    }

    public boolean isIdentifierType()
    {
        return PREFIX_IDENTIFIER_TYPE.equals( prefix );
    }

    public boolean isFixedAttribute()
    {
        return PREFIX_FIXED_ATTRIBUTE.equals( prefix );
    }

    public boolean isDynamicAttribute()
    {
        return PREFIX_PATIENT_ATTRIBUTE.equals( prefix );
    }

    public boolean isDataElement()
    {
        return PREFIX_DATA_ELEMENT.equals( prefix ) || PREFIX_NUMBER_DATA_ELEMENT.equals( prefix );
    }

    public boolean isNumberDataElement()
    {
        return PREFIX_NUMBER_DATA_ELEMENT.equals( prefix );
    }

    // -------------------------------------------------------------------------
    // Get methods
    // -------------------------------------------------------------------------

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier( String identifier )
    {
        this.identifier = identifier;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden( boolean hidden )
    {
        this.hidden = hidden;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery( String query )
    {
        this.query = query;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public boolean isDateType()
    {
        return dateType;
    }

    public void setDateType( boolean dateType )
    {
        this.dateType = dateType;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator( String operator )
    {
        this.operator = operator;
    }
}
