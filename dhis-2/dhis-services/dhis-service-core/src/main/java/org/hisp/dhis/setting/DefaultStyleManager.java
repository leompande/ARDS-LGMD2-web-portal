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

import java.io.File;
import java.util.SortedMap;

import org.hisp.dhis.setting.StyleManager;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.UserSettingService;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DefaultStyleManager
    implements StyleManager
{
    private static final String SETTING_NAME_STYLE = "currentStyle";

    private static final String SEPARATOR = "/";

    private static final String SYSTEM_SEPARATOR = File.separator;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }
    
    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    private String defaultStyle;

    public void setDefaultStyle( String defaultStyle )
    {
        this.defaultStyle = defaultStyle;
    }

    /**
     * Map for styles. The key refers to the user setting key and the value refers
     * to the path to the CSS file of the style relative to /dhis-web-commons/.
     */
    private SortedMap<String, String> styles;

    public void setStyles( SortedMap<String, String> styles )
    {
        this.styles = styles;
    }

    // -------------------------------------------------------------------------
    // StyleManager implementation
    // -------------------------------------------------------------------------

    public void setSystemStyle( String style )
    {
         systemSettingManager.saveSystemSetting( SETTING_NAME_STYLE, style );
    }
    
    public void setUserStyle( String style )
    {
        userSettingService.saveUserSetting( SETTING_NAME_STYLE, style );
    }

    public String getCurrentStyle()
    {
        String style = (String) userSettingService.getUserSetting( SETTING_NAME_STYLE );
        
        if ( style != null )
        {
            return style;
        }
        
        return getSystemStyle();
    }
    
    public String getSystemStyle()
    {
        return (String) systemSettingManager.getSystemSetting( SETTING_NAME_STYLE, styles.get( defaultStyle ) );
    }

    public String getCurrentStyleDirectory()
    {
        String currentStyle = getCurrentStyle();

        if ( currentStyle.lastIndexOf( SEPARATOR ) != -1 )
        {
            return currentStyle.substring( 0, currentStyle.lastIndexOf( SEPARATOR ) );
        }

        if ( currentStyle.lastIndexOf( SYSTEM_SEPARATOR ) != -1 )
        {
            return currentStyle.substring( 0, currentStyle.lastIndexOf( SYSTEM_SEPARATOR ) );
        }

        return currentStyle;
    }

    public SortedMap<String, String> getStyles()
    {
        return styles;
    }
}
