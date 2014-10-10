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

import java.io.InputStream;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.importexport.util.ImportExportUtils;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Lars Helge Overland
 */
public class GML2DXFTest
    extends DhisTest
{
    private ImportService importService;

    private OrganisationUnitService organisationUnitService;
    
    private InputStream inputStream;
    
    @Override
    public void setUpTest()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        importService = (ImportService) getBean( "org.hisp.dhis.importexport.ImportService" );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        inputStream = classLoader.getResourceAsStream( "polygon.gml" );        
    }
    
    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }
    
    @Test
    public void test()
        throws Exception
    {
        ImportParams params = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false, false );
        
        importService.importData( params, inputStream );
        
        assertNotNull( organisationUnitService.getAllOrganisationUnits() );
        assertEquals( 13, organisationUnitService.getAllOrganisationUnits().size() );
        
        assertEquals( 1, organisationUnitService.getOrganisationUnitByName( "Bo" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 18, organisationUnitService.getOrganisationUnitByName( "Bonthe" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 1, organisationUnitService.getOrganisationUnitByName( "Moyamba" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 3, organisationUnitService.getOrganisationUnitByName( "Pujehun" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 1, organisationUnitService.getOrganisationUnitByName( "Kailahun" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 1, organisationUnitService.getOrganisationUnitByName( "Kenema" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 1, organisationUnitService.getOrganisationUnitByName( "Kono" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 1, organisationUnitService.getOrganisationUnitByName( "Bombali" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 3, organisationUnitService.getOrganisationUnitByName( "Kambia" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 1, organisationUnitService.getOrganisationUnitByName( "Koinadugu" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 9, organisationUnitService.getOrganisationUnitByName( "Port Loko" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 1, organisationUnitService.getOrganisationUnitByName( "Tonkolili" ).get( 0 ).getCoordinatesAsList().size() );
        assertEquals( 2, organisationUnitService.getOrganisationUnitByName( "Western Area" ).get( 0 ).getCoordinatesAsList().size() );

        assertEquals( 76, organisationUnitService.getOrganisationUnitByName( "Bo" ).get( 0 ).getCoordinatesAsList().get( 0 ).getNumberOfCoordinates() );
        assertEquals( 7, organisationUnitService.getOrganisationUnitByName( "Pujehun" ).get( 0 ).getCoordinatesAsList().get( 0 ).getNumberOfCoordinates() );
        assertEquals( 7, organisationUnitService.getOrganisationUnitByName( "Pujehun" ).get( 0 ).getCoordinatesAsList().get( 1 ).getNumberOfCoordinates() );
        assertEquals( 159, organisationUnitService.getOrganisationUnitByName( "Pujehun" ).get( 0 ).getCoordinatesAsList().get( 2 ).getNumberOfCoordinates() );
        assertEquals( 189, organisationUnitService.getOrganisationUnitByName( "Bonthe" ).get( 0 ).getCoordinatesAsList().get( 1 ).getNumberOfCoordinates() );
    }
}
