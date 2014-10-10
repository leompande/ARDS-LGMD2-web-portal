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

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Brajesh Murari
 * @version $Id$
 */
public interface SurveyService
{
    String ID = SurveyService.class.getName();

    // -------------------------------------------------------------------------
    // Survey
    // -------------------------------------------------------------------------

    /**
     * Adds a Survey.
     * 
     * @param survey The Survey to add.
     * @return The generated unique identifier for this Survey.
     */
    int addSurvey( Survey survey );

    /**
     * Updates a Survey.
     * 
     * @param survey The Survey to update.
     */
    void updateSurvey( Survey survey );

    /**
     * Deletes a Survey.
     * 
     * @param survey The Survey to delete.
     */
    int deleteSurvey( Survey survey );

    /**
     * Get a Survey
     * 
     * @param id The unique identifier for the Survey to get.
     * @return The Survey with the given id or null if it does not exist.
     */
    Survey getSurvey( int id );

    /**
     * Returns a Survey with the given name.
     * 
     * @param name The name.
     * @return A Survey with the given name.
     */
    Survey getSurveyByName( String name );

    /**
     * Returns the Survey with the given short name.
     * 
     * @param shortName The short name.
     * @return The Survey with the given short name.
     */
    Survey getSurveyByShortName( String shortName );

    /**
     * Returns all Survey associated with the specified source.
     */
    Collection<Survey> getSurveysBySource( OrganisationUnit source );

    /**
     * Returns all Survey associated with the specified indicator.
     */
    Collection<Survey> getSurveysByIndicator( Indicator indicator );

    /**
     * Returns all Survey associated with the specified sources.
     */
    Collection<Survey> getSurveysBySources( Collection<OrganisationUnit> sources );

    /**
     * Returns the number of Sources among the specified Sources associated with
     * the specified Survey.
     */
    int getSourcesAssociatedWithSurvey( Survey survey, Collection<OrganisationUnit> sources );

    /**
     * Get all Surveys.
     * 
     * @return A collection containing all Surveys.
     */
    Collection<Survey> getAllSurveys();

    /**
     * Get all Survey with corresponding identifiers.
     * 
     * @param identifiers the collection of identifiers.
     * @return a collection of survey.
     */
    Collection<Survey> getSurveys( Collection<Integer> identifiers );

    /**
     * Get list of available ie. unassigned surveys.
     * 
     * @return A List containing all available Survey.
     */
    List<Survey> getAvailableSurveys();

    /**
     * Get list of assigned (ie. which had corresponding data entry form)
     * surveys.
     * 
     * @return A List containing assigned Surveys.
     */
    List<Survey> getAssignedSurveys();

    /**
     * Returns a distinct collection of indicators associated with the survey
     * with the given corresponding survey identifiers.
     * 
     * @param surveyIdentifiers the Survey identifiers.
     * @return a distinct collection of indicators.
     */
    Collection<Indicator> getDistinctIndicators( Collection<Integer> surveyIdentifiers );
    
    /**
     * Returns Collection of Indicators that are associated with atleast one survey.
     * 
     * @return List of Indicators
     */
    List<Indicator> getAllSurveyIndicators();

}
