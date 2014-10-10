package org.hisp.dhis.datavalue;

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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.junit.Test;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DataValueStoreTest.java 5715 2008-09-17 14:05:28Z larshelg $
 */
public class DataValueStoreTest
    extends DhisSpringTest
{
    private DataValueStore dataValueStore;

    private PeriodStore periodStore;

    // -------------------------------------------------------------------------
    // Supporting data
    // -------------------------------------------------------------------------

    private DataElement dataElementA;

    private DataElement dataElementB;

    private DataElement dataElementC;

    private DataElement dataElementD;

    private DataElementCategoryOptionCombo optionCombo;
    
    private Period periodA;

    private Period periodB;

    private Period periodC;

    private Period periodD;

    private OrganisationUnit sourceA;

    private OrganisationUnit sourceB;

    private OrganisationUnit sourceC;

    private OrganisationUnit sourceD;

    // -------------------------------------------------------------------------
    // Set up/tear down
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        dataValueStore = (DataValueStore) getBean( DataValueStore.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        periodStore = (PeriodStore) getBean( PeriodStore.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        // ---------------------------------------------------------------------
        // Add supporting data
        // ---------------------------------------------------------------------

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        dataElementC = createDataElement( 'C' );
        dataElementD = createDataElement( 'D' );
        
        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );

        periodA = createPeriod( getDay( 5 ), getDay( 6 ) );
        periodB = createPeriod( getDay( 6 ), getDay( 7 ) );
        periodC = createPeriod( getDay( 7 ), getDay( 8 ) );
        periodD = createPeriod( getDay( 8 ), getDay( 9 ) );

        periodStore.addPeriod( periodA );
        periodStore.addPeriod( periodB );
        periodStore.addPeriod( periodC );
        periodStore.addPeriod( periodD );

        sourceA = createOrganisationUnit( 'A' );
        sourceB = createOrganisationUnit( 'B' );
        sourceC = createOrganisationUnit( 'C' );
        sourceD = createOrganisationUnit( 'D' );

        organisationUnitService.addOrganisationUnit( sourceA );
        organisationUnitService.addOrganisationUnit( sourceB );
        organisationUnitService.addOrganisationUnit( sourceC );
        organisationUnitService.addOrganisationUnit( sourceD );        

        optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
    }
    
    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataValue()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementC, periodC, sourceA, optionCombo, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        dataValueD.setValue( "4" );

        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );
        dataValueStore.addDataValue( dataValueC );

        try
        {
            dataValueStore.addDataValue( dataValueD );
            fail("Should give unique constraint violation");
        }
        catch ( Exception e )
        {
            // Expected
        }

        dataValueA = dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        assertNotNull( dataValueA );
        assertEquals( sourceA.getId(), dataValueA.getSource().getId() );
        assertEquals( dataElementA, dataValueA.getDataElement() );
        assertEquals( periodA, dataValueA.getPeriod() );
        assertEquals( "1", dataValueA.getValue() );

        dataValueB = dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo );
        assertNotNull( dataValueB );
        assertEquals( sourceA.getId(), dataValueB.getSource().getId() );
        assertEquals( dataElementB, dataValueB.getDataElement() );
        assertEquals( periodA, dataValueB.getPeriod() );
        assertEquals( "2", dataValueB.getValue() );

        dataValueC = dataValueStore.getDataValue( dataElementC, periodC, sourceA, optionCombo, optionCombo );
        assertNotNull( dataValueC );
        assertEquals( sourceA.getId(), dataValueC.getSource().getId() );
        assertEquals( dataElementC, dataValueC.getDataElement() );
        assertEquals( periodC, dataValueC.getPeriod() );
        assertEquals( "3", dataValueC.getValue() );
    }
    
    @Test
    public void testUpdataDataValue()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementB, periodA, sourceB, optionCombo, optionCombo );
        dataValueB.setValue( "2" );

        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );

        assertNotNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementB, periodA, sourceB, optionCombo, optionCombo ) );

        dataValueA.setValue( "5" );
        dataValueStore.updateDataValue( dataValueA );

        dataValueA = dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        assertNotNull( dataValueA );
        assertEquals( "5", dataValueA.getValue() );

        dataValueB = dataValueStore.getDataValue( dataElementB, periodA, sourceB, optionCombo, optionCombo );
        assertNotNull( dataValueB );
        assertEquals( "2", dataValueB.getValue() );
    }

    @Test
    public void testDeleteAndGetDataValue()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo );
        dataValueD.setValue( "4" );

        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );
        dataValueStore.addDataValue( dataValueC );
        dataValueStore.addDataValue( dataValueD );

        assertNotNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValue( dataValueA );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValue( dataValueB );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValue( dataValueC );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValue( dataValueD );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );
    }

    @Test
    public void testDeleteDataValuesBySource()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo );
        dataValueD.setValue( "4" );

        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );
        dataValueStore.addDataValue( dataValueC );
        dataValueStore.addDataValue( dataValueD );

        assertNotNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValuesBySource( sourceA );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValuesBySource( sourceB );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValuesBySource( sourceC );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValuesBySource( sourceD );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );
    }

    @Test
    public void testDeleteDataValuesByDataElement()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo );
        dataValueD.setValue( "4" );

        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );
        dataValueStore.addDataValue( dataValueC );
        dataValueStore.addDataValue( dataValueD );

        assertNotNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValuesByDataElement( dataElementA );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValuesByDataElement( dataElementB );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValuesByDataElement( dataElementC );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNotNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );

        dataValueStore.deleteDataValuesByDataElement( dataElementD );
        assertNull( dataValueStore.getDataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo ) );
        assertNull( dataValueStore.getDataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo ) );
    }

    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    @Test
    public void testGetAllDataValues()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo );
        dataValueD.setValue( "4" );
    
        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );
        dataValueStore.addDataValue( dataValueC );
        dataValueStore.addDataValue( dataValueD );
        
        Collection<DataValue> dataValues = dataValueStore.getAllDataValues();
        assertNotNull( dataValues );
        assertEquals( 4, dataValues.size() );
    }   

    @Test
    public void testGetDataValuesSourcePeriod()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo );
        dataValueD.setValue( "4" );

        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );
        dataValueStore.addDataValue( dataValueC );
        dataValueStore.addDataValue( dataValueD );

        Collection<DataValue> dataValues = dataValueStore.getDataValues( sourceA, periodA );
        assertNotNull( dataValues );
        assertEquals( 2, dataValues.size() );

        dataValues = dataValueStore.getDataValues( sourceB, periodC );
        assertNotNull( dataValues );
        assertEquals( 1, dataValues.size() );

        dataValues = dataValueStore.getDataValues( sourceB, periodD );
        assertNotNull( dataValues );
        assertEquals( 0, dataValues.size() );
    }

    @Test
    public void testGetDataValuesSourceDataElement()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementD, periodC, sourceB, optionCombo, optionCombo );
        dataValueD.setValue( "4" );

        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );
        dataValueStore.addDataValue( dataValueC );
        dataValueStore.addDataValue( dataValueD );

        Collection<DataValue> dataValues = dataValueStore.getDataValues( sourceA, dataElementA );
        assertNotNull( dataValues );
        assertEquals( 1, dataValues.size() );

        dataValues = dataValueStore.getDataValues( sourceA, dataElementB );
        assertNotNull( dataValues );
        assertEquals( 1, dataValues.size() );

        dataValues = dataValueStore.getDataValues( sourceA, dataElementC );
        assertNotNull( dataValues );
        assertEquals( 0, dataValues.size() );
    }

    @Test
    public void testGetDataValuesSourcesDataElement()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementA, periodC, sourceB, optionCombo, optionCombo );
        dataValueD.setValue( "4" );

        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );
        dataValueStore.addDataValue( dataValueC );
        dataValueStore.addDataValue( dataValueD );

        Collection<OrganisationUnit> sources = new HashSet<OrganisationUnit>();
        sources.add( sourceA );
        sources.add( sourceB );

        Collection<DataValue> dataValues = dataValueStore.getDataValues( sources, dataElementA );
        assertNotNull( dataValues );
        assertEquals( 2, dataValues.size() );

        dataValues = dataValueStore.getDataValues( sources, dataElementB );
        assertNotNull( dataValues );
        assertEquals( 1, dataValues.size() );

        dataValues = dataValueStore.getDataValues( sources, dataElementC );
        assertNotNull( dataValues );
        assertEquals( 0, dataValues.size() );
    }

    @Test
    public void testGetDataValuesSourcePeriodDataElements()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceA, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementB, periodA, sourceA, optionCombo, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementC, periodC, sourceD, optionCombo, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementA, periodC, sourceB, optionCombo, optionCombo );
        dataValueD.setValue( "4" );

        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );
        dataValueStore.addDataValue( dataValueC );
        dataValueStore.addDataValue( dataValueD );

        Collection<DataElement> dataElements = new HashSet<DataElement>();
        dataElements.add( dataElementA );
        dataElements.add( dataElementB );

        Collection<DataValue> dataValues = dataValueStore.getDataValues( sourceA, periodA, dataElements );
        assertNotNull( dataValues );
        assertEquals( 2, dataValues.size() );

        dataValues = dataValueStore.getDataValues( sourceB, periodC, dataElements );
        assertNotNull( dataValues );
        assertEquals( 1, dataValues.size() );

        dataValues = dataValueStore.getDataValues( sourceD, periodC, dataElements );
        assertNotNull( dataValues );
        assertEquals( 0, dataValues.size() );
    }

    @Test
    public void testGetDataValuesDataElementPeriodsSources()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceB, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementA, periodB, sourceA, optionCombo, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementA, periodA, sourceC, optionCombo, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementB, periodB, sourceD, optionCombo, optionCombo );
        dataValueD.setValue( "4" );
        
        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );
        dataValueStore.addDataValue( dataValueC );
        dataValueStore.addDataValue( dataValueD );
        
        Collection<Period> periods = new HashSet<Period>();
        periods.add( periodA );
        periods.add( periodB );

        Collection<OrganisationUnit> sources = new HashSet<OrganisationUnit>();
        sources.add( sourceA );
        sources.add( sourceB );
        
        Collection<DataValue> dataValues = dataValueStore.getDataValues( dataElementA, periods, sources );
        
        assertEquals( dataValues.size(), 2 );
        assertTrue( dataValues.contains( dataValueA ) );
        assertTrue( dataValues.contains( dataValueB ) );
    }

    @Test
    public void testGetDataValuesOptionComboDataElementPeriodsSources()
        throws Exception
    {
        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceB, optionCombo, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementA, periodB, sourceA, optionCombo, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementA, periodA, sourceC, optionCombo, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementB, periodB, sourceD, optionCombo, optionCombo );
        dataValueD.setValue( "4" );
        
        dataValueStore.addDataValue( dataValueA );
        dataValueStore.addDataValue( dataValueB );
        dataValueStore.addDataValue( dataValueC );
        dataValueStore.addDataValue( dataValueD );
        
        Collection<Period> periods = new HashSet<Period>();
        periods.add( periodA );
        periods.add( periodB );

        Collection<OrganisationUnit> sources = new HashSet<OrganisationUnit>();
        sources.add( sourceA );
        sources.add( sourceB );
        
        Collection<DataValue> dataValues = dataValueStore.getDataValues( dataElementA, optionCombo, periods, sources );
        
        assertEquals( dataValues.size(), 2 );
        assertTrue( dataValues.contains( dataValueA ) );
        assertTrue( dataValues.contains( dataValueB ) );
    }
}
