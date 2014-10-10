package org.hisp.dhis.ccem.model.action;

import java.util.List;

import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroup;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroupService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version SaveModelTypeAttributeGroupSortOrderAction.javaOct 11, 2012 5:55:22 PM	
 */

public class SaveModelTypeAttributeGroupSortOrderAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ModelTypeAttributeGroupService modelTypeAttributeGroupService;
    
    public void setModelTypeAttributeGroupService( ModelTypeAttributeGroupService modelTypeAttributeGroupService )
    {
        this.modelTypeAttributeGroupService = modelTypeAttributeGroupService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    private List<String> modelTypeAttributeGroups;
        
    public void setModelTypeAttributeGroups( List<String> modelTypeAttributeGroups )
    {
        this.modelTypeAttributeGroups = modelTypeAttributeGroups;
    }
    
    private String modelTypeId;
    
    public void setModelTypeId( String modelTypeId )
    {
        this.modelTypeId = modelTypeId;
    }
    
    public String getModelTypeId()
    {
        return modelTypeId;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    

    public String execute() throws Exception
    {
        int sortOrder = 1;
        
        for ( String id : modelTypeAttributeGroups )
        {
            ModelTypeAttributeGroup modelTypeAttributeGroup = modelTypeAttributeGroupService.getModelTypeAttributeGroupById( Integer.parseInt( id ) );
            
            modelTypeAttributeGroup.setSortOrder( sortOrder++ );
            
            modelTypeAttributeGroupService.updateModelTypeAttributeGroup( modelTypeAttributeGroup );
        }
        
        return SUCCESS;
    }
}


