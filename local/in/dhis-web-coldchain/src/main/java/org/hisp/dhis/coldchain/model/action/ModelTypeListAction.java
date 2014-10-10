package org.hisp.dhis.coldchain.model.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.paging.ActionPagingSupport;

public class ModelTypeListAction
extends ActionPagingSupport<ModelType>
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
    // Getters & Setters
    // -------------------------------------------------------------------------
    
    private List<ModelType> modelTypes;
    
    public List<ModelType> getModelTypes()
    {
        return modelTypes;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------


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
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( modelTypeService.getModelTypeCountByName( key ) );
            
            modelTypes = new ArrayList<ModelType>( modelTypeService.getModelTypesBetweenByName( key, paging.getStartPos(), paging.getPageSize() ));
        }
        else
        {
            this.paging = createPaging( modelTypeService.getModelTypeCount() );
            
            modelTypes = new ArrayList<ModelType>( modelTypeService.getModelTypesBetween( paging.getStartPos(), paging.getPageSize() ));
        }
        /*
        modelTypes = new ArrayList<ModelType>( modelTypeService.getAllModelTypes());
        Collections.sort( modelTypes, new ModelTypeComparator() );
        */
        
        Collections.sort( modelTypes, new IdentifiableObjectNameComparator() );

        return SUCCESS;
    }
}

