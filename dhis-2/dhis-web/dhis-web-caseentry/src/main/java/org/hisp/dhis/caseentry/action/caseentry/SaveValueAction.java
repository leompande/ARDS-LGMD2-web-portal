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

import com.opensymphony.xwork2.Action;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.user.CurrentUserService;

import java.util.Date;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SaveValueAction
    implements Action
{
    private static final Log LOG = LogFactory.getLog( SaveValueAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }

    private String dataElementUid;
    
    public void setDataElementUid( String dataElementUid )
    {
        this.dataElementUid = dataElementUid;
    }

    private Integer programStageInstanceId;

    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    private int statusCode;

    public int getStatusCode()
    {
        return statusCode;
    }

    private Boolean providedElsewhere;

    public void setProvidedElsewhere( Boolean providedElsewhere )
    {
        this.providedElsewhere = providedElsewhere;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );

        DataElement dataElement = dataElementService.getDataElement( dataElementUid );

        PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance,
            dataElement );

        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }

        // ---------------------------------------------------------------------
        // Save value
        // ---------------------------------------------------------------------

        if ( programStageInstance.getExecutionDate() == null )
        {
            programStageInstance.setExecutionDate( new Date() );
            programStageInstanceService.updateProgramStageInstance( programStageInstance );
        }

        providedElsewhere = (providedElsewhere == null) ? false : providedElsewhere;
        String storedBy = currentUserService.getCurrentUsername();

        if ( patientDataValue == null && value != null )
        {
            LOG.debug( "Adding PatientDataValue, value added" );

            patientDataValue = new PatientDataValue( programStageInstance, dataElement, new Date(), value );
            patientDataValue.setStoredBy( storedBy );
            patientDataValue.setProvidedElsewhere( providedElsewhere );

            patientDataValueService.savePatientDataValue( patientDataValue );
        }

        if ( patientDataValue != null && value == null )
        {
            patientDataValueService.deletePatientDataValue( patientDataValue );
        }
        else if ( patientDataValue != null && value != null )
        {
            LOG.debug( "Updating PatientDataValue, value added/changed" );

            patientDataValue.setValue( value );
            patientDataValue.setTimestamp( new Date() );
            patientDataValue.setProvidedElsewhere( providedElsewhere );
            patientDataValue.setStoredBy( storedBy );

            patientDataValueService.updatePatientDataValue( patientDataValue );
        }

        statusCode = 0;

        return SUCCESS;
    }
}
