package org.hisp.dhis.patient.state;

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
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.ActionContext;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class DefaultSelectedStateManager
    implements SelectedStateManager
{
    public static final String SESSION_KEY_SELECTED_SEARCHING_ATTRIBUTE_ID = "selected_searching_attribute_id";

    public static final String SESSION_KEY_SPECIFIED_SEARCH_TEXT = "specified_search_text";

    public static final String SESSION_KEY_SELECTED_SORT_ATTRIBUTE_ID = "selected_sort_attribute_id";

    public static final String SESSION_KEY_SELECTED_PATIENT_ID = "selected_patient_id";

    public static final String SESSION_KEY_SELECTED_PROGRAM_ID = "selected_program_id";

    public static final String SESSION_KEY_SELECTED_PROGRAMSTAGE_ID = "selected_program_stage_id";

    public static final String SESSION_KEY_LISTALL = "list_all_value";

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

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    // -------------------------------------------------------------------------
    // SelectedStateManager implementation
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

    public void setSelectedProgram( Program program )
    {
        getSession().put( SESSION_KEY_SELECTED_PROGRAM_ID, program.getId() );
    }

    public Program getSelectedProgram()
    {
        Integer id = (Integer) getSession().get( SESSION_KEY_SELECTED_PROGRAM_ID );

        if ( id == null )
        {
            return null;
        }

        return programService.getProgram( id );
    }

    public void clearSelectedProgram()
    {
        getSession().remove( SESSION_KEY_SELECTED_PROGRAM_ID );
    }

    public void setSelectedProgramStage( ProgramStage programStage )
    {
        getSession().put( SESSION_KEY_SELECTED_PROGRAMSTAGE_ID, programStage.getId() );
    }

    public ProgramStage getSelectedProgramStage()
    {
        Integer id = (Integer) getSession().get( SESSION_KEY_SELECTED_PROGRAMSTAGE_ID );

        if ( id == null )
        {
            return null;
        }

        return programStageService.getProgramStage( id );
    }

    public void clearSelectedProgramStage()
    {
        getSession().remove( SESSION_KEY_SELECTED_PROGRAMSTAGE_ID );
    }

    public void clearListAll()
    {
        getSession().remove( SESSION_KEY_LISTALL );
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

    public void setListAll( boolean listAll )
    {
        getSession().put( SESSION_KEY_LISTALL, listAll );
    }

    // -------------------------------------------------------------------------
    // Search patients by patient-attribute
    // -------------------------------------------------------------------------

    public void setSearchingAttributeId( int searchingAttributeId )
    {
        getSession().put( SESSION_KEY_SELECTED_SEARCHING_ATTRIBUTE_ID, searchingAttributeId );
    }

    public Integer getSearchingAttributeId()
    {
        return (Integer) getSession().get( SESSION_KEY_SELECTED_SEARCHING_ATTRIBUTE_ID );
    }

    public void clearSearchingAttributeId()
    {
        getSession().remove( SESSION_KEY_SELECTED_SEARCHING_ATTRIBUTE_ID );
    }

    public void setSearchText( String searchText )
    {
        getSession().put( SESSION_KEY_SPECIFIED_SEARCH_TEXT, searchText );
    }

    public String getSearchText()
    {
        return (String) getSession().get( SESSION_KEY_SPECIFIED_SEARCH_TEXT );
    }

    public void clearSearchText()
    {
        getSession().remove( SESSION_KEY_SPECIFIED_SEARCH_TEXT );
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
    // Supporting methods
    // -------------------------------------------------------------------------

    private static final Map<String, Object> getSession()
    {
        return ActionContext.getContext().getSession();
    }
}
