package org.hisp.dhis.ccem.equipment.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValueService;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.paging.ActionPagingSupport;

public class GetEquipmentListAction  extends ActionPagingSupport<Equipment>
{
    private final String CLINIC = "Medical Clinic  Private";
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private EquipmentService equipmentService;

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }
    
    private EquipmentTypeService equipmentTypeService;
    
    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private EquipmentTypeAttributeService equipmentTypeAttributeService;
    
    public void setEquipmentTypeAttributeService( EquipmentTypeAttributeService equipmentTypeAttributeService )
    {
        this.equipmentTypeAttributeService = equipmentTypeAttributeService;
    }
    
    private EquipmentAttributeValueService equipmentAttributeValueService;

    public void setEquipmentAttributeValueService( EquipmentAttributeValueService equipmentAttributeValueService )
    {
        this.equipmentAttributeValueService = equipmentAttributeValueService;
    }
    
    private EquipmentType_AttributeService equipmentType_AttributeService;
    
    public void setEquipmentType_AttributeService( EquipmentType_AttributeService equipmentType_AttributeService )
    {
        this.equipmentType_AttributeService = equipmentType_AttributeService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<Equipment> equipmentList;

    public List<Equipment> getEquipmentList()
    {
        return equipmentList;
    }
    
    private EquipmentType equipmentType;
    
    public EquipmentType getEquipmentType()
    {
        return equipmentType;
    }

    private String orgUnitId;
    
    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String equipmentTypeId;

    public void setEquipmentTypeId( String equipmentTypeId )
    {
        this.equipmentTypeId = equipmentTypeId;
    }

    private String equipmentTypeAttributeId;
    
    public void setEquipmentTypeAttributeId( String equipmentTypeAttributeId )
    {
        this.equipmentTypeAttributeId = equipmentTypeAttributeId;
    }

    private Boolean listAll;
    
    public void setListAll( Boolean listAll )
    {
        this.listAll = listAll;
    }

    private Integer total;
    
    public Integer getTotal()
    {
        return total;
    }
    
    private String searchText;
    
    public String getSearchText()
    {
        return searchText;
    }

    public void setSearchText( String searchText )
    {
        this.searchText = searchText;
    }

    public Map<String, String> equipmentDetailsMap;
    
    public Map<String, String> getEquipmentDetailsMap()
    {
        return equipmentDetailsMap;
    }
    
    /*
    public List<EquipmentTypeAttribute> equipmentTypeAttributeList;
    
    public List<EquipmentTypeAttribute> getEquipmentTypeAttributeList()
    {
        return equipmentTypeAttributeList;
    }
    */
    
    public List<EquipmentType_Attribute> equipmentTypeAttributeList;
    
    public List<EquipmentType_Attribute> getEquipmentTypeAttributeList()
    {
        return equipmentTypeAttributeList;
    }
    
    private Map<Integer, String> equipmentOrgUnitHierarchyMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getEquipmentOrgUnitHierarchyMap()
    {
        return equipmentOrgUnitHierarchyMap;
    }
    
    private List<OrganisationUnit> orgUnitList;
    
    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    
    String orgUnitIdsByComma;
    String searchBy = "";
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        //System.out.println("insde GetEquipmentListAction");
        
        equipmentDetailsMap = new HashMap<String, String>();
        
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitId ) );
        
        equipmentType = equipmentTypeService.getEquipmentType( Integer.parseInt( equipmentTypeId ) );
        
        
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
        
		List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( CLINIC ) ); 
        OrganisationUnitGroup ouGroup = ouGroups.get( 0 );
       
        if ( ouGroup != null )
        {
            orgUnitList.retainAll( ouGroup.getMembers() );
        }
        
        orgUnitIdsByComma = "-1";
        for( OrganisationUnit orgnisationUnit : orgUnitList )
        {
            orgUnitIdsByComma += "," + orgnisationUnit.getId();
        }
        
        
        if ( listAll != null && listAll )
        {
            //listAllEquipment( orgUnit, equipmentType );
            
            listAllEquipment( orgUnitList, equipmentType );
            
            getEquipmentTypeAttributeData();
            
            return SUCCESS;
        }
        
        if( equipmentTypeAttributeId.equalsIgnoreCase(  EquipmentAttributeValue.PREFIX_MODEL_NAME ))
        {
            //System.out.println( equipmentTypeAttributeId + " -- inside search by -- " + EquipmentAttributeValue.PREFIX_MODEL_NAME );
            
            searchBy = equipmentTypeAttributeId;
            
            listEquipmentsByFilter( orgUnitIdsByComma, equipmentType, null, searchText, searchBy );
            
            getEquipmentTypeAttributeData();
            
            return SUCCESS;
        }
        
        if ( equipmentTypeAttributeId.equalsIgnoreCase( EquipmentAttributeValue.PREFIX_ORGANISATIONUNIT_NAME ))
        {
            //System.out.println( equipmentTypeAttributeId + " -- inside search by -- " + EquipmentAttributeValue.PREFIX_ORGANISATIONUNIT_NAME );
            
            searchBy = equipmentTypeAttributeId;
            
            listEquipmentsByFilter( orgUnitIdsByComma, equipmentType, null, searchText, searchBy );
            
            getEquipmentTypeAttributeData();
            
            return SUCCESS;
        }
        
        
        EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttribute( Integer.parseInt( equipmentTypeAttributeId ) );
        
        //listEquipmentsByFilter( orgUnit, equipmentType, equipmentTypeAttribute, searchText);
        
        listEquipmentsByFilter( orgUnitIdsByComma, equipmentType, equipmentTypeAttribute, searchText , "" );
        
        getEquipmentTypeAttributeData();
        
        return SUCCESS;
    }
    
    // supportive methods
    
    private void getEquipmentTypeAttributeData()
    {
        //EquipmentTypeAttribute tempEquipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttribute( Integer.parseInt( equipmentTypeAttributeId ) );
        
        //equipmentTypeAttributeList = new ArrayList<EquipmentTypeAttribute>( equipmentTypeService.getAllEquipmentTypeAttributesForDisplay( equipmentType ));
        
        equipmentTypeAttributeList = new ArrayList<EquipmentType_Attribute>( equipmentType_AttributeService.getAllEquipmentTypeAttributeForDisplay( equipmentType, true ) );
       
        /*
        List<EquipmentTypeAttribute> tempequipmentTypeAttributeList = new ArrayList<EquipmentTypeAttribute>( equipmentType.getEquipmentTypeAttributes() ) ;
        
        if( tempequipmentTypeAttributeList != null )
        {
            
            for( EquipmentTypeAttribute tempEquipmentTypeAttribute : tempequipmentTypeAttributeList )
            {
                if ( tempEquipmentTypeAttribute.isDisplay() )
                {
                    equipmentTypeAttributeList.add( tempEquipmentTypeAttribute );
                }
            }
        }
        */
        //equipmentTypeAttributeList = new ArrayList<EquipmentTypeAttribute>( equipmentType.getEquipmentTypeAttributes() );
       
        //System.out.println("size of equipmentTypeAttributeList " + equipmentTypeAttributeList.size() );
        
        if( equipmentTypeAttributeList == null || equipmentTypeAttributeList.size() == 0  )
        {
            //equipmentTypeAttributeList = new ArrayList<EquipmentTypeAttribute>( equipmentType.getEquipmentTypeAttributes() );
            
            equipmentTypeAttributeList = new ArrayList<EquipmentType_Attribute>( equipmentType_AttributeService.getAllEquipmentTypeAttributesByEquipmentType( equipmentType ) );
            
            //Collections.sort( equipmentTypeAttributeList, new EquipmentTypeAttributeMandatoryComparator() );
            if( equipmentTypeAttributeList != null && equipmentTypeAttributeList.size() > 3 )
            {
                int count = 1;
                //Iterator<EquipmentTypeAttribute> iterator = equipmentTypeAttributeList.iterator();
                Iterator<EquipmentType_Attribute> iterator = equipmentTypeAttributeList.iterator();
                while( iterator.hasNext() )
                {
                    iterator.next();
                    
                    if( count > 3 )
                        iterator.remove();
                    
                    count++;
                }            
            }
            
        }

        for( Equipment equipment : equipmentList )
        {
            for( EquipmentType_Attribute equipmentTypeAttribute1 : equipmentTypeAttributeList )
            {
                EquipmentAttributeValue EquipmentAttributeValueDetails = equipmentAttributeValueService.getEquipmentAttributeValue( equipment, equipmentTypeAttribute1.getEquipmentTypeAttribute() );
                if( EquipmentAttributeValueDetails != null && EquipmentAttributeValueDetails.getValue() != null )
                {
                    equipmentDetailsMap.put( equipment.getId()+":"+equipmentTypeAttribute1.getEquipmentTypeAttribute().getId(), EquipmentAttributeValueDetails.getValue() );
                }
            }
            equipmentOrgUnitHierarchyMap.put( equipment.getOrganisationUnit().getId(), getHierarchyOrgunit( equipment.getOrganisationUnit() ) );
        }
    }
    
    /*
    private void listAllEquipment( OrganisationUnit orgUnit, EquipmentType equipmentType )
    {
        total = equipmentService.getCountEquipment( orgUnit, equipmentType );
        
        this.paging = createPaging( total );

        equipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( orgUnit, equipmentType, paging.getStartPos(), paging
            .getPageSize() ) );
    }
    
    private void listEquipmentsByFilter( OrganisationUnit orgUnit, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchKey )
    {
        total = equipmentService.getCountEquipment( orgUnit, equipmentType, equipmentTypeAttribute, searchText );
        
        this.paging = createPaging( total );
        
        equipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( orgUnit, equipmentType, equipmentTypeAttribute, searchText, paging.getStartPos(), paging.getPageSize() ) );
    }
    */
    
    private void listAllEquipment( List<OrganisationUnit> orgUnitList, EquipmentType equipmentType )
    {
        total = equipmentService.getCountEquipment( orgUnitList, equipmentType );
        
        this.paging = createPaging( total );
        
        equipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( orgUnitList, equipmentType, paging.getStartPos(), paging.getPageSize() ) );
    }    
    
    
    private void listEquipmentsByFilter( String orgUnitIdsByComma, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchKey ,String searchBy )
    {
        total = equipmentService.getCountEquipment( orgUnitIdsByComma, equipmentType, equipmentTypeAttribute, searchText, searchBy );
        
        this.paging = createPaging( total );
        
        equipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( orgUnitIdsByComma, equipmentType, equipmentTypeAttribute, searchText, searchBy, paging.getStartPos(), paging.getPageSize() ) );
        
    }
        
    
    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = "";
       
        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + "/" + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }
}
