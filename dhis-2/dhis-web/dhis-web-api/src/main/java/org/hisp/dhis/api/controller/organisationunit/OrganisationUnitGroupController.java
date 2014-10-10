package org.hisp.dhis.api.controller.organisationunit;

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
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping(value = OrganisationUnitGroupController.RESOURCE_PATH)
public class OrganisationUnitGroupController
    extends AbstractCrudController<OrganisationUnitGroup>
{
    public static final String RESOURCE_PATH = "/organisationUnitGroups";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    // --------------------------------------------------------------------------
    // Methods
    // --------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}/members", method = RequestMethod.GET )
    public String getMembers( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        OrganisationUnitGroup organisationUnitGroup = getEntity( uid );

        if ( organisationUnitGroup == null )
        {
            ContextUtils.notFoundResponse( response, "OrganisationUnitGroup not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<OrganisationUnit> organisationUnits = Lists.newArrayList( organisationUnitGroup.getMembers() );

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), organisationUnits.size(), options.getPageSize() );
            metaData.setPager( pager );
            organisationUnits = PagerUtils.pageCollection( organisationUnits, pager );
        }

        metaData.setOrganisationUnits( organisationUnits );

        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }

    @RequestMapping(value = "/{uid}/members/query/{q}", method = RequestMethod.GET)
    public String getMembersByQuery( @PathVariable("uid") String uid, @PathVariable( "q" ) String q,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request,
        HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        OrganisationUnitGroup organisationUnitGroup = getEntity( uid );

        if ( organisationUnitGroup == null )
        {
            ContextUtils.notFoundResponse( response, "OrganisationUnitGroup not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<OrganisationUnit> organisationUnits = Lists.newArrayList();

        for ( OrganisationUnit organisationUnit : organisationUnitGroup.getMembers() )
        {
            if ( organisationUnit.getDisplayName().toLowerCase().contains( q.toLowerCase() ) )
            {
                organisationUnits.add( organisationUnit );
            }
        }

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), organisationUnits.size(), options.getPageSize() );
            metaData.setPager( pager );
            organisationUnits = PagerUtils.pageCollection( organisationUnits, pager );
        }

        metaData.setOrganisationUnits( organisationUnits );

        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }

    @RequestMapping( value = "/{uid}/members/{orgUnitUid}", method = RequestMethod.POST )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_ORGANISATIONUNIT_ADD')" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void addMember( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid,
        @PathVariable( "orgUnitUid" ) String orgUnitUid ) throws Exception
    {
        OrganisationUnitGroup group = organisationUnitGroupService.getOrganisationUnitGroup( uid );
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( orgUnitUid );

        if ( group.addOrganisationUnit( unit ) )
        {
            organisationUnitGroupService.updateOrganisationUnitGroup( group );
        }
    }

    @RequestMapping( value = "/{uid}/members/{orgUnitUid}", method = RequestMethod.DELETE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_ORGANISATIONUNIT_ADD')" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void removeMember( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid,
        @PathVariable( "orgUnitUid" ) String orgUnitUid ) throws Exception
    {
        OrganisationUnitGroup group = organisationUnitGroupService.getOrganisationUnitGroup( uid );
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( orgUnitUid );

        if ( group.removeOrganisationUnit( unit ) )
        {
            organisationUnitGroupService.updateOrganisationUnitGroup( group );
        }
    }
}
