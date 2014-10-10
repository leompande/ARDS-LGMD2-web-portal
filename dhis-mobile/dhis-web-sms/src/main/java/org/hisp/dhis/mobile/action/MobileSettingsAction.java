package org.hisp.dhis.mobile.action;

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

import com.opensymphony.xwork2.Action;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

public class MobileSettingsAction implements Action
{

    private File configFile = new File( System.getenv( "DHIS2_HOME" ), "SMSServer.conf" );

    private Properties props = new Properties();

    private String balancer;

    public String getBalancer()
    {
        return props.getProperty( "smsserver.balancer" );
    }

    public void setBalancer( String balancer )
    {
        props.setProperty( "smsserver.balancer", balancer );
    }

    private String router;

    public String getRouter()
    {
        return props.getProperty( "smsserver.router" );
    }

    public void setRouter( String router )
    {
        props.setProperty( "smsserver.router", router );
    }

    private String gatewayName;

    public String getGatewayName()
    {
        return props.getProperty( "gateway.0" );
    }

    public void setGatewayName( String gatewayName )
    {
        props.setProperty( "gateway.0", gatewayName );
    }
    
    private String bulksmsUsername;

    public String getBulksmsUsername()
    {
        return props.getProperty( "bulksms.username" );
    }

    public void setBulksmsUsername( String bulksmsUsername )
    {
        props.setProperty( "bulksms.username", bulksmsUsername );
    }
    
    private String bulksmsPassword;

    public String getBulksmsPassword()
    {
        return props.getProperty( "bulksms.password" );
    }

    public void setBulksmsPassword( String bulksmsPassword )
    {
        props.setProperty( "bulksms.password", bulksmsPassword );
    }
    
    private String clickatellUsername;
    
    public String getClickatellUsername()
    {
        return props.getProperty( "clickatell.username" );
    }

    public void setClickatellUsername( String clickatellUsername )
    {
        props.setProperty( "clickatell.username", clickatellUsername);
    }
    
    private String clickatellPassword;
    
    public String getClickatellPassword()
    {
        return props.getProperty( "clickatell.password" );
    }

    public void setClickatellPassword( String clickatellPassword )
    {
        props.setProperty( "clickatell.password", clickatellPassword);
    }
    
    private String clickatellApiId;
    
    public String getClickatellApiId()
    {
        return props.getProperty( "clickatell.api_id" );
    }

    public void setClickatellApiId( String clickatellApiId )
    {
        props.setProperty( "clickatell.api_id" , clickatellApiId);
    }
    
    private String longNumber;

    public String getLongNumber()
    {
        return props.getProperty( "provider.longnumber" );
    }

    public void setLongNumber( String longNumber )
    {
        props.setProperty( "provider.longnumber" , longNumber);
    }
    
    private String port;

    public String getPort()
    {
        return props.getProperty( "modem1.port" );
    }

    public void setPort( String port )
    {
        props.setProperty( "modem1.port", port );
    }

    private String baudRate;

    public String getBaudRate()
    {
        return props.getProperty( "modem1.baudrate" );
    }

    public void setBaudRate( String baudRate )
    {
        props.setProperty( "modem1.baudrate", baudRate );
    }

    private String manufacturer;

    public String getManufacturer()
    {
        return props.getProperty( "modem1.manufacturer" );
    }

    public void setManufacturer( String manufacturer )
    {
        props.setProperty( "modem1.manufacturer", manufacturer );
    }

    private String model;

    public String getModel()
    {
        return props.getProperty( "modem1.model" );
    }

    public void setModel( String model )
    {
        props.setProperty( "modem1.model", model );
    }

    private String protocol;

    public String getProtocol()
    {
        return props.getProperty( "modem1.protocol" );
    }

    public void setProtocol( String protocol )
    {
        props.setProperty( "modem1.protocol", protocol );
    }

    private String pin;

    public String getPin()
    {
        return props.getProperty( "modem1.pin" );
    }

    public void setPin( String pin )
    {
        props.setProperty( "modem1.pin", pin );
    }

    private String inbound;

    public String getInbound()
    {
        return props.getProperty( "modem1.inbound" );
    }

    public void setInbound( String inbound )
    {
        props.setProperty( "modem1.inbound", inbound );
    }

    private String outbound;

    public String getOutbound()
    {
        return props.getProperty( "modem1.outbound" );
    }

    public void setOutbound( String outbound )
    {
        props.setProperty( "modem1.outbound", outbound );
    }

    private String smsc_number;

    public String getSmsc_number()
    {
        return props.getProperty( "modem1.smsc_number" );
    }

    public void setSmsc_number( String smsc_number )
    {
        props.setProperty( "modem1.smsc_number", smsc_number );
    }

    private String init_string;

    public String getInit_string()
    {
        return props.getProperty( "modem1.init_string" );
    }

    public void setInit_string( String init_string )
    {
        props.setProperty( "modem1.init_string", init_string );
    }

    private String inbound_interval;

    public String getInbound_interval()
    {
        return props.getProperty( "settings.inbound_interval" );
    }

    public void setInbound_interval( String inbound_interval )
    {
        props.setProperty( "settings.inbound_interval", inbound_interval );
    }

    private String outbound_interval;

    public String getOutbound_interval()
    {
        return props.getProperty( "settings.outbound_interval" );
    }

    public void setOutbound_interval( String outbound_interval )
    {
        props.setProperty( "settings.outbound_interval", outbound_interval );
    }

    private String delete_after_processing;

    public String getDelete_after_processing()
    {
        return props.getProperty( "settings.delete_after_processing" );
    }

    public void setDelete_after_processing( String delete_after_processing ) throws Exception
    {
        props.setProperty( "settings.delete_after_processing", delete_after_processing );
    }

    private String simMemLocation = "-";

    public void setSimMemLocation(String simMemLocation)
    {
        props.setProperty("modem1.simMemLocation", simMemLocation);
    }

    public String getSimMemLocation()
    {
        return props.getProperty( "modem1.simMemLocation" );
    }

    private String send_mode;

    public String getSend_mode()
    {
        return props.getProperty( "settings.send_mode" );
    }

    public void setSend_mode( String send_mode ) throws Exception
    {
        props.setProperty( "settings.send_mode", send_mode );
        props.store( new FileWriter( configFile ), "SMS Server Configuration" );
        message = "Settings Saved Successfully";
    }

    private String message = "";

    public String getMessage()
    {
        return message;
    }

    @Override
    public String execute()
        throws Exception
    {
        if ( !configFile.exists() )
        {
            FileOutputStream fos = new FileOutputStream( configFile );
            fos.close();
            props.setProperty( "smsserver.balancer", "RoundRobinLoadBalancer" );
            props.setProperty( "smsserver.router", "NumberPoolRouter" );
            props.setProperty( "gateway.0", "modem1, SerialModem" );
            props.setProperty( "bulksms.username", "" );
            props.setProperty( "bulksms.password", "" );
            props.setProperty( "clickatell.username", "");
            props.setProperty( "clickatell.password", "");
            props.setProperty( "clickatell.api_id", "");
            props.setProperty( "provider.longnumber", "" );
            props.setProperty( "modem1.port", "COM1" );
            props.setProperty( "modem1.baudrate", "57600" );
            props.setProperty( "modem1.manufacturer", "Generic" );
            props.setProperty( "modem1.model", "AT" );
            props.setProperty( "modem1.protocol", "PDU" );
            props.setProperty( "modem1.pin", "0000" );
            props.setProperty( "modem1.inbound", "yes" );
            props.setProperty( "modem1.outbound", "yes" );
            props.setProperty( "modem1.smsc_number", "" );
            props.setProperty( "modem1.init_string", "ATZ\\\rATZ\\\rATZ\\\r" );
            props.setProperty( "modem1.simMemLocation", "-");
            props.setProperty( "settings.inbound_interval", "600" );
            props.setProperty( "settings.outbound_interval", "10" );
            props.setProperty( "settings.delete_after_processing", "yes" );
            props.setProperty( "settings.send_mode", "async" );
            props.store( new FileWriter( configFile ), "SMS Server Configuration" );
        } else
        {
            props.load( new FileReader( configFile ) );
        }
        return SUCCESS;
    }
}
