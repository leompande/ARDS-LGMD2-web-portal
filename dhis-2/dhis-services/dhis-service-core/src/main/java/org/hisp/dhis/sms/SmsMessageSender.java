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

import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.message.MessageSender;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.user.UserSetting;
import org.hisp.dhis.user.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Nguyen Kim Lai
 * 
 * @version SmsSender.java 10:29:11 AM Apr 16, 2013 $
 */
public class SmsMessageSender
    implements MessageSender
{
    private static final Log log = LogFactory.getLog( SmsMessageSender.class );

    private static int MAX_CHAR = 160;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    @Autowired
    private OutboundSmsTransportService outboundSmsTransportService;

    /**
     * Note this methods is invoked asynchronously.
     */
    // @Async
    @Override
    public String sendMessage( String subject, String text, User sender, Set<User> users, boolean forceSend )
    {
        String message = null;

        if ( outboundSmsTransportService == null || outboundSmsTransportService.getGatewayMap() == null || !outboundSmsTransportService.isEnabled() )
        {
            message = "No gateway";
            return message;
        }

        Set<User> toSendList = new HashSet<User>();

        String gatewayId = outboundSmsTransportService.getDefaultGateway();

        if ( gatewayId != null && !gatewayId.trim().isEmpty() )
        {
            if( !forceSend )
            {
                for ( User user : users )
                {
                    if ( currentUserService.getCurrentUser() != null )
                    {
                        if ( !currentUserService.getCurrentUser().equals( user ) )
                        {
                            if ( isQualifiedReceiver( user ) )
                            {
                                toSendList.add( user );
                            }
                        }
                    }
                    else if ( currentUserService.getCurrentUser() == null )
                    {
                        if ( isQualifiedReceiver( user ) )
                        {
                            toSendList.add( user );
                        }
                    }
                }
            }
            else
            {
                toSendList.addAll( users );
            }

            Set<String> phoneNumbers = null;

            if ( outboundSmsTransportService != null || outboundSmsTransportService.isEnabled() )
            {
                phoneNumbers = getRecipientsPhoneNumber( toSendList );

                text = createMessage( subject, text, sender );

                // Bulk is limited in sending long SMS, need to cut into small
                // pieces
                if ( outboundSmsTransportService.getGatewayMap().get( "bulk_gw" ) != null
                    && outboundSmsTransportService.getGatewayMap().get( "bulk_gw" ).equals( gatewayId ) )
                {
                    // Check if text contain any specific unicode character
                    for ( char each : text.toCharArray() )
                    {
                        if ( !Character.UnicodeBlock.of( each ).equals( UnicodeBlock.BASIC_LATIN ) )
                        {
                            MAX_CHAR = 40;
                            break;
                        }
                    }
                    if ( text.length() > MAX_CHAR )
                    {
                        List<String> splitTextList = new ArrayList<String>();
                        splitTextList = splitLongUnicodeString( text, splitTextList );
                        for ( String each : splitTextList )
                        {
                            if ( !phoneNumbers.isEmpty() && phoneNumbers.size() > 0 )
                            {
                                message = sendMessage( each, phoneNumbers, gatewayId );
                            }
                        }
                    }
                    else
                    {
                        if ( !phoneNumbers.isEmpty() && phoneNumbers.size() > 0 )
                        {
                            message = sendMessage( text, phoneNumbers, gatewayId );
                        }
                    }
                }
                else
                {
                    if ( !phoneNumbers.isEmpty() && phoneNumbers.size() > 0 )
                    {
                        message = sendMessage( text, phoneNumbers, gatewayId );
                    }
                }
            }
        }

        return message;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private boolean isQualifiedReceiver( User user )
    {
        if ( user.getFirstName() == null ) // If receiver is raw number
        {
            return true;
        }
        else
        // If receiver is user
        {
            UserSetting userSetting = userService
                .getUserSetting( user, UserSettingService.KEY_MESSAGE_SMS_NOTIFICATION );

            if ( userSetting != null )
            {
                boolean sendSMSNotification = (Boolean) userSetting.getValue();

                if ( sendSMSNotification == true )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
    }

    private String createMessage( String subject, String text, User sender )
    {
        String name = "DHIS";

        if ( sender != null )
        {
            name = sender.getUsername();
        }

        if ( subject == null || subject.isEmpty() )
        {
            subject = "";
        }
        else
        {
            subject = " - " + subject;
        }

        text = name + subject + ": " + text;

        int length = text.length(); // Simplistic cut off 160 characters

        return (length > 160) ? text.substring( 0, 157 ) + "..." : text;
    }

    private Set<String> getRecipientsPhoneNumber( Set<User> users )
    {
        Set<String> recipients = new HashSet<String>();

        for ( User user : users )
        {
            String phoneNumber = user.getPhoneNumber();

            if ( phoneNumber != null && !phoneNumber.trim().isEmpty() )
            {
                recipients.add( phoneNumber );
            }
        }
        return recipients;
    }

    private String sendMessage( String text, Set<String> recipients, String gateWayId )
    {
        String message = null;
        OutboundSms sms = new OutboundSms();
        sms.setMessage( text );
        sms.setRecipients( recipients );

        try
        {
            message = outboundSmsTransportService.sendMessage( sms, gateWayId );
        }
        catch ( SmsServiceException e )
        {
            message = "Unable to send message through sms: " + sms + e.getCause().getMessage();

            log.warn( "Unable to send message through sms: " + sms, e );
        }
        return message;
    }

    public List<String> splitLongUnicodeString( String message, List<String> result )
    {
        String firstTempString = null;
        String secondTempString = null;
        int indexToCut;

        firstTempString = message.substring( 0, MAX_CHAR );

        indexToCut = firstTempString.lastIndexOf( " " );

        firstTempString = firstTempString.substring( 0, indexToCut );

        result.add( firstTempString );

        secondTempString = message.substring( indexToCut + 1, message.length() );

        if ( secondTempString.length() <= MAX_CHAR )
        {
            result.add( secondTempString );
            return result;
        }
        else
        {
            return splitLongUnicodeString( secondTempString, result );
        }
    }
}
