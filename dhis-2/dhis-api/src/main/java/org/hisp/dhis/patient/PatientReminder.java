package org.hisp.dhis.patient;

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
import org.hisp.dhis.user.UserGroup;

/**
 * @author Chau Thu Tran
 * @version PatientReminder.java 1:07:58 PM Sep 18, 2012 $
 */
public class PatientReminder
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = 3101502417481903219L;

    public static final String DUE_DATE_TO_COMPARE = "duedate";

    public static final String ENROLLEMENT_DATE_TO_COMPARE = "enrollmentdate";

    public static final String INCIDENT_DATE_TO_COMPARE = "dateofincident";

    public static final String TEMPLATE_MESSSAGE_PROGRAM_NAME = "{program-name}";

    public static final String TEMPLATE_MESSSAGE_PROGAM_STAGE_NAME = "{program-stage-name}";

    public static final String TEMPLATE_MESSSAGE_DUE_DATE = "{due-date}";

    public static final String TEMPLATE_MESSSAGE_ORGUNIT_NAME = "{orgunit-name}";

    public static final String TEMPLATE_MESSSAGE_DAYS_SINCE_DUE_DATE = "{days-since-due-date}";

    public static final String TEMPLATE_MESSSAGE_INCIDENT_DATE = "{incident-date}";

    public static final String TEMPLATE_MESSSAGE_ENROLLMENT_DATE = "{enrollement-date}";

    public static final String TEMPLATE_MESSSAGE_DAYS_SINCE_ENROLLMENT_DATE = "{days-since-enrollment-date}";

    public static final String TEMPLATE_MESSSAGE_DAYS_SINCE_INCIDENT_DATE = "{days-since-incident-date}";

    public static final int SEND_TO_PATIENT = 1;

    public static final int SEND_TO_ATTRIBUTE_TYPE_USERS = 2;

    public static final int SEND_TO_ORGUGNIT_REGISTERED = 3;

    public static final int SEND_TO_ALL_USERS_IN_ORGUGNIT_REGISTERED = 4;

    public static final int SEND_TO_USER_GROUP = 5;

    public static final int SEND_WHEN_TO_EMROLLEMENT = 1;

    public static final int SEND_WHEN_TO_C0MPLETED_EVENT = 2;

    public static final int SEND_WHEN_TO_C0MPLETED_PROGRAM = 3;

    public static final int MESSAGE_TYPE_DIRECT_SMS = 1;

    public static final int MESSAGE_TYPE_DHIS_MESSAGE = 2;

    public static final int MESSAGE_TYPE_BOTH = 3;

    private Integer daysAllowedSendMessage;

    private String templateMessage;

    private String dateToCompare;

    private Integer sendTo;

    private Integer whenToSend;

    private Integer messageType;

    private UserGroup userGroup;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientReminder()
    {
    }

    public PatientReminder( String name, Integer daysAllowedSendMessage, String templateMessage )
    {
        this.name = name;
        this.daysAllowedSendMessage = daysAllowedSendMessage;
        this.templateMessage = templateMessage;
    }

    public PatientReminder( String name, Integer daysAllowedSendMessage, String templateMessage, String dateToCompare,
        Integer sendTo, Integer whenToSend, Integer messageType )
    {
        this.name = name;
        this.daysAllowedSendMessage = daysAllowedSendMessage;
        this.templateMessage = templateMessage;
        this.dateToCompare = dateToCompare;
        this.sendTo = sendTo;
        this.whenToSend = whenToSend;
        this.messageType = messageType;
    }

    // TODO implement hashcode and equals

    // -------------------------------------------------------------------------
    // Getter && Setter
    // -------------------------------------------------------------------------
    
    public Integer getDaysAllowedSendMessage()
    {
        return daysAllowedSendMessage;
    }

    public void setDaysAllowedSendMessage( Integer daysAllowedSendMessage )
    {
        this.daysAllowedSendMessage = daysAllowedSendMessage;
    }

    public String getTemplateMessage()
    {
        return templateMessage;
    }

    public void setTemplateMessage( String templateMessage )
    {
        this.templateMessage = templateMessage;
    }

    public String getDateToCompare()
    {
        return dateToCompare;
    }

    public void setDateToCompare( String dateToCompare )
    {
        this.dateToCompare = dateToCompare;
    }

    public Integer getSendTo()
    {
        return sendTo;
    }

    public void setSendTo( Integer sendTo )
    {
        this.sendTo = sendTo;
    }

    public Integer getWhenToSend()
    {
        return whenToSend;
    }

    public void setWhenToSend( Integer whenToSend )
    {
        this.whenToSend = whenToSend;
    }

    public UserGroup getUserGroup()
    {
        return userGroup;
    }

    public void setUserGroup( UserGroup userGroup )
    {
        this.userGroup = userGroup;
    }

    public Integer getMessageType()
    {
        return messageType;
    }

    public void setMessageType( Integer messageType )
    {
        this.messageType = messageType;
    }
}
