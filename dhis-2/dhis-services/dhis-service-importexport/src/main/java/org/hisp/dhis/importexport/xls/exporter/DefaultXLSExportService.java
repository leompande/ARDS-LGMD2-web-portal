package org.hisp.dhis.importexport.xls.exporter;

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

import org.hibernate.SessionFactory;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ExportService;
import org.hisp.dhis.importexport.xls.converter.DataElementConverter;
import org.hisp.dhis.importexport.xls.converter.IndicatorConverter;
import org.hisp.dhis.importexport.xls.converter.OrganisationUnitConverter;
import org.hisp.dhis.importexport.xls.converter.OrganisationUnitHierarchyConverter;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class DefaultXLSExportService
    implements ExportService
{
    private static final String ZIP_ENTRY_NAME = "Export.xls";

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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    // -------------------------------------------------------------------------
    // ExportService implementation
    // -------------------------------------------------------------------------

    public InputStream exportData( ExportParams params )
    {
        try
        {
            // -------------------------------------------------------------------------
            // Pipes are input/output pairs. Data written on the output stream
            // shows
            // up on the input stream at the other end of the pipe.
            // -------------------------------------------------------------------------

            PipedOutputStream out = new PipedOutputStream();

            PipedInputStream in = new PipedInputStream( out );

            ZipOutputStream zipOut = new ZipOutputStream( out );

            zipOut.putNextEntry( new ZipEntry( ZIP_ENTRY_NAME ) );

            // -------------------------------------------------------------------------
            // Writes to one end of the pipe
            // -------------------------------------------------------------------------

            XLSExportPipeThread thread = new XLSExportPipeThread( sessionFactory );

            thread.setOutputStream( zipOut );
            thread.setExportParams( params );

            thread.setDataElementConverter( new DataElementConverter( dataElementService ) );
            thread.setIndicatorConverter( new IndicatorConverter( indicatorService, expressionService ) );
            thread.setOrganisationUnitHierarchyConverter( new OrganisationUnitHierarchyConverter( organisationUnitService ) );
            thread.setOrganisationUnitConverter( new OrganisationUnitConverter( organisationUnitService ) );

            thread.start();

            return new BufferedInputStream( in );
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Error occured during PDF export", ex );
        }
    }
}
