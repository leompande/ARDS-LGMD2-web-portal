package org.hisp.dhis.coldchain.model.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.coldchain.model.comparator.ModelTypeAttributeOptionComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetModelTypeAttributeOptionListAction.javaAug 18, 2012 1:55:48 PM	
 */

public class GetModelTypeAttributeOptionListAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    private ModelTypeAttributeService modelTypeAttributeService;
    
    public void setModelTypeAttributeService( ModelTypeAttributeService modelTypeAttributeService )
    {
        this.modelTypeAttributeService = modelTypeAttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private List<ModelTypeAttributeOption> modelTypeAttributeOption = new ArrayList<ModelTypeAttributeOption>();
    
    public List<ModelTypeAttributeOption> getModelTypeAttributeOption()
    {
        return modelTypeAttributeOption;
    }

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( id );
        
        if( ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
        {
            modelTypeAttributeOption = new ArrayList<ModelTypeAttributeOption>( modelTypeAttribute.getAttributeOptions() );
            Collections.sort( modelTypeAttributeOption, new ModelTypeAttributeOptionComparator() );
        }
        
        //System.out.println("Size of modelTypeAttributeOption List is  " + modelTypeAttributeOption.size()+ "--- modelTypeAttribute value type is---- " + modelTypeAttribute.getValueType() );
        
        return SUCCESS;
    }
    
    
}
