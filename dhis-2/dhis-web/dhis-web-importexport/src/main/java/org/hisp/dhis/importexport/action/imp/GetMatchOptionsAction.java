package org.hisp.dhis.importexport.action.imp;

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

import static org.hisp.dhis.common.Objects.CHART;
import static org.hisp.dhis.common.Objects.CONSTANT;
import static org.hisp.dhis.common.Objects.DATADICTIONARY;
import static org.hisp.dhis.common.Objects.DATAELEMENT;
import static org.hisp.dhis.common.Objects.DATAELEMENTGROUP;
import static org.hisp.dhis.common.Objects.DATAELEMENTGROUPSET;
import static org.hisp.dhis.common.Objects.DATASET;
import static org.hisp.dhis.common.Objects.INDICATOR;
import static org.hisp.dhis.common.Objects.INDICATORGROUP;
import static org.hisp.dhis.common.Objects.INDICATORGROUPSET;
import static org.hisp.dhis.common.Objects.INDICATORTYPE;
import static org.hisp.dhis.common.Objects.ORGANISATIONUNIT;
import static org.hisp.dhis.common.Objects.ORGANISATIONUNITGROUP;
import static org.hisp.dhis.common.Objects.ORGANISATIONUNITGROUPSET;
import static org.hisp.dhis.common.Objects.ORGANISATIONUNITLEVEL;
import static org.hisp.dhis.common.Objects.REPORT;
import static org.hisp.dhis.common.Objects.REPORTTABLE;
import static org.hisp.dhis.common.Objects.VALIDATIONRULE;

import java.util.Collection;

import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.validation.ValidationRuleService;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetMatchOptionsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String objectType;

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType( String type )
    {
        this.objectType = type;
    }

    private Integer objectId;

    public Integer getObjectId()
    {
        return objectId;
    }

    public void setObjectId( Integer id )
    {
        this.objectId = id;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Object importObject;

    public Object getImportObject()
    {
        return importObject;
    }

    private Collection<?> objects;

    public Collection<?> getObjects()
    {
        return objects;
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ImportObjectService importObjectService;

    public void setImportObjectService( ImportObjectService importObjectService )
    {
        this.importObjectService = importObjectService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataDictionaryService dataDictionaryService;

    public void setDataDictionaryService( DataDictionaryService dataDictionaryService )
    {
        this.dataDictionaryService = dataDictionaryService;
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

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private ReportTableService reportTableService;

    public void setReportTableService( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }

    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        importObject = importObjectService.getImportObject( Integer.valueOf( objectId ) );

        if ( objectType.equals( CONSTANT.name() ) )
        {
            objects = constantService.getAllConstants();
        }
        else if ( objectType.equals( DATAELEMENT.name() ) )
        {
            objects = dataElementService.getAllDataElements();
        }
        else if ( objectType.equals( DATAELEMENTGROUP.name() ) )
        {
            objects = dataElementService.getAllDataElementGroups();
        }
        else if ( objectType.equals( DATAELEMENTGROUPSET.name() ) )
        {
            objects = dataElementService.getAllDataElementGroupSets();
        }
        else if ( objectType.equals( INDICATORTYPE.name() ) )
        {
            objects = indicatorService.getAllIndicatorTypes();
        }
        else if ( objectType.equals( INDICATOR.name() ) )
        {
            objects = indicatorService.getAllIndicators();
        }
        else if ( objectType.equals( INDICATORGROUP.name() ) )
        {
            objects = indicatorService.getAllIndicatorGroups();
        }
        else if ( objectType.equals( INDICATORGROUPSET.name() ) )
        {
            objects = indicatorService.getAllIndicatorGroupSets();
        }
        else if ( objectType.equals( DATADICTIONARY.name() ) )
        {
            objects = dataDictionaryService.getAllDataDictionaries();
        }
        else if ( objectType.equals( DATASET.name() ) )
        {
            objects = dataSetService.getAllDataSets();
        }
        else if ( objectType.equals( ORGANISATIONUNIT.name() ) )
        {
            objects = organisationUnitService.getAllOrganisationUnits();
        }
        else if ( objectType.equals( ORGANISATIONUNITGROUP.name() ) )
        {
            objects = organisationUnitGroupService.getAllOrganisationUnitGroups();
        }
        else if ( objectType.equals( ORGANISATIONUNITGROUPSET.name() ) )
        {
            objects = organisationUnitGroupService.getAllOrganisationUnitGroupSets();
        }
        else if ( objectType.equals( ORGANISATIONUNITLEVEL.name() ) )
        {
            objects = organisationUnitService.getOrganisationUnitLevels();
        }
        else if ( objectType.equals( VALIDATIONRULE.name() ) )
        {
            objects = validationRuleService.getAllValidationRules();
        }
        else if ( objectType.equals( REPORT.name() ) )
        {
            objects = reportService.getAllReports();
        }
        else if ( objectType.equals( REPORTTABLE.name() ) )
        {
            objects = reportTableService.getAllReportTables();
        }
        else if ( objectType.equals( CHART.name() ) )
        {
            objects = chartService.getAllCharts();
        }

        return SUCCESS;
    }

}
