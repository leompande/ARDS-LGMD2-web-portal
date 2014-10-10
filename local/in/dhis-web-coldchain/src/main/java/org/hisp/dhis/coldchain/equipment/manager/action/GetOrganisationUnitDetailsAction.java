package org.hisp.dhis.coldchain.equipment.manager.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.paging.ActionPagingSupport;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetOrganisationUnitDetailsAction.javaSep 26, 2012 1:30:34 PM	
 */

public class GetOrganisationUnitDetailsAction  extends ActionPagingSupport<Equipment>
{
    
    //private final String HEALTHFACILITY = "Health Facality";
    
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
    
    private EquipmentTypeService equipmentTypeService;

    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
    }
    
    private EquipmentService equipmentService;

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }
   
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    
    private String orgUnitId;
    
    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    /*
    private Integer orgUnitId;
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    */
    
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private List<OrganisationUnit> orgUnitList;
    
    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    
    private Map<Integer, String> orgunitHierarchyMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getOrgunitHierarchyMap()
    {
        return orgunitHierarchyMap;
    }
    
    private List<Attribute> attributes;

    public List<Attribute> getAttributes()
    {
        return attributes;
    }
    /*
    public Map<Integer, String> attributeValues = new HashMap<Integer, String>();

    public Map<Integer, String> getAttributeValues()
    {
        return attributeValues;
    }
    */
    
    public Map<String, String> orgUnitAttribDataValueMap;
    
    public Map<String, String> getOrgUnitAttribDataValueMap()
    {
        return orgUnitAttribDataValueMap;
    }
    
    public Map<String, String> parentOrgUnitAttribDataValueMap;
    
    public Map<String, String> getParentOrgUnitAttribDataValueMap()
    {
        return parentOrgUnitAttribDataValueMap;
    }
    
    public Map<String, String> selectedOrgUnitAttribDataValueMap;
    
    public Map<String, String> getSelectedOrgUnitAttribDataValueMap()
    {
        return selectedOrgUnitAttribDataValueMap;
    }

    String orgUnitIdsByComma;
    String attributedsByComma;
    
    private List<EquipmentType> equipmentTypes;

    public List<EquipmentType> getEquipmentTypes()
    {
        return equipmentTypes;
    }
    
    private Boolean listFilterOrgUnit;
    
    public void setListFilterOrgUnit( Boolean listFilterOrgUnit )
    {
        this.listFilterOrgUnit = listFilterOrgUnit;
    }
    
    private String searchOrgText;
    
    public String getSearchOrgText()
    {
        return searchOrgText;
    }

    public void setSearchOrgText( String searchOrgText )
    {
        this.searchOrgText = searchOrgText;
    }

    /*
    private String searchAttributeText;
    
    public String getSearchAttributeText()
    {
        return searchAttributeText;
    }

    public void setSearchAttributeText( String searchAttributeText )
    {
        this.searchAttributeText = searchAttributeText;
    }
    */
    private String orgUnitAttributeId;
    
    public void setOrgUnitAttributeId( String orgUnitAttributeId )
    {
        this.orgUnitAttributeId = orgUnitAttributeId;
    }
    
    private List<OrganisationUnit> filteredOrgUnitList;
    
    public List<OrganisationUnit> getFilteredOrgUnitList()
    {
        return filteredOrgUnitList;
    }

    public void setFilteredOrgUnitList( List<OrganisationUnit> filteredOrgUnitList )
    {
        this.filteredOrgUnitList = filteredOrgUnitList;
    }
    
    
    private List<OrganisationUnitGroup> orgUnitGroups;
    
    public List<OrganisationUnitGroup> getOrgUnitGroups()
    {
        return orgUnitGroups;
    }

    private OrganisationUnitGroupSet organisationUnitGroupSet;
    
    public OrganisationUnitGroupSet getOrganisationUnitGroupSet()
    {
        return organisationUnitGroupSet;
    }

    private List<OrganisationUnit> orgList;
    
    
    private Map<Integer, String> orgUnitGroupNameMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getOrgUnitGroupNameMap()
    {
        return orgUnitGroupNameMap;
    }
    
    private String searchingOrgUnitFilterOptionId;
    
    public void setSearchingOrgUnitFilterOptionId( String searchingOrgUnitFilterOptionId )
    {
        this.searchingOrgUnitFilterOptionId = searchingOrgUnitFilterOptionId;
    }
    
    private Integer searchingOrgUnitGroupId;
    
    public void setSearchingOrgUnitGroupId( Integer searchingOrgUnitGroupId )
    {
        this.searchingOrgUnitGroupId = searchingOrgUnitGroupId;
    }
    
    private List<Integer> orgunitIds;
    
    public List<Integer> getOrgunitIds()
    {
        return orgunitIds;
    }
    
    private Integer total;
    
    public Integer getTotal()
    {
        return total;
    }
    
    private OrganisationUnitGroupSet organisationUnitOwnershipGroupSet;
    
    public OrganisationUnitGroupSet getOrganisationUnitOwnershipGroupSet()
    {
        return organisationUnitOwnershipGroupSet;
    }
    
    private List<OrganisationUnitGroup> orgUnitOwnershipGroupSetMember;
    
    public List<OrganisationUnitGroup> getOrgUnitOwnershipGroupSetMember()
    {
        return orgUnitOwnershipGroupSetMember;
    }
    
    private Map<Integer, String> orgUnitGroupSetOwnerShipNameMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getOrgUnitGroupSetOwnerShipNameMap()
    {
        return orgUnitGroupSetOwnerShipNameMap;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        orgUnitAttribDataValueMap = new HashMap<String, String>();
        selectedOrgUnitAttribDataValueMap = new HashMap<String, String>();
        parentOrgUnitAttribDataValueMap = new HashMap<String, String>();
        orgUnitList = new ArrayList<OrganisationUnit>();
        filteredOrgUnitList = new ArrayList<OrganisationUnit>();
        
        orgunitIds = new ArrayList<Integer>();
        
        orgList = new ArrayList<OrganisationUnit>();
        
        orgUnitIdsByComma = "-1";
        attributedsByComma = "-1";
        
        // get orgUnit object usiging UID
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        //organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        equipmentTypes = new ArrayList<EquipmentType>( equipmentTypeService.getAllEquipmentTypes() );
        
        
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() ) );
        
        List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY ) ); 
        OrganisationUnitGroup ouGroup = ouGroups.get( 0 );				
       
        if ( ouGroup != null )
        {
            orgUnitList.retainAll( ouGroup.getMembers() );
        }
        
        
        List<OrganisationUnitGroupSet> organisationUnitGroupSets = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getOrganisationUnitGroupSetByName( Model.NAME_FACILITY_TYPE ) );
        organisationUnitGroupSet = organisationUnitGroupSets.get(0);
        orgUnitGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupSet.getOrganisationUnitGroups() );
        
        
        
        List<OrganisationUnitGroupSet> orgUnitOwnerShipGroupSets = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getOrganisationUnitGroupSetByName( Model.NAME_OWNERSHIP_GROUP_SET ) );
        organisationUnitOwnershipGroupSet = orgUnitOwnerShipGroupSets.get(0);
        orgUnitOwnershipGroupSetMember = new ArrayList<OrganisationUnitGroup>( organisationUnitOwnershipGroupSet.getOrganisationUnitGroups() );
        
        /*
        if ( organisationUnitOwnershipGroupSet != null )
        {
            orgUnitGroups.addAll( orgUnitOwnershipGroupSetMember );
        }
        
        orgUnitGroups.addAll( orgUnitOwnershipGroupSetMember );
        */
        
        Collections.sort( orgUnitOwnershipGroupSetMember, new IdentifiableObjectNameComparator() );
        
        Collections.sort( orgUnitGroups, new IdentifiableObjectNameComparator() );
        
        /*
        for ( OrganisationUnitGroup organisationUnitGroup : orgUnitGroups )
        {
            System.out.println( organisationUnitGroup.getName() );
        }
        */
        
        //for ( OrganisationUnitGroup organisationUnitGroup : orgUnitGroups )
        //{
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitGroupSet.getOrganisationUnitGroups() )
                {
                    if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                    {
                        if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                        {
                            
                            orgUnitGroupNameMap.put( orgUnit.getId(), organisationUnitGroup.getName() );
                            break;
                        }
                    }
                }
                
                for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitOwnershipGroupSet.getOrganisationUnitGroups() )
                {
                    if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                    {
                        if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                        {
                            
                            orgUnitGroupSetOwnerShipNameMap.put( orgUnit.getId(), organisationUnitGroup.getName() );
                            break;
                        }
                    }
                }
                
                /*
                if( organisationUnitGroup.getMembers().contains( orgUnit ) );
                {
                    orgUnitGroupNameMap.put( orgUnit.getId(), organisationUnitGroup.getName() );
                }
                */
            }
            
            //orgList.addAll( organisationUnitGroup.getMembers() );
        //}
        
        
        
        //System.out.println( "-- Lavel of Root OrgUnit is -" + organisationUnitService.getLevelOfOrganisationUnit( 1 ) );
        
        //attributeValues = AttributeUtils.getAttributeValueMap( organisationUnit.getAttributeValues() );
        
        attributes = new ArrayList<Attribute>( attributeService.getOrganisationUnitAttributes() );
        Collections.sort( attributes, IdentifiableObjectNameComparator.INSTANCE );
        
        for ( Attribute attribute : attributes )
        {
           attributedsByComma += "," + attribute.getId();
           //System.out.println( orgUnit.getName()  +" -- " + attribute.getName() );
        }
        // value for selected orgUnit
        
        selectedOrgUnitAttribDataValueMap = new HashMap<String, String>( equipmentType_AttributeService.getOrgUnitAttributeDataValue( ""+organisationUnit.getId(), attributedsByComma ) );
        
        
        //search by attribute value
        if ( listFilterOrgUnit != null && listFilterOrgUnit )
        {
            
            if( searchingOrgUnitFilterOptionId.equalsIgnoreCase(  Model.NAME_FACILITY_TYPE ) || searchingOrgUnitFilterOptionId.equalsIgnoreCase(  Model.NAME_OWNERSHIP_GROUP_SET ) )
            {
                OrganisationUnitGroup filterOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( searchingOrgUnitGroupId );
                
                orgUnitList.retainAll( filterOrgUnitGroup.getMembers() );
                
                filteredOrgUnitList.addAll( orgUnitList ); 
                
                for( OrganisationUnit orgUnit : orgUnitList )
                {
                    for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitGroupSet.getOrganisationUnitGroups() )
                    {
                        if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                        {
                            if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                            {
                                orgUnitGroupNameMap.put( orgUnit.getId(), organisationUnitGroup.getName() );
                                break;
                            }
                        }
                    }
                    
                    for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitOwnershipGroupSet.getOrganisationUnitGroups() )
                    {
                        if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                        {
                            if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                            {
                                
                                orgUnitGroupSetOwnerShipNameMap.put( orgUnit.getId(), organisationUnitGroup.getName() );
                                break;
                            }
                        }
                    }
                    
                    orgunitHierarchyMap.put( orgUnit.getId(), getHierarchyOrgunit( orgUnit ) );
                }
                
                getOrganisationUnitAttributeData( );
                
                return SUCCESS;
                
            }
            
            else if( searchingOrgUnitFilterOptionId.equalsIgnoreCase(  EquipmentAttributeValue.PREFIX_ORGANISATIONUNIT_NAME ) )
            {
                //orgUnitList = new ArrayList<OrganisationUnit>();
                
                //orgUnitList = new ArrayList<OrganisationUnit>( equipmentService.searchOrgUnitListByName( searchOrgText ));
                
                orgUnitList.retainAll( equipmentService.searchOrgUnitListByName( searchOrgText ) );
                
                filteredOrgUnitList.addAll( orgUnitList ); 
                
                for( OrganisationUnit orgUnit : orgUnitList )
                {
                    for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitGroupSet.getOrganisationUnitGroups() )
                    {
                        if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                        {
                            if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                            {
                                orgUnitGroupNameMap.put( orgUnit.getId(), organisationUnitGroup.getName() );
                                break;
                            }
                        }
                    }
                    
                    for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitOwnershipGroupSet.getOrganisationUnitGroups() )
                    {
                        if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                        {
                            if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                            {
                                
                                orgUnitGroupSetOwnerShipNameMap.put( orgUnit.getId(), organisationUnitGroup.getName() );
                                break;
                            }
                        }
                    }
                    
                    orgunitHierarchyMap.put( orgUnit.getId(), getHierarchyOrgunit( orgUnit ) );
                }
                
                getOrganisationUnitAttributeData( );
                
                return SUCCESS;
            }
            
            else if( searchingOrgUnitFilterOptionId.equalsIgnoreCase(  EquipmentAttributeValue.PREFIX_ORGANISATIONUNIT_CODE ) )
            {
                //orgUnitList = new ArrayList<OrganisationUnit>();
                
                //orgUnitList = new ArrayList<OrganisationUnit>( equipmentService.searchOrgUnitListByName( searchOrgText ));
                
                orgUnitList.retainAll( equipmentService.searchOrgUnitListByCode( searchOrgText ) );
                
                filteredOrgUnitList.addAll( orgUnitList ); 
                
                for( OrganisationUnit orgUnit : orgUnitList )
                {
                    for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitGroupSet.getOrganisationUnitGroups() )
                    {
                        if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                        {
                            if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                            {
                                orgUnitGroupNameMap.put( orgUnit.getId(), organisationUnitGroup.getName() );
                                break;
                            }
                        }
                    }
                    
                    
                    for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitOwnershipGroupSet.getOrganisationUnitGroups() )
                    {
                        if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                        {
                            if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                            {
                                
                                orgUnitGroupSetOwnerShipNameMap.put( orgUnit.getId(), organisationUnitGroup.getName() );
                                break;
                            }
                        }
                    }
                    
                    orgunitHierarchyMap.put( orgUnit.getId(), getHierarchyOrgunit( orgUnit ) );
                }
                
                getOrganisationUnitAttributeData( );
                
                return SUCCESS;
            }
            
            else
            {
                //Attribute attribute = attributeService.getAttribute( Integer.parseInt( orgUnitAttributeId ) );
                
                //System.out.println( searchingOrgUnitFilterOptionId + " -- inside search by -- " + searchingOrgUnitFilterOptionId );
                
                Attribute attribute = attributeService.getAttribute( Integer.parseInt( searchingOrgUnitFilterOptionId )  );
                
                //System.out.println( searchingOrgUnitFilterOptionId + " -- inside search by -- " + attribute.getId() + " -- " + attribute.getName() );
                
                for( OrganisationUnit orgUnit : orgUnitList )
                {
                   orgUnitIdsByComma += "," + orgUnit.getId();
                   
                   orgunitIds.add( orgUnit.getId() );
                   
                   orgunitHierarchyMap.put( orgUnit.getId(), getHierarchyOrgunit( orgUnit ) );
                   
                   for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitGroupSet.getOrganisationUnitGroups() )
                   {
                       if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                       {
                           if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                           {
                               orgUnitGroupNameMap.put( orgUnit.getId(), organisationUnitGroup.getName() );
                               break;
                           }
                       }
                   }
                   
                   for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitOwnershipGroupSet.getOrganisationUnitGroups() )
                   {
                       if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                       {
                           if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                           {
                               
                               orgUnitGroupSetOwnerShipNameMap.put( orgUnit.getId(), organisationUnitGroup.getName() );
                               break;
                           }
                       }
                   }
                   
                }
                
                listOrganisationUnitByFilter( orgUnitIdsByComma, attribute, searchOrgText );
                
                //listOrganisationUnitByFilterForPaging( orgunitIds, attribute, searchOrgText );
                
                getOrganisationUnitAttributeData( );
                
                //System.out.println( orgUnitAttributeId + " -- inside search by -- " + attribute.getId() + "-- Final List Size is -" + orgUnitList.size() + "---" + searchOrgText );
                
                return SUCCESS; 
            }
           
        }
        
        for( OrganisationUnit orgUnit : orgUnitList )
        {
           orgUnitIdsByComma += "," + orgUnit.getId();
           
           //System.out.println(  orgUnit.getId() + "  ----  "  + orgUnit.getGroupIdInGroupSet( organisationUnitGroupSet ) );
           
           orgunitHierarchyMap.put( orgUnit.getId(), getHierarchyOrgunit( orgUnit ) );
        }
        
        orgUnitAttribDataValueMap = new HashMap<String, String>( equipmentType_AttributeService.getOrgUnitAttributeDataValue( orgUnitIdsByComma, attributedsByComma ) );
        
        /*
        if( organisationUnit.getParent() != null )
        {
            orgUnitAttribDataValueMap = new HashMap<String, String>( equipmentType_AttributeService.getOrgUnitAttributeDataValue( orgUnitIdsByComma, attributedsByComma ) );
            parentOrgUnitAttribDataValueMap = new HashMap<String, String>( equipmentType_AttributeService.getOrgUnitAttributeDataValue( ""+organisationUnit.getParent().getId(), attributedsByComma ) );
        }
        else
        {
            orgUnitAttribDataValueMap = new HashMap<String, String>( equipmentType_AttributeService.getOrgUnitAttributeDataValue( orgUnitIdsByComma, attributedsByComma ) );
            parentOrgUnitAttribDataValueMap = new HashMap<String, String>( equipmentType_AttributeService.getOrgUnitAttributeDataValue( ""+organisationUnit.getId(), attributedsByComma ) );
        }
        */
        
        return SUCCESS;
    }
    
    // supportive methods
    
    private void getOrganisationUnitAttributeData()
    {
        //System.out.println( "--- Result " + orgUnitList.size() );
        
        String tempOrgUnitIdsByComma = "-1";
        
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            tempOrgUnitIdsByComma += "," + orgUnit.getId();
        }
        
        orgUnitAttribDataValueMap = new HashMap<String, String>( equipmentType_AttributeService.getOrgUnitAttributeDataValue( tempOrgUnitIdsByComma, attributedsByComma ) );
    }
    
    private void listOrganisationUnitByFilter( String orgUnitIdsByComma, Attribute attribute, String searchAttributeText )
    {
        orgUnitList = new ArrayList<OrganisationUnit>();
        
        orgUnitList = new ArrayList<OrganisationUnit>( equipmentType_AttributeService.searchOrgUnitByAttributeValue( orgUnitIdsByComma, attribute, searchAttributeText ) );
        
        filteredOrgUnitList = new ArrayList<OrganisationUnit>( equipmentType_AttributeService.searchOrgUnitByAttributeValue( orgUnitIdsByComma, attribute, searchAttributeText ) );
        
        //List<OrganisationUnit> tempOrgUnitList = new ArrayList<OrganisationUnit>( equipmentType_AttributeService.searchOrgUnitByAttributeValue( orgUnitIdsByComma, attribute, searchAttributeText ) );
        /*
        System.out.println( "--- Action " + orgUnitList.size() );
       
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            System.out.println( "--- " + orgUnit.getId() + "----" + orgUnit.getName() );
        }
        */
        
        /*
        for( OrganisationUnit orgUnit : tempOrgUnitList )
        {
           orgunitHierarchyMap.put( orgUnit.getId(), getHierarchyOrgunit( orgUnit ) );
        }
        */
    
    }
     
    
    private void listOrganisationUnitByFilterForPaging( List<Integer> orgunitIds, Attribute attribute, String searchAttributeText )
    {
        orgUnitList = new ArrayList<OrganisationUnit>();
        
        total = equipmentService.countOrgUnitByAttributeValue( orgunitIds, attribute, searchAttributeText );
        
        this.paging = createPaging( total );
        
        orgUnitList = new ArrayList<OrganisationUnit>( equipmentService.searchOrgUnitByAttributeValue( orgunitIds, attribute, searchAttributeText, paging.getStartPos(), paging.getPageSize() ) );
        
        /*
        orgUnitList = new ArrayList<OrganisationUnit>();
        
        orgUnitList = new ArrayList<OrganisationUnit>( equipmentType_AttributeService.searchOrgUnitByAttributeValue( orgUnitIdsByComma, attribute, searchAttributeText ) );
        
        filteredOrgUnitList = new ArrayList<OrganisationUnit>( equipmentType_AttributeService.searchOrgUnitByAttributeValue( orgUnitIdsByComma, attribute, searchAttributeText ) );
        */
    
    }
     
    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        //String hierarchyOrgunit = orgunit.getName();
        String hierarchyOrgunit = "";
       
        while ( orgunit.getParent() != null )
        {
            /*
            if( organisationUnitService.getLevelOfOrganisationUnit( orgunit.getId() ) == -1 )
            {
                break;
            }
            */
            
            hierarchyOrgunit = orgunit.getParent().getName() + "/" + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }
        
        hierarchyOrgunit = hierarchyOrgunit.substring( hierarchyOrgunit.indexOf( "/" ) + 1 );
        
        return hierarchyOrgunit;
    }
        
}
