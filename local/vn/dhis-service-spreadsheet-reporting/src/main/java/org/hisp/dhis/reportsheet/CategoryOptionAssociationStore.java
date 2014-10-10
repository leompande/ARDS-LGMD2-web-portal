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

import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public interface CategoryOptionAssociationStore
{
    String ID = CategoryOptionAssociationStore.class.getName();

    /**
     * Saves a CategoryOptionAssociation.
     * 
     * @param association the CategoryOptionAssociation to save.
     */
    void saveCategoryOptionAssociation( CategoryOptionAssociation association );

    /**
     * Updates a CategoryOptionAssociation.
     * 
     * @param association the CategoryOptionAssociation to update.
     */
    void updateCategoryOptionAssociation( CategoryOptionAssociation association );

    /**
     * Deletes a CategoryOptionAssociation.
     * 
     * @param association the CategoryOptionAssociation to delete.
     */
    void deleteCategoryOptionAssociation( CategoryOptionAssociation association );

    /**
     * Deletes the CategoryOptionAssociations associated with the given
     * OrganisationUnit.
     * 
     * @param source the OrganisationUnit.
     */
    void deleteCategoryOptionAssociations( OrganisationUnit source );

    /**
     * Deletes the CategoryOptionAssociations associated with the given
     * DataElementCategoryOption.
     * 
     * @param categoryOption the DataElementCategoryOption.
     */
    void deleteCategoryOptionAssociations( DataElementCategoryOption categoryOption );

    /**
     * Deletes the CategoryOptionAssociations associated with the given
     * DataElementCategoryOption and the list of OrganisationUnits.
     * 
     * @param categoryOption the DataElementCategoryOption.
     * @param sources the list of OrganisationUnits.
     */
    void deleteCategoryOptionAssociations( Collection<OrganisationUnit> sources,
        DataElementCategoryOption categoryOption );

    /**
     * Retrieves all CategoryOptionAssociations.
     * 
     * @return a Collection of CategoryOptionAssociations.
     */
    Collection<CategoryOptionAssociation> getAllCategoryOptionAssociations();

    /**
     * Retrieves all CategoryOptionAssociations associated with the given
     * OrganisationUnit.
     * 
     * @param source the OrganisationUnit
     * @return a Collection of CategoryOptionAssociations.
     */
    Collection<CategoryOptionAssociation> getCategoryOptionAssociations( OrganisationUnit source );

    /**
     * Retrieves the CategoryOptionAssociation for the given OrganisationUnit,
     * DataElementCategoryOption
     * 
     * @param source the OrganisationUnit.
     * @param categoryOption the DataElementCategoryOption.
     * @return the CategoryOptionAssociation.
     */
    CategoryOptionAssociation getCategoryOptionAssociation( OrganisationUnit source,
        DataElementCategoryOption categoryOption );

    /**
     * Retrieves all DataElementCategoryOptions associated with the given
     * OrganisationUnit.
     * 
     * @param source the OrganisationUnit
     * @return a Collection of DataElementCategoryOptions.
     */
    Collection<DataElementCategoryOption> getCategoryOptions( OrganisationUnit source );

    /**
     * Retrieves all OrganisationUnits associated with the given
     * DataElementCategoryOption.
     * 
     * @param categoryOption the DataElementCategoryOption
     * @return a Collection of OrganisationUnits.
     */
    Collection<CategoryOptionAssociation> getCategoryOptionAssociations( DataElementCategoryOption categoryOption );
}
