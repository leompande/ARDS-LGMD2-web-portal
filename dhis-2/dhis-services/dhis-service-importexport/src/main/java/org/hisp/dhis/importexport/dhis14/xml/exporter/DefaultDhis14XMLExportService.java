package org.hisp.dhis.importexport.dhis14.xml.exporter;

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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hibernate.SessionFactory;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ExportPipeThread;
import org.hisp.dhis.importexport.ExportService;
import org.hisp.dhis.importexport.dhis14.xml.converter.DataElementConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.DataTypeConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.DataValueConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.IndicatorConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.IndicatorTypeConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.OrganisationUnitConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.OrganisationUnitHierarchyConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.PeriodTypeConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.UserConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.UserRoleConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.xsd.DataElementXSDConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.xsd.DataRootXSDConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.xsd.DataTypeXSDConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.xsd.IndicatorTypeXSDConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.xsd.IndicatorXSDConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.xsd.OrganisationUnitHierarchyXSDConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.xsd.OrganisationUnitXSDConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.xsd.PeriodTypeXSDConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.xsd.UserRoleXSDConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.xsd.UserXSDConverter;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;


/**
 * @author Lars Helge Overland
 * @version $Id: DefaultDhis14XMLExportService.java 5793 2008-10-02 14:14:00Z larshelg $
 */
public class DefaultDhis14XMLExportService
    implements ExportService
{
    private static final String ENCODING = "ISO-8859-1";
    private static final String ZIP_ENTRY_NAME = "Export.xml";
    private static final String ROOT_NAME = "root";
    private static final String[] ROOT_PROPERTIES = { "xmlns:xsd", "http://www.w3.org/2001/XMLSchema", 
                                  "xmlns:od", "urn:schemas-microsoft-com:officedata" };

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;
    
    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // ExportService implementation
    // -------------------------------------------------------------------------

    public InputStream exportData( ExportParams params )
    {
        try
        {
            // -------------------------------------------------------------------------
            // Pipes are input/output pairs. Data written on the output stream shows 
            // up on the input stream at the other end of the pipe. 
            // -------------------------------------------------------------------------
            
            PipedOutputStream out = new PipedOutputStream();
            
            PipedInputStream in = new PipedInputStream( out );

            ZipOutputStream zipOut = new ZipOutputStream( out );
            
            zipOut.putNextEntry( new ZipEntry( ZIP_ENTRY_NAME ) );

            XMLWriter writer = XMLFactory.getXMLWriter( zipOut );

            // -------------------------------------------------------------------------
            // Writes to one end of the pipe 
            // -------------------------------------------------------------------------

            ExportPipeThread thread = new Dhis14ExportPipeThread( sessionFactory );
            
            thread.setZipOutputStream( zipOut );
            thread.setParams( params );
            thread.setWriter( writer );
            thread.setEncoding( ENCODING );
            thread.setRootName( ROOT_NAME );
            thread.setRootProperties( ROOT_PROPERTIES );

            thread.registerXSDConverter( new DataRootXSDConverter() );
            thread.registerXSDConverter( new DataElementXSDConverter() );
            thread.registerXSDConverter( new PeriodTypeXSDConverter() );
            thread.registerXSDConverter(new OrganisationUnitXSDConverter() );
            thread.registerXSDConverter( new OrganisationUnitHierarchyXSDConverter() );
            thread.registerXSDConverter( new IndicatorTypeXSDConverter() );
            thread.registerXSDConverter( new IndicatorXSDConverter() );
            thread.registerXSDConverter( new DataTypeXSDConverter() );
            thread.registerXSDConverter( new UserXSDConverter() );
            thread.registerXSDConverter( new UserRoleXSDConverter() );
            
            thread.registerXMLConverter( new DataElementConverter( dataElementService ) );
            thread.registerXMLConverter( new PeriodTypeConverter() );
            thread.registerXMLConverter( new PeriodTypeConverter() );
            thread.registerXMLConverter( new OrganisationUnitConverter( organisationUnitService ) );
            //thread.registerXMLConverter( new OrganisationUnitGroupConverter( organisationUnitGroupService ) );
            //thread.registerXMLConverter( new OrganisationUnitGroupMemberConverter( organisationUnitGroupService, organisationUnitService ) );
            thread.registerXMLConverter( new OrganisationUnitHierarchyConverter(  organisationUnitService ) );
            thread.registerXMLConverter( new IndicatorTypeConverter( indicatorService ) );
            thread.registerXMLConverter( new IndicatorConverter( indicatorService ) );            
            thread.registerXMLConverter( new DataTypeConverter() );
            thread.registerXMLConverter( new UserConverter() );
            thread.registerXMLConverter( new UserRoleConverter() );
            
            thread.registerCSVConverter( new DataValueConverter( periodService, dataValueService, dataElementService ) );
            
            thread.start();

            // -------------------------------------------------------------------------
            // Reads at the other end of the pipe 
            // -------------------------------------------------------------------------
            
            InputStream bis = new BufferedInputStream( in );
            
            return bis;
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Error occured during export to stream", ex );
        }
    }
}

