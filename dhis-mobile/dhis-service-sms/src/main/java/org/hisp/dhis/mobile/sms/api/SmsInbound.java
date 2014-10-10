package org.hisp.dhis.mobile.sms.api;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Saptarshi
 */
public class SmsInbound implements Serializable
{
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    //<editor-fold defaultstate="collapsed" desc="Properties">
    /*
     * The table's primary key.
     */
    private Integer id;
    
    /*
     * When new rows (i.e. messages) are created, sets this field to 0.
     * When datavalues successfully saved (completed), sets this field to 1.
     * When datavalues cannot be saved (bounced), sets this field to 2.
     */
    private Integer process;
    
    /*
     * The originator of the received message.
     */
    private String originator;
    
    /*
     * "I" for inbound message, "S" for status report message.
     */
    private Character type;
    
    /*
     * "7" for 7bit, "8" for 8bit and "U" for Unicode/UCS2.
     */
    private Character encoding;
    
    /*
     * The message date (retrieved by the message headers).
     */
    private Date messageDate;
    
    /*
     * The datetime when message was received.
     */
    private Date receiveDate;
    
    /*
     * The body of the message.
     */
    private String text;
    
    /*
     * Available only for status report messages: refers to the RefNo of the original outbound message.
     */
    private String originalRefNo;
    
    /*
     * Available only for status report messages: refers to the receive date of the original outbound message.
     */
    private Date originalReceiveDate;
    
    /*
     * The ID of the gateway from which the message was received.
     */
    private String gatewayId;
    //</editor-fold>
    
    // -------------------------------------------------------------------------
    // Getter-Setters
    // -------------------------------------------------------------------------
    //<editor-fold defaultstate="collapsed" desc="Getter-Setters">
    public Character getEncoding()
    {
        return encoding;
    }
    
    public void setEncoding( Character encoding )
    {
        this.encoding = encoding;
    }
    
    public String getGatewayId()
    {
        return gatewayId;
    }
    
    public void setGatewayId( String gatewayId )
    {
        this.gatewayId = gatewayId;
    }
    
    public Integer getId()
    {
        return id;
    }
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    public Date getMessageDate()
    {
        return messageDate;
    }
    
    public void setMessageDate( Date messageDate )
    {
        this.messageDate = messageDate;
    }
    
    public Date getOriginalReceiveDate()
    {
        return originalReceiveDate;
    }
    
    public void setOriginalReceiveDate( Date originalReceiveDate )
    {
        this.originalReceiveDate = originalReceiveDate;
    }
    
    public String getOriginalRefNo()
    {
        return originalRefNo;
    }
    
    public void setOriginalRefNo( String originalRefNo )
    {
        this.originalRefNo = originalRefNo;
    }
    
    public String getOriginator()
    {
        return originator;
    }
    
    public void setOriginator( String originator )
    {
        this.originator = originator;
    }
    
    public Integer getProcess()
    {
        return process;
    }
    
    public void setProcess( Integer process )
    {
        this.process = process;
    }
    
    public Date getReceiveDate()
    {
        return receiveDate;
    }
    
    public void setReceiveDate( Date receiveDate )
    {
        this.receiveDate = receiveDate;
    }
    
    public String getText()
    {
        return text;
    }
    
    public void setText( String text )
    {
        this.text = text;
    }
    
    public Character getType()
    {
        return type;
    }
    
    public void setType( Character type )
    {
        this.type = type;
    }
    //</editor-fold>
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------
    //<editor-fold defaultstate="collapsed" desc="hashCode and equals">
    @Override
    public int hashCode()
    {
        return this.hashCode();
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
        final SmsInbound other = (SmsInbound) o;
        return o.equals( other );
    }
    //</editor-fold>
}
