package org.hisp.dhis.api.controller.user;

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

import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.controller.WebMetaData;
import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = UserController.RESOURCE_PATH )
public class UserController
    extends AbstractCrudController<User>
{
    public static final String RESOURCE_PATH = "/users";

    @Autowired
    private UserService userService;

    @Override
    @PreAuthorize( "hasRole('ALL') or hasRole('F_USER_VIEW')" )
    public String getObjectList( @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request ) throws Exception
    {
        return super.getObjectList( parameters, model, request );
    }

    @Override
    @PreAuthorize( "hasRole('ALL') or hasRole('F_USER_VIEW')" )
    public String getObject( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters, Model model,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        return super.getObject( uid, parameters, model, request, response );
    }

    @Override
    protected List<User> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<User> entityList;

        Date lastUpdated = options.getLastUpdated();

        if ( lastUpdated != null )
        {
            entityList = new ArrayList<User>( userService.getUsersByLastUpdated( lastUpdated ) );
        }
        else if ( options.hasPaging() )
        {
            int count = userService.getUserCount();

            Pager pager = new Pager( options.getPage(), count );
            metaData.setPager( pager );

            entityList = new ArrayList<User>( userService.getAllUsersBetween( pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = new ArrayList<User>( userService.getAllUsers() );
        }

        return entityList;
    }

    @Override
    protected User getEntity( String uid )
    {
        return userService.getUser( uid );
    }

    //--------------------------------------------------------------------------
    // Overrides
    //--------------------------------------------------------------------------

    @Override
    public User searchForEntity( Class<User> clazz, String query )
    {
        return userService.searchForUser( query );
    }
    
    @Override
    public List<User> queryForList( Class<User> clazz, String query )
    {
        return userService.queryForUsers( query );
    }
}
