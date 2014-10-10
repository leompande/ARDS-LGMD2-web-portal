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

import org.hisp.dhis.api.mobile.IProgramService;
import org.hisp.dhis.api.mobile.model.DataElement;
import org.hisp.dhis.api.mobile.model.Model;
import org.hisp.dhis.api.mobile.model.ModelList;
import org.hisp.dhis.api.mobile.model.Program;
import org.hisp.dhis.api.mobile.model.ProgramStage;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramPatientAttribute;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageSection;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DefaultProgramService
    implements IProgramService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private org.hisp.dhis.program.ProgramService programService;

    // -------------------------------------------------------------------------
    // ProgramService
    // -------------------------------------------------------------------------

    public List<Program> getPrograms( OrganisationUnit unit, String localeString )
    {
        List<Program> programs = new ArrayList<Program>();

        for ( org.hisp.dhis.program.Program program : programService.getPrograms( unit ) )
        {
            programs.add( getProgram( program.getId(), localeString ) );
        }

        return programs;
    }

    public List<org.hisp.dhis.api.mobile.model.LWUITmodel.Program> getProgramsLWUIT( OrganisationUnit unit )
    {
        Collection<org.hisp.dhis.program.Program> programByUnit = programService.getPrograms( unit );

        Collection<org.hisp.dhis.program.Program> programByCurrentUser = new HashSet<org.hisp.dhis.program.Program>(
            programService.getProgramsByCurrentUser() );

        programByCurrentUser.retainAll( programByUnit );

        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Program> programs = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Program>();

        for ( org.hisp.dhis.program.Program program : programByCurrentUser )
        {
            programs.add( getProgramLWUIT( program.getId() ) );
        }

        return programs;
    }

    public List<Program> updateProgram( ModelList programsFromClient, String localeString, OrganisationUnit unit )
    {
        List<Program> programs = new ArrayList<Program>();
        boolean isExisted = false;

        // Get all Program belong to this OrgUnit
        List<Program> serverPrograms = this.getPrograms( unit, localeString );

        for ( Program program : serverPrograms )
        {
            // Loop thought the list of program from client
            for ( int j = 0; j < programsFromClient.getModels().size(); j++ )
            {
                Model model = programsFromClient.getModels().get( j );
                if ( program.getId() == model.getId() )
                {
                    // Version is different
                    if ( program.getVersion() != Integer.parseInt( model.getName() ) )
                    {
                        programs.add( program );
                        isExisted = true;
                    }
                }
            }

            // Server has more program than client
            if ( !isExisted )
            {
                programs.add( program );
            }
        }
        return programs;
    }

    public Program getProgram( int programId, String localeString )
    {
        org.hisp.dhis.program.Program program = programService.getProgram( programId );

        // program = i18n( i18nService, locale, program );

        Program pr = new Program();

        pr.setId( program.getId() );
        pr.setName( program.getName() );
        pr.setVersion( program.getVersion() );

        List<ProgramStage> prStgs = new ArrayList<ProgramStage>();

        for ( org.hisp.dhis.program.ProgramStage programStage : program.getProgramStages() )
        {
            // programStage = i18n( i18nService, locale, programStage );

            ProgramStage prStg = new ProgramStage();

            prStg.setId( programStage.getId() );

            prStg.setName( programStage.getName() );

            List<DataElement> des = new ArrayList<DataElement>();

            Set<ProgramStageDataElement> programStageDataElements = programStage.getProgramStageDataElements();

            for ( ProgramStageDataElement programStagedataElement : programStageDataElements )
            {
                org.hisp.dhis.dataelement.DataElement dataElement = programStagedataElement.getDataElement();

                DataElement de = ModelMapping.getDataElement( dataElement );

                de.setCompulsory( programStagedataElement.isCompulsory() );

                des.add( de );
            }

            prStg.setDataElements( des );

            prStgs.add( prStg );

        }

        pr.setProgramStages( prStgs );

        return pr;
    }

    public org.hisp.dhis.api.mobile.model.LWUITmodel.Program getProgramLWUIT( int programId )
    {
        org.hisp.dhis.program.Program program = programService.getProgram( programId );

        // program = i18n( i18nService, locale, program );

        org.hisp.dhis.api.mobile.model.LWUITmodel.Program pr = new org.hisp.dhis.api.mobile.model.LWUITmodel.Program();

        pr.setId( program.getId() );
        pr.setName( program.getName() );
        pr.setType( program.getType() );
        pr.setVersion( program.getVersion() );
        pr.setDateOfEnrollmentDescription( program.getDateOfEnrollmentDescription() );
        pr.setDateOfIncidentDescription( program.getDateOfIncidentDescription() );

        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> prStgs = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage>();

        for ( org.hisp.dhis.program.ProgramStage programStage : program.getProgramStages() )
        {
            // programStage = i18n( i18nService, locale, programStage );

            org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage prStg = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage();

            // add report date

            prStg.setReportDate( "" );

            prStg.setReportDateDescription( programStage.getReportDateDescription() );

            prStg.setId( programStage.getId() );

            prStg.setName( programStage.getName() );

            prStg.setRepeatable( programStage.getIrregular() );

            if ( programStage.getStandardInterval() == null )
            {
                prStg.setStandardInterval( 0 );
            }
            else
            {
                prStg.setStandardInterval( programStage.getStandardInterval() );
            }

            prStg.setCompleted( false );

            prStg.setSingleEvent( program.isSingleEvent() );

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> des = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement>();

            Set<ProgramStageDataElement> programStageDataElements = programStage.getProgramStageDataElements();

            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                org.hisp.dhis.dataelement.DataElement dataElement = programStageDataElement.getDataElement();

                org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement de = ModelMapping
                    .getDataElementLWUIT( dataElement );

                de.setCompulsory( programStageDataElement.isCompulsory() );

                de.setNumberType( programStageDataElement.getDataElement().getNumberType() );

                des.add( de );
            }

            prStg.setDataElements( des );

            // Set all program sections
            List<org.hisp.dhis.api.mobile.model.LWUITmodel.Section> mobileSections = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Section>();
            if ( programStage.getProgramStageSections().size() > 0 )
            {
                for ( ProgramStageSection eachSection : programStage.getProgramStageSections() )
                {
                    org.hisp.dhis.api.mobile.model.LWUITmodel.Section mobileSection = new org.hisp.dhis.api.mobile.model.LWUITmodel.Section();
                    mobileSection.setId( eachSection.getId() );
                    mobileSection.setName( eachSection.getName() );

                    // Set all data elements' id, then we could have full from
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
            prStg.setSections( mobileSections );

            prStgs.add( prStg );
        }

        pr.setProgramStages( prStgs );

        List<ProgramPatientAttribute> programPatientAttributes = new ArrayList<ProgramPatientAttribute>(
            program.getProgramPatientAttributes() );

        for ( int i = 0; i < programPatientAttributes.size(); i++ )
        {
            ProgramPatientAttribute ppa = programPatientAttributes.get( i );
            pr.getProgramAttributes().add( this.getPatientAttributeForMobile( ppa ) );
        }

        return pr;
    }

    private org.hisp.dhis.api.mobile.model.PatientAttribute getPatientAttributeForMobile( PatientAttribute pa )
    {
        PatientAttributeService patientAttributeService;
        List<String> optionList = new ArrayList<String>();
        if ( pa.getAttributeOptions() != null )
        {
            for ( PatientAttributeOption pao : pa.getAttributeOptions() )
            {
                optionList.add( pao.getName() );
            }
        }
        org.hisp.dhis.api.mobile.model.PatientAttribute mobileAttribute = new org.hisp.dhis.api.mobile.model.PatientAttribute();
        mobileAttribute.setName( pa.getName() );
        mobileAttribute.setMandatory( pa.isMandatory() );
        mobileAttribute.setType( pa.getValueType() );
        mobileAttribute.setValue( "" );
        mobileAttribute.setPredefinedValues( optionList );
        mobileAttribute.setDisplayedInList( false );
        return mobileAttribute;
    }

    private org.hisp.dhis.api.mobile.model.PatientAttribute getPatientAttributeForMobile( ProgramPatientAttribute ppa )
    {
        PatientAttribute pa = ppa.getPatientAttribute();
        List<String> optionList = new ArrayList<String>();
        if ( pa.getAttributeOptions() != null )
        {
            for ( PatientAttributeOption pao : pa.getAttributeOptions() )
            {
                optionList.add( pao.getName() );
            }
        }
        org.hisp.dhis.api.mobile.model.PatientAttribute mobileAttribute = new org.hisp.dhis.api.mobile.model.PatientAttribute();
        mobileAttribute.setName( pa.getName() );
        mobileAttribute.setMandatory( pa.isMandatory() );
        mobileAttribute.setType( pa.getValueType() );
        mobileAttribute.setValue( "" );
        mobileAttribute.setPredefinedValues( optionList );
        if ( ppa.getDisplayedInList() )
        {
            mobileAttribute.setDisplayedInList( true );
        }
        else
        {
            mobileAttribute.setDisplayedInList( false );
        }
        return mobileAttribute;
    }

    @Required
    public void setProgramService( org.hisp.dhis.program.ProgramService programService )
    {
        this.programService = programService;
    }
}
