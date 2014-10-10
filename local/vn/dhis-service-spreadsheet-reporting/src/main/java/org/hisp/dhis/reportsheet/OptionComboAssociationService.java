package org.hisp.dhis.reportsheet;

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

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public interface OptionComboAssociationService
{
    String ID = OptionComboAssociationService.class.getName();

    /**
     * Saves a OptionComboAssociation.
     * 
     * @param association the OptionComboAssociation to save.
     */
    void saveOptionComboAssociation( OptionComboAssociation association );

    /**
     * Updates a OptionComboAssociation.
     * 
     * @param association the OptionComboAssociation to update.
     */
    void updateOptionComboAssociation( OptionComboAssociation association );

    /**
     * Deletes a OptionComboAssociation.
     * 
     * @param association the OptionComboAssociation to delete.
     */
    void deleteOptionComboAssociation( OptionComboAssociation association );

    /**
     * Deletes the OptionComboAssociations associated with the given
     * OrganisationUnit.
     * 
     * @param source the OrganisationUnit.
     */
    void deleteOptionComboAssociations( OrganisationUnit source );

    /**
     * Deletes the OptionComboAssociations associated with the given
     * DataElementCategoryOptionCombo.
     * 
     * @param optionCombo the DataElementCategoryOptionCombo.
     */
    void deleteOptionComboAssociations( DataElementCategoryOptionCombo optionCombo );

    /**
     * Deletes the OptionComboAssociations associated with the given
     * DataElementCategoryOptionCombo and the list of OrganisationUnits.
     * 
     * @param optionCombo the DataElementCategoryOptionCombo.
     * @param sources the list of OrganisationUnits.
     */
    void deleteOptionComboAssociations( Collection<OrganisationUnit> sources,
        DataElementCategoryOptionCombo optionCombo );

    /**
     * Retrieves all CategoryOptionAssociations.
     * 
     * @return a Collection of OptionComboAssociations.
     */
    Collection<OptionComboAssociation> getAllOptionComboAssociations();

    /**
     * Retrieves all OptionComboAssociations associated with the given
     * OrganisationUnit.
     * 
     * @param source the OrganisationUnit
     * @return a Collection of OptionComboAssociations.
     */
    Collection<OptionComboAssociation> getOptionComboAssociations( OrganisationUnit source );

    /**
     * Retrieves the OptionComboAssociation for the given OrganisationUnit,
     * DataElementCategoryOptionCombo
     * 
     * @param source the OrganisationUnit.
     * @param optionCombo the DataElementCategoryOptionCombo.
     * @return the OptionComboAssociation.
     */
    OptionComboAssociation getOptionComboAssociation( OrganisationUnit source,
        DataElementCategoryOptionCombo optionCombo );

    /**
     * Retrieves all DataElementCategoryOptionCombos associated with the given
     * OrganisationUnit.
     * 
     * @param source the OrganisationUnit
     * @return a Collection of DataElementCategoryOptionCombos.
     */
    Collection<DataElementCategoryOptionCombo> getOptionCombos( OrganisationUnit source );

    /**
     * Retrieves all OrganisationUnits associated with the given
     * DataElementCategoryOptionCombo.
     * 
     * @param optionCombo the DataElementCategoryOptionCombo
     * @return a Collection of OrganisationUnits.
     */
    Collection<OptionComboAssociation> getOptionComboAssociations( DataElementCategoryOptionCombo optionCombo );
}
