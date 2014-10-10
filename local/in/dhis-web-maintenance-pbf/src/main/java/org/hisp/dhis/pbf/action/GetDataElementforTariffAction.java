package org.hisp.dhis.pbf.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class GetDataElementforTariffAction
    implements Action
{
    private final static String TARIFF_SETTING_AUTHORITY = "TARIFF_SETTING_AUTHORITY";

    private final static String TARIFF_DATAELEMENT = "TARIFF_DATAELEMENT";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private List<String> dataElementList = new ArrayList<String>();

    public List<String> getDataElementList()
    {
        return dataElementList;
    }

    public void setDataElementList( List<String> dataElementList )
    {
        this.dataElementList = dataElementList;
    }

    private String tariff_setting_authority;

    public String getTariff_setting_authority()
    {
        return tariff_setting_authority;
    }

    private List<String> levelOrgUnitIds = new ArrayList<String>();

    public List<String> getLevelOrgUnitIds()
    {
        return levelOrgUnitIds;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Constant tariff_authority = constantService.getConstantByName( TARIFF_SETTING_AUTHORITY );
        
        Constant tariffDataElement = constantService.getConstantByName( TARIFF_DATAELEMENT );
        if ( tariff_authority == null )
        {
            tariff_setting_authority = "Level 3";
            List<OrganisationUnit> allLevelOrg = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitsAtLevel( 3 ) );
            for ( OrganisationUnit org : allLevelOrg )
            {
                levelOrgUnitIds.add( "\"" + org.getUid() + "\"" );
            }
        }
        else
        {
            tariff_setting_authority = "Level " + (int) tariff_authority.getValue();
            List<OrganisationUnit> allLevelOrg = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitsAtLevel( (int) tariff_authority.getValue() ) );
            for ( OrganisationUnit org : allLevelOrg )
            {
                levelOrgUnitIds.add( "\"" + org.getUid() + "\"" );
            }
        }
        
        List<DataElement> dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );

        for ( DataElement de : dataElements )
        {
            Set<AttributeValue> attrValueSet = new HashSet<AttributeValue>( de.getAttributeValues() );
            for ( AttributeValue attValue : attrValueSet )
            {
                if ( dataElementList != null && !( dataElementList.contains( "{\"name\" : \"" + de.getName() + "\"}" ) )
                    && attValue.getAttribute().getId() == tariffDataElement.getValue() )
                {
                    dataElementList.add( "{\"name\" : \"" + de.getName() + "\"}" );
                }
            }
        }

        return SUCCESS;
    }
}