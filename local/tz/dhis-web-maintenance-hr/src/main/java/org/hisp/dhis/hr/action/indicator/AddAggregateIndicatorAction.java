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
 * AddDataSetAction.java  Nov 14, 2010 10:08:41 PM
 */
public class AddAggregateIndicatorAction 
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
    
    private DataElementService dataelementService;
    
    public void setDataelementService( DataElementService dataelementService )
    {
    	this.dataelementService = dataelementService;
    }
    
    private AggregateAttributeService aggregateAttributeService;
    
    public void setAggregateAttributeService( AggregateAttributeService aggregateAttributeService)
    {
    	this.aggregateAttributeService = aggregateAttributeService;
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
    
    private Collection<String> selectedCriterias;
    
    private Integer dataelementId;

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
    
    public void setSelectedCriterias( Collection<String> selectedCriterias )
    {
    	this.selectedCriterias = selectedCriterias;
    }
    
    public void setDataelementId(Integer dataelementId) {
    	this.dataelementId = dataelementId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	AggregateAttribute aggregateAttribute = new AggregateAttribute();
    	
    	aggregateAttribute.setName(nameField);
    	
    	DataElement dataelement = new DataElement();
    	dataelement = dataelementService.getDataElement(dataelementId);
    	
    	aggregateAttribute.setDataelement( dataelement );
    	

        // Add attributeOptions only if there were any selected.
    	if( selectedAttributeOptions.size() > 0 ) {
    	
	        Collection<AttributeOptions> attributeOptions = new HashSet<AttributeOptions>();
	        for ( String attributeOptionId : selectedAttributeOptions )
	        {
	            AttributeOptions attributeOption = attributeOptionsService.getAttributeOptions( Integer.parseInt( attributeOptionId ) );
	            attributeOptions.add( attributeOption );
	        }
	        
	        aggregateAttribute.setAttributeOptions(new HashSet<AttributeOptions>(attributeOptions));
    	}
        
    	// Add criterias only if there were any selected.
    	if( selectedCriterias.size() > 0 ) {
    	
	        Collection<Criteria> criterias = new HashSet<Criteria>();
	        for ( String criteriaId : selectedCriterias )
	        {
	            Criteria criteria = criteriaService.getCriteria( Integer.parseInt( criteriaId ) );
	            criterias.add( criteria );
	        }
	        
	        aggregateAttribute.setCriterias(new HashSet<Criteria>(criterias));
    	}
    	
        aggregateAttributeService.saveAggregateAttribute(aggregateAttribute);

        return SUCCESS;
    }
}