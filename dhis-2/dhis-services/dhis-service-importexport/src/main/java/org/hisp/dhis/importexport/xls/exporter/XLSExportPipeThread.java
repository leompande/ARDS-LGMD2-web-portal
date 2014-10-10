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

import java.util.zip.ZipOutputStream;

import jxl.write.WritableWorkbook;

import org.hibernate.SessionFactory;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.XLSConverter;
import org.hisp.dhis.system.process.OpenSessionThread;
import org.hisp.dhis.system.util.ExcelUtils;
import org.hisp.dhis.system.util.StreamUtils;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class XLSExportPipeThread
    extends OpenSessionThread
{
    private ZipOutputStream outputStream;

    public void setOutputStream( ZipOutputStream outputStream )
    {
        this.outputStream = outputStream;
    }

    private ExportParams exportParams;

    public void setExportParams( ExportParams exportParams )
    {
        this.exportParams = exportParams;
    }

    private XLSConverter dataElementConverter;

    public void setDataElementConverter( XLSConverter dataElementConverter )
    {
        this.dataElementConverter = dataElementConverter;
    }

    private XLSConverter indicatorConverter;

    public void setIndicatorConverter( XLSConverter indicatorConverter )
    {
        this.indicatorConverter = indicatorConverter;
    }

    private XLSConverter organisationUnitHierarchyConverter;

    public void setOrganisationUnitHierarchyConverter( XLSConverter organisationUnitHierarchyConverter )
    {
        this.organisationUnitHierarchyConverter = organisationUnitHierarchyConverter;
    }

    private XLSConverter organisationUnitConverter;

    public void setOrganisationUnitConverter( XLSConverter organisationUnitConverter )
    {
        this.organisationUnitConverter = organisationUnitConverter;
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public XLSExportPipeThread( SessionFactory sessionFactory )
    {
        super( sessionFactory );
    }

    // -------------------------------------------------------------------------
    // Thread implementation
    // -------------------------------------------------------------------------

    @Override
    public void doRun()
    {
        int sheetIndex = 0;

        WritableWorkbook workbook = null;

        try
        {
            workbook = ExcelUtils.openWorkbook( outputStream );
            
            dataElementConverter.write( workbook, exportParams, sheetIndex++ );

            indicatorConverter.write( workbook, exportParams, sheetIndex++ );

            organisationUnitConverter.write( workbook, exportParams, sheetIndex++ );

            organisationUnitHierarchyConverter.write( workbook, exportParams, sheetIndex++ );

            ExcelUtils.writeAndCloseWorkbook( workbook );
        }
        finally
        {
            StreamUtils.closeZipEntry( outputStream );
            StreamUtils.closeOutputStream( outputStream );
        }
    }
}
