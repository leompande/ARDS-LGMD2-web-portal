package org.hisp.dhis.importexport.dhis14.xml.converter;

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

import static org.hisp.dhis.system.util.CsvUtils.NEWLINE;
import static org.hisp.dhis.system.util.CsvUtils.SEPARATOR_B;
import static org.hisp.dhis.system.util.CsvUtils.csvEncode;
import static org.hisp.dhis.system.util.CsvUtils.getCsvEndValue;
import static org.hisp.dhis.system.util.CsvUtils.getCsvValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.importexport.CSVConverter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.dhis14.util.Dhis14TypeHandler;
import org.hisp.dhis.importexport.importer.DataValueImporter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.MimicingHashMap;
import org.hisp.dhis.system.util.StreamUtils;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataValueConverter
    extends DataValueImporter
    implements CSVConverter
{
    private static final String SEPARATOR = ",";

    private static final String FILENAME = "RoutineData.txt";

    private DataElementCategoryService categoryService;

    private PeriodService periodService;

    private DataElementService dataElementService;

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Object, Integer> dataElementMapping;

    private Map<Object, Integer> periodMapping;

    private Map<Object, Integer> sourceMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DataValueConverter( PeriodService periodService, DataValueService dataValueService,
         DataElementService dataElementService )
    {
        this.periodService = periodService;
        this.dataValueService = dataValueService;
        this.dataElementService = dataElementService;
    }

    /**
     * Constructor for read operations.
     */
    public DataValueConverter( BatchHandler<ImportDataValue> importDataValueBatchHandler,
        DataElementCategoryService categoryService, ImportObjectService importObjectService,
        ImportAnalyser importAnalyser, ImportParams params )
    {
        this.importDataValueBatchHandler = importDataValueBatchHandler;
        this.categoryService = categoryService;
        this.importObjectService = importObjectService;
        this.importAnalyser = importAnalyser;
        this.params = params;
        this.dataElementMapping = new MimicingHashMap<Object, Integer>();
        this.periodMapping = new MimicingHashMap<Object, Integer>();
        this.sourceMapping = new MimicingHashMap<Object, Integer>();
    }

    // -------------------------------------------------------------------------
    // CSVConverter implementation
    // -------------------------------------------------------------------------

    public void write( ZipOutputStream out, ExportParams params )
    {
        try
        {
            out.putNextEntry( new ZipEntry( FILENAME ) );

            out.write( getCsvValue( csvEncode( "RoutineDataID" ) ) );
            out.write( getCsvValue( csvEncode( "OrgUnitID" ) ) );
            out.write( getCsvValue( csvEncode( "DataElementID" ) ) );
            out.write( getCsvValue( csvEncode( "DataPeriodID" ) ) );
            out.write( getCsvValue( csvEncode( "EntryText" ) ) );
            out.write( getCsvValue( csvEncode( "EntryYesNo" ) ) );
            out.write( getCsvValue( csvEncode( "EntryNumber" ) ) );
            out.write( getCsvValue( csvEncode( "EntryDate" ) ) );
            out.write( getCsvValue( csvEncode( "EntryMemo" ) ) );
            out.write( getCsvValue( csvEncode( "EntryObject" ) ) );
            out.write( getCsvValue( csvEncode( "Check" ) ) );
            out.write( getCsvValue( csvEncode( "Verified" ) ) );
            out.write( getCsvValue( csvEncode( "Deleted" ) ) );
            out.write( getCsvValue( csvEncode( "Comment" ) ) );
            out.write( getCsvValue( csvEncode( "LastUserID" ) ) );
            out.write( getCsvEndValue( csvEncode( "LastUpdated" ) ) );

            out.write( NEWLINE );

            if ( params.isIncludeDataValues() )
            {
                if ( params.getStartDate() != null && params.getEndDate() != null )
                {
                    Collection<DeflatedDataValue> values = null;

                    Collection<Period> periods = periodService.getIntersectingPeriods( params.getStartDate(),
                        params.getEndDate() );

                    for ( final Integer element : params.getDataElements() )
                    {
                        for ( final Period period : periods )
                        {
                            values = dataValueService.getDeflatedDataValues( element, period.getId(),
                                params.getOrganisationUnits() );

                            for ( final DeflatedDataValue value : values )
                            {
                                out.write( getCsvValue( 0 ) );
                                out.write( getCsvValue( value.getSourceId() ) );
                                out.write( getCsvValue( value.getDataElementId() ) );
                                out.write( getCsvValue( value.getPeriodId() ) );
                                out = getCSVDataExportField( out, value );
                                out.write( getCsvValue( 0 ) );
                                out.write( getCsvValue( 0 ) );
                                out.write( getCsvValue( 0 ) );
                                out.write( getCsvValue( csvEncode( value.getComment() ) ) );
                                out.write( getCsvValue( 1 ) );
                                out.write( getCsvEndValue( DateUtils.getAccessDateString( value.getTimestamp() ) ) );

                                out.write( NEWLINE );
                            }
                        }
                    }
                }
            }

            StreamUtils.closeZipEntry( out );
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed to write data", ex );
        }
    }

    public void read( BufferedReader reader, ImportParams params )
    {
        String line = "";

        DataValue value = new DataValue();
        DataElement dataElement = new DataElement();
        Period period = new Period();
        OrganisationUnit organisationUnit = new OrganisationUnit();
        DataElementCategoryOptionCombo categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        DataElementCategoryOptionCombo proxyCategoryOptionCombo = new DataElementCategoryOptionCombo();
        proxyCategoryOptionCombo.setId( categoryOptionCombo.getId() );
        final String owner = params.getOwner();

        try
        {
            reader.readLine(); // Skip CSV header

            while ( (line = reader.readLine()) != null )
            {
                String[] values = line.split( SEPARATOR );
                Boolean validValue = true;

                dataElement.setId( dataElementMapping.get( Integer.parseInt( values[2] ) ) );
                period.setId( periodMapping.get( Integer.parseInt( values[3] ) ) );
                organisationUnit.setId( sourceMapping.get( Integer.parseInt( values[1] ) ) );

                value.setDataElement( dataElement );
                value.setPeriod( period );
                value.setSource( organisationUnit );

                if ( !values[6].isEmpty() ) // Numeric
                {
                    value.setValue( handleNumericValue( values[6] ) );
                    validValue = isValidNumeric( value.getValue() );

                }
                else if ( !values[4].isEmpty() ) // Text
                {
                    value.setValue( values[4].trim() );
                }
                else if ( !values[5].isEmpty() ) // Boolean
                {
                    value.setValue(Dhis14TypeHandler.convertYesNoFromDhis14( Integer.parseInt(values[5]) ) );

                }
                else if ( !values[7].isEmpty() ) // Date
                {
                    value.setValue( values[7] );

                }
                else if ( !values[8].isEmpty() ) // Memo not supported
                {
                    validValue = false;
                }

                else if ( !values[9].isEmpty() ) // OLE not supported
                {
                    validValue = false;
                }

                value.setComment( values[13] );
                value.setTimestamp( DateUtils.getDefaultDate( values[15] ) );
                value.setCategoryOptionCombo( proxyCategoryOptionCombo );
                value.setStoredBy( owner );

                if ( validValue )
                {
                    importObject( value, params );
                }
            }
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed to read data", ex );
        }
    }

    // -------------------------------------------------------------------------
    // CSVConverter implementation
    // -------------------------------------------------------------------------

    private String handleNumericValue( String value )
    {
        if ( value != null )
        {
            // Remove all spaces
            value = value.replaceAll( " ", "" );
            // Remove all quotes
            value = value.replaceAll( "\"", "" );
            // Strip trailing zeros
            value = value.replaceAll( "\\.0+$", "" );
        }

        return value;
    }

    private boolean isValidNumeric( String value )
    {
        return value != null && value.matches( "-?\\d+(\\.\\d+)?" );
    }

    private ZipOutputStream getCSVDataExportField( ZipOutputStream out, DeflatedDataValue value )
    {

        String dataElementType = dataElementService.getDataElement( value.getDataElementId() ).getType();

        try
        {
            if ( dataElementType.equals( DataElement.VALUE_TYPE_STRING ) )
            {
                out.write( getCsvValue( csvEncode( value.getValue() ) ) );
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
            }

            else if ( dataElementType.equals( DataElement.VALUE_TYPE_BOOL ) )
            {
                out.write( SEPARATOR_B );
                out.write( getCsvValue( csvEncode( Dhis14TypeHandler.convertBooleanToDhis14( value.getValue()) ) ) );
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
            }

            else if ( dataElementType.equals( DataElement.VALUE_TYPE_NUMBER )
                || dataElementType.equals( DataElement.VALUE_TYPE_INT )
                || dataElementType.equals( DataElement.VALUE_TYPE_NEGATIVE_INT )
                || dataElementType.equals( DataElement.VALUE_TYPE_POSITIVE_INT ) 
                || dataElementType.equals( DataElement.VALUE_TYPE_ZERO_OR_POSITIVE_INT) )
            {
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
                out.write( getCsvValue( csvEncode( value.getValue() ) ) );
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
            }

            else if ( dataElementType.equals( DataElement.VALUE_TYPE_DATE ) )
            {
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
                out.write( getCsvValue( csvEncode( DateUtils.getDefaultDate( value.getValue() ) ) ) );
                out.write( SEPARATOR_B );
                out.write( SEPARATOR_B );
            }
        }

        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed handle CSV data field export", ex );
        }

        return out;
    }
}
