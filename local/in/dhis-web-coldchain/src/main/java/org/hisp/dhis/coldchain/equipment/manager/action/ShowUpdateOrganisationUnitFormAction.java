package org.hisp.dhis.coldchain.equipment.manager.action;

import static org.hisp.dhis.system.util.ValidationUtils.coordinateIsValid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.util.AttributeUtils;
import org.hisp.dhis.system.util.ValidationUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ShowUpdateOrganisationUnitFormAction.javaOct 19, 2012 12:33:58 PM	
 */

public class ShowUpdateOrganisationUnitFormAction     implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }
    
    private EquipmentType_AttributeService equipmentType_AttributeService;
    
    public void setEquipmentType_AttributeService( EquipmentType_AttributeService equipmentType_AttributeService )
    {
        this.equipmentType_AttributeService = equipmentType_AttributeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private List<Attribute> attributes;

    public List<Attribute> getAttributes()
    {
        return attributes;
    }

    public Map<Integer, String> attributeValues = new HashMap<Integer, String>();

    public Map<Integer, String> getAttributeValues()
    {
        return attributeValues;
    }
    
    private boolean point;

    public boolean isPoint()
    {
        return point;
    }

    private String longitude;

    public String getLongitude()
    {
        return longitude;
    }

    private String latitude;
    
    public String getLatitude()
    {
        return latitude;
    }
    
    public Map<String, String> selectedOrgUnitAttribDataValueMap;
    
    public Map<String, String> getSelectedOrgUnitAttribDataValueMap()
    {
        return selectedOrgUnitAttribDataValueMap;
    }
    
    String attributedsByComma;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        attributedsByComma = "-1";
        selectedOrgUnitAttribDataValueMap = new HashMap<String, String>();
        
        organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        attributes = new ArrayList<Attribute>( attributeService.getOrganisationUnitAttributes() );

        attributeValues = AttributeUtils.getAttributeValueMap( organisationUnit.getAttributeValues() );

        
        attributes = new ArrayList<Attribute>( attributeService.getOrganisationUnitAttributes() );
        
        Collections.sort( attributes, IdentifiableObjectNameComparator.INSTANCE );
        
        for ( Attribute attribute : attributes )
        {
            //attribute.getValueType().equalsIgnoreCase( "bool" );
            //attribute.isMandatory();
            attributedsByComma += "," + attribute.getId();
        }
        
        selectedOrgUnitAttribDataValueMap = new HashMap<String, String>( equipmentType_AttributeService.getOrgUnitAttributeDataValue( ""+organisationUnit.getId(), attributedsByComma ) );
        
        // ---------------------------------------------------------------------
        // Allow update only if org unit does not have polygon coordinates
        // ---------------------------------------------------------------------

        point = organisationUnit.getCoordinates() == null || coordinateIsValid( organisationUnit.getCoordinates() );
        longitude = ValidationUtils.getLongitude( organisationUnit.getCoordinates() );
        latitude = ValidationUtils.getLatitude( organisationUnit.getCoordinates() );
        
        return SUCCESS;
    }
}


