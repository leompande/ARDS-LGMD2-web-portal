package org.hisp.dhis.pbf.impl;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.pbf.api.PBFDataValue;
import org.hisp.dhis.pbf.api.PBFDataValueService;
import org.hisp.dhis.pbf.api.PBFDataValueStore;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultPBFDataValueService implements PBFDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

	private PBFDataValueStore pbfDataValueStore;

	public void setPbfDataValueStore(PBFDataValueStore pbfDataValueStore) 
	{
		this.pbfDataValueStore = pbfDataValueStore;
	}

    // -------------------------------------------------------------------------
    // PBFDataValue
    // -------------------------------------------------------------------------

	@Override
	public void addPBFDataValue(PBFDataValue pbfDataValue) 
	{
		pbfDataValueStore.addPBFDataValue( pbfDataValue );
	}

	@Override
	public void updatePBFDataValue(PBFDataValue pbfDataValue) 
	{
		pbfDataValueStore.updatePBFDataValue(pbfDataValue);		
	}

	@Override
	public void deletePBFDataValue(PBFDataValue pbfDataValue) 
	{
		pbfDataValueStore.deletePBFDataValue(pbfDataValue);		
	}

	@Override
	public Collection<PBFDataValue> getPBFDataValues( OrganisationUnit organisationUnit, DataSet dataSet, Period period) 
	{
		return pbfDataValueStore.getPBFDataValues(organisationUnit, dataSet, period);
	}

	@Override
	public PBFDataValue getPBFDataValue( OrganisationUnit organisationUnit, DataSet dataSet, Period period, DataElement dataElement ) 
	{
		return pbfDataValueStore.getPBFDataValue( organisationUnit, dataSet, period, dataElement );
	}
}
