package org.hisp.dhis.api.mobile.model;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

public class PatientAttribute
    implements DataStreamSerializable
{
    private String clientVersion;

    private String name;

    private String value;

    private String type;

    private boolean isMandatory;
    
    private boolean isDisplayedInList = false;

    private List<String> predefinedValues = new ArrayList<String>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientAttribute( String name, String value, String type, boolean isMandatory, List<String> predefinedValues )
    {
        this.name = name;
        this.value = value;
        this.type = type;
        this.isMandatory = isMandatory;
        this.predefinedValues = predefinedValues;
    }

    public PatientAttribute()
    {
    }

    // -------------------------------------------------------------------------
    // Gettes && Setters
    // -------------------------------------------------------------------------

    @XmlAttribute
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @XmlAttribute
    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getClientVersion()
    {
        return clientVersion;
    }

    public void setClientVersion( String clientVersion )
    {
        this.clientVersion = clientVersion;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public List<String> getPredefinedValues()
    {
        return predefinedValues;
    }

    public void setPredefinedValues( List<String> predefinedValues )
    {
        this.predefinedValues = predefinedValues;
    }

    public boolean isMandatory()
    {
        return isMandatory;
    }

    public void setMandatory( boolean isMandatory )
    {
        this.isMandatory = isMandatory;
    }
    
    public boolean isDisplayedInList()
    {
        return isDisplayedInList;
    }

    public void setDisplayedInList( boolean isDisplayedInList )
    {
        this.isDisplayedInList = isDisplayedInList;
    }

    @Override
    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeUTF( this.name );
        dout.writeUTF( this.value );
        dout.writeUTF( this.type );
        dout.writeBoolean( this.isMandatory );
        dout.writeBoolean( this.isDisplayedInList );

        int valueSize = this.predefinedValues.size();
        dout.writeInt( valueSize );
        if ( valueSize > 0 )
        {
            for ( String option : predefinedValues )
            {
                dout.writeUTF( option );
            }
        }

    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        name = dataInputStream.readUTF();
        value = dataInputStream.readUTF();
        type = dataInputStream.readUTF();
        isMandatory = dataInputStream.readBoolean();
        isDisplayedInList = dataInputStream.readBoolean();

        List<String> optionList = new ArrayList<String>();

        int size = dataInputStream.readInt();

        for ( int i = 0; i < size; i++ )
        {
            optionList.add( dataInputStream.readUTF() );
        }
        predefinedValues = optionList;
    }

    @Override
    public void serializeVersion2_8( DataOutputStream dout )
        throws IOException
    {
        dout.writeUTF( this.name );
        dout.writeUTF( this.value );
    }

    @Override
    public void serializeVersion2_9( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void serializeVersion2_10( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub

    }

}
