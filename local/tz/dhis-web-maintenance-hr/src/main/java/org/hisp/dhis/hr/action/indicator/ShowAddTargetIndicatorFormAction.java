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
import java.util.Calendar;
import java.util.Collection;

import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptionGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;

import com.opensymphony.xwork2.Action;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 *
 * ShowAddAggregateIndicator.java  Nov 14, 2010 10:08:41 PM
 */

public class ShowAddTargetIndicatorFormAction 
implements Action
{
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private OrganisationUnitGroupService organisationUnitGroupService;
	
	public void setOrganisationUnitGroupService(OrganisationUnitGroupService organisationUnitGroupService) {
		this.organisationUnitGroupService = organisationUnitGroupService;
	}
	
	private AttributeOptionGroupService attributeOptionGroupService;
	
	public void setAttributeOptionGroupService(AttributeOptionGroupService attributeOptionGroupService) {
		this.attributeOptionGroupService = attributeOptionGroupService;
	}
	

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
	
	private int attributeOptionGroupId;
	
	public void setAttributeOptionGroupId(int attributeOptionGroupId) {
		this.attributeOptionGroupId = attributeOptionGroupId;
	}
	public int getAttributeOptionGroupId() {
		return attributeOptionGroupId;
	}
	
	private int organisationUnitGroupsId;
	
	public void setOrganisationUnitGroupsId(int organisationUnitGroupsId) {
		this.organisationUnitGroupsId = organisationUnitGroupsId;
	}
	public int getOrganisationUnitGroupsId() {
		return organisationUnitGroupsId;
	}
    
    private Collection<OrganisationUnitGroup> organisationUnitGroups;
    
    public Collection<OrganisationUnitGroup> getOrganisationUnitGroups() {
    	return organisationUnitGroups;
    }
    
    private Collection<AttributeOptionGroup> attributeOptionGroups;
    
    public Collection<AttributeOptionGroup> getAttributeOptionGroups() {
    	return attributeOptionGroups;
    }
    
    private Collection<Integer> targetIndicatorYears;
    
    public Collection<Integer> getTargetIndicatorYears() {
    	return targetIndicatorYears;
    }
    
    public Collection<Integer> generatePrevioustargetIndicatorYears() {
    	Calendar calendar = Calendar.getInstance();
		Integer thisYear = calendar.get(Calendar.YEAR);
    	targetIndicatorYears = new ArrayList<Integer>();
    	for( int incr=thisYear; incr>= (thisYear - 10); incr-- ) {
			targetIndicatorYears.add(incr);
		}
    	return targetIndicatorYears;
    }

    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	targetIndicatorYears = new ArrayList<Integer>(this.generatePrevioustargetIndicatorYears());
    	organisationUnitGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() );
    	attributeOptionGroups = new ArrayList<AttributeOptionGroup>( attributeOptionGroupService.getAllAttributeOptionGroup() );
    	
        return SUCCESS;
    }
}
