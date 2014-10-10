package org.hisp.dhis.ccem.model.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.paging.ActionPagingSupport;

//public class GetColdChainModelTypeAttributeListAction  implements Action
public class GetColdChainModelTypeAttributeListAction  extends ActionPagingSupport<ModelTypeAttribute>
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
    // Output
    // -------------------------------------------------------------------------
    
    private List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
    
    public List<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
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
        // -------------------------------------------------------------------------
        // Criteria
        // -------------------------------------------------------------------------

        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( modelTypeAttributeService.getModelTypeAttributeCountByName( key ) );
            
            modelTypeAttributes = new ArrayList<ModelTypeAttribute>( modelTypeAttributeService.getModelTypeAttributesBetweenByName( key, paging.getStartPos(), paging.getPageSize() ));
        }
        else
        {
            this.paging = createPaging( modelTypeAttributeService.getModelTypeAttributeCount() );
            
            modelTypeAttributes = new ArrayList<ModelTypeAttribute>( modelTypeAttributeService.getModelTypeAttributesBetween( paging.getStartPos(), paging.getPageSize() ));
        }
        
        Collections.sort( modelTypeAttributes, new IdentifiableObjectNameComparator() );
       
        /*
        modelTypeAttributes = new ArrayList<ModelTypeAttribute>( modelTypeAttributeService.getAllModelTypeAttributes() );
        
        Collections.sort( modelTypeAttributes, new ModelTypeAttributeComparator() );
        */
        return SUCCESS;
    }
    
}
