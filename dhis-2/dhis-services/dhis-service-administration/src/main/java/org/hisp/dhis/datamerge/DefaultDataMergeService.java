package org.hisp.dhis.datamerge;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
public class DefaultDataMergeService
    implements DataMergeService
{
    private static final Log log = LogFactory.getLog( DefaultDataMergeService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataMergeStore dataMergeStore;
    
    public void setDataMergeStore( DataMergeStore dataMergeStore )
    {
        this.dataMergeStore = dataMergeStore;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // DataMergeService implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void eliminateDuplicateDataElement( DataElement destDataElement, DataElementCategoryOptionCombo destCategoryOptionCombo,
        DataElement sourceDataElement, DataElementCategoryOptionCombo sourceCategoryOptionCombo )
    {
        // ---------------------------------------------------------------------
        // Eliminate duplicates
        // ---------------------------------------------------------------------

        dataMergeStore.eliminateDuplicateDataElement( destDataElement, destCategoryOptionCombo, sourceDataElement, sourceCategoryOptionCombo );

        // ---------------------------------------------------------------------
        // Delete source
        // ---------------------------------------------------------------------

        try
        {
            dataElementService.deleteDataElement( sourceDataElement );
        }
        catch ( DeleteNotAllowedException ex )
        {
            log.info( "Not deleting data element because it has custom dimensions and more data associated with it" );
        }
    }

    @Transactional
    public void mergeOrganisationUnits( OrganisationUnit dest, OrganisationUnit source )
    {
        // ---------------------------------------------------------------------
        // Merge source data with destination
        // ---------------------------------------------------------------------

        dataMergeStore.mergeOrganisationUnits( dest, source );      
        
        // ---------------------------------------------------------------------
        // Set parent of children of destination to source
        // ---------------------------------------------------------------------

        for ( OrganisationUnit child : source.getChildren() )
        {
            child.setParent( dest );
            
            organisationUnitService.updateOrganisationUnit( child );
        }

        // ---------------------------------------------------------------------
        // Delete source
        // ---------------------------------------------------------------------

        try
        {
            organisationUnitService.deleteOrganisationUnit( source );
        }
        catch ( HierarchyViolationException ex )
        {
        }
    }
}
