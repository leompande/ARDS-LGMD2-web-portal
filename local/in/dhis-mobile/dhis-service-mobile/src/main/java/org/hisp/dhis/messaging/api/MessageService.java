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
package org.hisp.dhis.messaging.api;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.mobile.api.SendSMS;
import org.smslib.InboundMessage;

public interface MessageService
{

    boolean getServiceStatus();

    void setServiceStatus( boolean serviceStatus );

    String startService();

    String stopService();

    void processMessage( Object message );

    void sendAck(String recipient, Object message, String msg);

    String sendMessage( String recipient, String msg );

    String saveData( String userId, Date msgTime, String data );
    
    String sendMessageToGroup( String groupName, List<String> recepients, String msg );

    List<InboundMessage> readAllMessages();
    
    String processPendingMessages();
    
    String sendMessages( List<SendSMS> sendSMSList );
    
    String sendDrafts();
    
    Map<String,String> readAllPendingMessages();

    String sendOtaMessage(String recipient, String url, String prompt);
}
