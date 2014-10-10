package org.hisp.dhis.mobile.sms;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.mobile.sms.api.SmsFormat;
import org.hisp.dhis.mobile.sms.api.SmsImportService;
import org.hisp.dhis.mobile.sms.api.SmsInbound;
import org.hisp.dhis.mobile.sms.api.SmsInboundStoreService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;
import org.smslib.helper.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation class of the SmsImportService This class provides
 * implementation of the methods required to import SmsInbound into the
 * datavalues using the datavalueservice of core DHIS2
 * 
 * @author Saptarshi
 */
public class DefaultSmsImportService
    implements SmsImportService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private SmsInboundStoreService smsInboundStoreService;

    public void setSmsInboundStoreService( SmsInboundStoreService smsInboundStoreService )
    {
        this.smsInboundStoreService = smsInboundStoreService;
    }

    // -------------------------------------------------------------------------
    // Helper Methods
    // -------------------------------------------------------------------------
    
    public User getUserInfo( String mobileNumber )
    {
        Collection<User> userList = userStore.getUsersByPhoneNumber( mobileNumber );
        User selectedUser = null;
        if ( userList != null && userList.size() > 0 )
        {
            selectedUser = userList.iterator().next();
        }
        return selectedUser;
    }

    public Period getPeriodInfo( String startDate, String periodTypeId )
        throws Exception
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        List<Period> periods = null;
        PeriodType pt = null;
        if ( periodTypeId.equals( "3" ) )
        {
            pt = new MonthlyPeriodType();
            periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
        }
        else
        {
            if ( periodTypeId.equals( "1" ) )
            {
                pt = new DailyPeriodType();
                periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
            }
            else
            {
                if ( periodTypeId.equals( "6" ) )
                {
                    pt = new YearlyPeriodType();
                    periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
                }
                else
                {
                    if ( periodTypeId.equals( "2" ) )
                    {
                        pt = new WeeklyPeriodType();
                        periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
                    }
                }
            }
        }

        for ( Period period : periods )
        {
            String tempDate = dateFormat.format( period.getStartDate() );
            if ( tempDate.equalsIgnoreCase( startDate ) )
            {
                return period;
            }
        }

        Period period = pt.createPeriod( dateFormat.parse( startDate ) );
        period = reloadPeriodForceAdd( period );
        periodService.addPeriod( period );

        return period;
    }

    private Period reloadPeriod( Period period )
    {
        return periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    private Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodService.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------
    
    @Override
    @Transactional
    public int saveDataValues()
    {
        int importedMessages = 0;

        try
        {
            File deIdFile = new File( System.getenv( "DHIS2_HOME" ) + File.separator + "formIDLayout.csv" );
            if ( deIdFile.exists() )
            {
                FileInputStream f = new FileInputStream( deIdFile );
                Properties props = new Properties();
                props.load( f );
                f.close();

                Collection<SmsInbound> msgs = smsInboundStoreService.getAllReceivedSms();
                for ( SmsInbound sms : msgs )
                {
                    SmsFormat dataSms = new SmsFormat( sms );
                    String storedBy = "";
                    User curUser = getUserInfo( sms.getOriginator() );
                    if ( curUser != null )
                    {
                        UserCredentials userCredentials = userStore.getUserCredentials( curUser );
                        if ( userCredentials != null )
                        {
                            storedBy = userCredentials.getUsername();
                        }
                        else
                        {
                            Logger.getInstance().logError( "User with phone number not found : " + sms.getOriginator(),
                                null, null );
                            return -1;
                        }
                        List<OrganisationUnit> units = new ArrayList<OrganisationUnit>( curUser.getOrganisationUnits() );
                        if ( units == null || units.size() <= 0 )
                        {
                            Logger.getInstance().logError(
                                " User with phone number not assigned any organization unit : " + sms.getOriginator(),
                                null, null );
                            return -1;
                        }
                        OrganisationUnit unit = units.get( 0 );
                        Period period = getPeriodInfo( dataSms.getPeriodText(), dataSms.getPeriodTypeId() );

                        String[] deIds = props.getProperty( dataSms.getFormId() ).split( "\\," );
                        String[] dataValues = dataSms.getDataValues();
                        if ( dataValues.length == deIds.length )
                        {
                            int saveCount = 0;
                            for ( int i = 0; i < dataValues.length; i++ )
                            {
                                String parts[] = deIds[i].split( "\\." );

                                String deStr = parts[0];

                                String optStr = parts[1];

                                DataElement dataElement = dataElementService.getDataElement( Integer.valueOf( deStr ) );

                                DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();

                                optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer
                                    .valueOf( optStr ) );

                                DataValue dataValue = dataValueService.getDataValue( unit, dataElement, period,
                                    optionCombo );

                                if ( dataValue == null )
                                {
                                    if ( dataValues[i] != null )
                                    {
                                        dataValue = new DataValue( dataElement, period, unit, optionCombo );
                                        dataValueService.addDataValue( dataValue );
                                        saveCount++;
                                    }
                                }
                                else
                                {
                                    dataValue.setValue( dataValues[i] );
                                    dataValue.setStoredBy( storedBy );
                                    dataValueService.updateDataValue( dataValue );
                                    saveCount++;
                                }
                            }
                        }
                        else
                        {
                            Logger.getInstance().logError(
                                "Incorrect formatted IdLayout file for : DV = " + dataValues.length + " DE = "
                                    + deIds.length, null, null );
                            return -1;
                        }

                    }
                    else
                    {
                        Logger.getInstance().logError( "Unrecognised Phone Numbers : " + sms.getOriginator(), null,
                            null );
                        return -1;
                    }
                }
            }
            else
            {
                Logger.getInstance().logError( "Error finding dataelement ids file: ", null, null );
                return -1;
            }
        }
        catch ( Exception ex )
        {
            Logger.getInstance().logError( "Error getting Period!", ex, null );
            return -1;
        }
        return importedMessages;
    }
}
