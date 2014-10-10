package org.hisp.dhis.validationrule.minmax.action;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.ActionSupport;

public class GetOrgUnitsAction extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private Integer orgUnitId;

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
        
    private OrganisationUnit orgUnit;
    
    public OrganisationUnit getOrgUnit()
    {
        return orgUnit;
    }

    private Integer orgUnitLevel;
    
    public Integer getOrgUnitLevel() 
    {
		return orgUnitLevel;
	}
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------




	public String execute() throws Exception
    {
        /* OrganisationUnit */
        if(orgUnitId != null)
        {
            orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );            
        }   

        orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit(orgUnit );
        
        System.out.println("OrgUnitLevel : "+orgUnitLevel+ " Name : "+orgUnit.getShortName());
        
        return SUCCESS;
    }

}
