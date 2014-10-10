package org.hisp.dhis.api.controller.mapping;

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

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.dataelement.DataElementOperandService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.mapgeneration.MapGenerationService;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.hisp.dhis.api.utils.ContextUtils.DATE_PATTERN;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = MapController.RESOURCE_PATH )
public class MapController
    extends AbstractCrudController<Map>
{
    public static final String RESOURCE_PATH = "/maps";

    @Autowired
    private MappingService mappingService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private DataElementOperandService operandService;
    
    @Autowired
    private PeriodService periodService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private I18nManager i18nManager;

    @Autowired
    private MapGenerationService mapGenerationService;
    
    @Autowired
    private DimensionService dimensionService;
    
    @Autowired
    private UserService userService; 

    @Autowired
    private ContextUtils contextUtils;

    //--------------------------------------------------------------------------
    // CRUD
    //--------------------------------------------------------------------------

    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        Map map = JacksonUtils.fromJson( input, Map.class );

        mergeMap( map );

        for ( MapView view : map.getMapViews() )
        {
            mergeMapView( view );

            mappingService.addMapView( view );
        }

        mappingService.addMap( map );

        ContextUtils.createdResponse( response, "Map created", RESOURCE_PATH + "/" + map.getUid() );
    }

    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        Map map = mappingService.getMap( uid );

        if ( map == null )
        {
            ContextUtils.notFoundResponse( response, "Map does not exist: " + uid );
            return;
        }

        Iterator<MapView> views = map.getMapViews().iterator();

        while ( views.hasNext() )
        {
            MapView view = views.next();
            views.remove();
            mappingService.deleteMapView( view );
        }

        Map newMap = JacksonUtils.fromJson( input, Map.class );

        mergeMap( newMap );

        for ( MapView view : newMap.getMapViews() )
        {
            mergeMapView( view );

            mappingService.addMapView( view );
        }

        map.mergeWith( newMap );

        if ( newMap.getUser() == null )
        {
            map.setUser( null );
        }

        mappingService.updateMap( map );
    }

    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid ) throws Exception
    {
        Map map = mappingService.getMap( uid );

        if ( map == null )
        {
            ContextUtils.notFoundResponse( response, "Map does not exist: " + uid );
            return;
        }

        mappingService.deleteMap( map );
    }

    //--------------------------------------------------------------------------
    // Get data
    //--------------------------------------------------------------------------

    @RequestMapping(value = { "/{uid}/data", "/{uid}/data.png" }, method = RequestMethod.GET)
    public void getMapData( @PathVariable String uid, 
        @RequestParam( value = "date", required = false ) @DateTimeFormat( pattern = DATE_PATTERN ) Date date,
        @RequestParam( value = "ou", required = false ) String ou,
        @RequestParam( required = false ) Integer width, 
        @RequestParam( required = false ) Integer height, 
        HttpServletResponse response ) throws Exception
    {
        Map map = mappingService.getMapNoAcl( uid );

        if ( map == null )
        {
            ContextUtils.notFoundResponse( response, "Map does not exist: " + uid );
            return;
        }

        OrganisationUnit unit = ou != null ? organisationUnitService.getOrganisationUnit( ou ) : null;
        
        renderMapViewPng( map, date, unit, width, height, response );
    }

    //--------------------------------------------------------------------------
    // Hooks
    //--------------------------------------------------------------------------

    @Override
    public void postProcessEntity( Map map ) throws Exception
    {
        I18nFormat format = i18nManager.getI18nFormat();
        
        for ( MapView view : map.getMapViews() )
        {
            view.populateAnalyticalProperties();
            
            for ( OrganisationUnit organisationUnit : view.getOrganisationUnits() )
            {
                view.getParentGraphMap().put( organisationUnit.getUid(), organisationUnit.getParentGraph() );
            }
            
            if ( view.getPeriods() != null && !view.getPeriods().isEmpty() )
            {   
                for ( Period period : view.getPeriods() )
                {
                    period.setName( format.formatPeriod( period ) );
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    private void mergeMap( Map map )
    {
        if ( map.getUser() != null )
        {
            map.setUser( userService.getUser( map.getUser().getUid() ) );
        }
        else
        {
            map.setUser( currentUserService.getCurrentUser() );
        }
    }

    private void mergeMapView( MapView view )
    {
        dimensionService.mergeAnalyticalObject( view );
        
        if ( view.getLegendSet() != null )
        {
            view.setLegendSet( mappingService.getMapLegendSet( view.getLegendSet().getUid() ) );
        }

        if ( view.getOrganisationUnitGroupSet() != null )
        {
            view.setOrganisationUnitGroupSet( organisationUnitGroupService.getOrganisationUnitGroupSet( view.getOrganisationUnitGroupSet().getUid() ) );
        }
    }

    private void renderMapViewPng( Map map, Date date, OrganisationUnit unit, Integer width, Integer height, HttpServletResponse response )
        throws Exception
    {
        BufferedImage image = mapGenerationService.generateMapImage( map, date, unit, width, height );

        if ( image != null )
        {
            contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PNG, CacheStrategy.RESPECT_SYSTEM_SETTING, "map.png", false );

            ImageIO.write( image, "PNG", response.getOutputStream() );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_NO_CONTENT );
        }
    }
}
