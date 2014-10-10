package org.hisp.dhis.importexport.dxf;

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

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.external.location.LocationManagerException;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.importexport.util.ImportExportUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Lars Helge Overland
 */
public class DXFOrganisationUnitsTest
    extends DhisTest
{
    private InputStream inputStream;

    private ImportService importService;

    @Override
    public void setUpTest() throws LocationManagerException, IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        inputStream = classLoader.getResourceAsStream( "dxfOrganisationUnits.xml" );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
    }

    @Override
    public void tearDownTest()
        throws Exception
    {
        inputStream.close();
    }

    @Override
    public boolean emptyDatabaseAfterTest() // Empty before to avoid problem for subsequent tests?
    {
        return true;
    }

    @Test
    public void testImportOrganisationUnits() throws Exception
    {
        importService = (ImportService) getBean( "org.hisp.dhis.importexport.ImportService" );

        ImportParams params = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false, false );

        importService.importData( params, inputStream );

        Collection<OrganisationUnit> units = organisationUnitService.getAllOrganisationUnits();
        OrganisationUnit unit = units.iterator().next();

        assertNotNull( units );
        assertEquals( 3, units.size() );
        assertEquals( "GeoCode", unit.getGeoCode() );
        assertEquals( "MultiPolygon", unit.getFeatureType() );
        assertEquals( "[[[[11.11,22.22],[33.33,44.44],[55.55,66.66]]],[[[77.77,88.88],[99.99,11.11],[22.22,33.33]]],[[[44.44,55.55],[66.66,77.77],[88.88,99.99]]]]", unit.getCoordinates() );
    }
}
