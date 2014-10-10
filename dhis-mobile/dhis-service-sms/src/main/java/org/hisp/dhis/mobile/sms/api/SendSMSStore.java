package org.hisp.dhis.mobile.sms.api;

import java.util.Collection;

public interface SendSMSStore
{
    String ID = SendSMSStore.class.getName();
    
    // -------------------------------------------------------------------------
    // SendSMS
    // -------------------------------------------------------------------------

    void addSendSMS( SendSMS sendSMS );
    
    void updateSendSMS( SendSMS sendSMS );
    
    void deleteSendSMS( SendSMS sendSMS );
    
    Collection<SendSMS> getSendSMS( int start, int end );

    Collection<SendSMS> getAllSendSMS( );
    
    long getRowCount();
}
