package org.hisp.dhis.light.singleevent.action;

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
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.option.OptionSet;

import com.opensymphony.xwork2.Action;

/**
 * @author Nguyen Kim Lai
 */
public class FindSingleEventOptionSetAction
    implements Action

{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;
 
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String keyword;

    public void setKeyword( String keyword )
    {
        this.keyword = keyword;
    }

    public String getKeyword()
    {
        return keyword;
    }

    private int dataElementIdForSearching;

    public void setDataElementIdForSearching( int dataElementIdForSearching )
    {
        this.dataElementIdForSearching = dataElementIdForSearching;
    }
    
    public int getDataElementIdForSearching()
    {
        return dataElementIdForSearching;
    }

    private List<String> searchingResultList;
    
    public List<String> getSearchingResultList()
    {
        return searchingResultList;
    }
    
    private Integer programId;

    public Integer getProgramId()
    {
        return programId;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private Integer patientId;

    public Integer getPatientId()
    {
        return patientId;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }
    
    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public Integer getOrganisationUnitId()
    {
        return this.organisationUnitId;
    }
    
    private String isEditing;

    public String getIsEditing()
    {
        return isEditing;
    }

    public void setIsEditing( String isEditing )
    {
        this.isEditing = isEditing;
    }
    
    private int programStageInstanceId;

    public int getProgramStageInstanceId()
    {
        return programStageInstanceId;
    }

    public void setProgramStageInstanceId( int programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    @Override
    public String execute()
        throws Exception
    {
        DataElement dataElement = dataElementService.getDataElement( this.dataElementIdForSearching );
        
        OptionSet optionSet = dataElement.getOptionSet();
        
        List<String> optionList = optionSet.getOptions();
        
        searchingResultList = new ArrayList<String>();
        
        for( String each: optionList )
        {
            if(each != null)
            {
                if( each.contains( this.keyword ) )
                {
                    this.searchingResultList.add( each );
                }
            }
        }
        
        return SUCCESS;
    }
}
