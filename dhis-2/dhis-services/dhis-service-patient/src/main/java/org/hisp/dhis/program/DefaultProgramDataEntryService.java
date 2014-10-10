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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.BooleanUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;

/**
 * @author Chau Thu Tran
 * @version $ DefaultProgramDataEntryService.java May 26, 2011 3:59:43 PM $
 */
public class DefaultProgramDataEntryService
    implements ProgramDataEntryService
{
    private static final String EMPTY = "";

    private static final String DATA_ELEMENT_DOES_NOT_EXIST = "[ Data element does not exist ]";

    private static final String EMPTY_VALUE_TAG = "value=\"\"";

    private static final String EMPTY_TITLE_TAG = "title=\"\"";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramStageDataElementService programStageDataElementService;

    public void setProgramStageDataElementService( ProgramStageDataElementService programStageDataElementService )
    {
        this.programStageDataElementService = programStageDataElementService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public String prepareDataEntryFormForEntry( String htmlCode, Collection<PatientDataValue> dataValues, I18n i18n,
        ProgramStage programStage, ProgramStageInstance programStageInstance, OrganisationUnit organisationUnit )
    {
        Map<String, Collection<PatientDataValue>> mapDataValue = new HashMap<String, Collection<PatientDataValue>>();

        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String jQueryCalendar = "<script>$DATAPICKER-METHOD(\"$PROGRAMSTAGEID-$DATAELEMENTID-val\", false, false);</script>";

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Matcher dataElementMatcher = INPUT_PATTERN.matcher( htmlCode );
        int tabindex = 0;

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        Map<String, DataElement> dataElementMap = getDataElementMap( programStage );

        while ( dataElementMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String compulsory = "null";
            boolean allowProvidedElsewhere = false;
            String dateMethod = "datePickerValid";
            String inputHTML = dataElementMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_FIELD.matcher( inputHTML );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------

                String programStageUid = identifierMatcher.group( 1 );

                String dataElementUid = identifierMatcher.group( 2 );

                DataElement dataElement = null;

                String programStageName = programStage.getDisplayName();

                if ( !programStageUid.equals( programStage.getUid() ) )
                {
                    dataElement = dataElementService.getDataElement( dataElementUid );

                    ProgramStage otherProgramStage = programStageService.getProgramStage( programStageUid );
                    programStageName = otherProgramStage != null ? otherProgramStage.getDisplayName() : "N/A";
                }
                else
                {
                    dataElement = dataElementMap.get( dataElementUid );
                    if ( dataElement == null )
                    {
                        return i18n.getString( "some_data_element_not_exist" );
                    }

                    ProgramStageDataElement psde = programStageDataElementService.get( programStage, dataElement );

                    compulsory = BooleanUtils.toStringTrueFalse( psde.isCompulsory() );
                    allowProvidedElsewhere = psde.getAllowProvidedElsewhere();
                    if ( psde.getAllowDateInFuture() )
                    {
                        dateMethod = "datePicker";
                    }
                }

                if ( dataElement == null )
                {
                    continue;
                }

                // -------------------------------------------------------------
                // Find type of data element
                // -------------------------------------------------------------

                String dataElementType = dataElement.getDetailedNumberType();

                // -------------------------------------------------------------
                // Find existing value of data element in data set
                // -------------------------------------------------------------

                PatientDataValue patientDataValue = null;

                String dataElementValue = EMPTY;

                if ( !programStageUid.equals( programStage.getUid() ) )
                {
                    Collection<PatientDataValue> patientDataValues = mapDataValue.get( programStageUid );

                    if ( patientDataValues == null && programStageInstance != null )
                    {
                        ProgramStage otherProgramStage = programStageService.getProgramStage( programStageUid );
                        ProgramStageInstance otherProgramStageInstance = programStageInstanceService
                            .getProgramStageInstance( programStageInstance.getProgramInstance(), otherProgramStage );
                        patientDataValues = patientDataValueService.getPatientDataValues( otherProgramStageInstance );
                        mapDataValue.put( programStageUid, patientDataValues );
                    }

                    patientDataValue = getValue( patientDataValues, dataElementUid );

                    dataElementValue = patientDataValue != null ? patientDataValue.getValue() : dataElementValue;
                }
                else
                {
                    patientDataValue = getValue( dataValues, dataElementUid );

                    dataElementValue = patientDataValue != null ? patientDataValue.getValue() : dataElementValue;
                }

                // -------------------------------------------------------------
                // Insert title information - Data element id, name, type, min,
                // max
                // -------------------------------------------------------------

                if ( inputHTML.contains( "title=\"\"" ) )
                {
                    inputHTML = inputHTML.replace( "title=\"\"",
                        "title=\"[ " + dataElement.getUid() + " - " + dataElement.getName() + " - " + dataElementType
                            + " ]\" " );
                }
                else
                {
                    inputHTML += "title=\"[ " + dataElement.getUid() + " - " + dataElement.getName() + " - "
                        + dataElementType + " ]\" ";
                }

                // -------------------------------------------------------------
                // Set field for dataElement
                // -------------------------------------------------------------

                tabindex++;

                if ( dataElement.getOptionSet() != null && dataElement.getOptionSet().getOptions().size() < 7
                    && programStage.getProgram().getDataEntryMethod() )
                {
                    String idField = programStageUid + "-" + dataElementUid + "-val";
                    inputHTML = populateCustomDataEntryForOptionSet( dataElement, idField, patientDataValue, i18n );
                }
                else if ( DataElement.VALUE_TYPE_INT.equals( dataElement.getType() )
                    || DataElement.VALUE_TYPE_STRING.equals( dataElement.getType() )
                    || DataElement.VALUE_TYPE_USER_NAME.equals( dataElement.getType() ) )
                {
                    inputHTML = populateCustomDataEntryForTextBox( dataElement, inputHTML, dataElementValue );
                }
                else if ( DataElement.VALUE_TYPE_DATE.equals( dataElement.getType() ) )
                {
                    inputHTML = populateCustomDataEntryForDate( inputHTML, dataElementValue );
                }
                else if ( DataElement.VALUE_TYPE_TRUE_ONLY.equals( dataElement.getType() ) )
                {
                    inputHTML = populateCustomDataEntryForTrueOnly( dataElement, inputHTML, dataElementValue );
                }
                else if ( DataElement.VALUE_TYPE_BOOL.equals( dataElement.getType() ) )
                {
                    inputHTML = populateCustomDataEntryForBoolean( dataElement, inputHTML, dataElementValue, i18n );
                }

                // -----------------------------------------------------------
                // Check if this dataElement is from another programStage then
                // disable
                // If programStagsInstance is completed then disabled it
                // -----------------------------------------------------------

                String disabled = "";
                if ( !programStageUid.equals( programStage.getUid() ) )
                {
                    disabled = "disabled=\"\"";
                }
                else
                {
                    if ( DataElement.VALUE_TYPE_DATE.equals( dataElement.getType() ) )
                    {
                        inputHTML += jQueryCalendar;
                    }

                    if ( allowProvidedElsewhere )
                    {
                        // Add ProvidedByOtherFacility checkbox
                        inputHTML = addProvidedElsewherCheckbox( inputHTML, patientDataValue, programStage );
                    }
                }

                // -----------------------------------------------------------
                //
                // -----------------------------------------------------------

                inputHTML = inputHTML.replace( "$DATAELEMENTID", String.valueOf( dataElementUid ) );
                inputHTML = inputHTML.replace( "$VALUE", dataElementValue );
                inputHTML = inputHTML.replace( "$PROGRAMSTAGEID", String.valueOf( programStageUid ) );
                inputHTML = inputHTML.replace( "$PROGRAMSTAGENAME", programStageName );
                inputHTML = inputHTML.replace( "$DATAELEMENTNAME", dataElement.getFormNameFallback() );
                inputHTML = inputHTML.replace( "$DATAELEMENTTYPE", dataElementType );
                inputHTML = inputHTML.replace( "$DISABLED", disabled );
                inputHTML = inputHTML.replace( "$COMPULSORY", compulsory );
                inputHTML = inputHTML.replace( "$SAVEMODE", "false" );
                inputHTML = inputHTML.replace( "$TABINDEX", tabindex + "" );
                inputHTML = inputHTML.replace( "$DATAPICKER-METHOD", dateMethod );
                inputHTML = inputHTML.replaceAll( "\\$", "\\\\\\$" );

                dataElementMatcher.appendReplacement( sb, inputHTML );
            }
        }

        dataElementMatcher.appendTail( sb );

        return populateI18nStrings( sb.toString(), i18n );
    }

    @Override
    public String prepareDataEntryFormForAdd( String htmlCode, I18n i18n, ProgramStage programStage )
    {
        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String jQueryCalendar = "<script>$DATAPICKER-METHOD(\"$PROGRAMSTAGEID-$DATAELEMENTID-val\", false);</script>";

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Matcher dataElementMatcher = INPUT_PATTERN.matcher( htmlCode );
        int tabindex = 0;

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        Map<String, DataElement> dataElementMap = getDataElementMap( programStage );

        while ( dataElementMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String compulsory = "null";
            boolean allowProvidedElsewhere = false;
            String dateMethod = "datePickerValid";
            String inputHTML = dataElementMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_FIELD.matcher( inputHTML );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------

                String programStageUid = identifierMatcher.group( 1 );

                String dataElementUid = identifierMatcher.group( 2 );

                DataElement dataElement = null;

                String programStageName = programStage.getDisplayName();

                if ( !programStageUid.equals( programStage.getUid() ) )
                {
                    dataElement = dataElementService.getDataElement( dataElementUid );

                    ProgramStage otherProgramStage = programStageService.getProgramStage( programStageUid );
                    programStageName = otherProgramStage != null ? otherProgramStage.getDisplayName() : "N/A";
                }
                else
                {
                    dataElement = dataElementMap.get( dataElementUid );
                    if ( dataElement == null )
                    {
                        return i18n.getString( "some_data_element_not_exist" );
                    }

                    ProgramStageDataElement psde = programStageDataElementService.get( programStage, dataElement );

                    compulsory = BooleanUtils.toStringTrueFalse( psde.isCompulsory() );
                    allowProvidedElsewhere = psde.getAllowProvidedElsewhere();
                    if ( psde.getAllowDateInFuture() )
                    {
                        dateMethod = "datePicker";
                    }
                }

                if ( dataElement == null )
                {
                    continue;
                }

                // -------------------------------------------------------------
                // Find type of data element
                // -------------------------------------------------------------

                String dataElementType = dataElement.getDetailedNumberType();

                // -------------------------------------------------------------
                // Find existing value of data element in data set
                // -------------------------------------------------------------

                PatientDataValue patientDataValue = null;

                String dataElementValue = EMPTY;

                // -------------------------------------------------------------
                // Insert title information - Data element id, name, type, min,
                // max
                // -------------------------------------------------------------

                if ( inputHTML.contains( "title=\"\"" ) )
                {
                    inputHTML = inputHTML.replace( "title=\"\"", "title=\"[ " + dataElement.getUid() + " - "
                        + dataElement.getName() + " - " + dataElementType + " ]\" " );
                }
                else
                {
                    inputHTML += "title=\"[ " + dataElement.getUid() + " - " + dataElement.getName() + " - "
                        + dataElementType + " ]\" ";
                }

                // -------------------------------------------------------------
                // Set field for dataElement
                // -------------------------------------------------------------

                tabindex++;

                if ( DataElement.VALUE_TYPE_INT.equals( dataElement.getType() )
                    || DataElement.VALUE_TYPE_STRING.equals( dataElement.getType() )
                    || DataElement.VALUE_TYPE_USER_NAME.equals( dataElement.getType() ) )
                {
                    inputHTML = populateCustomDataEntryForTextBox( dataElement, inputHTML, dataElementValue );
                }
                else if ( DataElement.VALUE_TYPE_DATE.equals( dataElement.getType() ) )
                {
                    inputHTML = populateCustomDataEntryForDate( inputHTML, dataElementValue );
                }
                else if ( DataElement.VALUE_TYPE_TRUE_ONLY.equals( dataElement.getType() ) )
                {
                    inputHTML = populateCustomDataEntryForTrueOnly( dataElement, inputHTML, dataElementValue );
                }
                else if ( DataElement.VALUE_TYPE_BOOL.equals( dataElement.getType() ) )
                {
                    inputHTML = populateCustomDataEntryForBoolean( dataElement, inputHTML, dataElementValue, i18n );
                }

                // -----------------------------------------------------------
                // Check if this dataElement is from another programStage then
                // disable
                // If programStagsInstance is completed then disabled it
                // -----------------------------------------------------------

                String disabled = "";
                if ( !programStageUid.equals( programStage.getUid() ) )
                {
                    disabled = "disabled=\"\"";
                }
                else
                {
                    if ( DataElement.VALUE_TYPE_DATE.equals( dataElement.getType() ) )
                    {
                        inputHTML += jQueryCalendar;
                    }

                    if ( allowProvidedElsewhere )
                    {
                        // Add ProvidedByOtherFacility checkbox
                        inputHTML = addProvidedElsewherCheckbox( inputHTML, patientDataValue, programStage );
                    }
                }

                // -----------------------------------------------------------
                //
                // -----------------------------------------------------------

                inputHTML = inputHTML.replace( "$DATAELEMENTID", String.valueOf( dataElementUid ) );
                inputHTML = inputHTML.replace( "$VALUE", dataElementValue );
                inputHTML = inputHTML.replace( "$PROGRAMSTAGEID", String.valueOf( programStageUid ) );
                inputHTML = inputHTML.replace( "$PROGRAMSTAGENAME", programStageName );
                inputHTML = inputHTML.replace( "$DATAELEMENTNAME", dataElement.getName() );
                inputHTML = inputHTML.replace( "$DATAELEMENTTYPE", dataElementType );
                inputHTML = inputHTML.replace( "$DISABLED", disabled );
                inputHTML = inputHTML.replace( "$COMPULSORY", compulsory );
                inputHTML = inputHTML.replace( "$SAVEMODE", "false" );
                inputHTML = inputHTML.replace( "$TABINDEX", tabindex + "" );
                inputHTML = inputHTML.replace( "$DATAPICKER-METHOD", dateMethod );
                inputHTML = inputHTML.replaceAll( "\\$", "\\\\\\$" );

                dataElementMatcher.appendReplacement( sb, inputHTML );
            }
        }

        dataElementMatcher.appendTail( sb );

        return populateI18nStrings( sb.toString(), i18n );
    }

    public String prepareDataEntryFormForEdit( String htmlCode )
    {
        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        while ( inputMatcher.find() )
        {
            String inputHTML = inputMatcher.group();
            inputHTML = inputHTML.replace( ">", "" );

            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String dataElementCode = inputMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_FIELD.matcher( dataElementCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------

                String dataElementUid = identifierMatcher.group( 2 );
                DataElement dataElement = dataElementService.getDataElement( dataElementUid );

                inputHTML = populateCustomDataEntryForTextBox( dataElement, inputHTML );

                inputHTML = inputHTML + ">";

                inputMatcher.appendReplacement( sb, inputHTML );
            }
        }

        inputMatcher.appendTail( sb );

        return (sb.toString().isEmpty()) ? htmlCode : sb.toString();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String populateCustomDataEntryForOptionSet( DataElement dataElement, String id,
        PatientDataValue patientDataValue, I18n i18n )
    {
        String inputHTML = "";
        if ( dataElement != null )
        {
            String metaData = "<input id=\'" + id + "\' name=\'" + id + "\' options=\'no\' type=\'radio\' optionset='"
                + dataElement.getOptionSet().getUid() + "'";
            metaData += " data=\"{compulsory:$COMPULSORY, deName:\'$DATAELEMENTNAME\', deType:\'"
                + dataElement.getDetailedNumberType() + "\' }\" ";

            inputHTML = "<table style=\'width:100%\'>";
            inputHTML += "<tr>";
            inputHTML += "<td>";
            inputHTML += metaData;

            if ( patientDataValue == null )
            {
                inputHTML += " checked ";
            }

            inputHTML += "onclick=\"saveRadio( \'" + dataElement.getUid() + "\', \'$option\' )\" />"
                + i18n.getString( "non_value" );
            inputHTML += " </td>";

            int index = 1;

            for ( String optionValue : dataElement.getOptionSet().getOptions() )
            {
                if ( index == 4 )
                {
                    inputHTML += "</tr><tr>";
                    index = 0;
                }

                inputHTML += "<td>" + metaData;
                if ( patientDataValue != null && patientDataValue.getValue().equals( optionValue ) )
                {
                    inputHTML += " checked ";
                }
                inputHTML += " onclick=\"saveRadio( \'" + dataElement.getUid() + "\', \'" + optionValue + "\' )\"  />"
                    + optionValue;
                inputHTML += "</td>";
                index++;
            }
            inputHTML += "  </tr>";
            inputHTML += "   </table>";

        }
        else
        {
            inputHTML = inputHTML.contains( EMPTY_VALUE_TAG ) ? " value=\"[" + DATA_ELEMENT_DOES_NOT_EXIST + "]\" "
                : " value=\"[ " + DATA_ELEMENT_DOES_NOT_EXIST + " ]\"";
        }

        return inputHTML;
    }

    private String populateCustomDataEntryForTextBox( DataElement dataElement, String inputHTML )
    {
        if ( dataElement != null )
        {
            inputHTML = inputHTML.contains( EMPTY_VALUE_TAG ) ? inputHTML.replace( EMPTY_VALUE_TAG, "value=\"["
                + dataElement.getDisplayName() + "]\"" ) : inputHTML + "value=\"[" + dataElement.getDisplayName()
                + "]\" ";

            String displayTitle = dataElement.getUid() + " - " + dataElement.getName() + " - "
                + dataElement.getDetailedNumberType();
            inputHTML = inputHTML.contains( EMPTY_TITLE_TAG ) ? inputHTML.replace( EMPTY_TITLE_TAG, "title=\"[ "
                + displayTitle + " ]\"" ) : inputHTML + " title=\"[ " + displayTitle + " ]\"";
        }
        else
        {
            inputHTML = inputHTML.contains( EMPTY_VALUE_TAG ) ? "value=\"[" + DATA_ELEMENT_DOES_NOT_EXIST + "]\" "
                : " value=\"[" + DATA_ELEMENT_DOES_NOT_EXIST + "]\"";
        }

        return inputHTML;
    }

    private String populateCustomDataEntryForBoolean( DataElement dataElement, String inputHTML,
        String patientDataValue, I18n i18n )
    {
        final String jsCodeForBoolean = " name=\"entryselect\" tabIndex=\"$TABINDEX\" $DISABLED data=\"{compulsory:$COMPULSORY, deName:'$DATAELEMENTNAME' }\" onchange=\"saveOpt( '$DATAELEMENTID' )\" ";

        inputHTML = inputHTML.replaceFirst( "input", "select" );
        inputHTML = inputHTML.replace( "name=\"entryselect\"", jsCodeForBoolean );

        inputHTML += ">";
        inputHTML += "<option value=\"\">" + i18n.getString( "no_value" ) + "</option>";
        inputHTML += "<option value=\"true\">" + i18n.getString( "yes" ) + "</option>";
        inputHTML += "<option value=\"false\">" + i18n.getString( "no" ) + "</option>";

        // -------------------------------------------------------------
        // Insert value of data element in output code
        // -------------------------------------------------------------
        if ( patientDataValue != null )
        {
            if ( patientDataValue.equalsIgnoreCase( "true" ) )
            {
                inputHTML = inputHTML.replace( "<option value=\"true\">", "<option value=\"" + i18n.getString( "true" )
                    + "\" selected>" );
            }
            else if ( patientDataValue.equalsIgnoreCase( "false" ) )
            {
                inputHTML = inputHTML.replace( "<option value=\"false\">",
                    "<option value=\"" + i18n.getString( "false" ) + "\" selected>" );
            }
        }

        inputHTML += "</select>";

        return inputHTML;

    }

    private String populateCustomDataEntryForTrueOnly( DataElement dataElement, String inputHTML,
        String dataElementValue )
    {
        final String jsCodeForInputs = " name=\"entryfield\" tabIndex=\"$TABINDEX\" $DISABLED data=\"{compulsory:$COMPULSORY, deName:'$DATAELEMENTNAME', deType:'$DATAELEMENTTYPE'}\" onchange=\"saveVal( '$DATAELEMENTID' )\" onkeypress=\"return keyPress(event, this)\" ";

        String checked = "";
        if ( !dataElementValue.equals( EMPTY ) && dataElementValue.equals( "true" ) )
        {
            checked = "checked";
        }

        if ( inputHTML.contains( "value=\"\"" ) )
        {
            inputHTML = inputHTML.replace( "value=\"\"", checked );
        }
        else
        {
            inputHTML += " " + checked;
        }

        inputHTML += jsCodeForInputs;
        inputHTML += " />";

        return inputHTML;
    }

    private String populateCustomDataEntryForTextBox( DataElement dataElement, String inputHTML, String dataElementValue )
    {
        final String jsCodeForInputs = " name=\"entryfield\" tabIndex=\"$TABINDEX\" $DISABLED data=\"{compulsory:$COMPULSORY, deName:'$DATAELEMENTNAME', deType:'$DATAELEMENTTYPE'}\" options='$OPTIONS' maxlength=255 ";
        final String jsCodeForOnchange = " onchange=\"saveVal( '$DATAELEMENTID' )\" onkeypress=\"return keyPress(event, this)\" ";

        // -------------------------------------------------------------
        // Insert value of data element in output code
        // -------------------------------------------------------------

        if ( inputHTML.contains( "value=\"\"" ) )
        {
            inputHTML = inputHTML.replace( "value=\"\"", "value=\"" + dataElementValue + "\"" );
        }
        else
        {
            inputHTML += "value=\"" + dataElementValue + "\"";
        }

        inputHTML += jsCodeForInputs;

        Boolean hasOptionSet = (dataElement.getOptionSet() != null);
        inputHTML = inputHTML.replace( "$OPTIONS", hasOptionSet.toString() );
        if ( !hasOptionSet )
        {
            inputHTML += jsCodeForOnchange;
        }
        else
        {
            inputHTML += " class=\"optionset\" optionset=\"" + dataElement.getOptionSet().getUid()
                + "\" data-optionset=\"" + dataElement.getOptionSet().getUid() + "\" ";
        }

        if ( DataElement.VALUE_TYPE_LONG_TEXT.equals( dataElement.getDetailedTextType() ) )
        {
            inputHTML = inputHTML.replaceFirst( "input", "textarea" );
            inputHTML += " >$VALUE</textarea>";
        }
        else
        {
            inputHTML += " />";
        }

        return inputHTML;
    }

    private String populateCustomDataEntryForDate( String inputHTML, String dataElementValue )
    {
        final String jsCodeForDate = " name=\"entryfield\" tabIndex=\"$TABINDEX\" $DISABLED data=\"{compulsory:$COMPULSORY, deName:'$DATAELEMENTNAME'}\" onchange=\"saveVal( '$DATAELEMENTID' )\" ";

        // -------------------------------------------------------------
        // Insert value of data element in output code
        // -------------------------------------------------------------

        if ( inputHTML.contains( "value=\"\"" ) )
        {
            inputHTML = inputHTML.replace( "value=\"\"", "value=\"" + dataElementValue + "\"" );
        }
        else
        {
            inputHTML += "value=\"" + dataElementValue + "\"";
        }

        inputHTML = inputHTML.replace( "name=\"entryfield\"", jsCodeForDate );
        inputHTML += " />";

        return inputHTML;
    }

    private String addProvidedElsewherCheckbox( String appendCode, PatientDataValue patientDataValue,
        ProgramStage programStage )
    {
        String id = "$PROGRAMSTAGEID-$DATAELEMENTID-facility";
        appendCode += "<div id=\"span_"
            + id
            + "\" class=\"provided-elsewhere\"><input name=\"providedByAnotherFacility\" title=\"is provided by another Facility ?\"  id=\""
            + id + "\"  type=\"checkbox\" ";

        if ( patientDataValue != null && patientDataValue.getProvidedElsewhere() )
        {
            appendCode += " checked=\"checked\" ";
        }

        appendCode += "onChange=\"updateProvidingFacility( $DATAELEMENTID, this )\"  ></div>";

        return appendCode;

    }

    /**
     * Returns the value of the PatientDataValue in the Collection of DataValues
     * with the given data element identifier.
     */
    private PatientDataValue getValue( Collection<PatientDataValue> dataValues, String dataElementUid )
    {
        if ( dataValues == null )
        {
            return null;
        }

        for ( PatientDataValue dataValue : dataValues )
        {
            if ( dataValue.getDataElement().getUid().equals( dataElementUid ) )
            {
                return dataValue;
            }
        }

        return null;
    }

    /**
     * Returns a Map of all DataElements in the given ProgramStage where the key
     * is the DataElement identifier and the value is the DataElement.
     */
    private Map<String, DataElement> getDataElementMap( ProgramStage programStage )
    {
        Collection<DataElement> dataElements = programStageDataElementService.getListDataElement( programStage );

        if ( programStage == null )
        {
            return null;
        }
        Map<String, DataElement> map = new HashMap<String, DataElement>();

        for ( DataElement element : dataElements )
        {
            map.put( element.getUid(), element );
        }

        return map;
    }

    /**
     * Replaces i18n string in the custom form code.
     * 
     * @param dataEntryFormCode the data entry form html.
     * @param i18n the I18n object.
     * @return internationalized data entry form html.
     */
    private String populateI18nStrings( String dataEntryFormCode, I18n i18n )
    {
        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match i18n strings in the HTML code
        // ---------------------------------------------------------------------

        Pattern i18nPattern = Pattern.compile( "(<i18n.*?)[/]?</i18n>", Pattern.DOTALL );
        Matcher i18nMatcher = i18nPattern.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching i18n element fields
        // ---------------------------------------------------------------------

        while ( i18nMatcher.find() )
        {
            String i18nCode = i18nMatcher.group( 1 );

            i18nCode = i18nCode.replaceAll( "<i18n>", "" );

            i18nCode = i18n.getString( i18nCode );

            i18nMatcher.appendReplacement( sb, i18nCode );
        }

        i18nMatcher.appendTail( sb );

        String result = sb.toString();

        result.replaceAll( "</i18n>", "" );

        return result;
    }
}