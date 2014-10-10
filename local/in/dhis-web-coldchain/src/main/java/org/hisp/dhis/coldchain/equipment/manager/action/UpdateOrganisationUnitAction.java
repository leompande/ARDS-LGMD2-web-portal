package org.hisp.dhis.coldchain.equipment.manager.action;

import static org.hisp.dhis.system.util.TextUtils.nullIfEmpty;
import static org.hisp.dhis.system.util.ValidationUtils.coordinateIsValid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.util.AttributeUtils;
import org.hisp.dhis.system.util.ValidationUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version UpdateOrganisationUnitAction.javaOct 19, 2012 2:25:28 PM	
 */

public class UpdateOrganisationUnitAction implements Action
{
    public static final String PREFIX_ATTRIBUTE = "attr";
    
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

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Integer orgUnitId;

    public Integer getOrganisationUnitId()
    {
        return orgUnitId;
    }
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    public Integer getOrgUnitId()
    {
        return orgUnitId;
    }

    private String orgUnitName;
    
    public void setOrgUnitName( String orgUnitName )
    {
        this.orgUnitName = orgUnitName;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }

    private String longitude;

    public void setLongitude( String longitude )
    {
        this.longitude = longitude;
    }

    private String latitude;

    public void setLatitude( String latitude )
    {
        this.latitude = latitude;
    }

    private String contactPerson;

    public void setContactPerson( String contactPerson )
    {
        this.contactPerson = contactPerson;
    }

    private String address;

    public void setAddress( String address )
    {
        this.address = address;
    }

    private String email;

    public void setEmail( String email )
    {
        this.email = email;
    }

    private String phoneNumber;

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    private List<String> jsonAttributeValues;

    public void setJsonAttributeValues( List<String> jsonAttributeValues )
    {
        this.jsonAttributeValues = jsonAttributeValues;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        System.out.println( " inside Update Org Unit "  + orgUnitId );
        
        code = nullIfEmpty( code );
       
        longitude = nullIfEmpty( longitude );
        latitude = nullIfEmpty( latitude );
        
        contactPerson = nullIfEmpty( contactPerson );
        address = nullIfEmpty( address );
        email = nullIfEmpty( email );
        phoneNumber = nullIfEmpty( phoneNumber );

        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        // ---------------------------------------------------------------------
        // Update organisation unit
        // ---------------------------------------------------------------------

        if ( !organisationUnit.getName().equals( orgUnitName ) )
        {
            organisationUnitService.updateVersion();
        }

        organisationUnit.setName( orgUnitName );
       
        organisationUnit.setCode( code );
        
        organisationUnit.setContactPerson( contactPerson );
        organisationUnit.setAddress( address );
        organisationUnit.setEmail( email );
        organisationUnit.setPhoneNumber( phoneNumber );

        if ( jsonAttributeValues != null )
        {
            AttributeUtils.updateAttributeValuesFromJson( organisationUnit.getAttributeValues(), jsonAttributeValues,
                attributeService );
        }

        // ---------------------------------------------------------------------
        // Set coordinates and feature type to point if valid
        // ---------------------------------------------------------------------

        boolean point = organisationUnit.getCoordinates() == null
            || coordinateIsValid( organisationUnit.getCoordinates() );

        if ( point )
        {
            String coordinates = null;
            String featureType = null;

            if ( longitude != null && latitude != null
                && ValidationUtils.coordinateIsValid( ValidationUtils.getCoordinate( longitude, latitude ) ) )
            {
                coordinates = ValidationUtils.getCoordinate( longitude, latitude );
                featureType = OrganisationUnit.FEATURETYPE_POINT;
            }

            organisationUnit.setCoordinates( coordinates );
            organisationUnit.setFeatureType( featureType );
        }

        //organisationUnitService.updateOrganisationUnit( organisationUnit );
        
        
        List<Attribute> attributes =  new ArrayList<Attribute>( attributeService.getOrganisationUnitAttributes() );
        
        Collections.sort( attributes, IdentifiableObjectNameComparator.INSTANCE );
        
        updateOrgUnitAttributeValues( organisationUnit.getAttributeValues(), attributes ); //method for Update Org Unit Attribute Values
        
        organisationUnitService.updateOrganisationUnit( organisationUnit );
        
        
        /*
        HttpServletRequest request = ServletActionContext.getRequest();
        String value = null;
        
        List<Attribute> attributes =  new ArrayList<Attribute>( attributeService.getOrganisationUnitAttributes() );
        
        Collections.sort( attributes, IdentifiableObjectNameComparator.INSTANCE );
        
        Set<AttributeValue> attributeValues = new HashSet<AttributeValue>( organisationUnit.getAttributeValues() );
        
        attributeValues.clear();
        
        for ( Attribute attribute : attributes )
        {
           // attributeValues.clear();
            
            value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
            
            System.out.println( " PREFIX ATTRIBUTE " + PREFIX_ATTRIBUTE + attribute.getId() + " Value : " +  value ) ;
            
            String tempString = PREFIX_ATTRIBUTE + attribute.getId();
           
            String attrId = tempString.substring( 4 );
            
            System.out.println( attrId + " -- " + attribute.getId() ) ;
            
            
            AttributeValue attributeValue = new AttributeValue();
            attributeValue.setId( attribute.getId() );
            attributeValue.setValue( value );

            Attribute tempAttribute = attributeService.getAttribute( attributeValue.getId() );
            
            if ( tempAttribute == null )
            {
                continue;
            }

            attributeValue.setAttribute( tempAttribute );
            
            for ( AttributeValue attributeValueItem : attributeValues )
            {
                if ( attributeValueItem.getAttribute().getId() == tempAttribute.getId() )
                {
                    if ( attributeValue.getValue() == null || attributeValue.getValue().length() == 0 )
                    {
                        attributeService.deleteAttributeValue( attributeValueItem );
                    }
                    else
                    {
                        attributeValueItem.setValue( attributeValue.getValue() );
                        attributeService.updateAttributeValue( attributeValueItem );
                        attributeValue = null;
                    }
                }
            }
            
            if ( attributeValue != null && attributeValue.getValue() != null && !attributeValue.getValue().isEmpty())
            {
                attributeService.addAttributeValue( attributeValue );
                organisationUnit.getAttributeValues().add( attributeValue );
            }   
        }
        */
        
        return SUCCESS;
    }
    
    // method for Update Org Unit Attribute Values
    public void updateOrgUnitAttributeValues( Set<AttributeValue> attributeValues, List<Attribute> attributes )
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        String value = null;
        
        attributeValues.clear();
        
        
        for( Attribute tempAttribute : attributes )
        {
            
            AttributeValue attributeValue = new AttributeValue();
            value = request.getParameter( PREFIX_ATTRIBUTE + tempAttribute.getId() );
            
            String tempString = PREFIX_ATTRIBUTE + tempAttribute.getId();
            String attrId = tempString.substring( 4 );
            attributeValue.setId( Integer.parseInt( attrId ) );
            attributeValue.setValue( value );
            
            //System.out.println( attrId + " -- " + tempAttribute.getId() + " -- " + tempAttribute.getName() + " -- " + value ) ;
            
            Attribute attribute = attributeService.getAttribute( attributeValue.getId() );
            
            if ( attribute == null )
            {
                continue;
            }

            attributeValue.setAttribute( attribute );
            
            for ( AttributeValue attributeValueItem : attributeValues )
            {
                if ( attributeValueItem.getAttribute().getId() == attribute.getId() )
                {
                    if ( attributeValue.getValue() == null || attributeValue.getValue().length() == 0 )
                    {
                        attributeService.deleteAttributeValue( attributeValueItem );
                    }
                    else
                    {
                        attributeValueItem.setValue( attributeValue.getValue() );
                        attributeService.updateAttributeValue( attributeValueItem );
                        attributeValue = null;
                    }
                }
            }

            if ( attributeValue != null && attributeValue.getValue() != null && !attributeValue.getValue().isEmpty())
            {
                attributeService.addAttributeValue( attributeValue );
                attributeValues.add( attributeValue );
            }
        }
        
    }
    
    
    
    
}

