 package org.hisp.dhis.ccem.equipmenttype.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;

import com.opensymphony.xwork2.Action;

public class UpdateEquipmentTypeDataSetAction  implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentTypeService equipmentTypeService;

    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
    }
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    
    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    
    private List<Integer> selectedEquipmentTypeDataSetList = new ArrayList<Integer>();
    
    public void setSelectedEquipmentTypeDataSetList( List<Integer> selectedEquipmentTypeDataSetList )
    {
        this.selectedEquipmentTypeDataSetList = selectedEquipmentTypeDataSetList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        
        EquipmentType equipmentType = equipmentTypeService.getEquipmentType( id );
        
        equipmentType.setName( name );
        
        
        Set<DataSet> equipmentTypeDataSet = new HashSet<DataSet>();
        
        if ( selectedEquipmentTypeDataSetList != null && selectedEquipmentTypeDataSetList.size() > 0 )
        {
            for ( int i = 0; i < this.selectedEquipmentTypeDataSetList.size(); i++ )
            {
                DataSet dataSet = dataSetService.getDataSet( selectedEquipmentTypeDataSetList.get( i ) );
                
                /*
                System.out.println( "ID---" + dataSet.getId() );
                System.out.println( "Name---" + dataSet.getName());
                System.out.println( "Display Name---" + dataSet.getDisplayName() );
                */
                equipmentTypeDataSet.add( dataSet );
                
            }
        }
        
        equipmentType.setDataSets( equipmentTypeDataSet );
        
        equipmentTypeService.updateEquipmentType( equipmentType );
        
        
        return SUCCESS;
    }
}

