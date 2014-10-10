package org.hisp.dhis.appmanager.action;

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
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.appmanager.AppManager;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.system.util.StreamUtils;
import org.hisp.dhis.util.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.opensymphony.xwork2.Action;

/**
 * @author Saptarshi Purkayastha
 */
public class AddAppAction
    implements Action
{
    private static final Log log = LogFactory.getLog( AddAppAction.class );
    
    private static final String FAILURE = "failure";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private AppManager appManager;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private File file;

    public void setUpload( File file )
    {
        this.file = file;
    }

    private String fileName;

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private String contentType;

    public void setUploadContentType( String contentType )
    {
        this.contentType = contentType;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( file == null )
        {
            message = i18n.getString( "appmanager_no_file_specified" );
            log.warn( "No file specified" );
            return FAILURE;
        }

        if ( !StreamUtils.isZip( new BufferedInputStream( new FileInputStream( file ) ) ) )
        {
            message = i18n.getString( "appmanager_not_zip" );
            log.warn( "App is not a zip archive" );
            return FAILURE;
        }
        
        ZipFile zip = new ZipFile( file );
        ZipEntry entry = zip.getEntry( "manifest.webapp" );

        if ( entry == null)
        {
            zip.close();
            message = i18n.getString( "appmanager_manifest_not_found" );
            log.warn( "Manifest file could not be found in app" );
            return FAILURE;
        }
        
        try
        {
            appManager.installApp( file, fileName, getRootPath() );
            
            message = i18n.getString( "appmanager_install_success" );
        }
        catch ( JsonParseException ex )
        {
            message = i18n.getString( "appmanager_invalid_json" );
            log.error( "Error parsing JSON in manifest", ex );
            return FAILURE;
        }
        finally
        {
            zip.close();
        }

        return SUCCESS;
    }
    
    private String getRootPath()
    {
        HttpServletRequest req = ServletActionContext.getRequest();
        StringBuffer fullUrl = req.getRequestURL();
        String baseUrl = ContextUtils.getBaseUrl( req );
        String rootPath = fullUrl.substring( 0, fullUrl.indexOf( "/", baseUrl.length() ) );
        
        return rootPath;
    }
}
