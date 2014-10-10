package org.hisp.dhis.caseentry.action;

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
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class GetProgramMetaDataAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer programType = Program.SINGLE_EVENT_WITHOUT_REGISTRATION;

    public void setProgramType( Integer programType )
    {
        this.programType = programType;
    }

    public Integer getProgramType()
    {
        return programType;
    }

    private List<Program> programs = new ArrayList<Program>();

    public List<Program> getPrograms()
    {
        return programs;
    }

    private Map<String, Set<String>> programAssociations = new HashMap<String, Set<String>>();

    public Map<String, Set<String>> getProgramAssociations()
    {
        return programAssociations;
    }

    private Set<OptionSet> optionSets = new HashSet<OptionSet>();

    public Set<OptionSet> getOptionSets()
    {
        return optionSets;
    }

    private Boolean usernames = false;

    public boolean getUsernames()
    {
        return usernames;
    }

    // -------------------------------------------------------------------------
    // Action Impl
    // -------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        programs = new ArrayList<Program>( programService.getProgramsByCurrentUser( programType ) );

        for ( Program program : programs )
        {
            Set<OrganisationUnit> organisationUnits = program.getOrganisationUnits();
            programAssociations.put( program.getUid(), new HashSet<String>() );

            for ( OrganisationUnit organisationUnit : organisationUnits )
            {
                programAssociations.get( program.getUid() ).add( organisationUnit.getUid() );
            }

            populateOptionSets( program );
        }

        return SUCCESS;
    }

    private void populateOptionSets( Program program )
    {
        for ( ProgramStage programStage : program.getProgramStages() )
        {
            Set<ProgramStageDataElement> programStageDataElements = programStage.getProgramStageDataElements();

            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                if ( programStageDataElement.getDataElement().getOptionSet() != null )
                {
                    optionSets.add( programStageDataElement.getDataElement().getOptionSet() );
                }

                if ( programStageDataElement.getDataElement().getType().equals( DataElement.VALUE_TYPE_USER_NAME ) )
                {
                    usernames = true;
                }
            }
        }
    }
}
