package org.hisp.dhis.settings.action.system;

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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.configuration.Configuration;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.UserGroupService;

import static org.hisp.dhis.setting.SystemSettingManager.*;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class SetGeneralSettingsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String cacheStrategy;

    public void setCacheStrategy( String cacheStrategy )
    {
        this.cacheStrategy = cacheStrategy;
    }

    private Integer infrastructuralDataElements;

    public void setInfrastructuralDataElements( Integer infrastructuralDataElements )
    {
        this.infrastructuralDataElements = infrastructuralDataElements;
    }

    private String infrastructuralPeriodType;

    public void setInfrastructuralPeriodType( String infrastructuralPeriodType )
    {
        this.infrastructuralPeriodType = infrastructuralPeriodType;
    }

    private Boolean omitIndicatorsZeroNumeratorDataMart;

    public void setOmitIndicatorsZeroNumeratorDataMart( Boolean omitIndicatorsZeroNumeratorDataMart )
    {
        this.omitIndicatorsZeroNumeratorDataMart = omitIndicatorsZeroNumeratorDataMart;
    }

    private Double factorDeviation;

    public void setFactorDeviation( Double factorDeviation )
    {
        this.factorDeviation = factorDeviation;
    }

    private Integer feedbackRecipients;

    public void setFeedbackRecipients( Integer feedbackRecipients )
    {
        this.feedbackRecipients = feedbackRecipients;
    }

    private Integer offlineOrganisationUnitLevel;

    public void setOfflineOrganisationUnitLevel( Integer offlineOrganisationUnitLevel )
    {
        this.offlineOrganisationUnitLevel = offlineOrganisationUnitLevel;
    }

    private String phoneNumberAreaCode;
    
    public void setPhoneNumberAreaCode( String phoneNumberAreaCode )
    {
        this.phoneNumberAreaCode = phoneNumberAreaCode;
    }

    private String googleAnalyticsUA;

    public void setGoogleAnalyticsUA( String googleAnalyticsUA )
    {
        this.googleAnalyticsUA = googleAnalyticsUA;
    }

    private boolean multiOrganisationUnitForms;

    public void setMultiOrganisationUnitForms( boolean multiOrganisationUnitForms )
    {
        this.multiOrganisationUnitForms = multiOrganisationUnitForms;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        systemSettingManager.saveSystemSetting( KEY_CACHE_STRATEGY, cacheStrategy );
        systemSettingManager.saveSystemSetting( KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART, omitIndicatorsZeroNumeratorDataMart );
        systemSettingManager.saveSystemSetting( KEY_FACTOR_OF_DEVIATION, factorDeviation );
        systemSettingManager.saveSystemSetting( KEY_PHONE_NUMBER_AREA_CODE, phoneNumberAreaCode );
        systemSettingManager.saveSystemSetting( KEY_MULTI_ORGANISATION_UNIT_FORMS, multiOrganisationUnitForms );
        systemSettingManager.saveSystemSetting( KEY_GOOGLE_ANALYTICS_UA, googleAnalyticsUA );

        Configuration configuration = configurationService.getConfiguration();

        if ( feedbackRecipients != null )
        {
            configuration.setFeedbackRecipients( userGroupService.getUserGroup( feedbackRecipients ) );
        }

        if ( offlineOrganisationUnitLevel != null )
        {
            configuration.setOfflineOrganisationUnitLevel( organisationUnitService
                .getOrganisationUnitLevel( offlineOrganisationUnitLevel ) );

            // When level is changed, make sure that the version is updated
            organisationUnitService.updateVersion();
        }

        if ( infrastructuralDataElements != null )
        {
            configuration.setInfrastructuralDataElements( dataElementService
                .getDataElementGroup( infrastructuralDataElements ) );
        }

        if ( infrastructuralPeriodType != null )
        {
            configuration.setInfrastructuralPeriodType( periodService.getPeriodTypeByClass( PeriodType
                .getPeriodTypeByName( infrastructuralPeriodType ).getClass() ) );
        }

        configurationService.setConfiguration( configuration );

        message = i18n.getString( "settings_updated" );

        return SUCCESS;
    }
}
