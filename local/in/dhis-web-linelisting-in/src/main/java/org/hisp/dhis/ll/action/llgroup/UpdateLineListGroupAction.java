package org.hisp.dhis.ll.action.llgroup;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class UpdateLineListGroupAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataBaseManagerInterface dataBaseManagerInterface;

    public void setDataBaseManagerInterface( DataBaseManagerInterface dataBaseManagerInterface )
    {
        this.dataBaseManagerInterface = dataBaseManagerInterface;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private Collection<String> selectedList;

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    private String periodTypeSelect;

    public void setPeriodTypeSelect( String periodTypeSelect )
    {
        this.periodTypeSelect = periodTypeSelect;
    }

    public String getPeriodTypeSelect()
    {
        return periodTypeSelect;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {

        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------
        
        System.out.println("Group Id for Updation is :" + id );
        System.out.println("name of Group is  :" + name );
        if ( description != null && description.trim().length() == 0 )
        {
            description = null;
        }

        shortName = shortName.replaceAll( " ", "_" );
        System.out.println("Short name of Group :" + shortName );
        System.out.println("Period Type of Group :" + periodTypeSelect );
        
        System.out.println("Description of Group :" + description );
        

        // ---------------------------------------------------------------------
        // Update data element
        // ---------------------------------------------------------------------

        LineListGroup lineListGroup = lineListService.getLineListGroup( id );
        List<LineListElement> newElements = new ArrayList<LineListElement>();
        List<LineListElement> oldElements = new ArrayList<LineListElement>( lineListGroup.getLineListElements() );
        List<LineListElement> removeElementList = new ArrayList<LineListElement>();
        Collection<LineListElement> updatedDataElementList = new ArrayList<LineListElement>();

        if ( selectedList == null )
        {
            System.out.println( "selectedList is null" );
        } else
        {
            if ( newElements.isEmpty() )
            {
                for ( String elementId : selectedList )
                {

                    LineListElement element = lineListService.getLineListElement( Integer.parseInt( elementId ) );
                    if ( !( oldElements.contains( element ) ) )
                    {
                        newElements.add( element );
                        System.out.println( "New element that should be added is: " + element );
                    }
                    updatedDataElementList.add( element );
                }
                System.out.println("message : " + newElements.isEmpty() );
            }
            //System.out.println("selectedList is not null" + selectedList );
        }
        System.out.println("Size of old Elements" + oldElements.size() );
        
        for ( int i = 0; i < oldElements.size(); i++ )
        {
            if ( !( updatedDataElementList.contains( oldElements.get( i ) ) ) )
            {

                boolean doNotDelete = dataBaseManagerInterface.checkDataFromTable( lineListGroup.getShortName(), oldElements.get( i ) );
                System.out.println("boolean : " + doNotDelete );
                if ( !doNotDelete )
                {
                    System.out.println( "element that should be removed is: " + oldElements.get( i ) + " " + lineListGroup.getShortName() );
                    removeElementList.add( oldElements.get( i ) );
                    updatedDataElementList.remove( oldElements.get( i ) );
                }
            }
        }

        lineListGroup.getLineListElements().removeAll( updatedDataElementList );

        lineListGroup.getLineListElements().retainAll( updatedDataElementList );

        lineListGroup.getLineListElements().addAll( updatedDataElementList );

        if ( !( removeElementList.isEmpty()) || !( newElements.isEmpty() ) )
        {
            boolean dataUpdated = dataBaseManagerInterface.updateTable( lineListGroup.getShortName(), removeElementList, newElements );
            if ( dataUpdated )
            {

                if ( lineListGroup != null )
                {
                    lineListGroup.setName( name );
                    lineListGroup.setShortName( shortName );
                    lineListGroup.setDescription( description );

                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeSelect );
                    lineListGroup.setPeriodType( periodService.getPeriodTypeByClass( periodType.getClass() ) );

                    lineListService.updateLineListGroup( lineListGroup );
                }
            }
        }

        return SUCCESS;
    }
}
