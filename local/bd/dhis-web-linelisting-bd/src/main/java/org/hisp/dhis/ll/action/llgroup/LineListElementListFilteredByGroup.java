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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.linelisting.comparator.LineListElementNameComparator;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Kristian
 * @version $Id: DataElementListFilteredByGroup.java 6256 2008-11-10 17:10:30Z
 *          larshelg $
 */
public class LineListElementListFilteredByGroup
    implements Action
{
   

    private String selectedList[];

    private List<LineListElement> lineListElements;

    private Integer groupId;

    private List<LineListElement> groupMembers;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private String id;
    
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public List<LineListElement> getGroupMembers()
    {
        return groupMembers;
    }

    public void setSelectedList( String[] selectedList )
    {
        this.selectedList = selectedList;
    }

    public void setGroupId( Integer groupId )
    {
        this.groupId = groupId;
    }

 

    public List<LineListElement> getLineListElements()
    {
        return lineListElements;
    }

    private List<PeriodType> periodTypes;

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
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

    private LineListGroup lineListGroup;

    public LineListGroup getLineListGroup()
    {
        return lineListGroup;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        System.out.println("ID : "+id);
        
       // lineListGroup = lineListService.getLineListGroup( id );
        lineListGroup = lineListService.getLineListGroup( Integer.parseInt( id ) );
        
        if( lineListGroup == null )
        {
            System.out.println("Linelistgroup is null");
        }

        periodTypes = new ArrayList<PeriodType>( periodStore.getAllPeriodTypes() );
        if ( id == null || id.equals( "ALL" ) )
        {
            lineListElements = new ArrayList<LineListElement>( lineListService.getAllLineListElements() );
        }
        else
        {
            lineListGroup = lineListService.getLineListGroup( Integer.parseInt( id ) );

            periodTypeSelect = lineListGroup.getPeriodType().getName();

            lineListElements = new ArrayList<LineListElement>( lineListService.getAllLineListElements() );

            groupMembers = new ArrayList<LineListElement>( lineListGroup.getLineListElements() );

            lineListElements.removeAll( lineListGroup.getLineListElements() );
        }

        if ( selectedList != null && selectedList.length > 0 )
        {
            Iterator<LineListElement> iter = lineListElements.iterator();

            while ( iter.hasNext() )
            {
                LineListElement lineListElement = iter.next();

                for ( int i = 0; i < selectedList.length; i++ )
                {
                    if ( lineListElement.getId() == Integer.parseInt( selectedList[i] ) )
                    {
                        iter.remove();
                    }
                }
            }
        }

        if ( groupId != null )
        {
            lineListGroup = lineListService.getLineListGroup( groupId );

            lineListElements.removeAll( lineListGroup.getLineListElements() );
        }

        //Collections.sort( lineListElements, new LineListElementNameComparator() );

        //displayPropertyHandler.handle( lineListElements );

        return SUCCESS;
    }
}
