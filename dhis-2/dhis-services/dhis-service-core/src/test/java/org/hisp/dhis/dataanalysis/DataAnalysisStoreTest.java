package org.hisp.dhis.dataanalysis;

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
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class DataAnalysisStoreTest
    extends DhisSpringTest
{
    private DataAnalysisStore dataAnalysisStore;
    
    private DataElement dataElementA;
    private DataElement dataElementB;

    private DataElementCategoryCombo categoryCombo;

    private DataElementCategoryOptionCombo categoryOptionCombo;

    private Period periodA;
    private Period periodB;
    private Period periodC;    
    private Period periodD;    
    private Period periodE;    
    private Period periodF;    
    private Period periodG;
    private Period periodH;
    private Period periodI;
    private Period periodJ;

    private OrganisationUnit organisationUnitA;
    private OrganisationUnit organisationUnitB;
    
    private Set<Integer> organisationUnits;
    
    // ----------------------------------------------------------------------
    // Fixture
    // ----------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        dataAnalysisStore = (DataAnalysisStore) getBean( DataAnalysisStore.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );

        dataSetService = (DataSetService) getBean( DataSetService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        dataValueService = (DataValueService) getBean( DataValueService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        categoryCombo = categoryService.getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        dataElementA = createDataElement( 'A', categoryCombo );
        dataElementB = createDataElement( 'B', categoryCombo );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );

        categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        periodA = createPeriod( new MonthlyPeriodType(), getDate( 2000, 3, 1 ), getDate( 2000, 3, 31 ) );
        periodB = createPeriod( new MonthlyPeriodType(), getDate( 2000, 4, 1 ), getDate( 2000, 4, 30 ) );
        periodC = createPeriod( new MonthlyPeriodType(), getDate( 2000, 5, 1 ), getDate( 2000, 5, 30 ) );
        periodD = createPeriod( new MonthlyPeriodType(), getDate( 2000, 6, 1 ), getDate( 2000, 6, 30 ) );
        periodE = createPeriod( new MonthlyPeriodType(), getDate( 2000, 7, 1 ), getDate( 2000, 7, 30 ) );
        periodF = createPeriod( new MonthlyPeriodType(), getDate( 2000, 8, 1 ), getDate( 2000, 8, 30 ) );
        periodG = createPeriod( new MonthlyPeriodType(), getDate( 2000, 9, 1 ), getDate( 2000, 9, 30 ) );
        periodH = createPeriod( new MonthlyPeriodType(), getDate( 2000, 10, 1 ), getDate( 2000, 10, 30 ) );
        periodI = createPeriod( new MonthlyPeriodType(), getDate( 2000, 11, 1 ), getDate( 2000, 11, 30 ) );
        periodJ = createPeriod( new MonthlyPeriodType(), getDate( 2000, 12, 1 ), getDate( 2000, 12, 30 ) );

        organisationUnitA = createOrganisationUnit( 'A' );
        organisationUnitB = createOrganisationUnit( 'B' );

        organisationUnitService.addOrganisationUnit( organisationUnitA );
        organisationUnitService.addOrganisationUnit( organisationUnitB );
        
        organisationUnits = new HashSet<Integer>();
        organisationUnits.add( organisationUnitA.getId() );
        organisationUnits.add( organisationUnitB.getId() );
    }

    // ----------------------------------------------------------------------
    // Business logic tests
    // ----------------------------------------------------------------------

    @Test
    public void getGetStdDev()
    {
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, organisationUnitA, "5", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, organisationUnitA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, organisationUnitA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodD, organisationUnitA, "12", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodE, organisationUnitA, "10", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodF, organisationUnitA, "7", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodG, organisationUnitA, "52", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodH, organisationUnitA, "23", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodI, organisationUnitA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodJ, organisationUnitA, "15", categoryOptionCombo ) );
        
        assertEquals( 15.26, dataAnalysisStore.getStandardDeviation( dataElementA, categoryOptionCombo, organisationUnits ).get( organisationUnitA.getId() ), DELTA );
        assertNull( dataAnalysisStore.getStandardDeviation( dataElementA, categoryOptionCombo, organisationUnits ).get( organisationUnitB.getId() ) );
    }

    @Test
    public void getGetAvg()
    {
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, organisationUnitA, "5", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, organisationUnitA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, organisationUnitA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodD, organisationUnitA, "12", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodE, organisationUnitA, "10", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodF, organisationUnitA, "7", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodG, organisationUnitA, "52", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodH, organisationUnitA, "23", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodI, organisationUnitA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodJ, organisationUnitA, "15", categoryOptionCombo ) );
        
        assertEquals( 12.78, dataAnalysisStore.getAverage( dataElementA, categoryOptionCombo, organisationUnits ).get( organisationUnitA.getId() ), DELTA );
        assertNull( dataAnalysisStore.getAverage( dataElementA, categoryOptionCombo, organisationUnits ).get( organisationUnitB.getId() ) );
    }
}
