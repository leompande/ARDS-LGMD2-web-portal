package org.hisp.dhis.mapping;

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
import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
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
public class MappingServiceTest
    extends DhisSpringTest
{
    private MappingService mappingService;

    private OrganisationUnit organisationUnit;

    private OrganisationUnitLevel organisationUnitLevel;

    private IndicatorGroup indicatorGroup;

    private IndicatorType indicatorType;

    private Indicator indicator;

    private DataElement dataElement;

    private DataElementGroup dataElementGroup;

    private PeriodType periodType;

    private Period period;

    private MapLegendSet mapLegendSet;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        mappingService = (MappingService) getBean( MappingService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        indicatorService = (IndicatorService) getBean( IndicatorService.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitLevel = new OrganisationUnitLevel( 1, "Level" );

        organisationUnitService.addOrganisationUnit( organisationUnit );
        organisationUnitService.addOrganisationUnitLevel( organisationUnitLevel );

        indicatorGroup = createIndicatorGroup( 'A' );
        indicatorService.addIndicatorGroup( indicatorGroup );

        indicatorType = createIndicatorType( 'A' );
        indicatorService.addIndicatorType( indicatorType );

        indicator = createIndicator( 'A', indicatorType );
        indicatorService.addIndicator( indicator );

        dataElement = createDataElement( 'A' );
        dataElementService.addDataElement( dataElement );

        dataElementGroup = createDataElementGroup( 'A' );
        dataElementGroup.getMembers().add( dataElement );
        dataElementService.addDataElementGroup( dataElementGroup );

        periodType = periodService.getPeriodTypeByName( MonthlyPeriodType.NAME );
        period = createPeriod( periodType, getDate( 2000, 1, 1 ), getDate( 2000, 2, 1 ) );
        periodService.addPeriod( period );

        mapLegendSet = createMapLegendSet( 'A' );
        mappingService.addMapLegendSet( mapLegendSet );
    }

    // -------------------------------------------------------------------------
    // MapLegend
    // -------------------------------------------------------------------------

    @Test
    public void testGetAddOrUpdateMapLegend()
    {
        MapLegend legend = createMapLegend( 'A', 0.1, 0.2 );

        mappingService.addOrUpdateMapLegend( legend.getName(), legend.getStartValue(), legend.getEndValue(),
            legend.getColor(), legend.getImage() );

        legend = mappingService.getMapLegendByName( legend.getName() );

        assertNotNull( legend );

        int id = legend.getId();

        mappingService.addOrUpdateMapLegend( legend.getName(), legend.getStartValue(), 0.3, "ColorB", "img.png" );

        assertEquals( "MapLegendA", mappingService.getMapLegend( id ).getName() );
        assertEquals( new Double( 0.1 ), mappingService.getMapLegend( id ).getStartValue() );
        assertEquals( new Double( 0.3 ), mappingService.getMapLegend( id ).getEndValue() );
        assertEquals( "ColorB", mappingService.getMapLegend( id ).getColor() );
        assertEquals( "img.png", mappingService.getMapLegend( id ).getImage() );
    }

    @Test
    public void testDeleteMapLegend()
    {
        MapLegend legend = createMapLegend( 'A', 0.1, 0.2 );

        int id = mappingService.addMapLegend( legend );

        legend = mappingService.getMapLegend( id );
        
        assertNotNull( legend );

        mappingService.deleteMapLegend( legend );

        assertNull( mappingService.getMapLegend( id ) );
    }

    @Test
    public void testGetAllMapLegends()
    {
        MapLegend legend1 = createMapLegend( 'A', 0.1, 0.2 );
        MapLegend legend2 = createMapLegend( 'B', 0.3, 0.4 );
        MapLegend legend3 = createMapLegend( 'C', 0.5, 0.6 );

        mappingService.addMapLegend( legend1 );
        mappingService.addMapLegend( legend3 );

        Collection<MapLegend> legends = mappingService.getAllMapLegends();
        
        assertEquals( 2, legends.size() );
        assertTrue( legends.contains( legend1 ) );
        assertTrue( legends.contains( legend3 ) );
        assertFalse( legends.contains( legend2 ) );
    }

    @Test
    public void testGetMapLegendsByName()
    {
        MapLegend legend1 = createMapLegend( 'A', 0.1, 0.2 );
        MapLegend legend2 = createMapLegend( 'B', 0.3, 0.4 );
        
        mappingService.addMapLegend( legend1 );
        mappingService.addMapLegend( legend2 );
        
        assertNotNull( mappingService.getMapLegendByName( "MapLegendA" ) );
        assertNotNull( mappingService.getMapLegendByName( "MapLegendB" ) );
        assertNull( mappingService.getMapLegendByName( "MapLegendC" ) );
    }

    // -------------------------------------------------------------------------
    // MapLegendSet
    // -------------------------------------------------------------------------

    @Test
    public void testAddGetMapLegendSet()
    {
        MapLegendSet legendSet = createMapLegendSet( 'B' );

        int id = mappingService.addMapLegendSet( legendSet );

        assertNotNull( mappingService.getMapLegendSet( id ) );
    }

    @Test
    public void testGetUpdateMapLegendSet()
    {
        MapLegendSet legendSet = createMapLegendSet( 'F' );
        legendSet.setSymbolizer( "SymbolF" );

        int id = mappingService.addMapLegendSet( legendSet );

        legendSet = mappingService.getMapLegendSet( id );

        assertNotNull( legendSet );
        assertEquals( "SymbolF", legendSet.getSymbolizer() );

        legendSet.setSymbolizer( "SymbolG" );

        mappingService.updateMapLegendSet( legendSet );

        legendSet = mappingService.getMapLegendSet( id );

        assertNotNull( legendSet );
        assertEquals( "SymbolG", legendSet.getSymbolizer() );
    }

    @Test
    public void testGetAllMapLegendSets()
    {
        MapLegendSet legendSet1 = createMapLegendSet( 'B' );
        MapLegendSet legendSet2 = createMapLegendSet( 'C' );
        MapLegendSet legendSet3 = createMapLegendSet( 'D' );

        Collection<MapLegendSet> mapLegendSets = new HashSet<MapLegendSet>();

        mapLegendSets.add( mapLegendSet );
        mapLegendSets.add( legendSet1 );
        mapLegendSets.add( legendSet2 );
        mapLegendSets.add( legendSet3 );

        mappingService.addMapLegendSet( legendSet1 );
        mappingService.addMapLegendSet( legendSet2 );
        mappingService.addMapLegendSet( legendSet3 );

        assertTrue( mappingService.getAllMapLegendSets().containsAll( mapLegendSets ) );
    }

    @Test
    public void testGetMapLegendSetByName()
    {
        MapLegendSet legendSet1 = createMapLegendSet( 'B' );
        MapLegendSet legendSet2 = createMapLegendSet( 'C' );

        mappingService.addMapLegendSet( legendSet1 );
        mappingService.addMapLegendSet( legendSet2 );

        assertNotNull( mappingService.getMapLegendSetByName( "MapLegendSetB" ) );
        assertNotNull( mappingService.getMapLegendSetByName( "MapLegendSetC" ) );
        assertNull( mappingService.getMapLegendSetByName( "MapLegendSetD" ) );
    }
}
