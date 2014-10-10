package org.hisp.dhis.dataentrystatus;

import java.util.Collection;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public interface DataEntryStatusStore
{
    String ID = DataEntryStatusStore.class.getName();
    
    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------
    
    /**
     * Adds a DataEntryStatus If  value  specified DataEntryStatus object are null, then the object should not be
     * persisted.
     * 
      * @param dataEntryStatus the DataEntryStatus to add.
     */
    
    void addDataEntryStatus( DataEntryStatus dataEntryStatus );
    
    /**
     * Update a DataEntryStatus If  value  specified DataEntryStatus object are null, then the object should not be
     * persisted.
     * 
      * @param dataEntryStatus the DataEntryStatus to add.
     */
    
    void updateDataEntryStatus( DataEntryStatus dataEntryStatus );
    
    /**
     * Deletes a DataEntryStatus.
     * 
     * @param dataEntryStatus the DataEntryStatus to delete.
     */
    void deleteDataEntryStatus( DataEntryStatus dataEntryStatus );
    
    /**
     * Deletes all DataEntryStatus connected to a Source.
     * 
     * @param organisationUnit the OrganisationUnit for which the DataEntryStatus should be deleted.
     * @return the number of deleted DataEntryStatus.
     */
    int deleteDataEntryStatusBySource( OrganisationUnit organisationUnit );

    /**
     * Deletes all DataEntryStatus registered for the given DataSet.
     * 
     * @param dataSet the DataSet for which the DataEntryStatus should be deleted.
     * @return the number of deleted DataValues.
     */
    int deleteDataEntryStatusByDataSet( DataSet dataSet );

    /**
     * Returns a DataEntryStatus.
     * 
     * @param organisationUnit the organisationUnit of the DataValue.
     * @param dataSet the DataSet of the DataEntryStatus.
     * @param period the Period of the DataEntryStatus.
     * @return the DataEntryStatus which corresponds to the given parameters, or null
     *         if no match.
     */
    
    DataEntryStatus getDataEntryStatusValue( DataSet dataSet, OrganisationUnit organisationUnit, Period period, String includeZero);

    /**
     * Returns a DataEntryStatus.
     * 
     * @param dataSetId the DataElement identifier.
      * @param organisationUnitId the Source identifier.
     * @param periodId the Period identifier.
     * @param includeZero the String identifier
     
     * @return the DataEntryStatus.
     */
    String getValue( int dataSetId,  int organisationUnitId, int periodId, String includeZero );
    
    // -------------------------------------------------------------------------
    // Collections of DataEntryStatus
    // -------------------------------------------------------------------------

    /**
     * Returns all DataEntryStatus.
     * 
     * @return a collection of all DataEntryStatusValues.
     */
    Collection<DataEntryStatus> getAllDataEntryStatusValues();
    
    /**
     * Returns all getAllDataEntryStatusValues for a given OrganisationUnit and Period.
     * 
     * @param organisationUnit the OrganisationUnit of the DataEntryStatus.
     * @param period the Period of the DataEntryStatus.
     * @return a collection of all DataEntryStatusValues which match the given OrganisationUnit and
     *         Period, or an empty collection if no values match.
     */
    Collection<DataEntryStatus> getDataEntryStatusValues( OrganisationUnit organisationUnit, Period period );
    
    /**
     * Returns all DataEntryStatusValues for a given  DataSet and OrganisationUnit
     * 
     * @param organisationUnit the OrganisationUnit of the DataEntryStatus.
     * @param dataSet the DataSet of the DataEntryStatus.
     * @return a collection of all DataEntryStatusValues which match the given OrganisationUnit and
     *         dataSet, or an empty collection if no values match.
     */
    Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, OrganisationUnit organisationUnit );
    
    /**
     * Returns all DataEntryStatus for a given collection of DataElements.
     * 
     * @param dataSet the DataSet of the DataEntryStatus.
     * @return a collection of all DataEntryStatusValues which mach the given collection of DataSet.
     */
    Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet );
    
    /**
     * Returns all DataEntryStatus for a given collection of OrganisationUnits and a
     * DataSet.
     * 
     * @param organisationUnits the OrganisationUnits of the DataEntryStatus.
     * @param dataSet the DataSet of the DataValues.
     * @return a collection of all DataEntryStatusValues which match any of the given
     *         organisationUnits and the DataSet, or an empty collection if no values
     *         match.
     */
    Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, Collection<OrganisationUnit> organisationUnits  );
    
    /**
     * Returns all DataEntryStatus for a given Source, Period, and collection of
     * DataElements.
     * 
     * @param organisationUnit the OrganisationUnit of the DataEntryStatus.
     * @param period the Period of the DataEntryStatus.
     * @param dataSets the DataSets of the DataEntryStatus.
     * @return a collection of all DataEntryStatusValues which match the given OrganisationUnit,
     *         Period, and any of the dataSets, or an empty collection if no
     *         values match.
     */
    Collection<DataEntryStatus> getDataEntryStatusValues( Collection<DataSet> dataSets, OrganisationUnit organisationUnit, Period period );
    
    /**
     * Returns all DataEntryStatus for a given DataElement, Period, and collection of
     * OrganisationUnits.
     * 
     * @param organisationUnit the OrganisationUnit of the DataEntryStatus.
     * @param period the Period of the DataEntryStatus.
     * @param dataSets the DataSets of the DataEntryStatus.
     * @return a collection of all DataEntryStatusValues which match the given DataSet,
     *         Period, and any of the OrganisationUnit, or an empty collection if no
     *         values match.
     */
    Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, Collection<OrganisationUnit> organisationUnits, Period period );
    
    /**
     * Returns all DataEntryStatus for a given DataSet, collection of Periods, and 
     * collection of organisationUnits
     * @param dataSet the dataSet of the DataEntryStatus.
     * @param periods the periods of the DataEntryStatus.
     * @param organisationUnits the organisationUnits of the DataEntryStatus.
     * @return a collection of all DataEntryStatusValues which match the given DataSet,
     *         Periods, and organisationUnits.
     */
    Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, Collection<OrganisationUnit> organisationUnits, Collection<Period> periods );
    
    /**
     * Returns all DataEntryStatusValues for a given collection of dataSets.
     * 
     * @param datasets the DataSet of the DataEntryStatus.
     * @return a collection of all DataEntryStatusValues which match the given collection of
     *         dataSets.
     */
    Collection<DataEntryStatus> getDataEntryStatusValues( Collection<DataSet> dataSets );
}
