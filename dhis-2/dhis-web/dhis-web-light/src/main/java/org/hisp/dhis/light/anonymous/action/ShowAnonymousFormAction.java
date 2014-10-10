package org.hisp.dhis.light.anonymous.action;

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
import java.util.Collections;
import java.util.Comparator;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.user.UserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Nguyen Kim Lai
 *
 * @version $ ShowAnonymousFormAction.java $
 */
public class ShowAnonymousFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private UserService userService;
    
    public UserService getUserService()
    {
        return userService;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }
    
    public Integer getProgramId()
    {
        return programId;
    }

    private ProgramStage programStage;

    public ProgramStage getProgramStage()

    {
        return programStage;
    }
    
    private Program program;

    public Program getProgram()

    {
        return program;
    }
    
    private int orgUnitId;
    
    public int getOrgUnitId()
    {
        return orgUnitId;
    }

    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private ArrayList<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>();

    public ArrayList<ProgramStageDataElement> getProgramStageDataElements()
    {
        return this.programStageDataElements;
    }
    
    public void setProgramStageDataElements( ArrayList<ProgramStageDataElement> programStageDataElements )
    {
        this.programStageDataElements = programStageDataElements;
    }
    
    static final Comparator<ProgramStageDataElement> OrderBySortOrder = new Comparator<ProgramStageDataElement>()
    {
        public int compare( ProgramStageDataElement i1, ProgramStageDataElement i2 )
        {
            return i1.getSortOrder().compareTo( i2.getSortOrder() );
        }
    };
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    
    @Override
    public String execute()
        throws Exception
    {
        
        program = programService.getProgram( programId );
        
        programStage = program.getProgramStages().iterator().next();
        
        programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
            
        Collections.sort( programStageDataElements, OrderBySortOrder );
        
        return SUCCESS;
    }

}
