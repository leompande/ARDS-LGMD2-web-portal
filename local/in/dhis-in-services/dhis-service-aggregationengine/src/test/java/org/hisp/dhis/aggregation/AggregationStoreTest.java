package org.hisp.dhis.aggregation;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id: AggregationStoreTest.java 5942 2008-10-16 15:44:57Z larshelg $
 */
public class AggregationStoreTest
    extends DhisTest
{
    private AggregationStore aggregationStore;

    private DataElementCategoryOptionCombo optionCombo;
    
    private StatementManager statementManager;

    @Override
    public void setUpTest()
        throws Exception
    {
        aggregationStore = (AggregationStore) getBean( AggregationStore.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        dataValueService = (DataValueService) getBean( DataValueService.ID );

        optionCombo = new DataElementCategoryOptionCombo();
        
        categoryService.addDataElementCategoryOptionCombo( optionCombo );
        
        statementManager = (StatementManager) getBean( "statementManager" );
    }
    
    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------
    
    private Collection<Integer> getPeriodIds( Collection<Period> periods )
    {
        Collection<Integer> periodIds = new ArrayList<Integer>();
        
        for ( Period period : periods )
        {
            periodIds.add( period.getId() );
        }
        
        return periodIds;
    }

    // -------------------------------------------------------------------------
    // AggregationStore test
    // -------------------------------------------------------------------------

    @Test
    public void testRetrieveDataValuesDataElementSourcesDates()
        throws Exception
    {
        DataElement dataElementA = createDataElement( 'A' );
        
        Period periodA = createPeriod( getDay( 5 ), getDay( 6 ) );
        Period periodB = createPeriod( getDay( 6 ), getDay( 7 ) );
        Period periodC = createPeriod( getDay( 7 ), getDay( 8 ) );
        Period periodD = createPeriod( getDay( 8 ), getDay( 9 ) );
        Period periodE = createPeriod( getDay( 9 ), getDay( 10 ) );
        Period periodF = createPeriod( getDay( 5 ), getDay( 7 ) );
        Period periodG = createPeriod( getDay( 8 ), getDay( 10 ) );
        Period periodH = createPeriod( getDay( 5 ), getDay( 10 ) );

        OrganisationUnit sourceA = new OrganisationUnit( "nameA", null, "shortNameA", "codeA", null, null, false, null );
        OrganisationUnit sourceB = new OrganisationUnit( "nameB", null, "shortNameB", "codeB", null, null, false, null );
        OrganisationUnit sourceC = new OrganisationUnit( "nameC", null, "shortNameC", "codeC", null, null, false, null );

        DataValue dataValueA = new DataValue( dataElementA, periodA, sourceA, optionCombo );
        dataValueA.setValue( "1" );
        DataValue dataValueB = new DataValue( dataElementA, periodB, sourceB, optionCombo );
        dataValueB.setValue( "2" );
        DataValue dataValueC = new DataValue( dataElementA, periodC, sourceC, optionCombo );
        dataValueC.setValue( "3" );
        DataValue dataValueD = new DataValue( dataElementA, periodD, sourceA, optionCombo );
        dataValueD.setValue( "4" );
        DataValue dataValueE = new DataValue( dataElementA, periodE, sourceB, optionCombo );
        dataValueE.setValue( "5" );
        DataValue dataValueF = new DataValue( dataElementA, periodF, sourceC, optionCombo );
        dataValueF.setValue( "6" );
        DataValue dataValueG = new DataValue( dataElementA, periodG, sourceA, optionCombo );
        dataValueG.setValue( "7" );
        DataValue dataValueH = new DataValue( dataElementA, periodH, sourceB, optionCombo );
        dataValueH.setValue( "8" );
        
        dataElementService.addDataElement( dataElementA );

        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        periodService.addPeriod( periodC );
        periodService.addPeriod( periodD );
        periodService.addPeriod( periodE );
        periodService.addPeriod( periodF );
        periodService.addPeriod( periodG );
        periodService.addPeriod( periodH );

        organisationUnitService.addOrganisationUnit( sourceA );
        organisationUnitService.addOrganisationUnit( sourceB );
        organisationUnitService.addOrganisationUnit( sourceC );

        dataValueService.addDataValue( dataValueA );
        dataValueService.addDataValue( dataValueB );
        dataValueService.addDataValue( dataValueC );
        dataValueService.addDataValue( dataValueD );
        dataValueService.addDataValue( dataValueE );
        dataValueService.addDataValue( dataValueF );
        dataValueService.addDataValue( dataValueG );
        dataValueService.addDataValue( dataValueH );

        Date startDate = getDay( 6 );
        Date endDate = getDay( 9 );

        Collection<Integer> sources = new HashSet<Integer>();
        sources.add( new Integer( sourceA.getId() ) );
        sources.add( new Integer( sourceB.getId() ) );
        sources.add( new Integer( sourceC.getId() ) );

        Collection<Integer> periods = getPeriodIds( periodService.getIntersectingPeriods( startDate, endDate ) );
        
        statementManager.initialise();
        
        Collection<DataValue> dataValues3 = aggregationStore.getDataValues( sources, dataElementA.getId(), optionCombo.getId(), periods );

        assertEquals( dataValues3.size(), 8 );

        Collection<DataValue> dataValues4 = aggregationStore.getDataValues( sourceA.getId(), dataElementA.getId(), optionCombo.getId(), periods );

        assertEquals( dataValues4.size(), 3 );        

        statementManager.destroy();
    }
}
