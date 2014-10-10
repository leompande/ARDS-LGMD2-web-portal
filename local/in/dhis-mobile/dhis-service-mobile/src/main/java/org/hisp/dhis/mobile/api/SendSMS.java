package org.hisp.dhis.mobile.api;

import java.io.Serializable;

public class SendSMS implements Serializable
{
    public static final int sendSMSRange = 30;
    
    /**
     * Sender Phone Number with time of Received, Unique and Required.
     */
    private String senderInfo;

    /**
     * Message to send to sender, Required
     */
    private String sendingMessage;

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public SendSMS()
    {
        
    }
    
    public SendSMS( String senderInfo, String sendingMessage )
    {
        this.senderInfo = senderInfo;
        this.sendingMessage = sendingMessage;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return senderInfo.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof SendSMS) )
        {
            return false;
        }

        final SendSMS other = (SendSMS) o;

        return senderInfo.equals( other.getSenderInfo() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getSenderInfo()
    {
        return senderInfo;
    }

    public void setSenderInfo( String senderInfo )
    {
        this.senderInfo = senderInfo;
    }

    public String getSendingMessage()
    {
        return sendingMessage;
    }

    public void setSendingMessage( String sendingMessage )
    {
        this.sendingMessage = sendingMessage;
    }
    
}
