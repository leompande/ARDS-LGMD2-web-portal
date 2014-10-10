package org.hisp.dhis.coldchain.equipment.action;

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
   // private final String HEALTHFACILITY = "Health Facality";
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
    
    private List<Equipment> finalEquipmentList =  new ArrayList<Equipment>();;
    
    public List<Equipment> getFinalEquipmentList()
    {
        return finalEquipmentList;
    }
    
    String orgUnitIdsByComma;
    String searchBy = "";
    
    
    
    private List<String> filteredOrgUnitList = new ArrayList<String>();
    
    public void setFilteredOrgUnitList( List<String> filteredOrgUnitList )
    {
        this.filteredOrgUnitList = filteredOrgUnitList;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        //System.out.println("insde GetEquipmentListAction");
        
        //System.out.println(" Initial Size of filter OrgUnit List is : " + filteredOrgUnitList.size());
        
        // How to find duplicate words in a string in java
        /*
        String text = "a r b k c d se f g a d f s s f d s ft gh f ws w f v x s g h d h j j k f sd j e wed a d f";
        
        List<String> list = Arrays.asList(text.split(" "));
        
        Set<String> uniqueWords = new HashSet<String>(list);
        
        for (String word : uniqueWords) 
        {
            System.out.println(word + ": " + Collections.frequency( list, word ));
        }
        */
        
        equipmentDetailsMap = new HashMap<String, String>();
        
        // get orgUnit object usiging UID
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        //OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitId ) );
        
        
        //System.out.println(" orgUnit is : " + orgUnit.getId() + " -- " + orgUnit.getName() );
        
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
       
        //System.out.println(" Size of orgUnitList is : " + orgUnitList.size() );
        
       
        List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY ) ); 
        //OrganisationUnitGroup ouGroup = organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY );
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
        
        if ( orgUnitList.size() == 0 ) 
        {
            orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );  
        }
        
        //System.out.println(" Size of orgUnitList is : " + orgUnitList.size() + " -- " + orgUnitIdsByComma );
        
        if ( filteredOrgUnitList != null && filteredOrgUnitList.size() > 0 )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            
            Iterator<String> orGUnitIterator = filteredOrgUnitList.iterator();
            while ( orGUnitIterator.hasNext() )
            {
                int filterOrgUnitID = Integer.parseInt( (String) orGUnitIterator.next() );
                
                OrganisationUnit filterOrgUnit = organisationUnitService.getOrganisationUnit(  filterOrgUnitID  );
                
                orgUnitList.add( filterOrgUnit );
            }
            
            orgUnitIdsByComma = new String();
            
            orgUnitIdsByComma = "-1";
            
            for( OrganisationUnit orgnisationUnit : orgUnitList )
            {
                orgUnitIdsByComma += "," + orgnisationUnit.getId();
            }
            //System.out.println(" Size of filteredOrgUnitList is : " + filteredOrgUnitList.size() + " -- " + orgUnitIdsByComma );
        }
        
        //orgUnitList.add( orgUnit );
        
        //System.out.println(" Size of filteredOrgUnitList is : " + filteredOrgUnitList.size() + " -- " + orgUnitIdsByComma );
        
        equipmentType = equipmentTypeService.getEquipmentType( Integer.parseInt( equipmentTypeId ) );
        
        //equipmentType.getModelType().getName();
        
        //equipmentType.getDataSets().size();
        
        //System.out.println( " Size of initial OrgUnit List is : " + orgUnitList.size() + " -- initial filter orgUnit List " + filteredOrgUnitList.size() );
        
        if ( listAll != null && listAll )
        {
            if ( filteredOrgUnitList != null && filteredOrgUnitList.size() > 0 )
            {
                //System.out.println( " Size of filter OrgUnit List inside filter condition is : " + filteredOrgUnitList.size());
                
                orgUnitList = new ArrayList<OrganisationUnit>();
                
                Iterator<String> orGUnitIterator = filteredOrgUnitList.iterator();
                while ( orGUnitIterator.hasNext() )
                {
                    int filterOrgUnitID = Integer.parseInt( (String) orGUnitIterator.next() );
                    
                    OrganisationUnit filterOrgUnit = organisationUnitService.getOrganisationUnit(  filterOrgUnitID  );
                    
                    orgUnitList.add( filterOrgUnit );
                }
                
                listAllEquipment( orgUnitList, equipmentType );

                getEquipmentTypeAttributeData();
            }
            
            //listAllEquipment( orgUnit, equipmentType );
            listAllEquipment( orgUnitList, equipmentType );

            getEquipmentTypeAttributeData();
            
            //System.out.println(" Size of final EquipmentAttributeValue Instance List is : " + finalEquipmentList.size());
            
            return SUCCESS;
        }
        
        /*
        if (  ( filteredOrgUnitList != null ) && ( listAll != null && listAll ) )
        {
            System.out.println( " Size of filter OrgUnit List is : " + filteredOrgUnitList.size());
            
            orgUnitList = new ArrayList<OrganisationUnit>();
            
            Iterator<String> orGUnitIterator = filteredOrgUnitList.iterator();
            while ( orGUnitIterator.hasNext() )
            {
                int filterOrgUnitID = Integer.parseInt( (String) orGUnitIterator.next() );
                
                OrganisationUnit filterOrgUnit = organisationUnitService.getOrganisationUnit(  filterOrgUnitID  );
                
                orgUnitList.add( filterOrgUnit );
            }
            
            listAllEquipment( orgUnitList, equipmentType );

            getEquipmentTypeAttributeData();
            
            return SUCCESS;
        }
        
        */
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
        
        //listEquipmentsByFilter( orgUnitList, equipmentType, equipmentTypeAttribute, searchText);
        
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
            //equipment.getOrganisationUnit().getId();
            //equipment.getModel().getName();
            
            for( EquipmentType_Attribute equipmentTypeAttribute1 : equipmentTypeAttributeList )
            {
                EquipmentAttributeValue equipmentAttributeValueDetails = equipmentAttributeValueService.getEquipmentAttributeValue( equipment, equipmentTypeAttribute1.getEquipmentTypeAttribute() );
                if( equipmentAttributeValueDetails != null && equipmentAttributeValueDetails.getValue() != null )
                {
                    equipmentDetailsMap.put( equipment.getId()+":"+equipmentTypeAttribute1.getEquipmentTypeAttribute().getId(), equipmentAttributeValueDetails.getValue() );
                }
            }
            
            equipmentOrgUnitHierarchyMap.put( equipment.getOrganisationUnit().getId(), getHierarchyOrgunit( equipment.getOrganisationUnit() ) );
            
        }
    }
    //private void listAllEquipment( OrganisationUnit orgUnit, EquipmentType equipmentType ) 
    private void listAllEquipment( List<OrganisationUnit> orgUnitList, EquipmentType equipmentType )
    {
       
        total = equipmentService.getCountEquipment( orgUnitList, equipmentType );
        
        this.paging = createPaging( total );
        
        equipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( orgUnitList, equipmentType, paging.getStartPos(), paging.getPageSize() ) );
        
        //System.out.println(" Size of EquipmentAttributeValue Instance List is : " + equipmentList.size());
        
        
        /*
        int tempTotal;
        
        List <Equipment> tempequipmentList = new ArrayList<Equipment>();
        
        for ( OrganisationUnit orgUnit : orgUnitList )
        {
            tempTotal = equipmentService.getCountEquipment( orgUnit, equipmentType );
            
            this.paging = createPaging( tempTotal );

            //equipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( orgUnit, equipmentType, paging.getStartPos(), paging.getPageSize() ) );
            
            tempequipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( orgUnit, equipmentType, paging.getStartPos(), paging.getPageSize() ) );
            
           // System.out.println(" Size of temp EquipmentAttributeValue Instance List is : " + tempequipmentList.size());
            
            finalEquipmentList.addAll( tempequipmentList );
        }
        */
    }
    
    //private void listEquipmentsByFilter( OrganisationUnit orgUnit, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchKey )
    //private void listEquipmentsByFilter( List<OrganisationUnit> orgUnitList, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchKey )
    private void listEquipmentsByFilter( String orgUnitIdsByComma, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchKey ,String searchBy )
    {
        /*
        int tempTotal;
        
        List <Equipment> tempequipmentList = new ArrayList<Equipment>();
        
        
        for ( OrganisationUnit orgUnit : orgUnitList )
        {
            tempTotal =  total = equipmentService.getCountEquipment( orgUnit, equipmentType, equipmentTypeAttribute, searchText );
            
            this.paging = createPaging( tempTotal );

            //equipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( orgUnit, equipmentType, paging.getStartPos(), paging.getPageSize() ) );
            
            tempequipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( orgUnit, equipmentType, equipmentTypeAttribute, searchText, paging.getStartPos(), paging.getPageSize() ) );
            
           // System.out.println(" Size of temp EquipmentAttributeValue Instance List is : " + tempequipmentList.size());
            
            finalEquipmentList.addAll( tempequipmentList );
        }
        */
        
        
        total = equipmentService.getCountEquipment( orgUnitIdsByComma, equipmentType, equipmentTypeAttribute, searchText, searchBy );
        
        this.paging = createPaging( total );
        
        equipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( orgUnitIdsByComma, equipmentType, equipmentTypeAttribute, searchText, searchBy, paging.getStartPos(), paging.getPageSize() ) );
        
        
    }
    
    
    
    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
      //String hierarchyOrgunit = orgunit.getName();
        String hierarchyOrgunit = "";
       
        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + "/" + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }
        
        hierarchyOrgunit = hierarchyOrgunit.substring( hierarchyOrgunit.indexOf( "/" ) + 1 );
        
        return hierarchyOrgunit;
    }
    
    
}
