/*
 * Copyright (c) 2004-2013, University of Oslo
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

package org.hisp.dhis.patientreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ PatientAggregateReportServiceTest.java Nov 28, 2013 11:21:05 AM $
 */
public class PatientAggregateReportServiceTest
    extends DhisSpringTest
{
    @Autowired
    private PatientAggregateReportService patientAggregateReportService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private UserService userService;

    private User user;

    private PatientAggregateReport aggregateReportA;

    private PatientAggregateReport aggregateReportB;

    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        Set<OrganisationUnit> orgUnits = new HashSet<OrganisationUnit>();
        orgUnits.add( organisationUnit );

        user = new User();
        user.setSurname( "A" );
        user.setFirstName( "B" );
        user.setPhoneNumber( "111-222-333" );
        user.updateOrganisationUnits( orgUnits );

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUser( user );
        userCredentials.setUsername( "A" );
        userCredentials.setPassword( "A" );
        userCredentials.setUser( user );
        user.setUserCredentials( userCredentials );

        UserAuthorityGroup group = new UserAuthorityGroup();
        group.setName( "A" );
        group.setDescription( "A" );

        Collection<String> authorityMember = new ArrayList<String>();
        authorityMember.add( "ALL" );
        group.getAuthorities().addAll( authorityMember );
        userService.addUserAuthorityGroup( group );

        userCredentials.getUserAuthorityGroups().add( group );
        userService.addUser( user );
        userService.addUserCredentials( userCredentials );

        aggregateReportA = new PatientAggregateReport( "A" );
        aggregateReportA.setUid( "UID-A" );
        aggregateReportA.setUser( user );
        aggregateReportB = new PatientAggregateReport( "B" );
        aggregateReportB.setUid( "UID-B" );
        aggregateReportB.setUser( user );
    }

    @Test
    public void testAddPatientAggregateReport()
    {
        int idA = patientAggregateReportService.addPatientAggregateReport( aggregateReportA );
        int idB = patientAggregateReportService.addPatientAggregateReport( aggregateReportB );

        assertNotNull( patientAggregateReportService.getPatientAggregateReport( idA ) );
        assertNotNull( patientAggregateReportService.getPatientAggregateReport( idB ) );
    }

    @Test
    public void testDeletePatientAggregateReport()
    {
        int idA = patientAggregateReportService.addPatientAggregateReport( aggregateReportA );
        int idB = patientAggregateReportService.addPatientAggregateReport( aggregateReportB );

        assertNotNull( patientAggregateReportService.getPatientAggregateReport( idA ) );
        assertNotNull( patientAggregateReportService.getPatientAggregateReport( idB ) );

        patientAggregateReportService.deletePatientAggregateReport( aggregateReportA );

        assertNull( patientAggregateReportService.getPatientAggregateReport( idA ) );
        assertNotNull( patientAggregateReportService.getPatientAggregateReport( idB ) );

        patientAggregateReportService.deletePatientAggregateReport( aggregateReportB );

        assertNull( patientAggregateReportService.getPatientAggregateReport( idA ) );
        assertNull( patientAggregateReportService.getPatientAggregateReport( idB ) );
    }

    @Test
    public void testUpdatePatientAggregateReport()
    {
        int idA = patientAggregateReportService.addPatientAggregateReport( aggregateReportA );

        assertNotNull( patientAggregateReportService.getPatientAggregateReport( idA ) );

        aggregateReportA.setName( "B" );
        patientAggregateReportService.updatePatientAggregateReport( aggregateReportA );

        assertEquals( "B", patientAggregateReportService.getPatientAggregateReport( idA ).getName() );
    }

    @Test
    public void testGetPatientAggregateReportById()
    {
        int idA = patientAggregateReportService.addPatientAggregateReport( aggregateReportA );
        int idB = patientAggregateReportService.addPatientAggregateReport( aggregateReportB );

        assertEquals( aggregateReportA, patientAggregateReportService.getPatientAggregateReport( idA ) );
        assertEquals( aggregateReportB, patientAggregateReportService.getPatientAggregateReport( idB ) );
    }

    @Test
    public void testGetPatientAggregateReportByUid()
    {
        patientAggregateReportService.addPatientAggregateReport( aggregateReportA );
        patientAggregateReportService.addPatientAggregateReport( aggregateReportB );

        assertEquals( aggregateReportA, patientAggregateReportService.getPatientAggregateReportByUid( "UID-A" ) );
        assertEquals( aggregateReportB, patientAggregateReportService.getPatientAggregateReportByUid( "UID-B" ) );
    }

    @Test
    public void testGetPatientAggregateReportByName()
    {
        int idA = patientAggregateReportService.addPatientAggregateReport( aggregateReportA );

        assertNotNull( patientAggregateReportService.getPatientAggregateReport( idA ) );
        assertEquals( aggregateReportA.getName(), patientAggregateReportService.getPatientAggregateReport( "A" )
            .getName() );
    }

    @Test
    public void testGetAllPatientAggregateReports()
    {
        patientAggregateReportService.addPatientAggregateReport( aggregateReportA );
        patientAggregateReportService.addPatientAggregateReport( aggregateReportB );

        assertTrue( equals( patientAggregateReportService.getAllPatientAggregateReports(), aggregateReportA,
            aggregateReportB ) );
    }

    @Test
    public void testGetPatientAggregateReports()
    {
        patientAggregateReportService.addPatientAggregateReport( aggregateReportA );
        patientAggregateReportService.addPatientAggregateReport( aggregateReportB );
        
        assertTrue( equals( patientAggregateReportService.getPatientAggregateReports( user, "a", null, null ),
            aggregateReportA ) );
    }

    @Test
    public void testCountPatientAggregateReportList()
    {
        patientAggregateReportService.addPatientAggregateReport( aggregateReportA );
        patientAggregateReportService.addPatientAggregateReport( aggregateReportB );

        assertEquals( 1, patientAggregateReportService.countPatientAggregateReportList( user, "a" ) );
    }
}