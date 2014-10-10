/*
 * Copyright (c) 2004-2012, University of Oslo, University Of Dar-es-salaam
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

package org.hisp.dhis.hr.action.indicator;


import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.Criteria;
import org.hisp.dhis.hr.CriteriaService;

import com.opensymphony.xwork2.Action;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 *
 * AddDataSetAction.java  Nov 14, 2010 10:08:41 PM
 */
public class AddCriteriaAction 
implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private AttributeOptionsService attributeOptionsService;

    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
        this.attributeOptionsService = attributeOptionsService;
    }
    
    private AttributeService attributeService;
    
    public void setAttributeService( AttributeService attributeService )
    {
    	this.attributeService = attributeService;
    }
    
    private CriteriaService criteriaService;
    
    public void setCriteriaService( CriteriaService criteriaService )
    {
    	this.criteriaService = criteriaService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String nameField;

    private Collection<String> selectedAttributeOptions;
    
    private Integer attributeId;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setNameField( String nameField )
    {
        this.nameField = nameField;
    }

    public void setSelectedAttributeOptions( Collection<String> selectedAttributeOptions )
    {
        this.selectedAttributeOptions = selectedAttributeOptions;
    }
    
    public void setAttributeId(Integer attributeId) {
    	this.attributeId = attributeId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	Criteria criteria = new Criteria();
    	criteria.setName(nameField);
    	
    	Attribute attribute = new Attribute();
    	attribute = attributeService.getAttribute(attributeId);
    	
    	criteria.setAttribute(attribute);
 

        // Add attributeOptions only if there were any selected.
    	if( selectedAttributeOptions.size() > 0 ) {
    	
	        Collection<AttributeOptions> attributeOptions = new HashSet<AttributeOptions>();
	        for ( String attributeOptionId : selectedAttributeOptions )
	        {
	            AttributeOptions attributeOption = attributeOptionsService.getAttributeOptions( Integer.parseInt( attributeOptionId ) );
	            attributeOptions.add( attributeOption );
	        }
	        
	        criteria.setAttributeOptions(new HashSet<AttributeOptions>(attributeOptions));
    	}
        criteriaService.saveCriteria(criteria);
        
        return SUCCESS;
    }
}