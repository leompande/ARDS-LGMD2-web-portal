/*
 * Copyright (c) 2004-2012, University of Oslo
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
package org.hisp.dhis.detarget.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detarget.DeTargetMember;
import org.hisp.dhis.detarget.DeTargetService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetDeTargetAction.java Jan 15, 2011 1:16:51 PM
 */
public class GetDeTargetAction  implements Action
{
   
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DeTargetService deTargetService;
    
    public void setDeTargetService( DeTargetService deTargetService )
    {
        this.deTargetService = deTargetService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private List<DataElementGroup> dataElementGroups;
    
    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }
    
    private int deTargetId;

    public void setDeTargetId( int deTargetId )
    {
        this.deTargetId = deTargetId;
    }
    
    private DeTarget deTarget;
    
    public DeTarget getDeTarget()
    {
        return deTarget;
    }

    private List<DataElement> deTargetDataElement;


    public List<DataElement> getDeTargetDataElement()
    {
        return deTargetDataElement;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }
    
    public String getDescription()
    {
        return description;
    }
    private List<DeTargetMember>  deTargetMemberList;
    
    public List<DeTargetMember> getDeTargetMemberList()
    {
        return deTargetMemberList;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

 

    public String execute()
        throws Exception
    {
        deTarget = deTargetService.getDeTarget( deTargetId );
        
        deTargetMemberList = new ArrayList<DeTargetMember>(deTargetService.getDeTargetMembers( deTarget ));
        
        dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );

        Collections.sort( dataElementGroups, new IdentifiableObjectNameComparator() );
        
        
        
        
        
        //deTargetDataElement = new ArrayList<DataElement>( deTarget. );
        
        //Collections.sort( surveyIndicators, dataElementComparator );       
                        
        //displayPropertyHandler.handle( dataSetDataElements );

        //dataEntryForm = dataEntryFormService.getDataEntryFormByDataSet( dataSet );
        
        return SUCCESS;
    }
}
