package org.hisp.dhis.aggregation;

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

import org.hisp.dhis.common.AggregatedValue;

/**
 * @author Lars Helge Overland
 */
public class AggregatedIndicatorValue
    extends AggregatedValue
{
    private int indicatorId;
    
    private String annualized;
    
    private double factor;
    
    private double numeratorValue;
    
    private double denominatorValue;

    private transient String indicatorName;
    
    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------
    
    public AggregatedIndicatorValue()
    {
    }
    
    /**
     * @param indicatorId indicator id
     * @param periodId period id
     * @param periodTypeId period type id
     * @param organisationUnitId organisation unit id
     * @param level level
     * @param factor factor
     * @param value value
     * @param numeratorValue numerator value
     * @param denominatorValue denominator value
     */
    public AggregatedIndicatorValue( int indicatorId, int periodId, int periodTypeId, int organisationUnitId, 
        int level, String annualized, double factor, double value, double numeratorValue, double denominatorValue )
    {
        this.indicatorId = indicatorId;
        this.periodId = periodId;
        this.periodTypeId = periodTypeId;
        this.organisationUnitId = organisationUnitId;
        this.level = level;
        this.annualized = annualized;
        this.factor = factor;
        this.value = value;
        this.numeratorValue = numeratorValue;
        this.denominatorValue = denominatorValue;
    }

    /**
     * @param indicatorId indicator id
     * @param periodId period id
     * @param periodTypeId period type id
     * @param organisationUnitId organisation unit id
     * @param organisationUnitGroupId organisation unit group id
     * @param level level
     * @param factor factor
     * @param value value
     * @param numeratorValue numerator value
     * @param denominatorValue denominator value
     */
    public AggregatedIndicatorValue( int indicatorId, int periodId, int periodTypeId, int organisationUnitId, 
        int organisationUnitGroupId, int level, String annualized, double factor, double value, double numeratorValue, double denominatorValue )
    {
        this.indicatorId = indicatorId;
        this.periodId = periodId;
        this.periodTypeId = periodTypeId;
        this.organisationUnitId = organisationUnitId;
        this.organisationUnitGroupId = organisationUnitGroupId;
        this.level = level;
        this.annualized = annualized;
        this.factor = factor;
        this.value = value;
        this.numeratorValue = numeratorValue;
        this.denominatorValue = denominatorValue;
    }

    // ----------------------------------------------------------------------
    // Logic
    // ----------------------------------------------------------------------
    
    public void clear()
    {
        this.indicatorId = 0;
        this.periodId = 0;
        this.periodTypeId = 0;
        this.organisationUnitId = 0;
        this.level = 0;
        this.factor = 0.0;
        this.value = 0.0;
        this.numeratorValue = 0.0;
        this.denominatorValue = 0.0;
    }

    @Override
    public int getElementId()
    {
        return indicatorId;
    }
    
    // ----------------------------------------------------------------------
    // Getters and setters
    // ----------------------------------------------------------------------

    public int getIndicatorId()
    {
        return indicatorId;
    }

    public void setIndicatorId( int indicatorId )
    {
        this.indicatorId = indicatorId;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel( int level )
    {
        this.level = level;
    }

    public double getDenominatorValue()
    {
        return denominatorValue;
    }

    public void setDenominatorValue( double denominatorValue )
    {
        this.denominatorValue = denominatorValue;
    }

    public String getAnnualized()
    {
        return annualized;
    }

    public void setAnnualized( String annualized )
    {
        this.annualized = annualized;
    }

    public double getFactor()
    {
        return factor;
    }

    public void setFactor( double factor )
    {
        this.factor = factor;
    }

    public double getNumeratorValue()
    {
        return numeratorValue;
    }

    public void setNumeratorValue( double numeratorValue )
    {
        this.numeratorValue = numeratorValue;
    }

    public String getIndicatorName()
    {
        return indicatorName;
    }

    public void setIndicatorName( String indicatorName )
    {
        this.indicatorName = indicatorName;
    }

    // ----------------------------------------------------------------------
    // hashCode and equals
    // ----------------------------------------------------------------------

    @Override
    public String toString()
    {
        return 
            "[Indicator: " + indicatorId +
            " period: " + periodId +
            " org unit: " + organisationUnitId +
            " org unit group: " + organisationUnitGroupId +
            " value: " + value + "]";
    }
    
    @Override
    public int hashCode()
    {
        return indicatorId * periodId * organisationUnitId * organisationUnitGroupId * 17;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        
        if ( o == null || !( o instanceof AggregatedIndicatorValue ) )
        {
            return false;
        }
        
        AggregatedIndicatorValue that = (AggregatedIndicatorValue) o;
        
        return this.indicatorId == that.getIndicatorId() && 
            this.periodId == that.getPeriodId() && 
            this.organisationUnitId == that.getOrganisationUnitId() &&
            this.organisationUnitGroupId == that.getOrganisationUnitGroupId();
    }
}
