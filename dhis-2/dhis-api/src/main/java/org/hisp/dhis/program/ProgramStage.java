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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.patient.PatientReminder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Abyot Asalefew
 */
@JacksonXmlRootElement( localName = "programStage", namespace = DxfNamespaces.DXF_2_0 )
public class ProgramStage
    extends BaseIdentifiableObject
{
    public static final String TYPE_DEFAULT = "default";

    public static final String TYPE_SECTION = "section";

    public static final String TYPE_CUSTOM = "custom";

    public static final String REPORT_DATE_TO_USE_INCIDENT_DATE = "dateOfIncident";

    public static final String REPORT_DATE_TO_USE_ENROLLMENT_DATE = "enrollmentDate";

    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6876401001559656214L;

    private String description;

    private int minDaysFromStart;

    private Boolean irregular = false;

    private Program program;

    private Set<ProgramStageDataElement> programStageDataElements = new HashSet<ProgramStageDataElement>();

    private Set<ProgramStageSection> programStageSections = new HashSet<ProgramStageSection>();

    private DataEntryForm dataEntryForm;

    private Integer standardInterval;

    private String reportDateDescription;

    private Set<PatientReminder> patientReminders = new HashSet<PatientReminder>();

    private Boolean autoGenerateEvent = true;

    private Boolean validCompleteOnly = false;

    private Boolean displayGenerateEventBox = true;

    private Boolean captureCoordinates = false;

    private Boolean blockEntryForm = false;

    /**
     * Enabled this property to show a pop-up for confirming Complete a program
     * after to complete a program-stage
     */
    private Boolean remindCompleted = false;

    private Boolean generatedByEnrollmentDate = false;

    private Boolean allowGenerateNextVisit = false;

    private Boolean openAfterEnrollment = false;

    private String reportDateToUse;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramStage()
    {
        setAutoFields();
    }

    public ProgramStage( String name, Program program )
    {
        this();
        this.name = name;
        this.program = program;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getGeneratedByEnrollmentDate()
    {
        return generatedByEnrollmentDate;
    }

    public void setGeneratedByEnrollmentDate( Boolean generatedByEnrollmentDate )
    {
        this.generatedByEnrollmentDate = generatedByEnrollmentDate;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getBlockEntryForm()
    {
        return blockEntryForm;
    }

    public void setBlockEntryForm( Boolean blockEntryForm )
    {
        this.blockEntryForm = blockEntryForm;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getRemindCompleted()
    {
        return remindCompleted;
    }

    public void setRemindCompleted( Boolean remindCompleted )
    {
        this.remindCompleted = remindCompleted;
    }

    public Set<PatientReminder> getPatientReminders()
    {
        return patientReminders;
    }

    public void setPatientReminders( Set<PatientReminder> patientReminders )
    {
        this.patientReminders = patientReminders;
    }

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }

    public void setDataEntryForm( DataEntryForm dataEntryForm )
    {
        this.dataEntryForm = dataEntryForm;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDescription()
    {
        return description;
    }

    @JsonProperty( value = "programStageSections" )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "programStageSections", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "programStageSection", namespace = DxfNamespaces.DXF_2_0 )
    public Set<ProgramStageSection> getProgramStageSections()
    {
        return programStageSections;
    }

    public void setProgramStageSections( Set<ProgramStageSection> programStageSections )
    {
        this.programStageSections = programStageSections;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Integer getStandardInterval()
    {
        return standardInterval;
    }

    public void setStandardInterval( Integer standardInterval )
    {
        this.standardInterval = standardInterval;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @JsonProperty( "repeatable" )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( localName = "repeatable", namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getIrregular()
    {
        return irregular;
    }

    public void setIrregular( Boolean irregular )
    {
        this.irregular = irregular;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public int getMinDaysFromStart()
    {
        return minDaysFromStart;
    }

    public void setMinDaysFromStart( int minDaysFromStart )
    {
        this.minDaysFromStart = minDaysFromStart;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "programStageDataElements", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "programStageDataElement", namespace = DxfNamespaces.DXF_2_0 )
    public Set<ProgramStageDataElement> getProgramStageDataElements()
    {
        return programStageDataElements;
    }

    public void setProgramStageDataElements( Set<ProgramStageDataElement> programStageDataElements )
    {
        this.programStageDataElements = programStageDataElements;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getReportDateDescription()
    {
        return reportDateDescription;
    }

    public void setReportDateDescription( String reportDateDescription )
    {
        this.reportDateDescription = reportDateDescription;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getAutoGenerateEvent()
    {
        return autoGenerateEvent;
    }

    public void setAutoGenerateEvent( Boolean autoGenerateEvent )
    {
        this.autoGenerateEvent = autoGenerateEvent;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getValidCompleteOnly()
    {
        return validCompleteOnly;
    }

    public void setValidCompleteOnly( Boolean validCompleteOnly )
    {
        this.validCompleteOnly = validCompleteOnly;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getDisplayGenerateEventBox()
    {
        return displayGenerateEventBox;
    }

    public void setDisplayGenerateEventBox( Boolean displayGenerateEventBox )
    {
        this.displayGenerateEventBox = displayGenerateEventBox;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDataEntryType()
    {
        if ( dataEntryForm != null )
        {
            return TYPE_CUSTOM;
        }

        if ( programStageSections.size() > 0 )
        {
            return TYPE_SECTION;
        }

        return TYPE_DEFAULT;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDefaultTemplateMessage()
    {
        return "Dear {person-name}, please come to your appointment on {program-stage-name} at {due-date}";
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getCaptureCoordinates()
    {
        return captureCoordinates;
    }

    public void setCaptureCoordinates( Boolean captureCoordinates )
    {
        this.captureCoordinates = captureCoordinates;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getAllowGenerateNextVisit()
    {
        return allowGenerateNextVisit;
    }

    public void setAllowGenerateNextVisit( Boolean allowGenerateNextVisit )
    {
        this.allowGenerateNextVisit = allowGenerateNextVisit;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getOpenAfterEnrollment()
    {
        return openAfterEnrollment;
    }

    public void setOpenAfterEnrollment( Boolean openAfterEnrollment )
    {
        this.openAfterEnrollment = openAfterEnrollment;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getReportDateToUse()
    {
        return reportDateToUse;
    }

    public void setReportDateToUse( String reportDateToUse )
    {
        this.reportDateToUse = reportDateToUse;
    }

}
