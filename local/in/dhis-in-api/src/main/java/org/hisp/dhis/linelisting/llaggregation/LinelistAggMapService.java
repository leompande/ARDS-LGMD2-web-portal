package org.hisp.dhis.linelisting.llaggregation;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public interface LinelistAggMapService
{

    String ID = LinelistAggMapService.class.getName();
    
    //----------------------------------------------------------------
    // Linelisting Aggregation Mapping
    //----------------------------------------------------------------

    void addLineListAggregationMapping( LinelistAggregationMapping llAggregationMapping );
    
    void updateLinelistAggregationMapping( LinelistAggregationMapping llAggregationMapping );
    
    void deleteLinelistAggregationMapping( LinelistAggregationMapping llAggregationMapping );
            
    LinelistAggregationMapping getLinelistAggregationMappingByOptionCombo( DataElement dataElement, DataElementCategoryOptionCombo optionCombo );
    
    int executeAggregationQuery( OrganisationUnit orgUnit, Period period, LinelistAggregationMapping mappingObject );
    
    void validateAggregateQuery(String query) throws Exception;
}
