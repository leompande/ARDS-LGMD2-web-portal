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

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.system.util.DateUtils;
import org.nfunk.jep.JEP;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * @version $ DefaultProgramIndicatorService.java Apr 16, 2013 1:29:00 PM $
 */

@Transactional
public class DefaultProgramIndicatorService
    implements ProgramIndicatorService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramIndicatorStore programIndicatorStore;

    public void setProgramIndicatorStore( ProgramIndicatorStore programIndicatorStore )
    {
        this.programIndicatorStore = programIndicatorStore;
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

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public int addProgramIndicator( ProgramIndicator programIndicator )
    {
        return programIndicatorStore.save( programIndicator );
    }

    @Override
    public void updateProgramIndicator( ProgramIndicator programIndicator )
    {
        programIndicatorStore.update( programIndicator );
    }

    @Override
    public void deleteProgramIndicator( ProgramIndicator programIndicator )
    {
        programIndicatorStore.delete( programIndicator );
    }

    @Override
    public ProgramIndicator getProgramIndicator( int id )
    {
        return i18n( i18nService, programIndicatorStore.get( id ) );
    }

    @Override
    public ProgramIndicator getProgramIndicator( String name )
    {
        return i18n( i18nService, programIndicatorStore.getByName( name ) );
    }

    @Override
    public ProgramIndicator getProgramIndicatorByUid( String uid )
    {
        return i18n( i18nService, programIndicatorStore.getByUid( uid ) );
    }

    @Override
    public ProgramIndicator getProgramIndicatorByShortName( String shortName )
    {
        return i18n( i18nService, programIndicatorStore.getByShortName( shortName ) );
    }

    @Override
    public Collection<ProgramIndicator> getAllProgramIndicators()
    {
        return i18n( i18nService, programIndicatorStore.getAll() );
    }

    @Override
    public Collection<ProgramIndicator> getProgramIndicators( Program program )
    {
        return i18n( i18nService, programIndicatorStore.getByProgram( program ) );
    }

    @Override
    public String getProgramIndicatorValue( ProgramInstance programInstance, ProgramIndicator programIndicator )
    {
        Double value = getValue( programInstance, programIndicator.getValueType(), programIndicator.getExpression() );

        if ( value != null )
        {
            if ( programIndicator.getValueType().equals( ProgramIndicator.VALUE_TYPE_DATE ) )
            {
                Date rootDate = new Date();

                if ( ProgramIndicator.INCIDENT_DATE.equals( programIndicator.getRootDate() ) )
                {
                    rootDate = programInstance.getDateOfIncident();
                }
                else if ( ProgramIndicator.ENROLLEMENT_DATE.equals( programIndicator.getRootDate() ) )
                {
                    rootDate = programInstance.getEnrollmentDate();
                }

                Date date = DateUtils.getDateAfterAddition( rootDate, value.intValue() );

                return DateUtils.getMediumDateString( date );
            }

            return Math.floor( value ) + "";
        }

        return null;
    }

    @Override
    public Map<String, String> getProgramIndicatorValues( ProgramInstance programInstance )
    {
        Map<String, String> result = new HashMap<String, String>();

        Collection<ProgramIndicator> programIndicators = programIndicatorStore.getByProgram( programInstance
            .getProgram() );

        for ( ProgramIndicator programIndicator : programIndicators )
        {
            result
                .put( programIndicator.getDisplayName(), getProgramIndicatorValue( programInstance, programIndicator ) );
        }

        return result;
    }

    public String getExpressionDescription( String expression )
    {
        StringBuffer description = new StringBuffer();

        Pattern patternCondition = Pattern.compile( ProgramIndicator.regExp );

        Matcher matcher = patternCondition.matcher( expression );

        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( ProgramIndicator.SEPARATOR_OBJECT );

            String[] ids = info[1].split( ProgramIndicator.SEPARATOR_ID );

            int programStageId = Integer.parseInt( ids[0] );
            ProgramStage programStage = programStageService.getProgramStage( programStageId );
            String programStageName = "The program stage not exist";
            if ( programStage != null )
            {
                programStageName = programStage.getDisplayName();
            }

            int dataElementId = Integer.parseInt( ids[1] );
            DataElement dataElement = dataElementService.getDataElement( dataElementId );
            String dataelementName = "The data element not exist";
            if ( dataElement != null )
            {
                dataelementName = dataElement.getDisplayName();
            }

            matcher.appendReplacement( description, "[" + ProgramIndicator.OBJECT_PROGRAM_STAGE_DATAELEMENT + ProgramIndicator.SEPARATOR_OBJECT + programStageName + ProgramIndicator.SEPARATOR_ID
                + dataelementName + "]" );
        }

        matcher.appendTail( description );

        return description.toString();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Double getValue( ProgramInstance programInstance, String valueType, String expression )
    {
        String value = "";

        if ( valueType.equals( ProgramIndicator.VALUE_TYPE_INT ) )
        {
            Date currentDate = new Date();
            expression = expression.replaceAll( ProgramIndicator.ENROLLEMENT_DATE,
                DateUtils.daysBetween( programInstance.getEnrollmentDate(), currentDate ) + "" );
            expression = expression.replaceAll( ProgramIndicator.INCIDENT_DATE,
                DateUtils.daysBetween( programInstance.getDateOfIncident(), currentDate ) + "" );
            expression = expression.replaceAll( ProgramIndicator.CURRENT_DATE, "0" );
        }

        StringBuffer description = new StringBuffer();

        Pattern pattern = Pattern.compile( ProgramIndicator.regExp );
        Matcher matcher = pattern.matcher( expression );
        while ( matcher.find() )
        {
            DataElement dataElement = null;

            String key = matcher.group().replaceAll( "[\\[\\]]", "" ).split( ProgramIndicator.SEPARATOR_OBJECT )[1];
            String[] infor = key.split( ProgramIndicator.SEPARATOR_ID );

            Integer programStageId = Integer.parseInt( infor[0] );
            ProgramStage programStage = programStageService.getProgramStage( programStageId );

            ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance(
                programInstance, programStage );

            Integer dataElementId = Integer.parseInt( infor[1] );
            dataElement = dataElementService.getDataElement( dataElementId );

            PatientDataValue dataValue = patientDataValueService
                .getPatientDataValue( programStageInstance, dataElement );

            if ( dataValue == null )
            {
                return null;
            }

            value = dataValue.getValue();

            if ( valueType.equals( ProgramIndicator.VALUE_TYPE_INT )
                && (dataElement == null || dataElement.getType().equals( DataElement.VALUE_TYPE_DATE )) )
            {
                value = DateUtils.daysBetween( new Date(), DateUtils.getDefaultDate( value ) ) + " ";
            }

            matcher.appendReplacement( description, value );

        }
        matcher.appendTail( description );

        final JEP parser = new JEP();
        parser.parseExpression( description.toString() );

        return parser.getValue();

    }
}
