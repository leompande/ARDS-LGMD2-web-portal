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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.dxf2.metadata.ImportSummary;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.scheduling.TaskCategory;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.SystemInfo;
import org.hisp.dhis.system.SystemService;
import org.hisp.dhis.system.notification.Notification;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.scheduling.Scheduler;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping(value = SystemController.RESOURCE_PATH)
public class SystemController
{
    public static final String RESOURCE_PATH = "/system";

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SystemService systemService;
    
    @Autowired
    private Notifier notifier;
    
    //--------------------------------------------------------------------------
    // UID Generator
    //--------------------------------------------------------------------------

    @RequestMapping( value = { "/uid", "/id" }, method = RequestMethod.GET )
    public void getUid( @RequestParam(required = false) Integer n, HttpServletResponse response ) throws IOException
    {
        response.setContentType( ContextUtils.CONTENT_TYPE_JSON );

        List<String> codes = new ArrayList<String>();

        if ( n == null )
        {
            codes.add( CodeGenerator.generateCode() );
        }
        else
        {
            if ( n > 10000 )
            {
                n = 10000;
            }

            for ( int i = 0; i < n; i++ )
            {
                codes.add( CodeGenerator.generateCode() );
            }
        }

        JacksonUtils.toJson( response.getOutputStream(), codes );
    }

    @RequestMapping( value = "/tasks/{category}", method = RequestMethod.GET, produces = { "*/*", "application/json" } )
    public void getTaskJson( @PathVariable("category") String category, 
        @RequestParam(required=false) String lastId, HttpServletResponse response ) throws IOException
    {
        List<Notification> notifications = new ArrayList<Notification>();

        if ( category != null )
        {
            TaskCategory taskCategory = TaskCategory.valueOf( category.toUpperCase() );

            TaskId taskId = new TaskId( taskCategory, currentUserService.getCurrentUser() );

            notifications = notifier.getNotifications( taskId, lastId );
        }

        JacksonUtils.toJson( response.getOutputStream(), notifications );
    }

    @RequestMapping( value = "/taskSummaries/{category}", method = RequestMethod.GET, produces = { "*/*", "application/json" } )
    public void getTaskSummaryJson( HttpServletResponse response, @PathVariable("category") String category ) throws IOException
    {
        ImportSummary importSummary = new ImportSummary();

        if ( category != null )
        {
            TaskCategory taskCategory = TaskCategory.valueOf( category.toUpperCase() );

            TaskId taskId = new TaskId( taskCategory, currentUserService.getCurrentUser() );

            importSummary = (ImportSummary) notifier.getTaskSummary( taskId );

            notifier.clear( taskId );
        }

        JacksonUtils.toJson( response.getOutputStream(), importSummary );
    }
    
    @RequestMapping( value = "/info", method = RequestMethod.GET, produces = { "application/json", "application/javascript" } )
    public String getSystemInfo( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        SystemInfo info = systemService.getSystemInfo();
        
        info.setContextPath( ContextUtils.getContextPath( request ) );
        info.setUserAgent( request.getHeader( ContextUtils.HEADER_USER_AGENT ) );
        
        if ( !currentUserService.currentUserIsSuper() )
        {
            info.clearSensitiveInfo();
        }

        model.addAttribute( "model", info );
        
        return "info";
    }
    
    @RequestMapping( value = "/context", method = RequestMethod.GET, produces = { "application/json", "application/javascript" } )
    public String getContextInfo( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        SystemInfo info = new SystemInfo();

        info.setContextPath( ContextUtils.getContextPath( request ) );
        info.setUserAgent( request.getHeader( ContextUtils.HEADER_USER_AGENT ) );

        model.addAttribute( "model", info );
        
        return "info";
    }
    
    @RequestMapping( value = "/ping", method = RequestMethod.GET, produces = "text/plain" )
    public @ResponseBody String ping( Model model )
    {
        return "pong";
    }
}
