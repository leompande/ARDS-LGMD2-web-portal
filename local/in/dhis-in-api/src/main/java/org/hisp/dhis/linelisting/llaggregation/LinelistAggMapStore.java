package org.hisp.dhis.linelisting.llaggregation;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;

public interface LinelistAggMapStore
{

    String ID = LinelistAggMapStore.class.getName();
    
    //----------------------------------------------------------------
    // Linelisting Aggregation Mapping
    //----------------------------------------------------------------

    void addLineListAggregationMapping( LinelistAggregationMapping llAggregationMapping );
    
    void updateLinelistAggregationMapping( LinelistAggregationMapping llAggregationMapping );
    
    void deleteLinelistAggregationMapping( LinelistAggregationMapping llAggregationMapping );
            
    LinelistAggregationMapping getLinelistAggregationMappingByOptionCombo( DataElement dataElement, DataElementCategoryOptionCombo optionCombo );
    
    int executeAggregationQuery( String query );

}
