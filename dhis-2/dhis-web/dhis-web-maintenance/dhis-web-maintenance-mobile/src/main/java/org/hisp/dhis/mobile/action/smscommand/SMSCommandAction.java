package org.hisp.dhis.mobile.action.smscommand;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.comparator.DataElementSortOrderComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.sms.parse.ParserType;
import org.hisp.dhis.smscommand.SMSCode;
import org.hisp.dhis.smscommand.SMSCommand;
import org.hisp.dhis.smscommand.SMSCommandService;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.Action;

public class SMSCommandAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SMSCommandService smsCommandService;

    public void setSmsCommandService( SMSCommandService smsCommandService )
    {
        this.smsCommandService = smsCommandService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private SMSCommand smsCommand;

    private List<DataElement> dataElements;

    private List<UserGroup> userGroupList;

    public List<UserGroup> getUserGroupList()
    {
        return userGroupList;
    }

    public List<Program> anonymousProgramList;

    private int selectedCommandID = -1;

    public int getSelectedCommandID()
    {
        return selectedCommandID;
    }

    public void setSelectedCommandID( int selectedCommandID )
    {
        this.selectedCommandID = selectedCommandID;
    }

    private Map<String, String> codes = new HashMap<String, String>();

    public Map<String, String> getCodes()
    {
        return codes;
    }

    public void setCodes( Map<String, String> codes )
    {
        this.codes = codes;
    }

    public ParserType[] getParserType()
    {
        return ParserType.values();
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( selectedCommandID > -1 )
        {
            smsCommand = smsCommandService.getSMSCommand( selectedCommandID );
        }

        if ( smsCommand != null && smsCommand.getCodes() != null )
        {
            for ( SMSCode x : smsCommand.getCodes() )
            {
                codes.put( "" + x.getDataElement().getId() + x.getOptionId(), x.getCode() );
            }
        }
        userGroupList = new ArrayList<UserGroup>( userGroupService.getAllUserGroups() );
        anonymousProgramList = new ArrayList<Program>(
            programService.getPrograms( Program.SINGLE_EVENT_WITHOUT_REGISTRATION ) );
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    public List<DataElement> getDataElements()
    {
        if ( smsCommand != null )
        {
            DataSet d = smsCommand.getDataset();
            if ( d != null )
            {
                dataElements = new ArrayList<DataElement>( d.getDataElements() );
                Collections.sort( dataElements, new DataElementSortOrderComparator() );
                return dataElements;
            }
        }
        return null;
    }

    public Collection<DataSet> getDataSets()
    {
        return dataSetService.getAllDataSets();
    }

    public Collection<SMSCommand> getSMSCommands()
    {
        return smsCommandService.getSMSCommands();
    }

    public SMSCommand getSmsCommand()
    {
        return smsCommand;
    }
}
