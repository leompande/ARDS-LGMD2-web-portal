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
package org.hisp.dhis.detargetdatavalue;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DeTargetDataValue.java Jan 12, 2011 3:17:51 PM
 */
@SuppressWarnings("serial")
public class DeTargetDataValue implements Serializable
{
    
    /**
     * Part of the DeTargetDataValue's composite ID
     */
    private DeTarget deTarget;
    
    /**
     * 
     */
    private DataElement dataelement;
    
    private DataElementCategoryOptionCombo decategoryOptionCombo;

    /**
     * Part of the DeTargetDataValue's composite ID
     */
    private OrganisationUnit source;

    /**
     * 
     */
    private Period period;

    /**
     * 
     */
    private String value;

    /**
     * 
     */
    private String storedBy;

    /**
     * 
     */
    private Date timestamp;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DeTargetDataValue()
    {
    }

    public DeTargetDataValue( DeTarget deTarget, DataElement dataelement, DataElementCategoryOptionCombo decategoryOptionCombo, OrganisationUnit source, Period period, String value )
    {
        this.deTarget = deTarget;
        this.dataelement = dataelement;
        this.decategoryOptionCombo = decategoryOptionCombo;
        this.source = source;
        this.period = period;
        this.value = value;
    }

    public DeTargetDataValue( DeTarget deTarget, DataElement dataelement, DataElementCategoryOptionCombo decategoryOptionCombo, OrganisationUnit source, Period period ,String value, String storedBy,Date timestamp )
    {
        this.deTarget = deTarget;
        this.dataelement = dataelement;
        this.decategoryOptionCombo = decategoryOptionCombo;
        this.source = source;
        this.period = period;
        this.value = value;
        this.storedBy = storedBy;
        this.timestamp = timestamp;
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

        if ( !(o instanceof DeTargetDataValue) )
        {
            return false;
        }

        final DeTargetDataValue other = (DeTargetDataValue) o;

        return deTarget.equals( other.getDeTarget() ) && source.equals( other.getSource() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        
        result = result * prime + deTarget.hashCode();
        result = result * prime + source.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public DeTarget getDeTarget()
    {
        return deTarget;
    }

    public void setDeTarget( DeTarget deTarget )
    {
        this.deTarget = deTarget;
    }

    public DataElement getDataelement()
    {
        return dataelement;
    }

    public void setDataelement( DataElement dataelement )
    {
        this.dataelement = dataelement;
    }

    public DataElementCategoryOptionCombo getDecategoryOptionCombo()
    {
        return decategoryOptionCombo;
    }

    public void setDecategoryOptionCombo( DataElementCategoryOptionCombo decategoryOptionCombo )
    {
        this.decategoryOptionCombo = decategoryOptionCombo;
    }

    public OrganisationUnit getSource()
    {
        return source;
    }

    public void setSource( OrganisationUnit source )
    {
        this.source = source;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    public String getValue()
    {
        return value;
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
    
    
    
}
