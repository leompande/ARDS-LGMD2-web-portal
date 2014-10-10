package org.hisp.dhis.sms;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsListener;
import org.hisp.dhis.sms.parse.ParserType;
import org.hisp.dhis.sms.parse.SMSParserException;
import org.hisp.dhis.smscommand.SMSCode;
import org.hisp.dhis.smscommand.SMSCommand;
import org.hisp.dhis.smscommand.SMSCommandService;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.transaction.annotation.Transactional;

public class J2MEDataValueSMSListener
    implements IncomingSmsListener
{
    private DataValueService dataValueService;

    private DataElementCategoryService dataElementCategoryService;

    private SMSCommandService smsCommandService;

    private UserService userService;

    private CompleteDataSetRegistrationService registrationService;

    private SmsSender smsSender;
    
    @Transactional
    @Override
    public boolean accept( IncomingSms sms )
    {
        String message = sms.getText();
        String commandString = null;
        if ( message.indexOf( " " ) > 0 )
        {
            commandString = message.substring( 0, message.indexOf( " " ) );
            message = message.substring( commandString.length() );
        }
        else
        {
            commandString = message;
        }

        return smsCommandService.getSMSCommand( commandString, ParserType.J2ME_PARSER ) != null;
    }
    
    @Transactional
    @Override
    public void receive( IncomingSms sms )
    {
        String message = sms.getText();
        String commandString = null;
        if ( message.indexOf( " " ) > 0 )
        {
            commandString = message.substring( 0, message.indexOf( " " ) );
            message = message.substring( commandString.length() );
        }
        else
        {
            commandString = message;
        }

        SMSCommand smsCommand = smsCommandService.getSMSCommand( commandString, ParserType.J2ME_PARSER );
        String token[] = message.split( "!" );
        Map<String, String> parsedMessage = this.parse( token[1], smsCommand );
        String senderPhoneNumber = StringUtils.replace( sms.getOriginator(), "+", "" );
        Collection<OrganisationUnit> orgUnits = getOrganisationUnitsByPhoneNumber( senderPhoneNumber );

        if ( orgUnits == null || orgUnits.size() == 0 )
        {
            throw new SMSParserException( "No user associated with this phone number. Please contact your supervisor." );
        }

        OrganisationUnit orgUnit = this.selectOrganisationUnit( orgUnits, parsedMessage );
        Period period = this.getPeriod( token[0].trim(), smsCommand.getDataset().getPeriodType() );
        boolean valueStored = false;

        for ( SMSCode code : smsCommand.getCodes() )
        {
            if ( parsedMessage.containsKey( code.getCode().toUpperCase() ) )
            {
                storeDataValue( senderPhoneNumber, orgUnit, parsedMessage, code, smsCommand, period,
                    smsCommand.getDataset() );
                valueStored = true;
            }
        }

        if ( parsedMessage.isEmpty() || !valueStored )
        {
            if ( StringUtils.isEmpty( smsCommand.getDefaultMessage() ) )
            {
                throw new SMSParserException( "No values reported for command '" + smsCommand.getName() + "'" );
            }
            else
            {
                throw new SMSParserException( smsCommand.getDefaultMessage() );
            }
        }

        this.registerCompleteDataSet( smsCommand.getDataset(), period, orgUnit, "mobile" );

        this.sendSuccessFeedback( senderPhoneNumber, smsCommand, parsedMessage, period, orgUnit );
    }

    private Map<String, String> parse( String sms, SMSCommand smsCommand )
    {
        String[] keyValuePairs = null;

        if ( sms.indexOf( "#" ) > -1 )
        {
            keyValuePairs = sms.split( "#" );
        }
        else
        {
            keyValuePairs = new String[1];
            keyValuePairs[0] = sms;
        }

        Map<String, String> keyValueMap = new HashMap<String, String>();
        for ( String keyValuePair : keyValuePairs )
        {
            String[] token = keyValuePair.split( Pattern.quote( smsCommand.getSeparator() ) );
            keyValueMap.put( token[0], token[1] );
        }

        return keyValueMap;
    }

    private void storeDataValue( String sender, OrganisationUnit orgUnit, Map<String, String> parsedMessage,
        SMSCode code, SMSCommand command, Period period, DataSet dataset )
    {
        String upperCaseCode = code.getCode().toUpperCase();

        String storedBy = getUser( sender ).getUsername();

        if ( StringUtils.isBlank( storedBy ) )
        {
            storedBy = "[unknown] from [" + sender + "]";
        }

        DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( code
            .getOptionId() );

        DataValue dv = dataValueService.getDataValue( code.getDataElement(), period, orgUnit, optionCombo );

        String value = parsedMessage.get( upperCaseCode );
        if ( !StringUtils.isEmpty( value ) )
        {
            boolean newDataValue = false;
            if ( dv == null )
            {
                dv = new DataValue();
                dv.setCategoryOptionCombo( optionCombo );
                dv.setSource( orgUnit );
                dv.setDataElement( code.getDataElement() );
                dv.setPeriod( period );
                dv.setComment( "" );
                newDataValue = true;
            }

            if ( StringUtils.equals( dv.getDataElement().getType(), DataElement.VALUE_TYPE_BOOL ) )
            {
                if ( "Y".equals( value.toUpperCase() ) || "YES".equals( value.toUpperCase() ) )
                {
                    value = "true";
                }
                else if ( "N".equals( value.toUpperCase() ) || "NO".equals( value.toUpperCase() ) )
                {
                    value = "false";
                }
            }

            dv.setValue( value );
            dv.setTimestamp( new java.util.Date() );
            dv.setStoredBy( storedBy );

            if ( ValidationUtils.dataValueIsValid( value, dv.getDataElement() ) != null )
            {
                return; // not a valid value for data element
            }

            if ( newDataValue )
            {
                dataValueService.addDataValue( dv );
            }
            else
            {
                dataValueService.updateDataValue( dv );
            }
        }

    }

    private OrganisationUnit selectOrganisationUnit( Collection<OrganisationUnit> orgUnits,
        Map<String, String> parsedMessage )
    {
        OrganisationUnit orgUnit = null;

        for ( OrganisationUnit o : orgUnits )
        {
            if ( orgUnits.size() == 1 )
            {
                orgUnit = o;
            }
            if ( parsedMessage.containsKey( "ORG" ) && o.getCode().equals( parsedMessage.get( "ORG" ) ) )
            {
                orgUnit = o;
                break;
            }
        }

        if ( orgUnit == null && orgUnits.size() > 1 )
        {
            String messageListingOrgUnits = "Found more than one org unit for this number. Please specify one of the following:";
            for ( Iterator<OrganisationUnit> i = orgUnits.iterator(); i.hasNext(); )
            {
                OrganisationUnit o = i.next();
                messageListingOrgUnits += " " + o.getName() + ":" + o.getCode();
                if ( i.hasNext() )
                {
                    messageListingOrgUnits += ",";
                }
            }
            throw new SMSParserException( messageListingOrgUnits );
        }
        
        return orgUnit;
    }

    private Collection<OrganisationUnit> getOrganisationUnitsByPhoneNumber( String sender )
    {
        Collection<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();
        Collection<User> users = userService.getUsersByPhoneNumber( sender );
        for ( User u : users )
        {
            if ( u.getOrganisationUnits() != null )
            {
                orgUnits.addAll( u.getOrganisationUnits() );
            }
        }

        return orgUnits;
    }

    private User getUser( String sender )
    {
        OrganisationUnit orgunit = null;
        User user = null;
        for ( User u : userService.getUsersByPhoneNumber( sender ) )
        {
            OrganisationUnit ou = u.getOrganisationUnit();

            // Might be undefined if the user has more than one org.units
            // "attached"
            if ( orgunit == null )
            {
                orgunit = ou;
            }
            else if ( orgunit.getId() == ou.getId() )
            {
                // same orgunit, no problem...
            }
            else
            {
                throw new SMSParserException(
                    "User is associated with more than one orgunit. Please contact your supervisor." );
            }
            user = u;
        }
        return user;
    }

    private void registerCompleteDataSet( DataSet dataSet, Period period, OrganisationUnit organisationUnit,
        String storedBy )
    {
        CompleteDataSetRegistration registration = new CompleteDataSetRegistration();

        if ( registrationService.getCompleteDataSetRegistration( dataSet, period, organisationUnit ) == null )
        {
            registration.setDataSet( dataSet );
            registration.setPeriod( period );
            registration.setSource( organisationUnit );
            registration.setDate( new Date() );
            registration.setStoredBy( storedBy );
            registration.setPeriodName( registration.getPeriod().toString() );
            registrationService.saveCompleteDataSetRegistration( registration, false );
        }
    }

    private void sendSuccessFeedback( String sender, SMSCommand command, Map<String, String> parsedMessage,
        Period period, OrganisationUnit orgunit )
    {
        String reportBack = "Thank you! Values entered: ";
        String notInReport = "Missing values for: ";
        boolean missingElements = false;

        for ( SMSCode code : command.getCodes() )
        {

            DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                .getDataElementCategoryOptionCombo( code.getOptionId() );

            DataValue dv = dataValueService.getDataValue( code.getDataElement(), period, orgunit, optionCombo );

            if ( dv == null && !StringUtils.isEmpty( code.getCode() ) )
            {
                notInReport += code.getCode() + ",";
                missingElements = true;
            }
            else if ( dv != null )
            {
                String value = dv.getValue();
                if ( StringUtils.equals( dv.getDataElement().getType(), DataElement.VALUE_TYPE_BOOL ) )
                {
                    if ( "true".equals( value ) )
                    {
                        value = "Yes";
                    }
                    else if ( "false".equals( value ) )
                    {
                        value = "No";
                    }
                }
                reportBack += code.getCode() + "=" + value + " ";
            }
        }

        notInReport = notInReport.substring( 0, notInReport.length() - 1 );

        if ( missingElements )
        {
            reportBack += notInReport;
        }

        smsSender.sendMessage( reportBack, sender );
    }

    public Period getPeriod( String periodName, PeriodType periodType )
        throws IllegalArgumentException
    {
        if ( periodType instanceof DailyPeriodType )
        {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat( pattern );
            Date date;
            try
            {
                date = formatter.parse( periodName );
            }
            catch ( ParseException e )
            {
                throw new IllegalArgumentException( "Couldn't make a period of type " + periodType.getName()
                    + " and name " + periodName, e );
            }
            return periodType.createPeriod( date );

        }

        if ( periodType instanceof WeeklyPeriodType )
        {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat( pattern );
            Date date;
            try
            {
                date = formatter.parse( periodName );
            }
            catch ( ParseException e )
            {
                throw new IllegalArgumentException( "Couldn't make a period of type " + periodType.getName()
                    + " and name " + periodName, e );
            }
            return periodType.createPeriod( date );
        }

        if ( periodType instanceof MonthlyPeriodType )
        {
            int dashIndex = periodName.indexOf( '-' );

            if ( dashIndex < 0 )
            {
                return null;
            }

            int month = Integer.parseInt( periodName.substring( 0, dashIndex ) );
            int year = Integer.parseInt( periodName.substring( dashIndex + 1, periodName.length() ) );

            Calendar cal = Calendar.getInstance();
            cal.set( Calendar.YEAR, year );
            cal.set( Calendar.MONTH, month );

            return periodType.createPeriod( cal.getTime() );
        }

        if ( periodType instanceof YearlyPeriodType )
        {
            Calendar cal = Calendar.getInstance();
            cal.set( Calendar.YEAR, Integer.parseInt( periodName ) );

            return periodType.createPeriod( cal.getTime() );
        }

        if ( periodType instanceof QuarterlyPeriodType )
        {
            Calendar cal = Calendar.getInstance();

            int month = 0;
            if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Jan" ) )
            {
                month = 1;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Apr" ) )
            {
                month = 4;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Jul" ) )
            {
                month = 6;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Oct" ) )
            {
                month = 10;
            }

            int year = Integer.parseInt( periodName.substring( periodName.lastIndexOf( " " ) + 1 ) );

            cal.set( Calendar.MONTH, month );
            cal.set( Calendar.YEAR, year );

            if ( month != 0 )
            {
                return periodType.createPeriod( cal.getTime() );
            }

        }

        throw new IllegalArgumentException( "Couldn't make a period of type " + periodType.getName() + " and name "
            + periodName );
    }

    public DataValueService getDataValueService()
    {
        return dataValueService;
    }

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    public SMSCommandService getSmsCommandService()
    {
        return smsCommandService;
    }

    public void setSmsCommandService( SMSCommandService smsCommandService )
    {
        this.smsCommandService = smsCommandService;
    }

    public UserService getUserService()
    {
        return userService;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    public CompleteDataSetRegistrationService getRegistrationService()
    {
        return registrationService;
    }

    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

    public DataElementCategoryService getDataElementCategoryService()
    {
        return dataElementCategoryService;
    }

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    public SmsSender getSmsSender()
    {
        return smsSender;
    }

    public void setSmsSender( SmsSender smsSender )
    {
        this.smsSender = smsSender;
    }
}
