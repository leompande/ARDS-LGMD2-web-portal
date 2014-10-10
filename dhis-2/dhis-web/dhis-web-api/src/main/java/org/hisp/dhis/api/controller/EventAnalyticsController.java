package org.hisp.dhis.api.controller;

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

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.analytics.IllegalQueryException;
import org.hisp.dhis.analytics.SortOrder;
import org.hisp.dhis.analytics.event.EventAnalyticsService;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.system.grid.GridUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lars Helge Overland
 */
@Controller
public class EventAnalyticsController
{
    private static final String RESOURCE_PATH = "/analytics/events";

    @Autowired
    private EventAnalyticsService analyticsService;

    @Autowired
    private ContextUtils contextUtils;

    @Autowired
    private I18nManager i18nManager;
    
    // -------------------------------------------------------------------------
    // Aggregate
    // -------------------------------------------------------------------------
    
    @RequestMapping( value = RESOURCE_PATH + "/aggregate/{program}", method = RequestMethod.GET, produces = { "application/json", "application/javascript" } )
    public String getAggregateJson( // JSON, JSONP
        @PathVariable String program,
        @RequestParam(required=false) String stage,
        @RequestParam(required=false) String startDate,
        @RequestParam(required=false) String endDate,
        @RequestParam Set<String> dimension,
        @RequestParam(required=false) Set<String> filter,
        @RequestParam(required=false) boolean hierarchyMeta,
        @RequestParam(required=false) Integer limit,
        @RequestParam(required=false) SortOrder sortOrder,
        Model model,
        HttpServletResponse response ) throws Exception
    {
        EventQueryParams params = analyticsService.getFromUrl( program, stage, startDate, endDate, dimension, filter, hierarchyMeta, sortOrder, limit, i18nManager.getI18nFormat() );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, CacheStrategy.RESPECT_SYSTEM_SETTING );
        Grid grid = analyticsService.getAggregatedEventData( params );
        model.addAttribute( "model", grid );
        model.addAttribute( "viewClass", "detailed" );
        return "grid";
    }

    @RequestMapping( value = RESOURCE_PATH + "/aggregate/{program}.xls", method = RequestMethod.GET )
    public void getAggregateXls(
        @PathVariable String program,
        @RequestParam(required=false) String stage,
        @RequestParam(required=false) String startDate,
        @RequestParam(required=false) String endDate,
        @RequestParam Set<String> dimension,
        @RequestParam(required=false) Set<String> filter,
        @RequestParam(required=false) boolean hierarchyMeta,
        @RequestParam(required=false) Integer limit,
        @RequestParam(required=false) SortOrder sortOrder,
        Model model,
        HttpServletResponse response ) throws Exception
    {
        EventQueryParams params = analyticsService.getFromUrl( program, stage, startDate, endDate, dimension, filter, hierarchyMeta, sortOrder, limit, i18nManager.getI18nFormat() );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_EXCEL, CacheStrategy.RESPECT_SYSTEM_SETTING, "events.xls", true );
        Grid grid = analyticsService.getAggregatedEventData( params );
        GridUtils.toXls( substituteMetaData( grid ), response.getOutputStream() );
    }

    @RequestMapping( value = RESOURCE_PATH + "/aggregate/{program}.csv", method = RequestMethod.GET )
    public void getAggregateCsv(
        @PathVariable String program,
        @RequestParam(required=false) String stage,
        @RequestParam(required=false) String startDate,
        @RequestParam(required=false) String endDate,
        @RequestParam Set<String> dimension,
        @RequestParam(required=false) Set<String> filter,
        @RequestParam(required=false) boolean hierarchyMeta,
        @RequestParam(required=false) Integer limit,
        @RequestParam(required=false) SortOrder sortOrder,
        Model model,
        HttpServletResponse response ) throws Exception
    {
        EventQueryParams params = analyticsService.getFromUrl( program, stage, startDate, endDate, dimension, filter, hierarchyMeta, sortOrder, limit, i18nManager.getI18nFormat() );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_CSV, CacheStrategy.RESPECT_SYSTEM_SETTING, "events.csv", true );
        Grid grid = analyticsService.getAggregatedEventData( params );
        GridUtils.toCsv( substituteMetaData( grid ), response.getOutputStream() );
    }
    
    // -------------------------------------------------------------------------
    // Query
    // -------------------------------------------------------------------------
    
    @RequestMapping( value = RESOURCE_PATH + "/query/{program}", method = RequestMethod.GET, produces = { "application/json", "application/javascript" } )
    public String getQueryJson( // JSON, JSONP
        @PathVariable String program,
        @RequestParam(required=false) String stage,
        @RequestParam(required=false) String startDate,
        @RequestParam(required=false) String endDate,
        @RequestParam Set<String> dimension,
        @RequestParam(required=false) Set<String> filter,
        @RequestParam(required=false) String ouMode,
        @RequestParam(required=false) Set<String> asc,
        @RequestParam(required=false) Set<String> desc,
        @RequestParam(required=false) boolean hierarchyMeta,
        @RequestParam(required=false) boolean coordinatesOnly,
        @RequestParam(required=false) Integer page,
        @RequestParam(required=false) Integer pageSize,
        Model model,
        HttpServletResponse response ) throws Exception
    {
        EventQueryParams params = analyticsService.getFromUrl( program, stage, startDate, endDate, dimension, filter, ouMode, 
            asc, desc, hierarchyMeta, coordinatesOnly, page, pageSize, i18nManager.getI18nFormat() );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, CacheStrategy.RESPECT_SYSTEM_SETTING );
        Grid grid = analyticsService.getEvents( params );
        model.addAttribute( "model", grid );
        model.addAttribute( "viewClass", "detailed" );
        return "grid";
    }

    @RequestMapping( value = RESOURCE_PATH + "/query/{program}.xls", method = RequestMethod.GET )
    public void getQueryXls(
        @PathVariable String program,
        @RequestParam(required=false) String stage,
        @RequestParam(required=false) String startDate,
        @RequestParam(required=false) String endDate,
        @RequestParam Set<String> dimension,
        @RequestParam(required=false) Set<String> filter,
        @RequestParam(required=false) String ouMode,
        @RequestParam(required=false) Set<String> asc,
        @RequestParam(required=false) Set<String> desc,
        @RequestParam(required=false) boolean hierarchyMeta,
        @RequestParam(required=false) boolean coordinatesOnly,
        @RequestParam(required=false) Integer page,
        @RequestParam(required=false) Integer pageSize,
        Model model,
        HttpServletResponse response ) throws Exception
    {
        EventQueryParams params = analyticsService.getFromUrl( program, stage, startDate, endDate, dimension, filter, 
            ouMode, asc, desc, hierarchyMeta, coordinatesOnly, page, pageSize, i18nManager.getI18nFormat() );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_EXCEL, CacheStrategy.RESPECT_SYSTEM_SETTING, "events.xls", true );
        Grid grid = analyticsService.getEvents( params );
        GridUtils.toXls( substituteMetaData( grid ), response.getOutputStream() );
    }

    @RequestMapping( value = RESOURCE_PATH + "/query/{program}.csv", method = RequestMethod.GET )
    public void getQueryCsv(
        @PathVariable String program,
        @RequestParam(required=false) String stage,
        @RequestParam(required=false) String startDate,
        @RequestParam(required=false) String endDate,
        @RequestParam Set<String> dimension,
        @RequestParam(required=false) Set<String> filter,
        @RequestParam(required=false) String ouMode,
        @RequestParam(required=false) Set<String> asc,
        @RequestParam(required=false) Set<String> desc,
        @RequestParam(required=false) boolean hierarchyMeta,
        @RequestParam(required=false) boolean coordinatesOnly,
        @RequestParam(required=false) Integer page,
        @RequestParam(required=false) Integer pageSize,
        Model model,
        HttpServletResponse response ) throws Exception
    {
        EventQueryParams params = analyticsService.getFromUrl( program, stage, startDate, endDate, dimension, filter, 
            ouMode, asc, desc, hierarchyMeta, coordinatesOnly, page, pageSize, i18nManager.getI18nFormat() );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_CSV, CacheStrategy.RESPECT_SYSTEM_SETTING, "events.csv", true );
        Grid grid = analyticsService.getEvents( params );
        GridUtils.toCsv( substituteMetaData( grid ), response.getOutputStream() );
    }
    
    // -------------------------------------------------------------------------
    // Exception handling
    // -------------------------------------------------------------------------
  
    @ExceptionHandler(IllegalQueryException.class)
    public void handleError( IllegalQueryException ex, HttpServletResponse response )
    {
        ContextUtils.conflictResponse( response, ex.getMessage() );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleError( IllegalArgumentException ex, HttpServletResponse response )
    {
        ContextUtils.conflictResponse( response, ex.getMessage() );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
  
    @SuppressWarnings("unchecked")
    private Grid substituteMetaData( Grid grid )
    {
        if ( grid.getMetaData() != null && grid.getMetaData().containsKey( NAMES_META_KEY ) )
        {
            grid.substituteMetaData( (Map<Object, Object>) grid.getMetaData().get( NAMES_META_KEY ) );
        }
        
        return grid;
    }
}
