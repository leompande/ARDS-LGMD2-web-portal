package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValueService;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.equipment.comparator.EquipmentTypeAttributeMandatoryComparator;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.opensymphony.xwork2.Action;

public class ShowEquipmentDataEntryFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    /*
    private EquipmentDataValueService equipmentDataValueService;
    
    public void setEquipmentDataValueService( EquipmentDataValueService equipmentDataValueService )
    {
        this.equipmentDataValueService = equipmentDataValueService;
    }
    */
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
    private EquipmentAttributeValueService equipmentAttributeValueService;
    
    public void setEquipmentAttributeValueService( EquipmentAttributeValueService equipmentAttributeValueService )
    {
        this.equipmentAttributeValueService = equipmentAttributeValueService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer equipmentId;

    public void setEquipmentId( Integer equipmentId )
    {
        this.equipmentId = equipmentId;
    }
    
    public Integer getEquipmentId()
    {
        return equipmentId;
    }
    /*
    private Integer dataSetId;
    
    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    
    private Integer periodId;
    
    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }
    */
    private List<DataSet> dataSetList;
    
    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private EquipmentType equipmentType;
    
    public EquipmentType getEquipmentType()
    {
        return equipmentType;
    }

    public List<EquipmentTypeAttribute> equipmentTypeAttributeList;
    
    public List<EquipmentTypeAttribute> getEquipmentTypeAttributeList()
    {
        return equipmentTypeAttributeList;
    }
    
    private Map<String, String> inventryTypeAttributeAndValueMap;
    
    public Map<String, String> getInventryTypeAttributeAndValueMap()
    {
        return inventryTypeAttributeAndValueMap;
    }

    private String equipmentTypeAttributeNameValue;
    
    public String getEquipmentTypeAttributeNameValue()
    {
        return equipmentTypeAttributeNameValue;
    }

    private String equipmentTypeAttributeName;
    
    public String getEquipmentTypeAttributeName()
    {
        return equipmentTypeAttributeName;
    }

    private String equipmentTypeAttributeValue;
    
    public String getEquipmentTypeAttributeValue()
    {
        return equipmentTypeAttributeValue;
    }
    
    private String modelName;
    
    public String getModelName()
    {
        return modelName;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        
        Equipment equipment = equipmentService.getEquipment( equipmentId );
        
        if( equipment.getModel()!= null )
        {
            modelName = equipment.getModel().getName();
            //System.out.println( "Model Name is : -- " + equipment.getModel().getName() );
        }
        
        else
        {
            modelName = " ";
        }
        
        //equipment.getModel().getName();
        //equipment.getEquipmentType().getDataSets();
        
        organisationUnit = equipment.getOrganisationUnit();
        equipmentType =  equipment.getEquipmentType();
        dataSetList = new ArrayList<DataSet>(  equipment.getEquipmentType().getDataSets() );
        
        Collections.sort( dataSetList, IdentifiableObjectNameComparator.INSTANCE );
        /*
        for( DataSet dataSet : dataSetList )
        {
            System.out.println( dataSet.getPeriodType().getId() +"--------" +dataSet.getPeriodType().getName());
        }
        */
      
        equipmentTypeAttributeList = new ArrayList<EquipmentTypeAttribute>( equipmentTypeService.getAllEquipmentTypeAttributesForDisplay( equipmentType ));
        
        if( equipmentTypeAttributeList == null || equipmentTypeAttributeList.size() == 0  )
        {
            equipmentTypeAttributeList = new ArrayList<EquipmentTypeAttribute>( );
            for( EquipmentType_Attribute equipmentType_Attribute : equipmentType.getEquipmentType_Attributes() )
            {
                equipmentTypeAttributeList.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
            }
            
            Collections.sort( equipmentTypeAttributeList, new EquipmentTypeAttributeMandatoryComparator() );
            if( equipmentTypeAttributeList != null && equipmentTypeAttributeList.size() > 3 )
            {
                int count = 1;
                Iterator<EquipmentTypeAttribute> iterator = equipmentTypeAttributeList.iterator();
                while( iterator.hasNext() )
                {
                    iterator.next();
                    
                    if( count > 3 )
                        iterator.remove();
                    
                    count++;
                }            
            }
            
        }
        //List<EquipmentAttributeValue> equipmentDetailsList = new ArrayList<EquipmentAttributeValue>( equipmentAttributeValueService.getEquipments( equipment ) );
        
        //inventryTypeAttributeAndValueMap = new HashMap<String, String>();
        
        //inventryTypeAttributeAndValueMap.putAll( equipmentAttributeValueService.inventryTypeAttributeAndValue( equipment, equipmentTypeAttributeList ));
        
        /*
        equipmentTypeAttributeNameValue = equipmentAttributeValueService.inventryTypeAttributeAndValue( equipment, equipmentTypeAttributeList );
        String[] tempNameValue = equipmentTypeAttributeNameValue.split( "#@#" ); 
       
        equipmentTypeAttributeName = tempNameValue[0];
        
        equipmentTypeAttributeValue = tempNameValue[1];
        */
        
        //System.out.println( equipmentTypeAttributeName + "---" + equipmentTypeAttributeValue );
        
       /*
        for( EquipmentTypeAttribute equipmentTypeAttribute : equipmentTypeAttributeList )
        {
            
            System.out.println( equipmentTypeAttribute.getName() + "---" + inventryTypeAttributeAndValueMap.get( equipmentTypeAttribute.getName()) );
            
            
            EquipmentAttributeValue equipmentDetails = equipmentAttributeValueService.getEquipment( equipment, equipmentTypeAttribute );
            if( equipmentDetails != null && equipmentDetails.getValue() != null )
            {
                //System.out.println( equipmentTypeAttribute.getName() + "---" + equipmentDetails.getValue() );
                
                
                //equipmentDetailsMap.put( equipment.getId()+":"+equipmentTypeAttribute1.getId(), equipmentDetails.getValue() );
            }
            
        }
       */
        /*
        
        for( EquipmentAttributeValue equipmentDetails : equipmentDetailsList )
        {
            if ( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentDetails.getEquipmentTypeAttribute().getValueType() ) )
            {
                equipmentValueMap.put( equipmentDetails.getEquipmentTypeAttribute().getId(), equipmentDetails.getEquipmentTypeAttributeOption().getName() );
            }
            else
            {
                equipmentValueMap.put( equipmentDetails.getEquipmentTypeAttribute().getId(), equipmentDetails.getValue() );
            }
        }
        */
        
        // data entry parts
        
        return SUCCESS;
    }

}
