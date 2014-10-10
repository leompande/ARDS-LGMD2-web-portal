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

import java.util.Collection;
import java.util.Map;

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.dhis14.util.Dhis14DateUtil;
import org.hisp.dhis.importexport.dhis14.util.Dhis14ObjectMappingUtil;
import org.hisp.dhis.importexport.importer.PeriodImporter;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class PeriodConverter
    extends PeriodImporter implements XMLConverter
{
    public static final String ELEMENT_NAME = "DataPeriod";
    
    private static final String FIELD_ID = "DataPeriodID";
    private static final String FIELD_PERIOD_TYPE = "DataPeriodTypeID";
    private static final String FIELD_START_DATE = "ValidFrom";
    private static final String FIELD_END_DATE = "ValidTo";

    private Map<String, Integer> periodTypeMapping;
        
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public PeriodConverter( PeriodService periodService )
    {   
        this.periodService = periodService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param importObjectService the importObjectService to use.
     * @param periodService the periodService to use.
     * @param periodTypeMapping the periodTypeMapping to use.
     */
    public PeriodConverter( ImportObjectService importObjectService,
        PeriodService periodService,
        Map<String, Integer> periodTypeMapping )
    {
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
            for ( Period period : periods )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( period.getId() ) );
                writer.writeElement( FIELD_PERIOD_TYPE, String.valueOf( period.getPeriodType().getId() ) );
                
                //TODO
                
                writer.closeElement();
            }
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        Period period = new Period();
                
        Map<String, String> values = reader.readElements( ELEMENT_NAME );
        
        Integer periodTypeId = Integer.parseInt( values.get( FIELD_PERIOD_TYPE ) );
        PeriodType periodType = Dhis14ObjectMappingUtil.getPeriodTypeMap().get( periodTypeId );
        period.setPeriodType( periodType );
        
        period.setId( Integer.valueOf( values.get( FIELD_ID ) ) );
        period.getPeriodType().setId( periodTypeMapping.get( periodType.getName() ) );
        period.setStartDate( Dhis14DateUtil.getDate( Integer.parseInt( values.get( FIELD_START_DATE ) ) ) );
        period.setEndDate( Dhis14DateUtil.getDate( Integer.parseInt( values.get( FIELD_END_DATE ) ) ) );
        
        importObject( period, params );
    }
}
