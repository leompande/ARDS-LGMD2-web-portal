package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroup;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.coldchain.model.comparator.ModelTypeAttributeGroupOrderComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetModelTypeAttributeGroupListSortOrderAction.javaOct 11, 2012 5:18:16 PM	
 */

public class GetModelTypeAttributeGroupListSortOrderAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String modelTypeId;
    
    public void setModelTypeId( String modelTypeId )
    {
        this.modelTypeId = modelTypeId;
    }
    
    public String getModelTypeId()
    {
        return modelTypeId;
    }

    private List<ModelTypeAttributeGroup> modelTypeAttributeGroups;

    public List<ModelTypeAttributeGroup> getModelTypeAttributeGroups()
    {
        return modelTypeAttributeGroups;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        ModelType modelType = modelTypeService.getModelType( Integer.parseInt( modelTypeId ) );
        
        modelTypeAttributeGroups = new ArrayList<ModelTypeAttributeGroup>( modelType.getModelTypeAttributeGroups() );

        Collections.sort( modelTypeAttributeGroups, new ModelTypeAttributeGroupOrderComparator() );

        return SUCCESS;
    }
}
