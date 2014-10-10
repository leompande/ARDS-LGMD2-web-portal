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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsListener;
import org.hisp.dhis.sms.incoming.IncomingSmsService;
import org.hisp.dhis.sms.incoming.SmsMessageStatus;
import org.hisp.dhis.sms.parse.ParserType;
import org.hisp.dhis.sms.parse.SMSParserException;
import org.hisp.dhis.smscommand.SMSCode;
import org.hisp.dhis.smscommand.SMSCommand;
import org.hisp.dhis.smscommand.SMSCommandService;
import org.hisp.dhis.smscommand.SMSSpecialCharacter;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.transaction.annotation.Transactional;

public class DataValueSMSListener
    implements IncomingSmsListener
{
    private static final String defaultPattern = "([a-zA-Z]+)\\s*(\\d+)";

    private CompleteDataSetRegistrationService registrationService;

    private DataValueService dataValueService;

    private SmsSender smsSender;

    private DataElementCategoryService dataElementCategoryService;

    private SMSCommandService smsCommandService;

    private UserService userService;

    private DataSetService dataSetService;

    private IncomingSmsService incomingSmsService;

    @Transactional
    @Override
    public boolean accept( IncomingSms sms )
    {
        String message = sms.getText();
        String commandString = null;

        for ( int i = 0; i < message.length(); i++ )
        {
            String c = String.valueOf( message.charAt( i ) );
            if ( c.matches( "\\W" ) )
            {
                commandString = message.substring( 0, i );
                message = message.substring( commandString.length() + 1 );
                break;
            }
        }

        return smsCommandService.getSMSCommand( commandString, ParserType.KEY_VALUE_PARSER ) != null;
    }

    @Transactional
    @Override
    public void receive( IncomingSms sms )
    {
        String message = sms.getText();
        String commandString = null;

        for ( int i = 0; i < message.length(); i++ )
        {
            String c = String.valueOf( message.charAt( i ) );
            if ( c.matches( "\\W" ) )
            {
                commandString = message.substring( 0, i );
                message = message.substring( commandString.length() + 1 );
                break;
            }
        }

        SMSCommand smsCommand = smsCommandService.getSMSCommand( commandString, ParserType.KEY_VALUE_PARSER );
        Map<String, String> parsedMessage = this.parse( message, smsCommand );

        Date date = lookForDate( message );
        String senderPhoneNumber = StringUtils.replace( sms.getOriginator(), "+", "" );
        Collection<OrganisationUnit> orgUnits = getOrganisationUnitsByPhoneNumber( senderPhoneNumber );

        if ( orgUnits == null || orgUnits.size() == 0 )
        {
            throw new SMSParserException( "No user associated with this phone number. Please contact your supervisor." );
        }

        OrganisationUnit orgUnit = this.selectOrganisationUnit( orgUnits, parsedMessage );
        Period period = getPeriod( smsCommand, date );

        if ( dataSetService.isLocked( smsCommand.getDataset(), period, orgUnit, null, null ) )
        {
            throw new SMSParserException( "Dataset is locked for the period " + period.getStartDate() + " - "
                + period.getEndDate() );
        }

        boolean valueStored = false;

        for ( SMSCode code : smsCommand.getCodes() )
        {
            if ( parsedMessage.containsKey( code.getCode().toUpperCase() ) )
            {
                // potential bugs [noted by Lai]
                valueStored = storeDataValue( senderPhoneNumber, orgUnit, parsedMessage, code, smsCommand, date,
                    smsCommand.getDataset() );
            }
        }

        if ( parsedMessage.isEmpty() )
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
        else if ( !valueStored )
        {
            throw new SMSParserException( "Wrong format for command '" + smsCommand.getName() + "'" );
        }

        markCompleteDataSet( senderPhoneNumber, orgUnit, parsedMessage, smsCommand, date );
        sendSuccessFeedback( senderPhoneNumber, smsCommand, parsedMessage, date, orgUnit );

        sms.setParsed( true );
        sms.setStatus( SmsMessageStatus.PROCESSED );
        incomingSmsService.update( sms );
    }

    private Map<String, String> parse( String sms, SMSCommand smsCommand )
    {
        HashMap<String, String> output = new HashMap<String, String>();
        Pattern pattern = Pattern.compile( defaultPattern );
        if ( !StringUtils.isBlank( smsCommand.getSeparator() ) )
        {
            String x = "(\\w+)\\s*\\" + smsCommand.getSeparator().trim() + "\\s*([\\w ]+)\\s*(\\"
                + smsCommand.getSeparator().trim() + "|$)*\\s*";
            pattern = Pattern.compile( x );
        }
        Matcher m = pattern.matcher( sms );
        while ( m.find() )
        {
            String key = m.group( 1 );
            String value = m.group( 2 );

            if ( !StringUtils.isEmpty( key ) && !StringUtils.isEmpty( value ) )
            {
                output.put( key.toUpperCase(), value );
            }
        }

        return output;
    }

    private Date lookForDate( String message )
    {
        if ( !message.contains( " " ) )
        {
            return null;
        }

        Date date = null;
        String dateString = message.trim().split( " " )[0];
        SimpleDateFormat format = new SimpleDateFormat( "ddMM" );

        try
        {
            Calendar cal = Calendar.getInstance();
            date = format.parse( dateString );
            cal.setTime( date );
            int year = Calendar.getInstance().get( Calendar.YEAR );
            int month = Calendar.getInstance().get( Calendar.MONTH );
            if ( cal.get( Calendar.MONTH ) < month )
            {
                cal.set( Calendar.YEAR, year );
            }
            else
            {
                cal.set( Calendar.YEAR, year - 1 );
            }
            date = cal.getTime();
        }
        catch ( Exception e )
        {
            // no date found
        }

        return date;
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

    private Period getPeriod( SMSCommand command, Date date )
    {
        Period period;
        period = command.getDataset().getPeriodType().createPeriod();
        CalendarPeriodType cpt = (CalendarPeriodType) period.getPeriodType();

        if ( command.isCurrentPeriodUsedForReporting() )
        {
            period = cpt.createPeriod( new Date() );
        }
        else
        {
            period = cpt.getPreviousPeriod( period );
        }

        if ( date != null )
        {
            period = cpt.createPeriod( date );
        }

        return period;
    }

    private boolean storeDataValue( String sender, OrganisationUnit orgunit, Map<String, String> parsedMessage,
        SMSCode code, SMSCommand command, Date date, DataSet dataSet )
    {
        String upperCaseCode = code.getCode().toUpperCase();

        String storedBy = getUser( sender ).getUsername();

        if ( StringUtils.isBlank( storedBy ) )
        {
            storedBy = "[unknown] from [" + sender + "]";
        }

        DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( code
            .getOptionId() );

        Period period = getPeriod( command, date );

        DataValue dv = dataValueService.getDataValue( code.getDataElement(), period, orgunit, optionCombo );

        String value = parsedMessage.get( upperCaseCode );

        Set<SMSSpecialCharacter> specialCharacters = command.getSpecialCharacters();

        // Check for special character cases
        for ( SMSSpecialCharacter each : specialCharacters )
        {
            if ( each.getName().equalsIgnoreCase( value ) )
            {
                value = each.getValue();
                break;
            }
        }

        if ( !StringUtils.isEmpty( value ) )
        {
            boolean newDataValue = false;
            if ( dv == null )
            {
                dv = new DataValue();
                dv.setCategoryOptionCombo( optionCombo );
                dv.setSource( orgunit );
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
            else if ( StringUtils.equals( dv.getDataElement().getType(), DataElement.VALUE_TYPE_INT ) )
            {
                try
                {
                    Integer.parseInt( value );
                }
                catch ( NumberFormatException e )
                {
                    return false;
                }
            }

            dv.setValue( value );
            dv.setTimestamp( new java.util.Date() );
            dv.setStoredBy( storedBy );

            if ( newDataValue )
            {
                dataValueService.addDataValue( dv );
            }
            else
            {
                dataValueService.updateDataValue( dv );
            }
        }

        return true;
    }

    private User getUser( String sender )
    {
        OrganisationUnit orgunit = null;
        User user = null;

        // -------------------------> Need to be edit
        for ( User u : userService.getUsersByPhoneNumber( sender ) )
        {
            OrganisationUnit ou = u.getOrganisationUnit();

            if ( ou != null )
            {
                // Might be undefined if the user has more than one org units
                if ( orgunit == null )
                {
                    orgunit = ou;
                }
                else if ( orgunit.getId() == ou.getId() )
                {
                    // Same org unit
                }
                else
                {
                    throw new SMSParserException(
                        "User is associated with more than one orgunit. Please contact your supervisor." );
                }
            }
            user = u;
        }
        // <-------------------------------------
        if ( user == null )
        {
            throw new SMSParserException( "User is not associated with any orgunit. Please contact your supervisor." );
        }

        return user;
    }

    private void markCompleteDataSet( String sender, OrganisationUnit orgunit, Map<String, String> parsedMessage,
        SMSCommand command, Date date )
    {
        Period period = null;

        for ( SMSCode code : command.getCodes() )
        {

            DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                .getDataElementCategoryOptionCombo( code.getOptionId() );

            period = getPeriod( command, date );

            DataValue dv = dataValueService.getDataValue( code.getDataElement(), period, orgunit, optionCombo );

            if ( dv == null && !StringUtils.isEmpty( code.getCode() ) )
            {
                return; // not marked as complete
            }
        }

        String storedBy = getUser( sender ).getUsername();

        if ( StringUtils.isBlank( storedBy ) )
        {
            storedBy = "[unknown] from [" + sender + "]";
        }

        // If new values are submitted re-register as complete
        deregisterCompleteDataSet( command.getDataset(), period, orgunit );
        registerCompleteDataSet( command.getDataset(), period, orgunit, storedBy );
    }

    protected void sendSuccessFeedback( String sender, SMSCommand command, Map<String, String> parsedMessage,
        Date date, OrganisationUnit orgunit )
    {
        String reportBack = "Thank you! Values entered: ";
        String notInReport = "Missing values for: ";

        Period period = null;

        Map<String, DataValue> codesWithDataValues = new TreeMap<String, DataValue>();
        List<String> codesWithoutDataValues = new ArrayList<String>();

        for ( SMSCode code : command.getCodes() )
        {

            DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                .getDataElementCategoryOptionCombo( code.getOptionId() );

            period = getPeriod( command, date );

            DataValue dv = dataValueService.getDataValue( code.getDataElement(), period, orgunit, optionCombo );

            if ( dv == null && !StringUtils.isEmpty( code.getCode() ) )
            {
                codesWithoutDataValues.add( code.getCode() );
            }
            else if ( dv != null )
            {
                codesWithDataValues.put( code.getCode(), dv );
            }
        }

        for ( String key : codesWithDataValues.keySet() )
        {
            DataValue dv = codesWithDataValues.get( key );
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
            reportBack += key + "=" + value + " ";
        }

        Collections.sort( codesWithoutDataValues );

        for ( String key : codesWithoutDataValues )
        {
            notInReport += key + ",";
        }
        notInReport = notInReport.substring( 0, notInReport.length() - 1 );

        if ( codesWithoutDataValues.size() > 0 )
        {
            smsSender.sendMessage( reportBack + notInReport, sender );
        }
        else
        {
            smsSender.sendMessage( reportBack, sender );
        }
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

    private void deregisterCompleteDataSet( DataSet dataSet, Period period, OrganisationUnit organisationUnit )
    {
        CompleteDataSetRegistration registration = registrationService.getCompleteDataSetRegistration( dataSet, period,
            organisationUnit );

        if ( registration != null )
        {
            registrationService.deleteCompleteDataSetRegistration( registration );
        }
    }

    public CompleteDataSetRegistrationService getRegistrationService()
    {
        return registrationService;
    }

    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
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

    public DataSetService getDataSetService()
    {
        return dataSetService;
    }

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
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

    public IncomingSmsService getIncomingSmsService()
    {
        return incomingSmsService;
    }

    public void setIncomingSmsService( IncomingSmsService incomingSmsService )
    {
        this.incomingSmsService = incomingSmsService;
    }
}
