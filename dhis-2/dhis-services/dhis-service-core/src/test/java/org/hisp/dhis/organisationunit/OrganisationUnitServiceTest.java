package org.hisp.dhis.organisationunit;

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

import org.hisp.dhis.DhisSpringTest;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Kristian Nordal
 */
public class OrganisationUnitServiceTest
    extends DhisSpringTest
{
    private OrganisationUnitService organisationUnitService;

    private OrganisationUnitGroupService organisationUnitGroupService;

    @Override
    public void setUpTest()
        throws Exception
    {
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        organisationUnitGroupService = (OrganisationUnitGroupService) getBean( OrganisationUnitGroupService.ID );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    @Test
    public void testBasicOrganisationUnitCoarseGrained()
        throws Exception
    {
        // Single OrganisationUnit
        String organisationUnitName1 = "organisationUnitName1";
        OrganisationUnit organisationUnit1 = new OrganisationUnit( organisationUnitName1, "shortName1",
            "organisationUnitCode1", new Date(), new Date(), true, "comment" );

        int id1 = organisationUnitService.addOrganisationUnit( organisationUnit1 );

        // assert getOrganisationUnit
        assertNotNull( organisationUnitService.getOrganisationUnit( id1 ) );

        assertNull( organisationUnitService.getOrganisationUnit( -1 ) );

        // OrganisationUnit with parent
        String organisationUnitName2 = "organisationUnitName2";
        OrganisationUnit organisationUnit2 = new OrganisationUnit( organisationUnitName2, organisationUnit1,
            "shortName2", "organisationUnitCode2", new Date(), new Date(), true, "comment" );

        int id2 = organisationUnitService.addOrganisationUnit( organisationUnit2 );

        assertTrue( organisationUnitService.getOrganisationUnit( id2 ).getParent().getId() == id1 );

        organisationUnitService.deleteOrganisationUnit( organisationUnitService.getOrganisationUnit( id2 ) );

        organisationUnitService.deleteOrganisationUnit( organisationUnitService.getOrganisationUnit( id1 ) );

        // assert delOrganisationUnit
        assertNull( organisationUnitService.getOrganisationUnit( id1 ) );
        assertNull( organisationUnitService.getOrganisationUnit( id2 ) );
    }

    @Test
    public void testUpdateOrganisationUnit()
        throws Exception
    {
        String name = "name";
        String shortName = "shortName";
        String updatedName = "updatedName";
        String updatedShortName = "updatedShortName";

        OrganisationUnit organisationUnit = new OrganisationUnit( name, shortName, "organisationUnitCode", new Date(),
            new Date(), true, "comment" );

        int id = organisationUnitService.addOrganisationUnit( organisationUnit );

        organisationUnit.setName( updatedName );
        organisationUnit.setShortName( updatedShortName );

        organisationUnitService.updateOrganisationUnit( organisationUnit );

        OrganisationUnit updatedOrganisationUnit = organisationUnitService.getOrganisationUnit( id );

        assertEquals( updatedOrganisationUnit.getName(), updatedName );
        assertEquals( updatedOrganisationUnit.getShortName(), updatedShortName );
    }

    @Test
    public void testGetOrganisationUnitWithChildren()
        throws Exception
    {
        OrganisationUnit unit1 = createOrganisationUnit( 'A' );
        OrganisationUnit unit2 = createOrganisationUnit( 'B', unit1 );
        OrganisationUnit unit3 = createOrganisationUnit( 'C', unit2 );
        OrganisationUnit unit4 = createOrganisationUnit( 'D' );

        int id1 = organisationUnitService.addOrganisationUnit( unit1 );
        unit1.getChildren().add( unit2 );
        organisationUnitService.addOrganisationUnit( unit2 );
        organisationUnitService.addOrganisationUnit( unit3 );
        organisationUnitService.addOrganisationUnit( unit4 );

        List<OrganisationUnit> actual = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( id1 ) );

        assertEquals( 3, actual.size() );
        assertTrue( actual.contains( unit1 ) );
        assertTrue( actual.contains( unit2 ) );
    }

    @Test
    public void testGetOrganisationUnitWithChildrenWithCorrectLevel()
        throws Exception
    {
        OrganisationUnit unit1 = createOrganisationUnit( 'A' );
        OrganisationUnit unit2 = createOrganisationUnit( 'B', unit1 );
        OrganisationUnit unit3 = createOrganisationUnit( 'C', unit2 );

        int id1 = organisationUnitService.addOrganisationUnit( unit1 );
        unit1.getChildren().add( unit2 );
        int id2 = organisationUnitService.addOrganisationUnit( unit2 );
        organisationUnitService.addOrganisationUnit( unit3 );

        List<OrganisationUnit> actual1 = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( id1 ) );
        List<OrganisationUnit> actual2 = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( id2 ) );

        assertEquals( 1, actual1.get( 0 ).getLevel() );
        assertEquals( 2, actual1.get( 1 ).getLevel() );
        assertEquals( 3, actual1.get( 2 ).getLevel() );

        assertEquals( 2, actual2.get( 0 ).getLevel() );
        assertEquals( 3, actual2.get( 1 ).getLevel() );
    }

    @Test
    public void testGetOrganisationUnitsByFields()
        throws Exception
    {
        String oU1Name = "OU1name";
        String oU2Name = "OU2name";
        String oU3Name = "OU3name";
        String oU1ShortName = "OU1ShortName";
        String oU2ShortName = "OU2ShortName";
        String oU3ShortName = "OU3ShortName";
        String oU1Code = "OU1Code";
        String oU2Code = "OU2Code";
        String oU3Code = "OU3Code";

        OrganisationUnit organisationUnit1 = new OrganisationUnit( oU1Name, null, oU1ShortName, oU1Code, null, null,
            true, null );
        OrganisationUnit organisationUnit2 = new OrganisationUnit( oU2Name, null, oU2ShortName, oU2Code, null, null,
            true, null );
        OrganisationUnit organisationUnit3 = new OrganisationUnit( oU3Name, null, oU3ShortName, oU3Code, null, null,
            false, null );

        organisationUnitService.addOrganisationUnit( organisationUnit1 );
        organisationUnitService.addOrganisationUnit( organisationUnit2 );
        organisationUnitService.addOrganisationUnit( organisationUnit3 );

        OrganisationUnit unit1 = organisationUnitService.getOrganisationUnitByName( oU1Name ).get( 0 );
        assertEquals( unit1.getName(), oU1Name );

        List<OrganisationUnit> foo = organisationUnitService.getOrganisationUnitByName( "foo" );
        assertTrue( foo.isEmpty() );

        unit1 = organisationUnitService.getOrganisationUnitByCode( oU1Code );
        assertEquals( unit1.getName(), oU1Name );

        OrganisationUnit unit4 = organisationUnitService.getOrganisationUnitByCode( "foo" );
        assertNull( unit4 );
    }

    @Test
    public void testGetOrganisationUnitGraph()
        throws Exception
    {
        OrganisationUnit organisationUnit1 = new OrganisationUnit( "Foo", "shortName1", "organisationUnitCode1",
            new Date(), new Date(), true, "comment" );
        OrganisationUnit organisationUnit2 = new OrganisationUnit( "Bar", organisationUnit1, "shortName2",
            "organisationUnitCode2", new Date(), new Date(), true, "comment" );
        OrganisationUnit organisationUnit3 = new OrganisationUnit( "Foobar", organisationUnit2, "shortName3",
            "organisationUnitCode3", new Date(), new Date(), true, "comment" );

        int orgId1 = organisationUnitService.addOrganisationUnit( organisationUnit1 );
        int orgId2 = organisationUnitService.addOrganisationUnit( organisationUnit2 );
        int orgId3 = organisationUnitService.addOrganisationUnit( organisationUnit3 );

        List<OrganisationUnit> graph = organisationUnitService.getOrganisationUnitBranch( orgId3 );
        assertNotNull( graph );
        assertEquals( 3, graph.size() );
        OrganisationUnit orgUnit = graph.get( 0 );
        assertEquals( orgId1, orgUnit.getId() );
        orgUnit = graph.get( 1 );
        assertEquals( orgId2, orgUnit.getId() );
        orgUnit = graph.get( 2 );
        assertEquals( orgId3, orgUnit.getId() );
    }

    @Test
    public void testGetAllOrganisationUnitsAndGetRootOrganisationUnit()
        throws Exception
    {
        // creating a tree with two roots ( id1 and id4 )

        OrganisationUnit unit1 = new OrganisationUnit( "OU1name", "OU1sname", "OU1code", null, null, true, null );
        OrganisationUnit unit2 = new OrganisationUnit( "OU2name", unit1, "OU2sname", "OU2code", null, null, true, null );
        OrganisationUnit unit3 = new OrganisationUnit( "OU3name", unit1, "OU3sname", "OU3code", null, null, true, null );
        OrganisationUnit unit4 = new OrganisationUnit( "OU4name", "OU4sname", "OU4code", null, null, true, null );
        OrganisationUnit unit5 = new OrganisationUnit( "OU5name", unit4, "OU5sname", "OU5code", null, null, true, null );

        organisationUnitService.addOrganisationUnit( unit1 );
        organisationUnitService.addOrganisationUnit( unit2 );
        organisationUnitService.addOrganisationUnit( unit3 );
        organisationUnitService.addOrganisationUnit( unit4 );
        organisationUnitService.addOrganisationUnit( unit5 );

        Collection<OrganisationUnit> units = organisationUnitService.getAllOrganisationUnits();

        assertNotNull( units );
        assertEquals( 5, units.size() );
        assertTrue( units.contains( unit1 ) );
        assertTrue( units.contains( unit2 ) );
        assertTrue( units.contains( unit3 ) );
        assertTrue( units.contains( unit4 ) );
        assertTrue( units.contains( unit5 ) );

        units = organisationUnitService.getRootOrganisationUnits();

        assertNotNull( units );
        assertEquals( 2, units.size() );
        assertTrue( units.contains( unit1 ) );
        assertTrue( units.contains( unit4 ) );
    }

    @Test
    public void testGetOrganisationUnitsAtLevel()
        throws Exception
    {
        OrganisationUnit unit1 = createOrganisationUnit( '1' );
        organisationUnitService.addOrganisationUnit( unit1 );

        OrganisationUnit unit2 = createOrganisationUnit( '2', unit1 );
        unit1.getChildren().add( unit2 );
        organisationUnitService.addOrganisationUnit( unit2 );

        OrganisationUnit unit3 = createOrganisationUnit( '3', unit2 );
        unit2.getChildren().add( unit3 );
        organisationUnitService.addOrganisationUnit( unit3 );

        OrganisationUnit unit4 = createOrganisationUnit( '4', unit2 );
        unit2.getChildren().add( unit4 );
        organisationUnitService.addOrganisationUnit( unit4 );

        OrganisationUnit unit5 = createOrganisationUnit( '5', unit2 );
        unit2.getChildren().add( unit5 );
        organisationUnitService.addOrganisationUnit( unit5 );

        OrganisationUnit unit6 = createOrganisationUnit( '6', unit3 );
        unit3.getChildren().add( unit6 );
        organisationUnitService.addOrganisationUnit( unit6 );

        OrganisationUnit unit7 = createOrganisationUnit( '7' );
        organisationUnitService.addOrganisationUnit( unit7 );

        assertTrue( organisationUnitService.getOrganisationUnitsAtLevel( 1 ).size() == 2 );
        assertTrue( organisationUnitService.getOrganisationUnitsAtLevel( 3 ).size() == 3 );
        assertTrue( organisationUnitService.getNumberOfOrganisationalLevels() == 4 );
        assertTrue( organisationUnitService.getOrganisationUnit( unit4.getId() ).getOrganisationUnitLevel() == 3 );
        assertTrue( organisationUnitService.getOrganisationUnit( unit1.getId() ).getOrganisationUnitLevel() == 1 );
        assertTrue( organisationUnitService.getOrganisationUnit( unit6.getId() ).getOrganisationUnitLevel() == 4 );
    }

    @Test
    public void testGetOrganisationUnitAtLevelAndBranch()
        throws Exception
    {
        OrganisationUnit unitA = createOrganisationUnit( 'A' );
        OrganisationUnit unitB = createOrganisationUnit( 'B', unitA );
        OrganisationUnit unitC = createOrganisationUnit( 'C', unitA );
        OrganisationUnit unitD = createOrganisationUnit( 'D', unitB );
        OrganisationUnit unitE = createOrganisationUnit( 'E', unitB );
        OrganisationUnit unitF = createOrganisationUnit( 'F', unitC );
        OrganisationUnit unitG = createOrganisationUnit( 'G', unitC );
        OrganisationUnit unitH = createOrganisationUnit( 'H', unitD );
        OrganisationUnit unitI = createOrganisationUnit( 'I', unitD );
        OrganisationUnit unitJ = createOrganisationUnit( 'J', unitE );
        OrganisationUnit unitK = createOrganisationUnit( 'K', unitE );
        OrganisationUnit unitL = createOrganisationUnit( 'L', unitF );
        OrganisationUnit unitM = createOrganisationUnit( 'M', unitF );
        OrganisationUnit unitN = createOrganisationUnit( 'N', unitG );
        OrganisationUnit unitO = createOrganisationUnit( 'O', unitG );

        unitA.getChildren().add( unitB );
        unitA.getChildren().add( unitC );
        unitB.getChildren().add( unitD );
        unitB.getChildren().add( unitE );
        unitC.getChildren().add( unitF );
        unitC.getChildren().add( unitG );
        unitD.getChildren().add( unitH );
        unitD.getChildren().add( unitI );
        unitE.getChildren().add( unitJ );
        unitE.getChildren().add( unitK );
        unitF.getChildren().add( unitL );
        unitF.getChildren().add( unitM );
        unitG.getChildren().add( unitN );
        unitG.getChildren().add( unitO );

        organisationUnitService.addOrganisationUnit( unitA );
        organisationUnitService.addOrganisationUnit( unitB );
        organisationUnitService.addOrganisationUnit( unitC );
        organisationUnitService.addOrganisationUnit( unitD );
        organisationUnitService.addOrganisationUnit( unitE );
        organisationUnitService.addOrganisationUnit( unitF );
        organisationUnitService.addOrganisationUnit( unitG );
        organisationUnitService.addOrganisationUnit( unitH );
        organisationUnitService.addOrganisationUnit( unitI );
        organisationUnitService.addOrganisationUnit( unitJ );
        organisationUnitService.addOrganisationUnit( unitK );
        organisationUnitService.addOrganisationUnit( unitL );
        organisationUnitService.addOrganisationUnit( unitM );
        organisationUnitService.addOrganisationUnit( unitN );
        organisationUnitService.addOrganisationUnit( unitO );

        Collection<OrganisationUnit> nill = null;

        assertTrue( equals( organisationUnitService.getOrganisationUnitsAtLevel( 2, unitB ), unitB ) );
        assertTrue( equals( organisationUnitService.getOrganisationUnitsAtLevel( 3, unitB ), unitD, unitE ) );
        assertTrue( equals( organisationUnitService.getOrganisationUnitsAtLevel( 4, unitB ), unitH, unitI, unitJ, unitK ) );
        assertTrue( equals( organisationUnitService.getOrganisationUnitsAtLevel( 2, nill ), unitB, unitC ) );

        assertEquals( 2, unitB.getLevel() );
        assertEquals( 3, unitD.getLevel() );
        assertEquals( 3, unitE.getLevel() );
        assertEquals( 4, unitH.getLevel() );
        assertEquals( 4, unitI.getLevel() );
        assertEquals( 4, unitJ.getLevel() );
        assertEquals( 4, unitK.getLevel() );
    }

    @Test
    public void testGetOrganisationUnitAtLevelAndBranches()
        throws Exception
    {
        OrganisationUnit unitA = createOrganisationUnit( 'A' );
        OrganisationUnit unitB = createOrganisationUnit( 'B', unitA );
        OrganisationUnit unitC = createOrganisationUnit( 'C', unitA );
        OrganisationUnit unitD = createOrganisationUnit( 'D', unitB );
        OrganisationUnit unitE = createOrganisationUnit( 'E', unitB );
        OrganisationUnit unitF = createOrganisationUnit( 'F', unitC );
        OrganisationUnit unitG = createOrganisationUnit( 'G', unitC );
        OrganisationUnit unitH = createOrganisationUnit( 'H', unitD );
        OrganisationUnit unitI = createOrganisationUnit( 'I', unitD );
        OrganisationUnit unitJ = createOrganisationUnit( 'J', unitE );
        OrganisationUnit unitK = createOrganisationUnit( 'K', unitE );
        OrganisationUnit unitL = createOrganisationUnit( 'L', unitF );
        OrganisationUnit unitM = createOrganisationUnit( 'M', unitF );
        OrganisationUnit unitN = createOrganisationUnit( 'N', unitG );
        OrganisationUnit unitO = createOrganisationUnit( 'O', unitG );

        unitA.getChildren().add( unitB );
        unitA.getChildren().add( unitC );
        unitB.getChildren().add( unitD );
        unitB.getChildren().add( unitE );
        unitC.getChildren().add( unitF );
        unitC.getChildren().add( unitG );
        unitD.getChildren().add( unitH );
        unitD.getChildren().add( unitI );
        unitE.getChildren().add( unitJ );
        unitE.getChildren().add( unitK );
        unitF.getChildren().add( unitL );
        unitF.getChildren().add( unitM );
        unitG.getChildren().add( unitN );
        unitG.getChildren().add( unitO );

        organisationUnitService.addOrganisationUnit( unitA );
        organisationUnitService.addOrganisationUnit( unitB );
        organisationUnitService.addOrganisationUnit( unitC );
        organisationUnitService.addOrganisationUnit( unitD );
        organisationUnitService.addOrganisationUnit( unitE );
        organisationUnitService.addOrganisationUnit( unitF );
        organisationUnitService.addOrganisationUnit( unitG );
        organisationUnitService.addOrganisationUnit( unitH );
        organisationUnitService.addOrganisationUnit( unitI );
        organisationUnitService.addOrganisationUnit( unitJ );
        organisationUnitService.addOrganisationUnit( unitK );
        organisationUnitService.addOrganisationUnit( unitL );
        organisationUnitService.addOrganisationUnit( unitM );
        organisationUnitService.addOrganisationUnit( unitN );
        organisationUnitService.addOrganisationUnit( unitO );

        List<OrganisationUnit> unitsA = new ArrayList<OrganisationUnit>( Arrays.asList( unitB, unitC ) );
        List<OrganisationUnit> unitsB = new ArrayList<OrganisationUnit>( Arrays.asList( unitD, unitE ) );

        OrganisationUnit nill = null;

        assertTrue( equals( organisationUnitService.getOrganisationUnitsAtLevel( 3, unitsA ), unitD, unitE, unitF, unitG ) );
        assertTrue( equals( organisationUnitService.getOrganisationUnitsAtLevel( 4, unitsA ), unitH, unitI, unitJ, unitK, unitL, unitM, unitN, unitO ) );
        assertTrue( equals( organisationUnitService.getOrganisationUnitsAtLevel( 4, unitsB ), unitH, unitI, unitJ, unitK ) );
        assertTrue( equals( organisationUnitService.getOrganisationUnitsAtLevel( 2, nill ), unitB, unitC ) );

        assertEquals( 2, unitB.getLevel() );
        assertEquals( 3, unitD.getLevel() );
        assertEquals( 3, unitE.getLevel() );
        assertEquals( 4, unitH.getLevel() );
        assertEquals( 4, unitI.getLevel() );
        assertEquals( 4, unitJ.getLevel() );
        assertEquals( 4, unitK.getLevel() );
    }

    @Test
    public void testGetOrganisationUnitsByNameAndGroups()
    {
        OrganisationUnit unitA = createOrganisationUnit( 'A' );
        OrganisationUnit unitB = createOrganisationUnit( 'B', unitA );
        unitA.getChildren().add( unitB );
        OrganisationUnit unitC = createOrganisationUnit( 'C' );
        organisationUnitService.addOrganisationUnit( unitA );
        organisationUnitService.addOrganisationUnit( unitB );
        organisationUnitService.addOrganisationUnit( unitC );

        OrganisationUnitGroup groupA = createOrganisationUnitGroup( 'A' );
        OrganisationUnitGroup groupB = createOrganisationUnitGroup( 'B' );
        OrganisationUnitGroup groupC = createOrganisationUnitGroup( 'C' );

        groupA.getMembers().add( unitA );
        groupA.getMembers().add( unitB );
        groupA.getMembers().add( unitC );
        groupB.getMembers().add( unitA );
        groupB.getMembers().add( unitB );
        groupC.getMembers().add( unitA );

        organisationUnitGroupService.addOrganisationUnitGroup( groupA );
        organisationUnitGroupService.addOrganisationUnitGroup( groupB );
        organisationUnitGroupService.addOrganisationUnitGroup( groupC );

        List<OrganisationUnitGroup> groups = Arrays.asList( groupA );
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnitsByNameAndGroups( null, groups, false );
        assertEquals( 3, units.size() );
        units = organisationUnitService.getOrganisationUnitsByNameAndGroups( unitA.getName().toLowerCase(), groups, false );
        assertEquals( 1, units.size() );
        assertTrue( units.contains( unitA ) );
        units = organisationUnitService.getOrganisationUnitsByNameAndGroups( unitA.getName(), null, false );
        assertEquals( 1, units.size() );
        assertEquals( unitA, units.iterator().next() );
        units = organisationUnitService.getOrganisationUnitsByNameAndGroups( null, groups, unitA, false );
        assertEquals( 2, units.size() );
        assertFalse( units.contains( unitC ) );

        groups = Arrays.asList( groupA, groupB );
        units = organisationUnitService.getOrganisationUnitsByNameAndGroups( null, groups, false );
        assertEquals( 2, units.size() );
        units = organisationUnitService.getOrganisationUnitsByNameAndGroups( unitB.getName().toUpperCase(), groups, false );
        assertEquals( 1, units.size() );
        assertEquals( unitB, units.iterator().next() );
        units = organisationUnitService.getOrganisationUnitsByNameAndGroups( unitB.getName(), null, false );
        assertEquals( 1, units.size() );
        assertEquals( unitB, units.iterator().next() );
        units = organisationUnitService.getOrganisationUnitsByNameAndGroups( null, groups, unitA, false );
        assertEquals( 2, units.size() );

        groups = Arrays.asList( groupA, groupB, groupC );
        units = organisationUnitService.getOrganisationUnitsByNameAndGroups( null, groups, false );
        assertEquals( 1, units.size() );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitGroup
    // -------------------------------------------------------------------------

    @Test
    public void testAddAndDelOrganisationUnitGroup()
        throws Exception
    {
        OrganisationUnitGroup organisationUnitGroup1 = new OrganisationUnitGroup( "OUGname" );

        int id1 = organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup1 );

        // assert getOrganisationUnitGroup
        assertNotNull( organisationUnitGroupService.getOrganisationUnitGroup( id1 ) );

        assertEquals( organisationUnitGroupService.getOrganisationUnitGroup( id1 ).getName(), "OUGname" );

        organisationUnitGroupService.deleteOrganisationUnitGroup( organisationUnitGroupService
            .getOrganisationUnitGroup( id1 ) );

        // assert delOrganisationUnitGroup
        assertNull( organisationUnitGroupService.getOrganisationUnitGroup( id1 ) );
    }

    @Test
    @Ignore
    public void testUpdateOrganisationUnitGroup()
        throws Exception
    {
        OrganisationUnitGroup organisationUnitGroup = new OrganisationUnitGroup( "OUGname" );

        OrganisationUnit organisationUnit1 = new OrganisationUnit( "OU1name", null, "OU1sname", "OU1code", null, null,
            true, null );
        OrganisationUnit organisationUnit2 = new OrganisationUnit( "OU2name", null, "OU2sname", "OU2code", null, null,
            true, null );

        organisationUnitGroup.getMembers().add( organisationUnit1 );
        organisationUnitGroup.getMembers().add( organisationUnit2 );

        organisationUnitService.addOrganisationUnit( organisationUnit1 );
        organisationUnitService.addOrganisationUnit( organisationUnit2 );

        int ougid = organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup );

        assertTrue( organisationUnitGroupService.getOrganisationUnitGroup( ougid ).getMembers().size() == 2 );

        organisationUnitGroup.getMembers().remove( organisationUnit1 );

        organisationUnitGroupService.updateOrganisationUnitGroup( organisationUnitGroup );

        assertTrue( organisationUnitGroupService.getOrganisationUnitGroup( ougid ).getMembers().size() == 1 );
    }

    @Test
    public void testGetAllOrganisationUnitGroups()
        throws Exception
    {
        OrganisationUnitGroup group1 = new OrganisationUnitGroup( "organisationUnitGroupName1" );
        int gid1 = organisationUnitGroupService.addOrganisationUnitGroup( group1 );

        OrganisationUnitGroup group2 = new OrganisationUnitGroup( "organisationUnitGroupName2" );
        int gid2 = organisationUnitGroupService.addOrganisationUnitGroup( group2 );

        OrganisationUnitGroup group3 = new OrganisationUnitGroup( "organisationUnitGroupName3" );
        int gid3 = organisationUnitGroupService.addOrganisationUnitGroup( group3 );

        OrganisationUnitGroup group4 = new OrganisationUnitGroup( "organisationUnitGroupName4" );
        int gid4 = organisationUnitGroupService.addOrganisationUnitGroup( group4 );

        Iterator<OrganisationUnitGroup> iterator = organisationUnitGroupService.getAllOrganisationUnitGroups().iterator();

        OrganisationUnitGroup organisationUnitGroup1 = iterator.next();
        assertTrue( organisationUnitGroup1.getId() == gid1 );

        OrganisationUnitGroup organisationUnitGroup2 = iterator.next();
        assertTrue( organisationUnitGroup2.getId() == gid2 );

        OrganisationUnitGroup organisationUnitGroup3 = iterator.next();
        assertTrue( organisationUnitGroup3.getId() == gid3 );

        OrganisationUnitGroup organisationUnitGroup4 = iterator.next();
        assertTrue( organisationUnitGroup4.getId() == gid4 );
    }

    @Test
    public void testGetOrganisationUnitGroupByName()
        throws Exception
    {
        String oUG1Name = "OUG1Name";
        String oUG2Name = "OUG2Name";

        OrganisationUnitGroup organisationUnitGroup1 = new OrganisationUnitGroup( oUG1Name );
        OrganisationUnitGroup organisationUnitGroup2 = new OrganisationUnitGroup( oUG2Name );

        organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup1 );
        organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup2 );

        OrganisationUnitGroup group1 = organisationUnitGroupService.getOrganisationUnitGroupByName( oUG1Name ).get( 0 );
        assertEquals( group1.getName(), oUG1Name );

        OrganisationUnitGroup group2 = organisationUnitGroupService.getOrganisationUnitGroupByName( oUG2Name ).get( 0 );
        assertEquals( group2.getName(), oUG2Name );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitHierarchy
    // -------------------------------------------------------------------------

    @Test
    public void testAddGetOrganisationUnitHierarchy()
        throws Exception
    {
        // creates a tree
        OrganisationUnit unit1 = new OrganisationUnit( "orgUnitName1", "shortName1", "organisationUnitCode1",
            new Date(), new Date(), true, "comment" );
        OrganisationUnit unit2 = new OrganisationUnit( "orgUnitName2", unit1, "shortName2", "organisationUnitCode2",
            new Date(), new Date(), true, "comment" );
        OrganisationUnit unit3 = new OrganisationUnit( "orgUnitName3", unit1, "shortName3", "organisationUnitCode3",
            new Date(), new Date(), true, "comment" );
        OrganisationUnit unit4 = new OrganisationUnit( "orgUnitName4", unit2, "shortName4", "organisationUnitCode4",
            new Date(), new Date(), true, "comment" );
        OrganisationUnit unit5 = new OrganisationUnit( "orgUnitName5", unit2, "shortName5", "organisationUnitCode5",
            new Date(), new Date(), true, "comment" );
        OrganisationUnit unit6 = new OrganisationUnit( "orgUnitName6", unit5, "shortName6", "organisationUnitCode6",
            new Date(), new Date(), true, "comment" );

        organisationUnitService.addOrganisationUnit( unit1 );
        int id2 = organisationUnitService.addOrganisationUnit( unit2 );
        organisationUnitService.addOrganisationUnit( unit3 );
        int id4 = organisationUnitService.addOrganisationUnit( unit4 );
        int id5 = organisationUnitService.addOrganisationUnit( unit5 );
        int id6 = organisationUnitService.addOrganisationUnit( unit6 );

        OrganisationUnitHierarchy hierarchy = organisationUnitService.getOrganisationUnitHierarchy();

        // retrieves children from hierarchyVersion ver_id and parentId id2
        Collection<Integer> children1 = hierarchy.getChildren( unit2.getId() );

        // assert 4, 5, 6 are children of 2
        assertEquals( 4, children1.size() );
        assertTrue( children1.contains( id2 ) );
        assertTrue( children1.contains( id4 ) );
        assertTrue( children1.contains( id5 ) );
        assertTrue( children1.contains( id6 ) );

        // retrieves children from hierarchyVersion ver_id and parentId id1
        Collection<Integer> children2 = hierarchy.getChildren( unit1.getId() );

        // assert the number of children
        assertTrue( children2.size() == 6 );

        // retrieves children from hierarchyVersion ver_id and parentId id5
        Collection<Integer> children3 = hierarchy.getChildren( unit5.getId() );

        // assert 6 is children of 5
        assertEquals( 2, children3.size() );
        assertTrue( children3.contains( id5 ) );
        assertTrue( children3.contains( id6 ) );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitGroupSets
    // -------------------------------------------------------------------------

    @Test
    public void testOrganisationUnitGroupSetsBasic()
        throws Exception
    {
        OrganisationUnitGroup organisationUnitGroup1 = new OrganisationUnitGroup();
        organisationUnitGroup1.setName( "oug1" );
        OrganisationUnitGroup organisationUnitGroup2 = new OrganisationUnitGroup();
        organisationUnitGroup2.setName( "oug2" );
        OrganisationUnitGroup organisationUnitGroup3 = new OrganisationUnitGroup();
        organisationUnitGroup3.setName( "oug3" );
        OrganisationUnitGroup organisationUnitGroup4 = new OrganisationUnitGroup();
        organisationUnitGroup4.setName( "oug4" );

        organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup1 );
        organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup2 );
        organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup3 );
        organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup4 );

        OrganisationUnitGroupSet organisationUnitGroupSet1 = new OrganisationUnitGroupSet();
        organisationUnitGroupSet1.setName( "ougs1" );
        organisationUnitGroupSet1.setCompulsory( true );
        organisationUnitGroupSet1.getOrganisationUnitGroups().add( organisationUnitGroup1 );
        organisationUnitGroupSet1.getOrganisationUnitGroups().add( organisationUnitGroup2 );
        organisationUnitGroupSet1.getOrganisationUnitGroups().add( organisationUnitGroup3 );

        int id1 = organisationUnitGroupService.addOrganisationUnitGroupSet( organisationUnitGroupSet1 );

        // assert add
        assertNotNull( organisationUnitGroupService.getOrganisationUnitGroupSet( id1 ) );

        assertEquals( organisationUnitGroupService.getOrganisationUnitGroupSet( id1 ).getName(), "ougs1" );

        assertTrue( organisationUnitGroupService.getOrganisationUnitGroupSet( id1 ).getOrganisationUnitGroups().size() == 3 );

        organisationUnitGroupSet1.getOrganisationUnitGroups().remove( organisationUnitGroup3 );

        organisationUnitGroupService.updateOrganisationUnitGroupSet( organisationUnitGroupSet1 );

        // assert update
        assertTrue( organisationUnitGroupService.getOrganisationUnitGroupSet( id1 ).getOrganisationUnitGroups().size() == 2 );

        OrganisationUnitGroupSet organisationUnitGroupSet2 = new OrganisationUnitGroupSet();
        organisationUnitGroupSet2.setName( "ougs2" );
        organisationUnitGroupSet2.setCompulsory( true );
        organisationUnitGroupSet2.getOrganisationUnitGroups().add( organisationUnitGroup4 );

        int id2 = organisationUnitGroupService.addOrganisationUnitGroupSet( organisationUnitGroupSet2 );

        // assert getAllOrderedName
        assertTrue( organisationUnitGroupService.getAllOrganisationUnitGroupSets().size() == 2 );

        organisationUnitGroupService.deleteOrganisationUnitGroupSet( organisationUnitGroupSet1 );
        organisationUnitGroupService.deleteOrganisationUnitGroupSet( organisationUnitGroupSet2 );

        assertNull( organisationUnitGroupService.getOrganisationUnitGroupSet( id1 ) );
        assertNull( organisationUnitGroupService.getOrganisationUnitGroupSet( id2 ) );
    }

    @Test
    public void testGetOrganisationUnitGroupSetsByName()
        throws Exception
    {
        OrganisationUnitGroup organisationUnitGroup1 = new OrganisationUnitGroup();
        organisationUnitGroup1.setName( "oug1" );
        OrganisationUnitGroup organisationUnitGroup2 = new OrganisationUnitGroup();
        organisationUnitGroup2.setName( "oug2" );
        OrganisationUnitGroup organisationUnitGroup3 = new OrganisationUnitGroup();
        organisationUnitGroup3.setName( "oug3" );
        OrganisationUnitGroup organisationUnitGroup4 = new OrganisationUnitGroup();
        organisationUnitGroup4.setName( "oug4" );

        organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup1 );
        organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup2 );
        organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup3 );
        organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup4 );

        String ougs1 = "ougs1";
        String ougs2 = "ougs2";

        OrganisationUnitGroupSet organisationUnitGroupSet1 = new OrganisationUnitGroupSet();
        organisationUnitGroupSet1.setName( ougs1 );
        organisationUnitGroupSet1.setCompulsory( true );
        organisationUnitGroupSet1.getOrganisationUnitGroups().add( organisationUnitGroup1 );
        organisationUnitGroupSet1.getOrganisationUnitGroups().add( organisationUnitGroup2 );
        organisationUnitGroupSet1.getOrganisationUnitGroups().add( organisationUnitGroup3 );

        OrganisationUnitGroupSet organisationUnitGroupSet2 = new OrganisationUnitGroupSet();
        organisationUnitGroupSet2.setName( ougs2 );
        organisationUnitGroupSet2.setCompulsory( false );
        organisationUnitGroupSet2.getOrganisationUnitGroups().add( organisationUnitGroup4 );

        organisationUnitGroupService.addOrganisationUnitGroupSet( organisationUnitGroupSet1 );
        organisationUnitGroupService.addOrganisationUnitGroupSet( organisationUnitGroupSet2 );

        OrganisationUnitGroupSet set1 = organisationUnitGroupService.getOrganisationUnitGroupSetByName( ougs1 ).get( 0 );
        OrganisationUnitGroupSet set2 = organisationUnitGroupService.getOrganisationUnitGroupSetByName( ougs2 ).get( 0 );

        assertEquals( set1.getName(), ougs1 );
        assertEquals( set2.getName(), ougs2 );

        Collection<OrganisationUnitGroupSet> compulsorySets = organisationUnitGroupService
            .getCompulsoryOrganisationUnitGroupSets();
        assertEquals( compulsorySets.size(), 1 );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitLevel
    // -------------------------------------------------------------------------

    @Test
    public void testAddGetOrganisationUnitLevel()
    {
        OrganisationUnitLevel levelA = new OrganisationUnitLevel( 1, "National" );
        OrganisationUnitLevel levelB = new OrganisationUnitLevel( 2, "District" );

        int idA = organisationUnitService.addOrganisationUnitLevel( levelA );
        int idB = organisationUnitService.addOrganisationUnitLevel( levelB );

        assertEquals( levelA, organisationUnitService.getOrganisationUnitLevel( idA ) );
        assertEquals( levelB, organisationUnitService.getOrganisationUnitLevel( idB ) );
    }

    @Test
    public void testGetOrganisationUnitLevels()
    {
        OrganisationUnitLevel level1 = new OrganisationUnitLevel( 1, "National" );
        OrganisationUnitLevel level2 = new OrganisationUnitLevel( 2, "District" );
        OrganisationUnitLevel level4 = new OrganisationUnitLevel( 4, "PHU" );

        organisationUnitService.addOrganisationUnitLevel( level1 );
        organisationUnitService.addOrganisationUnitLevel( level2 );
        organisationUnitService.addOrganisationUnitLevel( level4 );

        OrganisationUnit unitA = createOrganisationUnit( 'A' );
        OrganisationUnit unitB = createOrganisationUnit( 'B', unitA );
        OrganisationUnit unitC = createOrganisationUnit( 'C', unitB );
        OrganisationUnit unitD = createOrganisationUnit( 'D', unitC );

        unitA.getChildren().add( unitB );
        unitB.getChildren().add( unitC );
        unitC.getChildren().add( unitD );

        organisationUnitService.addOrganisationUnit( unitA );
        organisationUnitService.addOrganisationUnit( unitB );
        organisationUnitService.addOrganisationUnit( unitC );
        organisationUnitService.addOrganisationUnit( unitD );

        Iterator<OrganisationUnitLevel> actual = organisationUnitService.getOrganisationUnitLevels().iterator();

        assertNotNull( actual );
        assertEquals( level1, actual.next() );
        assertEquals( level2, actual.next() );

        level4 = actual.next();

        assertEquals( 4, level4.getLevel() );
        assertEquals( "PHU", level4.getName() );
    }

    @Test
    public void testRemoveOrganisationUnitLevel()
    {
        OrganisationUnitLevel levelA = new OrganisationUnitLevel( 1, "National" );
        OrganisationUnitLevel levelB = new OrganisationUnitLevel( 2, "District" );

        int idA = organisationUnitService.addOrganisationUnitLevel( levelA );
        int idB = organisationUnitService.addOrganisationUnitLevel( levelB );

        assertNotNull( organisationUnitService.getOrganisationUnitLevel( idA ) );
        assertNotNull( organisationUnitService.getOrganisationUnitLevel( idB ) );

        organisationUnitService.deleteOrganisationUnitLevel( levelA );

        assertNull( organisationUnitService.getOrganisationUnitLevel( idA ) );
        assertNotNull( organisationUnitService.getOrganisationUnitLevel( idB ) );

        organisationUnitService.deleteOrganisationUnitLevel( levelB );

        assertNull( organisationUnitService.getOrganisationUnitLevel( idA ) );
        assertNull( organisationUnitService.getOrganisationUnitLevel( idB ) );
    }

    @Test
    public void getMaxLevels()
    {
        assertEquals( 0, organisationUnitService.getMaxOfOrganisationUnitLevels() );

        OrganisationUnitLevel levelA = new OrganisationUnitLevel( 1, "National" );
        OrganisationUnitLevel levelB = new OrganisationUnitLevel( 2, "District" );

        organisationUnitService.addOrganisationUnitLevel( levelA );
        organisationUnitService.addOrganisationUnitLevel( levelB );

        assertEquals( 2, organisationUnitService.getMaxOfOrganisationUnitLevels() );
    }
}
