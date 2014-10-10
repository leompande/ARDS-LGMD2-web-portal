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

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.patientreport.PatientAggregateReport;
import org.hisp.dhis.patientreport.PatientAggregateReportService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version GetAggregateReportListAction.java 1:38:47 PM Jan 14, 2013 $
 */
public class GetAggregateReportListAction
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

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<PatientAggregateReport> reports = new HashSet<PatientAggregateReport>();

    public Collection<PatientAggregateReport> getReports()
    {
        return reports;
    }

    private String query;

    public void setQuery( String query )
    {
        this.query = query;
    }

    private Integer total;

    public Integer getTotal()
    {
        return total;
    }

    private Integer pageSize;

    public void setPageSize( Integer pageSize )
    {
        this.pageSize = pageSize;
    }

    private Integer currentPage;

    public void setCurrentPage( Integer currentPage )
    {
        this.currentPage = currentPage;
    }

    public Integer getCurrentPage()
    {
        return currentPage;
    }

    private Integer pageCount;

    public Integer getPageCount()
    {
        return pageCount;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        User user = currentUserService.getCurrentUser();

        total = aggregateReportService.countPatientAggregateReportList( user, query );

        pageSize = (pageSize == null) ? 10 : pageSize;
        int startPos = (currentPage == null || currentPage <= 0) ? 0 : (currentPage - 1) * pageSize;
        startPos = (startPos > total) ? total : startPos;

        pageCount = (total % pageSize == 0) ? (total / pageSize) : (total / pageSize + 1);

        reports = aggregateReportService.getPatientAggregateReports( currentUserService.getCurrentUser(), query,
            startPos, pageSize );

        return SUCCESS;
    }

}
