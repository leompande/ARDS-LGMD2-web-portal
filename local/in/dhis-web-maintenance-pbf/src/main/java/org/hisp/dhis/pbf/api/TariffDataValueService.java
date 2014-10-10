package org.hisp.dhis.pbf.api;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public interface TariffDataValueService
{
    String ID = TariffDataValueService.class.getName();
    
    void addTariffDataValue( TariffDataValue tariffDataValue );
    
    void updateTariffDataValue( TariffDataValue tariffDataValue );
    
    void deleteTariffDataValue( TariffDataValue tariffDataValue );
        
    TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate );
    
    Collection<TariffDataValue> getAllTariffDataValues();
    
    Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet );
    
    Map<Integer, Double> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet, Period period );
    
    Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement );
}
