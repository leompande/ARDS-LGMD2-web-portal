package org.hisp.dhis.ccem.equipmenttype.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeService;

import com.opensymphony.xwork2.Action;

public class AddEquipmentTypeAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentTypeService equipmentTypeService;

    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
    }
    
    private ModelTypeService modelTypeService;

    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }
    
    private EquipmentTypeAttributeService equipmentTypeAttributeService;
    
    public void setEquipmentTypeAttributeService( EquipmentTypeAttributeService equipmentTypeAttributeService )
    {
        this.equipmentTypeAttributeService = equipmentTypeAttributeService;
    }
    
    private EquipmentType_AttributeService equipmentType_AttributeService;
   
    public void setEquipmentType_AttributeService( EquipmentType_AttributeService equipmentType_AttributeService )
    {
        this.equipmentType_AttributeService = equipmentType_AttributeService;
    }
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private Integer modelType;

    public void setModelType( Integer modelType )
    {
        this.modelType = modelType;
    }

    private boolean tracking;

    public void setTracking( boolean tracking )
    {
        this.tracking = tracking;
    }
    
    /*
    private List<Integer> selectedEquipmentTypeAttributeList = new ArrayList<Integer>();
    
    public void setSelectedEquipmentTypeAttributeList( List<Integer> selectedEquipmentTypeAttributeList )
    {
        this.selectedEquipmentTypeAttributeList = selectedEquipmentTypeAttributeList;
    }
    */
    
    private List<Integer> selectedEquipmentTypeAttributeValidator = new ArrayList<Integer>();
    
    public void setSelectedEquipmentTypeAttributeValidator( List<Integer> selectedEquipmentTypeAttributeValidator )
    {
        this.selectedEquipmentTypeAttributeValidator = selectedEquipmentTypeAttributeValidator;
    }

    private List<Boolean> display = new ArrayList<Boolean>();
    
    public void setDisplay( List<Boolean> display )
    {
        this.display = display;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        EquipmentType equipmentType = new EquipmentType();
        
        equipmentType.setName( name );
        equipmentType.setDescription( description );
        equipmentType.setTracking( tracking );
        
        if( modelType != null )
        {
            equipmentType.setModelType( modelTypeService.getModelType( modelType ) );
        }
        
        //Set<EquipmentTypeAttribute> equipmentTypeSet = new HashSet<EquipmentTypeAttribute>();
        
        List<EquipmentTypeAttribute> equipmentTypeSet = new ArrayList<EquipmentTypeAttribute>();
        
        if ( selectedEquipmentTypeAttributeValidator != null && selectedEquipmentTypeAttributeValidator.size() > 0 )
        {
            for ( int i = 0; i < this.selectedEquipmentTypeAttributeValidator.size(); i++ )
            {
                
                EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttribute( selectedEquipmentTypeAttributeValidator.get( i ) );
                
                equipmentTypeSet.add( equipmentTypeAttribute );
                
               //equipmentTypeSet.add( equipmentTypeAttributeService.getEquipmentTypeAttribute( selectedEquipmentTypeAttributeList.get( i ) ) );
            }
            /*
            for( Integer equipmentTypeAttId : selectedEquipmentTypeAttributeList )
            {
                equipmentTypeSet.add( equipmentTypeAttributeService.getEquipmentTypeAttribute( equipmentTypeAttId ) );
            }
            */
        }
        
        //equipmentType.setEquipmentTypeAttributes( equipmentTypeSet );
        
        equipmentTypeService.addEquipmentType( equipmentType );
        
        if ( selectedEquipmentTypeAttributeValidator != null && selectedEquipmentTypeAttributeValidator.size() > 0 )
        {
            for ( int i = 0; i < this.selectedEquipmentTypeAttributeValidator.size(); i++ )
            {
                EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttribute( selectedEquipmentTypeAttributeValidator.get( i ) );
                
                EquipmentType_Attribute equipmentType_Attribute = new EquipmentType_Attribute( equipmentType,  equipmentTypeAttribute, this.display.get( i ), new Integer( i ) );
                
                equipmentType_AttributeService.addEquipmentType_Attribute( equipmentType_Attribute );
            }
        }
        
        return SUCCESS;
    }
}
