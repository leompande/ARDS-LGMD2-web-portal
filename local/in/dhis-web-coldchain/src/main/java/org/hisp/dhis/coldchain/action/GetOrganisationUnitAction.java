package org.hisp.dhis.coldchain.action;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class GetOrganisationUnitAction implements Action
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
    // Input/output
    // -------------------------------------------------------------------------
    private String message;
    
    public String getMessage()
    {
        return message;
    }
    
    private String orgunitId;
    
    public String getOrgunitId()
    {
        return orgunitId;
    }

    public void setOrgunitId( String orgunitId )
    {
        this.orgunitId = orgunitId;
    }
    
    /*
    private Integer orgunitId;

    public void setOrgunitId( Integer orgunitId )
    {
        this.orgunitId = orgunitId;
    }
    
    public Integer getOrgunitId()
    {
        return orgunitId;
    }
    */
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute() throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgunitId );

        message = organisationUnit.getName();

        return SUCCESS;
    }

}
