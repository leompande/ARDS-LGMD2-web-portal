package org.hisp.dhis.reportsheet.importing.action;

/*
 * Copyright (c) 2004-2011, University of Oslo
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
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.reportsheet.importing.ViewDataGeneric;
import org.hisp.dhis.reportsheet.importitem.ImportItem;
import org.hisp.dhis.reportsheet.importitem.ImportReport;
import org.hisp.dhis.reportsheet.preview.action.XMLStructureResponseImport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dang Duy Hieu
 * @version $Id
 */

public class ViewDataOrganizationGroupAction
    extends ViewDataGeneric
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    @Autowired
    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public void executeViewData( ImportReport importReport, List<ImportItem> importItems )
    {
        OrganisationUnit unit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        if ( unit != null )
        {
            List<ImportItem> orgUnitListingImportItems = new ArrayList<ImportItem>();

            setUpImportItems( importReport, unit, importItems, orgUnitListingImportItems );

            try
            {
                xmlStructureResponse = new XMLStructureResponseImport( selectionManager.getUploadFilePath(),
                    new HashSet<Integer>( importReportService.getAllSheet() ), orgUnitListingImportItems ).getXml();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void setUpImportItems( ImportReport importReport, OrganisationUnit selectedUnit,
        List<ImportItem> importItemsSource, List<ImportItem> importItemsDest )
    {
        int row = 0;

        for ( OrganisationUnitGroup organisationUnitGroup : importReport.getOrganisationUnitGroups() )
        {
            List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( getOrganisationUnits(
                organisationUnitGroup, selectedUnit ) );

            Collections.sort( organisationUnits, new IdentifiableObjectNameComparator() );

            row++;

            for ( OrganisationUnit o : organisationUnits )
            {
                for ( ImportItem importItem : importItemsSource )
                {
                    ImportItem item = new ImportItem();

                    item.setSheetNo( importItem.getSheetNo() );
                    item.setRow( importItem.getRow() + row + 1 );
                    item.setColumn( importItem.getColumn() );
                    item.setExpression( o.getId() + "_" + importItem.getExpression() );

                    importItemsDest.add( item );
                }

                row++;
            }
        }
    }

    private Collection<OrganisationUnit> getOrganisationUnits( OrganisationUnitGroup group, OrganisationUnit parentUnit )
    {
        List<OrganisationUnit> childrenOrganisationUnits = new ArrayList<OrganisationUnit>( parentUnit.getChildren() );

        Collection<OrganisationUnit> organisationUnits = group.getMembers();

        organisationUnits.retainAll( childrenOrganisationUnits );

        return organisationUnits;
    }

}
