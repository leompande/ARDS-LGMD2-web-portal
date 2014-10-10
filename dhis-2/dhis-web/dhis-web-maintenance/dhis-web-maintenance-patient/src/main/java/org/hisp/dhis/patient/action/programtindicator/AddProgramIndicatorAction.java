package org.hisp.dhis.patient.action.programtindicator;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramIndicator;
import org.hisp.dhis.program.ProgramIndicatorService;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ AddProgramIndicatorAction.java Apr 16, 2013 3:24:51 PM $
 */
public class AddProgramIndicatorAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramIndicatorService programIndicatorService;

    public void setProgramIndicatorService( ProgramIndicatorService programIndicatorService )
    {
        this.programIndicatorService = programIndicatorService;
    }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public Integer getProgramId()
    {
        return programId;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String valueType;

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    private String expression;

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    private String rootDate;

    public void setRootDate( String rootDate )
    {
        this.rootDate = rootDate;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        code = (code == null && code.trim().length() == 0) ? null : code;
        expression = expression.trim();

        if ( valueType.equals( ProgramIndicator.VALUE_TYPE_DATE ) )
        {
            Pattern pattern = Pattern.compile( "[(+|-|*|\\)]+" );
            Matcher matcher = pattern.matcher( expression );
            if ( matcher.find() && matcher.start() != 0 )
            {
                expression = "+" + expression;
            }
        }

        Program program = programService.getProgram( programId );
        ProgramIndicator programIndicator = new ProgramIndicator( name, description, valueType, expression );
        programIndicator.setShortName( shortName );
        programIndicator.setCode( code );
        programIndicator.setRootDate( rootDate );
        programIndicator.setProgram( program );

        programIndicatorService.addProgramIndicator( programIndicator );

        return SUCCESS;
    }

}
