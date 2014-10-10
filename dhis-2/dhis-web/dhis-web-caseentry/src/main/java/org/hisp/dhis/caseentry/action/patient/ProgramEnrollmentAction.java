package org.hisp.dhis.caseentry.action.patient;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.comparator.ProgramStageInstanceVisitDateComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 */
public class ProgramEnrollmentAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceService programInstanceService;

    private OrganisationUnitSelectionManager selectionManager;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programInstanceId;

    private Map<Integer, String> identiferMap;

    private List<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();

    private List<PatientIdentifierType> identifierTypes;

    private Collection<PatientAttribute> noGroupAttributes = new HashSet<PatientAttribute>();

    private List<PatientAttributeGroup> attributeGroups;

    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

    private Boolean hasDataEntry;

    private List<PatientAttribute> patientAttributes;

    private ProgramInstance programInstance;

    // -------------------------------------------------------------------------
    // Getters/Setters
    // -------------------------------------------------------------------------
    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public Collection<PatientAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }

    public List<PatientAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }

    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    public Map<Integer, String> getIdentiferMap()
    {
        return identiferMap;
    }

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    public List<ProgramStageInstance> getProgramStageInstances()
    {
        return programStageInstances;
    }

    public Boolean getHasDataEntry()
    {
        return hasDataEntry;
    }

    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit orgunit = selectionManager.getSelectedOrganisationUnit();

        // ---------------------------------------------------------------------
        // Load active ProgramInstance, completed = false
        // ---------------------------------------------------------------------

        programInstance = programInstanceService.getProgramInstance( programInstanceId );

        programStageInstances = new ArrayList<ProgramStageInstance>( programInstance.getProgramStageInstances() );

        Collections.sort( programStageInstances, new ProgramStageInstanceVisitDateComparator() );

        loadIdentifierTypes( programInstance );

        loadPatientAttributes( programInstance );

        hasDataEntry = showDataEntry( orgunit, programInstance.getProgram(), programInstance );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void loadIdentifierTypes( ProgramInstance programInstance )
    {
        // ---------------------------------------------------------------------
        // Load identifier types of the selected program
        // ---------------------------------------------------------------------

        identifierTypes = new ArrayList<PatientIdentifierType>( programInstance.getProgram().getIdentifierTypes() );
        identiferMap = new HashMap<Integer, String>();

        if ( identifierTypes != null && identifierTypes.size() > 0 )
        {
            Collection<PatientIdentifier> patientIdentifiers = programInstance.getPatient().getIdentifiers();

            for ( PatientIdentifier identifier : patientIdentifiers )
            {
                if ( identifier.getIdentifierType() != null )
                {
                    identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
                }
            }
        }
    }

    private void loadPatientAttributes( ProgramInstance programInstance )
    {
        // ---------------------------------------------------------------------
        // Load patient-attributes of the selected program
        // ---------------------------------------------------------------------

        patientAttributes = new ArrayList<PatientAttribute>( programInstance.getProgram().getAttributes() );

        if ( patientAttributes != null )
        {
            Collection<PatientAttributeValue> patientAttributeValues = programInstance.getPatient()
                .getAttributeValues();

            for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
            {
                if ( patientAttributes.contains( patientAttributeValue.getPatientAttribute() ) )
                {
                    patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                        patientAttributeValue.getValue() );
                }
            }
        }
    }

    private boolean showDataEntry( OrganisationUnit orgunit, Program program, ProgramInstance programInstance )
    {
        if ( !program.getOrganisationUnits().contains( orgunit ) )
        {
            return false;
        }
        else if ( !program.isSingleEvent() && programInstance == null )
        {
            return false;
        }

        return true;
    }

}
