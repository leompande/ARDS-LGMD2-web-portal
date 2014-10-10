package org.hisp.dhis.hr.action.indicator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptionGroupService;
import org.hisp.dhis.hr.TargetIndicator;
import org.hisp.dhis.hr.TargetIndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;

import com.opensymphony.xwork2.Action;

public class GetTargetIndicatorAction 
implements Action
{
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private TargetIndicatorService targetIndicatorService;
	
	public void setTargetIndicatorService(TargetIndicatorService targetIndicatorService) {
		this.targetIndicatorService = targetIndicatorService;
	}
	
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
	private int id;

    public void setId( int id )
    {
        this.id = id;
    }
    
    private TargetIndicator targetIndicator;
    
    public TargetIndicator getTargetIndicator() {
    	return targetIndicator;
    }
    
    private Collection<OrganisationUnitGroup> organisationUnitGroups;
    
    public Collection<OrganisationUnitGroup> getOrganisationUnitGroups() {
    	return organisationUnitGroups;
    }
	
	private int organisationUnitGroupsId;
	
	public void setOrganisationUnitGroupsId(int organisationUnitGroupsId) {
		this.organisationUnitGroupsId = organisationUnitGroupsId;
	}
	
    private Collection<AttributeOptionGroup> attributeOptionGroups;
    
    public Collection<AttributeOptionGroup> getAttributeOptionGroups() {
    	return attributeOptionGroups;
    }
    
	private int attributeOptionGroupId;
	
	public void setAttributeOptionGroupId(int attributeOptionGroupId) {
		this.attributeOptionGroupId = attributeOptionGroupId;
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
    	targetIndicator = targetIndicatorService.getTargetIndicator(id);
    	
    	organisationUnitGroups = new ArrayList<OrganisationUnitGroup> ( organisationUnitGroupService.getAllOrganisationUnitGroups()  );
    	attributeOptionGroups = new ArrayList<AttributeOptionGroup> ( attributeOptionGroupService.getAllAttributeOptionGroup() );
    	
    	targetIndicatorYears = new ArrayList<Integer>(this.generatePrevioustargetIndicatorYears());
    	
        return SUCCESS;
    }
}
