package org.hisp.dhis.caseentry.action.caseentry;

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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
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
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $SaveValuesAction.java Jun 27, 2012 7:45:20 AM$
 */
public class SaveValuesAction
    implements Action
{
    private static final Log LOG = LogFactory.getLog( SaveValueAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }


    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private Integer patientId;

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    private int statusCode;

    public int getStatusCode()
    {
        return statusCode;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Program program = programService.getProgram( programId );
        ProgramStage programStage = program.getProgramStages().iterator().next();
        Patient patient = patientService.getPatient( patientId );

        // ---------------------------------------------------------------------
        // Add a new program-instance
        // ---------------------------------------------------------------------

        ProgramInstance programInstance = new ProgramInstance();
        Date currentDate = new Date();
        programInstance.setEnrollmentDate( currentDate );
        programInstance.setDateOfIncident( currentDate );
        programInstance.setProgram( program );
        programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
        programInstance.setPatient( patient );

        programInstanceService.addProgramInstance( programInstance );

        // ---------------------------------------------------------------------
        // Add a new program-stage-instance
        // ---------------------------------------------------------------------

        ProgramStageInstance programStageInstance = new ProgramStageInstance();
        programStageInstance.setProgramInstance( programInstance );
        programStageInstance.setProgramStage( programStage );
        programStageInstance.setDueDate( currentDate );
        programStageInstance.setExecutionDate( currentDate );
        programStageInstance.setOrganisationUnit( selectionManager.getSelectedOrganisationUnit() );
        programStageInstance.setCompleted( true );

        programStageInstanceService.addProgramStageInstance( programStageInstance );

        // ---------------------------------------------------------------------
        // Save value
        // ---------------------------------------------------------------------

        HttpServletRequest request = ServletActionContext.getRequest();

        String storedBy = currentUserService.getCurrentUsername();
        Collection<ProgramStageDataElement> psDataElements = programStage.getProgramStageDataElements();
        for ( ProgramStageDataElement psDataElement : psDataElements )
        {
            String dataElementFieldId = programStage.getUid() + "-" + psDataElement.getDataElement().getUid() + "-val";
            String value = request.getParameter( dataElementFieldId );
            if ( value != null && value.trim().length() > 0 )
            {
                String providedElsewhereId = programStage.getUid() + "_" + psDataElement.getDataElement().getUid()
                    + "_facility";
                boolean providedElsewhere = (request.getParameter( providedElsewhereId ) == null) ? false : true;

                PatientDataValue patientDataValue = new PatientDataValue( programStageInstance,
                    psDataElement.getDataElement(), new Date(), value.trim() );
                patientDataValue.setStoredBy( storedBy );
                patientDataValue.setProvidedElsewhere( providedElsewhere );
                patientDataValueService.savePatientDataValue( patientDataValue );

                LOG.debug( "Adding PatientDataValue, value added" );
            }
        }

        statusCode = 0;

        return SUCCESS;
    }
}
