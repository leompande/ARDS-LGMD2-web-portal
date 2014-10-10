package org.hisp.dhis.api.mobile;

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

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.api.mobile.model.ActivityPlan;
import org.hisp.dhis.api.mobile.model.ActivityValue;
import org.hisp.dhis.api.mobile.model.PatientAttribute;
import org.hisp.dhis.api.mobile.model.LWUITmodel.LostEvent;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Notification;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Patient;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Program;
import org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.PatientIdentifierType;

/**
 * Provides services for activity reporting
 */
public interface ActivityReportingService
{
    public ActivityPlan getCurrentActivityPlan( OrganisationUnit unit, String localeString );

    public ActivityPlan getAllActivityPlan( OrganisationUnit unit, String localeString );

    public void saveActivityReport( OrganisationUnit unit, ActivityValue activityValue, Integer programStageSectionId )
        throws NotAllowedException;

    public String findPatient( String name, int orgUnitId )
        throws NotAllowedException;

    public Patient findPatient( int patientId )
        throws NotAllowedException;

    public String findPatientInAdvanced( String keyword, int orgUnitId, int programId )
        throws NotAllowedException;

    public String saveProgramStage( ProgramStage programStage, int patientId, int orgUnitId )
        throws NotAllowedException;

    public Patient enrollProgram( String enrollInfo, Date incidentDate )
        throws NotAllowedException;

    public Collection<org.hisp.dhis.patient.PatientAttribute> getPatientAtts( String programId );

    public Collection<PatientAttribute> getAttsForMobile();

    public Collection<PatientAttribute> getPatientAttributesForMobile( String programId );

    public Patient addRelationship( Relationship enrollmentRelationship, int orgUnitId )
        throws NotAllowedException;

    public Program getAllProgramByOrgUnit( int orgUnitId, String programType )
        throws NotAllowedException;

    public Program findProgram( String programInfo )
        throws NotAllowedException;

    public Patient findLatestPatient()
        throws NotAllowedException;

    public Integer savePatient( Patient patient, int orgUnitId, String programId )
        throws NotAllowedException;

    public String findLostToFollowUp( int orgUnitId, String programId )
        throws NotAllowedException;

    public Notification handleLostToFollowUp( LostEvent lostEvent )
        throws NotAllowedException;
    
    public Patient generateRepeatableEvent( int orgUnitId, String eventInfo )
        throws NotAllowedException;

}
