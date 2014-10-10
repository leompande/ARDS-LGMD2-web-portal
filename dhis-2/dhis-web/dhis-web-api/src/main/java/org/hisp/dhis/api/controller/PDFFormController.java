package org.hisp.dhis.api.controller;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.api.utils.PdfDataEntryFormImportUtil;
import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.pdfform.PdfDataEntryFormService;
import org.hisp.dhis.dxf2.pdfform.PdfDataEntryFormUtil;
import org.hisp.dhis.dxf2.pdfform.PdfFormFontSettings;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.i18n.I18nManagerException;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.scheduling.TaskCategory;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.util.StreamUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author James Chang <jamesbchang@gmail.com>
 */
@Controller
@RequestMapping(value = "/pdfForm")
public class PDFFormController
{
    private static final Log log = LogFactory.getLog( PDFFormController.class );

    //private static final String DATEFORMAT_DEFAULT = "MMMM dd, yyyy";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private Notifier notifier;

    @Autowired
    private DataValueSetService dataValueSetService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private I18nManager i18nManager;

    @Autowired
    private PdfDataEntryFormService pdfDataEntryFormService;

    @Autowired
    private ContextUtils contextUtils;

    //--------------------------------------------------------------------------
    // DataSet
    //--------------------------------------------------------------------------

    @RequestMapping(value = "/dataSet/{dataSetUid}", method = RequestMethod.GET)
    public void getFormPDF_DataSet( HttpServletRequest request, HttpServletResponse response,
        @PathVariable String dataSetUid )
        throws Exception
    {
        // 1. - Create Document and PdfWriter
        
        Document document = new Document();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance( document, baos );

        // 2. Generate PDF Document Content
        
        PdfFormFontSettings pdfFormFontSettings = new PdfFormFontSettings();

        PdfDataEntryFormUtil.setDefaultFooterOnDocument( document, request.getServerName(),
            pdfFormFontSettings.getFont( PdfFormFontSettings.FONTTYPE_FOOTER ) );

        pdfDataEntryFormService.generatePDFDataEntryForm( document, writer, dataSetUid,
            PdfDataEntryFormUtil.DATATYPE_DATASET,
            PdfDataEntryFormUtil.getDefaultPageSize( PdfDataEntryFormUtil.DATATYPE_DATASET ),
            pdfFormFontSettings, i18nManager.getI18nFormat() );

        // 3. - Response Header/Content Type Set

        String fileName = dataSetService.getDataSet( dataSetUid ).getName() + " " + (new SimpleDateFormat(
            Period.DEFAULT_DATE_FORMAT )).format( new Date() ) ;
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PDF, CacheStrategy.NO_CACHE, fileName, false );
        response.setContentLength( baos.size() );        
        
        // 4. - Output the data into Stream and close the stream.
        
        writeToOutputStream( baos, response );
    }

    @RequestMapping(value = "/dataSet", method = RequestMethod.POST)
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    public void sendFormPDF_DataSet( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        // 1. Set up Import Option
        
        ImportStrategy strategy = ImportStrategy.NEW_AND_UPDATES;
        IdentifiableProperty dataElementIdScheme = IdentifiableProperty.UID;
        IdentifiableProperty orgUnitIdScheme = IdentifiableProperty.UID;

        ImportOptions options = new ImportOptions( dataElementIdScheme, orgUnitIdScheme, false, true, strategy, false );
                
        log.info( options );

        // 2. Generate Task ID
        
        TaskId taskId = new TaskId( TaskCategory.DATAVALUE_IMPORT, currentUserService.getCurrentUser() );

        notifier.clear( taskId );

        // 3. Input Stream Check
        
        InputStream in = request.getInputStream();

        in = StreamUtils.wrapAndCheckCompressionFormat( in );

        // 4. Save (Import) the data values.
        
        dataValueSetService.saveDataValueSetPdf( in, options, taskId );

        // Step 5. Set the response - just simple OK response.
        
        ContextUtils.okResponse( response, "" );
    }

    //--------------------------------------------------------------------------
    // Program Stage
    //--------------------------------------------------------------------------

    @RequestMapping(value = "/programStage/{programStageUid}", method = RequestMethod.GET)
    public void getFormPDF_ProgramStage( HttpServletRequest request, HttpServletResponse response,
        @PathVariable String programStageUid )
        throws IOException, DocumentException, I18nManagerException
    {
        // 1. - Create Document and PdfWriter
        
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance( document, baos );

        // 2. Generate PDF Document Contents
        
        PdfFormFontSettings pdfFormFontSettings = new PdfFormFontSettings();

        PdfDataEntryFormUtil.setDefaultFooterOnDocument( document, request.getServerName(),
            pdfFormFontSettings.getFont( PdfFormFontSettings.FONTTYPE_FOOTER ) );

        pdfDataEntryFormService.generatePDFDataEntryForm( document, writer, programStageUid,
            PdfDataEntryFormUtil.DATATYPE_PROGRAMSTAGE,
            PdfDataEntryFormUtil.getDefaultPageSize( PdfDataEntryFormUtil.DATATYPE_PROGRAMSTAGE ), 
            pdfFormFontSettings, i18nManager.getI18nFormat() );

        // 3. - Response Header/Content Type Set
        
        String fileName = programStageService.getProgramStage( programStageUid ).getName() + " " 
            + (new SimpleDateFormat(Period.DEFAULT_DATE_FORMAT )).format( new Date() ) ;

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PDF, CacheStrategy.NO_CACHE, fileName, false );
        response.setContentLength( baos.size() );

        // 4. - write ByteArrayOutputStream to the ServletOutputStream
        
        writeToOutputStream( baos, response );
    }

    @RequestMapping( value = "/programStage", method = RequestMethod.POST )
    @PreAuthorize("hasRole('ALL') or hasRole('F_PATIENT_DATAVALUE_ADD')")
    public void sendFormPDF_ProgramStage( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        InputStream in = request.getInputStream();

        // Temporarily using Util class from same project.
        
        PdfDataEntryFormImportUtil pdfDataEntryFormImportUtil = new PdfDataEntryFormImportUtil();

        pdfDataEntryFormImportUtil.ImportProgramStage( in, i18nManager.getI18nFormat() );

        // Step 5. Set the response - just simple OK response.
        
        ContextUtils.okResponse( response, "" );
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    private void writeToOutputStream( ByteArrayOutputStream baos, HttpServletResponse response )
        throws IOException
    {
        OutputStream os = null;

        try
        {
            os = response.getOutputStream();
            baos.writeTo( os );
        }
        finally
        {
            if ( os != null )
            {
                os.flush();
                os.close();
            }
        }
    }
}
