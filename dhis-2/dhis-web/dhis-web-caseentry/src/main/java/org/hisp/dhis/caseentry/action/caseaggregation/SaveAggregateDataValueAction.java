package org.hisp.dhis.caseentry.action.caseaggregation;

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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $SaveAggregateDataValueAction.java Feb 27, 2012 10:04:36 AM$
 */
public class SaveAggregateDataValueAction
    implements Action
{
    private String SEPERATE_SIGN = "_";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Set<String> aggregateValues = new HashSet<String>();

    public void setAggregateValues( Set<String> aggregateValues )
    {
        this.aggregateValues = aggregateValues;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        String storedBy = currentUserService.getCurrentUsername() + "_CAE";

        for ( String aggregateValue : aggregateValues )
        {
            // -----------------------------------------------------------------
            // Get params
            // -----------------------------------------------------------------
            
            String[] info = aggregateValue.split( SEPERATE_SIGN );

            int dataElementId = Integer.parseInt( info[0] );
            int optionComboId = Integer.parseInt( info[1] );
            String periodIsoId = info[2];
            int orgunitId = Integer.parseInt( info[3] );
            String resultValue = info[4];
            
            // -----------------------------------------------------------------
            // Create objects
            // -----------------------------------------------------------------

            DataElement dataElement = dataElementService.getDataElement( dataElementId );
            DataElementCategoryOptionCombo optionCombo = categoryService
                .getDataElementCategoryOptionCombo( optionComboId );
            
            Period period = PeriodType.getPeriodFromIsoString( periodIsoId );

            OrganisationUnit orgunit = organisationUnitService.getOrganisationUnit( orgunitId );

            DataValue dataValue = dataValueService.getDataValue( dataElement, period, orgunit, optionCombo );
            
            // -----------------------------------------------------------------
            // Save/Update/Delete data-values
            // -----------------------------------------------------------------

            if ( resultValue != "0.0" )
            {
                if ( dataValue == null )
                {
                    dataValue = new DataValue( dataElement, period, orgunit, optionCombo, optionCombo, "" + resultValue, "", new Date(), null );

                    dataValueService.addDataValue( dataValue );
                }
                else
                {
                    dataValue.setValue( resultValue );
                    dataValue.setTimestamp( new Date() );
                    dataValue.setStoredBy( storedBy );

                    dataValueService.updateDataValue( dataValue );
                }
            }
            else if ( dataValue != null )
            {
                dataValueService.deleteDataValue( dataValue );
            }
        }

        return SUCCESS;
    }
}
