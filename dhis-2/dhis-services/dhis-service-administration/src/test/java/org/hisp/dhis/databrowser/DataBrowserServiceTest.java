package org.hisp.dhis.databrowser;

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
import static org.junit.Assert.assertTrue;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.period.PeriodService;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author joakibj, briane, eivinhb
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2010-04-15
 */
public class DataBrowserServiceTest
    extends DataBrowserTest
{
    private DataBrowserGridService dataBrowserService;

    private boolean isZeroAdded;

    @Override
    public void setUpTest()
        throws Exception
    {
        dataBrowserService = (DataBrowserGridService) getBean( DataBrowserGridService.ID );
        periodService = (PeriodService) getBean( PeriodService.ID );

        super.setUpDataBrowserTest();
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    /**
     * DataBrowserTable getDataSetsInPeriod( String startDate, String endDate,
     * PeriodType periodType );
     */
    @Test
    public void testGetDataSetsInPeriod()
    {
        // Get all DataSets from earliest to latest registered on daily basis
        // (this should be period A and B data values)
        Grid grid = dataBrowserService.getDataSetsInPeriod( null, null, periodA.getPeriodType(), mockFormat,
            isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_set", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", grid.getVisibleHeaders().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "Metarows", 1, grid.getRows().size() );
        //assertEquals( dataSetB.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        //assertEquals( dataSetB.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataSetA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataSetA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        //assertEquals( dataSetC.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        //assertEquals( dataSetC.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );

        //assertEquals( "DataValues in dataSetB", "18", grid.getRow( 0 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataSetA", "12", grid.getRow( 0 ).get( 1 ).toString() );
        //assertEquals( "DataValues in dataSetC", "3", grid.getRow( 2 ).get( 1 ).toString() );

        // Get all DataSets from 2005-05-01 to 2005-05-31 registered on weekly
        // basis (this should be only period D data values)
        grid = dataBrowserService.getDataSetsInPeriod( "2005-05-01", "2005-05-31", periodD.getPeriodType(), mockFormat,
            isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_set", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", grid.getVisibleHeaders().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "No.Row entries", 1, grid.getRows().size() );
        assertEquals( dataSetC.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataSetC.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in dataSetC", "6", grid.getRow( 0 ).get( 1 ).toString() );
    }

    /**
     * DataBrowserTable getDataElementGroupsInPeriod( String startDate, String
     * endDate, PeriodType periodType );
     */
    @Test
    public void testGetDataElementGroupsInPeriod()
    {
        // Get all DataElementGroups from earliest to latest registered on daily
        // basis (this should be period A and B data values)
        Grid grid = dataBrowserService.getDataElementGroupsInPeriod( null, null, periodA.getPeriodType(), mockFormat,
            isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element_group", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", grid.getVisibleHeaders().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "No.Row entries", 3, grid.getRows().size() );
        assertEquals( dataElementGroupB.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementGroupB.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementGroupA.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementGroupA.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementGroupC.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( dataElementGroupC.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in dataElementGroupB", "18", grid.getRow( 0 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementGroupA", "12", grid.getRow( 1 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementGroupC", "3", grid.getRow( 2 ).get( 1 ).toString() );

        // Get all DataElementGroups from 2005-05-01 to 2005-05-31 registered on
        // weekly basis (this should be only period D data values)
        grid = dataBrowserService.getDataElementGroupsInPeriod( "2005-05-01", "2005-05-31", periodD.getPeriodType(),
            mockFormat, isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element_group", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", grid.getVisibleHeaders().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "No.Row entries", 1, grid.getRows().size() );
        assertEquals( dataElementGroupC.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementGroupC.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in dataElementGroupC", "6", grid.getRow( 0 ).get( 1 ).toString() );
    }

    /**
     * DataBrowserTable getOrgUnitGroupsInPeriod( String startDate, String
     * endDate, PeriodType periodType );
     */
    @Test
    public void testGetOrgUnitGroupsInPeriod()
    {
        // Get all OrganisationUnitGroups from earliest to latest registered on
        // daily
        // basis (this should be period A and B data values)
        Grid grid = dataBrowserService.getOrgUnitGroupsInPeriod( null, null, periodA.getPeriodType(), mockFormat,
            isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_orgunit_group", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", grid.getVisibleHeaders().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "No.Row entries", 2, grid.getRows().size() );
        assertEquals( unitGroupB.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( unitGroupB.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( unitGroupA.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( unitGroupA.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );

        // unitD has 6 datavalues, unitE has 6 datavalues and unitF has 5
        // datavalues for periods A and B
        assertEquals( "DataValues in unitGroupB", "17", grid.getRow( 0 ).get( 1 ).toString() );
        // unitB has 0 datavalues and unitC has 6 datavalues for periods A and B
        assertEquals( "DataValues in unitGroupA", "6", grid.getRow( 1 ).get( 1 ).toString() );
    }

    /**
     * DataBrowserTable getOrgUnitsInPeriod( Integer orgUnitParent, String
     * startDate, String endDate, PeriodType periodType );
     */
    @Ignore
    public void testGetOrgUnitsInPeriod()
    {
        // Get all children of unit B from 2005-03-01 to 2005-04-30 registered
        // on daily basis (this should be period A and B data values)
        Grid grid = dataBrowserService.getOrgUnitsInPeriod( unitB.getId(), "2005-03-01", "2005-04-30", periodA
            .getPeriodType(), 4, mockFormat, isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 3, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_organisation_unit", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", grid.getVisibleHeaders().get( 1 ).getName() );
        assertEquals( "Period column header", "2005-04-01", grid.getVisibleHeaders().get( 2 ).getName() );

        // unitB has three children - sorted by name
        assertEquals( "No.Row entries", 3, grid.getRows().size() );
        assertEquals( unitD.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( unitD.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( unitE.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( unitE.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( unitF.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( unitF.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in unitD for periodA", "4", grid.getRow( 0 ).get( 1 ).toString() );
        assertEquals( "DataValues in unitD for periodB", "2", grid.getRow( 1 ).get( 1 ).toString() );
        assertEquals( "DataValues in unitE for periodA", "4", grid.getRow( 2 ).get( 1 ).toString() );
        assertEquals( "DataValues in unitE for periodB", "2", grid.getRow( 3 ).get( 1 ).toString() );
        assertEquals( "DataValues in unitF for periodA", "2", grid.getRow( 4 ).get( 1 ).toString() );
        assertEquals( "DataValues in unitF for periodB", "3", grid.getRow( 5 ).get( 1 ).toString() );

        // Retrieve children of unitG - zero children
        grid = dataBrowserService.getOrgUnitsInPeriod( unitG.getId(), null, null, periodA.getPeriodType(), 4,
            mockFormat, isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_organisation_unit", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", grid.getVisibleHeaders().get( 1 ).getName() );
        // Service layer adds "zero-column"
        assertEquals( "No.Row entries", 0, grid.getRows().size() );
    }

    /**
     * DataBrowserTable getCountDataElementsForDataSetInPeriod( Integer
     * dataSetId, String startDate, String endDate, PeriodType periodType );
     */
    @Test
    public void testGetCountDataElementsForDataSetInPeriod()
    {
        // Get count for dataSetA from 2005-03-01 to 2005-04-30 registered on
        // daily basis (this should be period A and B data values)
        Grid grid = dataBrowserService.getCountDataElementsForDataSetInPeriod( dataSetA.getId(), "2005-03-01",
            "2005-04-30", periodA.getPeriodType(), mockFormat, isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 3, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", grid.getVisibleHeaders().get( 1 ).getName() );
        assertEquals( "Period column header", "2005-04-01", grid.getVisibleHeaders().get( 2 ).getName() );

        // dataSetA has one dataElement - sorted by name
        assertEquals( "No.Row entries", 1, grid.getRows().size() );
        assertEquals( dataElementA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in dataElementA", "6", grid.getRow( 0 ).get( 1 ).toString() );

        // Get count for dataSetC from 2005-05-01 to 2005-05-31 registered on
        // weekly basis (this should be only period D data values)
        grid = dataBrowserService.getCountDataElementsForDataSetInPeriod( dataSetC.getId(), "2005-05-01", "2005-05-31",
            periodD.getPeriodType(), mockFormat, isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-05-01", grid.getVisibleHeaders().get( 1 ).getName() );

        // dataSetC has three dataElements - sorted by name
        assertEquals( "No.Row entries", 3, grid.getRows().size() );
        assertEquals( dataElementC.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementC.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementE.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementE.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementF.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( dataElementF.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );
        
        assertEquals( "DataValues in dataElementC", "3", grid.getRow( 0 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementE", "3", grid.getRow( 1 ).get( 1 ).toString() );
    }

    /**
     * DataBrowserTable getCountDataElementsForDataElementGroupInPeriod( Integer
     * dataElementGroupId, String startDate, String endDate, PeriodType
     * periodType );
     */
    @Test
    public void testGetCountDataElementsForDataElementGroupInPeriod()
    {
        // Get count for dataElementGroupA from 2005-03-01 to 2005-04-30
        // registered on daily basis (this should be period A and B data values)
        Grid grid = dataBrowserService.getCountDataElementsForDataElementGroupInPeriod( dataElementGroupA.getId(),
            "2005-03-01", "2005-04-30", periodA.getPeriodType(), mockFormat, isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 3, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", grid.getVisibleHeaders().get( 1 ).getName() );
        assertEquals( "Period column header", "2005-04-01", grid.getVisibleHeaders().get( 2 ).getName() );

        // dataElementGroupA has one dataElement - sorted by name
        assertEquals( "No.Row entries", 1, grid.getRows().size() );
        assertEquals( dataElementA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in dataElementA", "6", grid.getRow( 0 ).get( 1 ).toString() );

        // Get count for dataElementGroupC from 2005-05-01 to 2005-05-31
        // registered on weekly basis (this should be only period D data values)
        grid = dataBrowserService.getCountDataElementsForDataElementGroupInPeriod( dataElementGroupC.getId(),
            "2005-05-01", "2005-05-31", periodD.getPeriodType(), mockFormat, isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-05-01", grid.getVisibleHeaders().get( 1 ).getName() );

        // dataElementGroupC has two dataElements - sorted by name
        assertEquals( "No.Row entries", 3, grid.getRows().size() );
        assertEquals( dataElementC.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementC.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementE.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementE.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in dataElementC", "3", grid.getRow( 0 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementE", "3", grid.getRow( 1 ).get( 1 ).toString() );
    }

    /**
     * DataBrowserTable getCountDataElementGroupsForOrgUnitGroupInPeriod(
     * Integer orgUnitGroupId, String startDate, String endDate, PeriodType
     * periodType, I18nFormat format, boolean zeroShowed );
     */
    @Test
    public void testGetCountDataElementGroupsForOrgUnitGroupInPeriod()
    {
        isZeroAdded = true;

        // Get count for unitGroupA from 2005-03-01 to 2005-04-30 registered on
        // daily
        // basis (this should be period A and B data values)
        Grid grid = dataBrowserService.getCountDataElementGroupsForOrgUnitGroupInPeriod( unitGroupA.getId(),
            "2005-03-01", "2005-04-30", periodA.getPeriodType(), mockFormat, isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        // unitGroupA has data values for dataElementGroup A, B and C in the two
        // periods
        assertEquals( "Header Size", 3, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element_group", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", grid.getVisibleHeaders().get( 1 ).getName() );
        assertEquals( "Period column header", "2005-04-01", grid.getVisibleHeaders().get( 2 ).getName() );

        // unitGroupA has data values for dataElementGroup A, B and C - sorted
        // by name
        assertEquals( dataElementGroupA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementGroupA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementGroupB.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementGroupB.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementGroupC.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( dataElementGroupC.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in dataElementGroupA for periodA", "1", grid.getRow( 0 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementGroupA for PeriodB", "1", grid.getRow( 0 ).get( 2 ).toString() );
        assertEquals( "DataValues in dataElementGroupB for PeriodA", "2", grid.getRow( 1 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementGroupB for PeriodB", "1", grid.getRow( 1 ).get( 2 ).toString() );
        assertEquals( "DataValues in dataElementGroupC for PeriodA", "1", grid.getRow( 2 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementGroupC for PeriodB", "0", grid.getRow( 2 ).get( 2 ).toString() );
    }

    /**
     * DataBrowserTable getCountDataElementsForOrgUnitInPeriod( Integer
     * organizationUnitId, String startDate, String endDate, PeriodType
     * periodType );
     */
    @Ignore
    public void testGetRawDataElementsForOrgUnitInPeriod()
    {
        // Get count for unitB from 2005-03-01 to 2005-04-30 registered on daily
        // basis (this should be period A and B data values)
        Grid grid = dataBrowserService.getRawDataElementsForOrgUnitInPeriod( unitB.getId(), "2005-03-01", "2005-04-30",
            periodA.getPeriodType(), mockFormat, isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        // unitB has no data values
        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );
        // Service layer adds "zero-column"
        assertEquals( "Period column header", "counts_of_aggregated_values", grid.getVisibleHeaders().get( 1 )
            .getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 0, grid.getRows().size() );

        // Get count for unitF from 2005-03-01 to 2005-04-30 registered on daily
        // basis (this should be period A and B data values)
        grid = dataBrowserService.getRawDataElementsForOrgUnitInPeriod( unitF.getId(), "2005-03-01", "2005-04-30",
            periodA.getPeriodType(), mockFormat, isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        // unitF has data values for dataElements A, B, D and E in two periods
        assertEquals( "Header Size", 3, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", grid.getVisibleHeaders().get( 1 ).getName() );
        assertEquals( "Period column header", "2005-04-01", grid.getVisibleHeaders().get( 2 ).getName() );

        // unitF has data values for data elements A, B, and D - sorted by name
        // Consists:
        // two data values for A count
        // two data values for B count
        // one data value for D count

        assertEquals( "No.Row entries", 4, grid.getRows().size() );

        assertEquals( dataElementA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementB.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementB.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementD.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( dataElementD.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in dataElementA for periodA", "1", grid.getRow( 0 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementA for PeriodB", "1", grid.getRow( 0 ).get( 2 ).toString() );
        assertEquals( "DataValues in dataElementB for PeriodA", "1", grid.getRow( 1 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementB for PeriodB", "1", grid.getRow( 1 ).get( 2 ).toString() );
        assertEquals( "DataValues in dataElementD for PeriodA", "0", grid.getRow( 2 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementD for PeriodB", "1", grid.getRow( 2 ).get( 2 ).toString() );
    }

    /**
     * String convertDate( PeriodType periodType, String dateString, I18nFormat
     * format );
     */
    @Test
    public void testConvertDate()
    {
        // Get count for unitGroupA from 2005-03-01 to 2005-04-30 registered on
        // daily
        // basis (this should be period A and B data values)
        Grid grid = dataBrowserService.getCountDataElementGroupsForOrgUnitGroupInPeriod( unitGroupA.getId(),
            "2005-03-01", "2005-04-30", periodA.getPeriodType(), mockFormat, isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        // unitGroupA has data values for dataElementGroup A, B and C in the two
        // periods
        assertEquals( "Header Size", 3, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element_group", grid.getVisibleHeaders().get( 0 ).getName() );

        assertEquals( "drilldown_data_element_group", dataBrowserService.convertDate( periodA.getPeriodType(), grid
            .getVisibleHeaders().get( 0 ).getName(), mockI18n, mockFormat ) );
        assertTrue( "Period column header 2005-03-01", dataBrowserService.convertDate( periodA.getPeriodType(),
            grid.getVisibleHeaders().get( 1 ).getName(), mockI18n, mockFormat ).startsWith( "Period_2005-03-01" ) );
        assertTrue( "Period column header 2005-04-01", dataBrowserService.convertDate( periodA.getPeriodType(),
            grid.getVisibleHeaders().get( 2 ).getName(), mockI18n, mockFormat ).startsWith( "Period_2005-04-01" ) );

    }
}
