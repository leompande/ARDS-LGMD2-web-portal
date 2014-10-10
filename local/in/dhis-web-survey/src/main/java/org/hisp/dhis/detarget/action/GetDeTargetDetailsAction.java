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
import java.util.List;

import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detarget.DeTargetMember;
import org.hisp.dhis.detarget.DeTargetService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetDeTargetDetailsAction.java Jan 13, 2011 4:25:23 PM
 */
public class GetDeTargetDetailsAction   implements Action
{
   
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DeTargetService deTargetService;
    
    public void setDeTargetService( DeTargetService deTargetService )
    {
        this.deTargetService = deTargetService;
    }
    

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

  
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

    public void setDeTarget( DeTarget deTarget )
    {
        this.deTarget = deTarget;
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
    
    private Integer dataElementSize;
    
    public Integer getDataElementSize()
    {
        return dataElementSize;
    }
    
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

 

    public String execute()
        throws Exception
    {
        //System.out.println( " DeTarget ID : "  + deTargetId );
        
        deTarget = deTargetService.getDeTarget( deTargetId );
       // survey = surveyService.getSurvey( surveyId );
        
        List<DeTargetMember> targetDataElements = new ArrayList<DeTargetMember>( deTargetService.getDeTargetMembers( deTarget ) );
        
        if ( targetDataElements == null || targetDataElements.size() == 0 )
        {
            dataElementSize = 0;
        }
        else
        {
            dataElementSize = targetDataElements.size();
           
        }
        
       // System.out.println( " DeTarget Id : "  + deTarget.getId() + " DETarget Name "  + deTarget.getName() + " DETarget Description "  + deTarget.getDescription()  );
       // System.out.println( " DeTarget URL : "  + deTarget.getUrl() + " dataElementSize "  +dataElementSize );
        
        //surveyIndicators = new ArrayList<Indicator>( survey.getIndicators() );
        
        //Collections.sort( surveyIndicators, dataElementComparator );       
                        
        //displayPropertyHandler.handle( dataSetDataElements );

        //dataEntryForm = dataEntryFormService.getDataEntryFormByDataSet( dataSet );
        
        return SUCCESS;
    }
}
