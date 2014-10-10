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
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detarget.DeTargetMember;
import org.hisp.dhis.detarget.DeTargetService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DataElementListFilterByGroupAction.java Jan 13, 2011 7:09:15 PM
 */
public class DataElementListFilterByGroupAction  implements Action
{
   
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DeTargetService deTargetService;
    
    public void setDeTargetService( DeTargetService deTargetService )
    {
        this.deTargetService = deTargetService;
    }
   
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

 
    private String dataElementGroupId;
    
    public void setDataElementGroupId( String dataElementGroupId )
    {
        this.dataElementGroupId = dataElementGroupId;
    }

    
    /*
    private String selectedDataElements[];


    public void setSelectedDataElements( String[] selectedDataElements )
    {
        this.selectedDataElements = selectedDataElements;
    }
    */
 /*  
    private List<String> selectedList;
    
    public void setSelectedList( List<String> selectedList )
    {
        this.selectedList = selectedList;
    }
    */
    private List<DataElement> dataElements;
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    
    private List<String> optionComboNames;

    public List<String> getOptionComboNames()
    {
        return optionComboNames;
    }

    private List<String> optionComboIds;

    public List<String> getOptionComboIds()
    {
        return optionComboIds;
    }
    
  
    private Integer deTargetId;
    
    public void setDeTargetId( Integer deTargetId )
    {
        this.deTargetId = deTargetId;
    }
    
    private List<String> selectedDeTargetMember;
    
    public List<String> getSelectedDeTargetMember()
    {
        return selectedDeTargetMember;
    }
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------


    public String execute() throws Exception
    {
        
        optionComboIds = new ArrayList<String>();
        optionComboNames = new ArrayList<String>();
        selectedDeTargetMember = new ArrayList<String>();
        
        //System.out.println(" deTarget Id is    : " + deTargetId  );
        
        if (  dataElementGroupId == null || dataElementGroupId.equalsIgnoreCase( "ALL" ) )
        {
            //System.out.println("\n\n +++ \n Inside dataElementGroupId null dataElementGroup Id is   : " + dataElementGroupId  );
            //dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        }
        else
        {
            //System.out.println("\n\n +++ \n Inside dataElementGroupId " + dataElementGroupId  );    
            DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( Integer.parseInt( dataElementGroupId ) );

            dataElements = new ArrayList<DataElement>( dataElementGroup.getMembers() );
        }
       
 /*       
        if ( selectedDataElements != null && selectedDataElements.length > 0 )
        {
            Iterator<DataElement> iter = dataElements.iterator();

            while ( iter.hasNext() )
            {
                DataElement dataElement = iter.next();
                //System.out.println("\n\n +++ \n Indicator Id is   : " + indicator.getId() + " , Indicator name is :" + indicator.getName() );
                
                for ( int i = 0; i < selectedDataElements.length; i++ )
                {
                    //System.out.println("\n\n +++ \n Indicator Id is   : " + indicator.getId() + " , Indicator name is :" + indicator.getName() );
                    if ( dataElement.getId() == Integer.parseInt( selectedDataElements[i] ) )
                    {
                        iter.remove();
                    }
                }
            }
        }
*/        
    
        if ( deTargetId != null )
        {
             DeTarget deTarget = deTargetService.getDeTarget( deTargetId );
        
             List<DeTargetMember>  deTargetMemberList = new ArrayList<DeTargetMember>(deTargetService.getDeTargetMembers( deTarget ));
             for( DeTargetMember dataElementTarget : deTargetMemberList )
             {
                 selectedDeTargetMember.add( dataElementTarget.getDataelements().getId()+":" + dataElementTarget.getDecategoryOptionCombo().getId() );
             }
                //indicators.removeAll( survey.getIndicators() );
        }

      //  Collections.sort( indicators, indicatorComparator );

      //  displayPropertyHandler.handle( indicators );

        
       
        Iterator<DataElement> deIterator = dataElements.iterator();
        while ( deIterator.hasNext() )
        {
            DataElement de = deIterator.next();

            DataElementCategoryCombo dataElementCategoryCombo = de.getCategoryCombo();
            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryCombo.getOptionCombos() );

            Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
            while ( optionComboIterator.hasNext() )
            {
                DataElementCategoryOptionCombo decoc = optionComboIterator.next();
                
                String deOptComboId = de.getId() + ":" + decoc.getId();
        
       
        
            if( !selectedDeTargetMember.contains( deOptComboId ) )
            {
                optionComboIds.add( de.getId() + ":" + decoc.getId() );
                optionComboNames.add( de.getName() + ":" + dataElementCategoryService.getDataElementCategoryOptionCombo( decoc ).getName() );
            }
               
            }

        }
       
        return SUCCESS;
    }
}

