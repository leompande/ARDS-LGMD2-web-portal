package org.hisp.dhis.dataentry.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.equipment.EquipmentDataValue;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValueService;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.comparator.SectionOrderComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class LoadDataEntryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private EquipmentService equipmentService;
    
    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private EquipmentDataValueService equipmentDataValueService;
    
    public void setEquipmentDataValueService( EquipmentDataValueService equipmentDataValueService )
    {
        this.equipmentDataValueService = equipmentDataValueService;
    }
   
    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }
    

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private int dataSetId;
    
    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private int equipmentId;
    
    public void setEquipmentId( int equipmentId )
    {
        this.equipmentId = equipmentId;
    }
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    private Map<Integer, String> equipmentDataValueMap;
    
    public Map<Integer, String> getEquipmentDataValueMap()
    {
        return equipmentDataValueMap;
    }
    
    private List<Section> sections;

    public List<Section> getSections()
    {
        return sections;
    }
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }

    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        Equipment equipment = equipmentService.getEquipment( equipmentId );
        
        //Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        Period period = PeriodType.getPeriodFromIsoString( selectedPeriodId );
        
        dataSet = dataSetService.getDataSet( dataSetId );
        
        dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        Collections.sort( dataElements, dataElementComparator );
        
        List<EquipmentDataValue> equipmentDataValues = new ArrayList<EquipmentDataValue>( equipmentDataValueService.getEquipmentDataValues( equipment, period, dataElements ) );      
        
        if ( equipmentDataValues != null && equipmentDataValues.size() > 0 )
        {
            
            equipmentDataValueMap = new HashMap<Integer, String>();
            
            for( EquipmentDataValue equipmentDataValue : equipmentDataValues )
            {
                equipmentDataValueMap.put( equipmentDataValue.getDataElement().getId(), equipmentDataValue.getValue() );
            }
        }
        
        sections = new ArrayList<Section>( dataSet.getSections() );
        
        Collections.sort( sections, new SectionOrderComparator() );
        
        return SUCCESS;
    }

}
