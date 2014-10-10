package org.hisp.dhis.dataelement;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementCategoryOptionComboServiceTest
    extends DhisSpringTest
{
    private DataElementCategory categoryA;
    private DataElementCategory categoryB;
        
    private DataElementCategoryCombo categoryComboA;
    private DataElementCategoryCombo categoryComboB;
    
    private DataElementCategoryOption categoryOptionA;
    private DataElementCategoryOption categoryOptionB;
    private DataElementCategoryOption categoryOptionC;
    private DataElementCategoryOption categoryOptionD;
    
    private DataElementCategoryOptionCombo categoryOptionComboA;
    private DataElementCategoryOptionCombo categoryOptionComboB;
    private DataElementCategoryOptionCombo categoryOptionComboC;
    
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        categoryOptionA = new DataElementCategoryOption( "Male" );
        categoryOptionB = new DataElementCategoryOption( "Female" );
        categoryOptionC = new DataElementCategoryOption( "0-20" );
        categoryOptionD = new DataElementCategoryOption( "20-100" );

        categoryService.addDataElementCategoryOption( categoryOptionA );
        categoryService.addDataElementCategoryOption( categoryOptionB );
        categoryService.addDataElementCategoryOption( categoryOptionC );
        categoryService.addDataElementCategoryOption( categoryOptionD );
        
        categoryA = new DataElementCategory( "Gender" );
        categoryB = new DataElementCategory( "Agegroup" );
        
        categoryA.getCategoryOptions().add( categoryOptionA );
        categoryA.getCategoryOptions().add( categoryOptionB );        
        categoryB.getCategoryOptions().add( categoryOptionC );
        categoryB.getCategoryOptions().add( categoryOptionD );
        
        categoryService.addDataElementCategory( categoryA );
        categoryService.addDataElementCategory( categoryB );
        
        categoryComboA = new DataElementCategoryCombo( "GenderAgegroup" );
        categoryComboB = new DataElementCategoryCombo( "Gender" );
        
        categoryComboA.getCategories().add( categoryA );
        categoryComboA.getCategories().add( categoryB );
        categoryComboB.getCategories().add( categoryA );
        
        categoryService.addDataElementCategoryCombo( categoryComboA );
        categoryService.addDataElementCategoryCombo( categoryComboB ); 
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddGetDataElementCategoryOptionCombo()
    {
        categoryOptionComboA = new DataElementCategoryOptionCombo();
        
        Set<DataElementCategoryOption> categoryOptions = new HashSet<DataElementCategoryOption>();
        
        categoryOptions.add( categoryOptionA );
        categoryOptions.add( categoryOptionB );        
        
        categoryOptionComboA.setCategoryCombo( categoryComboA );
        categoryOptionComboA.setCategoryOptions( categoryOptions );        
        
        int id = categoryService.addDataElementCategoryOptionCombo( categoryOptionComboA );
        
        categoryOptionComboA = categoryService.getDataElementCategoryOptionCombo( id );
        
        assertNotNull( categoryOptionComboA );
        assertEquals( categoryComboA, categoryOptionComboA.getCategoryCombo() );
        assertEquals( categoryOptions, categoryOptionComboA.getCategoryOptions() );
    }
    
    @Test
    public void testGetDataElementCategoryOptionCombo()
    {
        categoryService.generateOptionCombos( categoryComboA );
        
        Collection<DataElementCategoryOption> catopts = new LinkedList<DataElementCategoryOption>();
        catopts.add( categoryOptionA );
        catopts.add( categoryOptionC );
        
        DataElementCategoryOptionCombo catoptcombo = categoryService.getDataElementCategoryOptionCombo( catopts );
        assertNotNull( catoptcombo );
    }

    @Test
    public void testUpdateGetDataElementCategoryOptionCombo()
    {
        categoryOptionComboA = new DataElementCategoryOptionCombo();
        
        Set<DataElementCategoryOption> categoryOptions = new HashSet<DataElementCategoryOption>();
        
        categoryOptions.add( categoryOptionA );
        categoryOptions.add( categoryOptionB );        
        
        categoryOptionComboA.setCategoryCombo( categoryComboA );
        categoryOptionComboA.setCategoryOptions( categoryOptions );        
        
        int id = categoryService.addDataElementCategoryOptionCombo( categoryOptionComboA );
        
        categoryOptionComboA = categoryService.getDataElementCategoryOptionCombo( id );
        
        assertNotNull( categoryOptionComboA );
        assertEquals( categoryComboA, categoryOptionComboA.getCategoryCombo() );
        assertEquals( categoryOptions, categoryOptionComboA.getCategoryOptions() );
        
        categoryOptionComboA.setCategoryCombo( categoryComboB );
        
        categoryService.updateDataElementCategoryOptionCombo( categoryOptionComboA );
        
        categoryOptionComboA = categoryService.getDataElementCategoryOptionCombo( id );
        
        assertNotNull( categoryOptionComboA );
        assertEquals( categoryComboB, categoryOptionComboA.getCategoryCombo() );
        assertEquals( categoryOptions, categoryOptionComboA.getCategoryOptions() );
    }

    @Test
    public void testDeleteDataElementCategoryOptionCombo()
    {
        categoryOptionComboA = new DataElementCategoryOptionCombo();
        categoryOptionComboB = new DataElementCategoryOptionCombo();
        categoryOptionComboC = new DataElementCategoryOptionCombo();
        
        int idA = categoryService.addDataElementCategoryOptionCombo( categoryOptionComboA );
        int idB = categoryService.addDataElementCategoryOptionCombo( categoryOptionComboB );
        int idC = categoryService.addDataElementCategoryOptionCombo( categoryOptionComboC );
        
        assertNotNull( categoryService.getDataElementCategoryOptionCombo( idA ) );
        assertNotNull( categoryService.getDataElementCategoryOptionCombo( idB ) );
        assertNotNull( categoryService.getDataElementCategoryOptionCombo( idC ) );
        
        categoryService.deleteDataElementCategoryOptionCombo( categoryService.getDataElementCategoryOptionCombo( idA ) );

        assertNull( categoryService.getDataElementCategoryOptionCombo( idA ) );
        assertNotNull( categoryService.getDataElementCategoryOptionCombo( idB ) );
        assertNotNull( categoryService.getDataElementCategoryOptionCombo( idC ) );

        categoryService.deleteDataElementCategoryOptionCombo( categoryService.getDataElementCategoryOptionCombo( idB ) );

        assertNull( categoryService.getDataElementCategoryOptionCombo( idA ) );
        assertNull( categoryService.getDataElementCategoryOptionCombo( idB ) );
        assertNotNull( categoryService.getDataElementCategoryOptionCombo( idC ) );

        categoryService.deleteDataElementCategoryOptionCombo( categoryService.getDataElementCategoryOptionCombo( idC ) );

        assertNull( categoryService.getDataElementCategoryOptionCombo( idA ) );
        assertNull( categoryService.getDataElementCategoryOptionCombo( idB ) );
        assertNull( categoryService.getDataElementCategoryOptionCombo( idC ) );
    }

    @Test
    public void testGetAllDataElementCategoryOptionCombos()
    {
        categoryOptionComboA = new DataElementCategoryOptionCombo();
        categoryOptionComboB = new DataElementCategoryOptionCombo();
        categoryOptionComboC = new DataElementCategoryOptionCombo();
        
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboA );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboB );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboC );
        
        Collection<DataElementCategoryOptionCombo> categoryOptionCombos = 
            categoryService.getAllDataElementCategoryOptionCombos();
        
        assertNotNull( categoryOptionCombos );
        assertEquals( 4, categoryOptionCombos.size() ); // Including default category option combo
    }
}
