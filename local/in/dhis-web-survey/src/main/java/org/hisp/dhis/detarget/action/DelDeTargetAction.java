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

import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detarget.DeTargetService;
import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DelDeTargetAction.java Jan 15, 2011 5:07:57 PM
 */
public class DelDeTargetAction implements Action
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
    // Getters & setters
    // -------------------------------------------------------------------------

   
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    
    private int deTargetId;

    public void setDeTargetId( int deTargetId )
    {
        this.deTargetId = deTargetId;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private String status;

    public String getStatus()
    {
        return status;
    }
    /*
    private List<DeTargetMember>  deTargetMemberList;
    
    public List<DeTargetMember> getDeTargetMemberList()
    {
        return deTargetMemberList;
    }*/
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        status = "success";
        
        
        
        DeTarget deTarget = deTargetService.getDeTarget( deTargetId );
        
        deTargetService.deleteDeTargetMembers( deTarget );//delete target Members(that is delete dataElements )
       
        //deTargetMemberList = new ArrayList<DeTargetMember>(deTargetService.getDeTargetMembers( deTarget ));
       
        /*
        for( DeTargetMember dataElementTarget : deTargetMemberList )
        {
            
            deTargetService.deleteDeTargetMember( dataElementTarget );
            //deTargetService.
            //selectedDeTargetMember.add( dataElementTarget.getDataelements().getId()+":" + dataElementTarget.getDecategoryOptionCombo().getId() );
        }
        
        */
        
       // DeTarget deTarget = deTargetService.getDeTarget( deTargetId );
        
        
        int flag = deTargetService.deleteDeTarget( deTarget ); // delete deTarget
       
        System.out.println( " Delete flag is flag is " + flag );
        if ( flag < 0 )
        {
            status = "error";
            String delMseg = i18n.getString( "not_del_contain_data" );
            message = deTarget.getName() + " : "+  delMseg;

            return ERROR;
        }
        
        return SUCCESS;
    }
}
