package org.hisp.dhis.caseentry.action.report;

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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patientreport.PatientAggregateReport;
import org.hisp.dhis.patientreport.PatientAggregateReportService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version SaveAggregateReportAction.java 1:16:10 PM Jan 14, 2013 $
 */
public class SaveAggregateReportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientAggregateReportService aggregateReportService;

    public void setAggregateReportService( PatientAggregateReportService aggregateReportService )
    {
        this.aggregateReportService = aggregateReportService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String name;

    private String programId;

    private String programStageId;

    private String startDate;

    private String endDate;

    private List<String> dimension = new ArrayList<String>();

    private List<String> filter = new ArrayList<String>();

    private String ouMode;

    private Integer limit;

    private String sortOrder;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setName( String name )
    {
        this.name = name;
    }

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    public void setLimit( Integer limit )
    {
        this.limit = limit;
    }

    public void setSortOrder( String sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    public void setOuMode( String ouMode )
    {
        this.ouMode = ouMode;
    }

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public void setProgramId( String programId )
    {
        this.programId = programId;
    }

    public void setProgramStageId( String programStageId )
    {
        this.programStageId = programStageId;
    }

    public void setFilter( List<String> filter )
    {
        this.filter = filter;
    }

    public void setDimension( List<String> dimension )
    {
        this.dimension = dimension;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        Program program = programService.getProgram( programId );
        ProgramStage programStage = programStageService.getProgramStage( programStageId );

        PatientAggregateReport aggregateReport = new PatientAggregateReport( name );
        aggregateReport.setStartDate( format.parseDate( startDate ) );
        aggregateReport.setEndDate( format.parseDate( endDate ) );
        aggregateReport.setOuMode( ouMode );
        aggregateReport.setUser( currentUserService.getCurrentUser() );
        aggregateReport.setDimension( dimension );
        aggregateReport.setFilter( filter );
        aggregateReport.setProgramStage( programStage );
        aggregateReport.setProgram( program );
        aggregateReport.setLimit( limit );
        aggregateReport.setSortOrder( sortOrder );
        aggregateReport.setUser( currentUserService.getCurrentUser() );

        aggregateReportService.addPatientAggregateReport( aggregateReport );

        return SUCCESS;
    }
}
