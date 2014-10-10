package org.hisp.dhis.reportsheet.exporting.action;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.ReportLocationManager;
import org.hisp.dhis.reportsheet.action.ActionSupport;
import org.hisp.dhis.reportsheet.state.SelectionManager;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ValidateGenerateExportReportAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ExportReportService exportReportService;

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private SelectionManager selectionManager;

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private ReportLocationManager reportLocationManager;

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private CompleteDataSetRegistrationService dataSetRegistrationService;

    public void setDataSetRegistrationService( CompleteDataSetRegistrationService dataSetRegistrationService )
    {
        this.dataSetRegistrationService = dataSetRegistrationService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String[] exportReportIds;

    public void setExportReportIds( String[] exportReportIds )
    {
        this.exportReportIds = exportReportIds;
    }

    private List<Integer> orderedGroupIds = new ArrayList<Integer>();

    public void setOrderedGroupIds( List<Integer> orderedGroupIds )
    {
        this.orderedGroupIds = orderedGroupIds;
    }

    private String periodIndex;

    public void setPeriodIndex( String periodIndex )
    {
        this.periodIndex = periodIndex;
    }

    private Boolean generateByDataSet = false;

    public void setGenerateByDataSet( Boolean generateByDataSet )
    {
        this.generateByDataSet = generateByDataSet;
    }

    private Integer selectedDataSetId;

    public void setSelectedDataSetId( Integer selectedDataSetId )
    {
        this.selectedDataSetId = selectedDataSetId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( generateByDataSet )
        {
            DataSet dataSet = dataSetService.getDataSet( selectedDataSetId );

            if ( dataSet == null )
            {
                message = i18n.getString( "specified_data_set_unavailable" );

                return ERROR;
            }

            ExportReport exportReport = exportReportService.getExportReportByDataSet( dataSet );

            if ( exportReport == null )
            {
                message = i18n.getString( "specified_data_set_unassign_to_export_report" );

                return ERROR;
            }

            exportReportIds = new String[] { exportReport.getId() + "" };
        }

        if ( exportReportIds == null || exportReportIds.length == 0 )
        {
            message = i18n.getString( "specify_export_report" );

            return ERROR;
        }

        File templateDirectory = reportLocationManager.getExportReportTemplateDirectory();

        if ( templateDirectory == null || !templateDirectory.exists() )
        {
            message = i18n.getString( "template_folder_is_null" );

            return ERROR;
        }

        for ( String exportReportId : exportReportIds )
        {
            Integer reportId = Integer.parseInt( exportReportId );

            ExportReport exportReport = exportReportService.getExportReport( reportId );

            if ( exportReport == null )
            {
                message = i18n.getString( "the_specified_report_is_not_exist" );

                return ERROR;
            }

            File templateFile = new File( templateDirectory, exportReport.getExcelTemplateFile() );

            if ( templateFile == null || !templateFile.exists() )
            {
                message = i18n.getString( "template_file_is_not_exist" );

                return ERROR;
            }

            /** Validate for exportItems * */
            // message = exportReportService.validateEmportItems( exportReport,
            // i18n );
            //
            // if ( message != null )
            // {
            // return ERROR;
            // }
            /** Validate for whether data set completed or not * */
            // Period period = PeriodType.createPeriodExternalId( periodIndex );
            //
            // for ( DataSet ds : exportReport.getDataSets() )
            // {
            // CalendarPeriodType periodType = (CalendarPeriodType)
            // ds.getPeriodType();
            //
            // List<Period> periods = periodType.generatePeriods(
            // period.getStartDate() );
            // Collection<Period> persistedPeriods =
            // periodService.getPeriodsByPeriodType( periodType );
            //
            // periods.retainAll( persistedPeriods );
            //
            // for ( Period p : periods )
            // {
            // for ( OrganisationUnit o : ds.getSources() )
            // {
            // if ( dataSetRegistrationService.getCompleteDataSetRegistration(
            // ds, p, o ) == null )
            // {
            // message = i18n.getString( "org_unit_with_name" ) + " \"" +
            // o.getDisplayName() + " \".";
            // message += i18n.getString( "data_set_with_name" ) + " \"" +
            // ds.getDisplayName() + "\" ";
            // message += i18n.getString( "uncompleted" );
            //
            // return ERROR;
            // }
            // }
            // }
            // }
        }

        selectionManager.setSelectedPeriodIndex( periodIndex );

        if ( exportReportIds.length == 1 )
        {
            selectionManager.setSelectedReportId( Integer.parseInt( exportReportIds[0] ) );
        }
        else
        {
            selectionManager.setListObject( exportReportIds );
        }

        selectionManager.setOrderedGroupList( orderedGroupIds );

        return SUCCESS;
    }
}
