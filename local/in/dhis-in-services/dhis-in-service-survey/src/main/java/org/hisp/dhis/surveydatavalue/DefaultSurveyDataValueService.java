package org.hisp.dhis.surveydatavalue;

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

import java.util.Collection;

import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.survey.Survey;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Brajesh Murari
 * @version $Id$
 */

@Transactional
public class DefaultSurveyDataValueService
    implements SurveyDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SurveyDataValueStore surveyDataValueStore;

    public void setSurveyDataValueStore( SurveyDataValueStore surveyDataValueStore )
    {
        this.surveyDataValueStore = surveyDataValueStore;
    }

    // -------------------------------------------------------------------------
    // Basic SurveyDataValue
    // -------------------------------------------------------------------------

    public void addSurveyDataValue( SurveyDataValue surveyDataValue )
    {
        if ( surveyDataValue.getValue() != null )
        {
            surveyDataValueStore.addSurveyDataValue( surveyDataValue );
        }
    }

    public void deleteSurveyDataValue( SurveyDataValue surveyDataValue )
    {
        surveyDataValueStore.deleteSurveyDataValue( surveyDataValue );
    }

    public int deleteSurveyDataValuesBySource( OrganisationUnit source )
    {
        return surveyDataValueStore.deleteSurveyDataValuesBySource( source );
    }

    public int deleteSurveyDataValuesBySurvey( Survey survey )
    {
        return surveyDataValueStore.deleteSurveyDataValuesBySurvey( survey );
    }

    public int deleteSurveyDataValuesByIndicator( Indicator indicator )
    {
        return surveyDataValueStore.deleteSurveyDataValuesByIndicator( indicator );
    }
    
    public int deleteSurveyDataValuesBySurveyIndicatorAndSource( Survey survey, Indicator indicator, OrganisationUnit source )
    {
    	return surveyDataValueStore.deleteSurveyDataValuesBySurveyIndicatorAndSource(survey, indicator, source);
    }

    public Collection<SurveyDataValue> getAllSurveyDataValues()
    {
        return surveyDataValueStore.getAllSurveyDataValues();
    }

    public Collection<SurveyDataValue> getSurveyDataValues( OrganisationUnit source )
    {
        return surveyDataValueStore.getSurveyDataValues( source );
    }

    public Collection<SurveyDataValue> getSurveyDataValues( Survey survey, Collection<OrganisationUnit> sources )
    {
        return surveyDataValueStore.getSurveyDataValues( survey, sources );
    }

    public SurveyDataValue getSurveyDataValue( OrganisationUnit source, Survey survey )
    {
        return surveyDataValueStore.getSurveyDataValue( source, survey );
    }

    public Collection<SurveyDataValue> getSurveyDataValues( OrganisationUnit source, Survey survey )
    {
        return surveyDataValueStore.getSurveyDataValues( source, survey );
    }

    public Collection<SurveyDataValue> getSurveyDataValues( Collection<OrganisationUnit> sources, Survey survey )
    {
        return surveyDataValueStore.getSurveyDataValues( sources, survey );
    }

    public Collection<SurveyDataValue> getSurveyDataValues( OrganisationUnit source, Collection<Survey> surveys )
    {
        return surveyDataValueStore.getSurveyDataValues( source, surveys );
    }

    public Collection<SurveyDataValue> getSurveyDataValues( Collection<Survey> surveys,
        Collection<OrganisationUnit> sources, int firstResult, int maxResults )
    {
        return surveyDataValueStore.getSurveyDataValues( surveys, sources, firstResult, maxResults );
    }

    public Collection<SurveyDataValue> getSurveyDataValues( Survey survey )
    {
        return surveyDataValueStore.getSurveyDataValues( survey );
    }

    public void updateSurveyDataValue( SurveyDataValue dataValue )
    {
        surveyDataValueStore.updateSurveyDataValue( dataValue );
    }

    public SurveyDataValue getSurveyDataValue( OrganisationUnit source, Survey survey, Indicator indicator )
    {
        return surveyDataValueStore.getSurveyDataValue( source, survey, indicator );
    }
}
