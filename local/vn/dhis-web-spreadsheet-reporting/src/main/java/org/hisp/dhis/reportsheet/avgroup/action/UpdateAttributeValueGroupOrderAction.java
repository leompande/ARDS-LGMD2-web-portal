package org.hisp.dhis.reportsheet.avgroup.action;

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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.reportsheet.AttributeValueGroupOrder;
import org.hisp.dhis.reportsheet.AttributeValueGroupOrderService;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class UpdateAttributeValueGroupOrderAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private AttributeValueGroupOrderService attributeValueGroupOrderService;

    public void setAttributeValueGroupOrderService( AttributeValueGroupOrderService attributeValueGroupOrderService )
    {
        this.attributeValueGroupOrderService = attributeValueGroupOrderService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer attributeId;

    private Integer attributeValueGroupOrderId;

    private String name;

    private List<String> attributeValues = new ArrayList<String>();

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public void setAttributeValueGroupOrderId( Integer attributeValueGroupOrderId )
    {
        this.attributeValueGroupOrderId = attributeValueGroupOrderId;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setAttributeId( Integer attributeId )
    {
        this.attributeId = attributeId;
    }

    public void setAttributeValues( List<String> attributeValues )
    {
        this.attributeValues = attributeValues;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        AttributeValueGroupOrder attributeValueGroupOrder = attributeValueGroupOrderService
            .getAttributeValueGroupOrder( attributeValueGroupOrderId );

        Attribute attribute = attributeService.getAttribute( attributeId );

        attributeValueGroupOrder.setName( name );

        attributeValueGroupOrder.setAttribute( attribute );

        List<String> finalList = new ArrayList<String>();

        removeDuplicatedItems( attributeValues, finalList );

        attributeValueGroupOrder.setAttributeValues( finalList );

        attributeValueGroupOrderService.updateAttributeValueGroupOrder( attributeValueGroupOrder );

        return SUCCESS;
    }

    private static void removeDuplicatedItems( List<String> a, List<String> b )
    {
        for ( String s1 : a )
        {
            if ( !b.contains( s1 ) )
            {
                b.add( s1 );
            }
        }
    }
}
