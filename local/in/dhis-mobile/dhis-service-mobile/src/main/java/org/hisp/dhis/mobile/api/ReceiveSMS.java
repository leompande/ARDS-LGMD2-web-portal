package org.hisp.dhis.mobile.api;

import java.io.Serializable;

public class ReceiveSMS implements Serializable

{

    /**
     * Sender Phone Number with time of Received, Unique and Required.
     */
    private String receiverInfo;

    /**
     * Message to send to sender, Required
     */
    private String receiveingMessage;

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public ReceiveSMS()
    {
        
    }
    
    public ReceiveSMS( String receiverInfo, String receiveingMessage )
    {
        this.receiverInfo = receiverInfo;
        this.receiveingMessage = receiveingMessage;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return receiverInfo.hashCode();
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

        if ( !(o instanceof ReceiveSMS) )
        {
            return false;
        }

        final ReceiveSMS other = (ReceiveSMS) o;

        return receiverInfo.equals( other.getReceiverInfo() );
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getReceiverInfo()
    {
        return receiverInfo;
    }

    public void setReceiverInfo( String receiverInfo )
    {
        this.receiverInfo = receiverInfo;
    }

    public String getReceiveingMessage()
    {
        return receiveingMessage;
    }

    public void setReceiveingMessage( String receiveingMessage )
    {
        this.receiveingMessage = receiveingMessage;
    }
    
}
