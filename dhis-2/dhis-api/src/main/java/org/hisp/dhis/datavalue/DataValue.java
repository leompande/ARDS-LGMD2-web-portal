package org.hisp.dhis.datavalue;

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

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

import org.hisp.dhis.common.ImportableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Kristian Nordal
 * @version $Id: DataValue.java 4638 2008-02-25 10:06:47Z larshelg $
 */
public class DataValue
    implements Serializable, ImportableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6269303850789110610L;

    private static final Pattern ZERO_PATTERN = Pattern.compile( "^0(\\.0*)?$" );
    
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private DataElement dataElement;

    private Period period;

    private OrganisationUnit source;

    private DataElementCategoryOptionCombo categoryOptionCombo;

    private DataElementCategoryOptionCombo attributeOptionCombo;

    private String value;

    private String storedBy;

    private Date timestamp;

    private String comment;

    private Boolean followup;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataValue()
    {
    }

    /**
     * @param dataElement the data element.
     * @param period the period.
     * @param source the organisation unit.
     * @param categoryOptionCombo the category option combo.
     */
    public DataValue( DataElement dataElement, Period period, OrganisationUnit source, 
        DataElementCategoryOptionCombo categoryOptionCombo, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.categoryOptionCombo = categoryOptionCombo;
        this.attributeOptionCombo = attributeOptionCombo;
    }

    /**
     * @param dataElement the data element.
     * @param period the period.
     * @param source the organisation unit.
     * @param categoryOptionCombo the category option combo.
     * @param attributeOptionCombo the attribute option combo.
     * @param value the value.
     * @param storedBy the user that stored this data value.
     * @param timestamp the time of creation of this data value.
     * @param comment the comment.
     */
    public DataValue( DataElement dataElement, Period period, OrganisationUnit source, DataElementCategoryOptionCombo categoryOptionCombo, 
        DataElementCategoryOptionCombo attributeOptionCombo, String value, String storedBy, Date timestamp, String comment )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.categoryOptionCombo = categoryOptionCombo;
        this.attributeOptionCombo = attributeOptionCombo;
        this.value = value;
        this.storedBy = storedBy;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    // -------------------------------------------------------------------------
    // Dimension
    // -------------------------------------------------------------------------

    public String getMeasure()
    {
        return value;
    }

    public String getName()
    {
        throw new UnsupportedOperationException();
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Alias for getCategoryOptionCombo(). TODO remove.
     */
    public DataElementCategoryOptionCombo getOptionCombo()
    {
        return getCategoryOptionCombo();
    }
    
    public boolean isZero()
    {
        return dataElement != null && dataElement.getType().equals( DataElement.VALUE_TYPE_INT )
            && value != null && ZERO_PATTERN.matcher( value ).find();
    }

    public boolean isNullValue()
    {
        return value == null && comment == null;
    }

    public boolean isFollowup()
    {
        return followup != null && followup;
    }

    public boolean hasComment()
    {
        return comment != null && !comment.isEmpty();
    }
    
    public void toggleFollowUp()
    {
        if ( this.followup == null )
        {
            this.followup = true;
        }
        else
        {
            this.followup = !this.followup;
        }
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

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

        if ( !(o instanceof DataValue) )
        {
            return false;
        }

        final DataValue other = (DataValue) o;

        return dataElement.equals( other.getDataElement() ) && categoryOptionCombo.equals( other.getCategoryOptionCombo() )
            && period.equals( other.getPeriod() ) && source.equals( other.getSource() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + categoryOptionCombo.hashCode();
        result = result * prime + period.hashCode();
        result = result * prime + dataElement.hashCode();
        result = result * prime + source.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
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

    public DataElementCategoryOptionCombo getCategoryOptionCombo()
    {
        return categoryOptionCombo;
    }

    public void setCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo )
    {
        this.categoryOptionCombo = categoryOptionCombo;
    }

    public String getValue()
    {
        return value;
    }

    public DataElementCategoryOptionCombo getAttributeOptionCombo()
    {
        return attributeOptionCombo;
    }

    public void setAttributeOptionCombo( DataElementCategoryOptionCombo attributeOptionCombo )
    {
        this.attributeOptionCombo = attributeOptionCombo;
    }

    public void setValue( String value )
    {
        this.value = value;
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

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    public Boolean getFollowup()
    {
        return followup;
    }

    public void setFollowup( Boolean followup )
    {
        this.followup = followup;
    }
}
