package org.hisp.dhis.mobile.service;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.api.mobile.ActivityReportingService;
import org.hisp.dhis.api.mobile.NotAllowedException;
import org.hisp.dhis.api.mobile.PatientMobileSettingService;
import org.hisp.dhis.api.mobile.model.Activity;
import org.hisp.dhis.api.mobile.model.ActivityPlan;
import org.hisp.dhis.api.mobile.model.ActivityValue;
import org.hisp.dhis.api.mobile.model.Beneficiary;
import org.hisp.dhis.api.mobile.model.DataValue;
import org.hisp.dhis.api.mobile.model.PatientAttribute;
import org.hisp.dhis.api.mobile.model.Task;
import org.hisp.dhis.api.mobile.model.LWUITmodel.LostEvent;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Notification;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Section;
import org.hisp.dhis.api.mobile.model.comparator.ActivityComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.message.Message;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientMobileSetting;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.program.ProgramStageSectionService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.program.comparator.ProgramStageInstanceVisitDateComparator;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.sms.SmsSender;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

public class ActivityReportingServiceImpl
    implements ActivityReportingService
{
    private static final String PROGRAM_STAGE_UPLOADED = "program_stage_uploaded";

    private static final String PROGRAM_STAGE_SECTION_UPLOADED = "program_stage_section_uploaded";

    private static final String SINGLE_EVENT_UPLOADED = "single_event_uploaded";

    private ActivityComparator activityComparator = new ActivityComparator();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    private PatientService patientService;

    private PatientAttributeValueService patientAttValueService;

    private PatientAttributeService patientAttService;

    private PatientDataValueService dataValueService;

    private PatientMobileSettingService patientMobileSettingService;

    private ProgramStageSectionService programStageSectionService;

    private ProgramInstanceService programInstanceService;

    private RelationshipService relationshipService;

    private RelationshipTypeService relationshipTypeService;

    private DataElementService dataElementService;

    private PatientDataValueService patientDataValueService;

    private ProgramService programService;

    private ProgramStageService programStageService;

    private CurrentUserService currentUserService;

    private MessageService messageService;

    private SmsSender smsSender;

    private PatientAttributeService patientAttributeService;

    private Integer patientId;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    @Required
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    @Required
    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    public void setSmsSender( SmsSender smsSender )
    {
        this.smsSender = smsSender;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    @Required
    public void setPatientAttValueService( PatientAttributeValueService patientAttValueService )
    {
        this.patientAttValueService = patientAttValueService;
    }

    @Required
    public void setPatientAttService( PatientAttributeService patientAttService )
    {
        this.patientAttService = patientAttService;
    }

    @Required
    public void setDataValueService( org.hisp.dhis.patientdatavalue.PatientDataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    @Required
    public void setPatientMobileSettingService( PatientMobileSettingService patientMobileSettingService )
    {
        this.patientMobileSettingService = patientMobileSettingService;
    }

    public void setSetting( PatientMobileSetting setting )
    {
        this.setting = setting;
    }

    public void setGroupByAttribute( org.hisp.dhis.patient.PatientAttribute groupByAttribute )
    {
        this.groupByAttribute = groupByAttribute;
    }

    @Required
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    @Required
    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    @Required
    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    @Required
    public void setProgramStageSectionService( ProgramStageSectionService programStageSectionService )
    {
        this.programStageSectionService = programStageSectionService;
    }

    @Required
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    @Required
    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    @Required
    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // MobileDataSetService
    // -------------------------------------------------------------------------

    private PatientMobileSetting setting;

    private org.hisp.dhis.patient.PatientAttribute groupByAttribute;

    @Override
    public ActivityPlan getCurrentActivityPlan( OrganisationUnit unit, String localeString )
    {
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, 30 );

        long upperBound = cal.getTime().getTime();

        cal.add( Calendar.DATE, -60 );
        long lowerBound = cal.getTime().getTime();

        List<Activity> items = new ArrayList<Activity>();
        Collection<Patient> patients = patientService.getPatients( unit, 0, Integer.MAX_VALUE );

        for ( Patient patient : patients )
        {
            for ( ProgramStageInstance programStageInstance : programStageInstanceService.getProgramStageInstances(
                patient, false ) )
            {
                if ( programStageInstance.getDueDate().getTime() >= lowerBound
                    && programStageInstance.getDueDate().getTime() <= upperBound )
                {
                    items.add( getActivity( programStageInstance, false ) );
                }
            }
        }

        this.setGroupByAttribute( patientAttService.getPatientAttributeByGroupBy() );

        if ( items.isEmpty() )
        {
            return null;
        }

        Collections.sort( items, activityComparator );

        return new ActivityPlan( items );
    }

    @Override
    public ActivityPlan getAllActivityPlan( OrganisationUnit unit, String localeString )
    {

        List<Activity> items = new ArrayList<Activity>();
        Collection<Patient> patients = patientService.getPatients( unit, 0, Integer.MAX_VALUE );

        for ( Patient patient : patients )
        {
            for ( ProgramStageInstance programStageInstance : programStageInstanceService.getProgramStageInstances(
                patient, false ) )
            {
                items.add( getActivity( programStageInstance, false ) );
            }
        }

        this.setGroupByAttribute( patientAttService.getPatientAttributeByGroupBy() );

        if ( items.isEmpty() )
        {
            return null;
        }

        Collections.sort( items, activityComparator );
        return new ActivityPlan( items );
    }

    // -------------------------------------------------------------------------
    // DataValueService
    // -------------------------------------------------------------------------

    @Override
    public void saveActivityReport( OrganisationUnit unit, ActivityValue activityValue, Integer programStageSectionId )
        throws NotAllowedException
    {

        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( activityValue
            .getProgramInstanceId() );
        if ( programStageInstance == null )
        {
            throw NotAllowedException.INVALID_PROGRAM_STAGE;
        }

        programStageInstance.getProgramStage();
        Collection<org.hisp.dhis.dataelement.DataElement> dataElements = new ArrayList<org.hisp.dhis.dataelement.DataElement>();

        ProgramStageSection programStageSection = programStageSectionService
            .getProgramStageSection( programStageSectionId );

        if ( programStageSectionId != 0 )
        {
            for ( ProgramStageDataElement de : programStageSection.getProgramStageDataElements() )
            {
                dataElements.add( de.getDataElement() );
            }
        }
        else
        {
            for ( ProgramStageDataElement de : programStageInstance.getProgramStage().getProgramStageDataElements() )
            {
                dataElements.add( de.getDataElement() );
            }
        }

        programStageInstance.getProgramStage().getProgramStageDataElements();
        Collection<Integer> dataElementIds = new ArrayList<Integer>( activityValue.getDataValues().size() );

        for ( DataValue dv : activityValue.getDataValues() )
        {
            dataElementIds.add( dv.getId() );
        }

        if ( dataElements.size() != dataElementIds.size() )
        {
            throw NotAllowedException.INVALID_PROGRAM_STAGE;
        }

        Map<Integer, org.hisp.dhis.dataelement.DataElement> dataElementMap = new HashMap<Integer, org.hisp.dhis.dataelement.DataElement>();
        for ( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
        {
            if ( !dataElementIds.contains( dataElement.getId() ) )
            {
                throw NotAllowedException.INVALID_PROGRAM_STAGE;
            }
            dataElementMap.put( dataElement.getId(), dataElement );
        }

        // Set ProgramStageInstance to completed
        if ( programStageSectionId == 0 )
        {
            programStageInstance.setCompleted( true );
            programStageInstanceService.updateProgramStageInstance( programStageInstance );
        }

        // Everything is fine, hence save
        saveDataValues( activityValue, programStageInstance, dataElementMap );

    }

    @Override
    public String findPatient( String keyword, int orgUnitId )
        throws NotAllowedException
    {
        Collection<Patient> patients = patientAttValueService.getPatient( null, keyword );

        if ( patients.size() == 0 )
        {
            throw NotAllowedException.NO_BENEFICIARY_FOUND;
        }

        Collection<org.hisp.dhis.patient.PatientAttribute> displayAttributes = patientAttributeService
            .getPatientAttributesDisplayed( true );
        String resultSet = "";

        for ( Patient patient : patients )
        {
            resultSet += patient.getId() + "/";
            String attText = "";
            for ( org.hisp.dhis.patient.PatientAttribute displayAttribute : displayAttributes )
            {
                PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient,
                    displayAttribute );
                attText += value + " ";
            }
            attText = attText.trim();
            resultSet += attText + "$";
        }

        return resultSet;
    }

    @Override
    public String saveProgramStage( org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage,
        int patientId, int orgUnitId )
        throws NotAllowedException
    {
        if ( mobileProgramStage.isSingleEvent() )
        {
            Patient patient = patientService.getPatient( patientId );
            ProgramStageInstance prStageInstance = programStageInstanceService
                .getProgramStageInstance( mobileProgramStage.getId() );
            ProgramStage programStage = programStageService.getProgramStage( prStageInstance.getProgramStage().getId() );
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

            // ---------------------------------------------------------------------
            // Add a new program-instance
            // ---------------------------------------------------------------------
            ProgramInstance programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( new Date() );
            programInstance.setDateOfIncident( new Date() );
            programInstance.setProgram( programStage.getProgram() );
            programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
            programInstance.setPatient( patient );

            programInstanceService.addProgramInstance( programInstance );

            // ---------------------------------------------------------------------
            // Add a new program-stage-instance
            // ---------------------------------------------------------------------

            ProgramStageInstance programStageInstance = new ProgramStageInstance();
            programStageInstance.setProgramInstance( programInstance );
            programStageInstance.setProgramStage( programStage );
            programStageInstance.setDueDate( new Date() );
            programStageInstance.setExecutionDate( new Date() );
            programStageInstance.setOrganisationUnit( organisationUnit );
            programStageInstance.setCompleted( true );
            programStageInstanceService.addProgramStageInstance( programStageInstance );

            // ---------------------------------------------------------------------
            // Save value
            // ---------------------------------------------------------------------

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> dataElements = mobileProgramStage
                .getDataElements();

            for ( int i = 0; i < dataElements.size(); i++ )
            {
                DataElement dataElement = dataElementService.getDataElement( dataElements.get( i ).getId() );

                String value = dataElements.get( i ).getValue();

                if ( dataElement.getType().equalsIgnoreCase( "date" ) && !value.trim().equals( "" ) )
                {
                    value = PeriodUtil.convertDateFormat( value );
                }

                PatientDataValue patientDataValue = new PatientDataValue();
                patientDataValue.setDataElement( dataElement );

                patientDataValue.setValue( value );
                patientDataValue.setProgramStageInstance( programStageInstance );
                patientDataValue.setTimestamp( new Date() );
                patientDataValueService.savePatientDataValue( patientDataValue );

            }

            return SINGLE_EVENT_UPLOADED;

        }
        else
        {
            ProgramStageInstance programStageInstance = programStageInstanceService
                .getProgramStageInstance( mobileProgramStage.getId() );

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> dataElements = mobileProgramStage
                .getDataElements();

            for ( int i = 0; i < dataElements.size(); i++ )
            {
                DataElement dataElement = dataElementService.getDataElement( dataElements.get( i ).getId() );
                String value = dataElements.get( i ).getValue();

                if ( dataElement.getType().equalsIgnoreCase( "date" ) && !value.trim().equals( "" ) )
                {
                    value = PeriodUtil.convertDateFormat( value );
                }

                PatientDataValue previousPatientDataValue = patientDataValueService.getPatientDataValue(
                    programStageInstance, dataElement );

                if ( previousPatientDataValue == null )
                {
                    PatientDataValue patientDataValue = new PatientDataValue( programStageInstance, dataElement,
                        new Date(), value );
                    patientDataValueService.savePatientDataValue( patientDataValue );
                }
                else
                {
                    previousPatientDataValue.setValue( value );
                    previousPatientDataValue.setTimestamp( new Date() );
                    previousPatientDataValue.setProvidedElsewhere( false );
                    patientDataValueService.updatePatientDataValue( previousPatientDataValue );
                }

            }

            if ( PeriodUtil.stringToDate( mobileProgramStage.getReportDate() ) != null )
            {
                programStageInstance.setExecutionDate( PeriodUtil.stringToDate( mobileProgramStage.getReportDate() ) );
            }
            else
            {
                programStageInstance.setExecutionDate( new Date() );
            }

            if ( programStageInstance.getProgramStage().getProgramStageDataElements().size() > dataElements.size() )
            {
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
                return PROGRAM_STAGE_SECTION_UPLOADED;
            }
            else
            {
                programStageInstance.setCompleted( mobileProgramStage.isCompleted() );

                // check if any compulsory value is null
                for ( int i = 0; i < dataElements.size(); i++ )
                {
                    if ( dataElements.get( i ).isCompulsory() == true )
                    {
                        if ( dataElements.get( i ).getValue() == null )
                        {
                            programStageInstance.setCompleted( false );
                            // break;
                            throw NotAllowedException.INVALID_PROGRAM_STAGE;
                        }
                    }
                }
                programStageInstanceService.updateProgramStageInstance( programStageInstance );

                // check if all belonged program stage are completed
                if ( isAllProgramStageFinished( programStageInstance ) == true )
                {
                    ProgramInstance programInstance = programStageInstance.getProgramInstance();
                    programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
                    programInstanceService.updateProgramInstance( programInstance );
                }
                if ( mobileProgramStage.isRepeatable() )
                {
                    Date nextDate = DateUtils.getDateAfterAddition( new Date(),
                        mobileProgramStage.getStandardInterval() );

                    return PROGRAM_STAGE_UPLOADED + "$" + PeriodUtil.dateToString( nextDate );
                }
                else
                {
                    return PROGRAM_STAGE_UPLOADED;
                }
            }
        }
    }

    private boolean isAllProgramStageFinished( ProgramStageInstance programStageInstance )
    {
        ProgramInstance programInstance = programStageInstance.getProgramInstance();
        Collection<ProgramStageInstance> programStageInstances = programInstance.getProgramStageInstances();
        if ( programStageInstances != null )
        {
            Iterator<ProgramStageInstance> iterator = programStageInstances.iterator();

            while ( iterator.hasNext() )
            {
                ProgramStageInstance each = iterator.next();
                if ( !each.isCompleted() )
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient enrollProgram( String enrollInfo, Date incidentDate )
        throws NotAllowedException
    {
        String[] enrollProgramInfo = enrollInfo.split( "-" );
        int patientId = Integer.parseInt( enrollProgramInfo[0] );
        int programId = Integer.parseInt( enrollProgramInfo[1] );

        Patient patient = patientService.getPatient( patientId );
        Program program = programService.getProgram( programId );

        ProgramInstance programInstance = new ProgramInstance();
        programInstance.setEnrollmentDate( new Date() );
        programInstance.setDateOfIncident( incidentDate );
        programInstance.setProgram( program );
        programInstance.setPatient( patient );
        programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );
        programInstanceService.addProgramInstance( programInstance );
        for ( ProgramStage programStage : program.getProgramStages() )
        {
            if ( programStage.getAutoGenerateEvent() )
            {
                ProgramStageInstance programStageInstance = new ProgramStageInstance();
                programStageInstance.setProgramInstance( programInstance );
                programStageInstance.setProgramStage( programStage );
                Date dateCreatedEvent = new Date();
                if ( programStage.getGeneratedByEnrollmentDate() )
                {
                    // dateCreatedEvent = sdf.parseDateTime( enrollmentDate
                    // ).toDate();
                }
                Date dueDate = DateUtils.getDateAfterAddition( dateCreatedEvent, programStage.getMinDaysFromStart() );

                programStageInstance.setDueDate( dueDate );

                if ( program.isSingleEvent() )
                {
                    programStageInstance.setExecutionDate( dueDate );
                }

                programStageInstanceService.addProgramStageInstance( programStageInstance );
                programInstance.getProgramStageInstances().add( programStageInstance );
            }
        }
        programInstanceService.updateProgramInstance( programInstance );
        patient.getProgramInstances().add( programInstance );
        patientService.updatePatient( patient );

        patient = patientService.getPatient( patientId );

        return getPatientModel( patient );
    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private Activity getActivity( ProgramStageInstance instance, boolean late )
    {

        Activity activity = new Activity();
        Patient patient = instance.getProgramInstance().getPatient();

        activity.setBeneficiary( getBeneficiaryModel( patient ) );
        activity.setDueDate( instance.getDueDate() );
        activity.setTask( getTask( instance ) );
        activity.setLate( late );
        activity.setExpireDate( DateUtils.getDateAfterAddition( instance.getDueDate(), 30 ) );

        return activity;
    }

    private Task getTask( ProgramStageInstance instance )
    {
        if ( instance == null )
            return null;

        Task task = new Task();
        task.setCompleted( instance.isCompleted() );
        task.setId( instance.getId() );
        task.setProgramStageId( instance.getProgramStage().getId() );
        task.setProgramId( instance.getProgramInstance().getProgram().getId() );
        return task;
    }

    private Beneficiary getBeneficiaryModel( Patient patient )
    {
        Beneficiary beneficiary = new Beneficiary();
        List<PatientAttribute> patientAtts = new ArrayList<PatientAttribute>();
        List<org.hisp.dhis.patient.PatientAttribute> atts;

        beneficiary.setId( patient.getId() );
        beneficiary.setName( patient.getName() );

        this.setSetting( getSettings() );

        if ( setting != null )
        {
            atts = setting.getPatientAttributes();
            for ( org.hisp.dhis.patient.PatientAttribute each : atts )
            {
                PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient, each );
                if ( value != null )
                {
                    // patientAtts.add( new PatientAttribute( each.getName(),
                    // value.getValue(), each.getValueType(),
                    // new ArrayList<String>() ) );
                }
            }

        }

        // Set attribute which is used to group beneficiary on mobile (only if
        // there is attribute which is set to be group factor)
        PatientAttribute beneficiaryAttribute = null;

        if ( groupByAttribute != null )
        {
            beneficiaryAttribute = new PatientAttribute();
            beneficiaryAttribute.setName( groupByAttribute.getName() );
            PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient, groupByAttribute );
            beneficiaryAttribute.setValue( value == null ? "Unknown" : value.getValue() );
            beneficiary.setGroupAttribute( beneficiaryAttribute );
        }

        beneficiary.setPatientAttValues( patientAtts );
        return beneficiary;
    }

    // get patient model for LWUIT
    private org.hisp.dhis.api.mobile.model.LWUITmodel.Patient getPatientModel( Patient patient )
    {
        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientModel = new org.hisp.dhis.api.mobile.model.LWUITmodel.Patient();
        List<PatientAttribute> patientAtts = new ArrayList<PatientAttribute>();

        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance> mobileProgramInstanceList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance>();

        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance> mobileCompletedProgramInstanceList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance>();

        patientModel.setId( patient.getId() );

        if ( patient.getOrganisationUnit() != null )
        {
            patientModel.setOrganisationUnitName( patient.getOrganisationUnit().getName() );
        }

        this.setSetting( getSettings() );

        List<PatientAttributeValue> atts = new ArrayList<PatientAttributeValue>(
            patientAttValueService.getPatientAttributeValues( patient ) );

        Set<org.hisp.dhis.patient.PatientAttribute> displayAttributes = new HashSet<org.hisp.dhis.patient.PatientAttribute>(
            patientAttributeService.getPatientAttributesDisplayed( true ) );

        for ( PatientAttributeValue value : atts )
        {
            if ( value != null )
            {
                PatientAttribute patientAttribute = new PatientAttribute( value.getPatientAttribute().getName(),
                    value.getValue(), value.getPatientAttribute().getValueType(), false, new ArrayList<String>() );
                if ( displayAttributes.contains( value.getPatientAttribute() ) )
                {
                    patientAttribute.setDisplayedInList( true );
                }
                patientAtts.add( patientAttribute );
            }
        }

        patientModel.setAttributes( patientAtts );

        // Set current programs
        List<ProgramInstance> listOfProgramInstance = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( patient, ProgramInstance.STATUS_ACTIVE ) );

        if ( listOfProgramInstance.size() > 0 )
        {
            for ( ProgramInstance each : listOfProgramInstance )
            {
                mobileProgramInstanceList.add( getMobileProgramInstance( each ) );
            }
        }
        patientModel.setEnrollmentPrograms( mobileProgramInstanceList );

        // Set completed programs
        List<ProgramInstance> listOfCompletedProgramInstance = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( patient, ProgramInstance.STATUS_COMPLETED ) );

        if ( listOfCompletedProgramInstance.size() > 0 )
        {
            for ( ProgramInstance each : listOfCompletedProgramInstance )
            {
                mobileCompletedProgramInstanceList.add( getMobileProgramInstance( each ) );
            }
        }

        patientModel.setCompletedPrograms( mobileCompletedProgramInstanceList );

        // Set Relationship
        List<Relationship> relationships = new ArrayList<Relationship>(
            relationshipService.getRelationshipsForPatient( patient ) );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship> relationshipList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship>();

        for ( Relationship eachRelationship : relationships )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship relationshipMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship();
            relationshipMobile.setId( eachRelationship.getId() );
            if ( eachRelationship.getPatientA().getId() == patient.getId() )
            {
                relationshipMobile.setName( eachRelationship.getRelationshipType().getaIsToB() );
                relationshipMobile.setPersonBName( eachRelationship.getPatientB().getName() );
                relationshipMobile.setPersonBId( eachRelationship.getPatientB().getId() );
            }
            else
            {
                relationshipMobile.setName( eachRelationship.getRelationshipType().getbIsToA() );
                relationshipMobile.setPersonBName( eachRelationship.getPatientA().getName() );
                relationshipMobile.setPersonBId( eachRelationship.getPatientA().getId() );
            }
            relationshipList.add( relationshipMobile );
        }
        patientModel.setRelationships( relationshipList );

        // Set available enrollment relationships
        // List<RelationshipType> enrollmentRelationshipList = new
        // ArrayList<RelationshipType>(
        // relationshipTypeService.getAllRelationshipTypes() );
        // List<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship>
        // enrollmentRelationshipMobileList = new
        // ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship>();
        // for ( RelationshipType enrollmentRelationship :
        // enrollmentRelationshipList )
        // {
        // org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship
        // enrollmentRelationshipMobile = new
        // org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship();
        // enrollmentRelationshipMobile.setId( enrollmentRelationship.getId() );
        // enrollmentRelationshipMobile.setName(
        // enrollmentRelationship.getName() );
        // enrollmentRelationshipMobile.setaIsToB(
        // enrollmentRelationship.getaIsToB() );
        // enrollmentRelationshipMobile.setbIsToA(
        // enrollmentRelationship.getbIsToA() );
        // enrollmentRelationshipMobileList.add( enrollmentRelationshipMobile );
        // }
        // patientModel.setRelationships( enrollmentRelationshipMobileList );
        return patientModel;
    }

    private org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance getMobileProgramInstance(
        ProgramInstance programInstance )
    {
        org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance mobileProgramInstance = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance();

        mobileProgramInstance.setId( programInstance.getId() );
        mobileProgramInstance.setName( programInstance.getProgram().getName() );
        mobileProgramInstance.setStatus( programInstance.getStatus() );
        mobileProgramInstance.setDateOfEnrollment( PeriodUtil.dateToString( programInstance.getEnrollmentDate() ) );
        mobileProgramInstance.setDateOfIncident( PeriodUtil.dateToString( programInstance.getDateOfIncident() ) );
        mobileProgramInstance.setPatientId( programInstance.getPatient().getId() );
        mobileProgramInstance.setProgramId( programInstance.getProgram().getId() );
        mobileProgramInstance.setProgramStageInstances( getMobileProgramStages( programInstance ) );
        return mobileProgramInstance;
    }

    private List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> getMobileProgramStages(
        ProgramInstance programInstance )
    {
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> mobileProgramStages = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage>();

        for ( ProgramStageInstance eachProgramStageInstance : programInstance.getProgramStageInstances() )
        {
            // only for Mujhu database, because there is null program stage
            // instance. This condition should be removed in the future
            if ( eachProgramStageInstance != null )
            {
                ProgramStage programStage = eachProgramStageInstance.getProgramStage();

                org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage();
                List<org.hisp.dhis.api.mobile.model.LWUITmodel.Section> mobileSections = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Section>();
                mobileProgramStage.setId( eachProgramStageInstance.getId() );
                /* mobileProgramStage.setName( eachProgramStage.getName() ); */
                mobileProgramStage.setName( programStage.getName() );

                // get report date
                if ( eachProgramStageInstance.getExecutionDate() != null )
                {
                    mobileProgramStage.setReportDate( PeriodUtil.dateToString( eachProgramStageInstance
                        .getExecutionDate() ) );
                }
                else
                {
                    mobileProgramStage.setReportDate( "" );
                }

                if ( programStage.getReportDateDescription() == null )
                {
                    mobileProgramStage.setReportDateDescription( "Report Date" );
                }
                else
                {
                    mobileProgramStage.setReportDateDescription( programStage.getReportDateDescription() );
                }

                // is repeatable
                mobileProgramStage.setRepeatable( programStage.getIrregular() );

                if ( programStage.getStandardInterval() == null )
                {
                    mobileProgramStage.setStandardInterval( 0 );
                }
                else
                {
                    mobileProgramStage.setStandardInterval( programStage.getStandardInterval() );
                }

                // is completed
                /*
                 * mobileProgramStage.setCompleted(
                 * checkIfProgramStageCompleted( patient,
                 * programInstance.getProgram(), programStage ) );
                 */
                mobileProgramStage.setCompleted( eachProgramStageInstance.isCompleted() );

                // is single event
                mobileProgramStage.setSingleEvent( programInstance.getProgram().isSingleEvent() );

                // Set all data elements
                mobileProgramStage.setDataElements( getDataElementsForMobile( programStage, eachProgramStageInstance ) );

                // Set all program sections
                if ( programStage.getProgramStageSections().size() > 0 )
                {
                    for ( ProgramStageSection eachSection : programStage.getProgramStageSections() )
                    {
                        org.hisp.dhis.api.mobile.model.LWUITmodel.Section mobileSection = new org.hisp.dhis.api.mobile.model.LWUITmodel.Section();
                        mobileSection.setId( eachSection.getId() );
                        mobileSection.setName( eachSection.getName() );

                        // Set all data elements' id, then we could have full
                        // from
                        // data element list of program stage
                        List<Integer> dataElementIds = new ArrayList<Integer>();
                        for ( ProgramStageDataElement eachPogramStageDataElement : eachSection
                            .getProgramStageDataElements() )
                        {
                            dataElementIds.add( eachPogramStageDataElement.getDataElement().getId() );
                        }
                        mobileSection.setDataElementIds( dataElementIds );
                        mobileSections.add( mobileSection );
                    }
                }
                mobileProgramStage.setSections( mobileSections );

                mobileProgramStages.add( mobileProgramStage );
            }
        }
        return mobileProgramStages;
    }

    private List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> getDataElementsForMobile(
        ProgramStage programStage, ProgramStageInstance programStageInstance )
    {
        List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>(
            programStage.getProgramStageDataElements() );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> mobileDataElements = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement>();
        for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement mobileDataElement = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement();
            mobileDataElement.setId( programStageDataElement.getDataElement().getId() );

            String dataElementName;

            if ( programStageDataElement.getDataElement().getFormName() != null
                && !programStageDataElement.getDataElement().getFormName().trim().equals( "" ) )
            {
                dataElementName = programStageDataElement.getDataElement().getFormName();
            }
            else
            {
                dataElementName = programStageDataElement.getDataElement().getName();
            }

            mobileDataElement.setName( dataElementName );
            mobileDataElement.setType( programStageDataElement.getDataElement().getType() );

            // problem
            mobileDataElement.setCompulsory( programStageDataElement.isCompulsory() );

            mobileDataElement.setNumberType( programStageDataElement.getDataElement().getNumberType() );

            // Value
            PatientDataValue patientDataValue = dataValueService.getPatientDataValue( programStageInstance,
                programStageDataElement.getDataElement() );
            if ( patientDataValue != null )
            {
                // Convert to standard date format before send to client
                if ( programStageDataElement.getDataElement().getType().equalsIgnoreCase( "date" )
                    && !patientDataValue.equals( "" ) )
                {
                    mobileDataElement.setValue( PeriodUtil.convertDateFormat( patientDataValue.getValue() ) );
                }
                else
                {
                    mobileDataElement.setValue( patientDataValue.getValue() );
                }
            }
            else
            {
                mobileDataElement.setValue( null );
            }

            // Option set
            if ( programStageDataElement.getDataElement().getOptionSet() != null )
            {
                mobileDataElement.setOptionSet( ModelMapping.getOptionSet( programStageDataElement.getDataElement() ) );
            }
            else
            {
                mobileDataElement.setOptionSet( null );
            }

            // Category Option Combo
            if ( programStageDataElement.getDataElement().getCategoryCombo() != null )
            {
                mobileDataElement.setCategoryOptionCombos( ModelMapping
                    .getCategoryOptionCombos( programStageDataElement.getDataElement() ) );
            }
            else
            {
                mobileDataElement.setCategoryOptionCombos( null );
            }
            mobileDataElements.add( mobileDataElement );
        }
        return mobileDataElements;
    }

    private PatientMobileSetting getSettings()
    {
        PatientMobileSetting setting = null;

        Collection<PatientMobileSetting> currentSetting = patientMobileSettingService.getCurrentSetting();
        if ( currentSetting != null && !currentSetting.isEmpty() )
            setting = currentSetting.iterator().next();
        return setting;
    }

    private List<Program> generateEnrollmentProgramList( Patient patient )
    {
        List<Program> programs = new ArrayList<Program>();

        for ( Program program : programService.getPrograms( patient.getOrganisationUnit() ) )
        {
            if ( (program.isSingleEvent() && program.isRegistration()) || !program.isSingleEvent() )
            {
                // wrong here
                if ( programInstanceService.getProgramInstances( patient, program ).size() == 0 )
                {
                    programs.add( program );
                }
                else
                {
                    // TODO handle
                }
            }
        }

        return programs;
    }

    private boolean isNumber( String value )
    {
        try
        {
            Double.parseDouble( value );
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
        return true;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient addRelationship(
        org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship enrollmentRelationship, int orgUnitId )
        throws NotAllowedException
    {
        Patient patientB;
        if ( enrollmentRelationship.getPersonBId() != 0 )
        {
            patientB = patientService.getPatient( enrollmentRelationship.getPersonBId() );
        }
        else
        {
            List<Patient> patients = new ArrayList<Patient>();

            // remove the own searcher
            patients = removeIfDuplicated( patients, enrollmentRelationship.getPersonAId() );

            if ( patients.size() > 1 )
            {
                String patientsInfo = "";

                for ( Patient each : patients )
                {
                    patientsInfo += each.getId() + "/" + each.getName() + "$";
                }

                throw new NotAllowedException( patientsInfo );
            }
            else if ( patients.size() == 0 )
            {
                throw NotAllowedException.NO_BENEFICIARY_FOUND;
            }
            else
            {
                patientB = patients.get( 0 );
            }
        }
        Patient patientA = patientService.getPatient( enrollmentRelationship.getPersonAId() );
        RelationshipType relationshipType = relationshipTypeService
            .getRelationshipType( enrollmentRelationship.getId() );

        Relationship relationship = new Relationship();
        relationship.setRelationshipType( relationshipType );
        if ( enrollmentRelationship.getChosenRelationship().equals( relationshipType.getaIsToB() ) )
        {
            relationship.setPatientA( patientA );
            relationship.setPatientB( patientB );
        }
        else
        {
            relationship.setPatientA( patientB );
            relationship.setPatientB( patientA );
        }
        relationshipService.saveRelationship( relationship );
        // return getPatientModel( orgUnitId, patientA );
        return getPatientModel( patientA );
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Program getAllProgramByOrgUnit( int orgUnitId, String programType )
        throws NotAllowedException
    {
        String programsInfo = "";

        int programTypeInt = Integer.valueOf( programType );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        List<Program> tempPrograms = null;

        if ( programTypeInt == Program.SINGLE_EVENT_WITHOUT_REGISTRATION )
        {
            tempPrograms = new ArrayList<Program>(
                programService.getProgramsByCurrentUser( Program.SINGLE_EVENT_WITHOUT_REGISTRATION ) );
        }
        else if ( programTypeInt == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
        {
            tempPrograms = new ArrayList<Program>(
                programService.getProgramsByCurrentUser( Program.MULTIPLE_EVENTS_WITH_REGISTRATION ) );
        }

        List<Program> programs = new ArrayList<Program>();

        for ( Program program : tempPrograms )
        {
            if ( program.getOrganisationUnits().contains( organisationUnit ) )
            {
                programs.add( program );
            }
        }

        if ( programs.size() != 0 )
        {
            if ( programs.size() == 1 )
            {
                Program program = programs.get( 0 );

                return getMobileProgramWithoutData( program );
            }
            else
            {
                for ( Program program : programs )
                {
                    if ( program.getOrganisationUnits().contains( organisationUnit ) )
                    {
                        programsInfo += program.getId() + "/" + program.getName() + "$";
                    }
                }
                throw new NotAllowedException( programsInfo );
            }
        }
        else
        {
            throw NotAllowedException.NO_PROGRAM_FOUND;
        }
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Program findProgram( String programInfo )
        throws NotAllowedException
    {
        if ( isNumber( programInfo ) == false )
        {
            return null;
        }
        else
        {
            Program program = programService.getProgram( Integer.parseInt( programInfo ) );
            if ( program.isSingleEvent() )
            {
                return getMobileProgramWithoutData( program );
            }
            else
            {
                return null;
            }
        }
    }

    // If the return program is anonymous, the client side will show the entry
    // form as normal
    // If the return program is not anonymous, it is still OK because in client
    // side, we only need name and id
    private org.hisp.dhis.api.mobile.model.LWUITmodel.Program getMobileProgramWithoutData( Program program )
    {
        Comparator<ProgramStageDataElement> OrderBySortOrder = new Comparator<ProgramStageDataElement>()
        {
            public int compare( ProgramStageDataElement i1, ProgramStageDataElement i2 )
            {
                return i1.getSortOrder().compareTo( i2.getSortOrder() );
            }
        };

        org.hisp.dhis.api.mobile.model.LWUITmodel.Program anonymousProgramMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Program();

        anonymousProgramMobile.setId( program.getId() );

        anonymousProgramMobile.setName( program.getName() );

        // if ( program.getType() == Program.SINGLE_EVENT_WITHOUT_REGISTRATION )
        {
            anonymousProgramMobile.setVersion( program.getVersion() );

            // anonymousProgramMobile.setStatus( ProgramInstance.STATUS_ACTIVE
            // );

            ProgramStage programStage = program.getProgramStages().iterator().next();

            List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>(
                programStage.getProgramStageDataElements() );
            Collections.sort( programStageDataElements, OrderBySortOrder );

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> mobileProgramStages = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage>();

            org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage();

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> mobileProgramStageDataElements = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement>();

            mobileProgramStage.setId( programStage.getId() );
            mobileProgramStage.setName( programStage.getName() );
            mobileProgramStage.setCompleted( false );
            mobileProgramStage.setRepeatable( false );
            mobileProgramStage.setSingleEvent( true );
            mobileProgramStage.setSections( new ArrayList<Section>() );

            // get report date
            mobileProgramStage.setReportDate( PeriodUtil.dateToString( new Date() ) );

            if ( programStage.getReportDateDescription() == null )
            {
                mobileProgramStage.setReportDateDescription( "Report Date" );
            }
            else
            {
                mobileProgramStage.setReportDateDescription( programStage.getReportDateDescription() );
            }

            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement mobileDataElement = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement();
                mobileDataElement.setId( programStageDataElement.getDataElement().getId() );
                mobileDataElement.setName( programStageDataElement.getDataElement().getName() );
                mobileDataElement.setType( programStageDataElement.getDataElement().getType() );

                // problem
                mobileDataElement.setCompulsory( programStageDataElement.isCompulsory() );

                mobileDataElement.setNumberType( programStageDataElement.getDataElement().getNumberType() );

                mobileDataElement.setValue( "" );

                if ( programStageDataElement.getDataElement().getOptionSet() != null )
                {
                    mobileDataElement
                        .setOptionSet( ModelMapping.getOptionSet( programStageDataElement.getDataElement() ) );
                }
                else
                {
                    mobileDataElement.setOptionSet( null );
                }
                if ( programStageDataElement.getDataElement().getCategoryCombo() != null )
                {
                    mobileDataElement.setCategoryOptionCombos( ModelMapping
                        .getCategoryOptionCombos( programStageDataElement.getDataElement() ) );
                }
                else
                {
                    mobileDataElement.setCategoryOptionCombos( null );
                }
                mobileProgramStageDataElements.add( mobileDataElement );
            }
            mobileProgramStage.setDataElements( mobileProgramStageDataElements );
            mobileProgramStages.add( mobileProgramStage );
            anonymousProgramMobile.setProgramStages( mobileProgramStages );
        }

        return anonymousProgramMobile;
    }

    private List<Patient> removeIfDuplicated( List<Patient> patients, int patientId )
    {
        List<Patient> result = new ArrayList<Patient>( patients );
        for ( int i = 0; i < patients.size(); i++ )
        {
            if ( patients.get( i ).getId() == patientId )
            {
                result.remove( i );
            }
        }
        return result;
    }

    private void saveDataValues( ActivityValue activityValue, ProgramStageInstance programStageInstance,
        Map<Integer, DataElement> dataElementMap )
    {
        org.hisp.dhis.dataelement.DataElement dataElement;
        String value;

        for ( DataValue dv : activityValue.getDataValues() )
        {
            value = dv.getValue();

            if ( value != null && value.trim().length() == 0 )
            {
                value = null;
            }

            if ( value != null )
            {
                value = value.trim();
            }

            dataElement = dataElementMap.get( dv.getId() );
            PatientDataValue dataValue = dataValueService.getPatientDataValue( programStageInstance, dataElement );
            if ( dataValue == null )
            {
                if ( value != null )
                {
                    if ( programStageInstance.getExecutionDate() == null )
                    {
                        programStageInstance.setExecutionDate( new Date() );
                        programStageInstanceService.updateProgramStageInstance( programStageInstance );
                    }

                    dataValue = new PatientDataValue( programStageInstance, dataElement, new Date(), value );

                    dataValueService.savePatientDataValue( dataValue );
                }
            }
            else
            {
                if ( programStageInstance.getExecutionDate() == null )
                {
                    programStageInstance.setExecutionDate( new Date() );
                    programStageInstanceService.updateProgramStageInstance( programStageInstance );
                }

                dataValue.setValue( value );
                dataValue.setTimestamp( new Date() );

                dataValueService.updatePatientDataValue( dataValue );
            }
        }
    }

    public Collection<org.hisp.dhis.patient.PatientAttribute> getPatientAtts( String programId )
    {
        Collection<org.hisp.dhis.patient.PatientAttribute> patientAttributes = null;

        if ( programId != null && !programId.trim().equals( "" ) )
        {
            Program program = programService.getProgram( Integer.parseInt( programId ) );
            patientAttributes = program.getAttributes();
        }
        else
        {
            patientAttributes = patientAttributeService.getAllPatientAttributes();
        }

        return patientAttributes;
    }

    public Collection<PatientAttribute> getAttsForMobile()
    {
        Collection<PatientAttribute> list = new HashSet<PatientAttribute>();

        for ( org.hisp.dhis.patient.PatientAttribute patientAtt : getPatientAtts( null ) )
        {
            list.add( new PatientAttribute( patientAtt.getName(), null, patientAtt.getValueType(), patientAtt
                .isMandatory(), new ArrayList<String>() ) );
        }

        return list;

    }

    @Override
    public Collection<PatientAttribute> getPatientAttributesForMobile( String programId )
    {
        Collection<PatientAttribute> list = new HashSet<PatientAttribute>();
        for ( org.hisp.dhis.patient.PatientAttribute pa : getPatientAtts( programId ) )
        {
            PatientAttribute patientAttribute = new PatientAttribute();
            String name = pa.getName();

            patientAttribute.setName( name );
            patientAttribute.setType( pa.getValueType() );
            patientAttribute.setValue( "" );
            List<String> optionList = new ArrayList<String>();
            if ( pa.getAttributeOptions() != null )
            {
                for ( PatientAttributeOption option : pa.getAttributeOptions() )
                {
                    optionList.add( option.getName() );
                }
            }

            patientAttribute.setPredefinedValues( optionList );
            list.add( patientAttribute );
        }
        return list;
    }

    @Required
    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    @Required
    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findLatestPatient()
        throws NotAllowedException
    {
        // Patient patient = patientService.getPatient( this.patientId );
        //
        // org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile =
        // getPatientModel( patient );
        return this.getPatientMobile();
    }

    @Override
    public Integer savePatient( org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patient, int orgUnitId,
        String programIdText )
        throws NotAllowedException
    {
        org.hisp.dhis.patient.Patient patientWeb = new org.hisp.dhis.patient.Patient();

        patientWeb.setOrganisationUnit( organisationUnitService.getOrganisationUnit( orgUnitId ) );

        Set<org.hisp.dhis.patient.PatientAttribute> patientAttributeSet = new HashSet<org.hisp.dhis.patient.PatientAttribute>();
        Set<PatientAttributeValue> patientAttributeValues = new HashSet<PatientAttributeValue>();

        Collection<org.hisp.dhis.api.mobile.model.PatientAttribute> attributesMobile = patient.getAttributes();

        if ( attributesMobile != null )
        {
            for ( org.hisp.dhis.api.mobile.model.PatientAttribute paAtt : attributesMobile )
            {

                org.hisp.dhis.patient.PatientAttribute patientAttribute = patientAttributeService
                    .getPatientAttributeByName( paAtt.getName() );

                patientAttributeSet.add( patientAttribute );

                PatientAttributeValue patientAttributeValue = new PatientAttributeValue();

                patientAttributeValue.setPatient( patientWeb );
                patientAttributeValue.setPatientAttribute( patientAttribute );
                patientAttributeValue.setValue( paAtt.getValue() );
                patientAttributeValues.add( patientAttributeValue );

            }
        }

        patientId = patientService.createPatient( patientWeb, null, null, patientAttributeValues );

        try
        {
            for ( org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance mobileProgramInstance : patient
                .getEnrollmentPrograms() )
            {
                Date incidentDate = PeriodUtil.stringToDate( mobileProgramInstance.getDateOfIncident() );
                enrollProgram( patientId + "-" + mobileProgramInstance.getProgramId(), incidentDate );
            }
        }
        catch ( Exception e )
        {
            return patientId;
        }

        Patient patientNew = patientService.getPatient( this.patientId );
        setPatientMobile( getPatientModel( patientNew ) );

        return patientId;

    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findPatient( int patientId )
        throws NotAllowedException
    {
        Patient patient = patientService.getPatient( patientId );
        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = getPatientModel( patient );
        return patientMobile;
    }

    @Override
    public String findPatientInAdvanced( String keyword, int orgUnitId, int programId )
        throws NotAllowedException
    {
        Set<Patient> patients = new HashSet<Patient>();

        Collection<org.hisp.dhis.patient.PatientAttribute> attributes = patientAttributeService
            .getAllPatientAttributes();

        for ( org.hisp.dhis.patient.PatientAttribute displayAttribute : attributes )
        {
            Collection<Patient> resultPatients = patientAttValueService.getPatient( displayAttribute, keyword );
            // Search in specific OrgUnit
            if ( orgUnitId != 0 )
            {
                for ( Patient patient : resultPatients )
                {
                    if ( patient.getOrganisationUnit().getId() == orgUnitId )
                    {
                        patients.add( patient );
                    }
                }
            }
            // Search in all OrgUnit
            else
            {
                patients.addAll( resultPatients );
            }

        }

        if ( patients.size() == 0 )
        {
            throw NotAllowedException.NO_BENEFICIARY_FOUND;
        }

        String resultSet = "";

        Collection<org.hisp.dhis.patient.PatientAttribute> displayAttributes = patientAttributeService
            .getPatientAttributesDisplayed( true );
        for ( Patient patient : patients )
        {
            resultSet += patient.getId() + "/";
            String attText = "";
            for ( org.hisp.dhis.patient.PatientAttribute displayAttribute : displayAttributes )
            {
                PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient,
                    displayAttribute );
                if ( value != null )
                {
                    attText += value.getValue() + " ";
                }
            }
            attText = attText.trim();
            resultSet += attText + "$";
        }
        return resultSet;
    }

    @Override
    public String findLostToFollowUp( int orgUnitId, String searchEventInfos )
        throws NotAllowedException
    {
        String[] searchEventInfosArray = searchEventInfos.split( "-" );

        int programStageStatus = 0;

        if ( searchEventInfosArray[1].equalsIgnoreCase( "Scheduled in future" ) )
        {
            programStageStatus = ProgramStageInstance.FUTURE_VISIT_STATUS;
        }
        else if ( searchEventInfosArray[1].equalsIgnoreCase( "Overdue" ) )
        {
            programStageStatus = ProgramStageInstance.LATE_VISIT_STATUS;
        }

        boolean followUp;

        if ( searchEventInfosArray[2].equalsIgnoreCase( "true" ) )
        {
            followUp = true;
        }
        else
        {
            followUp = false;
        }

        String eventsInfo = "";

        DateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );

        List<String> searchTextList = new ArrayList<String>();
        Collection<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>();

        Calendar toCalendar = new GregorianCalendar();
        toCalendar.add( Calendar.DATE, -1 );
        toCalendar.add( Calendar.YEAR, 100 );
        Date toDate = toCalendar.getTime();

        Calendar fromCalendar = new GregorianCalendar();
        fromCalendar.add( Calendar.DATE, -1 );
        fromCalendar.add( Calendar.YEAR, -100 );

        Date fromDate = fromCalendar.getTime();

        String searchText = Patient.PREFIX_PROGRAM_EVENT_BY_STATUS + "_" + searchEventInfosArray[0] + "_"
            + formatter.format( fromDate ) + "_" + formatter.format( toDate ) + "_" + orgUnitId + "_" + true + "_"
            + programStageStatus;

        searchTextList.add( searchText );
        orgUnitList.add( organisationUnitService.getOrganisationUnit( orgUnitId ) );
        List<Integer> stageInstanceIds = patientService.getProgramStageInstances( searchTextList, orgUnitList,
            followUp, ProgramInstance.STATUS_ACTIVE, null, null );

        if ( stageInstanceIds.size() == 0 )
        {
            throw NotAllowedException.NO_EVENT_FOUND;
        }
        else if ( stageInstanceIds.size() > 0 )
        {
            for ( Integer stageInstanceId : stageInstanceIds )
            {
                ProgramStageInstance programStageInstance = programStageInstanceService
                    .getProgramStageInstance( stageInstanceId );
                Patient patient = programStageInstance.getProgramInstance().getPatient();
                eventsInfo += programStageInstance.getId() + "/" + patient.getName() + ", "
                    + programStageInstance.getProgramStage().getName() + "("
                    + formatter.format( programStageInstance.getDueDate() ) + ")" + "$";
            }

            throw new NotAllowedException( eventsInfo );
        }
        else
        {
            return "";
        }
    }

    @SuppressWarnings( "finally" )
    @Override
    public Notification handleLostToFollowUp( LostEvent lostEvent )
        throws NotAllowedException
    {
        Notification notification = new Notification();
        try
        {
            ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( lostEvent
                .getId() );
            programStageInstance.setDueDate( PeriodUtil.stringToDate( lostEvent.getDueDate() ) );
            programStageInstance.setStatus( lostEvent.getStatus() );

            if ( lostEvent.getComment() != null )
            {
                List<MessageConversation> conversationList = new ArrayList<MessageConversation>();

                MessageConversation conversation = new MessageConversation( lostEvent.getName(),
                    currentUserService.getCurrentUser() );

                conversation
                    .addMessage( new Message( lostEvent.getComment(), null, currentUserService.getCurrentUser() ) );

                conversation.setRead( true );

                conversationList.add( conversation );

                programStageInstance.setMessageConversations( conversationList );

                messageService.saveMessageConversation( conversation );
            }

            programStageInstanceService.updateProgramStageInstance( programStageInstance );

            // send SMS
            if ( programStageInstance.getProgramInstance().getPatient().getAttributeValues() != null
                && lostEvent.getSMS() != null )
            {
                List<User> recipientsList = new ArrayList<User>();
                for ( PatientAttributeValue attrValue : programStageInstance.getProgramInstance().getPatient()
                    .getAttributeValues() )
                {
                    if ( attrValue.getPatientAttribute().getValueType().equals( "phoneNumber" ) )
                    {
                        User user = new User();
                        user.setPhoneNumber( attrValue.getValue() );
                        recipientsList.add( user );
                    }

                }
                smsSender.sendMessage( lostEvent.getName(), lostEvent.getSMS(), currentUserService.getCurrentUser(),
                    recipientsList, false );
            }

            notification.setMessage( "Success" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            notification.setMessage( "Fail" );
        }
        finally
        {
            return notification;
        }
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient generateRepeatableEvent( int orgUnitId, String eventInfo )
        throws NotAllowedException
    {
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        String mobileProgramStageId = eventInfo.substring( 0, eventInfo.indexOf( "$" ) );

        String nextDueDate = eventInfo.substring( eventInfo.indexOf( "$" ) + 1, eventInfo.length() );

        ProgramStageInstance oldProgramStageIntance = programStageInstanceService.getProgramStageInstance( Integer
            .valueOf( mobileProgramStageId ) );

        ProgramInstance programInstance = oldProgramStageIntance.getProgramInstance();

        ProgramStageInstance newProgramStageInstance = new ProgramStageInstance( programInstance,
            oldProgramStageIntance.getProgramStage() );

        newProgramStageInstance.setDueDate( PeriodUtil.stringToDate( nextDueDate ) );

        newProgramStageInstance.setOrganisationUnit( orgUnit );

        programInstance.getProgramStageInstances().add( newProgramStageInstance );

        List<ProgramStageInstance> proStageInstanceList = new ArrayList<ProgramStageInstance>(
            programInstance.getProgramStageInstances() );

        Collections.sort( proStageInstanceList, new ProgramStageInstanceVisitDateComparator() );

        programInstance.getProgramStageInstances().removeAll( proStageInstanceList );
        programInstance.getProgramStageInstances().addAll( proStageInstanceList );

        programStageInstanceService.addProgramStageInstance( newProgramStageInstance );

        programInstanceService.updateProgramInstance( programInstance );

        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient mobilePatient = getPatientModel( patientService
            .getPatient( programInstance.getPatient().getId() ) );

        return mobilePatient;
    }

    private org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile;

    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient getPatientMobile()
    {
        return patientMobile;
    }

    public void setPatientMobile( org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile )
    {
        this.patientMobile = patientMobile;
    }
}
