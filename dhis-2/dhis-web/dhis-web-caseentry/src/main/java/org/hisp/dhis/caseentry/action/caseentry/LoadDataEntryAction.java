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
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramDataEntryService;
import org.hisp.dhis.program.ProgramIndicatorService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.program.comparator.ProgramStageDataElementSortOrderComparator;
import org.hisp.dhis.program.comparator.ProgramStageSectionSortOrderComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ LoadDataEntryAction.java May 7, 2011 2:37:44 PM $
 */
public class LoadDataEntryAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramDataEntryService programDataEntryService;

    public void setProgramDataEntryService( ProgramDataEntryService programDataEntryService )
    {
        this.programDataEntryService = programDataEntryService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private Integer organisationUnitId;

    private Integer programStageId;

    private Integer programStageInstanceId;

    private ProgramStageInstance programStageInstance;

    private String customDataEntryFormCode;

    private I18n i18n;

    private List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>();

    private Map<Integer, PatientDataValue> patientDataValueMap;

    private OrganisationUnit organisationUnit;

    private Program program;

    private ProgramStage programStage;

    private List<ProgramStageSection> sections = new ArrayList<ProgramStageSection>();

    private Map<String, Double> calAttributeValueMap = new HashMap<String, Double>();

    private ProgramIndicatorService programIndicatorService;

    private String displayOptionSetAsRadioButton;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setProgramIndicatorService( ProgramIndicatorService programIndicatorService )
    {
        this.programIndicatorService = programIndicatorService;
    }

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }

    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    public List<ProgramStageSection> getSections()
    {
        return sections;
    }

    public Program getProgram()
    {
        return program;
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public ProgramStageInstance getProgramStageInstance()
    {
        return programStageInstance;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public String getCustomDataEntryFormCode()
    {
        return customDataEntryFormCode;
    }

    public List<ProgramStageDataElement> getProgramStageDataElements()
    {
        return programStageDataElements;
    }

    public Map<Integer, PatientDataValue> getPatientDataValueMap()
    {
        return patientDataValueMap;
    }

    private String visitor;

    public String getVisitor()
    {
        return visitor;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    public Map<String, Double> getCalAttributeValueMap()
    {
        return calAttributeValueMap;
    }

    private Double longitude;

    public Double getLongitude()
    {
        return longitude;
    }

    private Double latitude;

    public Double getLatitude()
    {
        return latitude;
    }

    private Map<String, String> programIndicatorsMap = new HashMap<String, String>();

    public Map<String, String> getProgramIndicatorsMap()
    {
        return programIndicatorsMap;
    }

    public String getDisplayOptionSetAsRadioButton()
    {
        return displayOptionSetAsRadioButton;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( programStageInstanceId != null )
        {
            programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );

            program = programStageInstance.getProgramStage().getProgram();

            programStage = programStageInstance.getProgramStage();
        }
        else if ( programStageId != null )
        {
            programStage = programStageService.getProgramStage( programStageId );

            program = programStage.getProgram();
        }
        else
        {
            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Get program-stage-instance
        // ---------------------------------------------------------------------

        programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );

        Collections.sort( programStageDataElements, new ProgramStageDataElementSortOrderComparator() );

        DataEntryForm dataEntryForm = programStage.getDataEntryForm();

        if ( programStage.getDataEntryType().equals( ProgramStage.TYPE_SECTION ) )
        {
            sections = new ArrayList<ProgramStageSection>( programStage.getProgramStageSections() );

            Collections.sort( sections, new ProgramStageSectionSortOrderComparator() );
        }
        else if ( programStage.getDataEntryType().equals( ProgramStage.TYPE_CUSTOM ) )
        {
            customDataEntryFormCode = programDataEntryService.prepareDataEntryFormForEntry(
                dataEntryForm.getHtmlCode(), null, i18n, programStage, null, organisationUnit );
        }

        if ( programStageInstance != null )
        {
            // ---------------------------------------------------------------------
            // Get program indicators
            // ---------------------------------------------------------------------

            programIndicatorsMap.putAll( programIndicatorService.getProgramIndicatorValues( programStageInstance
                .getProgramInstance() ) );

            // ---------------------------------------------------------------------
            // Get registration orgunit
            // ---------------------------------------------------------------------

            organisationUnit = organisationUnitId == null ? selectionManager.getSelectedOrganisationUnit()
                : organisationUnitService.getOrganisationUnit( organisationUnitId );

            if ( program.isRegistration() )
            {
                patient = programStageInstance.getProgramInstance().getPatient();
            }

            // ---------------------------------------------------------------------
            // Get data values
            // ---------------------------------------------------------------------

            Collection<PatientDataValue> patientDataValues = getPatientDataValues();

            // ---------------------------------------------------------------------
            // Get data-entry-form
            // ---------------------------------------------------------------------

            if ( programStage.getDataEntryType().equals( ProgramStage.TYPE_CUSTOM ) )
            {
                customDataEntryFormCode = programDataEntryService.prepareDataEntryFormForEntry(
                    dataEntryForm.getHtmlCode(), patientDataValues, i18n, programStage, programStageInstance,
                    organisationUnit );
            }

            // -----------------------------------------------------------------
            // Allow update only if org unit does not have polygon coordinates
            // -----------------------------------------------------------------

            longitude = programStageInstance.getLongitude();
            latitude = programStageInstance.getLatitude();
        }

        return SUCCESS;
    }

    private Collection<PatientDataValue> getPatientDataValues()
    {
        Collection<PatientDataValue> patientDataValues = patientDataValueService
            .getPatientDataValues( programStageInstance );

        patientDataValueMap = new HashMap<Integer, PatientDataValue>( patientDataValues.size() );

        for ( PatientDataValue patientDataValue : patientDataValues )
        {
            int key = patientDataValue.getDataElement().getId();
            patientDataValueMap.put( key, patientDataValue );
        }

        return patientDataValues;
    }
}
