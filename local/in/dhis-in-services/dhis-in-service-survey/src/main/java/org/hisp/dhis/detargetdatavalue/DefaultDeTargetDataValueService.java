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
package org.hisp.dhis.detargetdatavalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DefaultDeTargetDataValueService.java Jan 13, 2011 10:36:40 AM
 */
@Transactional
public class DefaultDeTargetDataValueService implements DeTargetDataValueService
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DeTargetDataValueStore deTargetDataValueStore;

    public void setDeTargetDataValueStore( DeTargetDataValueStore deTargetDataValueStore )
    {
        this.deTargetDataValueStore = deTargetDataValueStore;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Basic DeTargetDataValue
    // -------------------------------------------------------------------------
    
    public void addDeTargetDataValue( DeTargetDataValue deTargetDataValue )
    {
        if ( deTargetDataValue.getValue() != null )
        {
            deTargetDataValueStore.addDeTargetDataValue( deTargetDataValue );
        }
    }
    
    public void updateDeTargetDataValue( DeTargetDataValue deTargetDataValue )
    {
        deTargetDataValueStore.updateDeTargetDataValue( deTargetDataValue );
    }

    public void deleteDeTargetDataValue( DeTargetDataValue deTargetDataValue )
    {
        deTargetDataValueStore.deleteDeTargetDataValue( deTargetDataValue );
    }
        
    public  int deleteDeTargetDataValuesBySource( OrganisationUnit source )
    {
        return deTargetDataValueStore.deleteDeTargetDataValuesBySource( source );
    }
    
    public int deleteDeTargetDataValuesByDeTarget( DeTarget deTarget )
    {
        return deTargetDataValueStore.deleteDeTargetDataValuesByDeTarget( deTarget );
    }
    
    public int deleteDeTargetDataValuesByDataElementAndCategoryOptionCombo( DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo )
    {
        return deTargetDataValueStore.deleteDeTargetDataValuesByDataElementAndCategoryOptionCombo( dataelement, deoptioncombo );
    }
    
    public int deleteDeTargetDataValuesByDeTargetDataElementCategoryOptionComboAndSource( DeTarget deTarget, DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo, OrganisationUnit source )
    {
        return deTargetDataValueStore.deleteDeTargetDataValuesByDeTargetDataElementCategoryOptionComboAndSource( deTarget, dataelement, deoptioncombo, source );
    }
 /*   
    public DeTargetDataValue getDeTargetDataValue( Source source, DeTarget deTarget )
    {
        return deTargetDataValueStore.getDeTargetDataValue( source, deTarget );
    }
 */   
    public Collection<DeTargetDataValue> getAllDeTargetDataValues()
    {
        return deTargetDataValueStore.getAllDeTargetDataValues();
    }
    
    public Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source )
    {
        return deTargetDataValueStore.getDeTargetDataValues( source );
    }
   
    public Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source, DeTarget deTarget )
    {
        return deTargetDataValueStore.getDeTargetDataValues( source, deTarget );
    }
    
    public Collection<DeTargetDataValue> getDeTargetDataValues( Collection<OrganisationUnit> sources, DeTarget deTarget )
    {
        return deTargetDataValueStore.getDeTargetDataValues( sources, deTarget );
    }
    
   public Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source, Collection<DeTarget> deTargets )
   {
       return deTargetDataValueStore.getDeTargetDataValues( source, deTargets );
   }
   
   public Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget, Collection<OrganisationUnit> sources )
   {
       return deTargetDataValueStore.getDeTargetDataValues( deTarget, sources );
   }
    
   public Collection<DeTargetDataValue> getDeTargetDataValues( Collection<DeTarget> deTargets,  Collection<OrganisationUnit> sources, int firstResult, int maxResults )
   {
       return deTargetDataValueStore.getDeTargetDataValues( deTargets, sources, firstResult, maxResults );
   }
    
   public Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget )
   {
       return deTargetDataValueStore.getDeTargetDataValues( deTarget );
   }
   
   public Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source, DeTarget deTarget, DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo )
   {
       return deTargetDataValueStore.getDeTargetDataValues( source, deTarget, dataelement, deoptioncombo );
   }

   public DeTargetDataValue getDeTargetDataValue( OrganisationUnit source, DeTarget deTarget ,Period period, DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo )
   {
       return deTargetDataValueStore.getDeTargetDataValue( source, deTarget, period, dataelement, deoptioncombo );
   }
   
   
   /*   
   public Collection<DeTargetDataValue> getDeTargetMemberDataValues( DeTargetMember deTargetMember ,DataElement dataelement ,DataElementCategoryOptionCombo decategoryOptionCombo )
   {
       return deTargetDataValueStore.getDeTargetMemberDataValues( deTargetMember ,dataelement ,decategoryOptionCombo );
   }
*/
   public Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget ,DataElement dataelement ,DataElementCategoryOptionCombo decategoryOptionCombo )
   {
       return deTargetDataValueStore.getDeTargetDataValues( deTarget ,dataelement ,decategoryOptionCombo );
   }
   

   public Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget, OrganisationUnit source, Period period)
   {
        return deTargetDataValueStore.getDeTargetDataValues( deTarget ,source , period );
   }
   
   public Double getAggregatedDeTargetDataValue( OrganisationUnit source, DeTarget deTarget,Period period, DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo )
   {
       List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
       childOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( source.getId() ) );
       
       Double aggDataValue = 0.0;
       
       for( OrganisationUnit orgChild : childOrgUnitList )
       {
           DeTargetDataValue tempAggDataValue = getDeTargetDataValue(  orgChild,  deTarget,  period, dataelement , deoptioncombo );
           
           if( tempAggDataValue != null && tempAggDataValue.getValue() != null ) 
           {
               try
               {
                   aggDataValue += Double.parseDouble( tempAggDataValue.getValue() );
               }
               catch( Exception e )
               {
                   
               }
           }
               
       }
       
       return aggDataValue;
   }
}
