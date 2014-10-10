package org.hisp.dhis.mobile.impl;

import java.util.Collection;

import org.hisp.dhis.mobile.api.SendSMS;
import org.hisp.dhis.mobile.api.SendSMSService;
import org.hisp.dhis.mobile.api.SendSMSStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultSendSMSService implements SendSMSService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SendSMSStore sendSMSStore;

    public void setSendSMSStore( SendSMSStore sendSMSStore )
    {
        this.sendSMSStore = sendSMSStore;
    }
    
    // -------------------------------------------------------------------------
    // SendSMS
    // -------------------------------------------------------------------------
    
    public void addSendSMS( SendSMS sendSMS )
    {
        sendSMSStore.addSendSMS( sendSMS );
    }
    
    public void updateSendSMS( SendSMS sendSMS )
    {
        sendSMSStore.updateSendSMS( sendSMS );
    }
    
    public void deleteSendSMS( SendSMS sendSMS )
    {
        sendSMSStore.deleteSendSMS( sendSMS );
    }
    
    public Collection<SendSMS> getSendSMS( int start, int end )
    {
        return sendSMSStore.getSendSMS( start, end );
    }

    public Collection<SendSMS> getAllSendSMS( )
    {
        return sendSMSStore.getAllSendSMS();
    }
    
    public long getRowCount()
    {
        return sendSMSStore.getRowCount();
    }
    
    public SendSMS getSendSMS( String senderInfo )
    {
        return sendSMSStore.getSendSMS( senderInfo );
    }
}
