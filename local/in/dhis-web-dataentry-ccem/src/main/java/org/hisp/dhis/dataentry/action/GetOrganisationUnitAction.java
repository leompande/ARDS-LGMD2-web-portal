package org.hisp.dhis.dataentry.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
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
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    private String message;
    
    public String getMessage()
    {
        return message;
    }
    
    private Integer orgunitId;

    public void setOrgunitId( Integer orgunitId )
    {
        this.orgunitId = orgunitId;
    }
    
    public Integer getOrgunitId() 
    {
        return orgunitId;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgunitId );

        List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY ) );
        
        OrganisationUnitGroup ouGroup = ouGroups.get( 0 );

        if ( !ouGroup.getMembers().contains( organisationUnit ) )
        {
            message = organisationUnit.getName();
            
            //System.out.println( message  + "--------" +  " Not in Group");
            
            return INPUT;
        }
        else
        {
            message = organisationUnit.getName();
            
            //System.out.println( message  + "--------" +  " In Side Group");
            
            return SUCCESS;
           
        }

    }

}
