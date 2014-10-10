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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.smscommand.SMSCode;
import org.hisp.dhis.smscommand.SMSCommand;
import org.hisp.dhis.smscommand.SMSCommandService;
import org.hisp.dhis.smscommand.SMSSpecialCharacter;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.Action;

public class EditSMSCommandForm
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

    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private String name;

    private int selectedDataSetID;
    
    private Integer userGroupID;

    private String codeDataelementOption;
    
    private String specialCharactersInfo;

    private String separator;

    private String codeSeparator;

    private String defaultMessage;
    
    private String receivedMessage;

    private int selectedCommandID = -1;

    private boolean currentPeriodUsedForReporting = false;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        Set<SMSCode> codeSet = new HashSet<SMSCode>();

        @SuppressWarnings( "unchecked" )
        List<JSONObject> jsonCodes = (List<JSONObject>) JSONObject.fromObject( codeDataelementOption ).get( "codes" );
        for ( JSONObject x : jsonCodes )
        {
            SMSCode c = new SMSCode();
            c.setCode( x.getString( "code" ) );
            c.setDataElement( dataElementService.getDataElement( x.getInt( "dataElementId" ) ) );
            c.setOptionId( x.getInt( "optionId" ) );
            codeSet.add( c );
        }
        
        @SuppressWarnings( "unchecked" )
        List<JSONObject> jsonSpecialCharacters = (List<JSONObject>) JSONObject.fromObject( specialCharactersInfo ).get( "specialCharacters" );
        Set<SMSSpecialCharacter> specialCharacterSet = new HashSet<SMSSpecialCharacter>();
        for ( JSONObject x : jsonSpecialCharacters )
        {
            String name = x.getString( "name" );
            String value = x.getString( "value" );
            SMSSpecialCharacter smsSpecialCharacter = new SMSSpecialCharacter( name, value );
            specialCharacterSet.add( smsSpecialCharacter );
        }
        smsCommandService.saveSpecialCharacterSet( specialCharacterSet );

        if ( codeSet.size() > 0 )
        {
            smsCommandService.save( codeSet );
        }

        SMSCommand c = getSMSCommand();
        
        if ( selectedDataSetID > -1 && c != null )
        {
            c.setCurrentPeriodUsedForReporting( currentPeriodUsedForReporting );
            c.setName( name );
            c.setSeparator( separator );
            
            //remove codes
            Set<SMSCode> toRemoveCodes = c.getCodes();
            smsCommandService.deleteCodeSet( toRemoveCodes );
            
            //remove special characters
            Set<SMSSpecialCharacter> toRemoveCharacters = c.getSpecialCharacters();
            smsCommandService.deleteSpecialCharacterSet( toRemoveCharacters );
            
            c.setCodes( codeSet );
            c.setDefaultMessage( defaultMessage );
            c.setReceivedMessage( receivedMessage );
            if( userGroupID != null && userGroupID > -1 )
            {
                c.setUserGroup( userGroupService.getUserGroup( userGroupID ) );
            }
            c.setSpecialCharacters( specialCharacterSet );
            smsCommandService.save( c );
        }

        return SUCCESS;
    }

    public Collection<DataSet> getDataSets()
    {
        return dataSetService.getAllDataSets();
    }

    public Set<DataElement> getDataSetElements()
    {
        DataSet d = dataSetService.getDataSet( selectedDataSetID );
        if ( d != null )
        {
            return d.getDataElements();
        }
        return null;
    }

    public SMSCommand getSMSCommand()
    {
        return smsCommandService.getSMSCommand( selectedCommandID );
    }


    public int getSelectedDataSetID()
    {
        return selectedDataSetID;
    }

    public void setSelectedDataSetID( int selectedDataSetID )
    {
        this.selectedDataSetID = selectedDataSetID;
    }

    public String getCodeDataelementOption()
    {
        return codeDataelementOption;
    }

    public void setCodeDataelementOption( String codeDataelementOption )
    {
        this.codeDataelementOption = codeDataelementOption;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public int getSelectedCommandID()
    {
        return selectedCommandID;
    }

    public void setSelectedCommandID( int selectedCommandID )
    {
        this.selectedCommandID = selectedCommandID;
    }

    public String getSeparator()
    {
        return separator;
    }

    public void setSeparator( String separator )
    {
        this.separator = separator;
    }

    public String getCodeSeparator()
    {
        return codeSeparator;
    }

    public void setCodeSeparator( String codeSeparator )
    {
        this.codeSeparator = codeSeparator;
    }

    public String getDefaultMessage()
    {
        return defaultMessage;
    }

    public void setDefaultMessage( String defaultMessage )
    {
        this.defaultMessage = defaultMessage;
    }

    public boolean isCurrentPeriodUsedForReporting()
    {
        return currentPeriodUsedForReporting;
    }

    public void setCurrentPeriodUsedForReporting( String currentPeriodUsedForReporting )
    {
        if ( !StringUtils.isEmpty( currentPeriodUsedForReporting ) )
        {
            this.currentPeriodUsedForReporting = true;
        }
        else
        {
            this.currentPeriodUsedForReporting = false;
        }
    }

    public void setUserGroupID( Integer userGroupID )
    {
        this.userGroupID = userGroupID;
    }

    public void setReceivedMessage( String receivedMessage )
    {
        this.receivedMessage = receivedMessage;
    }

    public String getSpecialCharactersInfo()
    {
        return specialCharactersInfo;
    }

    public void setSpecialCharactersInfo( String specialCharactersInfo )
    {
        this.specialCharactersInfo = specialCharactersInfo;
    }
    
}