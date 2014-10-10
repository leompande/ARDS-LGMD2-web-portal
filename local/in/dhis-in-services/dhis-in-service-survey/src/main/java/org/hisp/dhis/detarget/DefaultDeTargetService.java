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
package org.hisp.dhis.detarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DefaultDeTargetService.java Jan 13, 2011 10:34:54 AM
 */
@Transactional
public class DefaultDeTargetService implements DeTargetService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DeTargetStore deTargetStore;

    public void setDeTargetStore( DeTargetStore deTargetStore )
    {
        this.deTargetStore = deTargetStore;
    }

    // -------------------------------------------------------------------------
    // DeTarget
    // -------------------------------------------------------------------------

    public int addDeTarget( DeTarget deTarget )
    {
        return deTargetStore.addDeTarget( deTarget );
    }

    public void updateDeTarget( DeTarget deTarget )
    {
         deTargetStore.updateDeTarget( deTarget );
    }
    
    public int deleteDeTarget( DeTarget deTarget )
    {
        return deTargetStore.deleteDeTarget( deTarget );
    }

    public DeTarget getDeTarget( int id )
    {
        return deTargetStore.getDeTarget( id );
    }
    
 
    public DeTarget getDeTargetByName( String name )
    {
        return deTargetStore.getDeTargetByName( name );
    }
    
    public DeTarget getDeTargetByShortName( String shortName )
    {
        return deTargetStore.getDeTargetByShortName( shortName );
    }
    
    public Collection<DeTarget> getDeTargetBySource( OrganisationUnit source )
    {
        return deTargetStore.getDeTargetsBySource( source );
    }
    
    public Collection<DeTargetMember> getDeTargetsByDataElementAndCategoryOptionCombo( DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo )
    {
        return deTargetStore.getDeTargetsByDataElementAndCategoryOptionCombo( dataelement, deoptioncombo );
    }
    
    public Collection<DeTarget> getAllDeTargets()
    {
        return deTargetStore.getAllDeTargets();
    }

    // -------------------------------------------------------------------------
    // DeTargetMember
    // -------------------------------------------------------------------------
    
    public Collection<DeTargetMember> getDeTargetMembers( DeTarget deTarget )
    {
        return deTargetStore.getDeTargetMembers( deTarget );
    }
    
    public void addDeTargetMember( DeTargetMember  deTargetMember )
    {
        deTargetStore.addDeTargetMember( deTargetMember );
    }
    
    
    public void updateDeTargetMember( DeTargetMember deTargetMember )
    {
        deTargetStore.updateDeTargetMember( deTargetMember );
    }
 /*  
    public int deleteDeTargetMember( DeTarget deTarget ,DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo )
     {
         return deTargetStore.deleteDeTargetMember( deTarget , dataelement, deoptioncombo );
     }
 */   
    
    public int deleteDeTargetMember( DeTargetMember  deTargetMember )
    {
        return deTargetStore.deleteDeTargetMember( deTargetMember );
    }
    
    public void deleteDeTargetMembers( DeTarget deTarget )
    {
        List<DeTargetMember>  deTargetMemberList = new ArrayList<DeTargetMember>( getDeTargetMembers( deTarget ));
        
        for( DeTargetMember dataElementTarget : deTargetMemberList )
        {
            
            deleteDeTargetMember( dataElementTarget );
            //deTargetService.
            //selectedDeTargetMember.add( dataElementTarget.getDataelements().getId()+":" + dataElementTarget.getDecategoryOptionCombo().getId() );
        }
        
    }
    
    
}

