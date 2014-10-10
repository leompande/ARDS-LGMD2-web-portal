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

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Stian Strandli
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultSystemSettingManager
    implements SystemSettingManager
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingStore systemSettingStore;

    public void setSystemSettingStore( SystemSettingStore systemSettingStore )
    {
        this.systemSettingStore = systemSettingStore;
    }

    private List<String> flags;

    public void setFlags( List<String> flags )
    {
        this.flags = flags;
    }

    // -------------------------------------------------------------------------
    // SystemSettingManager implementation
    // -------------------------------------------------------------------------

    public void saveSystemSetting( String name, Serializable value )
    {
        SystemSetting setting = systemSettingStore.getByName( name );

        if ( setting == null )
        {
            setting = new SystemSetting();

            setting.setName( name );
            setting.setValue( value );

            systemSettingStore.save( setting );
        }
        else
        {
            setting.setValue( value );

            systemSettingStore.update( setting );
        }
    }

    public Serializable getSystemSetting( String name )
    {
        SystemSetting setting = systemSettingStore.getByName( name );

        return setting != null && setting.hasValue() ? setting.getValue() : null;
    }

    public Serializable getSystemSetting( String name, Serializable defaultValue )
    {
        SystemSetting setting = systemSettingStore.getByName( name );

        return setting != null && setting.hasValue() ? setting.getValue() : defaultValue;
    }

    public Collection<SystemSetting> getAllSystemSettings()
    {
        return systemSettingStore.getAll();
    }

    public void deleteSystemSetting( String name )
    {
        SystemSetting setting = systemSettingStore.getByName( name );

        if ( setting != null )
        {
            systemSettingStore.delete( setting );
        }
    }

    // -------------------------------------------------------------------------
    // Specific methods
    // -------------------------------------------------------------------------

    public List<String> getFlags()
    {
        Collections.sort( flags );
        return flags;
    }

    public String getFlagImage()
    {
        String flag = (String) getSystemSetting( KEY_FLAG, DEFAULT_FLAG );

        return flag != null ? flag + ".png" : null;
    }

    public String getEmailHostName()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( KEY_EMAIL_HOST_NAME ) );
    }

    public int getEmailPort()
    {
        return (Integer) getSystemSetting( KEY_EMAIL_PORT, DEFAULT_EMAIL_PORT );
    }

    public String getEmailPassword()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( KEY_EMAIL_PASSWORD ) );
    }

    public String getEmailUsername()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( KEY_EMAIL_USERNAME ) );
    }

    public boolean getEmailTls()
    {
        return (Boolean) getSystemSetting( KEY_EMAIL_TLS, true );
    }

    public boolean accountRecoveryEnabled()
    {
        return (Boolean) getSystemSetting( KEY_ACCOUNT_RECOVERY, false );
    }

    @Override
    public boolean selfRegistrationNoRecaptcha()
    {
        return (Boolean) getSystemSetting( KEY_SELF_REGISTRATION_NO_RECAPTCHA, false );
    }

    public boolean emailEnabled()
    {
        return getEmailHostName() != null;
    }

    public String googleAnalyticsUA()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( KEY_GOOGLE_ANALYTICS_UA ) );
    }

    @Override
    public Integer credentialsExpires()
    {
        return (Integer) (getSystemSetting( KEY_CREDENTIALS_EXPIRES ) == null ? 0 : getSystemSetting( KEY_CREDENTIALS_EXPIRES ));
    }
}
