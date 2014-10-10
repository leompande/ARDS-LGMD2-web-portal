package org.hisp.dhis.setting;

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

import static org.hisp.dhis.setting.SystemSettingManager.DEFAULT_APPLICATION_TITLE;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_APPLICATION_FOOTER;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_APPLICATION_INTRO;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_APPLICATION_NOTIFICATION;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_APPLICATION_TITLE;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author James Chang
 */

public class DefaultTranslateSystemSettingManager
    implements TranslateSystemSettingManager
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SystemSettingManager systemSettingManager;
    
    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // Method implementation
    // -------------------------------------------------------------------------

    @Override
    public Map<String, String> getTranslation_SystemAppearanceSetting( String localeStr )
    {
        Map<String, String> translations = new Hashtable<String, String>();

         // Add the key application data (with localeCode name) into translations map object
        translations.put( KEY_APPLICATION_TITLE, getSystemSettingWithFallbacks( KEY_APPLICATION_TITLE, localeStr, DEFAULT_APPLICATION_TITLE ) );        
        translations.put( KEY_APPLICATION_INTRO, getSystemSettingWithFallbacks( KEY_APPLICATION_INTRO, localeStr, "" ) );
        translations.put( KEY_APPLICATION_NOTIFICATION, getSystemSettingWithFallbacks( KEY_APPLICATION_NOTIFICATION, localeStr, "" ) );
        translations.put( KEY_APPLICATION_FOOTER, getSystemSettingWithFallbacks( KEY_APPLICATION_FOOTER, localeStr, "" ) );
                
        return translations;
    }

    @Override
    public Map<String, String> getTranslationNoFallback_SystemAppearanceSetting( String localeStr )
    {
        Map<String, String> translations = new Hashtable<String, String>();

         // Add the key application data (with localeCode name) into translations map object
        translations.put( KEY_APPLICATION_TITLE, systemSettingManager.getSystemSetting( KEY_APPLICATION_TITLE + localeStr, DEFAULT_APPLICATION_TITLE ).toString() );        
        translations.put( KEY_APPLICATION_INTRO, systemSettingManager.getSystemSetting( KEY_APPLICATION_INTRO + localeStr, "" ).toString() );
        translations.put( KEY_APPLICATION_NOTIFICATION, systemSettingManager.getSystemSetting( KEY_APPLICATION_NOTIFICATION + localeStr, "" ).toString() );
        translations.put( KEY_APPLICATION_FOOTER, systemSettingManager.getSystemSetting( KEY_APPLICATION_FOOTER + localeStr, "" ).toString() );
                
        return translations;
    }

    // -------------------------------------------------------------------------
    // Support Method implementation
    // -------------------------------------------------------------------------
    private String getSystemSettingWithFallbacks( String keyName, String localeStr, String defaultValue )
    {
        String settingValue = "";

        String keyWithLocale = systemSettingManager.getSystemSetting( keyName + localeStr, "" ).toString();

        if ( keyWithLocale.isEmpty() )
        {
            settingValue = systemSettingManager.getSystemSetting( keyName, defaultValue ).toString();            
        }
        else
        {
            settingValue = keyWithLocale;          
        }

        return settingValue;
    }
        
}
