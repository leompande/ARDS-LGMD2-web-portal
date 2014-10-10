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

import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.mapping.MapLegend;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = MapLegendSetController.RESOURCE_PATH )
public class MapLegendSetController
    extends AbstractCrudController<MapLegendSet>
{
    public static final String RESOURCE_PATH = "/mapLegendSets";

    @Autowired
    private MappingService mappingService;
    
    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    @PreAuthorize( "hasRole('F_GIS_ADMIN') or hasRole('ALL')" )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        MapLegendSet legendSet = JacksonUtils.fromJson( input, MapLegendSet.class );
        
        for ( MapLegend legend : legendSet.getMapLegends() )
        {
            mappingService.addMapLegend( legend );
        }
        
        mappingService.addMapLegendSet( legendSet );
        
        ContextUtils.createdResponse( response, "Map legend set created", RESOURCE_PATH + "/" + legendSet.getUid() );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    @PreAuthorize( "hasRole('F_GIS_ADMIN') or hasRole('ALL')" )
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        MapLegendSet legendSet = mappingService.getMapLegendSet( uid );
        
        if ( legendSet == null )
        {
            ContextUtils.notFoundResponse( response, "Map legend set does not exist: " + uid );
            return;
        }

        Iterator<MapLegend> legends = legendSet.getMapLegends().iterator();

        while ( legends.hasNext() )
        {
            MapLegend legend = legends.next();            
            legends.remove();            
            mappingService.deleteMapLegend( legend );
        }

        MapLegendSet newLegendSet = JacksonUtils.fromJson( input, MapLegendSet.class );
        
        for ( MapLegend legend : newLegendSet.getMapLegends() )
        {
            mappingService.addMapLegend( legend );
        }
        
        legendSet.mergeWith( newLegendSet );
        
        mappingService.updateMapLegendSet( legendSet );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    @PreAuthorize( "hasRole('F_GIS_ADMIN') or hasRole('ALL')" )
    public void deleteObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid ) throws Exception
    {
        MapLegendSet legendSet = mappingService.getMapLegendSet( uid );
        
        if ( legendSet == null )
        {
            ContextUtils.notFoundResponse( response, "Map legend set does not exist: " + uid );
            return;
        }

        Iterator<MapLegend> legends = legendSet.getMapLegends().iterator();

        while ( legends.hasNext() )
        {
            MapLegend legend = legends.next();            
            legends.remove();            
            mappingService.deleteMapLegend( legend );
        }
        
        mappingService.deleteMapLegendSet( legendSet );
    }
}
