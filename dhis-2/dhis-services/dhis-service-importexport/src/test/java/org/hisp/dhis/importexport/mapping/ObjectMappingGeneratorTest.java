package org.hisp.dhis.importexport.mapping;

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

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ObjectMappingGeneratorTest
    extends DhisTest
{
    private ObjectMappingGenerator objectMappingGenerator;
    
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        NameMappingUtil.clearMapping();
        
        objectMappingGenerator = (ObjectMappingGenerator) getBean( ObjectMappingGenerator.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );
        
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
    }
        
    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testGetDataElementMapping()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        
        dataElementA.setId( 'A' );
        dataElementB.setId( 'B' );
        
        NameMappingUtil.addDataElementMapping( dataElementA.getId(), dataElementA.getName() );
        NameMappingUtil.addDataElementMapping( dataElementB.getId(), dataElementB.getName() );
        
        int idA = dataElementService.addDataElement( dataElementA );
        int idB = dataElementService.addDataElement( dataElementB );
        
        Map<Object, Integer> mapping = objectMappingGenerator.getDataElementMapping( false );
        
        assertEquals( mapping.get( Integer.valueOf( 'A' ) ), Integer.valueOf( idA ) );
        assertEquals( mapping.get( Integer.valueOf( 'B' ) ), Integer.valueOf( idB ) );
        
        mapping = objectMappingGenerator.getDataElementMapping( true );

        assertEquals( mapping.get( Integer.valueOf( 'A' ) ), Integer.valueOf( 'A' ) );
        assertEquals( mapping.get( Integer.valueOf( 'B' ) ), Integer.valueOf( 'B' ) );
    }

    @Test
    public void testGetCategoryOptionComboMapping()
    {
        DataElementCategoryCombo categoryComboA = new DataElementCategoryCombo( "CategoryComboA" );
        
        categoryService.addDataElementCategoryCombo( categoryComboA );
        
        DataElementCategoryOption categoryOptionA = new DataElementCategoryOption( "CategoryOptionA" );
        DataElementCategoryOption categoryOptionB = new DataElementCategoryOption( "CategoryOptionB" );
        DataElementCategoryOption categoryOptionC = new DataElementCategoryOption( "CategoryOptionC" );
        DataElementCategoryOption categoryOptionD = new DataElementCategoryOption( "CategoryOptionD" );
        
        categoryService.addDataElementCategoryOption( categoryOptionA );
        categoryService.addDataElementCategoryOption( categoryOptionB );
        categoryService.addDataElementCategoryOption( categoryOptionC );
        categoryService.addDataElementCategoryOption( categoryOptionD );
        
        DataElementCategoryOptionCombo categoryOptionComboA = new DataElementCategoryOptionCombo();
        categoryOptionComboA.setId( 'A' );
        categoryOptionComboA.setCategoryCombo( categoryComboA );
        categoryOptionComboA.getCategoryOptions().add( categoryOptionA );
        categoryOptionComboA.getCategoryOptions().add( categoryOptionB );

        DataElementCategoryOptionCombo categoryOptionComboB = new DataElementCategoryOptionCombo();
        categoryOptionComboB.setId( 'B' );
        categoryOptionComboB.setCategoryCombo( categoryComboA );
        categoryOptionComboB.getCategoryOptions().add( categoryOptionC );
        categoryOptionComboB.getCategoryOptions().add( categoryOptionD );
        
        NameMappingUtil.addCategoryOptionComboMapping( categoryOptionComboA.getId(), categoryOptionComboA );
        NameMappingUtil.addCategoryOptionComboMapping( categoryOptionComboB.getId(), categoryOptionComboB );        

        int idA = categoryService.addDataElementCategoryOptionCombo( categoryOptionComboA );
        int idB = categoryService.addDataElementCategoryOptionCombo( categoryOptionComboB );
        
        Map<Object, Integer> mapping = objectMappingGenerator.getCategoryOptionComboMapping( false );

        assertEquals( mapping.get( Integer.valueOf( 'A' ) ), Integer.valueOf( idA ) );
        assertEquals( mapping.get( Integer.valueOf( 'B' ) ), Integer.valueOf( idB ) );

        mapping = objectMappingGenerator.getCategoryOptionComboMapping( true );

        assertEquals( mapping.get( Integer.valueOf( 'A' ) ), Integer.valueOf( 'A' ) );
        assertEquals( mapping.get( Integer.valueOf( 'B' ) ), Integer.valueOf( 'B' ) );
    }

    @Test
    public void testGetPeriodMapping()
    {        
        PeriodType periodTypeA = periodService.getPeriodTypeByName( MonthlyPeriodType.NAME );
        
        Period periodA = createPeriod( periodTypeA, getDate( 2000, 0, 1 ), getDate( 2000, 0, 31 ) );
        Period periodB = createPeriod( periodTypeA, getDate( 2000, 1, 1 ), getDate( 2000, 1, 28 ) );
        
        periodA.setId( 'A' );
        periodB.setId( 'B' );
        
        NameMappingUtil.addPeriodMapping( periodA.getId(), periodA );
        NameMappingUtil.addPeriodMapping( periodB.getId(), periodB );

        int idA = periodService.addPeriod( periodA );
        int idB = periodService.addPeriod( periodB );
        
        Map<Object, Integer> mapping = objectMappingGenerator.getPeriodMapping( false );

        assertEquals( mapping.get( Integer.valueOf( 'A' ) ), Integer.valueOf( idA ) );
        assertEquals( mapping.get( Integer.valueOf( 'B' ) ), Integer.valueOf( idB ) );
        
        mapping = objectMappingGenerator.getPeriodMapping( true );
        
        assertEquals( mapping.get( Integer.valueOf( 'A' ) ), Integer.valueOf( 'A' ) );
        assertEquals( mapping.get( Integer.valueOf( 'B' ) ), Integer.valueOf( 'B' ) );
    }

    @Test
    public void testOrganisationUnitMapping()
    {
        OrganisationUnit organisationUnitA = createOrganisationUnit( 'A' );
        OrganisationUnit organisationUnitB = createOrganisationUnit( 'B' );
        
        organisationUnitA.setId( 'A' );
        organisationUnitB.setId( 'B' );        

        NameMappingUtil.addOrganisationUnitMapping( organisationUnitA.getId(), organisationUnitA.getName() );
        NameMappingUtil.addOrganisationUnitMapping( organisationUnitB.getId(), organisationUnitB.getName() );

        int idA = organisationUnitService.addOrganisationUnit( organisationUnitA );
        int idB = organisationUnitService.addOrganisationUnit( organisationUnitB );

        Map<Object, Integer> mapping = objectMappingGenerator.getOrganisationUnitMapping( false );

        assertEquals( mapping.get( Integer.valueOf( 'A' ) ), Integer.valueOf( idA ) );
        assertEquals( mapping.get( Integer.valueOf( 'B' ) ), Integer.valueOf( idB ) );
        
        mapping = objectMappingGenerator.getOrganisationUnitMapping( true );
        
        assertEquals( mapping.get( Integer.valueOf( 'A' ) ), Integer.valueOf( 'A' ) );
        assertEquals( mapping.get( Integer.valueOf( 'B' ) ), Integer.valueOf( 'B' ) );        
    }
}
