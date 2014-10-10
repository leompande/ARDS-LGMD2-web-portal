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

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.ChartImporter;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.RelativePeriods;

public class ChartConverter
    extends ChartImporter implements XMLConverter
{
    public static final String COLLECTION_NAME = "charts";
    public static final String ELEMENT_NAME = "chart";

    private static final String FIELD_ID = "id";
    private static final String FIELD_UID = "uid";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_HIDE_LEGEND = "hideLegend";
    private static final String FIELD_REGRESSION = "regression";

    private static final String FIELD_INDICATORS = "indicators";
    private static final String FIELD_ORGANISATION_UNITS = "organisationUnits";

    private static final String FIELD_REPORTING_MONTH = "reportingMonth";
    private static final String FIELD_MONTHS_THIS_YEAR = "monthsThisYear";
    private static final String FIELD_QUARTERS_THIS_YEAR = "quartersThisYear";
    private static final String FIELD_THIS_YEAR = "thisYear";
    private static final String FIELD_MONTHS_LAST_YEAR = "monthsLastYear";
    private static final String FIELD_QUARTERS_LAST_YEAR = "quartersLastYear";
    private static final String FIELD_LAST_YEAR = "lastYear";
    
    private IndicatorService indicatorService;
    private OrganisationUnitService organisationUnitService;

    private Map<Object, Integer> indicatorMapping;
    private Map<Object, Integer> organisationUnitMapping;

    /**
     * Constructor for write operations.
     * 
     * @param chartService
     */
    public ChartConverter( ChartService chartService )
    {
        this.chartService = chartService;
    }

    /**
     * Constructor for read operations.
     * 
     * @param chartService
     */
    public ChartConverter( ChartService chartService, 
        ImportObjectService importObjectService, 
        IndicatorService indicatorService,
        OrganisationUnitService organisationUnitService,
        Map<Object, Integer> indicatorMapping,
        Map<Object, Integer> organisationUnitMapping )
    {
        this.chartService = chartService;
        this.importObjectService = importObjectService;
        this.indicatorService = indicatorService;
        this.organisationUnitService = organisationUnitService;
        this.indicatorMapping = indicatorMapping;
        this.organisationUnitMapping = organisationUnitMapping;
    }
    
    @Override
    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<Chart> charts = chartService.getCharts( params.getCharts() );
        
        if ( charts != null && charts.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( Chart chart : charts )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( chart.getId() ) );
                writer.writeElement( FIELD_UID, chart.getUid() );    //v1.2.1
                writer.writeElement( FIELD_CODE, chart.getCode()  );  // v1.2.1
                writer.writeElement( FIELD_TITLE,  chart.getName() );
                writer.writeElement( FIELD_TYPE, chart.getType() );
                writer.writeElement( FIELD_HIDE_LEGEND, String.valueOf( chart.isHideLegend() ) );
                writer.writeElement( FIELD_REGRESSION, String.valueOf( chart.isRegression() ) );                

                writer.openElement( FIELD_INDICATORS );
                for ( Indicator indicator : chart.getIndicators() )
                {
                    writer.writeElement( FIELD_ID, String.valueOf( indicator.getId() ) );
                }
                writer.closeElement();

                writer.openElement( FIELD_ORGANISATION_UNITS );
                for ( OrganisationUnit unit : chart.getOrganisationUnits() )
                {
                    writer.writeElement( FIELD_ID, String.valueOf( unit.getId() ) );
                }
                writer.closeElement();                

                writer.writeElement( FIELD_REPORTING_MONTH, String.valueOf( chart.getRelatives().isReportingMonth() ) );
                writer.writeElement( FIELD_MONTHS_THIS_YEAR, String.valueOf( chart.getRelatives().isMonthsThisYear() ) );
                writer.writeElement( FIELD_QUARTERS_THIS_YEAR, String.valueOf( chart.getRelatives().isQuartersThisYear() ) );
                writer.writeElement( FIELD_THIS_YEAR, String.valueOf( chart.getRelatives().isThisYear() ) );
                writer.writeElement( FIELD_MONTHS_LAST_YEAR, String.valueOf( chart.getRelatives().isMonthsLastYear() ) );
                writer.writeElement( FIELD_QUARTERS_LAST_YEAR, String.valueOf( chart.getRelatives().isQuartersLastYear() ) );
                writer.writeElement( FIELD_LAST_YEAR, String.valueOf( chart.getRelatives().isLastYear() ) );

                writer.closeElement();
            }

            writer.closeElement();
        }        
    }
    
    @Override
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Chart chart = new Chart();
            
            final RelativePeriods relatives = new RelativePeriods();
            chart.setRelatives( relatives );

            reader.moveToStartElement( FIELD_ID );
            chart.setId( Integer.parseInt( reader.getElementValue() ) );

            if (params.minorVersionGreaterOrEqual( "1.3")) {
                reader.moveToStartElement( FIELD_UID );
                chart.setUid( reader.getElementValue() );
                reader.moveToStartElement( FIELD_CODE );
                chart.setCode( reader.getElementValue() );
            }

            reader.moveToStartElement( FIELD_TITLE );
            chart.setName( reader.getElementValue() );

            reader.moveToStartElement( FIELD_TYPE );
            chart.setType( reader.getElementValue() );

            reader.moveToStartElement( FIELD_HIDE_LEGEND );
            chart.setHideLegend( Boolean.parseBoolean( reader.getElementValue() ) );

            reader.moveToStartElement( FIELD_REGRESSION );
            chart.setRegression( Boolean.parseBoolean( reader.getElementValue() ) );
            
            while ( reader.moveToStartElement( FIELD_ID, FIELD_INDICATORS ) )
            {
                int id = Integer.parseInt( reader.getElementValue() );
                chart.getIndicators().add( indicatorService.getIndicator( indicatorMapping.get( id ) ) );
            }

            while ( reader.moveToStartElement( FIELD_ID, FIELD_ORGANISATION_UNITS ) )
            {
                int id = Integer.parseInt( reader.getElementValue() );
                chart.getOrganisationUnits().add( organisationUnitService.getOrganisationUnit( organisationUnitMapping.get( id ) ) );
            }
            
            if ( params.minorVersionGreaterOrEqual( DXFConverter.MINOR_VERSION_12 ) )
            {
                reader.moveToStartElement( FIELD_REPORTING_MONTH );          
                chart.getRelatives().setReportingMonth( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_MONTHS_THIS_YEAR );
                chart.getRelatives().setMonthsThisYear( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_QUARTERS_THIS_YEAR );
                chart.getRelatives().setQuartersThisYear( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_THIS_YEAR );
                chart.getRelatives().setThisYear( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_MONTHS_LAST_YEAR );
                chart.getRelatives().setMonthsLastYear( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_QUARTERS_LAST_YEAR );
                chart.getRelatives().setQuartersLastYear( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_LAST_YEAR );
                chart.getRelatives().setLastYear( Boolean.parseBoolean( reader.getElementValue() ) );
            }
            
            importObject( chart, params );
        }
    }
}
