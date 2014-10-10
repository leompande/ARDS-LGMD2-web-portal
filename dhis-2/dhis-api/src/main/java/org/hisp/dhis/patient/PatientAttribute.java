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

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.WithoutOrganisationUnitsView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Abyot Asalefew
 */
@JacksonXmlRootElement( localName = "personAttribute", namespace = DxfNamespaces.DXF_2_0 )
public class PatientAttribute
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 3026922158464592390L;

    public static final String TYPE_DATE = "date";

    public static final String TYPE_STRING = "string";

    public static final String TYPE_INT = "number";

    public static final String TYPE_BOOL = "bool";

    public static final String TYPE_TRUE_ONLY = "trueOnly";

    public static final String TYPE_COMBO = "combo";

    public static final String TYPE_PHONE_NUMBER = "phoneNumber";

    public static final String TYPE_TRACKER_ASSOCIATE = "trackerAssociate";

    public static final String TYPE_USERS = "users";

    public static final String TYPE_AGE = "age";

    private String description;

    private String valueType;

    private boolean mandatory;

    private Boolean inherit = false;

    private Boolean groupBy = false;

    private PatientAttributeGroup patientAttributeGroup;

    private Set<PatientAttributeOption> attributeOptions = new HashSet<PatientAttributeOption>();

    private String expression;

    private Boolean displayOnVisitSchedule = false;

    private Integer sortOrderInVisitSchedule;

    private Boolean displayInListNoProgram = false;

    private Integer sortOrderInListNoProgram;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientAttribute()
    {
        setAutoFields();
    }

    public PatientAttribute( String name, String description, String valueType, boolean mandatory, Boolean inherit,
        Boolean displayOnVisitSchedule )
    {
        this.name = name;
        this.description = description;
        this.valueType = valueType;
        this.mandatory = mandatory;
        this.inherit = inherit;
        this.displayOnVisitSchedule = displayOnVisitSchedule;

        setAutoFields();
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Indicates whether the value type of this attribute is numeric.
     */
    public boolean isNumericType()
    {
        return TYPE_INT.equals( valueType );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty( "personAttributeOptions" )
    @JsonView( { DetailedView.class } )
    @JacksonXmlElementWrapper( localName = "personAttributeOptions", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "personAttributeOption", namespace = DxfNamespaces.DXF_2_0 )
    public Set<PatientAttributeOption> getAttributeOptions()
    {
        return attributeOptions == null ? new HashSet<PatientAttributeOption>() : attributeOptions;
    }

    public void setAttributeOptions( Set<PatientAttributeOption> attributeOptions )
    {
        this.attributeOptions = attributeOptions;
    }

    public void addAttributeOptions( PatientAttributeOption option )
    {
        if ( attributeOptions == null )
        {
            attributeOptions = new HashSet<PatientAttributeOption>();
        }

        attributeOptions.add( option );
    }

    @JsonProperty
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getInherit()
    {
        return inherit;
    }

    public void setInherit( Boolean inherit )
    {
        this.inherit = inherit;
    }

    @JsonProperty
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getGroupBy()
    {
        return groupBy;
    }

    public void setGroupBy( Boolean groupBy )
    {
        this.groupBy = groupBy;
    }

    @JsonProperty
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isMandatory()
    {
        return mandatory;
    }

    public void setMandatory( boolean mandatory )
    {
        this.mandatory = mandatory;
    }

    @JsonProperty
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    // TODO remove WithoutOrganisationUnitsView, temporary hack
    @JsonProperty
    @JsonView( { DetailedView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getValueType()
    {
        return valueType;
    }

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    @JsonProperty( "personAttributeGroup" )
    @JsonView( { DetailedView.class } )
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JacksonXmlProperty( localName = "personAttributeGroup", namespace = DxfNamespaces.DXF_2_0 )
    public PatientAttributeGroup getPatientAttributeGroup()
    {
        return patientAttributeGroup;
    }

    public void setPatientAttributeGroup( PatientAttributeGroup patientAttributeGroup )
    {
        this.patientAttributeGroup = patientAttributeGroup;
    }

    @JsonProperty
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    @JsonProperty
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getDisplayOnVisitSchedule()
    {
        return displayOnVisitSchedule;
    }

    public void setDisplayOnVisitSchedule( Boolean displayOnVisitSchedule )
    {
        this.displayOnVisitSchedule = displayOnVisitSchedule;
    }

    @JsonProperty
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Integer getSortOrderInVisitSchedule()
    {
        return sortOrderInVisitSchedule;
    }

    public void setSortOrderInVisitSchedule( Integer sortOrderInVisitSchedule )
    {
        this.sortOrderInVisitSchedule = sortOrderInVisitSchedule;
    }

    @JsonProperty
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getDisplayInListNoProgram()
    {
        return displayInListNoProgram;
    }

    public void setDisplayInListNoProgram( Boolean displayInListNoProgram )
    {
        this.displayInListNoProgram = displayInListNoProgram;
    }

    @JsonProperty
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Integer getSortOrderInListNoProgram()
    {
        return sortOrderInListNoProgram;
    }

    public void setSortOrderInListNoProgram( Integer sortOrderInListNoProgram )
    {
        this.sortOrderInListNoProgram = sortOrderInListNoProgram;
    }

    // -------------------------------------------------------------------------
    // Static methods
    // -------------------------------------------------------------------------

    public static Date getDateFromAge( int age )
    {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.clear( Calendar.MILLISECOND );
        todayCalendar.clear( Calendar.SECOND );
        todayCalendar.clear( Calendar.MINUTE );
        todayCalendar.set( Calendar.HOUR_OF_DAY, 0 );

        todayCalendar.add( Calendar.YEAR, -1 * age );

        return todayCalendar.getTime();
    }

    public static int getAgeFromDate( Date date )
    {
        if ( date == null )
        {
            return -1;
        }

        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime( date );

        Calendar todayCalendar = Calendar.getInstance();

        int age = todayCalendar.get( Calendar.YEAR ) - birthCalendar.get( Calendar.YEAR );

        if ( todayCalendar.get( Calendar.MONTH ) < birthCalendar.get( Calendar.MONTH ) )
        {
            age--;
        }
        else if ( todayCalendar.get( Calendar.MONTH ) == birthCalendar.get( Calendar.MONTH )
            && todayCalendar.get( Calendar.DAY_OF_MONTH ) < birthCalendar.get( Calendar.DAY_OF_MONTH ) )
        {
            age--;
        }

        return age;
    }
}