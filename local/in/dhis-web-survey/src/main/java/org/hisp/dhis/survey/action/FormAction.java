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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyService;
import org.hisp.dhis.survey.comparator.SurveyNameComparator;
import org.hisp.dhis.survey.state.SelectedStateManager;
import org.hisp.dhis.surveydatavalue.SurveyDataValue;
import org.hisp.dhis.surveydatavalue.SurveyDataValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: FormAction.java 6216 2008-11-06 18:06:42Z eivindwa $
 */
public class FormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }
    
    private SurveyDataValueService surveyDataValueService;
    
    public void setSurveyDataValueService ( SurveyDataValueService surveyDataValueService )
    {
        this.surveyDataValueService = surveyDataValueService;
    }
    
    private SurveyService surveyService;

    public void setSurveyService( SurveyService surveyService )
    {
        this.surveyService = surveyService;
    }
    
    @SuppressWarnings("unused")
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    } 
    
    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    @SuppressWarnings("unused")
    /*
    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }
    */
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private List<Indicator> orderedIndicators = new ArrayList<Indicator>();

    public List<Indicator> getOrderedIndicators()
    {
        return orderedIndicators;
    }

    private Map<Integer, SurveyDataValue> dataValueMap;

    public Map<Integer, SurveyDataValue> getDataValueMap()
    {
        return dataValueMap;
    }

    public void setSelectedSurveyId( Integer selectedSurveyId )
    {
        this.selectedSurveyId = selectedSurveyId;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer selectedSurveyId;

    public Integer getSelectedSurveyId()
    {
        return selectedSurveyId;
    }
    //code for Select Action
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private List<Survey> surveys = new ArrayList<Survey>();

    public Collection<Survey> getSurveys()
    {
        return surveys;
    }

    private int flage;
    
    public int getFlage()
    {
        return flage;
    }

    public void setFlage( int flage )
    {
        this.flage = flage;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {    	
   	
        //code for Select Action
        

        // ---------------------------------------------------------------------
        // Validate selected OrganisationUnit
        // ---------------------------------------------------------------------
        
        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();
        
        //System.out.println( "---organisationUnit --  " + organisationUnit.getName() );
        
        if ( organisationUnit == null )
        {
            selectedSurveyId = null;
 
            selectedStateManager.clearSelectedSurvey();     

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Load and Sort surveys
        // ---------------------------------------------------------------------

        surveys = selectedStateManager.loadSurveysForSelectedOrgUnit( organisationUnit );       
        
        Collections.sort( surveys, new SurveyNameComparator() );

        // ---------------------------------------------------------------------
        // Validate selected surveys
        // ---------------------------------------------------------------------

        Survey selectedSurvey;

        if ( selectedSurveyId != null )
        {
            selectedSurvey = surveyService.getSurvey( selectedSurveyId );
        }
        else
        {
            selectedSurvey = selectedStateManager.getSelectedSurvey();
        }

        if ( selectedSurvey != null && surveys.contains( selectedSurvey ) )
        {
            selectedSurveyId = selectedSurvey.getId();
            selectedStateManager.setSelectedSurvey( selectedSurvey );
        }
        else
        {
            selectedSurveyId = null;
            
            selectedStateManager.clearSelectedSurvey();

            return SUCCESS;
        }
        flage = 1;
        
    	OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        Survey survey = selectedStateManager.getSelectedSurvey();
      
        List<Indicator> indicators = new ArrayList<Indicator>(survey.getIndicators());        

        if ( indicators.size() == 0 )
        {
            return SUCCESS;
        }  

        // ---------------------------------------------------------------------
        // Get the DataValues and create a map
        // ---------------------------------------------------------------------

        Collection<SurveyDataValue> dataValues = surveyDataValueService.getSurveyDataValues( organisationUnit, survey );
        
        dataValueMap = new HashMap<Integer, SurveyDataValue>( dataValues.size() );

        for ( SurveyDataValue dataValue : dataValues )
        {
            dataValueMap.put( dataValue.getIndicator().getId(), dataValue );
        }
   
        // ---------------------------------------------------------------------
        // Working on the display of indicators
        // ---------------------------------------------------------------------

        orderedIndicators = indicators;
        
        //displayPropertyHandler.handle( orderedDataElements );
        
        return SUCCESS;
    }

}
