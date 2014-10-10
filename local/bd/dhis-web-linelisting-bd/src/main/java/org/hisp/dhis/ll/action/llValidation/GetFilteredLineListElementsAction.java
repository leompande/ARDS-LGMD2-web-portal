package org.hisp.dhis.ll.action.llValidation;

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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.linelisting.comparator.LineListElementNameComparator;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id: GetFilteredDataElementsActionIN.java 5730 2008-09-20 14:32:22Z brajesh $
 */
public class GetFilteredLineListElementsAction
    implements Action
{
    private static final int ALL = 0;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private int selectedLineListGroupId;

    public void setSelectedLineListGroupId( int selectedLineListGroupId )
    {
        this.selectedLineListGroupId = selectedLineListGroupId;
    }

    private List<LineListElement> llElements;

    public List<LineListElement> getllElements()
    {
        return llElements;
    }
/*    
    private List<Operand> operands = new ArrayList<Operand>();

    public List<Operand> getOperands()
    {
        return operands;
    }
*/
    private List<DataElementOperand> operands = new ArrayList<DataElementOperand>();

    public List<DataElementOperand> getOperands()
    {
        return operands;
    }
    
    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // DataElementGroup filter
        // ---------------------------------------------------------------------

        System.out.println("inside GetFilteredLineListElementsAction");
        if ( selectedLineListGroupId == ALL )
        {
            llElements = new ArrayList<LineListElement>( lineListService.getAllLineListElements() );
        }
        else
        {
            Collection<LineListElement> groupElements = lineListService.getLineListGroup( selectedLineListGroupId ).getLineListElements();
            
            llElements = new ArrayList<LineListElement>(groupElements);
        }

        Collections.sort( llElements, new LineListElementNameComparator() );

        //dataElements = displayPropertyHandler.handleDataElements( dataElements );
        
        //displayPropertyHandler.handle( llElements );

        // ---------------------------------------------------------------------
        // String filter
        // ---------------------------------------------------------------------

        Iterator<LineListElement> iterator = llElements.iterator();

        while ( iterator.hasNext() )
        {
            LineListElement element = (LineListElement) iterator.next();            
            String name = element.getName();
            System.out.println("elementname = "+name);
        }

        return SUCCESS;
    }
}
