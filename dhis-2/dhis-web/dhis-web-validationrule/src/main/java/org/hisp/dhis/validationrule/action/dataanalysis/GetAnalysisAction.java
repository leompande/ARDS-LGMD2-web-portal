package org.hisp.dhis.validationrule.action.dataanalysis;

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

import com.opensymphony.xwork2.Action;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.ServiceProvider;
import org.hisp.dhis.dataanalysis.DataAnalysisService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.util.SessionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds outliers in given data elements for given sources in a given period and
 * displays a list of them.
 *
 * @author Jon Moen Drange, Peter Flem, Dag Haavi Finstad, Lars Helge Oeverland
 * @version $Id: GetOutliersAction.java 1005 2009-06-04 13:29:44Z jonmd $
 */
public class GetAnalysisAction
    implements Action
{
    private static final Log log = LogFactory.getLog( GetAnalysisAction.class );

    public static final String KEY_ANALYSIS_DATA_VALUES = "analysisDataValues";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ServiceProvider<DataAnalysisService> serviceProvider;

    public void setServiceProvider( ServiceProvider<DataAnalysisService> serviceProvider )
    {
        this.serviceProvider = serviceProvider;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String key;

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    private String toDate;

    public void setToDate( String toDate )
    {
        this.toDate = toDate.trim();
    }

    private String fromDate;

    public void setFromDate( String fromDate )
    {
        this.fromDate = fromDate.trim();
    }

    private Collection<String> dataSets;

    public void setDataSets( Collection<String> dataSets )
    {
        this.dataSets = dataSets;
    }

    private Double standardDeviation;

    public void setStandardDeviation( Double standardDeviation )
    {
        this.standardDeviation = standardDeviation;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<DeflatedDataValue> dataValues = new ArrayList<DeflatedDataValue>();

    public Collection<DeflatedDataValue> getDataValues()
    {
        return dataValues;
    }

    private boolean maxExceeded;

    public boolean isMaxExceeded()
    {
        return maxExceeded;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Set<DataElement> dataElements = new HashSet<DataElement>();
        Collection<Period> periods = null;
        OrganisationUnit unit = selectionTreeManager.getReloadedSelectedOrganisationUnit();
        Collection<OrganisationUnit> orgUnits = null;

        // TODO filter periods with data element period type

        if ( fromDate != null && toDate != null && dataSets != null && unit != null )
        {
            orgUnits = organisationUnitService.getOrganisationUnitWithChildren( unit.getId() );

            periods = periodService.getPeriodsBetweenDates( format.parseDate( fromDate ), format.parseDate( toDate ) );

            for ( String id : dataSets )
            {
                dataElements.addAll( dataSetService.getDataSet( Integer.parseInt( id ) ).getDataElements() );
            }

            log.info( "From date: " + fromDate + ", To date: " + toDate + ", Organisation unit: " + unit
                + ", Std dev: " + standardDeviation + ", Key: " + key );

            log.info( "Nr of data elements: " + dataElements.size() + " Nr of periods: " + periods.size() );
        }

        DataAnalysisService service = serviceProvider.provide( key );

        if ( service != null ) // Follow-up analysis has no input params
        {
            dataValues = service.analyse( orgUnits, dataElements, periods, standardDeviation );

            maxExceeded = dataValues.size() > DataAnalysisService.MAX_OUTLIERS;
        }

        SessionUtils.setSessionVar( KEY_ANALYSIS_DATA_VALUES, dataValues );

        return SUCCESS;
    }
}
