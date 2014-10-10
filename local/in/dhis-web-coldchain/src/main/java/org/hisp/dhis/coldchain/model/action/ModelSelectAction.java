package org.hisp.dhis.coldchain.model.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ModelSelectAction.java Jun 22, 2012 1:02:17 PM	
 */
public class ModelSelectAction implements Action
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
    // Input/output
    // -------------------------------------------------------------------------

    private List<ModelType> modelTypes;
    
    public List<ModelType> getModelTypes()
    {
        return modelTypes;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        modelTypes = new ArrayList<ModelType>( modelTypeService.getAllModelTypes() );
        
        Iterator<ModelType> modelTypesIterator = modelTypes.iterator();
        while( modelTypesIterator.hasNext() )
        {
            ModelType modelType = modelTypesIterator.next();
            
            if ( modelType.getDescription().equalsIgnoreCase( "Vaccines" ) )
            {
                modelTypesIterator.remove( );
            }
            
        }
        
        
        return SUCCESS;
    }

}

