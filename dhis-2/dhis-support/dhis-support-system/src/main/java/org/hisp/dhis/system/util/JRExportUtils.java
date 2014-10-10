package org.hisp.dhis.system.util;

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

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;

/**
 * Supports PDF and XLS exports.
 * 
 * @author Lars Helge Overland
 */
public class JRExportUtils
{
    public static final String TYPE_XLS = "xls";
    public static final String TYPE_PDF = "pdf";
    public static final String TYPE_HTML = "html";
    
    private static final Map<String, JRExportProvider> exporters = new HashMap<String, JRExportProvider>() {    
    {
        put( TYPE_XLS, new JRXlsExportProvider() );
        put( TYPE_PDF, new JRPdfExportProvider() );
        put( TYPE_HTML, new JRHtmlExportProvider() );
    } };
    
    public static void export( String type, OutputStream out, JasperPrint jasperPrint )
        throws JRException
    {
        JRExportProvider provider = exporters.get( type );
        
        if ( provider != null )
        {
            JRAbstractExporter exporter = provider.provide();
            
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, out );
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.exportReport();
        }
    }
    
    private interface JRExportProvider
    {
        JRAbstractExporter provide();
    }
    
    private static class JRXlsExportProvider implements JRExportProvider
    {
        public JRAbstractExporter provide()
        {
            JRXlsExporter exporter = new JRXlsExporter();
            exporter.setParameter( JRXlsAbstractExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE );
            exporter.setParameter( JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE );
            exporter.setParameter( JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE );
            exporter.setParameter( JRXlsAbstractExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE );
            exporter.setParameter( JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE );
            return exporter;
        }
    }
    
    private static class JRPdfExportProvider implements JRExportProvider
    {
        public JRAbstractExporter provide()
        {
            return new JRPdfExporter();
        }
    }
    
    private static class JRHtmlExportProvider implements JRExportProvider
    {
        public JRAbstractExporter provide()
        {
            JRHtmlExporter exporter = new JRHtmlExporter();
            exporter.setParameter( JRHtmlExporterParameter.IMAGES_URI, "../jasperReports/img?image=" );
            
            return exporter;
        }
    }
}


