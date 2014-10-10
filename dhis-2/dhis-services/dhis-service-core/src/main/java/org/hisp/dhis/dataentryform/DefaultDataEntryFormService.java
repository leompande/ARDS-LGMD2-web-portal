package org.hisp.dhis.dataentryform;

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

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_BOOL;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_LONG_TEXT;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_TRUE_ONLY;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Bharath Kumar
 */
@Transactional
public class DefaultDataEntryFormService
    implements DataEntryFormService
{
    private static final Log log = LogFactory.getLog( DefaultDataEntryFormService.class );

    private static final String EMPTY_VALUE_TAG = "value=\"\"";
    private static final String EMPTY_TITLE_TAG = "title=\"\"";
    private static final String TAG_CLOSE = "/>";
    private static final String EMPTY = "";

    // ------------------------------------------------------------------------
    // Dependencies
    // ------------------------------------------------------------------------

    private DataEntryFormStore dataEntryFormStore;

    public void setDataEntryFormStore( DataEntryFormStore dataEntryFormStore )
    {
        this.dataEntryFormStore = dataEntryFormStore;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    // ------------------------------------------------------------------------
    // Implemented Methods
    // ------------------------------------------------------------------------

    public int addDataEntryForm( DataEntryForm dataEntryForm )
    {
        return dataEntryFormStore.addDataEntryForm( dataEntryForm );
    }

    public void updateDataEntryForm( DataEntryForm dataEntryForm )
    {
        dataEntryFormStore.updateDataEntryForm( dataEntryForm );
    }

    public void deleteDataEntryForm( DataEntryForm dataEntryForm )
    {
        dataEntryFormStore.deleteDataEntryForm( dataEntryForm );
    }

    public DataEntryForm getDataEntryForm( int id )
    {
        return dataEntryFormStore.getDataEntryForm( id );
    }

    public DataEntryForm getDataEntryFormByName( String name )
    {
        return dataEntryFormStore.getDataEntryFormByName( name );
    }

    public Collection<DataEntryForm> getAllDataEntryForms()
    {
        return dataEntryFormStore.getAllDataEntryForms();
    }

    public String prepareDataEntryFormForSave( String htmlCode )
    {
        StringBuffer sb = new StringBuffer();

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        while ( inputMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Remove value and title tags from the HTML code
            // -----------------------------------------------------------------

            String dataElementCode = inputMatcher.group();

            Matcher valueTagMatcher = VALUE_TAG_PATTERN.matcher( dataElementCode );
            Matcher titleTagMatcher = TITLE_TAG_PATTERN.matcher( dataElementCode );

            if ( valueTagMatcher.find() && valueTagMatcher.groupCount() > 0 )
            {
                dataElementCode = dataElementCode.replace( valueTagMatcher.group( 1 ), EMPTY );
            }

            if ( titleTagMatcher.find() && titleTagMatcher.groupCount() > 0 )
            {
                dataElementCode = dataElementCode.replace( titleTagMatcher.group( 1 ), EMPTY );
            }

            inputMatcher.appendReplacement( sb, dataElementCode );
        }

        inputMatcher.appendTail( sb );

        return sb.toString();
    }

    public String prepareDataEntryFormForEdit( String htmlCode, I18n i18n )
    {
        //TODO HTML encode names

        StringBuffer sb = new StringBuffer();

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        while ( inputMatcher.find() )
        {
            String inputHtml = inputMatcher.group();

            Matcher identifierMatcher = IDENTIFIER_PATTERN.matcher( inputHtml );
            Matcher dataElementTotalMatcher = DATAELEMENT_TOTAL_PATTERN.matcher( inputHtml );
            Matcher indicatorMatcher = INDICATOR_PATTERN.matcher( inputHtml );

            String displayValue = null;
            String displayTitle = null;

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                String dataElementId = identifierMatcher.group( 1 );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                String optionComboId = identifierMatcher.group( 2 );
                DataElementCategoryOptionCombo categegoryOptionCombo = categoryService.getDataElementCategoryOptionCombo( optionComboId );
                String optionComboName = categegoryOptionCombo != null ? escapeHtml( categegoryOptionCombo.getName() ) : "[ " + i18n.getString( "cat_option_combo_not_exist" ) + " ]";

                StringBuilder title = dataElement != null ?
                    new StringBuilder( "title=\"" ).append( dataElementId ).append( " - " ).
                        append( escapeHtml( dataElement.getDisplayName() ) ).append( " - " ).append( optionComboId ).append( " - " ).
                        append( optionComboName ).append( " - " ).append( dataElement.getType() ).append( "\"" ) : new StringBuilder();

                displayValue = dataElement != null ? "value=\"[ " + escapeHtml( dataElement.getDisplayName() ) + " " + optionComboName + " ]\"" : "[ " + i18n.getString( "data_element_not_exist" ) + " ]";
                displayTitle = dataElement != null ? title.toString() : "[ " + i18n.getString( "dataelement_not_exist" ) + " ]";
            }
            else if ( dataElementTotalMatcher.find() && dataElementTotalMatcher.groupCount() > 0 )
            {
                String dataElementId = dataElementTotalMatcher.group( 1 );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                displayValue = dataElement != null ? "value=\"[ " + escapeHtml( dataElement.getDisplayName() ) + " ]\"" : "[ " + i18n.getString( "data_element_not_exist" ) + " ]";
                displayTitle = dataElement != null ? "title=\"" + escapeHtml( dataElement.getDisplayName() ) + "\"" : "[ " + i18n.getString( "data_element_not_exist" ) + " ]";
            }
            else if ( indicatorMatcher.find() && indicatorMatcher.groupCount() > 0 )
            {
                String indicatorId = indicatorMatcher.group( 1 );
                Indicator indicator = indicatorService.getIndicator( indicatorId );

                displayValue = indicator != null ? "value=\"[ " + escapeHtml( indicator.getDisplayName() ) + " ]\"" : "[ " + i18n.getString( "indicator_not_exist" ) + " ]";
                displayTitle = indicator != null ? "title=\"" + escapeHtml( indicator.getDisplayName() ) + "\"" : "[ " + i18n.getString( "indicator_not_exist" ) + " ]";
            }

            // -----------------------------------------------------------------
            // Insert name of data element operand as value and title
            // -----------------------------------------------------------------

            if ( displayValue == null || displayTitle == null )
            {
                log.warn( "Ignoring invalid form markup: '" + inputHtml + "'" );
                continue;
            }

            inputHtml = inputHtml.contains( EMPTY_VALUE_TAG ) ? inputHtml.replace( EMPTY_VALUE_TAG, displayValue ) : inputHtml + " " + displayValue;
            inputHtml = inputHtml.contains( EMPTY_TITLE_TAG ) ? inputHtml.replace( EMPTY_TITLE_TAG, displayTitle ) : inputHtml + " " + displayTitle;

            inputMatcher.appendReplacement( sb, inputHtml );
        }

        inputMatcher.appendTail( sb );

        return sb.toString();
    }

    public String prepareDataEntryFormForEntry( String htmlCode, I18n i18n, DataSet dataSet )
    {
        //TODO HTML encode names

        // ---------------------------------------------------------------------
        // Inline javascript/html to add to HTML before output
        // ---------------------------------------------------------------------

        int i = 1;

        StringBuffer sb = new StringBuffer();

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        Map<String, DataElement> dataElementMap = getDataElementMap( dataSet );

        while ( inputMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String inputHtml = inputMatcher.group();

            Matcher identifierMatcher = IDENTIFIER_PATTERN.matcher( inputHtml );
            Matcher indicatorMatcher = INDICATOR_PATTERN.matcher( inputHtml );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                String dataElementId = identifierMatcher.group( 1 );
                String optionComboId = identifierMatcher.group( 2 );

                DataElement dataElement = dataElementMap.get( dataElementId );

                if ( dataElement == null )
                {
                    return i18n.getString( "dataelement_with_id" ) + ": " + dataElementId + " " + i18n.getString( "does_not_exist" );
                }

                DataElementCategoryOptionCombo categoryOptionCombo = categoryService
                    .getDataElementCategoryOptionCombo( optionComboId );

                if ( categoryOptionCombo == null )
                {
                    return i18n.getString( "category_option_combo_with_id" ) + ": " + optionComboId + " " + i18n.getString( "does_not_exist" );
                }
                
                if ( dataSet.isDataElementDecoration() && dataElement.hasDescription() ) 
                {
                    String titleTag = " title=\"" +  escapeHtml( dataElement.getDisplayDescription() ) + "\" ";
                    inputHtml = inputHtml.replaceAll( "title=\".*?\"", "" ).replace( TAG_CLOSE, titleTag + TAG_CLOSE );
                }                
                
                String appendCode = "";

                if ( VALUE_TYPE_BOOL.equals( dataElement.getType() ) )
                {
                    inputHtml = inputHtml.replace( "input", "select" );
                    inputHtml = inputHtml.replaceAll( "value=\".*?\"", "" );

                    appendCode += " name=\"entryselect\" class=\"entryselect\" tabindex=\"" + i++ + "\">";

                    appendCode += "<option value=\"\">" + i18n.getString( "no_value" ) + "</option>";
                    appendCode += "<option value=\"true\">" + i18n.getString( "yes" ) + "</option>";
                    appendCode += "<option value=\"false\">" + i18n.getString( "no" ) + "</option>";
                    appendCode += "</select>";
                }
                else if ( VALUE_TYPE_TRUE_ONLY.equals( dataElement.getType() ) )
                {
                    appendCode += " name=\"entrytrueonly\" class=\"entrytrueonly\" type=\"checkbox\" tabindex=\"" + i++ + "\"" + TAG_CLOSE;
                }
                else if ( dataElement.hasOptionSet() )
                {
                    appendCode += " name=\"entryoptionset\" class=\"entryoptionset\" tabindex=\"" + i++ + "\"" + TAG_CLOSE;
                }
                else if ( VALUE_TYPE_LONG_TEXT.equals( dataElement.getTextType() ) )
                {
                    inputHtml = inputHtml.replace( "input", "textarea" );
                    
                    appendCode += " name=\"entryfield\" class=\"entryfield entryarea\" tabindex=\"" + i++ + "\"" + "></textarea>";
                }
                else
                {
                    appendCode += " name=\"entryfield\" class=\"entryfield\" tabindex=\"" + i++ + "\"" + TAG_CLOSE;
                }
                
                inputHtml = inputHtml.replace( TAG_CLOSE, appendCode );

                inputHtml += "<span id=\"" + dataElement.getUid() + "-dataelement\" style=\"display:none\">" + dataElement.getFormNameFallback() + "</span>";
                inputHtml += "<span id=\"" + categoryOptionCombo.getUid() + "-optioncombo\" style=\"display:none\">" + categoryOptionCombo.getName() + "</span>";
            }
            else if ( indicatorMatcher.find() && indicatorMatcher.groupCount() > 0 )
            {
                inputHtml = inputHtml.replace( TAG_CLOSE, " class=\"indicator\"" + TAG_CLOSE );
            }

            inputMatcher.appendReplacement( sb, inputHtml );            
        }

        inputMatcher.appendTail( sb );

        return sb.toString();
    }

    public Set<DataElement> getDataElementsInDataEntryForm( DataSet dataSet )
    {
        if ( dataSet == null || dataSet.getDataEntryForm() == null )
        {
            return null;
        }

        Map<String, DataElement> dataElementMap = getDataElementMap( dataSet );

        Set<DataElement> dataElements = new HashSet<DataElement>();

        Matcher inputMatcher = INPUT_PATTERN.matcher( dataSet.getDataEntryForm().getHtmlCode() );

        while ( inputMatcher.find() )
        {
            String inputHtml = inputMatcher.group();

            Matcher identifierMatcher = IDENTIFIER_PATTERN.matcher( inputHtml );
            Matcher dataElementTotalMatcher = DATAELEMENT_TOTAL_PATTERN.matcher( inputHtml );

            DataElement dataElement = null;

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                String dataElementId = identifierMatcher.group( 1 );
                dataElement = dataElementMap.get( dataElementId );
            }
            else if ( dataElementTotalMatcher.find() && dataElementTotalMatcher.groupCount() > 0 )
            {
                String dataElementId = dataElementTotalMatcher.group( 1 );
                dataElement = dataElementMap.get( dataElementId );
            }

            if ( dataElement != null )
            {
                dataElements.add( dataElement );
            }
        }

        return dataElements;
    }

    public Set<DataElementOperand> getOperandsInDataEntryForm( DataSet dataSet )
    {
        if ( dataSet == null || dataSet.getDataEntryForm() == null )
        {
            return null;
        }

        Set<DataElementOperand> operands = new HashSet<DataElementOperand>();

        Matcher inputMatcher = INPUT_PATTERN.matcher( dataSet.getDataEntryForm().getHtmlCode() );

        while ( inputMatcher.find() )
        {
            String inputHtml = inputMatcher.group();

            Matcher identifierMatcher = IDENTIFIER_PATTERN.matcher( inputHtml );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                String dataElementId = identifierMatcher.group( 1 );
                String categoryOptionComboId = identifierMatcher.group( 2 );

                DataElementOperand operand = new DataElementOperand( dataElementId, categoryOptionComboId );

                operands.add( operand );
            }
        }

        return operands;
    }

    public Collection<DataEntryForm> listDistinctDataEntryFormByProgramStageIds( List<Integer> programStageIds )
    {
        if ( programStageIds == null || programStageIds.isEmpty() )
        {
            return null;
        }

        return dataEntryFormStore.listDistinctDataEntryFormByProgramStageIds( programStageIds );
    }

    public Collection<DataEntryForm> listDistinctDataEntryFormByDataSetIds( List<Integer> dataSetIds )
    {
        if ( dataSetIds == null || dataSetIds.size() == 0 )
        {
            return null;
        }

        return dataEntryFormStore.listDistinctDataEntryFormByDataSetIds( dataSetIds );
    }

    public Collection<DataEntryForm> getDataEntryForms( final Collection<Integer> identifiers )
    {
        Collection<DataEntryForm> dataEntryForms = getAllDataEntryForms();

        return identifiers == null ? dataEntryForms : FilterUtils.filter( dataEntryForms, new Filter<DataEntryForm>()
        {
            public boolean retain( DataEntryForm object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Returns a Map of all DataElements in the given DataSet where the key is
     * the DataElement identifier and the value is the DataElement.
     */
    private Map<String, DataElement> getDataElementMap( DataSet dataSet )
    {
        Map<String, DataElement> map = new HashMap<String, DataElement>();

        for ( DataElement element : dataSet.getDataElements() )
        {
            map.put( element.getUid(), element );
        }

        return map;
    }
}
