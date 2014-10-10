package org.hisp.dhis.reportsheet.jchart.action;

/*
 * Copyright (c) 2004-2011, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.jchart.JChart;
import org.hisp.dhis.jchart.JChartComparator;
import org.hisp.dhis.jchart.JChartSevice;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 */

public class GetALLJchartAction
    implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private JChartSevice jchartService;

    public void setJchartService( JChartSevice jchartService )
    {
        this.jchartService = jchartService;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------
    // Output
    // -------------------------------------------

    private List<JChart> jcharts;

    public List<JChart> getJcharts()
    {
        return jcharts;
    }

    @Override
    public String execute()
        throws Exception
    {

        jcharts = new ArrayList<JChart>();

        List<JChart> jcharts_ = new ArrayList<JChart>( jchartService.getALLJChart() );

        if ( currentUserService.currentUserIsSuper() )
        {
            jcharts.addAll( jcharts_ );
        }
        else
        {
            Set<UserAuthorityGroup> userRoles = userService.getUserCredentials( currentUserService.getCurrentUser() )
                .getUserAuthorityGroups();

            for ( JChart jchart : jcharts_ )
            {
                if ( !CollectionUtils.intersection( jchart.getUserRoles(), userRoles ).isEmpty()
                    || currentUserService.getCurrentUsername().equals( jchart.getStoredby() ) )
                {
                    jcharts.add( jchart );
                }
            }

        }

        Collections.sort( jcharts, new JChartComparator() );

        return SUCCESS;
    }
}
