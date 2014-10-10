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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;

import com.opensymphony.xwork2.ActionSupport;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.linelisting.comparator.LineListGroupNameComparator;

/**
 * @author Margrethe Store
 * @author Lars Helge Overland
 * @version $Id: GetExpressionAction.java 5730 2008-09-20 14:32:22Z brajesh $
 */
public class GetExpressionAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final int ALL = 0;

    public int getALL()
    {
        return ALL;
    }
    
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

    private Comparator<LineListElement> lineListElementComparator;

    public void setLineListElementComparator( Comparator<LineListElement> lineListElementComparator )
    {
        this.lineListElementComparator = lineListElementComparator;
    }

    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private String description;

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }
    
    private String expression;

    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }
    
    private String textualExpression;

    public String getTextualExpression()
    {
        return textualExpression;
    }

    public void setTextualExpression( String textualExpression )
    {
        this.textualExpression = textualExpression;
    }

    private List<LineListElement> llElements;
    
    public List<LineListElement> getllElements()
    {
        return llElements;
    }

    private List<LineListGroup> lineListGroups;
    
    public List<LineListGroup> getLineListGroups()
    {
        return lineListGroups;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------  
    
    public String execute() throws Exception
    {
        llElements = new ArrayList<LineListElement>( lineListService.getAllLineListElements() );
        
        Collections.sort( llElements, lineListElementComparator );

        //dataElements = displayPropertyHandler.handleDataElements( dataElements );
        //displayPropertyHandler.handle( llElements );
        
        lineListGroups = new ArrayList<LineListGroup>( lineListService.getAllLineListGroups() );

        System.out.println("lineListGroups.size() = "+lineListGroups.size());
        Collections.sort( lineListGroups, new LineListGroupNameComparator() );
        
        return SUCCESS;
    }
}
