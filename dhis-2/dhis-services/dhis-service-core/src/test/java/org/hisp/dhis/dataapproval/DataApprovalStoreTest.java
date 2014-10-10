package org.hisp.dhis.dataapproval;

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
import static org.junit.Assert.fail;

import java.util.Date;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jim Grace
 * @version $Id$
 */
public class DataApprovalStoreTest
    extends DhisSpringTest
{
    @Autowired
    private DataApprovalStore dataApprovalStore;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    // -------------------------------------------------------------------------
    // Supporting data
    // -------------------------------------------------------------------------

    private DataSet dataSetA;

    private DataSet dataSetB;

    private Period periodA;

    private Period periodB;

    private OrganisationUnit sourceA;

    private OrganisationUnit sourceB;

    private OrganisationUnit sourceC;

    private OrganisationUnit sourceD;

    private User userA;

    private User userB;

    private DataElementCategoryOptionCombo attributeOptionCombo;
    
    // -------------------------------------------------------------------------
    // Set up/tear down
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest() throws Exception
    {
        // ---------------------------------------------------------------------
        // Add supporting data
        // ---------------------------------------------------------------------

        PeriodType periodType = PeriodType.getPeriodTypeByName( "Monthly" );

        dataSetA = createDataSet( 'A', periodType );
        dataSetB = createDataSet( 'B', periodType );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );

        periodA = createPeriod( getDay( 5 ), getDay( 6 ) );
        periodB = createPeriod( getDay( 6 ), getDay( 7 ) );

        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );

        sourceA = createOrganisationUnit( 'A' );
        sourceB = createOrganisationUnit( 'B', sourceA );
        sourceC = createOrganisationUnit( 'C', sourceB );
        sourceD = createOrganisationUnit( 'D', sourceC );

        organisationUnitService.addOrganisationUnit( sourceA );
        organisationUnitService.addOrganisationUnit( sourceB );
        organisationUnitService.addOrganisationUnit( sourceC );
        organisationUnitService.addOrganisationUnit( sourceD );

        userA = createUser( 'A' );
        userB = createUser( 'B' );

        userService.addUser( userA );
        userService.addUser( userB );

        attributeOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
    }

    // -------------------------------------------------------------------------
    // Basic DataApproval
    // -------------------------------------------------------------------------

    @Test
    public void testAddAndGetDataApproval() throws Exception
    {
        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, sourceA, attributeOptionCombo, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, sourceB, attributeOptionCombo, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodB, sourceA, attributeOptionCombo, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetB, periodA, sourceA, attributeOptionCombo, date, userA );
        DataApproval dataApprovalE;

        dataApprovalStore.addDataApproval( dataApprovalA );
        dataApprovalStore.addDataApproval( dataApprovalB );
        dataApprovalStore.addDataApproval( dataApprovalC );
        dataApprovalStore.addDataApproval( dataApprovalD );

        dataApprovalA = dataApprovalStore.getDataApproval( dataSetA, periodA, sourceA, attributeOptionCombo );
        assertNotNull( dataApprovalA );
        assertEquals( dataSetA.getId(), dataApprovalA.getDataSet().getId() );
        assertEquals( periodA, dataApprovalA.getPeriod() );
        assertEquals( sourceA.getId(), dataApprovalA.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalA.getCreated() );
        assertEquals( userA.getId(), dataApprovalA.getCreator().getId() );

        dataApprovalB = dataApprovalStore.getDataApproval( dataSetA, periodA, sourceB, attributeOptionCombo );
        assertNotNull( dataApprovalB );
        assertEquals( dataSetA.getId(), dataApprovalB.getDataSet().getId() );
        assertEquals( periodA, dataApprovalB.getPeriod() );
        assertEquals( sourceB.getId(), dataApprovalB.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalB.getCreated() );
        assertEquals( userA.getId(), dataApprovalB.getCreator().getId() );

        dataApprovalC = dataApprovalStore.getDataApproval( dataSetA, periodB, sourceA, attributeOptionCombo );
        assertNotNull( dataApprovalC );
        assertEquals( dataSetA.getId(), dataApprovalC.getDataSet().getId() );
        assertEquals( periodB, dataApprovalC.getPeriod() );
        assertEquals( sourceA.getId(), dataApprovalC.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalC.getCreated() );
        assertEquals( userA.getId(), dataApprovalC.getCreator().getId() );

        dataApprovalD = dataApprovalStore.getDataApproval( dataSetB, periodA, sourceA, attributeOptionCombo );
        assertNotNull( dataApprovalD );
        assertEquals( dataSetB.getId(), dataApprovalD.getDataSet().getId() );
        assertEquals( periodA, dataApprovalD.getPeriod() );
        assertEquals( sourceA.getId(), dataApprovalD.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalD.getCreated() );
        assertEquals( userA.getId(), dataApprovalD.getCreator().getId() );

        dataApprovalE = dataApprovalStore.getDataApproval( dataSetB, periodB, sourceB, attributeOptionCombo );
        assertNull( dataApprovalE );
    }

    @Test
    public void testAddDuplicateDataApproval() throws Exception
    {
        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, sourceA, attributeOptionCombo, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, sourceA, attributeOptionCombo, date, userA );

        dataApprovalStore.addDataApproval( dataApprovalA );

        try
        {
            dataApprovalStore.addDataApproval( dataApprovalB );
            fail("Should give unique constraint violation");
        }
        catch ( Exception e )
        {
            // Expected
        }
    }

    @Test
    public void testDeleteDataApproval() throws Exception
    {
        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, sourceA, attributeOptionCombo, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetB, periodB, sourceB, attributeOptionCombo, date, userB );

        dataApprovalStore.addDataApproval( dataApprovalA );
        dataApprovalStore.addDataApproval( dataApprovalB );

        dataApprovalA = dataApprovalStore.getDataApproval( dataSetA, periodA, sourceA, attributeOptionCombo );
        assertNotNull( dataApprovalA );

        dataApprovalB = dataApprovalStore.getDataApproval( dataSetB, periodB, sourceB, attributeOptionCombo );
        assertNotNull( dataApprovalB );

        dataApprovalStore.deleteDataApproval( dataApprovalA );

        dataApprovalA = dataApprovalStore.getDataApproval( dataSetA, periodA, sourceA, attributeOptionCombo );
        assertNull( dataApprovalA );

        dataApprovalB = dataApprovalStore.getDataApproval( dataSetB, periodB, sourceB, attributeOptionCombo );
        assertNotNull( dataApprovalB );

        dataApprovalStore.deleteDataApproval( dataApprovalB );

        dataApprovalA = dataApprovalStore.getDataApproval( dataSetA, periodA, sourceA, attributeOptionCombo );
        assertNull( dataApprovalA );

        dataApprovalB = dataApprovalStore.getDataApproval( dataSetB, periodB, sourceB, attributeOptionCombo );
        assertNull( dataApprovalB );
    }
}
