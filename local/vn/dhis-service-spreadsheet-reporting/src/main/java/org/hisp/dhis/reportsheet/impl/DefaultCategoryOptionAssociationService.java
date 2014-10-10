package org.hisp.dhis.reportsheet.impl;

/*
 * Copyright (c) 2004-2012, University of Oslo All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the HISP project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission. THIS SOFTWARE IS
 * PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reportsheet.CategoryOptionAssociation;
import org.hisp.dhis.reportsheet.CategoryOptionAssociationService;
import org.hisp.dhis.reportsheet.CategoryOptionAssociationStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
@Transactional
public class DefaultCategoryOptionAssociationService
    implements CategoryOptionAssociationService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CategoryOptionAssociationStore categoryOptionAssociationStore;

    public void setCategoryOptionAssociationStore( CategoryOptionAssociationStore categoryOptionAssociationStore )
    {
        this.categoryOptionAssociationStore = categoryOptionAssociationStore;
    }

    // -------------------------------------------------------------------------
    // CategoryOptionAssociationService
    // -------------------------------------------------------------------------

    @Override
    public void saveCategoryOptionAssociation( CategoryOptionAssociation association )
    {
        categoryOptionAssociationStore.saveCategoryOptionAssociation( association );
    }

    @Override
    public void updateCategoryOptionAssociation( CategoryOptionAssociation association )
    {
        categoryOptionAssociationStore.updateCategoryOptionAssociation( association );
    }

    @Override
    public void deleteCategoryOptionAssociation( CategoryOptionAssociation association )
    {
        categoryOptionAssociationStore.deleteCategoryOptionAssociation( association );
    }

    @Override
    public void deleteCategoryOptionAssociations( OrganisationUnit source )
    {
        categoryOptionAssociationStore.deleteCategoryOptionAssociations( source );
    }

    @Override
    public void deleteCategoryOptionAssociations( DataElementCategoryOption categoryOption )
    {
        categoryOptionAssociationStore.deleteCategoryOptionAssociations( categoryOption );
    }

    @Override
    public void deleteCategoryOptionAssociations( Collection<OrganisationUnit> sources,
        DataElementCategoryOption categoryOption )
    {
        categoryOptionAssociationStore.deleteCategoryOptionAssociations( sources, categoryOption );
    }

    @Override
    public Collection<CategoryOptionAssociation> getAllCategoryOptionAssociations()
    {
        return categoryOptionAssociationStore.getAllCategoryOptionAssociations();
    }

    @Override
    public Collection<CategoryOptionAssociation> getCategoryOptionAssociations( OrganisationUnit source )
    {
        return categoryOptionAssociationStore.getCategoryOptionAssociations( source );
    }

    @Override
    public CategoryOptionAssociation getCategoryOptionAssociation( OrganisationUnit source,
        DataElementCategoryOption categoryOption )
    {
        return categoryOptionAssociationStore.getCategoryOptionAssociation( source, categoryOption );
    }

    @Override
    public Collection<DataElementCategoryOption> getCategoryOptions( OrganisationUnit source )
    {
        return categoryOptionAssociationStore.getCategoryOptions( source );
    }

    @Override
    public Collection<CategoryOptionAssociation> getCategoryOptionAssociations( DataElementCategoryOption categoryOption )
    {
        return categoryOptionAssociationStore.getCategoryOptionAssociations( categoryOption );
    }
}
