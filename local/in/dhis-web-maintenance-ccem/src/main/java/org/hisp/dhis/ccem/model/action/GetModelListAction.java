package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelAttributeValueService;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.paging.ActionPagingSupport;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetModelListAction.java Jun 22, 2012 3:30:36 PM	
 */
public class GetModelListAction extends ActionPagingSupport<Model>
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
   
    private ModelService modelService;
    
    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }
    
    private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }
    
    private ModelTypeAttributeService modelTypeAttributeService;
    
    public void setModelTypeAttributeService( ModelTypeAttributeService modelTypeAttributeService )
    {
        this.modelTypeAttributeService = modelTypeAttributeService;
    }
    
    private ModelAttributeValueService modelAttributeValueService;
    
    public void setModelAttributeValueService( ModelAttributeValueService modelAttributeValueService )
    {
        this.modelAttributeValueService = modelAttributeValueService;
    }
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    /*
    private int modelTypeId;
    
    public void setModelTypeId( int modelTypeId )
    {
        this.modelTypeId = modelTypeId;
    }
    */
    
    private String modelTypeId;
    
    public void setModelTypeId( String modelTypeId )
    {
        this.modelTypeId = modelTypeId;
    }

    private Boolean listAll;
    
    public void setListAll( Boolean listAll )
    {
        this.listAll = listAll;
    }
    
    private ModelType modelType;
    
    public ModelType getModelType()
    {
        return modelType;
    }
    
    private Integer total;
    
    public Integer getTotal()
    {
        return total;
    }
    
    private List<Model> modelList;
    
    public List<Model> getModelList()
    {
        return modelList;
    }
    /*
    private int modelTypeAttributeId;
    
    public void setModelTypeAttributeId( int modelTypeAttributeId )
    {
        this.modelTypeAttributeId = modelTypeAttributeId;
    }
    */
    
    private String modelTypeAttributeId;
    
    public void setModelTypeAttributeId( String modelTypeAttributeId )
    {
        this.modelTypeAttributeId = modelTypeAttributeId;
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
    
    public List<ModelTypeAttribute> modelTypeAttributeList;
    
    public List<ModelTypeAttribute> getModelTypeAttributeList()
    {
        return modelTypeAttributeList;
    }
    
    public Map<String, String> modelAttributeValueMap;
    
    public Map<String, String> getModelAttributeValueMap()
    {
        return modelAttributeValueMap;
    }
    
    String searchBy = "";
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        
        modelType = modelTypeService.getModelType( Integer.parseInt( modelTypeId ) );
        //modelType = modelTypeService.getModelType(  modelTypeId );
        
        modelAttributeValueMap = new HashMap<String, String>();
        
        if ( listAll != null && listAll )
        {
            listAllModel( modelType );
            
            getModelTypeAttributeData();

            return SUCCESS;
        }

        if( modelTypeAttributeId.equalsIgnoreCase(  Model.PREFIX_MODEL_NAME ))
        {
            //System.out.println( equipmentTypeAttributeId + " -- inside search by -- " + EquipmentAttributeValue.PREFIX_MODEL_NAME );
            
            searchBy = modelTypeAttributeId;
            
            listModelByFilter( modelType, null, searchText, searchBy );
            
            getModelTypeAttributeData();
            
            return SUCCESS;
        }
        
        ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( Integer.parseInt( modelTypeAttributeId ) );
        //ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute(  modelTypeAttributeId  );
        //System.out.println("modelTypeAttribute " + modelTypeAttribute.getName() + "--- modelType Name " + modelType.getName() +"--- searchText is  " + searchText  );
        
        listModelByFilter( modelType, modelTypeAttribute, searchText, "" );
        
        getModelTypeAttributeData();
        
        return SUCCESS;
    }
    
    // -------------------------------------------------------------------------
    // Support Methods
    // -------------------------------------------------------------------------
    private void listAllModel( ModelType modelType )
    {
        total = modelService.getCountModel( modelType );
        
        this.paging = createPaging( total );
        
        modelList = new ArrayList<Model>( modelService.getModels( modelType, paging.getStartPos(), paging.getPageSize() ));
    }
    /*
    private void listModelByFilter( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchKey )
    {
        total = modelService.getCountModel( modelType, modelTypeAttribute, searchText );
        
        this.paging = createPaging( total );
        
        modelList = new ArrayList<Model>( modelService.getModels( modelType, modelTypeAttribute, searchText, paging.getStartPos(), paging.getPageSize() ));
    }   
    */
    
    private void listModelByFilter( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchKey, String searchBy )
    {
        total = modelService.getCountModel( modelType, modelTypeAttribute, searchText, searchBy );
        
        this.paging = createPaging( total );
        
        modelList = new ArrayList<Model>( modelService.getModels( modelType, modelTypeAttribute, searchText, searchBy, paging.getStartPos(), paging.getPageSize() ));
    }   
    
    private void getModelTypeAttributeData()
    {
        //modelTypeAttributeList = new ArrayList<ModelTypeAttribute> ( modelType.getModelTypeAttributes());
        
        modelTypeAttributeList = new ArrayList<ModelTypeAttribute>( modelTypeService.getAllModelTypeAttributeForDisplay( modelType ) );
        
        //System.out.println("--- modelTypeAttributeList for Display --- " + modelTypeAttributeList.size() );
        
        for( Model model : modelList )
        {
            for( ModelTypeAttribute modelTypeAttribute : modelTypeAttributeList )
            {
                ModelAttributeValue modelAttributeValue = modelAttributeValueService.modelAttributeValue( model, modelTypeAttribute );
                if( modelAttributeValue != null && modelAttributeValue.getValue() != null )
                {
                    modelAttributeValueMap.put( model.getId() + ":" + modelTypeAttribute.getId(), modelAttributeValue.getValue() );
                }
            }
            
            /*
            List<ModelAttributeValue> modelAttributeValues = new ArrayList<ModelAttributeValue>( modelAttributeValueService.getAllModelAttributeValuesByModel( modelService.getModel( model.getId() )) );
            
            for( ModelAttributeValue modelAttributeValue : modelAttributeValues )
            {
                if ( ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( modelAttributeValue.getModelTypeAttribute().getValueType() ) )
                {
                    modelAttributeValueMap.put( modelAttributeValue.getModelTypeAttribute().getId(), modelAttributeValue.getModelTypeAttributeOption().getName() );
                }
                
                else
                {
                    modelAttributeValueMap.put( modelAttributeValue.getModelTypeAttribute().getId(), modelAttributeValue.getValue() );
                }
            }
            */
        }
    }
}


