package org.hisp.dhis.reports.mobile.action;

import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class GenerateMobileReportAnalyserFormAction 
implements Action
{
    //--------------------------------------------------------------------------
    //Dependencies
    //--------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;
    
    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public String execute()
    {
        
        return SUCCESS;
    }

}
