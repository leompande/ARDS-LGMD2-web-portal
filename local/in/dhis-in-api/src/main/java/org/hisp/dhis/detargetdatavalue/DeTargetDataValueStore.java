/*
 * Copyright (c) 2004-2012, University of Oslo
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
package org.hisp.dhis.detargetdatavalue;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DeTargetDataValueStore.java Jan 12, 2011 3:41:29 PM
 */
public interface DeTargetDataValueStore
{
    
    String ID = DeTargetDataValueStore.class.getName();
    
    // -------------------------------------------------------------------------
    // Basic DeTargetDataValue
    // -------------------------------------------------------------------------

    /**
     * Adds a DeTargetDataValue. If both the value and the comment properties of the
     * specified DeTargetDataValue object are null, then the object should not be
     * persisted.
     * 
     * @param DeTargetDataValue the DeTargetDataValue to add.
     */
    void addDeTargetDataValue( DeTargetDataValue deTargetDataValue );
    
    /**
     * Updates a DeTargetDataValue. If both the value and the comment properties of the
     * specified DeTargetDataValue object are null, then the object should be deleted
     * from the underlying storage.
     * 
     * @param DeTargetDataValue the SurveyDataValue to update.
     */
    void updateDeTargetDataValue( DeTargetDataValue deTargetDataValue );
    
    /**
     * Deletes a DeTargetDataValue.
     * 
     * @param DeTargetDataValue the DeTargetDataValue to delete.
     */
    void deleteDeTargetDataValue( DeTargetDataValue deTargetDataValue );
    
    /**
     * Deletes all DeTargetDataValue connected to a Source.
     * 
     * @param source the Source for which the DeTargetDataValue should be deleted.
     * @return the number of deleted DeTargetDataValue.
     */
    int deleteDeTargetDataValuesBySource( OrganisationUnit source );
    
    /**
     * Deletes all DeTargetDataValue registered for the given DeTarget.
     * 
     * @param dataElement the Survey for which the DeTargetDataValue should be deleted.
     * @return the number of deleted DeTargetDataValue.
     */
    int deleteDeTargetDataValuesByDeTarget( DeTarget deTarget );
    
    /**
     * Deletes all DeTargetDataValue registered for the given Survey.
     * 
     * @param dataElement the DeTarget for which the DeTargetDataValue should be deleted.
     * @return the number of deleted DeTargetDataValue.
     */
    int deleteDeTargetDataValuesByDataElementAndCategoryOptionCombo( DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo );
    int deleteDeTargetDataValuesByDeTargetDataElementCategoryOptionComboAndSource( DeTarget deTarget, DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo, OrganisationUnit source );
    
    /**
     * Returns a DeTargetDataValue.
     * 
     * @param source the Source of the DeTargetDataValue.
     * @param deTarget the DeTarget of the DeTargetDataValue.
     * @return the DeTargetDataValue which corresponds to the given parameters, or null
     *         if no match.
     */
   // DeTargetDataValue getDeTargetDataValue( Source source, DeTarget deTarget );
    
    /**
     * Returns a DeTargetDataValue.
     * 
     * @param source the Source of the DeTargetDataValue.
     * @param deTarget the DeTarget of the DeTargetDataValue.
     * @param period the DeTarget of the DeTargetDataValue.
     * @param source the DeTarget of the DeTargetDataValue.
     * @param dataElement the DeTarget of the DeTargetDataValue.
     * @param optioncombo the DeTarget of the DeTargetDataValue.
     * @return the DeTargetDataValue which corresponds to the given parameters, or null
     *         if no match.
     */
    DeTargetDataValue getDeTargetDataValue( OrganisationUnit source, DeTarget deTarget ,Period period, DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo );
    
    
    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    /**
     * Returns all DeTargetDataValue.
     * 
     * @return a collection of all DeTargetDataValue.
     */
    Collection<DeTargetDataValue> getAllDeTargetDataValues();
    
    /**
     * Returns all DeTargetDataValue for a given Source.
     * 
     * @param period the Period of the DataValues.
     * @return a collection of all DeTargetDataValue which match the given Source 
     *         or an empty collection if no values match.
     */
    Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source );
    
    /**
     * Returns all DeTargetDataValue for a given Source and DeTarget.
     * 
     * @param source the Source of the DeTargetDataValue.
     * @param deTarget the DeTarget of the DeTargetDataValue.
     * @return a collection of all DeTargetDataValue which match the given Source and
     *         DeTarget, or an empty collection if no values match.
     */
    Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source, DeTarget deTarget );
    
    /**
     * Returns all DeTargetDataValue for a given collection of Sources and a
     * DeTarget.
     * 
     * @param sources the Sources of the DeTargetDataValue.
     * @param deTarget the DeTarget of the DeTargetDataValue.
     * @return a collection of all DeTargetDataValue which match any of the given
     *         Sources and the Survey, or an empty collection if no values
     *         match.
     */
    Collection<DeTargetDataValue> getDeTargetDataValues( Collection<OrganisationUnit> sources, DeTarget deTarget );
    
    /**
     * Returns all DeTargetDataValue for a given Source, and collection of
     * Surveys.
     * 
     * @param source the Source of the DeTargetDataValue.
     * @param period the Period of the DeTargetDataValue.
     * @param deTarget the DeTarget of the DeTargetDataValue.
     * @return a collection of all DeTargetDataValue which match the given Source
     *         and any of the DeTarget, or an empty collection if no
     *         values match.
     */
    Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source, Collection<DeTarget> deTargets );
    
    /**
     * Returns all DataValues for a given DataElement, Period, and collection of 
     * Sources.
     * 
     * @param deTarget the DeTarget of the DeTargetDataValue.
     * @param period the Period of the DeTargetDataValue.
     * @param sources the Sources of the DeTargetDataValue.
     * @return a collection of all DeTargetDataValue which match the given DeTarget,
     *         and Sources.
     */
    Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget, Collection<OrganisationUnit> sources );
    
    /**
     * Returns all DataValues for a given collection of DataElements, collection of Periods, and
     * collection of Sources, limited by a given start indexs and number of elements to return.
     * 
     * @param deTargets the DeTarget of the DeTargetDataValue.
     * @param sources the Sources of the DeTargetDataValue.
     * @param firstResult the zero-based index of the first DataValue in the collection to return.
     * @param maxResults the maximum number of SurveyDataValues to return. 0 means no restrictions.
     * @return a collection of all DeTargetDataValue which match the given collection of DeTarget,
     *         and Sources, limited by the firstResult and maxResults property.
     */
    Collection<DeTargetDataValue> getDeTargetDataValues( Collection<DeTarget> deTargets,  Collection<OrganisationUnit> sources, int firstResult, int maxResults );    
    //Collection<DeTargetDataValue> getDeTargetMemberDataValues( DeTargetMember deTargetMember ,DataElement dataelement ,DataElementCategoryOptionCombo decategoryOptionCombo );
    Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget ,DataElement dataelement ,DataElementCategoryOptionCombo decategoryOptionCombo );
    
    /**
     * Returns all DeTargetDataValue for a given collection of DeTarget.
     * 
     * @param deTarget the DeTarget of the DeTargetDataValue.
     * @return a collection of all DeTargetDataValue which mach the given collection of DeTarget.
     */
    Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget );
    
    /**
     * Returns all DeTargetDataValue for a given collection of DeTarget.
     * 
     * @param deTarget the DeTarget of the DeTargetDataValue.
     * @return a collection of all DeTargetDataValue which mach the given collection of DeTarget.
     */
    Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget, OrganisationUnit source, Period period );
    
    
    
    /**
     * 
     * @param organisationUnit
     * @param deTarget
     * @param dataelement
     * * @param deoptioncombo
     * @return
     */
    Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source, DeTarget deTarget, DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo );

}
