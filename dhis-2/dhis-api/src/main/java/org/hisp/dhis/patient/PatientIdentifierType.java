package org.hisp.dhis.patient;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.period.PeriodType;

@XmlRootElement( name = "patientIdentifierType", namespace = DxfNamespaces.DXF_2_0 )
@XmlAccessorType( value = XmlAccessType.NONE )
public class PatientIdentifierType
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -4750240762379125498L;

    public static final String FIRST_INDEX = ".00000";

    public static final String VALUE_TYPE_TEXT = "text";

    public static final String VALUE_TYPE_NUMBER = "number";

    public static final String VALUE_TYPE_LETTER = "letter";

    public static final String VALUE_TYPE_LOCAL_ID = "localId";

    private String description;

    private boolean mandatory;

    private boolean related;

    private Integer noChars;

    private String type;

    // For Local ID type

    private Boolean orgunitScope = false;

    private Boolean programScope = false;

    private PeriodType periodType;

    private Boolean displayInListNoProgram = false;

    private Integer sortOrderInListNoProgram;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientIdentifierType()
    {
    }

    public PatientIdentifierType( String name, String description )
    {
        this.name = name;
        this.description = description;
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

    public boolean isMandatory()
    {
        return mandatory;
    }

    public void setMandatory( boolean mandatory )
    {
        this.mandatory = mandatory;
    }

    public boolean isRelated()
    {
        return related;
    }

    public void setRelated( boolean related )
    {
        this.related = related;
    }

    public Integer getNoChars()
    {
        return noChars;
    }

    public void setNoChars( Integer noChars )
    {
        this.noChars = noChars;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public Boolean getOrgunitScope()
    {
        return orgunitScope;
    }

    public void setOrgunitScope( Boolean orgunitScope )
    {
        this.orgunitScope = orgunitScope;
    }

    public Boolean getProgramScope()
    {
        return programScope;
    }

    public void setProgramScope( Boolean programScope )
    {
        this.programScope = programScope;
    }

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    public Boolean getDisplayInListNoProgram()
    {
        return displayInListNoProgram;
    }

    public void setDisplayInListNoProgram( Boolean displayInListNoProgram )
    {
        this.displayInListNoProgram = displayInListNoProgram;
    }

    public Integer getSortOrderInListNoProgram()
    {
        return sortOrderInListNoProgram;
    }

    public void setSortOrderInListNoProgram( Integer sortOrderInListNoProgram )
    {
        this.sortOrderInListNoProgram = sortOrderInListNoProgram;
    }

}
