package org.hisp.dhis.survey;

/*
 * Copyright (c) 2004-2009, University of Oslo
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Brajesh Murari
 * @version $Id$
 */

@Transactional
public class DefaultSurveyService
    implements SurveyService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SurveyStore surveyStore;

    public void setSurveyStore( SurveyStore surveyStore )
    {
        this.surveyStore = surveyStore;
    }

    // -------------------------------------------------------------------------
    // Survey
    // -------------------------------------------------------------------------

    public int addSurvey( Survey survey )
    {
        return surveyStore.addSurvey( survey );
    }

    public int deleteSurvey( Survey survey )
    {
        return surveyStore.deleteSurvey( survey );
    }

    public Collection<Survey> getAllSurveys()
    {
        return surveyStore.getAllSurveys();
    }

    public List<Survey> getAssignedSurveys()
    {

        return null;
    }

    public List<Survey> getAvailableSurveys()
    {

        return null;
    }

    public Collection<Indicator> getDistinctIndicators( Collection<Integer> surveyIdentifiers )
    {
        Collection<Survey> surveys = getSurveys( surveyIdentifiers );

        Set<Indicator> indicators = new HashSet<Indicator>();

        for ( Survey survey : surveys )
        {
            indicators.addAll( survey.getIndicators() );
        }

        return indicators;
    }

    public int getSourcesAssociatedWithSurvey( Survey survey, Collection<OrganisationUnit> sources )
    {
        int count = 0;

        for ( OrganisationUnit source : sources )
        {
            if ( survey.getSources().contains( source ) )
            {
                count++;
            }
        }

        return count;
    }

    public Survey getSurvey( int id )
    {
        return surveyStore.getSurvey( id );
    }

    public Survey getSurveyByName( String name )
    {
        return surveyStore.getSurveyByName( name );
    }

    public Survey getSurveyByShortName( String shortName )
    {
        return surveyStore.getSurveyByShortName( shortName );
    }

    public Collection<Survey> getSurveys( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllSurveys();
        }

        Collection<Survey> objects = new ArrayList<Survey>();

        for ( Integer id : identifiers )
        {
            objects.add( getSurvey( id ) );
        }

        return objects;
    }

    public Collection<Survey> getSurveysBySource( OrganisationUnit source )
    {
        return surveyStore.getSurveysBySource( source );
    }

    public Collection<Survey> getSurveysByIndicator( Indicator indicator )
    {
        return surveyStore.getSurveysByIndicator( indicator );
    }

    public Collection<Survey> getSurveysBySources( Collection<OrganisationUnit> sources )
    {
        Set<Survey> surveys = new HashSet<Survey>();

        for ( OrganisationUnit source : sources )
        {
            surveys.addAll( surveyStore.getSurveysBySource( source ) );
        }

        return surveys;
    }

    public void updateSurvey( Survey survey )
    {
        surveyStore.updateSurvey( survey );
    }
    
    public List<Indicator> getAllSurveyIndicators()
    {
        List<Indicator> surveyIndicatorList = new ArrayList<Indicator>();
        
        List<Survey> surveyList = new ArrayList<Survey>( getAllSurveys() );
        
        List<Indicator> oneSurveyIndicatorList = new ArrayList<Indicator>(); 
        
        for( Survey survey : surveyList )
        {
            oneSurveyIndicatorList = new ArrayList<Indicator>( survey.getIndicators() );
                        
            for( Indicator indicator : oneSurveyIndicatorList )
            {
                if( !surveyIndicatorList.contains( indicator ) )
                {
                    surveyIndicatorList.add( indicator );
                }
            }
        }
                
        return surveyIndicatorList;
    }
    
}
