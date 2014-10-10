package org.hisp.dhis.program;

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

import static org.hisp.dhis.program.ProgramExpression.OBJECT_PROGRAM_STAGE_DATAELEMENT;
import static org.hisp.dhis.program.ProgramExpression.SEPARATOR_ID;
import static org.hisp.dhis.program.ProgramExpression.SEPARATOR_OBJECT;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.system.util.DateUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version DefaultProgramExpressionService.java 3:06:24 PM Nov 8, 2012 $
 */
@Transactional
public class DefaultProgramExpressionService
    implements ProgramExpressionService
{
    private final String regExp = "\\[" + OBJECT_PROGRAM_STAGE_DATAELEMENT + SEPARATOR_OBJECT + "([a-zA-Z0-9\\- ]+["
        + SEPARATOR_ID + "[a-zA-Z0-9\\- ]+]*)" + "\\]";

    private final String INVALID_CONDITION = "Invalid condition";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericStore<ProgramExpression> programExpressionStore;

    public void setProgramExpressionStore( GenericStore<ProgramExpression> programExpressionStore )
    {
        this.programExpressionStore = programExpressionStore;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // ProgramExpression CRUD operations
    // -------------------------------------------------------------------------

    @Override
    public int addProgramExpression( ProgramExpression programExpression )
    {
        return programExpressionStore.save( programExpression );
    }

    @Override
    public void updateProgramExpression( ProgramExpression programExpression )
    {
        programExpressionStore.update( programExpression );
    }

    @Override
    public void deleteProgramExpression( ProgramExpression programExpression )
    {
        programExpressionStore.delete( programExpression );
    }

    @Override
    public ProgramExpression getProgramExpression( int id )
    {
        return programExpressionStore.get( id );
    }

    @Override
    public Collection<ProgramExpression> getAllProgramExpressions()
    {
        return programExpressionStore.getAll();
    }

    @Override
    public String getProgramExpressionValue( ProgramExpression programExpression,
        ProgramStageInstance programStageInstance, Map<String, String> patientDataValueMap )
    {
        String value = "";
        if ( programExpression.getExpression().contains( ProgramExpression.DUE_DATE ) )
        {
            value = DateUtils.getMediumDateString( programStageInstance.getDueDate() );
        }
        else if ( programExpression.getExpression().contains( ProgramExpression.REPORT_DATE ) )
        {
            value = DateUtils.getMediumDateString( programStageInstance.getExecutionDate() );
        }
        else
        {
            StringBuffer description = new StringBuffer();
            Pattern pattern = Pattern.compile( regExp );
            Matcher matcher = pattern.matcher( programExpression.getExpression() );

            while ( matcher.find() )
            {
                String key = matcher.group().replaceAll( "[\\[\\]]", "" ).split( SEPARATOR_OBJECT )[1];
                String dataValue = patientDataValueMap.get( key );
                if ( dataValue == null )
                {
                    return null;
                }

                matcher.appendReplacement( description, dataValue );
            }

            matcher.appendTail( description );

            value = description.toString();
        }

        return value;
    }

    @Override
    public String getExpressionDescription( String programExpression )
    {
        StringBuffer description = new StringBuffer();

        Pattern pattern = Pattern.compile( regExp );
        Matcher matcher = pattern.matcher( programExpression );
        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );
            String[] ids = info[1].split( SEPARATOR_ID );

            String programStageId = ids[0];
            ProgramStage programStage = programStageService.getProgramStage( Integer.parseInt( programStageId ) );

            int dataElementId = Integer.parseInt( ids[1] );
            DataElement dataElement = dataElementService.getDataElement( dataElementId );

            if ( programStage == null || dataElement == null )
            {
                return INVALID_CONDITION;
            }

            matcher.appendReplacement( description,
                "[" + ProgramExpression.OBJECT_PROGRAM_STAGE_DATAELEMENT + ProgramExpression.SEPARATOR_OBJECT
                    + programStage.getDisplayName() + SEPARATOR_ID + dataElement.getName() + "]" );
        }

        matcher.appendTail( description );

        return description.toString();
    }
}
