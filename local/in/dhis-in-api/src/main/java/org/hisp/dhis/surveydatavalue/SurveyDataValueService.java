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

/**
 * @author Brajesh Murari
 * @version $Id$
 */
public interface SurveyDataValueService
{
    
    String ID = SurveyDataValueService.class.getName();

    // -------------------------------------------------------------------------
    // Basic SurveyDataValue
    // -------------------------------------------------------------------------

    /**
     * Adds a SurveyDataValue. If both the value and the comment properties of the
     * specified SurveyDataValue object are null, then the object should not be
     * persisted.
     * 
     * @param surveyDataValue the SurveyDataValue to add.
     */
    void addSurveyDataValue( SurveyDataValue surveyDataValue );

    /**
     * Updates a SurveyDataValue. If both the value and the comment properties of the
     * specified SurveyDataValue object are null, then the object should be deleted
     * from the underlying storage.
     * 
     * @param surveyDataValue the SurveyDataValue to update.
     */
    void updateSurveyDataValue( SurveyDataValue dataValue );

    /**
     * Deletes a SurveyDataValue.
     * 
     * @param surveyDataValue the SurveyDataValue to delete.
     */
    void deleteSurveyDataValue( SurveyDataValue surveyDataValue );
    
    /**
     * Deletes all SurveyDataValue connected to a Source.
     * 
     * @param source the Source for which the SurveyDataValue should be deleted.
     * @return the number of deleted SurveyDataValue.
     */
    int deleteSurveyDataValuesBySource( OrganisationUnit source );
    
    /**
     * Deletes all SurveyDataValues registered for the given Survey.
     * 
     * @param dataElement the Survey for which the SurveyDataValues should be deleted.
     * @return the number of deleted SurveyDataValues.
     */
    int deleteSurveyDataValuesByIndicator( Indicator indicator );

    /**
     * Deletes all SurveyDataValues registered for the given Survey.
     * 
     * @param dataElement the Survey for which the SurveyDataValues should be deleted.
     * @return the number of deleted SurveyDataValues.
     */
    int deleteSurveyDataValuesBySurvey( Survey survey );
    
    int deleteSurveyDataValuesBySurveyIndicatorAndSource( Survey survey, Indicator indicator, OrganisationUnit source );

    /**
     * Returns a SurveyDataValue.
     * 
     * @param source the Source of the SurveyDataValue.
     * @param survey the Survey of the SurveyDataValue.
     * @return the SurveyDataValue which corresponds to the given parameters, or null
     *         if no match.
     */
    SurveyDataValue getSurveyDataValue( OrganisationUnit source, Survey survey );

    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    /**
     * Returns all SurveyDataValues.
     * 
     * @return a collection of all SurveyDataValues.
     */
    Collection<SurveyDataValue> getAllSurveyDataValues();
    
    /**
     * Returns all SurveyDataValues for a given Source.
     * 
     * @param period the Period of the DataValues.
     * @return a collection of all SurveyDataValues which match the given Source 
     *         or an empty collection if no values match.
     */
    Collection<SurveyDataValue> getSurveyDataValues( OrganisationUnit source );

    /**
     * Returns all SurveyDataValues for a given Source and Survey.
     * 
     * @param source the Source of the SurveyDataValues.
     * @param survey the Survey of the SurveyDataValues.
     * @return a collection of all SurveyDataValues which match the given Source and
     *         Survey, or an empty collection if no values match.
     */
    Collection<SurveyDataValue> getSurveyDataValues( OrganisationUnit source, Survey survey );

    /**
     * Returns all SurveyDataValues for a given collection of Sources and a
     * Survey.
     * 
     * @param sources the Sources of the SurveyDataValues.
     * @param survey the Survey of the SurveyDataValues.
     * @return a collection of all SurveyDataValues which match any of the given
     *         Sources and the Survey, or an empty collection if no values
     *         match.
     */
    Collection<SurveyDataValue> getSurveyDataValues( Collection<OrganisationUnit> sources, Survey survey );

    /**
     * Returns all SurveyDataValues for a given Source, and collection of
     * Surveys.
     * 
     * @param source the Source of the SurveyDataValues.
     * @param period the Period of the SurveyDataValues.
     * @param surveys the Surveys of the SurveyDataValues.
     * @return a collection of all SurveyDataValues which match the given Source
     *         and any of the Surveys, or an empty collection if no
     *         values match.
     */
    Collection<SurveyDataValue> getSurveyDataValues( OrganisationUnit source, Collection<Survey> surveys );

    /**
     * Returns all DataValues for a given DataElement, Period, and collection of 
     * Sources.
     * 
     * @param survey the Surveys of the SurveyDataValues.
     * @param period the Period of the SurveyDataValues.
     * @param sources the Sources of the SurveyDataValues.
     * @return a collection of all SurveyDataValues which match the given Survey,
     *         and Sources.
     */
    Collection<SurveyDataValue> getSurveyDataValues( Survey survey, Collection<OrganisationUnit> sources );
  
    /**
     * Returns all DataValues for a given collection of DataElements, collection of Periods, and
     * collection of Sources, limited by a given start indexs and number of elements to return.
     * 
     * @param surveys the Surveys of the SurveyDataValue.
     * @param sources the Sources of the SurveyDataValues.
     * @param firstResult the zero-based index of the first DataValue in the collection to return.
     * @param maxResults the maximum number of SurveyDataValues to return. 0 means no restrictions.
     * @return a collection of all SurveyDataValues which match the given collection of Surveys,
     *         and Sources, limited by the firstResult and maxResults property.
     */
    Collection<SurveyDataValue> getSurveyDataValues( Collection<Survey> surveys,  
        Collection<OrganisationUnit> sources, int firstResult, int maxResults );    
    
    /**
     * Returns all SurveyDataValues for a given collection of Surveys.
     * 
     * @param survey the Surveys of the SurveyDataValue.
     * @return a collection of all SurveyDataValues which mach the given collection of Surveys.
     */
    Collection<SurveyDataValue> getSurveyDataValues( Survey survey );
    
    
    /**
     * 
     * @param organisationUnit
     * @param survey
     * @param indicator
     * @return
     */
    SurveyDataValue getSurveyDataValue( OrganisationUnit source, Survey survey, Indicator indicator );

}
