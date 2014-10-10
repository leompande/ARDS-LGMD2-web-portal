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

import java.util.HashSet;

import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detarget.DeTargetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DefineDeTargetAssociationsAction.java Jan 15, 2011 11:21:06 AM
 */
public class DefineDeTargetAssociationsAction implements Action
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
/*    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
*/
 
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private int deTargetId;
    
    public void setDeTargetId( int deTargetId )
    {
        this.deTargetId = deTargetId;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        DeTarget deTarget = deTargetService.getDeTarget( deTargetId );
        
        deTarget.updateOrganisationUnits( new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() ) );
        
        deTargetService.updateDeTarget( deTarget );
        
        //Collection<OrganisationUnit> rootUnits = selectionTreeManager.getRootOrganisationUnits(); 
        
        //Set<OrganisationUnit> unitsInTheTree = new HashSet<OrganisationUnit>();  
        
        //getUnitsInTheTree( rootUnits, unitsInTheTree ); 
        /*
        DeTarget deTarget = deTargetService.getDeTarget( deTargetId );            
        
        System.out.println( " DeTarget Id : "  + deTarget.getId() + " DETarget Name "  + deTarget.getName() );
        
        Set<OrganisationUnit> assignedSources = deTarget.getSources();
        
        //assignedSources.removeAll( unitsInTheTree );
        
        assignedSources.removeAll( assignedSources );  

        Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager.getReloadedSelectedOrganisationUnits();
        
        assignedSources.addAll( selectedOrganisationUnits );         
        
        deTarget.setSources( assignedSources );
        
        System.out.println( " size of selected Organisation Units : "  + selectedOrganisationUnits.size() + " Size of assigned Sources "  + assignedSources.size() );
        
        deTargetService.updateDeTarget( deTarget );
        */
        return SUCCESS;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
/*    
    private void getUnitsInTheTree( Collection<OrganisationUnit> rootUnits, Set<OrganisationUnit> unitsInTheTree )
    {
        for( OrganisationUnit root : rootUnits )
        {
                unitsInTheTree.add( root );
                getUnitsInTheTree( root.getChildren(), unitsInTheTree );                
        }
    }
    */
}

