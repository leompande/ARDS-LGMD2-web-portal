package org.hisp.dhis.target;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultDeTargetMappingService implements DeTargetMappingService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DeTargetMappingStore deTargetMappingStore;

    public void setDeTargetMappingStore( DeTargetMappingStore deTargetMappingStore )
    {
        this.deTargetMappingStore = deTargetMappingStore;
    }
    
    // -------------------------------------------------------------------------
    // DeTargetMapping
    // -------------------------------------------------------------------------
    
    public void addDeTargetMapping( DeTargetMapping deTargetMapping )
    {
    	deTargetMappingStore.addDeTargetMapping( deTargetMapping );  
    }

    public void deleteDeTargetMapping( DeTargetMapping deTargetMapping )
    {
    	deTargetMappingStore.deleteDeTargetMapping( deTargetMapping );
    }
    
    public void updateDeTargetMapping( DeTargetMapping deTargetMapping )
    {
    	deTargetMappingStore.updateDeTargetMapping( deTargetMapping );        
    }
    
    public DeTargetMapping getDeTargetMapping( DataElement dataElement,  DataElementCategoryOptionCombo optionCombo )
    {
    	return deTargetMappingStore.getDeTargetMapping( dataElement,  optionCombo );
    }

}
