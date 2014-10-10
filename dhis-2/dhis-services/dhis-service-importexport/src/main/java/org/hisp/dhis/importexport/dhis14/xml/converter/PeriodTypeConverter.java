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

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

/**
 * @author Lars Helge Overland
 * @version $Id: PeriodTypeConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class PeriodTypeConverter
    implements XMLConverter
{
    public static final String ELEMENT_NAME = "DataPeriodType";
    
    private static final String FIELD_ID = "DataPeriodTypeID";
    private static final String FIELD_NAME_ENG = "DataPeriodTypeNameEng";
    private static final String FIELD_NAME = "DataPeriodTypeName";
    private static final String FIELD_SORT_ORDER = "SortOrder";
    private static final String FIELD_FORMAT = "DataPeriodFormat";
    private static final String FIELD_ANNUALISATION_FACTOR = "AnnualisationFactor";
    private static final String FIELD_UPPER_RANGE_DAY_COUNT = "UpperRangeDayCount";
    private static final String FIELD_INTERVAL_SETTING = "DataPeriodIntervalSetting";
    private static final String FIELD_FIRST_DAY_OF_WEEK = "FirstDayOfWeekID";
    private static final String FIELD_FIRST_WEEK_OF_YEAR = "FirstWeekOfYearID";
    private static final String FIELD_SUPPORTED = "Supported";
    private static final String FIELD_BASE_PART = "BasePeriodPart";
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public PeriodTypeConverter()
    {   
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------
    
    public void write( XMLWriter writer, ExportParams params )
    {
        // ---------------------------------------------------------------------
        // Monthly
        // ---------------------------------------------------------------------

        writer.openElement( ELEMENT_NAME );
        
        writer.writeElement( FIELD_ID, String.valueOf( 1 ) );
        writer.writeElement( FIELD_NAME_ENG, MonthlyPeriodType.NAME );
        writer.writeElement( FIELD_NAME, MonthlyPeriodType.NAME );
        writer.writeElement( FIELD_SORT_ORDER, String.valueOf( 1 ) );
        writer.writeElement( FIELD_FORMAT, "MMM-Y" );
        writer.writeElement( FIELD_ANNUALISATION_FACTOR, String.valueOf( 12 ) );
        writer.writeElement( FIELD_UPPER_RANGE_DAY_COUNT, String.valueOf( 31 ) );
        writer.writeElement( FIELD_INTERVAL_SETTING, "m" );
        writer.writeElement( FIELD_FIRST_DAY_OF_WEEK, String.valueOf( 1 ) );
        writer.writeElement( FIELD_FIRST_WEEK_OF_YEAR, String.valueOf( 0 ) );
        writer.writeElement( FIELD_SUPPORTED, String.valueOf( 1 ) );
        writer.writeElement( FIELD_BASE_PART, "MMM" );
        
        writer.closeElement();

        // ---------------------------------------------------------------------
        // Quarterly
        // ---------------------------------------------------------------------

        writer.openElement( ELEMENT_NAME );
        
        writer.writeElement( FIELD_ID, String.valueOf( 2 ) );
        writer.writeElement( FIELD_NAME_ENG, QuarterlyPeriodType.NAME );
        writer.writeElement( FIELD_NAME, QuarterlyPeriodType.NAME );
        writer.writeElement( FIELD_SORT_ORDER, String.valueOf( 2 ) );
        writer.writeElement( FIELD_FORMAT, "yyyy \"Q\"Q" );
        writer.writeElement( FIELD_ANNUALISATION_FACTOR, String.valueOf( 4 ) );
        writer.writeElement( FIELD_UPPER_RANGE_DAY_COUNT, String.valueOf( 92 ) );
        writer.writeElement( FIELD_INTERVAL_SETTING, "q" );
        writer.writeElement( FIELD_FIRST_DAY_OF_WEEK, String.valueOf( 0 ) );
        writer.writeElement( FIELD_FIRST_WEEK_OF_YEAR, String.valueOf( 0 ) );
        writer.writeElement( FIELD_SUPPORTED, String.valueOf( 1 ) );
        writer.writeElement( FIELD_BASE_PART, "\"Q\"Q" );
        
        writer.closeElement();

        // ---------------------------------------------------------------------
        // Yearly
        // ---------------------------------------------------------------------

        writer.openElement( ELEMENT_NAME );
        
        writer.writeElement( FIELD_ID, String.valueOf( 3 ) );
        writer.writeElement( FIELD_NAME_ENG, YearlyPeriodType.NAME );
        writer.writeElement( FIELD_NAME, YearlyPeriodType.NAME );
        writer.writeElement( FIELD_SORT_ORDER, String.valueOf( 3 ) );
        writer.writeElement( FIELD_FORMAT, "yyyy" );
        writer.writeElement( FIELD_ANNUALISATION_FACTOR, String.valueOf( 1 ) );
        writer.writeElement( FIELD_UPPER_RANGE_DAY_COUNT, String.valueOf( 365 ) );
        writer.writeElement( FIELD_INTERVAL_SETTING, "yyyy" );
        writer.writeElement( FIELD_FIRST_DAY_OF_WEEK, String.valueOf( 1 ) );
        writer.writeElement( FIELD_FIRST_WEEK_OF_YEAR, String.valueOf( 1 ) );
        writer.writeElement( FIELD_SUPPORTED, String.valueOf( 1 ) );
        writer.writeElement( FIELD_BASE_PART, "yyyy" );
        
        writer.closeElement();

        // ---------------------------------------------------------------------
        // Daily
        // ---------------------------------------------------------------------
        
        writer.openElement( ELEMENT_NAME );
        
        writer.writeElement( FIELD_ID, String.valueOf( 6 ) );
        writer.writeElement( FIELD_NAME_ENG, DailyPeriodType.NAME );
        writer.writeElement( FIELD_NAME, DailyPeriodType.NAME );
        writer.writeElement( FIELD_SORT_ORDER, String.valueOf( 6 ) );
        writer.writeElement( FIELD_FORMAT, "dd/mm/yyyy" );
        writer.writeElement( FIELD_ANNUALISATION_FACTOR, String.valueOf( 365 ) );
        writer.writeElement( FIELD_UPPER_RANGE_DAY_COUNT, String.valueOf( 1 ) );
        writer.writeElement( FIELD_INTERVAL_SETTING, "d" );
        writer.writeElement( FIELD_FIRST_DAY_OF_WEEK, String.valueOf( 1 ) );
        writer.writeElement( FIELD_FIRST_WEEK_OF_YEAR, String.valueOf( 0 ) );
        writer.writeElement( FIELD_SUPPORTED, String.valueOf( 0 ) );
        
        writer.closeElement();
        
        // ---------------------------------------------------------------------
        // Weekly
        // ---------------------------------------------------------------------

        writer.openElement( ELEMENT_NAME );
        
        writer.writeElement( FIELD_ID, String.valueOf( 7 ) );
        writer.writeElement( FIELD_NAME_ENG, WeeklyPeriodType.NAME );
        writer.writeElement( FIELD_NAME, WeeklyPeriodType.NAME );
        writer.writeElement( FIELD_SORT_ORDER, String.valueOf( 7 ) );
        writer.writeElement( FIELD_FORMAT, "yyyy \"w\"W" );
        writer.writeElement( FIELD_ANNUALISATION_FACTOR, String.valueOf( 52 ) );
        writer.writeElement( FIELD_UPPER_RANGE_DAY_COUNT, String.valueOf( 7 ) );
        writer.writeElement( FIELD_INTERVAL_SETTING, "w" );
        writer.writeElement( FIELD_FIRST_DAY_OF_WEEK, String.valueOf( 1 ) );
        writer.writeElement( FIELD_FIRST_WEEK_OF_YEAR, String.valueOf( 0 ) );
        writer.writeElement( FIELD_SUPPORTED, String.valueOf( 1 ) );
        writer.writeElement( FIELD_BASE_PART, "\"w\"W" );
        
        writer.closeElement();

        // ---------------------------------------------------------------------
        // Six-Monthly
        // ---------------------------------------------------------------------

        writer.openElement( ELEMENT_NAME );
        
        writer.writeElement( FIELD_ID, String.valueOf( 8 ) );
        writer.writeElement( FIELD_NAME_ENG, "Six-monthly" );
        writer.writeElement( FIELD_NAME, "Six-monthly" );
        writer.writeElement( FIELD_SORT_ORDER, String.valueOf( 8 ) );
        writer.writeElement( FIELD_FORMAT, "yyyy-x" );
        writer.writeElement( FIELD_ANNUALISATION_FACTOR, String.valueOf( 2 ) );
        writer.writeElement( FIELD_UPPER_RANGE_DAY_COUNT, String.valueOf( 183 ) );
        writer.writeElement( FIELD_FIRST_DAY_OF_WEEK, String.valueOf( 1 ) );
        writer.writeElement( FIELD_FIRST_WEEK_OF_YEAR, String.valueOf( 0 ) );
        writer.writeElement( FIELD_SUPPORTED, String.valueOf( 0 ) );
        
        writer.closeElement();

        // ---------------------------------------------------------------------
        // Two-Yearly
        // ---------------------------------------------------------------------

        writer.openElement( ELEMENT_NAME );
        
        writer.writeElement( FIELD_ID, String.valueOf( 9 ) );
        writer.writeElement( FIELD_NAME_ENG, "Two-yearly" );
        writer.writeElement( FIELD_NAME, "Two-yearly" );
        writer.writeElement( FIELD_SORT_ORDER, String.valueOf( 9 ) );
        writer.writeElement( FIELD_ANNUALISATION_FACTOR, String.valueOf( 0.5 ) );
        writer.writeElement( FIELD_UPPER_RANGE_DAY_COUNT, String.valueOf( 721 ) );
        writer.writeElement( FIELD_FIRST_DAY_OF_WEEK, String.valueOf( 1 ) );
        writer.writeElement( FIELD_FIRST_WEEK_OF_YEAR, String.valueOf( 0 ) );
        writer.writeElement( FIELD_SUPPORTED, String.valueOf( 0 ) );
        
        writer.closeElement();
    }

    public void read( XMLReader reader, ImportParams params )
    {
        // Not implemented        
    }
}
