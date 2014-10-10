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
import static org.hisp.dhis.program.ProgramExpression.OBJECT_PROGRAM_STAGE;
import static org.hisp.dhis.program.ProgramExpression.SEPARATOR_ID;
import static org.hisp.dhis.program.ProgramExpression.SEPARATOR_OBJECT;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * @version $ DefaultProgramValidationService.java Apr 28, 2011 10:36:50 AM $
 */
@Transactional
public class DefaultProgramValidationService
    implements ProgramValidationService
{
    private final String regExp = "\\[(" + OBJECT_PROGRAM_STAGE_DATAELEMENT + "|" + OBJECT_PROGRAM_STAGE + ")"
        + SEPARATOR_OBJECT + "([a-zA-Z0-9\\- ]+[" + SEPARATOR_ID + "[a-zA-Z0-9\\- ]*]*)" + "\\]";

    private ProgramValidationStore validationStore;

    private ProgramStageService programStageService;

    private DataElementService dataElementService;

    private ProgramExpressionService expressionService;

    private PatientDataValueService patientDataValueService;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setValidationStore( ProgramValidationStore validationStore )
    {
        this.validationStore = validationStore;
    }

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    public void setExpressionService( ProgramExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------
    @Override
    public int addProgramValidation( ProgramValidation programValidation )
    {
        return validationStore.save( programValidation );
    }

    @Override
    public void updateProgramValidation( ProgramValidation programValidation )
    {
        validationStore.update( programValidation );
    }

    @Override
    public void deleteProgramValidation( ProgramValidation programValidation )
    {
        validationStore.delete( programValidation );
    }

    @Override
    public Collection<ProgramValidation> getAllProgramValidation()
    {
        return validationStore.getAll();
    }

    @Override
    public ProgramValidation getProgramValidation( int id )
    {
        return validationStore.get( id );
    }

    @Override
    public Collection<ProgramValidationResult> validate( Collection<ProgramValidation> validation,
        ProgramStageInstance programStageInstance )
    {
        Collection<ProgramValidationResult> result = new HashSet<ProgramValidationResult>();

        // ---------------------------------------------------------------------
        // Get patient-data-values
        // ---------------------------------------------------------------------

        Program program = programStageInstance.getProgramInstance().getProgram();
        Collection<PatientDataValue> patientDataValues = null;
        if ( program.isSingleEvent() )
        {
            patientDataValues = patientDataValueService.getPatientDataValues( programStageInstance );
        }
        else
        {
            patientDataValues = patientDataValueService.getPatientDataValues( programStageInstance.getProgramInstance()
                .getProgramStageInstances() );
        }

        Map<String, String> patientDataValueMap = new HashMap<String, String>( patientDataValues.size() );
        for ( PatientDataValue patientDataValue : patientDataValues )
        {
            String key = patientDataValue.getProgramStageInstance().getProgramStage().getId() + "."
                + patientDataValue.getDataElement().getId();
            patientDataValueMap.put( key, patientDataValue.getValue() );
        }

        // ---------------------------------------------------------------------
        // Validate rules
        // ---------------------------------------------------------------------

        for ( ProgramValidation validate : validation )
        {
            String leftSideValue = expressionService.getProgramExpressionValue( validate.getLeftSide(),
                programStageInstance, patientDataValueMap );
            String rightSideValue = expressionService.getProgramExpressionValue( validate.getRightSide(),
                programStageInstance, patientDataValueMap );
            String operator = validate.getOperator().getMathematicalOperator();
            
            if ( (leftSideValue != null && rightSideValue != null && !((operator.equals( "==" ) && leftSideValue
                .compareTo( rightSideValue ) == 0)
                || (operator.equals( "<" ) && leftSideValue.compareTo( rightSideValue ) < 0)
                || (operator.equals( "<=" ) && (leftSideValue.compareTo( rightSideValue ) <= 0))
                || (operator.equals( ">" ) && leftSideValue.compareTo( rightSideValue ) > 0)
                || (operator.equals( ">=" ) && leftSideValue.compareTo( rightSideValue ) >= 0) || (operator
                .equals( "!=" ) && leftSideValue.compareTo( rightSideValue ) == 0))) )
            {
                ProgramValidationResult validationResult = new ProgramValidationResult( programStageInstance, validate,
                    leftSideValue, rightSideValue );
                result.add( validationResult );
            }
        }

        return result;
    }

    public Collection<ProgramValidation> getProgramValidation( Program program )
    {
        return validationStore.get( program );
    }

    public Collection<ProgramValidation> getProgramValidation( ProgramStageDataElement psdataElement )
    {
        Collection<ProgramValidation> programValidation = validationStore.get( psdataElement.getProgramStage()
            .getProgram() );
        Collection<ProgramValidation> result = new HashSet<ProgramValidation>();

        for ( ProgramValidation validation : programValidation )
        {
            Collection<DataElement> dataElements = getDataElementInExpression( validation );
            Collection<ProgramStage> programStages = getProgramStageInExpression( validation );

            if ( dataElements.contains( psdataElement.getDataElement() )
                && programStages.contains( psdataElement.getProgramStage() ) )
            {
                result.add( validation );
            }
        }

        return result;
    }

    public Collection<ProgramValidation> getProgramValidation( ProgramStage programStage )
    {
        Collection<ProgramValidation> programValidation = getProgramValidation( programStage.getProgram() );
        
        Iterator<ProgramValidation> iter = programValidation.iterator();

        Pattern pattern = Pattern.compile( regExp );

        while ( iter.hasNext() )
        {
            ProgramValidation validation = iter.next();

            String expression = validation.getLeftSide().getExpression() + " "
                + validation.getRightSide().getExpression();
            Matcher matcher = pattern.matcher( expression );
            
            boolean flag = false;
            while ( matcher.find() )
            {
                String match = matcher.group();
                match = match.replaceAll( "[\\[\\]]", "" );

                String[] info = match.split( SEPARATOR_OBJECT );
                String[] ids = info[1].split( SEPARATOR_ID );

                int programStageId = Integer.parseInt( ids[0] );
                
                if ( programStageId == programStage.getId() )
                {
                    flag = true;
                    break;
                }
            }

            if ( !flag )
            {
                iter.remove();
            }
        }

        return programValidation;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Collection<DataElement> getDataElementInExpression( ProgramValidation programValidation )
    {
        Collection<DataElement> dataElements = new HashSet<DataElement>();

        Pattern pattern = Pattern.compile( regExp );
        String expression = programValidation.getLeftSide().getExpression() + " "
            + programValidation.getRightSide().getExpression();
        Matcher matcher = pattern.matcher( expression );
        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );
            String[] ids = info[1].split( SEPARATOR_ID );

            int dataElementId = Integer.parseInt( ids[1] );
            DataElement dataElement = dataElementService.getDataElement( dataElementId );

            dataElements.add( dataElement );
        }

        return dataElements;
    }

    private Collection<ProgramStage> getProgramStageInExpression( ProgramValidation programValidation )
    {
        Collection<ProgramStage> programStages = new HashSet<ProgramStage>();

        Pattern pattern = Pattern.compile( regExp );
        String expression = programValidation.getLeftSide().getExpression() + " "
            + programValidation.getRightSide().getExpression();
        Matcher matcher = pattern.matcher( expression );

        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );
            String[] ids = info[1].split( SEPARATOR_ID );

            int programStageId = Integer.parseInt( ids[0] );
            ProgramStage programStage = programStageService.getProgramStage( programStageId );

            programStages.add( programStage );
        }

        return programStages;
    }

}
