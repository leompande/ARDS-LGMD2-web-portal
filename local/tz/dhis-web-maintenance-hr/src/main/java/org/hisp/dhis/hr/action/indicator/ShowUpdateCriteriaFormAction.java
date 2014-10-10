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

import java.util.ArrayList;
import java.util.Collection;

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
 * ShowAddHrDataSetAction.java  Nov 14, 2010 3:25:22 PM
 */
public class ShowUpdateCriteriaFormAction
implements Action{
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }
    
    private AttributeOptionsService attributeOptionsService;

    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
        this.attributeOptionsService = attributeOptionsService;
    }
    
    private CriteriaService criteriaService;
    
    public void setCriteriaService(CriteriaService criteriaService )
    {
    	this.criteriaService = criteriaService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private int id;
    
    public void setId(int id )
    {
    	this.id = id;
    }
    
    public int getId()
    {
    	return id;
    }
   
    private Collection<AttributeOptions> attributeOptions;

    public Collection<AttributeOptions> getAttributeOptions()
    {
        return attributeOptions;
    }
    
    private Collection<Attribute> attributes;
    
    public Collection<Attribute> getAttributes()
    {
    	return attributes;
    }
    
    private Criteria criteria;
    
    public Criteria getCriteria()
    {
    	return criteria;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	criteria = criteriaService.getCriteria(id);
        
        //attributeOptions = new ArrayList<AttributeOptions> ( attributeOptionsService.getAllAttributeOptions() );
        
        attributeOptions = attributeOptionsService.getAttributeOptionsByAttribute(criteria.getAttribute());
        
        attributeOptions.removeAll(criteria.getAttributeOptions());
        
        attributes = attributeService.getAllAttribute();
        
        return SUCCESS;
    }
}
