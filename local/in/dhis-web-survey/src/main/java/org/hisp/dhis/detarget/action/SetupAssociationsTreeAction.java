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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detarget.DeTargetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version SetupAssociationsTreeAction.java Jan 15, 2011 11:08:15 AM
 */
public class SetupAssociationsTreeAction implements Action
{
    

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private DeTargetService deTargetService;
    
    public void setDeTargetService( DeTargetService deTargetService )
    {
        this.deTargetService = deTargetService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private int deTargetId;
    
   
    
    public int getDeTargetId()
    {
        return deTargetId;
    }

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
   

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        deTarget = deTargetService.getDeTarget( deTargetId );
        
        selectionTreeManager.setSelectedOrganisationUnits( deTarget.getSources() );

        return SUCCESS;
    }
}

