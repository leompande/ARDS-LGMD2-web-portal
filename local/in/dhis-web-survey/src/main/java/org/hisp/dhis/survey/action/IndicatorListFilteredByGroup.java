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
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyService;

import com.opensymphony.xwork2.Action;

/**
 * @author Kristian
 * @version $Id: DataElementListFilteredByGroup.java 6256 2008-11-10 17:10:30Z larshelg $
 */
public class IndicatorListFilteredByGroup
    implements Action
{
    private String indicatorGroupId;

    private String selectedIndicators[];

    private List<Indicator> indicators;

    private Integer surveyId;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private SurveyService surveyService;

    public void setSurveyService( SurveyService surveyService )
    {
        this.surveyService = surveyService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public void setIndicatorGroupId( String indicatorGroupId )
    {
        this.indicatorGroupId = indicatorGroupId;
    }

    public void setSelectedIndicators( String[] selectedIndicators )
    {
        this.selectedIndicators = selectedIndicators;
    }

    public void setSurveyId( Integer surveyId )
    {
        this.surveyId = surveyId;
    }

    public String getIndicatorGroupId()
    {
        return indicatorGroupId;
    }

    public List<Indicator> getIndicators()
    {
        return indicators;
    }
    
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        //System.out.println("\n\n +++ \n indicatorGroupId   is  : " + indicatorGroupId ); 
        
        if (  indicatorGroupId == null || indicatorGroupId.equalsIgnoreCase( "ALL" ) )
        {
        	indicators = new ArrayList<Indicator>( indicatorService.getAllIndicators() );
        }
        else
        {
        	IndicatorGroup indicatorGroup = indicatorService.getIndicatorGroup( Integer.parseInt( indicatorGroupId ) );

        	indicators = new ArrayList<Indicator>( indicatorGroup.getMembers() );
        }
       // System.out.println("\n\n +++ \n selectedIndicators list  is  : " + selectedIndicators + " ,selectedIndicators list size is : "  + selectedIndicators.length );
        if ( selectedIndicators != null && selectedIndicators.length > 0 )
        {
            Iterator<Indicator> iter = indicators.iterator();

            while ( iter.hasNext() )
            {
            	Indicator indicator = iter.next();
            	//System.out.println("\n\n +++ \n Indicator Id is   : " + indicator.getId() + " , Indicator name is :" + indicator.getName() );
            	
                for ( int i = 0; i < selectedIndicators.length; i++ )
                {
                    //System.out.println("\n\n +++ \n Indicator Id is   : " + indicator.getId() + " , Indicator name is :" + indicator.getName() );
                    if ( indicator.getId() == Integer.parseInt( selectedIndicators[i] ) )
                    {
                        iter.remove();
                    }
                }
            }
        }

        if ( surveyId != null )
        {
        	Survey survey = surveyService.getSurvey( surveyId );
        	
                indicators.removeAll( survey.getIndicators() );
        }

      //  Collections.sort( indicators, indicatorComparator );

      //  displayPropertyHandler.handle( indicators );

        return SUCCESS;
    }
}
