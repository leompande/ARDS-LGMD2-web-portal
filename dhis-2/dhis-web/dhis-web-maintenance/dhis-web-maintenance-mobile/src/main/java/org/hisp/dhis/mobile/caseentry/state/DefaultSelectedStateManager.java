package org.hisp.dhis.mobile.caseentry.state;

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

import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;

import com.opensymphony.xwork2.ActionContext;

/**
 * @author Abyot Asalefew
 */
public class DefaultSelectedStateManager
    implements SelectedStateManager
{
    public static final String SESSION_KEY_SELECTED_PATIENT_ID = "selected_patient_id";

    public static final String SESSION_KEY_SELECTED_PROGRAM_INSTANCE_ID = "selected_program_instance_id";

    public static final String SESSION_KEY_SELECTED_PROGRAM_STAGE_INSTANCE_ID = "selected_program_stage_instance_id";

    public static final String SESSION_KEY_SELECTED_PROGRAM_ID = "selected_program_id";

    public static final String SESSION_KEY_SELECTED_PROGRAMSTAGE_ID = "selected_program_stage_id";

    public static final String SESSION_KEY_LISTALL = "list_all_value";

    public static final String SESSION_KEY_SELECTED_SEARCHING_ATTRIBUTE_ID = "selected_searching_attribute_id";

    public static final String SESSION_KEY_SPECIFIED_SEARCH_TEXT = "specified_search_text";
    
    public static final String SESSION_KEY_SELECTED_SORT_ATTRIBUTE_ID = "selected_sort_attribute_id";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public OrganisationUnit getSelectedOrganisationUnit()
    {
        return selectionManager.getSelectedOrganisationUnit();
    }

    public void setSelectedPatient( Patient patient )
    {
        getSession().put( SESSION_KEY_SELECTED_PATIENT_ID, patient.getId() );
    }

    public Patient getSelectedPatient()
    {
        Integer id = (Integer) getSession().get( SESSION_KEY_SELECTED_PATIENT_ID );

        if ( id == null )
        {
            return null;
        }

        return patientService.getPatient( id );
    }

    public void clearSelectedPatient()
    {
        getSession().remove( SESSION_KEY_SELECTED_PATIENT_ID );
    }

    public void setSelectedProgramInstance( ProgramInstance programInstance )
    {
        getSession().put( SESSION_KEY_SELECTED_PROGRAM_INSTANCE_ID, programInstance.getId() );
    }

    public ProgramInstance getSelectedProgramInstance()
    {
        Integer id = (Integer) getSession().get( SESSION_KEY_SELECTED_PROGRAM_INSTANCE_ID );

        if ( id == null )
        {
            return null;
        }

        return programInstanceService.getProgramInstance( id );
    }

    public void clearSelectedProgramInstance()
    {
        getSession().remove( SESSION_KEY_SELECTED_PROGRAM_INSTANCE_ID );
    }

    public void setSelectedProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        getSession().put( SESSION_KEY_SELECTED_PROGRAM_STAGE_INSTANCE_ID, programStageInstance.getId() );
    }

    public ProgramStageInstance getSelectedProgramStageInstance()
    {
        Integer id = (Integer) getSession().get( SESSION_KEY_SELECTED_PROGRAM_STAGE_INSTANCE_ID );

        if ( id == null )
        {
            return null;
        }

        return programStageInstanceService.getProgramStageInstance( id );
    }

    public void clearSelectedProgramStageInstance()
    {
        getSession().remove( SESSION_KEY_SELECTED_PROGRAM_STAGE_INSTANCE_ID );
    }

    public void clearListAll()
    {
        getSession().remove( SESSION_KEY_LISTALL );
    }

    public void clearSearchTest()
    {
        getSession().remove( SESSION_KEY_SPECIFIED_SEARCH_TEXT );
    }

    public void clearSearchingAttributeId()
    {
        getSession().remove( SESSION_KEY_SELECTED_SEARCHING_ATTRIBUTE_ID );
    }

    public boolean getListAll()
    {
        if ( getSession().get( SESSION_KEY_LISTALL ) != null )
        {
            return (Boolean) getSession().get( SESSION_KEY_LISTALL );
        }

        else
        {
            return false;
        }
    }

    public String getSearchText()
    {
        return (String) getSession().get( SESSION_KEY_SPECIFIED_SEARCH_TEXT );
    }

    public Integer getSearchingAttributeId()
    {
        return (Integer) getSession().get( SESSION_KEY_SELECTED_SEARCHING_ATTRIBUTE_ID );
    }

    public void setListAll( boolean listAll )
    {
        getSession().put( SESSION_KEY_LISTALL, listAll );
    }

    public void setSearchText( String searchText )
    {
        getSession().put( SESSION_KEY_SPECIFIED_SEARCH_TEXT, searchText );
    }

    public void setSearchingAttributeId( int searchingAttributeId )
    {
        getSession().put( SESSION_KEY_SELECTED_SEARCHING_ATTRIBUTE_ID, searchingAttributeId );
    }

    // -------------------------------------------------------------------------
    // Sort by patient-attribute
    // -------------------------------------------------------------------------

    public void setSortingAttributeId( int sortAttributeId )
    {
        getSession().put( SESSION_KEY_SELECTED_SORT_ATTRIBUTE_ID, sortAttributeId );
    }

    public Integer getSortAttributeId()
    {
        return (Integer) getSession().get( SESSION_KEY_SELECTED_SORT_ATTRIBUTE_ID );
    }

    public void clearSortingAttributeId()
    {
        getSession().remove( SESSION_KEY_SELECTED_SORT_ATTRIBUTE_ID );
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    private static final Map<String, Object> getSession()
    {
        return ActionContext.getContext().getSession();
    }
}
