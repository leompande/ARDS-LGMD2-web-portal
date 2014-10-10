package org.hisp.dhis.pbf.api;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public interface PBFDataValueStore 
{
	String ID = PBFDataValueStore.class.getName();
	
    void addPBFDataValue( PBFDataValue pbfDataValue );
    
    void updatePBFDataValue( PBFDataValue pbfDataValue );
    
    void deletePBFDataValue( PBFDataValue pbfDataValue );
    
    PBFDataValue getPBFDataValue( OrganisationUnit organisationUnit, DataSet dataSet, Period period, DataElement dataElement );
    
    Collection<PBFDataValue> getPBFDataValues( OrganisationUnit organisationUnit, DataSet dataSet, Period period );
}
