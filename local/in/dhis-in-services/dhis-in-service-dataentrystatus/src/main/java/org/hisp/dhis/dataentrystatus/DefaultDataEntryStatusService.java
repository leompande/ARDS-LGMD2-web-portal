package org.hisp.dhis.dataentrystatus;

import java.util.Collection;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultDataEntryStatusService implements DataEntryStatusService
{
    //private static final Log log = LogFactory.getLog( DefaultDataEntryStatusService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataEntryStatusStore dataEntryStatusStore;
    
    public void setDataEntryStatusStore( DataEntryStatusStore dataEntryStatusStore )
    {
        this.dataEntryStatusStore = dataEntryStatusStore;
    }

    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------
    
    public void addDataEntryStatus( DataEntryStatus dataEntryStatus )
    {
       if ( !dataEntryStatus.isNullValue() )
       {
           dataEntryStatusStore.addDataEntryStatus( dataEntryStatus );
       }
    }
    
    public void updateDataEntryStatus( DataEntryStatus dataEntryStatus )
    {
       if ( dataEntryStatus.isNullValue() )
        {
            this.deleteDataEntryStatus( dataEntryStatus );
        }
       else 
       {
            dataEntryStatusStore.updateDataEntryStatus( dataEntryStatus );
       }
    }
    
    @Transactional
    public void deleteDataEntryStatus( DataEntryStatus dataEntryStatus )
    {
        dataEntryStatusStore.deleteDataEntryStatus( dataEntryStatus );
    }
    
    
    @Transactional
    public int deleteDataEntryStatusBySource( OrganisationUnit organisationUnit )
    {
        return dataEntryStatusStore.deleteDataEntryStatusBySource( organisationUnit );
    }

    @Transactional
    public int deleteDataEntryStatusByDataSet( DataSet dataSet )
    {
        return dataEntryStatusStore.deleteDataEntryStatusByDataSet( dataSet );
    }
    
    public DataEntryStatus getDataEntryStatusValue( DataSet dataSet, OrganisationUnit organisationUnit, Period period, String includeZero )
    {
        return dataEntryStatusStore.getDataEntryStatusValue( dataSet, organisationUnit, period, includeZero );
    }

    public String getValue( int dataSetId,  int organisationUnitId, int periodId ,String includeZero )
    {
        return dataEntryStatusStore.getValue( dataSetId, organisationUnitId, periodId , includeZero );
    }
    
    // -------------------------------------------------------------------------
    // Collections of DataEntryStatus
    // -------------------------------------------------------------------------

    public Collection<DataEntryStatus> getAllDataEntryStatusValues()
    {
        return dataEntryStatusStore.getAllDataEntryStatusValues();
        
    }
    
    public Collection<DataEntryStatus> getDataEntryStatusValues( OrganisationUnit organisationUnit, Period period )
    {
        return dataEntryStatusStore.getDataEntryStatusValues( organisationUnit, period );
    }
    
    public Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, OrganisationUnit organisationUnit )
    {
        return dataEntryStatusStore.getDataEntryStatusValues( dataSet, organisationUnit );
    }
    
    public Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet )
    {
        return dataEntryStatusStore.getDataEntryStatusValues( dataSet );
    }
    
    
    public Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, Collection<OrganisationUnit> organisationUnits  )
    {
        return dataEntryStatusStore.getDataEntryStatusValues( dataSet, organisationUnits );
    }
    
    
    public Collection<DataEntryStatus> getDataEntryStatusValues( Collection<DataSet> dataSets, OrganisationUnit organisationUnit, Period period )
    {
        return dataEntryStatusStore.getDataEntryStatusValues( dataSets, organisationUnit, period );
    }
    
    public Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, Collection<OrganisationUnit> organisationUnits, Period period )
    {
        return dataEntryStatusStore.getDataEntryStatusValues( dataSet, organisationUnits, period );
    }
    
    public Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, Collection<OrganisationUnit> organisationUnits, Collection<Period> periods )
    {
        return dataEntryStatusStore.getDataEntryStatusValues( dataSet, organisationUnits, periods );
    }
    
    public Collection<DataEntryStatus> getDataEntryStatusValues( Collection<DataSet> dataSets )
    {
        return dataEntryStatusStore.getDataEntryStatusValues( dataSets );
    }
}
