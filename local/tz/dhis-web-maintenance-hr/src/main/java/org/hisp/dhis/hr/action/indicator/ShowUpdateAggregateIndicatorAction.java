package org.hisp.dhis.hr.action.indicator;

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


import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.hr.AggregateAttribute;
import org.hisp.dhis.hr.AggregateAttributeService;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;
import org.hisp.dhis.hr.Criteria;
import org.hisp.dhis.hr.CriteriaService;

import com.opensymphony.xwork2.Action;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 *
 * ShowUpdateAggregateIndicator.java  Nov 14, 2010 10:08:41 PM
 */
public class ShowUpdateAggregateIndicatorAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private AggregateAttributeService aggregateAttributeService;
	
	public void setAggregateAttributeService( AggregateAttributeService aggregateAttributeService )
	{
		this.aggregateAttributeService = aggregateAttributeService;
	}
	
	private AttributeOptionsService attributeOptionsService;
	
	public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
	{
		this.attributeOptionsService = attributeOptionsService;
	}
	
	private CriteriaService criteriaService;
    
    public void setCriteriaService( CriteriaService criteriaService )
    {
    	this.criteriaService = criteriaService;
    }
	
	private DataElementService dataElementService;
	
	public void setDataElementService(DataElementService dataElementService)
	{
		this.dataElementService = dataElementService;
	}

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private int id;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }
    
    private AggregateAttribute aggregateAttribute;
    
    public AggregateAttribute getAggregateAttribute()
    {
    	return aggregateAttribute;
    }
    
    private Collection<DataElement> dataElements;
    
    public Collection<DataElement> getDataElements()
    {
    	return dataElements;
    }
    
    private Collection<AttributeOptions> attributeOptions;
    
    public Collection<AttributeOptions> getAttributeOptions()
    {
    	return attributeOptions;
    }
    
    private Collection<Criteria> criterias;
    
    public Collection<Criteria> getCriterias()
    {
    	return criterias;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

	public String execute()
        throws Exception
    {
    	aggregateAttribute = aggregateAttributeService.getAggregateAttribute(id);
    	
    	attributeOptions = new ArrayList<AttributeOptions>( attributeOptionsService.getAllAttributeOptions());    	
    	
    	Collection<AttributeOptions> attributeOptionsToExclude = new ArrayList<AttributeOptions>(aggregateAttribute.getAttributeOptions());
    	
    	attributeOptions.removeAll(attributeOptionsToExclude);
    	
    	criterias = new ArrayList<Criteria>( criteriaService.getAllCriteria());
    	
    	Collection<Criteria> criteriasToExclude = new ArrayList<Criteria>(aggregateAttribute.getCriterias());
    	
    	criterias.removeAll(criteriasToExclude);
    	
    	String domainType = new String("humanresource");
        
        dataElements = dataElementService.getDataElementsByDomainType( domainType );
        
        return SUCCESS;
    }
}
