package org.hisp.dhis.pbf.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueService;
import org.hisp.dhis.pbf.api.TariffDataValueStore;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultTariffDataValueService implements TariffDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TariffDataValueStore tariffDataValueStore;

    public void setTariffDataValueStore( TariffDataValueStore tariffDataValueStore )
    {
        this.tariffDataValueStore = tariffDataValueStore;
    }

    // -------------------------------------------------------------------------
    // TariffDataValue
    // -------------------------------------------------------------------------

    @Override
    public void addTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.addTariffDataValue( tariffDataValue );;
    }

    @Override
    public void updateTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.updateTariffDataValue( tariffDataValue );
    }

    @Override
    public void deleteTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.deleteTariffDataValue( tariffDataValue );
    }

    @Override
    public TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate )
    {
        return tariffDataValueStore.getTariffDataValue( organisationUnit, dataElement, dataSet, startDate, endDate );
    }

    @Override
    public Collection<TariffDataValue> getAllTariffDataValues()
    {
        return tariffDataValueStore.getAllTariffDataValues();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        return tariffDataValueStore.getTariffDataValues( organisationUnit, dataSet );
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        return tariffDataValueStore.getTariffDataValues( organisationUnit, dataElement );
    }

    public Map<Integer, Double> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet, Period period )
    {
        return tariffDataValueStore.getTariffDataValues( organisationUnit, dataSet, period );
    }
}
