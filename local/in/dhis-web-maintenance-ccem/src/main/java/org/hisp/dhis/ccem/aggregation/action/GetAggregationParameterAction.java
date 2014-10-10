package org.hisp.dhis.ccem.aggregation.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelAttributeValueService;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;

import com.opensymphony.xwork2.Action;

public class GetAggregationParameterAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    private OptionService optionService;

    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }

    private ModelAttributeValueService modelAttributeValueService;

    public void setModelAttributeValueService( ModelAttributeValueService modelAttributeValueService )
    {
        this.modelAttributeValueService = modelAttributeValueService;
    }

    private ModelTypeAttributeService modelTypeAttributeService;

    public void setModelTypeAttributeService( ModelTypeAttributeService modelTypeAttributeService )
    {
        this.modelTypeAttributeService = modelTypeAttributeService;
    }

    private EquipmentTypeAttributeService equipmentTypeAttributeService;
    
    public void setEquipmentTypeAttributeService( EquipmentTypeAttributeService equipmentTypeAttributeService )
    {
        this.equipmentTypeAttributeService = equipmentTypeAttributeService;
    }

    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------
    private String aggTypeId;

    public void setAggTypeId( String aggTypeId )
    {
        this.aggTypeId = aggTypeId;
    }

    private Map<String, List<String>> lookUpParamMap;

    public Map<String, List<String>> getLookUpParamMap()
    {
        return lookUpParamMap;
    }

    List<Lookup> lookups;

    public List<Lookup> getLookups()
    {
        return lookups;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        lookUpParamMap = new HashMap<String, List<String>>();
        
        if( aggTypeId.equals( Lookup.CCEI_AGG_TYPE_STORAGE_CAPACITY ) )
        {

        }
        else if( aggTypeId.equals( Lookup.CCEI_AGG_TYPE_REF_UTILIZATION ) )
        {
            Lookup lookup = lookupService.getLookupByName( Lookup.CCEI_MODEL_MODELTYPEATTRIBUTE );
            
            ModelTypeAttribute mtAttribute = modelTypeAttributeService.getModelTypeAttribute( Integer.parseInt( lookup.getValue() ) );

            List<ModelAttributeValue> modelAttValueList = new ArrayList<ModelAttributeValue>( modelAttributeValueService.getAllModelAttributeValuesByModelTypeAttribute( mtAttribute ) );

            List<String> modelNameList = new ArrayList<String>();

            for ( ModelAttributeValue maValue : modelAttValueList )
            {
                modelNameList.add( maValue.getValue() );
            }

            lookUpParamMap.put( lookup.getName(), modelNameList );
            
            lookup = lookupService.getLookupByName( Lookup.CCEI_UTILIZATION_OPTIONSET );
            
            OptionSet os = optionService.getOptionSet( Integer.parseInt( lookup.getValue() ) );
            
            lookUpParamMap.put( lookup.getName(), os.getOptions() );
        }
        else if( aggTypeId.equals( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_MODEL ) )
        {
            Lookup lookup = lookupService.getLookupByName( Lookup.CCEI_WORKING_STATUS_OPTIONSET );
            
            OptionSet os = optionService.getOptionSet( Integer.parseInt( lookup.getValue() ) );
            
            lookUpParamMap.put( lookup.getName(), os.getOptions() );
            
            lookup = lookupService.getLookupByName( Lookup.CCEI_MODEL_MODELTYPEATTRIBUTE );
            
            ModelTypeAttribute mtAttribute = modelTypeAttributeService.getModelTypeAttribute( Integer.parseInt( lookup.getValue() ) );

            List<ModelAttributeValue> modelAttValueList = new ArrayList<ModelAttributeValue>( modelAttributeValueService.getAllModelAttributeValuesByModelTypeAttribute( mtAttribute ) );

            List<String> modelNameList = new ArrayList<String>();

            for ( ModelAttributeValue maValue : modelAttValueList )
            {
                modelNameList.add( maValue.getValue() );
            }

            lookUpParamMap.put( lookup.getName(), modelNameList );
        }
        else if( aggTypeId.equals( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_TYPE ) )
        {
            Lookup lookup = lookupService.getLookupByName( Lookup.CCEI_WORKING_STATUS_OPTIONSET );
            
            OptionSet os = optionService.getOptionSet( Integer.parseInt( lookup.getValue() ) );
            
            lookUpParamMap.put( lookup.getName(), os.getOptions() );

            lookup = lookupService.getLookupByName( Lookup.CCEI_EQUIPMENTTYPE_MODELTYPEATTRIBUTE );
            
            ModelTypeAttribute mtAttribute = modelTypeAttributeService.getModelTypeAttribute( Integer.parseInt( lookup.getValue() ) );

            lookUpParamMap.put( lookup.getName(), mtAttribute.getOptionSet().getOptions() );
            
            /*
            List<ModelAttributeValue> modelAttValueList = new ArrayList<ModelAttributeValue>( modelAttributeValueService.getAllModelAttributeValuesByModelTypeAttribute( mtAttribute ) );

            List<String> modelNameList = new ArrayList<String>();

            for ( ModelAttributeValue maValue : modelAttValueList )
            {
                modelNameList.add( maValue.getValue() );
            }
            */
        }

        
        return SUCCESS;
    }
}
