package org.hisp.dhis.analytics.event.data;

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

import static org.hisp.dhis.analytics.AnalyticsService.NAMES_META_KEY;
import static org.hisp.dhis.analytics.AnalyticsService.OU_HIERARCHY_KEY;
import static org.hisp.dhis.analytics.DataQueryParams.DIMENSION_NAME_SEP;
import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;
import static org.hisp.dhis.common.NameableObjectUtils.asTypedList;
import static org.hisp.dhis.organisationunit.OrganisationUnit.getParentGraphMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.IllegalQueryException;
import org.hisp.dhis.analytics.SortOrder;
import org.hisp.dhis.analytics.event.EventAnalyticsManager;
import org.hisp.dhis.analytics.event.EventAnalyticsService;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.analytics.event.EventQueryPlanner;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class DefaultEventAnalyticsService
    implements EventAnalyticsService
{
    private static final String ITEM_EVENT = "psi";

    private static final String ITEM_PROGRAM_STAGE = "ps";

    private static final String ITEM_EXECUTION_DATE = "eventdate";

    private static final String ITEM_LONGITUDE = "longitude";

    private static final String ITEM_LATITUDE = "latitude";

    private static final String ITEM_ORG_UNIT = "ou";

    private static final String ITEM_ORG_UNIT_NAME = "ouname";

    private static final String ITEM_ORG_UNIT_CODE = "oucode";

    private static final String COL_NAME_EVENTDATE = "executiondate";

    private static final List<String> SORTABLE_ITEMS = Arrays.asList( ITEM_EXECUTION_DATE, ITEM_ORG_UNIT_NAME,
        ITEM_ORG_UNIT_CODE );

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private PatientAttributeService attributeService;

    @Autowired
    private PatientIdentifierTypeService identifierTypeService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private EventAnalyticsManager analyticsManager;

    @Autowired
    private EventQueryPlanner queryPlanner;

    @Autowired
    private AnalyticsService analyticsService;

    // -------------------------------------------------------------------------
    // EventAnalyticsService implementation
    // -------------------------------------------------------------------------

    // TODO order event analytics tables on execution date to avoid default
    // sorting in queries
    // TODO parallel processing of queries

    public Grid getAggregatedEventData( EventQueryParams params )
    {
        queryPlanner.validate( params );
        
        List<String> validPartitions = analyticsManager.getAnalyticsTables( params.getProgram() );

        Grid grid = new ListGrid();

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        for ( DimensionalObject dimension : params.getDimensions() )
        {
            grid.addHeader( new GridHeader( dimension.getDimension(), dimension.getDisplayName() ) );
        }

        for ( QueryItem item : params.getItems() )
        {
            grid.addHeader( new GridHeader( item.getItem().getUid(), item.getItem().getName() ) );
        }

        grid.addHeader( new GridHeader( "value", "Value" ) );

        // ---------------------------------------------------------------------
        // Data
        // ---------------------------------------------------------------------

        List<EventQueryParams> queries = queryPlanner.planQuery( params, validPartitions );

        for ( EventQueryParams query : queries )
        {
            analyticsManager.getAggregatedEventData( query, grid );
        }

        // ---------------------------------------------------------------------
        // Meta-data
        // ---------------------------------------------------------------------

        Map<Object, Object> metaData = new HashMap<Object, Object>();

        Map<String, String> uidNameMap = getUidNameMap( params );

        metaData.put( NAMES_META_KEY, uidNameMap );

        if ( params.isHierarchyMeta() )
        {
            metaData
                .put(
                    OU_HIERARCHY_KEY,
                    getParentGraphMap( asTypedList( params.getDimensionOrFilter( ORGUNIT_DIM_ID ),
                        OrganisationUnit.class ) ) );
        }

        grid.setMetaData( metaData );

        return grid;
    }

    public Grid getEvents( EventQueryParams params )
    {
        queryPlanner.validate( params );

        Grid grid = new ListGrid();

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        grid.addHeader( new GridHeader( ITEM_EVENT, "Event", String.class.getName(), true, false ) );
        grid.addHeader( new GridHeader( ITEM_PROGRAM_STAGE, "Program stage", String.class.getName(), false, true ) );
        grid.addHeader( new GridHeader( ITEM_EXECUTION_DATE, "Event date" ) );
        grid.addHeader( new GridHeader( ITEM_LONGITUDE, "Longitude" ) );
        grid.addHeader( new GridHeader( ITEM_LATITUDE, "Latitude" ) );
        grid.addHeader( new GridHeader( ITEM_ORG_UNIT_NAME, "Organisation unit name" ) );
        grid.addHeader( new GridHeader( ITEM_ORG_UNIT_CODE, "Organisation unit code" ) );
        grid.addHeader( new GridHeader( ITEM_ORG_UNIT, "Organisation unit", String.class.getName(), true, false ) );

        for ( QueryItem queryItem : params.getItems() )
        {
            IdentifiableObject item = queryItem.getItem();

            grid.addHeader( new GridHeader( item.getUid(), item.getName() ) );
        }

        // ---------------------------------------------------------------------
        // Data
        // ---------------------------------------------------------------------

        Timer t = new Timer().start();

        List<EventQueryParams> queries = queryPlanner.planQuery( params, null );

        t.getSplitTime( "Planned query, got: " + queries.size() );

        int count = 0;

        for ( EventQueryParams query : queries )
        {
            if ( params.isPaging() )
            {
                count += analyticsManager.getEventCount( query );
            }

            analyticsManager.getEvents( query, grid );
        }

        t.getTime( "Queried events, got: " + grid.getHeight() );

        // ---------------------------------------------------------------------
        // Meta-data
        // ---------------------------------------------------------------------

        Map<Object, Object> metaData = new HashMap<Object, Object>();

        Map<String, String> uidNameMap = getUidNameMap( params );

        metaData.put( NAMES_META_KEY, uidNameMap );

        if ( params.isHierarchyMeta() )
        {
            metaData
                .put(
                    OU_HIERARCHY_KEY,
                    getParentGraphMap( asTypedList( params.getDimensionOrFilter( ORGUNIT_DIM_ID ),
                        OrganisationUnit.class ) ) );
        }

        if ( params.isPaging() )
        {
            Pager pager = new Pager( params.getPageWithDefault(), count, params.getPageSizeWithDefault() );
            metaData.put( AnalyticsService.PAGER_META_KEY, pager );
        }

        grid.setMetaData( metaData );

        return grid;
    }

    public EventQueryParams getFromUrl( String program, String stage, String startDate, String endDate,
        Set<String> dimension, Set<String> filter, boolean hierarchyMeta, SortOrder sortOrder, Integer limit,
        I18nFormat format )
    {
        EventQueryParams params = getFromUrl( program, stage, startDate, endDate, dimension, filter, null, null, null,
            hierarchyMeta, false, null, null, format );
        params.setSortOrder( sortOrder );
        params.setLimit( limit );
        params.setAggregate( true );

        return params;
    }

    public EventQueryParams getFromUrl( String program, String stage, String startDate, String endDate,
        Set<String> dimension, Set<String> filter, String ouMode, Set<String> asc, Set<String> desc,
        boolean hierarchyMeta, boolean coordinatesOnly, Integer page, Integer pageSize, I18nFormat format )
    {
        EventQueryParams params = new EventQueryParams();

        Date date = new Date();

        Program pr = programService.getProgram( program );

        if ( pr == null )
        {
            throw new IllegalQueryException( "Program does not exist: " + program );
        }

        ProgramStage ps = programStageService.getProgramStage( stage );

        if ( stage != null && !stage.isEmpty() && ps == null )
        {
            throw new IllegalQueryException( "Program stage is specified but does not exist: " + stage );
        }

        Date start = null;
        Date end = null;

        if ( startDate != null && endDate != null )
        {
            try
            {
                start = DateUtils.getMediumDate( startDate );
                end = DateUtils.getMediumDate( endDate );
            }
            catch ( RuntimeException ex )
            {
                throw new IllegalQueryException( "Start date or end date is invalid: " + startDate + " - " + endDate );
            }
        }

        if ( dimension != null )
        {
            for ( String dim : dimension )
            {
                String dimensionId = DataQueryParams.getDimensionFromParam( dim );

                if ( ORGUNIT_DIM_ID.equals( dimensionId ) || PERIOD_DIM_ID.equals( dimensionId ) )
                {
                    List<String> items = DataQueryParams.getDimensionItemsFromParam( dim );
                    params.getDimensions().addAll( analyticsService.getDimension( dimensionId, items, date, format ) );
                }
                else
                {
                    params.getItems().addAll( getQueryItems( dim, pr ) );
                }
            }
        }

        if ( filter != null )
        {
            for ( String dim : filter )
            {
                String dimensionId = DataQueryParams.getDimensionFromParam( dim );

                if ( ORGUNIT_DIM_ID.equals( dimensionId ) || PERIOD_DIM_ID.equals( dimensionId ) )
                {
                    List<String> items = DataQueryParams.getDimensionItemsFromParam( dim );
                    params.getFilters().addAll( analyticsService.getDimension( dimensionId, items, date, format ) );
                }
                else
                {
                    params.getItemFilters().addAll( getQueryItems( dim, pr ) );
                }
            }
        }

        for ( NameableObject object : params.getDimensionOrFilter( ORGUNIT_DIM_ID ) )
        {
            OrganisationUnit unit = (OrganisationUnit) object;
            unit.setLevel( organisationUnitService.getLevelOfOrganisationUnit( unit.getUid() ) );
        }

        if ( asc != null )
        {
            for ( String sort : asc )
            {
                params.getAsc().add( getSortItem( sort, pr ) );
            }
        }

        if ( desc != null )
        {
            for ( String sort : desc )
            {
                params.getDesc().add( getSortItem( sort, pr ) );
            }
        }

        params.setProgram( pr );
        params.setProgramStage( ps );
        params.setStartDate( start );
        params.setEndDate( end );
        params.setOrganisationUnitMode( ouMode );
        params.setHierarchyMeta( hierarchyMeta );
        params.setCoordinatesOnly( coordinatesOnly );
        params.setPage( page );
        params.setPageSize( pageSize );
        params.setAggregate( false );

        return params;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private List<QueryItem> getQueryItems( String dimension, Program program )
    {
        List<QueryItem> items = new ArrayList<QueryItem>();

        if ( !dimension.contains( DIMENSION_NAME_SEP ) )
        {
            items.add( getItem( program, dimension, null, null ) );
        }
        else // Filter
        {
            String[] split = dimension.split( DIMENSION_NAME_SEP );

            if ( split == null || split.length != 3 )
            {
                throw new IllegalQueryException( "Item filter has invalid format: " + dimension );
            }

            items.add( getItem( program, split[0], split[1], split[2] ) );
        }

        return items;
    }

    private Map<String, String> getUidNameMap( EventQueryParams params )
    {
        Map<String, String> map = new HashMap<String, String>();

        Program program = params.getProgram();
        ProgramStage stage = params.getProgramStage();

        map.put( program.getUid(), program.getName() );

        if ( stage != null )
        {
            map.put( stage.getUid(), stage.getName() );
        }
        else
        {
            for ( ProgramStage st : program.getProgramStages() )
            {
                map.put( st.getUid(), st.getName() );
            }
        }

        for ( QueryItem item : params.getItems() )
        {
            map.put( item.getItem().getUid(), item.getItem().getDisplayName() );
        }

        for ( QueryItem item : params.getItemFilters() )
        {
            map.put( item.getItem().getUid(), item.getItem().getDisplayName() );
        }

        map.putAll( getUidNameMap( params.getDimensions(), params.isHierarchyMeta() ) );
        map.putAll( getUidNameMap( params.getFilters(), params.isHierarchyMeta() ) );

        return map;
    }

    private Map<String, String> getUidNameMap( List<DimensionalObject> dimensions, boolean hierarchyMeta )
    {
        Map<String, String> map = new HashMap<String, String>();

        for ( DimensionalObject dimension : dimensions )
        {
            boolean hierarchy = hierarchyMeta && DimensionType.ORGANISATIONUNIT.equals( dimension.getType() );

            for ( IdentifiableObject idObject : dimension.getItems() )
            {
                map.put( idObject.getUid(), idObject.getDisplayName() );

                if ( hierarchy )
                {
                    OrganisationUnit unit = (OrganisationUnit) idObject;

                    map.putAll( IdentifiableObjectUtils.getUidNameMap( unit.getAncestors() ) );
                }
            }
        }

        return map;
    }

    private String getSortItem( String item, Program program )
    {
        if ( !SORTABLE_ITEMS.contains( item.toLowerCase() ) && getItem( program, item, null, null ) == null )
        {
            throw new IllegalQueryException( "Descending sort item is invalid: " + item );
        }

        item = ITEM_EXECUTION_DATE.equalsIgnoreCase( item ) ? COL_NAME_EVENTDATE : item;

        return item;
    }

    private QueryItem getItem( Program program, String item, String operator, String filter )
    {
        DataElement de = dataElementService.getDataElement( item );

        if ( de != null && program.getAllDataElements().contains( de ) )
        {
            return new QueryItem( de, operator, filter, de.isNumericType() );
        }

        PatientAttribute at = attributeService.getPatientAttribute( item );

        if ( at != null && program.getAttributes().contains( at ) )
        {
            return new QueryItem( at, operator, filter, at.isNumericType() );
        }

        PatientIdentifierType it = identifierTypeService.getPatientIdentifierType( item );

        if ( it != null && program.getIdentifierTypes().contains( it ) )
        {
            return new QueryItem( it, operator, filter, false );
        }

        throw new IllegalQueryException( "Item identifier does not reference any item part of the program: " + item );
    }
}
