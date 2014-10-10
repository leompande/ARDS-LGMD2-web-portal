package org.hisp.dhis.reports.datasetlock.action;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class GetOrgUnitNameAction
implements Action
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
    // Input/Output
    // -------------------------------------------------------------------------
    private String selectedOrgUnitId;
    
    public void setSelectedOrgUnitId( String selectedOrgUnitId )
    {
        this.selectedOrgUnitId = selectedOrgUnitId;
    }
    
    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
       
        System.out.println( "orgUnit Id is "  + id );
        organisationUnit = organisationUnitService.getOrganisationUnit( id );
       // organisationUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( selectedOrgUnitId ) );
       // organisationUnit = organisationUnitService.getOrganisationUnit( selectedOrgUnitId.intValue() );
        
        System.out.println( "orgUnit Id is "  + selectedOrgUnitId + " , orgNunit name is : " + organisationUnit.getName() );
        
        //Integer.parseInt( year );
       
        return SUCCESS;
    }
}