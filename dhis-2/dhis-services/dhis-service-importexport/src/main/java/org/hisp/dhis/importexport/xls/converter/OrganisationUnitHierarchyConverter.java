package org.hisp.dhis.importexport.xls.converter;

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

import java.util.ArrayList;
import java.util.Collection;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.XLSConverter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.util.ExcelUtils;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class OrganisationUnitHierarchyConverter
    extends ExcelUtils
    implements XLSConverter
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public OrganisationUnitHierarchyConverter( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // PDFConverter implementation
    // -------------------------------------------------------------------------

    public void write( WritableWorkbook workbook, ExportParams params, int sheetIndex )
    {
        I18n i18n = params.getI18n();

        int rowNumber = 0;
        int columnIndex = 0;

        WritableSheet sheet = workbook.createSheet( i18n.getString( "organisation_unit_hierarchy" ), sheetIndex );

        try
        {
            printOrganisationUnitHierarchyHeaders( sheet, i18n,
                rowNumber++, columnIndex, organisationUnitService.getNumberOfOrganisationalLevels() );

            rowNumber++;

            Collection<OrganisationUnit> hierarchy = getHierarchy();

            for ( OrganisationUnit unit : hierarchy )
            {
                addOrganisationUnitHierarchyCellToSheet( sheet, unit, i18n, rowNumber++, unit.getLevel()-1 );
            }
        }
        catch ( RowsExceededException e )
        {
            e.printStackTrace();
        }
        catch ( WriteException e )
        {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Collection<OrganisationUnit> getHierarchy()
    {
        Collection<OrganisationUnit> hierarchy = new ArrayList<OrganisationUnit>();

        Collection<OrganisationUnit> roots = organisationUnitService.getRootOrganisationUnits();

        for ( OrganisationUnit root : roots )
        {
            hierarchy.addAll( organisationUnitService.getOrganisationUnitWithChildren( root.getId() ) );
        }

        return hierarchy;
    }
}
