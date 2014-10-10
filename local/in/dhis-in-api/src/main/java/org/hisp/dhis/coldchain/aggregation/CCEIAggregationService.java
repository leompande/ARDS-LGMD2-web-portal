package org.hisp.dhis.coldchain.aggregation;

import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

public interface CCEIAggregationService
{
    String ID = CCEIAggregationService.class.getName();

    String getQueryForRefrigeratorWorkingStatus( Integer equipmentTypeId, Integer modelTypeAttributeId, String modelName, String workingStatus );
    
    String getQueryForRefrigeratorUtilization( Integer equipmentTypeId, Integer modelTypeAttributeId, String modelName, String utilization );

    Map<String, Integer> calculateStorageCapacityData( Period period, DataElement dataElement, Set<OrganisationUnit> orgUnits, Set<OrganisationUnitGroup> orgUnitGroups );
    
    Map<String, Integer> calculateRefrigeratorWorkingStatus( Period period, DataElement dataElement, Set<OrganisationUnit> orgUnits, String query );

    Map<String, Integer> calculateRefrigeratorUtilization( Period period, DataElement dataElement, Set<OrganisationUnit> orgUnits, String query );
    
    String importData( Map<String, Integer> aggregationResultMap, Period period );
}
