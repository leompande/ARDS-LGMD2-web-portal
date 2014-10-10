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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.ListUtils;
import org.junit.Test;

/**
 * @author eirikmi
 * @version $Id: MinMaxOutlierAnalysisServiceTest.java 883 2009-05-15 00:42:45Z daghf $
 */
public class MinMaxOutlierAnalysisServiceTest
    extends DhisSpringTest
{
    private DataAnalysisService minMaxOutlierAnalysisService;

    private MinMaxDataElementService minMaxDataElementService;

    private DataElement dataElementA;
    private DataElement dataElementB;
    private DataElement dataElementC;
    private DataElement dataElementD;

    private DataValue dataValueA;
    private DataValue dataValueB;

    private Set<DataElement> dataElementsA = new HashSet<DataElement>();
    private Set<DataElement> dataElementsB = new HashSet<DataElement>();
    private Set<DataElement> dataElementsC = new HashSet<DataElement>();

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

    private MinMaxDataElement minMaxDataElement;

    // ----------------------------------------------------------------------
    // Fixture
    // ----------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        minMaxOutlierAnalysisService = (DataAnalysisService) getBean( "org.hisp.dhis.dataanalysis.MinMaxOutlierAnalysisService" );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        minMaxDataElementService = (MinMaxDataElementService) getBean( MinMaxDataElementService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );

        dataSetService = (DataSetService) getBean( DataSetService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        dataValueService = (DataValueService) getBean( DataValueService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        categoryCombo = categoryService.getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        dataElementA = createDataElement( 'A', categoryCombo );
        dataElementB = createDataElement( 'B', categoryCombo );
        dataElementC = createDataElement( 'C', categoryCombo );
        dataElementD = createDataElement( 'D', categoryCombo );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );

        dataElementsA.add( dataElementA );
        dataElementsA.add( dataElementB );
        dataElementsB.add( dataElementC );
        dataElementsB.add( dataElementD );
        dataElementsC.add( dataElementB );

        categoryOptionCombo = categoryCombo.getOptionCombos().iterator().next();

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

        organisationUnitService.addOrganisationUnit( organisationUnitA );
    }

    // ----------------------------------------------------------------------
    // Business logic tests
    // ----------------------------------------------------------------------

    @Test
    public void testGetFindOutliers()
    {
        dataValueA = createDataValue( dataElementA, periodI, organisationUnitA, "41", categoryOptionCombo );
        dataValueB = createDataValue( dataElementA, periodJ, organisationUnitA, "-41", categoryOptionCombo );

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, organisationUnitA, "5", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, organisationUnitA, "-5", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, organisationUnitA, "5", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodD, organisationUnitA, "-5", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodE, organisationUnitA, "10", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodF, organisationUnitA, "-10", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodG, organisationUnitA, "13", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodH, organisationUnitA, "-13", categoryOptionCombo ) );
        
        dataValueService.addDataValue( dataValueA );
        dataValueService.addDataValue( dataValueB );
        
        minMaxDataElement = new MinMaxDataElement( organisationUnitA, dataElementA, categoryOptionCombo, -40, 40, false );
        minMaxDataElementService.addMinMaxDataElement( minMaxDataElement );
                
        Collection<Period> periods = new ArrayList<Period>();
        periods.add( periodI );
        periods.add( periodJ );
        periods.add( periodA );
        periods.add( periodE );

        Collection<DeflatedDataValue> result = minMaxOutlierAnalysisService.analyse( ListUtils.getCollection( organisationUnitA ), dataElementsA, periods, null );

        assertEquals( 2, result.size() );
    }
}
