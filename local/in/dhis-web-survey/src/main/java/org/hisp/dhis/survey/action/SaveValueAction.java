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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.state.SelectedStateManager;
import org.hisp.dhis.surveydatavalue.SurveyDataValue;
import org.hisp.dhis.surveydatavalue.SurveyDataValueService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * TODO Replace this with StatefulDataValueSaver
 * @author Torgeir Lorange Ostby
 * @version $Id: SaveValueAction.java 5652 2008-09-06 13:24:34Z larshelg $
 */
public class SaveValueAction
    implements Action
{
    private static final Log LOG = LogFactory.getLog( SaveValueAction.class );    

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
        
    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }    
    
    private IndicatorService indicatorService;
    
    public void setIndicatorService ( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    private SurveyDataValueService surveyDataValueService;
    
    public void setSurveyDataValueService ( SurveyDataValueService surveyDataValueService )
    {
        this.surveyDataValueService = surveyDataValueService;
    }
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }

    private int indicatorId;

    public int getIndicatorId()
    {
        return indicatorId;
    }
    
    public void setIndicatorId( int indicatorId )
    {
        this.indicatorId = indicatorId;
    }    

    private int statusCode=0;

    public int getStatusCode()
    {
        return statusCode;
    }

    private Date timestamp;

    public Date getTimestamp()
    {
        return timestamp;
    }

    private String storedBy;

    public String getStoredBy()
    {
        return storedBy;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()    
    {    	
    	System.out.println("inside action");
        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();
        
        Survey survey = selectedStateManager.getSelectedSurvey();
        
        Indicator indicator = indicatorService.getIndicator( indicatorId );

        storedBy = currentUserService.getCurrentUsername();       
        
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }
        
        if ( value != null )
        {
            value = value.trim();
        }   
        
        // ---------------------------------------------------------------------
        // Save or update
        // ---------------------------------------------------------------------      
                  
        SurveyDataValue dataValue = surveyDataValueService.getSurveyDataValue( organisationUnit, survey, indicator );

        if ( dataValue == null )
        {
            if ( value != null )
            {
                LOG.debug( "Adding DataValue, value added" );

                dataValue = new SurveyDataValue( survey, indicator, organisationUnit, value, storedBy, new Date() );

                surveyDataValueService.addSurveyDataValue( dataValue );
            }
        }
        else
        { 	
	        LOG.debug( "Updating DataValue, value added/changed" );
	
	        dataValue.setValue( value );
	        dataValue.setTimestamp( new Date() );
	        dataValue.setStoredBy( storedBy );
	
	        surveyDataValueService.updateSurveyDataValue( dataValue );  
	        System.out.println("check value : "+value);
	        
        }
       
        if ( dataValue != null )
        {
            this.timestamp = dataValue.getTimestamp();
            this.storedBy = dataValue.getStoredBy();
        }
        
        return SUCCESS;        
    }   
}
