package org.hisp.dhis.program;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientcomment.PatientComment;
import org.hisp.dhis.sms.outbound.OutboundSms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@JacksonXmlRootElement( localName = "programInstance", namespace = DxfNamespaces.DXF_2_0 )
public class ProgramInstance
    extends BaseIdentifiableObject
{
    public static int STATUS_ACTIVE = 0;

    public static int STATUS_COMPLETED = 1;

    public static int STATUS_CANCELLED = 2;

    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -1235315582356509653L;

    private int id;

    private Date dateOfIncident;

    private Date enrollmentDate;

    private Date endDate;

    private Integer status = STATUS_ACTIVE;

    private Patient patient;

    private Program program;
    
    private Set<ProgramStageInstance> programStageInstances = new HashSet<ProgramStageInstance>();

    private List<OutboundSms> outboundSms = new ArrayList<OutboundSms>();

    private List<MessageConversation> messageConversations = new ArrayList<MessageConversation>();

    private Boolean followup = false;

    private Set<PatientComment> patientComments = new HashSet<PatientComment>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramInstance()
    {
    }

    
    public ProgramInstance( Date enrollmentDate, Date dateOfIncident, Patient patient, Program program )
    {
        this.enrollmentDate = enrollmentDate;
        this.dateOfIncident = dateOfIncident;
        this.patient = patient;
        this.program = program;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Updated the bi-directional associations between this program instance and
     * the given patient and program.
     * 
     * @param patient the patient to enroll.
     * @param program the program to enroll the patient to.
     */
    public void enrollPatient( Patient patient, Program program )
    {
        setPatient( patient );
        patient.getProgramInstances().add( this );
        
        setProgram( program );
        program.getProgramInstances().add( this );
    }
    
    public ProgramStageInstance getProgramStageInstanceByStage( int stage )
    {
        int count = 1;

        for ( ProgramStageInstance programInstanceStage : programStageInstances )
        {
            if ( count == stage )
            {
                return programInstanceStage;
            }

            count++;
        }

        return null;
    }

    public ProgramStageInstance getActiveProgramStageInstance()
    {
        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {
            if ( programStageInstance.getProgramStage().getOpenAfterEnrollment() && !programStageInstance.isCompleted()
                && ( programStageInstance.getStatus() != null && programStageInstance.getStatus() != ProgramStageInstance.SKIPPED_STATUS ) )
            {
                return programStageInstance;
            }
        }

        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {
            if ( !programStageInstance.isCompleted()
                &&  ( programStageInstance.getStatus() != null &&  programStageInstance.getStatus() != ProgramStageInstance.SKIPPED_STATUS ) )
            {
                return programStageInstance;
            }
        }

        return null;
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getDateOfIncident()
    {
        return dateOfIncident;
    }

    public void setDateOfIncident( Date dateOfIncident )
    {
        this.dateOfIncident = dateOfIncident;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getEnrollmentDate()
    {
        return enrollmentDate;
    }

    public void setEnrollmentDate( Date enrollmentDate )
    {
        this.enrollmentDate = enrollmentDate;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate( Date endDate )
    {
        this.endDate = endDate;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public int getStatus()
    {
        return status.intValue();
    }

    public void setStatus( Integer status )
    {
        this.status = status;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public Set<ProgramStageInstance> getProgramStageInstances()
    {
        return programStageInstances;
    }

    public void setProgramStageInstances( Set<ProgramStageInstance> programStageInstances )
    {
        this.programStageInstances = programStageInstances;
    }

    public List<OutboundSms> getOutboundSms()
    {
        return outboundSms;
    }

    public void setOutboundSms( List<OutboundSms> outboundSms )
    {
        this.outboundSms = outboundSms;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getFollowup()
    {
        return followup;
    }

    public void setFollowup( Boolean followup )
    {
        this.followup = followup;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Set<PatientComment> getPatientComments()
    {
        return patientComments;
    }

    public void setPatientComments( Set<PatientComment> patientComments )
    {
        this.patientComments = patientComments;
    }

    public List<MessageConversation> getMessageConversations()
    {
        return messageConversations;
    }

    public void setMessageConversations( List<MessageConversation> messageConversations )
    {
        this.messageConversations = messageConversations;
    }		
}
