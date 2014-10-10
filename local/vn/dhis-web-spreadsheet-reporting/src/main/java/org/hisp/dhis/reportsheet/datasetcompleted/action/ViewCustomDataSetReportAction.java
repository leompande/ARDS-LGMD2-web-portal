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
package org.hisp.dhis.reportsheet.datasetcompleted.action;

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */

public class ViewCustomDataSetReportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    private DataSetService dataSetService;

    private DataValueService dataValueService;

    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    private Integer periodId;

    private Integer organisationUnitId;

    private String customDataEntryFormCode;

    private DataSet dataSet;

    private Period period;

    private OrganisationUnit organisationUnit;

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public Period getPeriod()
    {
        return period;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    public String getCustomDataEntryFormCode()
    {
        return this.customDataEntryFormCode;
    }

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public String execute()
        throws Exception
    {
        period = periodService.getPeriod( periodId.intValue() );

        dataSet = dataSetService.getDataSet( dataSetId.intValue() );

        organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId.intValue() );

        Map<String, String> dataValues = new HashMap<String, String>();
        for ( DataElement dataElement : dataSet.getDataElements() )
        {
            DataElementCategoryCombo catCombo = dataElement.getCategoryCombo();
            for ( DataElementCategoryOptionCombo optionCombo : catCombo.getOptionCombos() )
            {
                if ( dataElement.getType().equals( DataElement.VALUE_TYPE_INT ) )
                {
                    DataValue value = dataValueService
                        .getDataValue( organisationUnit, dataElement, period, optionCombo );
                    if ( value != null )
                    {
                        dataValues.put( dataElement.getId() + ":" + optionCombo.getId(), value.getValue() );
                    }

                }

            }
        }

        // ---------------------------------------------------------------------
        // Get the custom data entry form if any
        // ---------------------------------------------------------------------

        DataEntryForm dataEntryForm = dataSet.getDataEntryForm();

        customDataEntryFormCode = CustomDataSetReportGenerator.prepareReportContent( dataEntryForm.getHtmlCode(),
            dataValues );

        return SUCCESS;
    }

}
