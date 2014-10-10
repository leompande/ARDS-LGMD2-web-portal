package org.hisp.dhis.analytics.data;

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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.IllegalQueryException;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.mock.MockCurrentUserService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class AnalyticsServiceTest
    extends DhisSpringTest
{
    private DataElement deA;
    private DataElement deB;
    private DataElement deC;
    private DataElement deD;
    
    private OrganisationUnit ouA;
    private OrganisationUnit ouB;
    private OrganisationUnit ouC;
    private OrganisationUnit ouD;
    private OrganisationUnit ouE;
    
    private OrganisationUnitGroup ouGroupA;
    private OrganisationUnitGroup ouGroupB;
    private OrganisationUnitGroup ouGroupC;
    
    private OrganisationUnitGroupSet ouGroupSetA;
            
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    @Override
    public void setUpTest()
    {
        deA = createDataElement( 'A' );
        deB = createDataElement( 'B' );
        deC = createDataElement( 'C' );
        deD = createDataElement( 'D' );
        
        dataElementService.addDataElement( deA );
        dataElementService.addDataElement( deB );
        dataElementService.addDataElement( deC );
        dataElementService.addDataElement( deD );
        
        ouA = createOrganisationUnit( 'A' );
        ouB = createOrganisationUnit( 'B' );
        ouC = createOrganisationUnit( 'C' );
        ouD = createOrganisationUnit( 'D' );
        ouE = createOrganisationUnit( 'E' );
        
        ouB.updateParent( ouA );
        ouC.updateParent( ouA );
        ouD.updateParent( ouB );
        ouE.updateParent( ouB );
        
        organisationUnitService.addOrganisationUnit( ouA );
        organisationUnitService.addOrganisationUnit( ouB );
        organisationUnitService.addOrganisationUnit( ouC );
        organisationUnitService.addOrganisationUnit( ouD );
        organisationUnitService.addOrganisationUnit( ouE );

        ouGroupSetA = createOrganisationUnitGroupSet( 'A' );
        
        organisationUnitGroupService.addOrganisationUnitGroupSet( ouGroupSetA );
        
        ouGroupA = createOrganisationUnitGroup( 'A' );
        ouGroupB = createOrganisationUnitGroup( 'B' );
        ouGroupC = createOrganisationUnitGroup( 'C' );
        
        ouGroupA.setGroupSet( ouGroupSetA );
        ouGroupB.setGroupSet( ouGroupSetA );
        ouGroupC.setGroupSet( ouGroupSetA );
        
        organisationUnitGroupService.addOrganisationUnitGroup( ouGroupA );
        organisationUnitGroupService.addOrganisationUnitGroup( ouGroupB );
        organisationUnitGroupService.addOrganisationUnitGroup( ouGroupC );
        
        ouGroupSetA.getOrganisationUnitGroups().add( ouGroupA );
        ouGroupSetA.getOrganisationUnitGroups().add( ouGroupB );
        ouGroupSetA.getOrganisationUnitGroups().add( ouGroupC );
        
        organisationUnitGroupService.updateOrganisationUnitGroupSet( ouGroupSetA );

        // ---------------------------------------------------------------------
        // Mock injection
        // ---------------------------------------------------------------------

        User user = createUser( 'A' );
        user.addOrganisationUnit( ouA );
        
        CurrentUserService currentUserService = new MockCurrentUserService( user );
        
        setDependency( analyticsService, "currentUserService", currentUserService );
    }
    
    @Test
    public void testGetFromUrlA()
    {
        Set<String> dimensionParams = new HashSet<String>();
        dimensionParams.add( "dx:" + deA.getUid() + ";" + deB.getUid() + ";" + deC.getUid() + ";" + deD.getUid() );
        dimensionParams.add( "pe:2012;2012S1;2012S2" );
        dimensionParams.add( ouGroupSetA.getUid() + ":" + ouGroupA.getUid() + ";" + ouGroupB.getUid() + ";" + ouGroupC.getUid() );
        
        Set<String> filterParams = new HashSet<String>();
        filterParams.add( "ou:" + ouA.getUid() + ";" + ouB.getUid() + ";" + ouC.getUid() + ";" + ouD.getUid() + ";" + ouE.getUid() );
        
        DataQueryParams params = analyticsService.getFromUrl( dimensionParams, filterParams, null, null, false, false, false, false, false, false, null );
        
        assertEquals( 4, params.getDataElements().size() );
        assertEquals( 3, params.getPeriods().size() );
        assertEquals( 5, params.getFilterOrganisationUnits().size() );
        assertEquals( 3, params.getDimensionOptions( ouGroupSetA.getUid() ).size() );
    }

    @Test
    public void testGetFromUrlB()
    {
        Set<String> dimensionParams = new HashSet<String>();
        dimensionParams.add( "dx:" + deA.getUid() + ";" + deB.getUid() + ";" + deC.getUid() + ";" + deD.getUid() );

        Set<String> filterParams = new HashSet<String>();
        filterParams.add( "ou:" + ouA.getUid() );
        
        DataQueryParams params = analyticsService.getFromUrl( dimensionParams, filterParams, null, null, false, false, false, false, false, false, null );
        
        assertEquals( 4, params.getDataElements().size() );
        assertEquals( 1, params.getFilterOrganisationUnits().size() );
    }
    
    @Test
    public void testGetFromUrlRelativePeriods()
    {
        Set<String> dimensionParams = new HashSet<String>();
        dimensionParams.add( "dx:" + deA.getUid() + ";" + deB.getUid() + ";" + deC.getUid() + ";" + deD.getUid() );
        dimensionParams.add( "pe:LAST_12_MONTHS" );

        Set<String> filterParams = new HashSet<String>();
        filterParams.add( "ou:" + ouA.getUid() + ";" + ouB.getUid() );

        DataQueryParams params = analyticsService.getFromUrl( dimensionParams, filterParams, null, null, false, false, false, false, false, false, null );
        
        assertEquals( 4, params.getDataElements().size() );
        assertEquals( 12, params.getPeriods().size() );
        assertEquals( 2, params.getFilterOrganisationUnits().size() );
    }
    
    @Test
    public void testGetFromUrlUserOrgUnit()
    {
        Set<String> dimensionParams = new HashSet<String>();
        dimensionParams.add( "ou:" + OrganisationUnit.KEY_USER_ORGUNIT );
        dimensionParams.add( "dx:" + deA.getUid() + ";" + deB.getUid() );
        dimensionParams.add( "pe:2011;2012" );
        
        DataQueryParams params = analyticsService.getFromUrl( dimensionParams, null, null, null, false, false, false, false, false, false, null );
        
        assertEquals( 1, params.getOrganisationUnits().size() );  
        assertEquals( 2, params.getDataElements().size() );
        assertEquals( 2, params.getPeriods().size() );      
    }
    
    @Test
    public void testGetFromUrlOrgUnitLevel()
    {
        Set<String> dimensionParams = new HashSet<String>();
        dimensionParams.add( "ou:LEVEL-2-" + ouA.getUid() );
        dimensionParams.add( "dx:" + deA.getUid() + ";" + deB.getUid() );
        dimensionParams.add( "pe:2011;2012" );
        
        DataQueryParams params = analyticsService.getFromUrl( dimensionParams, null, null, null, false, false, false, false, false, false, null );
        
        assertEquals( 2, params.getOrganisationUnits().size() );  
        assertEquals( 2, params.getDataElements().size() );
        assertEquals( 2, params.getPeriods().size() ); 
    }

    @Test( expected = IllegalQueryException.class )
    public void testGetFromUrlNoDx()
    {
        Set<String> dimensionParams = new HashSet<String>();
        dimensionParams.add( "dx" );
        dimensionParams.add( "pe:2012,2012S1,2012S2" );
        
        analyticsService.getFromUrl( dimensionParams, null, null, null, false, false, false, false, false, false, null );        
    }
    
    @Test( expected = IllegalQueryException.class )
    public void testGetFromUrlNoPeriods()
    {
        Set<String> dimensionParams = new HashSet<String>();
        dimensionParams.add( "dx:" + BASE_UID + "A;" + BASE_UID + "B;" + BASE_UID + "C;" + BASE_UID + "D" );
        dimensionParams.add( "pe" );

        analyticsService.getFromUrl( dimensionParams, null, null, null, false, false, false, false, false, false, null );        
    }

    @Test( expected = IllegalQueryException.class )
    public void testGetFromUrlNoOrganisationUnits()
    {
        Set<String> dimensionParams = new HashSet<String>();
        dimensionParams.add( "dx:" + BASE_UID + "A;" + BASE_UID + "B;" + BASE_UID + "C;" + BASE_UID + "D" );
        dimensionParams.add( "ou" );
        
        analyticsService.getFromUrl( dimensionParams, null, null, null, false, false, false, false, false, false, null );        
    }

    @Test( expected = IllegalQueryException.class )
    public void testGetFromUrlInvalidDimension()
    {
        Set<String> dimensionParams = new HashSet<String>();
        dimensionParams.add( "dx:" + BASE_UID + "A;" + BASE_UID + "B;" + BASE_UID + "C;" + BASE_UID + "D" );
        dimensionParams.add( "yebo:2012,2012S1,2012S2" );
        
        analyticsService.getFromUrl( dimensionParams, null, null, null, false, false, false, false, false, false, null );        
    }
    
    @Test
    public void testGetFromAnalyticalObjectA()
    {
        Chart chart = new Chart();
        chart.setSeries( DimensionalObject.DATA_X_DIM_ID );
        chart.setCategory( DimensionalObject.ORGUNIT_DIM_ID );
        chart.getFilterDimensions().add( DimensionalObject.PERIOD_DIM_ID );
        
        chart.getDataElements().add( deA );
        chart.getDataElements().add( deB );
        chart.getDataElements().add( deC );
        
        chart.getOrganisationUnits().add( ouA );
        chart.getOrganisationUnits().add( ouB );
        
        chart.getPeriods().add( PeriodType.getPeriodFromIsoString( "2012" ) );
        
        DataQueryParams params = analyticsService.getFromAnalyticalObject( chart, null );
        
        assertNotNull( params );
        assertEquals( 3, params.getDataElements().size() );
        assertEquals( 2, params.getOrganisationUnits().size() );
        assertEquals( 1, params.getFilterPeriods().size() );
        assertEquals( 2, params.getDimensions().size() );
        assertEquals( 1, params.getFilters().size() );
    }
    
    @Test
    public void testGetFromAnalyticalObjectB()
    {
        Chart chart = new Chart();
        chart.setSeries( DimensionalObject.DATA_X_DIM_ID );
        chart.setCategory( ouGroupSetA.getUid() );
        chart.getFilterDimensions().add( DimensionalObject.PERIOD_DIM_ID );
        
        chart.getDataElements().add( deA );
        chart.getDataElements().add( deB );
        chart.getDataElements().add( deC );
        
        chart.getOrganisationUnitGroups().add( ouGroupA );
        chart.getOrganisationUnitGroups().add( ouGroupB );
        chart.getOrganisationUnitGroups().add( ouGroupC );
        
        chart.getPeriods().add( PeriodType.getPeriodFromIsoString( "2012" ) );
        
        DataQueryParams params = analyticsService.getFromAnalyticalObject( chart, null );
        
        assertNotNull( params );
        assertEquals( 3, params.getDataElements().size() );
        assertEquals( 1, params.getFilterPeriods().size() );
        assertEquals( 2, params.getDimensions().size() );
        assertEquals( 1, params.getFilters().size() );
        assertEquals( 3, params.getDimensionOptions( ouGroupSetA.getUid() ).size() );
    }
}
