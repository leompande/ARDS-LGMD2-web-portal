package org.hisp.dhis.caseentry.action.report;

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
import java.util.List;

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.program.ProgramStageSectionService;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $LoadDataElementsAction.java Feb 29, 2012 9:40:40 AM$
 */
public class LoadDataElementsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramStageSectionService programStageSectionService;

    public void setProgramStageSectionService( ProgramStageSectionService programStageSectionService )
    {
        this.programStageSectionService = programStageSectionService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private String programStageId;

    public void setProgramStageId( String programStageId )
    {
        this.programStageId = programStageId;
    }

    private Integer sectionId;

    public void setSectionId( Integer sectionId )
    {
        this.sectionId = sectionId;
    }

    private List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();

    public List<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    private List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();

    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    private List<ProgramStageDataElement> psDataElements = new ArrayList<ProgramStageDataElement>();

    public List<ProgramStageDataElement> getPsDataElements()
    {
        return psDataElements;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        Program program = null;

        if ( programStageId != null )
        {
            ProgramStage programStage = programStageService.getProgramStage( programStageId );
            psDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
            program = programStage.getProgram();
        }
        else if ( sectionId != null )
        {
            ProgramStageSection section = programStageSectionService.getProgramStageSection( sectionId );
            psDataElements = new ArrayList<ProgramStageDataElement>( section.getProgramStageDataElements() );
            program = section.getProgramStageDataElements().iterator().next().getProgramStage().getProgram();
        }

        if ( program != null && program.isRegistration() )
        {
            identifierTypes = program.getIdentifierTypes();
            patientAttributes = program.getAttributes();
        }

        return SUCCESS;
    }
}
