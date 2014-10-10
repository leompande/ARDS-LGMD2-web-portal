package org.hisp.dhis.dxf2.datavalueset;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty.UUID;
import static org.hisp.dhis.system.notification.NotificationLevel.ERROR;
import static org.hisp.dhis.system.notification.NotificationLevel.INFO;
import static org.hisp.dhis.system.util.ConversionUtils.wrap;
import static org.hisp.dhis.system.util.DateUtils.getDefaultDate;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.staxwax.factory.XMLFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportCount;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.pdfform.PdfDataEntryFormUtil;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.jdbc.batchhandler.DataValueBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.DebugUtils;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.csvreader.CsvReader;

/**
 * @author Lars Helge Overland
 */
public class DefaultDataValueSetService
    implements DataValueSetService
{
    private static final Log log = LogFactory.getLog( DefaultDataValueSetService.class );

    private static final String ERROR_INVALID_DATA_SET = "Invalid data set: ";
    private static final String ERROR_INVALID_PERIOD = "Invalid period: ";
    private static final String ERROR_INVALID_ORG_UNIT = "Invalid org unit: ";
    private static final String ERROR_OBJECT_NEEDED_TO_COMPLETE = "Must be provided to complete data set";

    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private BatchHandlerFactory batchHandlerFactory;

    @Autowired
    private CompleteDataSetRegistrationService registrationService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private DataValueSetStore dataValueSetStore;

    @Autowired
    private Notifier notifier;

    //--------------------------------------------------------------------------
    // DataValueSet implementation
    //--------------------------------------------------------------------------

    public void writeDataValueSet( String dataSet, String period, String orgUnit, OutputStream out )
    {
        DataSet dataSet_ = dataSetService.getDataSet( dataSet );
        Period period_ = PeriodType.getPeriodFromIsoString( period );
        OrganisationUnit orgUnit_ = organisationUnitService.getOrganisationUnit( orgUnit );

        if ( dataSet_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_DATA_SET + dataSet );
        }

        if ( period_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_PERIOD + period );
        }

        if ( orgUnit_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_ORG_UNIT + orgUnit );
        }

        CompleteDataSetRegistration registration = registrationService.getCompleteDataSetRegistration( dataSet_, period_, orgUnit_ );

        Date completeDate = registration != null ? registration.getDate() : null;

        period_ = periodService.reloadPeriod( period_ );

        dataValueSetStore.writeDataValueSetXml( dataSet_, completeDate, period_, orgUnit_, dataSet_.getDataElements(), wrap( period_ ), wrap( orgUnit_ ), out );
    }

    public void writeDataValueSet( Set<String> dataSets, Date startDate, Date endDate, Set<String> orgUnits, OutputStream out )
    {
        Set<Period> periods = new HashSet<Period>( periodService.getPeriodsBetweenDates( startDate, endDate ) );

        if ( periods.isEmpty() )
        {
            throw new IllegalArgumentException( "At least one period must be specified" );
        }

        dataValueSetStore.writeDataValueSetXml( null, null, null, null, getDataElements( dataSets ), periods, getOrgUnits( orgUnits ), out );
    }

    public void writeDataValueSetCsv( Set<String> dataSets, Date startDate, Date endDate, Set<String> orgUnits, Writer writer )
    {
        Set<Period> periods = new HashSet<Period>( periodService.getPeriodsBetweenDates( startDate, endDate ) );

        if ( periods.isEmpty() )
        {
            throw new IllegalArgumentException( "At least one period must be specified" );
        }

        dataValueSetStore.writeDataValueSetCsv( getDataElements( dataSets ), periods, getOrgUnits( orgUnits ), writer );
    }

    public ImportSummary saveDataValueSet( InputStream in )
    {
        return saveDataValueSet( in, ImportOptions.getDefaultImportOptions(), null );
    }

    public ImportSummary saveDataValueSetJson( InputStream in )
    {
        return saveDataValueSetJson( in, ImportOptions.getDefaultImportOptions(), null );
    }

    public ImportSummary saveDataValueSet( InputStream in, ImportOptions importOptions )
    {
        return saveDataValueSet( in, importOptions, null );
    }

    public ImportSummary saveDataValueSetJson( InputStream in, ImportOptions importOptions )
    {
        return saveDataValueSetJson( in, importOptions, null );
    }

    public ImportSummary saveDataValueSet( InputStream in, ImportOptions importOptions, TaskId id )
    {
        try
        {
            DataValueSet dataValueSet = new StreamingDataValueSet( XMLFactory.getXMLReader( in ) );
            return saveDataValueSet( importOptions, id, dataValueSet );
        }
        catch ( RuntimeException ex )
        {
            log.error( DebugUtils.getStackTrace( ex ) );
            notifier.notify( id, ERROR, "Process failed: " + ex.getMessage(), true );
            return new ImportSummary( ImportStatus.ERROR, "The import process failed: " + ex.getMessage() );
        }
    }

    public ImportSummary saveDataValueSetJson( InputStream in, ImportOptions importOptions, TaskId id )
    {
        try
        {
            DataValueSet dataValueSet = JacksonUtils.fromJson( in, DataValueSet.class );
            return saveDataValueSet( importOptions, id, dataValueSet );
        }
        catch ( Exception ex )
        {
            log.error( DebugUtils.getStackTrace( ex ) );
            notifier.notify( id, ERROR, "Process failed: " + ex.getMessage(), true );
            return new ImportSummary( ImportStatus.ERROR, "The import process failed: " + ex.getMessage() );
        }
    }

    public ImportSummary saveDataValueSetCsv( InputStream in, ImportOptions importOptions, TaskId id )
    {
        try
        {
            DataValueSet dataValueSet = new StreamingCsvDataValueSet( new CsvReader( in, Charset.forName( "UTF-8" ) ) );
            return saveDataValueSet( importOptions, id, dataValueSet );
        }
        catch ( RuntimeException ex )
        {
            log.error( DebugUtils.getStackTrace( ex ) );
            notifier.clear( id ).notify( id, ERROR, "Process failed: " + ex.getMessage(), true );
            return new ImportSummary( ImportStatus.ERROR, "The import process failed: " + ex.getMessage() );
        }
    }

    public ImportSummary saveDataValueSetPdf( InputStream in, ImportOptions importOptions, TaskId id )
    {
        try
        {
            DataValueSet dataValueSet = PdfDataEntryFormUtil.getDataValueSet( in );

            return saveDataValueSet( importOptions, id, dataValueSet );
        }
        catch ( RuntimeException ex )
        {
            log.error( DebugUtils.getStackTrace( ex ) );
            notifier.clear( id ).notify( id, ERROR, "Process failed: " + ex.getMessage(), true );
            return new ImportSummary( ImportStatus.ERROR, "The import process failed: " + ex.getMessage() );
        }
    }

    private ImportSummary saveDataValueSet( ImportOptions importOptions, TaskId id, DataValueSet dataValueSet )
    {
        notifier.clear( id ).notify( id, "Process started" );

        ImportSummary summary = new ImportSummary();

        importOptions = importOptions != null ? importOptions : ImportOptions.getDefaultImportOptions();

        IdentifiableProperty dataElementIdScheme = dataValueSet.getDataElementIdScheme() != null ? IdentifiableProperty.valueOf( dataValueSet.getDataElementIdScheme().toUpperCase() ) : importOptions.getDataElementIdScheme();
        IdentifiableProperty orgUnitIdScheme = dataValueSet.getOrgUnitIdScheme() != null ? IdentifiableProperty.valueOf( dataValueSet.getOrgUnitIdScheme().toUpperCase() ) : importOptions.getOrgUnitIdScheme();
        boolean dryRun = dataValueSet.getDryRun() != null ? dataValueSet.getDryRun() : importOptions.isDryRun();
        ImportStrategy strategy = dataValueSet.getStrategy() != null ? ImportStrategy.valueOf( dataValueSet.getStrategy() ) : importOptions.getImportStrategy();
        boolean skipExistingCheck = importOptions.isSkipExistingCheck();

        Map<String, DataElement> dataElementMap = identifiableObjectManager.getIdMap( DataElement.class, dataElementIdScheme );
        Map<String, OrganisationUnit> orgUnitMap = orgUnitIdScheme == UUID ? getUuidOrgUnitMap() : identifiableObjectManager.getIdMap( OrganisationUnit.class, orgUnitIdScheme );
        Map<String, DataElementCategoryOptionCombo> categoryOptionComboMap = identifiableObjectManager.getIdMap( DataElementCategoryOptionCombo.class, IdentifiableProperty.UID );
        Map<String, Period> periodMap = new HashMap<String, Period>();

        DataSet dataSet = dataValueSet.getDataSet() != null ? identifiableObjectManager.getObject( DataSet.class, IdentifiableProperty.UID, dataValueSet.getDataSet() ) : null;
        Date completeDate = getDefaultDate( dataValueSet.getCompleteDate() );

        Period outerPeriod = PeriodType.getPeriodFromIsoString( dataValueSet.getPeriod() );

        OrganisationUnit outerOrgUnit;

        if ( orgUnitIdScheme.equals( IdentifiableProperty.UUID ) )
        {
            outerOrgUnit = dataValueSet.getOrgUnit() == null ? null : organisationUnitService.getOrganisationUnitByUuid( dataValueSet.getOrgUnit() );
        }
        else
        {
            outerOrgUnit = dataValueSet.getOrgUnit() != null ? identifiableObjectManager.getObject( OrganisationUnit.class, orgUnitIdScheme, dataValueSet.getOrgUnit() ) : null;
        }

        if ( dataSet != null && completeDate != null )
        {
            notifier.notify( id, "Completing data set" );
            handleComplete( dataSet, completeDate, outerOrgUnit, outerPeriod, summary );
        }
        else
        {
            summary.setDataSetComplete( Boolean.FALSE.toString() );
        }

        DataElementCategoryOptionCombo fallbackCategoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        String currentUser = currentUserService.getCurrentUsername();

        BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class ).init();

        int importCount = 0;
        int updateCount = 0;
        int totalCount = 0;

        notifier.notify( id, "Importing data values" );
        log.info( "importing data values" );

        while ( dataValueSet.hasNextDataValue() )
        {
            org.hisp.dhis.dxf2.datavalue.DataValue dataValue = dataValueSet.getNextDataValue();

            DataValue internalValue = new DataValue();

            totalCount++;

            DataElement dataElement = dataElementMap.get( dataValue.getDataElement() );
            DataElementCategoryOptionCombo categoryOptionCombo = categoryOptionComboMap.get( dataValue.getCategoryOptionCombo() );
            Period period = outerPeriod != null ? outerPeriod : PeriodType.getPeriodFromIsoString( dataValue.getPeriod() );
            OrganisationUnit orgUnit = outerOrgUnit != null ? outerOrgUnit : orgUnitMap.get( dataValue.getOrgUnit() );

            if ( dataElement == null )
            {
                summary.getConflicts().add( new ImportConflict( DataElement.class.getSimpleName(), dataValue.getDataElement() ) );
                continue;
            }

            if ( period == null )
            {
                summary.getConflicts().add( new ImportConflict( Period.class.getSimpleName(), dataValue.getPeriod() ) );
                continue;
            }

            if ( orgUnit == null )
            {
                summary.getConflicts().add( new ImportConflict( OrganisationUnit.class.getSimpleName(), dataValue.getOrgUnit() ) );
                continue;
            }

            if ( categoryOptionCombo == null )
            {
                categoryOptionCombo = fallbackCategoryOptionCombo;
            }

            if ( dataValue.getValue() == null && dataValue.getComment() == null )
            {
                continue;
            }

            String valueValid = ValidationUtils.dataValueIsValid( dataValue.getValue(), dataElement );

            if ( valueValid != null )
            {
                summary.getConflicts().add( new ImportConflict( DataValue.class.getSimpleName(), valueValid ) );
                continue;
            }

            String commentValid = ValidationUtils.commentIsValid( dataValue.getComment() );

            if ( commentValid != null )
            {
                summary.getConflicts().add( new ImportConflict( DataValue.class.getSimpleName(), commentValid ) );
                continue;
            }

            if ( periodMap.containsKey( dataValue.getPeriod() ) )
            {
                period = periodMap.get( dataValue.getPeriod() );
            }
            else
            {
                period = periodService.reloadPeriod( period );
                periodMap.put( dataValue.getPeriod(), period );
            }

            internalValue.setDataElement( dataElement );
            internalValue.setPeriod( period );
            internalValue.setSource( orgUnit );
            internalValue.setCategoryOptionCombo( categoryOptionCombo );
            internalValue.setAttributeOptionCombo( fallbackCategoryOptionCombo ); // TODO
            internalValue.setValue( StringUtils.trimToNull( dataValue.getValue() ) );

            if ( dataValue.getStoredBy() == null || dataValue.getStoredBy().trim().isEmpty() )
            {
                internalValue.setStoredBy( currentUser );
            }
            else
            {
                internalValue.setStoredBy( dataValue.getStoredBy() );
            }

            internalValue.setTimestamp( getDefaultDate( dataValue.getLastUpdated() ) );
            internalValue.setComment( StringUtils.trimToNull( dataValue.getComment() ) );
            internalValue.setFollowup( dataValue.getFollowup() );

            if ( !skipExistingCheck && batchHandler.objectExists( internalValue ) )
            {
                if ( strategy.isCreateAndUpdate() || strategy.isUpdate() )
                {
                    if ( !dryRun )
                    {
                        batchHandler.updateObject( internalValue );
                    }

                    updateCount++;
                }
            }
            else
            {
                if ( strategy.isCreateAndUpdate() || strategy.isCreate() )
                {
                    if ( !dryRun )
                    {
                        batchHandler.addObject( internalValue );
                    }

                    importCount++;
                }
            }
        }

        batchHandler.flush();

        int ignores = totalCount - importCount - updateCount;

        summary.setDataValueCount( new ImportCount( importCount, updateCount, ignores, 0 ) );
        summary.setStatus( ImportStatus.SUCCESS );
        summary.setDescription( "Import process completed successfully" );

        notifier.notify( id, INFO, "Import done", true ).addTaskSummary( id, summary );

        dataValueSet.close();

        return summary;
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    private void handleComplete( DataSet dataSet, Date completeDate, OrganisationUnit orgUnit, Period period, ImportSummary summary )
    {
        if ( orgUnit == null )
        {
            summary.getConflicts().add( new ImportConflict( OrganisationUnit.class.getSimpleName(), ERROR_OBJECT_NEEDED_TO_COMPLETE ) );
            return;
        }

        if ( period == null )
        {
            summary.getConflicts().add( new ImportConflict( Period.class.getSimpleName(), ERROR_OBJECT_NEEDED_TO_COMPLETE ) );
            return;
        }

        period = periodService.reloadPeriod( period );

        CompleteDataSetRegistration completeAlready = registrationService.getCompleteDataSetRegistration( dataSet, period, orgUnit );

        String username = currentUserService.getCurrentUsername();

        if ( completeAlready != null )
        {
            completeAlready.setStoredBy( username );
            completeAlready.setDate( completeDate );

            registrationService.updateCompleteDataSetRegistration( completeAlready );
        }
        else
        {
            CompleteDataSetRegistration registration = new CompleteDataSetRegistration( dataSet, period, orgUnit, completeDate, username );

            registrationService.saveCompleteDataSetRegistration( registration );
        }

        summary.setDataSetComplete( DateUtils.getMediumDateString( completeDate ) );
    }

    private Set<DataElement> getDataElements( Set<String> dataSets )
    {
        Set<DataElement> dataElements = new HashSet<DataElement>();

        for ( String ds : dataSets )
        {
            DataSet dataSet = dataSetService.getDataSet( ds );

            if ( dataSet == null )
            {
                throw new IllegalArgumentException( ERROR_INVALID_DATA_SET + ds );
            }

            dataElements.addAll( dataSet.getDataElements() );
        }

        return dataElements;
    }

    private Set<OrganisationUnit> getOrgUnits( Set<String> orgUnits )
    {
        Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

        for ( String ou : orgUnits )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ou );

            if ( orgUnit == null )
            {
                throw new IllegalArgumentException( ERROR_INVALID_ORG_UNIT + ou );
            }

            organisationUnits.add( orgUnit );
        }

        return organisationUnits;
    }

    private Map<String, OrganisationUnit> getUuidOrgUnitMap()
    {
        Map<String, OrganisationUnit> orgUnitMap = new HashMap<String, OrganisationUnit>();

        Collection<OrganisationUnit> allOrganisationUnits = organisationUnitService.getAllOrganisationUnits();

        for ( OrganisationUnit organisationUnit : allOrganisationUnits )
        {
            orgUnitMap.put( organisationUnit.getUuid(), organisationUnit );
        }

        return orgUnitMap;
    }
}
