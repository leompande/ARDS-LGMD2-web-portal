package org.hisp.dhis.reportsheet.preview.action;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.action.ActionSupport;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class AutoGenerateFormRollBack
    extends ActionSupport
{
    private static final Map<String, String> keyMap = new HashMap<String, String>()
    {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        {
            put( "de", "data_element_duplicated" );
            put( "id", "indicator_duplicated" );
            put( "vr", "validation_rule_duplicated" );
        }
    };

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private ValidationRuleService validationRuleService;

    @Autowired
    private ExportReportService exportReportService;

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private Integer exportReportId;

    public void setExportReportId( Integer exportReportId )
    {
        this.exportReportId = exportReportId;
    }

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Set<Integer> dataElementIds = new HashSet<Integer>();

    public void setDataElementIds( Set<Integer> dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    private Set<Integer> indicatorIds = new HashSet<Integer>();

    public void setIndicatorIds( Set<Integer> indicatorIds )
    {
        this.indicatorIds = indicatorIds;
    }

    private Set<Integer> validationRuleIds = new HashSet<Integer>();

    public void setValidationRuleIds( Set<Integer> validationRuleIds )
    {
        this.validationRuleIds = validationRuleIds;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        try
        {
            if ( message == null || message.trim().isEmpty() )
            {
                message = "";
            }
            else
            {
                String values[] = message.split( "@" );
                
                message = i18n.getString( keyMap.get( values[0] ) ) + " \"" + values[1] + "\"<br/>";
                message += "----------------------------------<br/>";
                message += i18n.getString( "rollback_result" ) + "<br/>";
                message += "----------------------------------<br/>";
            }

            if ( exportReportId > 0 )
            {
                ExportReport exportReport = exportReportService.getExportReport( exportReportId );

                if ( exportReport != null )
                {
                    message += i18n.getString( "report_with_name" ) + " \"" + exportReport.getDisplayName();

                    exportReportService.deleteExportReport( exportReportId );

                    message += "\" " + i18n.getString( "deleted" );
                }
            }
            if ( dataSetId > 0 )
            {
                DataSet ds = dataSetService.getDataSet( dataSetId );

                if ( ds != null )
                {
                    message += "<br/>" + i18n.getString( "data_set_with_name" ) + " \"" + ds.getDisplayName();

                    dataSetService.deleteDataSet( ds );

                    message += "\" " + i18n.getString( "deleted" );
                }
            }

            int i = 0;

            if ( indicatorIds != null && !indicatorIds.isEmpty() )
            {
                for ( Integer id : indicatorIds )
                {
                    Indicator indicator = indicatorService.getIndicator( id );

                    if ( indicator != null )
                    {
                        indicatorService.deleteIndicator( indicator );
                        i++;
                    }
                }

                message += "<br/>" + i18n.getString( "indicators" ) + ": " + i + "/" + indicatorIds.size() + " "
                    + i18n.getString( "deleted" );
            }
            if ( validationRuleIds != null && !validationRuleIds.isEmpty() )
            {
                i = 0;

                for ( Integer id : validationRuleIds )
                {
                    validationRuleService.deleteValidationRule( validationRuleService.getValidationRule( id ) );
                    i++;
                }

                message += "<br/>" + i18n.getString( "validation_rules" ) + ": " + i + "/" + validationRuleIds.size()
                    + " " + i18n.getString( "deleted" );
            }
            if ( dataElementIds != null && !dataElementIds.isEmpty() )
            {
                i = 0;

                for ( Integer id : dataElementIds )
                {
                    dataElementService.deleteDataElement( dataElementService.getDataElement( id ) );
                    i++;
                }

                message += "<br/>" + i18n.getString( "data_elements" ) + ": " + i + "/" + dataElementIds.size() + " "
                    + i18n.getString( "deleted" );
            }
        }
        catch ( Exception e )
        {
            message = i18n.getString( "auto_roll_back_failed" );

            e.printStackTrace();

            return ERROR;
        }

        return SUCCESS;
    }
}