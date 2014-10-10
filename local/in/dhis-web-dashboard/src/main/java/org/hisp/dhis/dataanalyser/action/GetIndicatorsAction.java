package org.hisp.dhis.dataanalyser.action;

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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyService;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id: GetIndicatorsAction.java 3305 2007-05-14 18:55:52Z larshelg $
 */
public class GetIndicatorsAction
    implements Action
{
    private final static int ALL = 0;

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
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<Indicator> indicatorComparator;

    public void setIndicatorComparator( Comparator<Indicator> indicatorComparator )
    {
        this.indicatorComparator = indicatorComparator;
    }

    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------
    /*
    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }
    */
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }
/*
    private String surveyflag;
    
    public void setSurveyflag( String surveyflag )
    {
        this.surveyflag = surveyflag;
    }
*/
    private List<Indicator> indicators;

    public List<Indicator> getIndicators()
    {
        return indicators;
    }
    
    private String surveyExist;
    
    public void setSurveyExist( String surveyExist )
    {
        this.surveyExist = surveyExist;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( id == null || id == ALL )
        {
            indicators = new ArrayList<Indicator>( indicatorService.getAllIndicators() );
        }
        else
        {
            IndicatorGroup indicatorGroup = indicatorService.getIndicatorGroup( id );

            if ( indicatorGroup != null )
            {
                indicators = new ArrayList<Indicator>( indicatorGroup.getMembers() );
            }
            else
            {
                indicators = new ArrayList<Indicator>();
            }
        }
        
       
        // To Filter the indicators that are assigned to any survey
        /*
        if( surveyflag != null && surveyflag.equalsIgnoreCase( "yes" ) )
        {
            List<Indicator> surveyIndicators = new ArrayList<Indicator>( surveyService.getAllSurveyIndicators() );
            
            indicators.retainAll( surveyIndicators );
        }
        System.out.println( "id = "+id + " indicator size = "+ indicators.size() );
        */
        
     // filter all the indicators which have not any survey
        if( surveyExist != null && surveyExist.equalsIgnoreCase( "yes" ) )
        {
            System.out.println( "surveyExist" + surveyExist );
            Iterator<Indicator> allIndicatorIterator = indicators.iterator();
            while ( allIndicatorIterator.hasNext() )
            {
                Indicator indicator = allIndicatorIterator.next();
                Collection<Survey> surveyList = surveyService.getSurveysByIndicator( indicator );
                //surveyList = surveyService.getSurveysByIndicator( indicator );
                
                if ( surveyList == null || surveyList.size()<=0 )
                {
                    allIndicatorIterator.remove();
                }
                
            }
        }
        
       System.out.println("id = "+id + " indicator final size = "+ indicators.size());
        
       Collections.sort( indicators, indicatorComparator );
        
       //displayPropertyHandler.handle( indicators );

       return SUCCESS;
    }
}
