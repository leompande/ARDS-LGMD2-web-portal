package org.hisp.dhis.api.mobile.model.LWUITmodel;

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

import org.hisp.dhis.api.mobile.model.Model;
import org.hisp.dhis.api.mobile.model.PatientAttribute;

/**
 * @author Nguyen Kim Lai
 */
public class Program
    extends Model
{
    // Work as Program and ProgramInstance
    private String clientVersion;

    private int version;

    // multiple event with registration: 1
    // single event with registration: 2
    // single event without registration: 3

    private Integer type;

    private String dateOfEnrollmentDescription = "Date of Enrollment";

    private String dateOfIncidentDescription = "Date of Incident";

    private List<ProgramStage> programStages = new ArrayList<ProgramStage>();

    private List<PatientAttribute> programAttributes = new ArrayList<PatientAttribute>();

    public List<ProgramStage> getProgramStages()
    {
        return programStages;
    }

    public void setProgramStages( List<ProgramStage> programStages )
    {
        this.programStages = programStages;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion( int version )
    {
        this.version = version;
    }

    public String getClientVersion()
    {
        return clientVersion;
    }

    public void setClientVersion( String clientVersion )
    {
        this.clientVersion = clientVersion;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType( Integer type )
    {
        this.type = type;
    }

    public String getDateOfEnrollmentDescription()
    {
        return dateOfEnrollmentDescription;
    }

    public void setDateOfEnrollmentDescription( String dateOfEnrollmentDescription )
    {
        this.dateOfEnrollmentDescription = dateOfEnrollmentDescription;
    }

    public String getDateOfIncidentDescription()
    {
        return dateOfIncidentDescription;
    }

    public void setDateOfIncidentDescription( String dateOfIncidentDescription )
    {
        this.dateOfIncidentDescription = dateOfIncidentDescription;
    }

    public List<PatientAttribute> getProgramAttributes()
    {
        return programAttributes;
    }

    public void setProgramAttributes( List<PatientAttribute> programAttributes )
    {
        this.programAttributes = programAttributes;
    }

    @Override
    public void serialize( DataOutputStream dout )
        throws IOException
    {
        super.serialize( dout );
        dout.writeInt( getVersion() );
        dout.writeInt( this.getType() );
        dout.writeUTF( getDateOfEnrollmentDescription() );
        dout.writeUTF( getDateOfIncidentDescription() );

        // Write program stage
        dout.writeInt( programStages.size() );
        for ( int i = 0; i < programStages.size(); i++ )
        {
            ProgramStage ps = programStages.get( i );
            ps.serialize( dout );
        }

        // Write program attribute
        dout.writeInt( programAttributes.size() );
        for ( int i = 0; i < programAttributes.size(); i++ )
        {
            PatientAttribute pa = programAttributes.get( i );
            pa.serialize( dout );
        }

    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        super.deSerialize( dataInputStream );
        this.setVersion( dataInputStream.readInt() );
        this.setType( dataInputStream.readInt() );
        this.setDateOfEnrollmentDescription( dataInputStream.readUTF() );
        this.setDateOfIncidentDescription( dataInputStream.readUTF() );

        // Read program stage
        int programStageNumber = dataInputStream.readInt();
        if ( programStageNumber > 0 )
        {
            for ( int i = 0; i < programStageNumber; i++ )
            {
                ProgramStage programStage = new ProgramStage();
                programStage.deSerialize( dataInputStream );
                programStages.add( programStage );
            }
        }

        // Read program attribute
        int programAttSize = dataInputStream.readInt();
        if ( programAttSize > 0 )
        {
            for ( int i = 0; i < programAttSize; i++ )
            {
                PatientAttribute pa = new PatientAttribute();
                pa.deSerialize( dataInputStream );
                programAttributes.add( pa );
            }
        }
    }
}
