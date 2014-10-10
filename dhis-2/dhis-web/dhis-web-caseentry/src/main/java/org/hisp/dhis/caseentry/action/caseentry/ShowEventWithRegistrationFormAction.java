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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientRegistrationForm;
import org.hisp.dhis.patient.PatientRegistrationFormService;
import org.hisp.dhis.patient.comparator.PatientAttributeGroupSortOrderComparator;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramDataEntryService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 */
public class ShowEventWithRegistrationFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private PatientRegistrationFormService patientRegistrationFormService;

    public void setPatientRegistrationFormService( PatientRegistrationFormService patientRegistrationFormService )
    {
        this.patientRegistrationFormService = patientRegistrationFormService;
    }

    private ProgramDataEntryService programDataEntryService;

    public void setProgramDataEntryService( ProgramDataEntryService programDataEntryService )
    {
        this.programDataEntryService = programDataEntryService;
    }

    private PatientAttributeService attributeService;

    public void setAttributeService( PatientAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private PatientAttributeGroupService attributeGroupService;

    public void setAttributeGroupService( PatientAttributeGroupService attributeGroupService )
    {
        this.attributeGroupService = attributeGroupService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programId;

    private Collection<PatientAttribute> noGroupAttributes = new HashSet<PatientAttribute>();

    private Collection<PatientIdentifierType> identifierTypes;

    private OrganisationUnit organisationUnit;

    private String customDataEntryFormCode;

    private List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>();

    private ProgramStage programStage;

    private Collection<User> healthWorkers;

    private String customRegistrationForm;

    private List<PatientAttributeGroup> attributeGroups;

    private Map<Integer, Collection<PatientAttribute>> attributeGroupsMap = new HashMap<Integer, Collection<PatientAttribute>>();

    private Program program;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // Get health workers
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        healthWorkers = organisationUnit.getUsers();

        program = programService.getProgram( programId );
        PatientRegistrationForm patientRegistrationForm = patientRegistrationFormService
            .getPatientRegistrationForm( program );

        if ( patientRegistrationForm != null )
        {
            customRegistrationForm = patientRegistrationFormService.prepareDataEntryFormForAdd( patientRegistrationForm
                .getDataEntryForm().getHtmlCode(), patientRegistrationForm.getProgram(), healthWorkers, null, null,
                i18n, format );
        }

        if ( customRegistrationForm == null )
        {
            identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

            Collection<PatientAttribute> patientAttributesInProgram = new HashSet<PatientAttribute>();
            Collection<Program> programs = programService.getAllPrograms();
            programs.remove( program );
            for ( Program p : programs )
            {
                identifierTypes.removeAll( p.getIdentifierTypes() );
                patientAttributesInProgram.addAll( p.getAttributes() );
            }

            attributeGroups = new ArrayList<PatientAttributeGroup>(
                attributeGroupService.getAllPatientAttributeGroups() );
            Collections.sort( attributeGroups, new PatientAttributeGroupSortOrderComparator() );
            for ( PatientAttributeGroup attributeGroup : attributeGroups )
            {
                List<PatientAttribute> attributes = attributeGroupService.getPatientAttributes( attributeGroup );
                attributes.removeAll( patientAttributesInProgram );

                if ( attributes.size() > 0 )
                {
                    attributeGroupsMap.put( attributeGroup.getId(), attributes );
                }
            }

            noGroupAttributes = attributeService.getPatientAttributesWithoutGroup();
            noGroupAttributes.removeAll( patientAttributesInProgram );
        }

        // Get data entry form
        programStage = program.getProgramStages().iterator().next();
        if ( programStage.getDataEntryForm() != null )
        {
            customDataEntryFormCode = programDataEntryService.prepareDataEntryFormForAdd( programStage
                .getDataEntryForm().getHtmlCode(), i18n, programStage );
        }
        else
        {
            programStageDataElements = new ArrayList<ProgramStageDataElement>(
                programStage.getProgramStageDataElements() );
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Getter/Setter
    // -------------------------------------------------------------------------

    public Program getProgram()
    {
        return program;
    }

    public Collection<User> getHealthWorkers()
    {
        return healthWorkers;
    }

    public String getCustomRegistrationForm()
    {
        return customRegistrationForm;
    }

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public Collection<PatientAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public String getCustomDataEntryFormCode()
    {
        return customDataEntryFormCode;
    }

    public List<ProgramStageDataElement> getProgramStageDataElements()
    {
        return programStageDataElements;
    }

    public List<PatientAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }

    public Map<Integer, Collection<PatientAttribute>> getAttributeGroupsMap()
    {
        return attributeGroupsMap;
    }
}
