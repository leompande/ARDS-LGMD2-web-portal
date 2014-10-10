package org.hisp.dhis.hr.action.indicator;
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

import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptionGroupService;
import org.hisp.dhis.hr.TargetIndicator;
import org.hisp.dhis.hr.TargetIndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;

import com.opensymphony.xwork2.Action;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id: AddTargetIndicatorAction.java 6216 2010-11-06  $
 */
public class UpdateTargetIndicatorAction implements Action
{

	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
	private TargetIndicatorService targetIndicatorService;
	
	public void setTargetIndicatorService(TargetIndicatorService targetIndicatorService) {
		this.targetIndicatorService = targetIndicatorService;
	}
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService ) {
    	this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private AttributeOptionGroupService attributeOptionGroupService;
    
    public void setAttributeOptionGroupService( AttributeOptionGroupService attributeOptionGroupService ) {
    	this.attributeOptionGroupService = attributeOptionGroupService;
    }

    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private String targetIndicatorName;

    public void setTargetIndicatorName( String targetIndicatorName )
    {
        this.targetIndicatorName = targetIndicatorName;
    }

    private Integer organisationUnitGroupId;

    public void setOrganisationUnitGroupId( Integer organisationUnitGroupId )
    {
        this.organisationUnitGroupId = organisationUnitGroupId;
    }

    private Integer attributeOptionGroupId;

    public void setAttributeOptionGroupId( Integer attributeOptionGroupId )
    {
        this.attributeOptionGroupId = attributeOptionGroupId;
    }

    private Integer targetIndicatorYear;

    public void setTargetIndicatorYear( Integer targetIndicatorYear )
    {
        this.targetIndicatorYear = targetIndicatorYear;
    }

    private Double expectedValue;

    public void setExpectedValue( Double expectedValue )
    {
        this.expectedValue = expectedValue;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    	throws Exception
    {
        // ---------------------------------------------------------------------
        // Create data element
        // ---------------------------------------------------------------------

        TargetIndicator targetIndicator = targetIndicatorService.getTargetIndicator(id);
        
        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup(organisationUnitGroupId);
        
        AttributeOptionGroup attributeOptionGroup = attributeOptionGroupService.getAttributeOptionGroup(attributeOptionGroupId);
        
        targetIndicator.setOrganisationUnitGroup(organisationUnitGroup);
        targetIndicator.setAttributeOptionGroup(attributeOptionGroup);
        targetIndicator.setName(targetIndicatorName);
        targetIndicator.setValue(expectedValue);
        targetIndicator.setYear(targetIndicatorYear);
        
        targetIndicatorService.updateTargetIndicator(targetIndicator);
        
        return SUCCESS;
    }
}
