package org.hisp.dhis.api.mobile.model.LWUITmodel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.api.mobile.model.DataStreamSerializable;

public class ProgramInstance
    implements DataStreamSerializable
{
    private Integer id;

    private Integer patientId;

    private Integer programId;
    
    private String name;

    // status active = 0
    // status complete = 1
    // status canceled = 2
    private Integer status;

    private String dateOfEnrollment;

    private String dateOfIncident;

    private List<ProgramStage> programStageInstances = new ArrayList<ProgramStage>();

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getPatientId()
    {
        return patientId;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    public Integer getProgramId()
    {
        return programId;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus( Integer status )
    {
        this.status = status;
    }

    public String getDateOfEnrollment()
    {
        return dateOfEnrollment;
    }

    public void setDateOfEnrollment( String dateOfEnrollment )
    {
        this.dateOfEnrollment = dateOfEnrollment;
    }

    public String getDateOfIncident()
    {
        return dateOfIncident;
    }

    public void setDateOfIncident( String dateOfIncident )
    {
        this.dateOfIncident = dateOfIncident;
    }

    public List<ProgramStage> getProgramStageInstances()
    {
        return programStageInstances;
    }

    public void setProgramStageInstances( List<ProgramStage> programStageInstances )
    {
        this.programStageInstances = programStageInstances;
    }
    
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeInt( this.getId() );
        dataOutputStream.writeInt( this.getPatientId() );
        dataOutputStream.writeInt( this.getProgramId() );
        dataOutputStream.writeUTF( this.getName() );
        dataOutputStream.writeInt( this.getStatus() );
        dataOutputStream.writeUTF( this.getDateOfEnrollment() );
        dataOutputStream.writeUTF( this.getDateOfIncident() );

        dataOutputStream.writeInt( programStageInstances.size() );
        for ( ProgramStage programStageInstance : programStageInstances )
        {
            programStageInstance.serialize( dataOutputStream );
        }

    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        this.setId( dataInputStream.readInt() );
        this.setPatientId( dataInputStream.readInt() );
        this.setProgramId( dataInputStream.readInt() );
        this.setName( dataInputStream.readUTF() );
        this.setStatus( dataInputStream.readInt() );
        this.setDateOfEnrollment( dataInputStream.readUTF() );
        this.setDateOfIncident( dataInputStream.readUTF() );

        // Read programstage instance
        int programStageInstanceSize = dataInputStream.readInt();
        for ( int i = 0; i < programStageInstanceSize; i++ )
        {
            ProgramStage programStageInstance = new ProgramStage();
            programStageInstance.deSerialize( dataInputStream );
            programStageInstances.add( programStageInstance );
        }

    }

    @Override
    public void serializeVersion2_8( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub

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
