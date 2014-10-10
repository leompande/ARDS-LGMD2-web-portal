package org.hisp.dhis.api.controller.dataelement;

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

import com.google.common.collect.Lists;
import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.controller.WebMetaData;
import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.WebUtils;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementOperandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping(value = DataElementGroupController.RESOURCE_PATH)
public class DataElementGroupController
    extends AbstractCrudController<DataElementGroup>
{
    public static final String RESOURCE_PATH = "/dataElementGroups";

    @Autowired
    private DataElementOperandService dataElementOperandService;

    @RequestMapping( value = "/{uid}/members", method = RequestMethod.GET )
    public String getMembers( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        DataElementGroup dataElementGroup = getEntity( uid );

        if ( dataElementGroup == null )
        {
            ContextUtils.notFoundResponse( response, "DataElementGroup not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<DataElement> dataElements = Lists.newArrayList( dataElementGroup.getMembers() );

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), dataElements.size(), options.getPageSize() );
            metaData.setPager( pager );
            dataElements = PagerUtils.pageCollection( dataElements, pager );
        }

        metaData.setDataElements( dataElements );

        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }

    @RequestMapping( value = "/{uid}/members/query/{q}", method = RequestMethod.GET )
    public String getMembersByQuery( @PathVariable( "uid" ) String uid, @PathVariable( "q" ) String q,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request,
        HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        DataElementGroup dataElementGroup = getEntity( uid );

        if ( dataElementGroup == null )
        {
            ContextUtils.notFoundResponse( response, "DataElementGroup not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<DataElement> dataElements = Lists.newArrayList();

        for ( DataElement dataElement : dataElementGroup.getMembers() )
        {
            if ( dataElement.getDisplayName().toLowerCase().contains( q.toLowerCase() ) )
            {
                dataElements.add( dataElement );
            }
        }

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), dataElements.size(), options.getPageSize() );
            metaData.setPager( pager );
            dataElements = PagerUtils.pageCollection( dataElements, pager );
        }

        metaData.setDataElements( dataElements );

        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }

    @RequestMapping( value = "/{uid}/operands", method = RequestMethod.GET )
    public String getOperands( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        DataElementGroup dataElementGroup = getEntity( uid );

        if ( dataElementGroup == null )
        {
            ContextUtils.notFoundResponse( response, "DataElementGroup not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<DataElementOperand> dataElementOperands = Lists.newArrayList( dataElementOperandService.getDataElementOperandByDataElementGroup( dataElementGroup ) );

        metaData.setDataElementOperands( dataElementOperands );

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), dataElementOperands.size(), options.getPageSize() );
            metaData.setPager( pager );
            dataElementOperands = PagerUtils.pageCollection( dataElementOperands, pager );
        }

        metaData.setDataElementOperands( dataElementOperands );

        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }

    @RequestMapping( value = "/{uid}/operands/query/{q}", method = RequestMethod.GET )
    public String getOperandsByQuery( @PathVariable( "uid" ) String uid, @PathVariable( "q" ) String q,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request,
        HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        DataElementGroup dataElementGroup = getEntity( uid );

        if ( dataElementGroup == null )
        {
            ContextUtils.notFoundResponse( response, "DataElementGroup not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<DataElementOperand> dataElementOperands = Lists.newArrayList();

        for ( DataElementOperand dataElementOperand : dataElementOperandService.getDataElementOperandByDataElementGroup( dataElementGroup ) )
        {
            if ( dataElementOperand.getDisplayName().toLowerCase().contains( q.toLowerCase() ) )
            {
                dataElementOperands.add( dataElementOperand );
            }
        }

        metaData.setDataElementOperands( dataElementOperands );

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), dataElementOperands.size(), options.getPageSize() );
            metaData.setPager( pager );
            dataElementOperands = PagerUtils.pageCollection( dataElementOperands, pager );
        }

        metaData.setDataElementOperands( dataElementOperands );

        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }
}
