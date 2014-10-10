package org.hisp.dhis.reportsheet.state;

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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.datavalue.DataValue;

import com.opensymphony.xwork2.ActionContext;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class DefaultSelectionManager
    implements SelectionManager
{
    private static final String SESSION_KEY_FILE_DOWNLOAD = "SESSION_KEY_FILE_DOWNLOAD";

    private static final String SESSION_KEY_FILE_UPLOAD = "SESSION_KEY_FILE_UPLOAD";

    private static final String SESSION_KEY_FILE_RENAME = "SESSION_KEY_FILE_RENAME";

    private static final String SESSION_KEY_SELECTED_REPORT_ID = "SESSION_KEY_SELECTED_REPORT_ID";

    private static final String SESSION_KEY_SELECTED_PERIOD_ID = "SESSION_KEY_SELECTED_PERIOD_ID";

    private static final String SESSION_KEY_BOOKMARK_TYPE = "SESSION_KEY_BOOKMARK_TYPE";

    private static final String SESSION_KEY_LIST_OBJECT = "SESSION_KEY_LIST_OBJECT";

    private static final String SESSION_KEY_LIST_ORDERED_GROUP = "SESSION_KEY_LIST_ORDERED_GROUP";

    private static final String SESSION_KEY_OLD_DATA_VALUE_LIST = "SESSION_KEY_OLD_DATA_VALUE_LIST";

    private static final String SESSION_KEY_NEW_DATA_VALUE_LIST = "SESSION_KEY_NEW_DATA_VALUE_LIST";

    public String getDownloadFilePath()
    {
        return (String) getSession().get( SESSION_KEY_FILE_DOWNLOAD );
    }

    public String getUploadFilePath()
    {
        return (String) getSession().get( SESSION_KEY_FILE_UPLOAD );
    }

    @SuppressWarnings( "unchecked" )
    public void setDownloadFilePath( String path )
    {
        getSession().put( SESSION_KEY_FILE_DOWNLOAD, path );
    }

    @SuppressWarnings( "unchecked" )
    public void setUploadFilePath( String path )
    {
        getSession().put( SESSION_KEY_FILE_UPLOAD, path );
    }

    @SuppressWarnings( "unchecked" )
    private static final Map getSession()
    {
        return ActionContext.getContext().getSession();
    }

    public Integer getSelectedReportId()
    {
        return (Integer) getSession().get( SESSION_KEY_SELECTED_REPORT_ID );
    }

    @SuppressWarnings( "unchecked" )
    public void setSelectedReportId( Integer id )
    {
        getSession().put( SESSION_KEY_SELECTED_REPORT_ID, id );
    }

    public String getRenameFilePath()
    {
        return (String) getSession().get( SESSION_KEY_FILE_RENAME );
    }

    @SuppressWarnings( "unchecked" )
    public void setRenameFilePath( String path )
    {
        getSession().put( SESSION_KEY_FILE_RENAME, path );
    }

    public String getBookmarkType()
    {
        return (String) getSession().get( SESSION_KEY_BOOKMARK_TYPE );
    }

    @SuppressWarnings( "unchecked" )
    public void setBookmarkType( String type )
    {
        getSession().put( SESSION_KEY_BOOKMARK_TYPE, type );
    }

    public String[] getListObject()
    {
        return (String[]) getSession().get( SESSION_KEY_LIST_OBJECT );
    }

    @SuppressWarnings( "unchecked" )
    public void setListObject( String[] objects )
    {
        getSession().put( SESSION_KEY_LIST_OBJECT, objects );
    }

    @SuppressWarnings( "unchecked" )
    public List<Integer> getOrderedGroupList()
    {
        return (List<Integer>) getSession().get( SESSION_KEY_LIST_ORDERED_GROUP );
    }

    @SuppressWarnings( "unchecked" )
    public void setOrderedGroupList( List<Integer> objects )
    {
        getSession().put( SESSION_KEY_LIST_ORDERED_GROUP, objects );
    }

    public String getSelectedPeriodIndex()
    {
        return (String) getSession().get( SESSION_KEY_SELECTED_PERIOD_ID );
    }

    @SuppressWarnings( "unchecked" )
    public void setSelectedPeriodIndex( String periodIndex )
    {
        getSession().put( SESSION_KEY_SELECTED_PERIOD_ID, periodIndex );
    }

    @SuppressWarnings("unchecked")
    public Set<DataValue> getNewDataValueList()
    {
        return (Set<DataValue>) getSession().get( SESSION_KEY_NEW_DATA_VALUE_LIST );
    }

    @SuppressWarnings("unchecked")
    public Set<DataValue> getOldDataValueList()
    {
        return (Set<DataValue>) getSession().get( SESSION_KEY_OLD_DATA_VALUE_LIST );
    }

    @SuppressWarnings("unchecked")
    public void setNewDataValueList( Set<DataValue> dataValues )
    {
        getSession().put( SESSION_KEY_NEW_DATA_VALUE_LIST, dataValues );
    }

    @SuppressWarnings("unchecked")
    public void setOldDataValueList( Set<DataValue> dataValues )
    {
        getSession().put( SESSION_KEY_OLD_DATA_VALUE_LIST, dataValues );
    }
}
