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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.system.grid.ListGrid;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author joakibj, briane, eivinhb
 * @version $Id$
 */
public class DataBrowserStoreTest
    extends DataBrowserTest
{
    private DataBrowserGridStore dataBrowserStore;

    private boolean isZeroAdded;

    @Override
    public void setUpTest()
        throws Exception
    {
        dataBrowserStore = (DataBrowserGridStore) getBean( DataBrowserGridStore.ID );

        super.setUpDataBrowserTest();
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    /**
     * Grid getDataSetsBetweenPeriods( List<Integer> betweenPeriodIds );
     */
    @Test
    public void testGetDataSetsBetweenPeriods()
    {
        List<Integer> betweenPeriodIds = new ArrayList<Integer>();
        betweenPeriodIds.add( periodA.getId() );
        betweenPeriodIds.add( periodB.getId() );
        betweenPeriodIds.add( periodC.getId() );
        betweenPeriodIds.add( periodD.getId() );

        Grid grid = dataBrowserStore.getDataSetsBetweenPeriods( betweenPeriodIds, periodA.getPeriodType(), isZeroAdded );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_set", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", grid.getVisibleHeaders().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "No.Row entries", 1, grid.getRows().size() );
        // assertEquals( dataSetB.getName(), ((MetaValue) grid.getRow( 0 ).get(
        // 0 )).getName() );
        // assertEquals( dataSetB.getId(), ((MetaValue) grid.getRow( 0 ).get( 0
        // )).getId().intValue() );
        assertEquals( dataSetA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataSetA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        // assertEquals( dataSetC.getName(), ((MetaValue) grid.getRow( 2 ).get(
        // 0 )).getName() );
        // assertEquals( dataSetC.getId(), ((MetaValue) grid.getRow( 2 ).get( 0
        // )).getId().intValue() );

        // assertEquals( "DataValues in dataSetB", "24", grid.getRow( 0 ).get( 1
        // ).toString() );
        assertEquals( "DataValues in dataSetA", "18", grid.getRow( 0 ).get( 1 ).toString() );
        // assertEquals( "DataValues in dataSetC", "12", grid.getRow( 2 ).get( 1
        // ).toString() );
    }

    /**
     * Grid getDataElementGroupsBetweenPeriods( List<Integer> betweenPeriodIds );
     */
    @Test
    public void testGetDataElementGroupsBetweenPeriods()
    {
        List<Integer> betweenPeriodIds = new ArrayList<Integer>();
        betweenPeriodIds.add( periodA.getId() );
        betweenPeriodIds.add( periodB.getId() );
        betweenPeriodIds.add( periodC.getId() );
        betweenPeriodIds.add( periodD.getId() );

        Grid grid = dataBrowserStore.getDataElementGroupsBetweenPeriods( betweenPeriodIds, isZeroAdded );

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

        assertEquals( "DataValues in dataElementGroupB", "24", grid.getRow( 0 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementGroupA", "18", grid.getRow( 1 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementGroupC", "12", grid.getRow( 2 ).get( 1 ).toString() );
    }

    /**
     * Grid getOrgUnitGroupsBetweenPeriods( List<Integer> betweenPeriodIds );
     */
    @Test
    public void testGetOrgUnitGroupsBetweenPeriods()
    {
        List<Integer> betweenPeriodIds = new ArrayList<Integer>();

        betweenPeriodIds.add( periodA.getId() );
        betweenPeriodIds.add( periodB.getId() );
        betweenPeriodIds.add( periodC.getId() );
        betweenPeriodIds.add( periodD.getId() );

        Grid grid = dataBrowserStore.getOrgUnitGroupsBetweenPeriods( betweenPeriodIds, isZeroAdded );

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

        // unitD has 10 DataValues, unitE has 10 DataValues and unitF has 8
        // DataValues
        assertEquals( "DataValues in unitGroupB", "28", grid.getRow( 0 ).get( 1 ).toString() );
        // unitB has 0 DataValues and unitC has 10 DataValues
        assertEquals( "DataValues in unitGroupA", "10", grid.getRow( 1 ).get( 1 ).toString() );
    }

    /**
     * void setDataElementStructureForDataSetBetweenPeriods( DataBrowserTable
     * table, Integer dataSetId, List<Integer> betweenPeriods );
     */
    @Test
    public void testSetDataElementStructureForDataSetBetweenPeriods()
    {
        List<Integer> metaList = new ArrayList<Integer>();

        // Retrieve dataElements of DataSetA - one dataElement
        Grid grid = new ListGrid();
        dataBrowserStore.setDataElementStructureForDataSet( grid, dataSetA.getId(), metaList );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 1, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 1, grid.getRows().size() );
        assertEquals( dataElementA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );

        // Retrieve dataElements of DataSetC - three dataElements
        grid = new ListGrid();
        metaList.clear();

        dataBrowserStore.setDataElementStructureForDataSet( grid, dataSetC.getId(), metaList );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 1, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 3, grid.getRows().size() );
        assertEquals( dataElementC.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementC.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementE.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementE.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementF.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( dataElementF.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );
    }

    /**
     * void setDataElementStructureForDataElementGroup( Grid grid, Integer
     * dataElementGroupId, List<Integer> betweenPeriods );
     */
    @Test
    public void testSetDataElementStructureForDataElementGroup()
    {
        List<Integer> metaList = new ArrayList<Integer>();

        // Retrieve dataElements of DataElementGroupA - one dataElement
        Grid grid = new ListGrid();
        dataBrowserStore.setDataElementStructureForDataElementGroup( grid, dataElementGroupA.getId(), metaList );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 1, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 1, grid.getRows().size() );
        assertEquals( dataElementA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );

        // Retrieve dataElements of DataElementGroupC - three dataElements
        grid = new ListGrid();
        metaList.clear();

        dataBrowserStore.setDataElementStructureForDataElementGroup( grid, dataElementGroupC.getId(), metaList );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 1, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 3, grid.getRows().size() );
        assertEquals( dataElementC.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementC.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementE.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementE.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementF.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( dataElementF.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );
    }

    /**
     * void setDataElementGroupStructureForOrgUnitGroup( Grid grid, Integer
     * orgUnitGroupId, List<Integer> betweenPeriods );
     */
    @Test
    public void testSetDataElementGroupStructureForOrgUnitGroup()
    {
        List<Integer> metaList = new ArrayList<Integer>();

        // Retrieve orgUnitGroupA - three dataElementGroups
        Grid grid = new ListGrid();
        dataBrowserStore.setDataElementGroupStructureForOrgUnitGroup( grid, unitGroupA.getId(), metaList );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 1, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element_group", grid.getVisibleHeaders().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 3, grid.getRows().size() );
        assertEquals( dataElementGroupA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementGroupA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementGroupB.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementGroupB.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementGroupC.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( dataElementGroupC.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );

        // Retrieve dataElements of orgUnitGroupB - three dataElementGroups
        grid = new ListGrid();
        metaList.clear();

        dataBrowserStore.setDataElementGroupStructureForOrgUnitGroup( grid, unitGroupB.getId(), metaList );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 1, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element_group", grid.getVisibleHeaders().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 3, grid.getRows().size() );
        assertEquals( dataElementGroupA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementGroupA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementGroupB.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementGroupB.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementGroupC.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( dataElementGroupC.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );
    }

    /**
     * void setStructureForOrgUnit( Grid grid, Integer orgUnitParent, List<Integer>
     * betweenPeriods );
     */
    @Test
    public void testSetStructureForOrgUnit()
    {
        List<Integer> metaList = new ArrayList<Integer>();

        // Retrieve children of unitB - three children
        Grid grid = new ListGrid();
        dataBrowserStore.setStructureForOrgUnit( grid, unitB.getId(), metaList );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 1, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_orgunit", grid.getVisibleHeaders().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 3, grid.getRows().size() );
        assertEquals( unitD.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( unitD.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( unitE.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( unitE.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( unitF.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( unitF.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );

        // Retrieve children of unitG - zero children
        grid = new ListGrid();
        metaList.clear();

        dataBrowserStore.setStructureForOrgUnit( grid, unitG.getId(), metaList );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 1, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_orgunit", grid.getVisibleHeaders().get( 0 ).getName() );

        assertEquals( "No.Row entries", 0, grid.getRows().size() );
    }

    /**
     * void setDataElementStructureForOrgUnit( DataBrowserTable grid, String
     * orgUnitId, List<Integer> betweenPeriodIds );
     * 
     * Notes: This test would be failure if active because of
     * _CATEGORYOPTIONCOMBONAME is missing/not found by H2 database
     */
    @Ignore
    public void testSetDataElementStructureForOrgUnit()
    {
        List<Integer> metaList = new ArrayList<Integer>();

        // Retrieve structure for dataElements in periodA for unitC - six
        // dataElements
        Grid grid = new ListGrid();

        dataBrowserStore.setDataElementStructureForOrgUnit( grid, unitC.getId(), metaList );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 1, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 6, grid.getRows().size() );
        assertEquals( dataElementA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementB.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementB.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementC.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( dataElementC.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementD.getName(), ((MetaValue) grid.getRow( 3 ).get( 0 )).getName() );
        assertEquals( dataElementD.getId(), ((MetaValue) grid.getRow( 3 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementE.getName(), ((MetaValue) grid.getRow( 4 ).get( 0 )).getName() );
        assertEquals( dataElementE.getId(), ((MetaValue) grid.getRow( 4 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementF.getName(), ((MetaValue) grid.getRow( 5 ).get( 0 )).getName() );
        assertEquals( dataElementF.getId(), ((MetaValue) grid.getRow( 5 ).get( 0 )).getId().intValue() );
    }

    /**
     * Integer setCountDataElementsForDataSetBetweenPeriods( DataBrowserTable
     * grid, Integer dataSetId, List<Integer> betweenPeriodIds, List<Integer>
     * metaIds, boolean zeroShowed );
     */
    @Test
    public void testSetCountDataElementsForDataSet()
    {
        List<Integer> metaIds = new ArrayList<Integer>();
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );

        // Retrieve structure for dataElements in periodA for dataSetA - one
        // dataElement
        Grid grid = new ListGrid();
        dataBrowserStore.setDataElementStructureForDataSet( grid, dataSetA.getId(), metaIds );

        // Retrieve actual count for dataElements in periodA for dataSetA
        int results = dataBrowserStore.setCountDataElementsForDataSetBetweenPeriods( grid, dataSetA.getId(), periodA
            .getPeriodType(), pList, metaIds, isZeroAdded );
        assertEquals( "DataValue results", 1, results );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", grid.getVisibleHeaders().get( 1 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 1, grid.getRows().size() );
        assertEquals( dataElementA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in dataElementA for periodA", "6", grid.getRow( 0 ).get( 1 ).toString() );
    }

    /**
     * Integer setDataElementStructureForDataElementGroup( Grid grid, Integer
     * dataElementGroupId, List<Integer> betweenPeriodIds, List<Integer>
     * metaIds, boolean zeroShowed );
     */
    @Test
    public void testSetCountDataElementsForDataElementGroupBetweenPeriods()
    {
        List<Integer> metaIds = new ArrayList<Integer>();
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );

        // Retrieve structure for dataElements in periodA for dataElementGroupA
        // - one dataElement
        Grid grid = new ListGrid();
        dataBrowserStore.setDataElementStructureForDataElementGroup( grid, dataElementGroupA.getId(), metaIds );

        // Retrieve actual count for dataElements in periodA for
        // dataElementGroupA
        int results = dataBrowserStore.setCountDataElementsForDataElementGroupBetweenPeriods( grid, dataElementGroupA
            .getId(), pList, metaIds, isZeroAdded );
        assertEquals( "DataValue results", 1, results );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", grid.getVisibleHeaders().get( 1 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 1, grid.getRows().size() );
        assertEquals( dataElementA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in dataElementA for periodA", "6", grid.getRow( 0 ).get( 1 ).toString() );
    }

    /**
     * Integer setCountDataElementGroupsForOrgUnitGroupBetweenPeriods( Grid
     * grid, Integer orgUnitGroupId, List<Integer> betweenPeriodIds, List<Integer>
     * metaIds, boolean zeroShowed );
     */
    @Test
    public void testSetCountDataElementGroupsForOrgUnitGroupBetweenPeriods()
    {
        List<Integer> metaIds = new ArrayList<Integer>();
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );

        // Retrieve structure for dataElementGroups in periodA for unitGroupA -
        // three dataElementGroups
        Grid grid = new ListGrid();
        dataBrowserStore.setDataElementGroupStructureForOrgUnitGroup( grid, unitGroupA.getId(), metaIds );

        // Retrieve actual count for dataElementGroups in periodA for unitGroupA
        int results = dataBrowserStore.setCountDataElementGroupsForOrgUnitGroupBetweenPeriods( grid,
            unitGroupA.getId(), pList, metaIds, isZeroAdded );
        assertEquals( "DataValue results", 3, results );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element_group", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", grid.getVisibleHeaders().get( 1 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 3, grid.getRows().size() );
        assertEquals( dataElementGroupA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementGroupA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementGroupB.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementGroupB.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementGroupC.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( dataElementGroupC.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );

        assertEquals( "DataValues in dataElementGroupA for periodA", "1", grid.getRow( 0 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementGroupB for periodA", "2", grid.getRow( 1 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementGroupC for periodA", "1", grid.getRow( 2 ).get( 1 ).toString() );
    }

    /**
     * Integer setRawDataElementsForOrgUnitBetweenPeriods( DataBrowserTable
     * grid, String orgUnitId, List<Integer> betweenPeriodIds, List<Integer>
     * metaIds, boolean zeroShowed );
     * 
     * Notes: This test would be failure if active because of
     * _CATEGORYOPTIONCOMBONAME is missing/not found by H2 database
     */
    @Ignore
    public void testSetRawDataElementsForOrgUnitBetweenPeriods()
    {
        List<Integer> metaIds = new ArrayList<Integer>();
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );

        // Retrieve structure for dataElements in periodA for unitC - six
        // dataElements
        Grid grid = new ListGrid();
        dataBrowserStore.setDataElementStructureForOrgUnit( grid, unitC.getId(), metaIds );

        // Retrieve actual count for dataElements in periodA for unitC
        int results = dataBrowserStore.setRawDataElementsForOrgUnitBetweenPeriods( grid, unitC.getId(), pList, metaIds,
            isZeroAdded );
        assertEquals( "DataValue results", 4, results );

        assertNotNull( "Grid not supposed to be null", grid );

        assertEquals( "Header Size", 2, grid.getVisibleHeaders().size() );
        assertEquals( "drilldown_data_element", grid.getVisibleHeaders().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", grid.getVisibleHeaders().get( 1 ).getName() );

        // Sorted by name
        assertEquals( "No.Row entries", 6, grid.getRows().size() );
        assertEquals( dataElementA.getName(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getName() );
        assertEquals( dataElementA.getId(), ((MetaValue) grid.getRow( 0 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementB.getName(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getName() );
        assertEquals( dataElementB.getId(), ((MetaValue) grid.getRow( 1 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementC.getName(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getName() );
        assertEquals( dataElementC.getId(), ((MetaValue) grid.getRow( 2 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementD.getName(), ((MetaValue) grid.getRow( 3 ).get( 0 )).getName() );
        assertEquals( dataElementD.getId(), ((MetaValue) grid.getRow( 3 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementE.getName(), ((MetaValue) grid.getRow( 4 ).get( 0 )).getName() );
        assertEquals( dataElementE.getId(), ((MetaValue) grid.getRow( 4 ).get( 0 )).getId().intValue() );
        assertEquals( dataElementF.getName(), ((MetaValue) grid.getRow( 5 ).get( 0 )).getName() );
        assertEquals( dataElementF.getId(), ((MetaValue) grid.getRow( 5 ).get( 0 )).getId().intValue() );

        // unitC has all six dataElements but only dataValues in periodA for
        // four of them. The other two (C and E) are zero
        assertEquals( "DataValues in dataElementA for periodA", "1", grid.getRow( 0 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementB for periodA", "1", grid.getRow( 1 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementC for periodA", "0", grid.getRow( 2 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementD for periodA", "1", grid.getRow( 3 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementE for periodA", "0", grid.getRow( 4 ).get( 1 ).toString() );
        assertEquals( "DataValues in dataElementF for periodA", "1", grid.getRow( 5 ).get( 1 ).toString() );
    }
}
