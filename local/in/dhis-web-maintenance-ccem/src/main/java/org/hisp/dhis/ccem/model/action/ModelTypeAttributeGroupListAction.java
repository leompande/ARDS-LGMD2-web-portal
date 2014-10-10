package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroup;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroupService;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.coldchain.model.comparator.ModelTypeAttributeGroupOrderComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ModelTypeAttributeGroupListAction.javaOct 9, 2012 5:41:57 PM	
 */
//public class ModelTypeAttributeGroupListAction extends ActionPagingSupport<ModelTypeAttributeGroup>
public class ModelTypeAttributeGroupListAction implements Action
{
  
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
   
    private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }
    
    private ModelTypeAttributeGroupService modelTypeAttributeGroupService;
    
    public void setModelTypeAttributeGroupService( ModelTypeAttributeGroupService modelTypeAttributeGroupService )
    {
        this.modelTypeAttributeGroupService = modelTypeAttributeGroupService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output Getter/Setter
    // -------------------------------------------------------------------------
    


    private int id;
    
    public void setId( int id )
    {
        this.id = id;
    }
    
    private ModelType modelType;

    public ModelType getModelType()
    {
        return modelType;
    }
    
    private List<ModelTypeAttributeGroup> modelTypeAttributeGroupList;
    
    public List<ModelTypeAttributeGroup> getModelTypeAttributeGroupList()
    {
        return modelTypeAttributeGroupList;
    }
    /*
    private String key;

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }
    */
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        modelType = modelTypeService.getModelType( id );
        
        //modelTypeAttributeGroupList = new ArrayList<ModelTypeAttributeGroup>( modelType.getModelTypeAttributeGroups() );
        
        modelTypeAttributeGroupList = new ArrayList<ModelTypeAttributeGroup>( modelTypeAttributeGroupService.getModelTypeAttributeGroupsByModelType( modelType ) );       
        
        Collections.sort( modelTypeAttributeGroupList, new ModelTypeAttributeGroupOrderComparator() );
        
        /*
        for( ModelTypeAttributeGroup modelTypeAttributeGroup : modelTypeAttributeGroupList )
        {
            System.out.println( modelTypeAttributeGroup.getId() + " -- " + modelTypeAttributeGroup.getName() );
        }
        */
        
        /*
        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( modelTypeAttributeGroupService..getModelTypeAttributeGroupCountByName( key ) );
            
            modelTypeAttributeGroupList = new ArrayList<ModelTypeAttributeGroup>( modelTypeAttributeGroupService.getModelTypeAttributeGroupsBetweenByName( key, paging.getStartPos(), paging.getPageSize() ));
        }
        else
        {
            this.paging = createPaging( modelTypeAttributeGroupService.getModelTypeAttributeGroupCount() );
            
            modelTypeAttributeGroupList = new ArrayList<ModelTypeAttributeGroup>( modelTypeAttributeGroupService.getModelTypeAttributeGroupsBetween( paging.getStartPos(), paging.getPageSize() ));
        }
        */
        
        return SUCCESS;
    }
}




