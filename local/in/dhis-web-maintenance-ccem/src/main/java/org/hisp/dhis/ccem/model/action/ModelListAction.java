package org.hisp.dhis.ccem.model.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.user.UserSettingService;

//public class ModelListAction implements Action
public class ModelListAction extends ActionPagingSupport<Model>
{
    
    final String KEY_CURRENT_MODEL = "currentModel";
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
    
    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private List<Model> models = new ArrayList<Model>();
    
    public List<Model> getModels()
    {
        return models;
    }

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private List<ModelType> modelTypes;
    
    public List<ModelType> getModelTypes()
    {
        return modelTypes;
    }
    
    private ModelType modelType;
    
    public ModelType getModelType()
    {
        return modelType;
    }
    
    private String key;
    
    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
       
        if ( id == null ) // None, get current data dictionary
        {
            id = (Integer) userSettingService.getUserSetting( KEY_CURRENT_MODEL );
        }
        else if ( id == -1 ) // All, reset current data dictionary
        {
            userSettingService.saveUserSetting( KEY_CURRENT_MODEL, null );
            
            id = null;
        }
        else // Specified, set current data dictionary
        {
            userSettingService.saveUserSetting( KEY_CURRENT_MODEL, id );
        }
        
        modelTypes = new ArrayList<ModelType>( modelTypeService.getAllModelTypes());
        Collections.sort( modelTypes, IdentifiableObjectNameComparator.INSTANCE );
        
        // -------------------------------------------------------------------------
        // Criteria
        // -------------------------------------------------------------------------

        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( modelService.getModelCountByName( key ) );
            
            models = new ArrayList<Model>( modelService.getModelsBetweenByName( key, paging.getStartPos(), paging.getPageSize() ) );
        }
        
        else if ( id != null )
        {
            modelType = modelTypeService.getModelType( id );
            
            models = new ArrayList<Model>( modelService.getModels( modelType ) );
            
            this.paging = createPaging( models.size() );
            
            models = getBlockElement( models, paging.getStartPos(), paging.getPageSize() );
        }
        
        else
        {
            this.paging = createPaging( modelService.getModelCount() );
            
            models = new ArrayList<Model>( modelService.getModelsBetween( paging.getStartPos(), paging.getPageSize() ) );
        }
        
        Collections.sort( models, new IdentifiableObjectNameComparator() );
        
        /*
        modelTypes = new ArrayList<ModelType>( modelTypeService.getAllModelTypes());
        Collections.sort( modelTypes, new ModelTypeComparator() );
        
        if ( id != null )
        {
            modelType = modelTypeService.getModelType( id );
            
            models = new ArrayList<Model>( modelService.getModels( modelType ) );
        }
        else
        {
            models = new ArrayList<Model>( modelService.getAllModels() );
        }
        Collections.sort( models, new ModelComparator() );
        */
        return SUCCESS;
    }
    
}
