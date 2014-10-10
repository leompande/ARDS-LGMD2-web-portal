package org.hisp.dhis.reportsheet.impl;

/*
 * Copyright (c) 2004-2011, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import java.io.File;
import java.util.List;

import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.external.location.LocationManagerException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reportsheet.ExcelContentTypeMap;
import org.hisp.dhis.reportsheet.ReportLocationManager;
import org.hisp.dhis.setting.SystemSettingManager;

/**
 * @author Tran Thanh Tri
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class DefaultReportLocationManager
    implements ReportLocationManager
{
    private File REPORT;

    private File REPORT_TEMP;

    private File REPORT_TEMPLATE;

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // Initialize
    // -------------------------------------------------------------------------

    void init()
    {
        try
        {
            REPORT = new File( locationManager.getExternalDirectory(), REPORT_DIR );
            REPORT.mkdir();
            REPORT_TEMP = new File( REPORT, REPORT_TEMP_DIR );
            REPORT_TEMP.mkdir();
        }
        catch ( LocationManagerException e )
        {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Implemented
    // -------------------------------------------------------------------------

    public List<File> getListFileInOrganisationDirectory( OrganisationUnit arg0 )
    {
        return null;
    }

    public File getOrganisationDirectory( OrganisationUnit organisationUnit )
    {
        File dir = new File( REPORT, String.valueOf( organisationUnit.getId() ) );

        if ( !dir.exists() )
        {
            dir.mkdir();
        }

        return dir;
    }

    public File getExportReportDirectory()
    {
        return this.REPORT;
    }

    public File getExportReportTemporaryDirectory()
    {
        return this.REPORT_TEMP;
    }

    public File getExportReportTemplateDirectory()
    {
        String pathConfig = (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_REPORT_TEMPLATE_DIRECTORY );

        if ( pathConfig == null )
        {
            return null;
        }

        REPORT_TEMPLATE = new File( REPORT, pathConfig );

        REPORT_TEMPLATE.mkdirs();

        return REPORT_TEMPLATE;
    }

    public boolean isFileTypeSupported( String extension, String contentType )
    {
        List<String> types = ExcelContentTypeMap.getContentTypeByKey( extension );

        return (types == null ? false : types.contains( contentType ));
    }
}
