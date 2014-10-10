package org.hisp.dhis.reportsheet.datasetcompleted.action;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reportsheet.Bookmark;
import org.hisp.dhis.reportsheet.BookmarkService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */

public class DataSetCompletedReportFormAction
    implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private BookmarkService bookmarkService;

    public void setBookmarkService( BookmarkService bookmarkService )
    {
        this.bookmarkService = bookmarkService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
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

    private List<Bookmark> bookmarks;

    public List<Bookmark> getBookmarks()
    {
        return bookmarks;
    }

    private List<DataSet> dataSets;

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    private Set<PeriodType> periodTypes;

    public Set<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    @Override
    public String execute()
        throws Exception
    {
        bookmarks = new ArrayList<Bookmark>( bookmarkService.getAllBookmark( Bookmark.COMPLETED_REPORT ) );

        dataSets = new ArrayList<DataSet>();

        if ( currentUserService.currentUserIsSuper() )
        {
            dataSets.addAll( dataSetService.getAssignedDataSets() );
        }
        else
        {
            UserCredentials userCredentials = userService.getUserCredentials( currentUserService.getCurrentUser() );

            for ( UserAuthorityGroup userAuthorityGroup : userCredentials.getUserAuthorityGroups() )
            {
                dataSets.addAll( userAuthorityGroup.getDataSets() );
            }
        }

        periodTypes = new HashSet<PeriodType>();

        for ( DataSet dataSet : this.dataSets )
        {
            periodTypes.add( dataSet.getPeriodType() );
        }

        Collections.sort( dataSets, new IdentifiableObjectNameComparator() );

        return SUCCESS;
    }

}
