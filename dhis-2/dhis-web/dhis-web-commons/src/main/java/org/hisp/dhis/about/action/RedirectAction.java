package org.hisp.dhis.about.action;

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

import static org.hisp.dhis.setting.SystemSettingManager.KEY_START_MODULE;

import org.hisp.dhis.setting.SystemSettingManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class RedirectAction
    implements Action
{
    @Autowired
    private SystemSettingManager systemSettingManager;

    private String redirectUrl;
    
    public String getRedirectUrl()
    {
        return redirectUrl;
    }

    @Override
    public String execute()
        throws Exception
    {
        String startModule = (String) systemSettingManager.getSystemSetting( KEY_START_MODULE );

        if ( startModule != null )
        {
            if(startModule.equals("home")){
                redirectUrl = "../dhis-web-dashboard-integration/home.action";
            }else if(startModule.equals("dashboard")){
                redirectUrl = "../dhis-web-dashboard-integration/index.action";
            }else if(startModule.equals("reports")){
                redirectUrl = "../dhis-web-reporting/index.action";
            }else if(startModule.equals("dataanalysis")){
                redirectUrl = "../dhis-web-dashboard-integration/analysis.action";
            }else if(startModule.equals("news")){
                redirectUrl = "../dhis-web-dashboard-integration/news.action";
            }else{
                redirectUrl = "../dhis-web-dashboard-integration/home.action";
            }

        }
        else
        {
            redirectUrl = "../dhis-web-dashboard-integration/home.action";//was index.action
        }

        return SUCCESS;
    }  
}
