package org.hisp.dhis.reporting.exp;

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

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.importexport.synchronous.ExportPivotViewService;
import org.hisp.dhis.importexport.synchronous.ExportPivotViewService.RequestType;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.StreamUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.util.ContextUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Bob Jolliffe
 * 
 * This action is called to export a csv formatted selection of
 * aggregated indicator or data values from datamart. It requires 4
 * parameters: startdate and enddate: 8 character string representation
 * of date - 20100624 root: id of root organization unit level: level
 * number to fetch aggregated values for
 */
public class ExportDataMartAction
    implements Action
{
    // TODO: experiment with different sizes for this to stop data dribbling out
    private static final int GZIPBUFFER = 8192;

    // dummy figure to keep legacy mydatamart happy
    private static final int DUMMYCOUNT = 100000;

    private static final Log log = LogFactory.getLog( ExportDataMartAction.class );

    private static final DateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd" );

    private static final String NO_STARTDATE = "The request is missing a startDate parameter";

    private static final String NO_ENDDATE = "The request is missing an endDate parameter";

    private static final String BAD_STARTDATE = "The request has a bad startDate parameter. Required format is YYYMMDD";

    private static final String BAD_ENDDATE = "The request has a bad endDate parameter. Required format is YYYMMDD";

    private static final String NO_ROOT = "The request is missing a non-zero dataSourceRoot parameter";

    private static final String NO_LEVEL = "The request is missing a non-zero dataSourceLevel parameter";

    private static final String CLIENT_ERROR = "client-error";

    private static final int HTTP_ERROR = 400;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private ExportPivotViewService exportPivotViewService;

    public void setExportPivotViewService( ExportPivotViewService exportPivotViewService )
    {
        this.exportPivotViewService = exportPivotViewService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private int dataSourceLevel;

    public void setDataSourceLevel( int dataSourceLevel )
    {
        this.dataSourceLevel = dataSourceLevel;
    }

    private int dataSourceRoot;

    public void setDataSourceRoot( int dataSourceRoot )
    {
        this.dataSourceRoot = dataSourceRoot;
    }

    private RequestType requestType;

    public void setRequestType( RequestType requestType )
    {
        this.requestType = requestType;
    }

    private String periodType;

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws IOException
    {
        HttpServletRequest request = ServletActionContext.getRequest();

        log.info( "DataMart export request from " + currentUserService.getCurrentUsername() + " @ "
            + request.getRemoteAddr() );

        HttpServletResponse response = ServletActionContext.getResponse();

        // ---------------------------------------------------------------------
        // Check all parameters
        // ---------------------------------------------------------------------

        String paramError = null;

        if ( startDate == null )
        {
            paramError = NO_STARTDATE;
        }

        if ( endDate == null )
        {
            paramError = NO_ENDDATE;
        }

        if ( dataSourceRoot == 0 )
        {
            paramError = NO_ROOT;
        }

        if ( dataSourceLevel == 0 )
        {
            paramError = NO_LEVEL;
        }

        Date start = null;
        Date end = null;

        if ( paramError == null )
        {
            try
            {
                start = dateFormat.parse( startDate );

                if ( start == null )
                {
                    paramError = BAD_STARTDATE;
                }

                end = dateFormat.parse( endDate );

                if ( end == null )
                {
                    paramError = BAD_ENDDATE;
                }
            }
            catch ( java.text.ParseException ex )
            {
                paramError = ex.getMessage();
            }
        }

        if ( paramError != null )
        {
            response.sendError( HTTP_ERROR, paramError );
            log.info( paramError );
            return CLIENT_ERROR;
        }

        // timestamp filename
        SimpleDateFormat format = new SimpleDateFormat( "_yyyy_MM_dd_HHmm_ss" );
        String filename = requestType + format.format( Calendar.getInstance().getTime() ) + ".csv.gz";

        PeriodType pType = PeriodType.getPeriodTypeByName( periodType );

        // prepare to write output
        OutputStream out = null;

        // how many rows do we expect
        // int count = exportPivotViewService.count( requestType, pType, start, end, dataSourceLevel, dataSourceRoot );
        
        // Turns out it is too expensive to count the size of the resultset on large datamarts
        // so we just return a dummy value here
        int count = DUMMYCOUNT;
        
        ContextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_GZIP, true, filename, true );

        // write number of rows to custom header
        response.addHeader( "X-Number-Of-Rows", String.valueOf( count ) );

        try
        {
            out = new GZIPOutputStream( response.getOutputStream(), GZIPBUFFER );
            exportPivotViewService.execute( out, requestType, pType, start, end, dataSourceLevel, dataSourceRoot );
        }
        finally
        {
            StreamUtils.closeOutputStream( out );
        }

        return SUCCESS;
    }
}
