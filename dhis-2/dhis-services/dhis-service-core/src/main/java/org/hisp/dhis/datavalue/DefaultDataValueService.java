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

import static org.hisp.dhis.system.util.ValidationUtils.dataValueIsValid;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kristian Nordal
 */
@Transactional
public class DefaultDataValueService
    implements DataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataValueStore dataValueStore;

    public void setDataValueStore( DataValueStore dataValueStore )
    {
        this.dataValueStore = dataValueStore;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------

    public void addDataValue( DataValue dataValue )
    {
        if ( !dataValue.isNullValue() && dataValueIsValid( dataValue.getValue(), dataValue.getDataElement() ) == null )
        {
            if ( dataValue.getCategoryOptionCombo() == null )
            {
                dataValue.setCategoryOptionCombo( categoryService.getDefaultDataElementCategoryOptionCombo() );
            }
            
            if ( dataValue.getAttributeOptionCombo() == null )
            {
                dataValue.setAttributeOptionCombo( categoryService.getDefaultDataElementCategoryOptionCombo() );
            }
            
            dataValueStore.addDataValue( dataValue );
        }
    }

    public void updateDataValue( DataValue dataValue )
    {
        if ( dataValue.isNullValue() )
        {
            deleteDataValue( dataValue );
        }
        else if ( dataValueIsValid( dataValue.getValue(), dataValue.getDataElement() ) == null )
        {
            dataValueStore.updateDataValue( dataValue );
        }
    }

    @Transactional
    public void deleteDataValue( DataValue dataValue )
    {
        dataValueStore.deleteDataValue( dataValue );
    }

    @Transactional
    public int deleteDataValuesBySource( OrganisationUnit source )
    {
        return dataValueStore.deleteDataValuesBySource( source );
    }

    @Transactional
    public int deleteDataValuesByDataElement( DataElement dataElement )
    {
        return dataValueStore.deleteDataValuesByDataElement( dataElement );
    }

    public DataValue getDataValue( DataElement dataElement, Period period, OrganisationUnit source, DataElementCategoryOptionCombo categoryOptionCombo )
    {
        DataElementCategoryOptionCombo defaultOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        return dataValueStore.getDataValue( dataElement, period, source, categoryOptionCombo, defaultOptionCombo );
    }

    public DataValue getDataValue( DataElement dataElement, Period period, OrganisationUnit source, 
        DataElementCategoryOptionCombo categoryOptionCombo, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        return dataValueStore.getDataValue( dataElement, period, source, categoryOptionCombo, attributeOptionCombo );
    }
    
    public DataValue getDataValue( int dataElementId, int periodId, int sourceId, int categoryOptionComboId )
    {
        DataElementCategoryOptionCombo defaultOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        return dataValueStore.getDataValue( dataElementId, periodId, sourceId, categoryOptionComboId, defaultOptionCombo.getId() );
    }
    
    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    public Collection<DataValue> getAllDataValues()
    {
        return dataValueStore.getAllDataValues();
    }

    public Collection<DataValue> getDataValues( OrganisationUnit source, Period period )
    {
        return dataValueStore.getDataValues( source, period );
    }

    public Collection<DataValue> getDataValues( OrganisationUnit source, DataElement dataElement )
    {
        return dataValueStore.getDataValues( source, dataElement );
    }

    public Collection<DataValue> getDataValues( Collection<OrganisationUnit> sources, DataElement dataElement )
    {
        return dataValueStore.getDataValues( sources, dataElement );
    }

    public Collection<DataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements )
    {
        return dataValueStore.getDataValues( source, period, dataElements );
    }

    public Collection<DataValue> getDataValues( OrganisationUnit source, Period period, 
        Collection<DataElement> dataElements, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        return dataValueStore.getDataValues( source, period, dataElements, attributeOptionCombo );
    }

    public Collection<DataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements,
        Collection<DataElementCategoryOptionCombo> optionCombos )
    {
        return dataValueStore.getDataValues( source, period, dataElements, optionCombos );
    }

    public Collection<DataValue> getDataValues( DataElement dataElement, Period period,
        Collection<OrganisationUnit> sources )
    {
        return dataValueStore.getDataValues( dataElement, period, sources );
    }

    public Collection<DataValue> getDataValues( DataElement dataElement, Collection<Period> periods,
        Collection<OrganisationUnit> sources )
    {
        return dataValueStore.getDataValues( dataElement, periods, sources );
    }

    public Collection<DataValue> getDataValues( DataElement dataElement, DataElementCategoryOptionCombo optionCombo,
        Collection<Period> periods, Collection<OrganisationUnit> sources )
    {
        return dataValueStore.getDataValues( dataElement, optionCombo, periods, sources );
    }

    public Collection<DataValue> getDataValues( Collection<DataElementCategoryOptionCombo> optionCombos )
    {
        return dataValueStore.getDataValues( optionCombos );
    }

    public Collection<DataValue> getDataValues( DataElement dataElement )
    {
        return dataValueStore.getDataValues( dataElement );
    }

    public DataValue getLatestDataValues( DataElement dataElement, PeriodType periodType,
        OrganisationUnit organisationUnit )
    {
        return dataValueStore.getLatestDataValues( dataElement, periodType, organisationUnit );
    }

    public int getDataValueCount( int days )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.DAY_OF_YEAR, (days * -1) );

        return dataValueStore.getDataValueCount( cal.getTime() );
    }
    
    public Map<DataElementOperand, Double> getDataValueMap( Collection<DataElement> dataElements, Period period, OrganisationUnit source )
    {
        return dataValueStore.getDataValueMap( dataElements, period, source );
    }
    
    public Map<DataElementOperand, Double> getDataValueMap( Collection<DataElement> dataElements, Date date, OrganisationUnit source,
    		Collection<PeriodType> periodTypes, Map<DataElementOperand, Date> lastUpdatedMap )
    {
    	return dataValueStore.getDataValueMap( dataElements, date, source, periodTypes, lastUpdatedMap );
    }
    
    public Collection<DeflatedDataValue> getDeflatedDataValues( int dataElementId, int periodId, Collection<Integer> sourceIds )
    {
        return dataValueStore.getDeflatedDataValues( dataElementId, periodId, sourceIds );
    }
}
