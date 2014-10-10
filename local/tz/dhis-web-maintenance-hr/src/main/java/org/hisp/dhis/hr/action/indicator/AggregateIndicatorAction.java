package org.hisp.dhis.hr.action.indicator;

import org.hisp.dhis.hr.AggregateAttribute;
import org.hisp.dhis.hr.AggregateAttributeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;


import com.opensymphony.xwork2.Action;

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

/**
 * @author Wilfred Felix Senyoni<senyoni@gmail.com>
 *
 * AggregateIndicatorrAction.java  June 30, 2011 
 */
public class AggregateIndicatorAction 
implements Action{
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
       
    private AggregateAttributeService aggregateAttributeService;
    
    public void setAggregateAttributeService( AggregateAttributeService aggregateAttributeService )
    {
    	this.aggregateAttributeService = aggregateAttributeService;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
    	this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
   
    private int id;
    
    public void setId( int id )
    {
        this.id = id;
    }
    
    private int organisationUnitId;
    
    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
   
   
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
    	AggregateAttribute aggregateAttribute = aggregateAttributeService.getAggregateAttribute( id );
    	
    	OrganisationUnit unit = organisationUnitService.getOrganisationUnit(organisationUnitId);
    	    	
	    aggregateAttributeService.getCountPersonByAggregateAttribute( aggregateAttribute, unit );
	    	
	    return SUCCESS;
    	
    }
}
