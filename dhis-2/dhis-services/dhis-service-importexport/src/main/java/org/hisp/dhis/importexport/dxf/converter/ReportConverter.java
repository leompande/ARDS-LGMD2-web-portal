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
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.ReportImporter;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportService;

/**
 * @author Lars Helge Overland
 */
public class ReportConverter
    extends ReportImporter implements XMLConverter
{
    public static final String COLLECTION_NAME = "reports";
    public static final String ELEMENT_NAME = "report";

    private static final String FIELD_ID = "id";
    private static final String FIELD_UID = "uid";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESIGN_CONTENT = "designContent";
    
    /**
     * Constructor for write operations.
     * 
     * @param reportService
     */
    public ReportConverter( ReportService reportService )
    {
        this.reportService = reportService;
    }

    /**
     * Constructor for read operations.
     * 
     * @param reportService
     * @param importObjectService
     */
    public ReportConverter( ReportService reportService, ImportObjectService importObjectService )
    {
        this.reportService = reportService;
        this.importObjectService = importObjectService;
    }
    
    @Override
    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<Report> reports = reportService.getReports( params.getReports() );
        
        if ( reports != null && reports.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( Report report : reports )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( report.getId() ) );
                writer.writeElement( FIELD_UID, report.getUid() );
                writer.writeElement( FIELD_CODE, report.getCode() );
                writer.writeElement( FIELD_NAME, report.getName() );
                writer.writeElement( FIELD_DESIGN_CONTENT, report.getDesignContent() );
                
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
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final Report report = new Report();
            
            report.setId( Integer.parseInt( values.get( FIELD_ID ) ) );

            if ( params.minorVersionGreaterOrEqual( "1.3") )
            {
                report.setUid( values.get( FIELD_UID ) );
                report.setCode( values.get( FIELD_CODE) );
            }

            report.setName( values.get( FIELD_NAME ) );
            report.setDesignContent( values.get( FIELD_DESIGN_CONTENT ) );
            
            importObject( report, params );
        }
    }
}
