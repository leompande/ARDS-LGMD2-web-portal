package org.hisp.dhis.patient;

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

import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version DefaultPatientRegistrationFormService.java 9:44:29 AM Jan 31, 2013 $
 */
@Transactional
public class DefaultPatientRegistrationFormService
    implements PatientRegistrationFormService
{
    private static final String TAG_OPEN = "<";

    private static final String TAG_CLOSE = "/>";

    private static final String PROGRAM_INCIDENT_DATE = "dateOfIncident";

    private static final String PROGRAM_ENROLLMENT_DATE = "enrollmentDate";

    private static final String DOB_FIELD = "@DOB_FIELD";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientRegistrationFormStore formStore;

    public void setFormStore( PatientRegistrationFormStore formStore )
    {
        this.formStore = formStore;
    }

    private PatientIdentifierTypeService identifierTypeService;

    public void setIdentifierTypeService( PatientIdentifierTypeService identifierTypeService )
    {
        this.identifierTypeService = identifierTypeService;
    }

    private PatientIdentifierService identifierService;

    public void setIdentifierService( PatientIdentifierService identifierService )
    {
        this.identifierService = identifierService;
    }

    private PatientAttributeService attributeService;

    public void setAttributeService( PatientAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private PatientAttributeValueService attributeValueService;

    public void setAttributeValueService( PatientAttributeValueService attributeValueService )
    {
        this.attributeValueService = attributeValueService;
    }

    // -------------------------------------------------------------------------
    // PatientRegistrationForm implementation
    // -------------------------------------------------------------------------

    @Override
    public int savePatientRegistrationForm( PatientRegistrationForm registrationForm )
    {
        return formStore.save( registrationForm );
    }

    @Override
    public void deletePatientRegistrationForm( PatientRegistrationForm registrationForm )
    {
        formStore.delete( registrationForm );
    }

    @Override
    public void updatePatientRegistrationForm( PatientRegistrationForm registrationForm )
    {
        formStore.update( registrationForm );
    }

    @Override
    public PatientRegistrationForm getPatientRegistrationForm( int id )
    {
        return formStore.get( id );
    }

    @Override
    public Collection<PatientRegistrationForm> getAllPatientRegistrationForms()
    {
        return formStore.getAll();
    }

    @Override
    public PatientRegistrationForm getPatientRegistrationForm( Program program )
    {
        return formStore.get( program );
    }

    @Override
    public PatientRegistrationForm getCommonPatientRegistrationForm()
    {
        return formStore.getCommonForm();
    }

    @Override
    public String prepareDataEntryFormForAdd( String htmlCode, Program program, Collection<User> healthWorkers,
        Patient patient, ProgramInstance programInstance, I18n i18n, I18nFormat format )
    {
        int index = 1;

        StringBuffer sb = new StringBuffer();

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        boolean hasBirthdate = false;
        boolean hasAge = false;

        while ( inputMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String inputHtml = inputMatcher.group();
            Matcher fixedAttrMatcher = FIXED_ATTRIBUTE_PATTERN.matcher( inputHtml );
            Matcher identifierMatcher = IDENTIFIER_PATTERN.matcher( inputHtml );
            Matcher dynamicAttrMatcher = DYNAMIC_ATTRIBUTE_PATTERN.matcher( inputHtml );
            Matcher programMatcher = PROGRAM_PATTERN.matcher( inputHtml );
            Matcher suggestedMarcher = SUGGESTED_VALUE_PATTERN.matcher( inputHtml );

            index++;

            String hidden = "";
            String style = "";
            Matcher classMarcher = CLASS_PATTERN.matcher( inputHtml );
            if ( classMarcher.find() )
            {
                hidden = classMarcher.group( 2 );
            }

            Matcher styleMarcher = STYLE_PATTERN.matcher( inputHtml );
            if ( styleMarcher.find() )
            {
                style = styleMarcher.group( 2 );
            }

            if ( fixedAttrMatcher.find() && fixedAttrMatcher.groupCount() > 0 )
            {
                String fixedAttr = fixedAttrMatcher.group( 1 );

                // Get value
                String value = "";
                if ( patient != null )
                {
                    Object object = getValueFromPatient( fixedAttr, patient );
                    if ( object != null )
                    {
                        if ( object instanceof Date )
                        {
                            value = format.formatDate( (Date) object );
                        }
                        else if ( object instanceof User )
                        {
                            value = ((User) object).getId() + "";
                        }
                        else
                        {
                            value = object.toString();
                        }
                    }
                }
                else if ( suggestedMarcher.find() )
                {
                    value = suggestedMarcher.group( 2 );
                }

                inputHtml = getFixedAttributeField( inputHtml, fixedAttr, value.toString(), healthWorkers, i18n, index,
                    hidden, style );
            }
            else if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                String uid = identifierMatcher.group( 1 );
                PatientIdentifierType identifierType = identifierTypeService.getPatientIdentifierTypeByUid( uid );
                if ( identifierType == null )
                {
                    inputHtml = "<input value='[" + i18n.getString( "missing_patient_identifier_type" ) + " " + uid
                        + "]' title='[" + i18n.getString( "missing_patient_identifier_type" ) + " " + uid + "]'>/";
                }
                else
                {
                    int id = identifierType.getId();
                    // Get value
                    String value = "";
                    if ( patient != null )
                    {
                        PatientIdentifier patientIdentifier = identifierService.getPatientIdentifier( identifierType,
                            patient );

                        if ( patientIdentifier != null )
                        {
                            value = patientIdentifier.getIdentifier();
                        }
                    }

                    inputHtml = "<input id=\"iden" + id + "\" name=\"iden" + id + "\" tabindex=\"" + index
                        + "\" value=\"" + value + "\" style=\"" + style + "\"";

                    inputHtml += "class=\"" + hidden + " {validate:{required:" + identifierType.isMandatory() + ",";
                    if ( identifierType.getNoChars() != null )
                    {
                        inputHtml += "maxlength:" + identifierType.getNoChars() + ",";
                    }

                    if ( PatientIdentifierType.VALUE_TYPE_NUMBER.equals( identifierType.getType() ) )
                    {
                        inputHtml += "number:true";
                    }
                    else if ( PatientIdentifierType.VALUE_TYPE_LETTER.equals( identifierType.getType() ) )
                    {
                        inputHtml += "lettersonly:true";
                    }
                    inputHtml += "}}\" " + TAG_CLOSE;
                }
            }
            else if ( dynamicAttrMatcher.find() && dynamicAttrMatcher.groupCount() > 0 )
            {
                String uid = dynamicAttrMatcher.group( 1 );
                PatientAttribute attribute = attributeService.getPatientAttribute( uid );

                if ( attribute == null )
                {
                    inputHtml = "<input value='[" + i18n.getString( "missing_patient_attribute" ) + " " + uid
                        + "]' title='[" + i18n.getString( "missing_patient_attribute" ) + " " + uid + "]'>/";
                }
                else
                {
                    // Get value
                    String value = "";
                    if ( patient != null )
                    {
                        PatientAttributeValue attributeValue = attributeValueService.getPatientAttributeValue( patient,
                            attribute );
                        if ( attributeValue != null )
                        {
                            value = attributeValue.getValue();
                        }
                    }

                    inputHtml = getAttributeField( inputHtml, attribute, value, i18n, index, hidden, style );
                }

            }
            else if ( programMatcher.find() && programMatcher.groupCount() > 0 )
            {
                String property = programMatcher.group( 1 );

                // Get value
                String value = "";
                if ( programInstance != null )
                {
                    value = format.formatDate( ((Date) getValueFromProgram( StringUtils.capitalize( property ),
                        programInstance )) );
                }

                inputHtml = "<input id=\"" + property + "\" name=\"" + property + "\" tabindex=\"" + index
                    + "\" value=\"" + value + "\" " + TAG_CLOSE;
                if ( property.equals( PROGRAM_ENROLLMENT_DATE ) )
                {
                    if ( program != null && program.getSelectEnrollmentDatesInFuture() )
                    {
                        inputHtml += "<script>datePicker(\"" + property + "\", true);</script>";
                    }
                    else
                    {
                        inputHtml += "<script>datePickerValid(\"" + property + "\", true);</script>";
                    }
                }
                else if ( property.equals( PROGRAM_INCIDENT_DATE ) )
                {
                    if ( program != null && program.getSelectIncidentDatesInFuture() )
                    {
                        inputHtml += "<script>datePicker(\"" + property + "\", true);</script>";
                    }
                    else
                    {
                        inputHtml += "<script>datePickerValid(\"" + property + "\", true);</script>";
                    }
                }
            }

            inputMatcher.appendReplacement( sb, inputHtml );
        }

        inputMatcher.appendTail( sb );

        String entryForm = sb.toString();
        String dobType = "";
        if ( hasBirthdate && hasAge )
        {
            dobType = "<select id=\'dobType\' name=\"dobType\" style=\'width:120px\' onchange=\'dobTypeOnChange(\"patientForm\")\' >";
            dobType += "     <option value=\"V\" >" + i18n.getString( "verified" ) + "</option>";
            dobType += "     <option value=\"D\" >" + i18n.getString( "declared" ) + "</option>";
            dobType += "     <option value=\"A\" >" + i18n.getString( "approximated" ) + "</option>";
            dobType += "</select>";
        }
        else if ( hasBirthdate )
        {
            dobType = "<input type=\'hidden\' id=\'dobType\' name=\"dobType\" value=\'V\'>";
        }
        else if ( hasAge )
        {
            dobType = "<input type=\'hidden\' id=\'dobType\' name=\"dobType\" value=\'A\'>";
        }

        entryForm = entryForm.replaceFirst( DOB_FIELD, dobType );
        entryForm = entryForm.replaceAll( DOB_FIELD, "" );

        return entryForm;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String getAttributeField( String inputHtml, PatientAttribute attribute, String value, I18n i18n, int index,
        String hidden, String style )
    {
        inputHtml = TAG_OPEN + "input id=\"attr" + attribute.getId() + "\" name=\"attr" + attribute.getId()
            + "\" tabindex=\"" + index + "\" style=\"" + style + "\"";

        inputHtml += "\" class=\"" + hidden + " {validate:{required:" + attribute.isMandatory();
        if ( PatientAttribute.TYPE_INT.equals( attribute.getValueType() ) )
        {
            inputHtml += ",number:true";
        }
        inputHtml += "}}\" ";

        if ( attribute.getValueType().equals( PatientAttribute.TYPE_TRUE_ONLY ) )
        {
            inputHtml += " type='checkbox' value='true' ";
            if ( value.equals( "true" ) )
            {
                inputHtml += " checked ";
            }
        }
        else if ( attribute.getValueType().equals( PatientAttribute.TYPE_BOOL ) )
        {
            inputHtml = inputHtml.replaceFirst( "input", "select" ) + ">";

            if ( value.equals( "" ) )
            {
                inputHtml += "<option value=\"\" selected>" + i18n.getString( "no_value" ) + "</option>";
                inputHtml += "<option value=\"true\">" + i18n.getString( "yes" ) + "</option>";
                inputHtml += "<option value=\"false\">" + i18n.getString( "no" ) + "</option>";
            }
            else if ( value.equals( "true" ) )
            {
                inputHtml += "<option value=\"\">" + i18n.getString( "no_value" ) + "</option>";
                inputHtml += "<option value=\"true\" selected >" + i18n.getString( "yes" ) + "</option>";
                inputHtml += "<option value=\"false\">" + i18n.getString( "no" ) + "</option>";
            }
            else if ( value.equals( "false" ) )
            {
                inputHtml += "<option value=\"\">" + i18n.getString( "no_value" ) + "</option>";
                inputHtml += "<option value=\"true\">" + i18n.getString( "yes" ) + "</option>";
                inputHtml += "<option value=\"false\" selected >" + i18n.getString( "no" ) + "</option>";
            }

            inputHtml += "</select>";
        }
        else if ( attribute.getValueType().equals( PatientAttribute.TYPE_COMBO ) )
        {
            inputHtml = inputHtml.replaceFirst( "input", "select" ) + ">";
            inputHtml += "<option value=\"\" selected>" + i18n.getString( "no_value" ) + "</option>";

            for ( PatientAttributeOption option : attribute.getAttributeOptions() )
            {
                inputHtml += "<option value=\"" + option.getId() + "\" ";
                if ( option.getName().equals( value ) )
                {
                    inputHtml += " selected ";
                }
                inputHtml += ">" + option.getName() + "</option>";
            }
            inputHtml += "</select>";
        }
        else if ( attribute.getValueType().equals( PatientAttribute.TYPE_DATE ) )
        {
            String jQueryCalendar = "<script>datePicker(\"attr" + attribute.getId() + "\", false);</script>";
            inputHtml += " value=\"" + value + "\"" + TAG_CLOSE;
            inputHtml += jQueryCalendar;
        }
        else
        {
            inputHtml += " value=\"" + value + "\"" + TAG_CLOSE;
        }
        return inputHtml;
    }

    private String getFixedAttributeField( String inputHtml, String fixedAttr, String value,
        Collection<User> healthWorkers, I18n i18n, int index, String hidden, String style )
    {
        inputHtml = TAG_OPEN + "input id=\"" + fixedAttr + "\" name=\"" + fixedAttr + "\" tabindex=\"" + index
            + "\" value=\"" + value + "\"  style=\"" + style + "\"";

        // Health-worker field
        if ( fixedAttr.equals( PatientRegistrationForm.FIXED_ATTRIBUTE_ASSOCIATE ) )
        {
            inputHtml = inputHtml.replaceFirst( "input", "select" ) + " class='" + hidden + "' >";
            inputHtml += "<option value=\"\" selected >" + i18n.getString( "please_select" ) + "</option>";

            for ( User healthWorker : healthWorkers )
            {
                inputHtml += "<option value=\"" + healthWorker.getId() + "\" ";
                if ( value.equals( healthWorker.getId() + "" ) )
                {
                    inputHtml += " selected ";
                }
                inputHtml += ">" + healthWorker.getName() + "</option>";
            }
            inputHtml += "</select>";
        }

        return inputHtml;
    }

    private Object getValueFromPatient( String property, Patient patient )
    {
        property = StringUtils.capitalize( property );

        try
        {
            return Patient.class.getMethod( "get" + property ).invoke( patient );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        return null;
    }

    private Object getValueFromProgram( String property, ProgramInstance programInstance )
    {
        try
        {
            return ProgramInstance.class.getMethod( "get" + property ).invoke( programInstance );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        return null;
    }

}
