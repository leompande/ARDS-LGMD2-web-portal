package org.hisp.dhis.mobile.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import org.apache.commons.io.FileUtils;
import org.hisp.dhis.mobile.SmsService;
import org.hisp.dhis.mobile.api.MobileImportService;

import com.opensymphony.xwork2.Action;

public class ReceiveImportPageAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    SmsService smsService;

    public void setSmsService( SmsService smsService )
    {
        this.smsService = smsService;
    }

    private MobileImportService mobileImportService;

    public void setMobileImportService( MobileImportService mobileImportService )
    {
        this.mobileImportService = mobileImportService;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    String result = "";

    public String getResult()
    {
        return result;
    }

    boolean smsServiceStatus;

    public boolean getSmsServiceStatus()
    {
        smsServiceStatus = smsService.getServiceStatus();
        return smsServiceStatus;
    }

    String statAction;

    public void setStatAction( String statAction )
    {
        if ( statAction.equalsIgnoreCase( "Start" ) )
        {
            this.result = smsService.startService();
        } else
        {
            this.result = smsService.stopService();
        }
    }

    String importAction;

    public void setImportAction( String importAction )
    {
        startImportingMessages();
    }

    private List<File> pending;

    public List<File> getPending()
    {
        File pendingFolder = new File( System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "pending" );
//        FilenameFilter filter = new XMLFilter();

        pending = (List<File>) FileUtils.listFiles( pendingFolder, new String[]
            {
                "xml"
            }, false );
        return pending;
    }

    private List<File> bounced;

    public List<File> getBounced()
    {
        File bouncedFolder = new File( System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "bounced" );
        bounced = (List<File>) FileUtils.listFiles( bouncedFolder, new String[]
            {
                "xml"
            }, false );
        return bounced;
    }

    private List<File> completed;

    public List<File> getCompleted()
    {
        File completedFolder = new File( System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "completed" );
        completed = (List<File>) FileUtils.listFiles( completedFolder, new String[]
            {
                "xml"
            }, false );
        return completed;
    }

    public void startImportingMessages()
    {
        //mobileImportService.importAllFiles();
        mobileImportService.importPendingFiles();
    }

    @Override
    public String execute()
        throws Exception
    {
        return SUCCESS;
    }
}
