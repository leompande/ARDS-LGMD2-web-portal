package org.hisp.dhis.interceptor;

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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.setting.SystemSettingManager;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import static org.hisp.dhis.appmanager.AppManager.KEY_APP_BASE_URL;
import static org.hisp.dhis.setting.SystemSettingManager.*;

/**
 * @author Lars Helge Overland
 */
public class SystemSettingInterceptor
    implements Interceptor
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    // -------------------------------------------------------------------------
    // AroundInterceptor implementation
    // -------------------------------------------------------------------------

    public void destroy()
    {
    }

    public void init()
    {
    }

    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put( KEY_CACHE_STRATEGY, systemSettingManager.getSystemSetting( KEY_CACHE_STRATEGY, DEFAULT_CACHE_STRATEGY ) );
        map.put( KEY_APPLICATION_TITLE, systemSettingManager.getSystemSetting( KEY_APPLICATION_TITLE, DEFAULT_APPLICATION_TITLE ) );
        map.put( KEY_APPLICATION_INTRO, systemSettingManager.getSystemSetting( KEY_APPLICATION_INTRO ) );
        map.put( KEY_APPLICATION_NOTIFICATION, systemSettingManager.getSystemSetting( KEY_APPLICATION_NOTIFICATION ) );
        map.put( KEY_APPLICATION_FOOTER, systemSettingManager.getSystemSetting( KEY_APPLICATION_FOOTER ) );
        map.put( KEY_FLAG, systemSettingManager.getSystemSetting( KEY_FLAG, DEFAULT_FLAG ) );
        map.put( KEY_FLAG_IMAGE, systemSettingManager.getFlagImage() );
        map.put( KEY_START_MODULE, systemSettingManager.getSystemSetting( KEY_START_MODULE, DEFAULT_START_MODULE ) );
        map.put( KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART, systemSettingManager.getSystemSetting( KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART, false ) );
        map.put( KEY_FACTOR_OF_DEVIATION, systemSettingManager.getSystemSetting( KEY_FACTOR_OF_DEVIATION, DEFAULT_FACTOR_OF_DEVIATION ) );
        map.put( KEY_PHONE_NUMBER_AREA_CODE, systemSettingManager.getSystemSetting( KEY_PHONE_NUMBER_AREA_CODE, "" ) );
        map.put( KEY_MULTI_ORGANISATION_UNIT_FORMS, systemSettingManager.getSystemSetting( KEY_MULTI_ORGANISATION_UNIT_FORMS, false ) );
        map.put( KEY_ACCOUNT_RECOVERY, systemSettingManager.getSystemSetting( KEY_ACCOUNT_RECOVERY, false ) );
        map.put( KEY_CONFIGURATION, configurationService.getConfiguration() );
        map.put( KEY_APP_BASE_URL, systemSettingManager.getSystemSetting( KEY_APP_BASE_URL ) );
        map.put( KEY_GOOGLE_ANALYTICS_UA, systemSettingManager.getSystemSetting( KEY_GOOGLE_ANALYTICS_UA, "" ) );
        map.put( KEY_CREDENTIALS_EXPIRES, systemSettingManager.credentialsExpires() );
        map.put( KEY_SELF_REGISTRATION_NO_RECAPTCHA, systemSettingManager.selfRegistrationNoRecaptcha() );

        map.put( SYSPROP_PORTAL, defaultIfEmpty( System.getProperty( SYSPROP_PORTAL ), String.valueOf( false ) ) );

        invocation.getStack().push( map );

        return invocation.invoke();
    }
}
