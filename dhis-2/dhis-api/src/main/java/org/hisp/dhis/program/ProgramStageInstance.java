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

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientcomment.PatientComment;
import org.hisp.dhis.sms.outbound.OutboundSms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Abyot Asalefew
 */
/**
 * @author abyot
 * 
 */
public class ProgramStageInstance
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6239130884678145713L;

    public static final int ACTIVE_STATUS = 0;

    public static final int COMPLETED_STATUS = 1;

    public static final int VISITED_STATUS = 2;

    public static final int FUTURE_VISIT_STATUS = 3;

    public static final int LATE_VISIT_STATUS = 4;

    public static final int SKIPPED_STATUS = 5;

    private ProgramInstance programInstance;

    private ProgramStage programStage;

    private Date dueDate;

    private Date executionDate;

    private OrganisationUnit organisationUnit;

    private boolean completed;

    private List<OutboundSms> outboundSms = new ArrayList<OutboundSms>();

    private List<MessageConversation> messageConversations = new ArrayList<MessageConversation>();

    private PatientComment patientComment;

    private Integer status = ACTIVE_STATUS;

    private Double longitude;

    private Double latitude;

    private String completedUser;

    private Date completedDate;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramStageInstance()
    {
        setAutoFields();
    }

    public ProgramStageInstance( ProgramInstance programInstance, ProgramStage programStage )
    {
        this.programInstance = programInstance;
        this.programStage = programStage;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( !getClass().isAssignableFrom( object.getClass() ) )
        {
            return false;
        }

        // TODO include due date and execution date to make consistent with
        // hashcode

        final ProgramStageInstance other = (ProgramStageInstance) object;

        return programInstance.equals( other.getProgramInstance() ) && programStage.equals( other.getProgramStage() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + programInstance.hashCode();
        result = result * prime + programStage.hashCode();
        result = result * prime + ((dueDate == null) ? 0 : dueDate.hashCode());
        result = result * prime + ((executionDate == null) ? 0 : executionDate.hashCode());

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /**
     * @return the programInstance
     */
    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    /**
     * @param programInstance the programInstance to set
     */
    public void setProgramInstance( ProgramInstance programInstance )
    {
        this.programInstance = programInstance;
    }

    /**
     * @return the programStage
     */
    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    /**
     * @param programStage the programStage to set
     */
    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    public String getCompletedUser()
    {
        return completedUser;
    }

    public void setCompletedUser( String completedUser )
    {
        this.completedUser = completedUser;
    }

    /**
     * @return the dueDate
     */
    public Date getDueDate()
    {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate( Date dueDate )
    {
        this.dueDate = dueDate;
    }

    /**
     * @return the executionDate
     */
    public Date getExecutionDate()
    {
        return executionDate;
    }

    /**
     * @param executionDate the executionDate to set
     */
    public void setExecutionDate( Date executionDate )
    {
        this.executionDate = executionDate;
    }

    /**
     * @return the completed
     */
    public boolean isCompleted()
    {
        return completed;
    }

    /**
     * @param completed the completed to set
     */
    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public List<OutboundSms> getOutboundSms()
    {
        return outboundSms;
    }

    public void setOutboundSms( List<OutboundSms> outboundSms )
    {
        this.outboundSms = outboundSms;
    }

    public Date getCompletedDate()
    {
        return completedDate;
    }

    public void setCompletedDate( Date completedDate )
    {
        this.completedDate = completedDate;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus( Integer status )
    {
        this.status = status;
    }

    public List<MessageConversation> getMessageConversations()
    {
        return messageConversations;
    }

    public void setMessageConversations( List<MessageConversation> messageConversations )
    {
        this.messageConversations = messageConversations;
    }

    public Double getLongitude()
    {
        return longitude;
    }

    public void setLongitude( Double longitude )
    {
        this.longitude = longitude;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public void setLatitude( Double latitude )
    {
        this.latitude = latitude;
    }

    public PatientComment getPatientComment()
    {
        return patientComment;
    }

    public void setPatientComment( PatientComment patientComment )
    {
        this.patientComment = patientComment;
    }

    public Integer getEventStatus()
    {
        if ( this.status != 0 )
        {
            return status;
        }
        else if ( this.isCompleted() )
        {
            return ProgramStageInstance.COMPLETED_STATUS;
        }
        else if ( this.getExecutionDate() != null )
        {
            return ProgramStageInstance.VISITED_STATUS;
        }
        else
        {
            // -------------------------------------------------------------
            // If a program stage is not provided even a day after its due
            // date, then that service is alerted red - because we are
            // getting late
            // -------------------------------------------------------------

            Calendar dueDateCalendar = Calendar.getInstance();
            dueDateCalendar.setTime( this.getDueDate() );
            dueDateCalendar.add( Calendar.DATE, 1 );

            if ( dueDateCalendar.getTime().before( new Date() ) )
            {
                return ProgramStageInstance.LATE_VISIT_STATUS;
            }

            return ProgramStageInstance.FUTURE_VISIT_STATUS;
        }
    }
}
