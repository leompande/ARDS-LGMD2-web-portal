package org.hisp.dhis.importexport.dxf.converter;

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

import java.util.Collection;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.PeriodImporter;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: PeriodConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class PeriodConverter
    extends PeriodImporter implements XMLConverter
{
    public static final String COLLECTION_NAME = "periods";
    public static final String ELEMENT_NAME = "period";
    
    private static final String FIELD_ID = "id";
    private static final String FIELD_PERIOD_TYPE = "periodType";
    private static final String FIELD_START_DATE = "startDate";
    private static final String FIELD_END_DATE = "endDate";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<String, Integer> periodTypeMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public PeriodConverter(PeriodService periodService)
    {   
        this.periodService = periodService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param periodService the periodService to use.
     * @param importObjectService the importObjectService to use.
     */
    public PeriodConverter( BatchHandler<Period> batchHandler, 
        ImportObjectService importObjectService, 
        PeriodService periodService,
        Map<String, Integer> periodTypeMapping )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.periodService = periodService;
        this.periodTypeMapping = periodTypeMapping;
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<Period> periods = periodService.getPeriods( params.getPeriods() );
        
        if ( periods != null && periods.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( Period period : periods )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( period.getId() ) );
                writer.writeElement( FIELD_PERIOD_TYPE, String.valueOf( period.getPeriodType().getName() ) );
                writer.writeElement( FIELD_START_DATE, DateUtils.getMediumDateString( period.getStartDate() ) );
                writer.writeElement( FIELD_END_DATE, DateUtils.getMediumDateString( period.getEndDate() ) );
                
                writer.closeElement();
            }
            
            writer.closeElement();
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final Period period = new Period();
            
            PeriodType periodType = new MonthlyPeriodType();
            period.setPeriodType( periodType );

            Integer periodTypeId = periodTypeMapping.get( values.get( FIELD_PERIOD_TYPE ) );
            
            if ( periodTypeId != null )
            {
                period.getPeriodType().setId( periodTypeId );                
            }
            else
            {
                log.warn( "Unknow period type, falling back to Monthly: " + values.get( FIELD_PERIOD_TYPE ) );
                
                period.getPeriodType().setId( periodTypeMapping.get( MonthlyPeriodType.NAME ) );
            }
            
            period.setId( Integer.parseInt( values.get( FIELD_ID ) ) );

            period.setStartDate( DateUtils.getMediumDate( values.get( FIELD_START_DATE ) ) );
            period.setEndDate( DateUtils.getMediumDate( values.get( FIELD_END_DATE ) ) );

            importObject( period, params );
        }
    }
}
