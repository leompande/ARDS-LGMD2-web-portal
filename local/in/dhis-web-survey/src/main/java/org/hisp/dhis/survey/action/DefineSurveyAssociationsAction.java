package org.hisp.dhis.survey.action;

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

import java.util.HashSet;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyService;

import com.opensymphony.xwork2.Action;

/**
 * @author Kristian
 * @version $Id: DefineDataSetAssociationsAction.java 3648 2007-10-15 22:47:45Z larshelg $
 */
public class DefineSurveyAssociationsAction
    implements Action
{
    private int surveyId;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private SurveyService surveyService;

    public void setSurveyService( SurveyService surveyService )
    {
        this.surveyService = surveyService;
    }    
 
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public void setSurveyId( int surveyId )
    {
        this.surveyId = surveyId;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	
        Survey survey = surveyService.getSurvey( surveyId ); 
        
        survey.updateOrganisationUnits( new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() ) );
        
        surveyService.updateSurvey( survey );
        
        //Collection<OrganisationUnit> rootUnits = selectionTreeManager.getRootOrganisationUnits(); 
        
       // Set<OrganisationUnit> unitsInTheTree = new HashSet<OrganisationUnit>();        
        
        //getUnitsInTheTree( rootUnits, unitsInTheTree );          
	
        /*
        Survey survey = surveyService.getSurvey( surveyId );    	
    	
    	Set<OrganisationUnit> assignedSources = survey.getSources();
    	
    	assignedSources.removeAll( assignedSources );
    	
    	//assignedSources.removeAll( unitsInTheTree );        

    	Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager.getSelectedOrganisationUnits();
    	
    	assignedSources.addAll( selectedOrganisationUnits );  	
    	
    	survey.setSources( assignedSources );
    	
    	surveyService.updateSurvey( survey );
        */
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
/*    
    private void getUnitsInTheTree( Collection<OrganisationUnit> rootUnits, Set<OrganisationUnit> unitsInTheTree )
    {
    	for( OrganisationUnit root : rootUnits )
        {
    		unitsInTheTree.add( root );
    		getUnitsInTheTree( root.getChildren(), unitsInTheTree );    		
        }
    }
    */
}
