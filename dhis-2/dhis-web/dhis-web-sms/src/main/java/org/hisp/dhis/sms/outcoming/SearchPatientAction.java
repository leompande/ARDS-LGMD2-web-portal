package org.hisp.dhis.sms.outcoming;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchPatientAction
    extends ActionPagingSupport<Patient>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private OrganisationUnitSelectionManager selectionManager;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ProgramService programService;

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private List<String> searchTexts = new ArrayList<String>();

    private Boolean searchBySelectedOrgunit;

    private boolean listAll;

    private Collection<Patient> patients = new ArrayList<Patient>();

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setSearchBySelectedOrgunit( Boolean searchBySelectedOrgunit )
    {
        this.searchBySelectedOrgunit = searchBySelectedOrgunit;
    }

    public void setSearchTexts( List<String> searchTexts )
    {
        this.searchTexts = searchTexts;
    }

    public void setListAll( boolean listAll )
    {
        this.listAll = listAll;
    }

    private List<Integer> programIds;

    public void setProgramIds( List<Integer> programIds )
    {
        this.programIds = programIds;
    }

    public Collection<Patient> getPatients()
    {
        return patients;
    }

    private Integer total;

    public Integer getTotal()
    {
        return total;
    }

    private Map<Integer, String> mapPatientOrgunit = new HashMap<Integer, String>();

    public Map<Integer, String> getMapPatientOrgunit()
    {
        return mapPatientOrgunit;
    }

    private List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();

    public List<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();
        Collection<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>();

        // List all patients
        if ( listAll )
        {
            total = patientService.countGetPatientsByOrgUnit( organisationUnit );
            this.paging = createPaging( total );

            patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, paging.getStartPos(),
                paging.getPageSize() ) );

        }
        // search patients
        else if ( searchTexts.size() > 0 )
        {
            organisationUnit = (searchBySelectedOrgunit) ? organisationUnit : null;
            if ( organisationUnit != null )
            {
                orgunits.add( organisationUnit );
            }

            total = patientService.countSearchPatients( searchTexts, orgunits, null, ProgramInstance.STATUS_ACTIVE );
            this.paging = createPaging( total );
            patients = patientService.searchPatients( searchTexts, orgunits, null, null, null,
                ProgramInstance.STATUS_ACTIVE, paging.getStartPos(), paging.getPageSize() );

            if ( !searchBySelectedOrgunit )
            {
                for ( Patient patient : patients )
                {
                    mapPatientOrgunit.put( patient.getId(), getHierarchyOrgunit( patient.getOrganisationUnit() ) );
                }
            }

            if ( programIds != null )
            {
                for ( Integer programId : programIds )
                {
                    Program program = programService.getProgram( programId );
                    identifierTypes.addAll( program.getIdentifierTypes() );
                }
            }
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " / " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }
}
