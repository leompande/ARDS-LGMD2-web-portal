package org.hisp.dhis.coldchain.equipment.manager.action;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ClinicEquipmentsAction.javaSep 26, 2012 11:46:33 AM	
 */

public class ClinicEquipmentsAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Integer orgUnitId;
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        if( organisationUnit == null )
        {
            //System.out.println("Organisationunit is null");
        }
        else
        {      
            //System.out.println("Organisationunit is not null ---" + organisationUnit.getId() );
        }
        
        if( organisationUnit == null && orgUnitId != null )
        {
            organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        }
        
        return SUCCESS;
    }

}

