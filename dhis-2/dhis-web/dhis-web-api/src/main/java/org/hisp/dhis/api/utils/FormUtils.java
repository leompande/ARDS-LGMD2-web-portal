package org.hisp.dhis.api.utils;

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

import org.hisp.dhis.api.webdomain.form.Field;
import org.hisp.dhis.api.webdomain.form.Form;
import org.hisp.dhis.api.webdomain.form.Group;
import org.hisp.dhis.api.webdomain.form.InputType;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageSection;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class FormUtils
{
    public static Form fromDataSet( DataSet dataSet )
    {
        Form form = new Form();
        form.setLabel( dataSet.getDisplayName() );

        form.getOptions().put( "periodType", dataSet.getPeriodType().getName() );
        form.getOptions().put( "allowFuturePeriods", dataSet.isAllowFuturePeriods() );

        if ( dataSet.getSections().size() > 0 )
        {
            for ( Section section : dataSet.getSections() )
            {
                List<Field> fields = inputsFromDataElements( new ArrayList<DataElement>( section.getDataElements() ), new ArrayList<DataElementOperand>( section.getGreyedFields() ) );

                if ( !fields.isEmpty() )
                {
                    Group s = new Group();
                    s.setLabel( section.getDisplayName() );
                    s.setFields( fields );
                    form.getGroups().add( s );
                }
            }
        }
        else
        {
            List<Field> fields = inputsFromDataElements( new ArrayList<DataElement>( dataSet.getDataElements() ) );

            if ( !fields.isEmpty() )
            {
                Group s = new Group();
                s.setLabel( "default" );
                s.setFields( fields );
                form.getGroups().add( s );
            }
        }

        return form;
    }


    public static Form fromProgram( Program program )
    {
        Assert.notNull( program );

        Form form = new Form();
        form.setLabel( program.getDisplayName() );

        if ( !StringUtils.isEmpty( program.getDescription() ) )
        {
            form.getOptions().put( "description", program.getDescription() );
        }

        if ( !StringUtils.isEmpty( program.getDateOfEnrollmentDescription() ) )
        {
            form.getOptions().put( "dateOfEnrollmentDescription", program.getDateOfEnrollmentDescription() );
        }

        if ( !StringUtils.isEmpty( program.getDateOfIncidentDescription() ) )
        {
            form.getOptions().put( "dateOfIncidentDescription", program.getDateOfIncidentDescription() );
        }

        form.getOptions().put( "type", Program.TYPE_LOOKUP.get( program.getType() ) );

        ProgramStage programStage = program.getProgramStageByStage( 1 );

        if ( programStage == null )
        {
            if ( (Program.SINGLE_EVENT_WITHOUT_REGISTRATION == program.getType()
                || Program.SINGLE_EVENT_WITH_REGISTRATION == program.getType()) )
            {
                throw new NullPointerException();
            }
            else
            {
                return form;
            }
        }

        form.getOptions().put( "captureCoordinates", programStage.getCaptureCoordinates() );

        if ( programStage.getProgramStageSections().size() > 0 )
        {
            for ( ProgramStageSection section : programStage.getProgramStageSections() )
            {
                List<Field> fields = inputsFromProgramStageDataElements( section.getProgramStageDataElements() );

                if ( !fields.isEmpty() )
                {
                    Group s = new Group();
                    s.setLabel( section.getDisplayName() );
                    s.setFields( fields );
                    form.getGroups().add( s );
                }
            }
        }
        else
        {
            List<Field> fields = inputsFromProgramStageDataElements(
                new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() ) );

            if ( !fields.isEmpty() )
            {
                Group s = new Group();
                s.setLabel( "default" );
                s.setFields( fields );
                form.getGroups().add( s );
            }
        }

        return form;
    }

    private static List<Field> inputsFromProgramStageDataElements( List<ProgramStageDataElement> programStageDataElements )
    {
        List<DataElement> dataElements = new ArrayList<DataElement>();

        for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
        {
            dataElements.add( programStageDataElement.getDataElement() );
        }

        return inputsFromDataElements( dataElements, new ArrayList<DataElementOperand>() );
    }

    private static List<Field> inputsFromDataElements( List<DataElement> dataElements )
    {
        return inputsFromDataElements( dataElements, new ArrayList<DataElementOperand>() );
    }

    private static List<Field> inputsFromDataElements( List<DataElement> dataElements, final List<DataElementOperand> greyedFields )
    {
        List<Field> fields = new ArrayList<Field>();

        for ( DataElement dataElement : dataElements )
        {
            for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo().getSortedOptionCombos() )
            {
                if ( !isDisabled( dataElement, categoryOptionCombo, greyedFields ) )
                {
                    Field field = new Field();

                    if ( categoryOptionCombo.isDefault() )
                    {
                        field.setLabel( dataElement.getDisplayName() );
                    }
                    else
                    {
                        field.setLabel( dataElement.getDisplayName() + " " + categoryOptionCombo.getDisplayName() );
                    }

                    field.setDataElement( dataElement.getUid() );
                    field.setCategoryOptionCombo( categoryOptionCombo.getUid() );
                    field.setType( inputTypeFromDataElement( dataElement ) );

                    if ( dataElement.getOptionSet() != null )
                    {
                        field.setOptionSet( dataElement.getOptionSet().getUid() );
                    }

                    fields.add( field );
                }
            }
        }

        return fields;
    }

    private static boolean isDisabled( DataElement dataElement, DataElementCategoryOptionCombo dataElementCategoryOptionCombo, List<DataElementOperand> greyedFields )
    {
        for ( DataElementOperand operand : greyedFields )
        {
            if ( dataElement.getUid().equals( operand.getDataElement().getUid() )
                && dataElementCategoryOptionCombo.getUid().equals( operand.getCategoryOptionCombo().getUid() ) )
            {
                return true;
            }
        }

        return false;
    }

    private static InputType inputTypeFromDataElement( DataElement dataElement )
    {
        if ( DataElement.VALUE_TYPE_STRING.equals( dataElement.getType() ) )
        {
            if ( DataElement.VALUE_TYPE_TEXT.equals( dataElement.getTextType() ) )
            {
                return InputType.TEXT;
            }
            if ( DataElement.VALUE_TYPE_LONG_TEXT.equals( dataElement.getTextType() ) )
            {
                return InputType.LONG_TEXT;
            }
        }
        else if ( DataElement.VALUE_TYPE_INT.equals( dataElement.getType() ) )
        {
            if ( DataElement.VALUE_TYPE_NUMBER.equals( dataElement.getNumberType() ) )
            {
                return InputType.NUMBER;
            }
            else if ( DataElement.VALUE_TYPE_INT.equals( dataElement.getNumberType() ) )
            {
                return InputType.INTEGER;
            }
            else if ( DataElement.VALUE_TYPE_POSITIVE_INT.equals( dataElement.getNumberType() ) )
            {
                return InputType.INTEGER_POSITIVE;
            }
            else if ( DataElement.VALUE_TYPE_ZERO_OR_POSITIVE_INT.equals( dataElement.getNumberType() ) )
            {
                return InputType.INTEGER_ZERO_OR_POSITIVE;
            }
            else if ( DataElement.VALUE_TYPE_NEGATIVE_INT.equals( dataElement.getNumberType() ) )
            {
                return InputType.INTEGER_NEGATIVE;
            }
        }
        else if ( DataElement.VALUE_TYPE_BOOL.equals( dataElement.getType() ) )
        {
            return InputType.BOOLEAN;
        }
        else if ( DataElement.VALUE_TYPE_TRUE_ONLY.equals( dataElement.getType() ) )
        {
            return InputType.TRUE_ONLY;
        }
        else if ( DataElement.VALUE_TYPE_DATE.equals( dataElement.getType() ) )
        {
            return InputType.DATE;
        }

        return null;
    }

    public static void fillWithDataValues( Form form, Collection<DataValue> dataValues )
    {
        Map<String, Field> cacheMap = buildCacheMap( form );

        for ( DataValue dataValue : dataValues )
        {
            DataElement dataElement = dataValue.getDataElement();
            DataElementCategoryOptionCombo categoryOptionCombo = dataValue.getCategoryOptionCombo();

            cacheMap.get( dataElement.getUid() + "-" + categoryOptionCombo.getUid() ).setValue( dataValue.getValue() );
        }
    }

    private static Map<String, Field> buildCacheMap( Form form )
    {
        Map<String, Field> cacheMap = new HashMap<String, Field>();

        for ( Group group : form.getGroups() )
        {
            for ( Field field : group.getFields() )
            {
                cacheMap.put( field.getDataElement() + "-" + field.getCategoryOptionCombo(), field );
            }
        }

        return cacheMap;
    }
}
