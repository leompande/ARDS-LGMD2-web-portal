package org.hisp.dhis.reports.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.source.Source;

import com.opensymphony.xwork2.Action;

/**
 * @author Kristian
 * @version $Id: DefineReportAssociationsAction.java 3648 2007-10-15 22:47:45Z larshelg $
 */
public class DefineReportAssociationsAction
    implements Action
{

	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private ReportService reportService;

	public void setReportService(ReportService reportService) 
	{
		this.reportService = reportService;
	}
 
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

	private int reportId;
    
	public void setReportId(int reportId) 
	{
		this.reportId = reportId;
	}

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	
        Collection<OrganisationUnit> rootUnits = selectionTreeManager.getRootOrganisationUnits(); 
        
        Set<OrganisationUnit> unitsInTheTree = new HashSet<OrganisationUnit>();        
        
        getUnitsInTheTree( rootUnits, unitsInTheTree );          
	
    	Report_in report = reportService.getReport( reportId );    	
    	
    	Set<Source> assignedSources = report.getSources();
    	
    	assignedSources.removeAll( convert( unitsInTheTree ) );        

    	Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager.getSelectedOrganisationUnits();
    	
    	assignedSources.addAll( convert( selectedOrganisationUnits ) );  	
    	
    	report.setSources( assignedSources );
    	
        reportService.updateReport( report );
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Set<Source> convert( Collection<OrganisationUnit> organisationUnits )
    {
        Set<Source> sources = new HashSet<Source>();
        
        sources.addAll( organisationUnits );
        
        return sources;
    }   
    
    private void getUnitsInTheTree( Collection<OrganisationUnit> rootUnits, Set<OrganisationUnit> unitsInTheTree )
    {
    	for( OrganisationUnit root : rootUnits )
        {
    		unitsInTheTree.add( root );
    		getUnitsInTheTree( root.getChildren(), unitsInTheTree );    		
        }
    }
}
