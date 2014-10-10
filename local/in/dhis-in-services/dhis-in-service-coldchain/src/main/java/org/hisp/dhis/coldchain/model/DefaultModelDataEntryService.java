package org.hisp.dhis.coldchain.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.BooleanUtils;
import org.hisp.dhis.coldchain.model.ModelDataEntryService;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.i18n.I18n;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DefaultModelDataEntryService.java Jun 7, 2012 5:18:09 PM	
 */
public class DefaultModelDataEntryService implements ModelDataEntryService
{
    
    private static final String EMPTY = "";

    private static final String MODEL_TYPE_ATTRIBUTE_DOES_NOT_EXIST = "[ Modeltype attribute does not exist ]";

    private static final String EMPTY_VALUE_TAG = "value=\"\"";

    private static final String EMPTY_TITLE_TAG = "title=\"\"";
    
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ModelTypeAttributeService modelTypeAttributeService;
    
    public void setModelTypeAttributeService( ModelTypeAttributeService modelTypeAttributeService )
    {
        this.modelTypeAttributeService = modelTypeAttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public  String prepareDataEntryFormForModel( String htmlCode, Collection<ModelAttributeValue> dataValues, String disabled,
        I18n i18n, ModelType modelType )
    {
        Map<Integer, Collection<ModelAttributeValue>> mapDataValue = new HashMap<Integer, Collection<ModelAttributeValue>>();

        String result = "";

        result = populateCustomDataEntryForTextBox( htmlCode, dataValues, disabled, i18n, modelType, mapDataValue );
        
        result = populateCustomDataEntryForCOMBO( htmlCode, dataValues, disabled, i18n, modelType, mapDataValue );

        result = populateCustomDataEntryForDate( result, dataValues, disabled, i18n, modelType, mapDataValue );

        result = populateCustomDataEntryForBoolean( result, dataValues, disabled, i18n, modelType,  mapDataValue );

        result = populateI18nStrings( result, i18n );

        return result;
    }
    
    
    
    public String prepareDataEntryFormForEdit( String htmlCode )
    {
        String result = populateCustomDataEntryForDate( htmlCode );

        result = populateCustomDataEntryForBoolean( result );

        result = populateCustomDataEntryForTextBox( result );
        
        result = populateCustomDataEntryForCOMBO( result );

        return result;
    }
    
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private String populateCustomDataEntryForTextBox( String htmlCode )
    {
     // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match modelType attribute in the HTML code
        // ---------------------------------------------------------------------

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching modelType attribute fields
        // ---------------------------------------------------------------------
        
        while ( inputMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String modelTypeAttributeCode = inputMatcher.group( 1 );

            String inputHTML = inputMatcher.group();
            inputHTML = inputHTML.replace( ">", "" );

            Matcher identifierMatcher = ModelDataEntryService.IDENTIFIER_PATTERN_FIELD.matcher( modelTypeAttributeCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get modelType attribute ID of modelType attribute
                // -------------------------------------------------------------

                int modelTypeAttributeId = Integer.parseInt( identifierMatcher.group( 1 ) );
                
                ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( modelTypeAttributeId );
                
                if ( modelTypeAttribute != null && !ModelTypeAttribute.TYPE_INT.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
                {
                    continue;
                }
                else if ( modelTypeAttribute != null && !ModelTypeAttribute.TYPE_STRING.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
                {
                    continue;
                }

                String displayValue = ( modelTypeAttribute == null ) ? " value=\"" + MODEL_TYPE_ATTRIBUTE_DOES_NOT_EXIST + "\" "
                    : " value=\"[ " + modelTypeAttribute.getName() + " ]\"";
                
                inputHTML = inputHTML.contains( EMPTY_VALUE_TAG ) ? inputHTML.replace( EMPTY_VALUE_TAG, displayValue )
                    : inputHTML + " " + displayValue;

                String displayTitle = ( modelTypeAttribute == null ) ? " title=\"" + MODEL_TYPE_ATTRIBUTE_DOES_NOT_EXIST + "\" "
                    : " title=\"" + modelTypeAttribute.getId() + "." + modelTypeAttribute.getName() + "-"
                        + modelTypeAttribute.getValueType() + "\" ";
                
                inputHTML = inputHTML.contains( EMPTY_TITLE_TAG ) ? inputHTML.replace( EMPTY_TITLE_TAG, displayTitle )
                    : inputHTML + " " + displayTitle;

                inputHTML = inputHTML + ">";

                inputMatcher.appendReplacement( sb, inputHTML );
            }
        }

        inputMatcher.appendTail( sb );

        return (sb.toString().isEmpty()) ? htmlCode : sb.toString();
    }
    
 
    private String populateCustomDataEntryForCOMBO( String htmlCode )
    {
     // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match modelType attribute in the HTML code
        // ---------------------------------------------------------------------

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching modelType attribute fields
        // ---------------------------------------------------------------------
        
        while ( inputMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String modelTypeAttributeCode = inputMatcher.group( 1 );

            String inputHTML = inputMatcher.group();
            inputHTML = inputHTML.replace( ">", "" );

            Matcher identifierMatcher = ModelDataEntryService.IDENTIFIER_PATTERN_FIELD.matcher( modelTypeAttributeCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get modelType attribute ID of modelType attribute
                // -------------------------------------------------------------

                int modelTypeAttributeId = Integer.parseInt( identifierMatcher.group( 1 ) );
                
                ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( modelTypeAttributeId );
                
                if ( modelTypeAttribute != null && !ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( modelTypeAttribute.getValueType() )  )
                {
                    continue;
                }
                

                String displayValue = ( modelTypeAttribute == null ) ? " value=\"" + MODEL_TYPE_ATTRIBUTE_DOES_NOT_EXIST + "\" "
                    : " value=\"[ " + modelTypeAttribute.getName() + " ]\"";
                
                inputHTML = inputHTML.contains( EMPTY_VALUE_TAG ) ? inputHTML.replace( EMPTY_VALUE_TAG, displayValue )
                    : inputHTML + " " + displayValue;

                String displayTitle = ( modelTypeAttribute == null ) ? " title=\"" + MODEL_TYPE_ATTRIBUTE_DOES_NOT_EXIST + "\" "
                    : " title=\"" + modelTypeAttribute.getId() + "." + modelTypeAttribute.getName() + "-"
                        + modelTypeAttribute.getValueType() + "\" ";
                
                inputHTML = inputHTML.contains( EMPTY_TITLE_TAG ) ? inputHTML.replace( EMPTY_TITLE_TAG, displayTitle )
                    : inputHTML + " " + displayTitle;

                inputHTML = inputHTML + ">";

                inputMatcher.appendReplacement( sb, inputHTML );
            }
        }

        inputMatcher.appendTail( sb );

        return (sb.toString().isEmpty()) ? htmlCode : sb.toString();
    }
        
   
    private String populateCustomDataEntryForBoolean( String htmlCode )
    {
        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match modelType attribute in the HTML code
        // ---------------------------------------------------------------------

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching modelType attribute fields
        // ---------------------------------------------------------------------

        while ( inputMatcher.find() )
        {
            String inputHTML = inputMatcher.group();
            inputHTML = inputHTML.replace( ">", "" );

            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String modelTypeAttributeCode = inputMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_FIELD.matcher( modelTypeAttributeCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get modelType attribute ID of modelType attribute
                // -------------------------------------------------------------
                int modelTypeAttributeId = Integer.parseInt( identifierMatcher.group( 1 ) );
                
                ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( modelTypeAttributeId );
                
                if ( modelTypeAttribute != null && !ModelTypeAttribute.TYPE_BOOL.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
                {
                    continue;
                }

                String displayValue = ( modelTypeAttribute == null) ? " value=\"" + MODEL_TYPE_ATTRIBUTE_DOES_NOT_EXIST + "\" "
                    : " value=\"[ " + modelTypeAttribute.getName() + " ]\" ";
                
                inputHTML = inputHTML.contains( EMPTY_VALUE_TAG ) ? inputHTML.replace( EMPTY_VALUE_TAG, displayValue )
                    : inputHTML + " " + displayValue;

                String displayTitle = ( modelTypeAttribute == null) ? " title=\"" + MODEL_TYPE_ATTRIBUTE_DOES_NOT_EXIST + "\" "
                    : " title=\"" + modelTypeAttribute.getId() + "." + modelTypeAttribute.getName() + "-"
                        + modelTypeAttribute.getValueType() + "\" ";
                
                inputHTML = inputHTML.contains( EMPTY_TITLE_TAG ) ? inputHTML.replace( EMPTY_TITLE_TAG, displayTitle )
                    : inputHTML + " " + displayTitle;

                inputHTML = inputHTML + ">";

                inputMatcher.appendReplacement( sb, inputHTML );
            }
        }

        inputMatcher.appendTail( sb );

        return (sb.toString().isEmpty()) ? htmlCode : sb.toString();
    }
    
 
    private String populateCustomDataEntryForDate( String htmlCode )
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

            String modelTypeAttributeCode = inputMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_FIELD.matcher( modelTypeAttributeCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get modelType attribute ID of modelType attribute
                // -------------------------------------------------------------
                
                int modelTypeAttributeId = Integer.parseInt( identifierMatcher.group( 1 ) );
                
                ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( modelTypeAttributeId );

                //int dataElementId = Integer.parseInt( identifierMatcher.group( 2 ) );
                //DataElement dataElement = dataElementService.getDataElement( dataElementId );

                if ( modelTypeAttribute != null && !ModelTypeAttribute.TYPE_DATE.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
                {
                    continue;
                }

                String displayValue = ( modelTypeAttribute == null ) ? " value=\"" + MODEL_TYPE_ATTRIBUTE_DOES_NOT_EXIST + "\""
                    : " value=\"[ " + modelTypeAttribute.getName() + " ]\"";
                
                inputHTML = inputHTML.contains( EMPTY_VALUE_TAG ) ? inputHTML.replace( EMPTY_VALUE_TAG, displayValue )
                    : inputHTML + " " + displayValue;

                String displayTitle = (modelTypeAttribute == null) ? " title=\"" + MODEL_TYPE_ATTRIBUTE_DOES_NOT_EXIST + "\""
                    : " title=\"" + modelTypeAttribute.getId() + "." + modelTypeAttribute.getName() + "-"
                        + modelTypeAttribute.getValueType() + "\" ";
                
                inputHTML = inputHTML.contains( EMPTY_TITLE_TAG ) ? inputHTML.replace( EMPTY_TITLE_TAG, displayTitle )
                    : inputHTML + " " + displayTitle;

                inputHTML = inputHTML + ">";

                inputMatcher.appendReplacement( sb, inputHTML );
            }
        }

        inputMatcher.appendTail( sb );

        return (sb.toString().isEmpty()) ? htmlCode : sb.toString();
    }    
    
    private String populateCustomDataEntryForBoolean( String dataEntryFormCode,
        Collection<ModelAttributeValue> dataValues, String disabled, I18n i18n, ModelType modelType,Map<Integer, Collection<ModelAttributeValue>> mapDataValue )
    {
        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String jsCodeForBoolean = " name=\"entryselect\" $DISABLED data=\"{mandatory:$MANDATORY, modelTypeAttributeName:'$MODELTYPEATTRIBUTENAME' }\" onchange=\"saveOpt( $MODELTYPEATTRIBUTEID )\" style=\"  text-align:center;\" ";

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match ModelTypeAttributes in the HTML code
        // ---------------------------------------------------------------------

        Matcher ModelTypeAttributeMatcher = INPUT_PATTERN.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching modelTypeAttribute fields
        // ---------------------------------------------------------------------
       
        Map<Integer, ModelTypeAttribute> modelTypeAttributeMap = getModelTypeAttributeMap( modelType );
        
        int modelTypeId = modelType.getId();
        
        while ( ModelTypeAttributeMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String mandatory = "null";
           
            
            String modelTypeAttributeCode = ModelTypeAttributeMatcher.group( 1 );
            
            Matcher identifierMatcher = IDENTIFIER_PATTERN_FIELD.matcher( modelTypeAttributeCode );
            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
               
                // -------------------------------------------------------------
                // Get modelType attribute ID of modelType attribute
                // -------------------------------------------------------------

                //int modelTypeId = Integer.parseInt( identifierMatcher.group( 1 ) );
                
                int modelTypeAttributeId = Integer.parseInt( identifierMatcher.group( 1 ) );

                ModelTypeAttribute modelTypeAttribute = null;

                String modelTypeName = modelType.getName();

                if ( modelTypeId != modelType.getId() )
                {
                    modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( modelTypeAttributeId );
                }
                else
                {
                    modelTypeAttribute = modelTypeAttributeMap.get( modelTypeAttributeId );
                    
                    if ( modelTypeAttribute == null )
                    {
                        return i18n.getString( "some_modelType_attribute_not_exist" );
                    }
                   
                    mandatory = BooleanUtils.toStringTrueFalse( modelTypeAttribute.isMandatory() );
                }

                if ( modelTypeAttribute == null )
                {
                    continue;
                }

                if ( !ModelTypeAttribute.TYPE_BOOL.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
                {
                    continue;
                }

                // -------------------------------------------------------------
                // Find type of modelType attribute
                // -------------------------------------------------------------

                String modelTypeAttributeType = modelTypeAttribute.getValueType();

                // -------------------------------------------------------------
                // Find existing value of modelType Attribute
                // -------------------------------------------------------------

                ModelAttributeValue modelAttributeValue = null;
               

                String modelTypeAttributeValue = EMPTY;

                if ( modelTypeId != modelType.getId() )
                {
                    
                    
                    Collection<ModelAttributeValue> modelAttributeValues = mapDataValue.get( modelTypeId );

                    if ( modelAttributeValues == null )
                    {
                       
                    }

                    modelAttributeValue = getValue( modelAttributeValues, modelTypeAttributeId );

                    modelTypeAttributeValue = modelAttributeValue != null ? modelAttributeValue.getValue() : modelTypeAttributeValue;
                }
                else
                {

                    modelAttributeValue = getValue( dataValues, modelTypeAttributeId );

                    if ( modelAttributeValue != null )
                    {
                        modelTypeAttributeValue = modelAttributeValue.getValue();
                    }
                }

                String appendCode = modelTypeAttributeCode.replaceFirst( "input", "select" );
                appendCode = appendCode.replace( "name=\"entryselect\"", jsCodeForBoolean );

                // -------------------------------------------------------------
                // Add title
                // -------------------------------------------------------------

                if ( modelTypeAttributeCode.contains( "title=\"\"" ) )
                {
                    appendCode = appendCode.replace( "title=\"\"", "title=\"" + modelTypeAttribute.getId() + "."
                        + modelTypeAttribute.getName() + "-" + modelTypeAttributeType + "\" " );
                }
                else
                {
                    appendCode += "title=\"" + modelTypeAttribute.getId() + "." + modelTypeAttribute.getName() + "-"
                        + modelTypeAttributeType + "\" ";
                }

                appendCode += ">";
                appendCode += "<option value=\"\">" + i18n.getString( "Please select" ) + "</option>";
                appendCode += "<option value=\"true\">" + i18n.getString( "yes" ) + "</option>";
                appendCode += "<option value=\"false\">" + i18n.getString( "no" ) + "</option>";

                // -------------------------------------------------------------
                // Insert value of modelType Attribute in output code
                // -------------------------------------------------------------

                if ( modelAttributeValue != null )
                {
                    if ( modelTypeAttributeValue.equalsIgnoreCase( "true" ) )
                    {
                        appendCode = appendCode.replace( "<option value=\"true\">", "<option value=\""
                            + i18n.getString( "true" ) + "\" selected>" );
                    }

                    if ( modelTypeAttributeValue.equalsIgnoreCase( "false" ) )
                    {
                        appendCode = appendCode.replace( "<option value=\"false\">", "<option value=\""
                            + i18n.getString( "false" ) + "\" selected>" );
                    }
                }

                appendCode += "</select>";

                // -----------------------------------------------------------
                // Check if this dataElement is from another programStage then
                // disable
                // If programStagsInstance is completed then disabled it
                // -----------------------------------------------------------

                disabled = "";
                if ( modelTypeId != modelType.getId() )
                {
                    disabled = "disabled";
                }
                // -----------------------------------------------------------
                // 
                // -----------------------------------------------------------

                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTEID", String.valueOf( modelTypeAttributeId ) );
                appendCode = appendCode.replace( "$MODELTYPEID", String.valueOf( modelTypeId ) );
                appendCode = appendCode.replace( "$MODELTYPENAME", modelTypeName );
                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTENAME", modelTypeAttribute.getName() );
                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTETYPE", modelTypeAttributeType );
                appendCode = appendCode.replace( "$DISABLED", disabled );
                appendCode = appendCode.replace( "$MANDATORY", mandatory );
                appendCode = appendCode.replace( "i18n_yes", i18n.getString( "yes" ) );
                appendCode = appendCode.replace( "i18n_no", i18n.getString( "no" ) );
                appendCode = appendCode.replace( "i18n_select_value", i18n.getString( "select_value" ) );
                appendCode = appendCode.replace( "$SAVEMODE", "false" );

                appendCode = appendCode.replaceAll( "\\$", "\\\\\\$" );

                ModelTypeAttributeMatcher.appendReplacement( sb, appendCode );
            }
        }

        ModelTypeAttributeMatcher.appendTail( sb );

        return sb.toString();
    }


    private String populateCustomDataEntryForTextBox( String dataEntryFormCode,
        Collection<ModelAttributeValue> dataValues, String disabled, I18n i18n, ModelType modelType,
         Map<Integer, Collection<ModelAttributeValue>> mapDataValue )
    {
        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String jsCodeForInputs = " $DISABLED onchange=\"saveVal( $MODELTYPEATTRIBUTEID )\" data=\"{mandatory:$MANDATORY, modelTypeAttributeName:'$MODELTYPEATTRIBUTENAME', modelTypeAttributeValueType:'$MODELTYPEATTRIBUTEVALUETYPE'}\" onkeypress=\"return keyPress(event, this)\" style=\" text-align:center;\"  ";
       // final String jsCodeForOptions = " $DISABLED options='$OPTIONS' modelTypeAttributeId=\"$MODELTYPEATTRIBUTEID\" data=\"{mandatory:$MANDATORY, modelTypeAttributeName:'$MODELTYPEATTRIBUTENAME', modelTypeAttributeValueType:'$MODELTYPEATTRIBUTEVALUETYPE'}\" onkeypress=\"return keyPress(event, this)\" style=\" text-align:center;\"  ";

        
        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match ModelTypeAttributes in the HTML code
        // ---------------------------------------------------------------------
        
        Pattern INPUT_PATTERN = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );
        
        Matcher ModelTypeAttributeMatcher = INPUT_PATTERN.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching modelTypeAttribute fields
        // ---------------------------------------------------------------------
       
        Map<Integer, ModelTypeAttribute> modelTypeAttributeMap = getModelTypeAttributeMap( modelType );
        int modelTypeId = modelType.getId();
       // Map<Integer, DataElement> dataElementMap = getDataElementMap( programStage );

        while ( ModelTypeAttributeMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String mandatory = "null";
            
            String modelTypeAttributeCode = ModelTypeAttributeMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_FIELD.matcher( modelTypeAttributeCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get modelType attribute ID of modelType attribute
                // -------------------------------------------------------------

                //int modelTypeId = Integer.parseInt( identifierMatcher.group( 1 ) );
                
                int modelTypeAttributeId = Integer.parseInt( identifierMatcher.group( 1 ) );

                ModelTypeAttribute modelTypeAttribute = null;

                String modelTypeName = modelType.getName();

                
                if ( modelTypeId != modelType.getId() )
                {
                    modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( modelTypeAttributeId );
                }
                
                else
                {
                    modelTypeAttribute = modelTypeAttributeMap.get( modelTypeAttributeId );
                    
                    if ( modelTypeAttribute == null )
                    {
                        return i18n.getString( "some_modelType_attribute_not_exist" );
                    }
                   
                    mandatory = BooleanUtils.toStringTrueFalse( modelTypeAttribute.isMandatory() );
                    
                }

                if ( modelTypeAttribute == null )
                {
                    continue;
                }
               
                if ( !ModelTypeAttribute.TYPE_INT.equalsIgnoreCase( modelTypeAttribute.getValueType() )
                    && !ModelTypeAttribute.TYPE_STRING.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
                {
                    continue;
                }
               
                
                // -------------------------------------------------------------
                // Find type of modelType attribute
                // -------------------------------------------------------------

                String modelTypeAttributeType = modelTypeAttribute.getValueType();

                
                // -------------------------------------------------------------
                // Find existing value of modelType Attribute
                // -------------------------------------------------------------

                ModelAttributeValue modelAttributeValue = null;
               

                String modelTypeAttributeValue = EMPTY;
                
                
                
                if ( modelTypeId != modelType.getId() )
                {
                    
                    
                    Collection<ModelAttributeValue> modelAttributeValues = mapDataValue.get( modelTypeId );

                    if ( modelAttributeValues == null )
                    {
                       
                    }

                    modelAttributeValue = getValue( modelAttributeValues, modelTypeAttributeId );

                    modelTypeAttributeValue = modelAttributeValue != null ? modelAttributeValue.getValue() : modelTypeAttributeValue;
                }
                else
                {
                    modelAttributeValue = getValue( dataValues, modelTypeAttributeId );

                    if ( modelAttributeValue != null )
                    {
                        modelTypeAttributeValue = modelAttributeValue.getValue();
                    }
                }

                // -------------------------------------------------------------
                // Insert title information - Data element id, name, type, min,
                // max
                // -------------------------------------------------------------
                
                
                if ( modelTypeAttributeCode.contains( "title=\"\"" ) )
                {
                    modelTypeAttributeCode = modelTypeAttributeCode.replace( "title=\"\"", "title=\"" + modelTypeAttribute.getId() + "."
                        + modelTypeAttribute.getName() + " (" + modelTypeAttributeType + ")\" " );
                }
                else
                {
                    modelTypeAttributeCode += "title=\"" + modelTypeAttribute.getId() + "." + modelTypeAttribute.getName() + " ("
                        + modelTypeAttributeType + ")\" ";
                }

                // -------------------------------------------------------------
                // Insert value of modelType Attribute in output code
                // -------------------------------------------------------------
                
                String appendCode = modelTypeAttributeCode;

                if ( appendCode.contains( "value=\"\"" ) )
                {
                    appendCode = appendCode.replace( "value=\"\"", "value=\"" + modelTypeAttributeValue + "\"" );
                }
                else
                {
                    appendCode += "value=\"" + modelTypeAttributeValue + "\"";
                }

                appendCode += jsCodeForInputs;
              

                appendCode += " />";


                disabled = "";
                
                if ( modelTypeId != modelType.getId() )
                {
                    disabled = "disabled=\"\"";
                }
                
                // -----------------------------------------------------------
                // 
                // -----------------------------------------------------------
                
                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTEID", String.valueOf( modelTypeAttributeId ) );
                appendCode = appendCode.replace( "$MODELTYPEID", String.valueOf( modelTypeId ) );
                appendCode = appendCode.replace( "$MODELTYPENAME", modelTypeName );
                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTENAME", modelTypeAttribute.getName() );
                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTEVALUETYPE", modelTypeAttributeType );
                appendCode = appendCode.replace( "$DISABLED", disabled );
                appendCode = appendCode.replace( "$MANDATORY", mandatory );
                appendCode = appendCode.replace( "$SAVEMODE", "false" );
                appendCode = appendCode.replaceAll( "\\$", "\\\\\\$" );
                
                System.out.println( "---appendCode---" + appendCode );
                
                ModelTypeAttributeMatcher.appendReplacement( sb, appendCode );
            }
        }

        ModelTypeAttributeMatcher.appendTail( sb );

        return sb.toString();
    }    
    
 
    
    
    
    private String populateCustomDataEntryForCOMBO( String dataEntryFormCode,
        Collection<ModelAttributeValue> dataValues, String disabled, I18n i18n, ModelType modelType,
         Map<Integer, Collection<ModelAttributeValue>> mapDataValue )
    {
        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------

        //final String jsCodeForInputs = " $DISABLED onchange=\"saveVal( $MODELTYPEATTRIBUTEID )\" data=\"{mandatory:$MANDATORY, modelTypeAttributeName:'$MODELTYPEATTRIBUTENAME', modelTypeAttributeValueType:'$MODELTYPEATTRIBUTEVALUETYPE'}\" onkeypress=\"return keyPress(event, this)\" style=\" text-align:center;\"  ";
        final String jsCodeForOptions = " $DISABLED options='$OPTIONS' modelTypeAttributeId=\"$MODELTYPEATTRIBUTEID\" data=\"{mandatory:$MANDATORY, modelTypeAttributeName:'$MODELTYPEATTRIBUTENAME', modelTypeAttributeValueType:'$MODELTYPEATTRIBUTEVALUETYPE'}\" onkeypress=\"return keyPress(event, this)\" style=\" text-align:center;\"  ";

        
        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match ModelTypeAttributes in the HTML code
        // ---------------------------------------------------------------------

        Pattern INPUT_PATTERN = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );
        
        Matcher ModelTypeAttributeMatcher = INPUT_PATTERN.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching modelTypeAttribute fields
        // ---------------------------------------------------------------------
       
        Map<Integer, ModelTypeAttribute> modelTypeAttributeMap = getModelTypeAttributeMap( modelType );
        int modelTypeId = modelType.getId();

       // Map<Integer, DataElement> dataElementMap = getDataElementMap( programStage );

        while ( ModelTypeAttributeMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String mandatory = "null";
            
            String modelTypeAttributeCode = ModelTypeAttributeMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_FIELD.matcher( modelTypeAttributeCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get modelType attribute ID of modelType attribute
                // -------------------------------------------------------------

                //int modelTypeId = Integer.parseInt( identifierMatcher.group( 1 ) );
                
                int modelTypeAttributeId = Integer.parseInt( identifierMatcher.group( 1 ) );

                ModelTypeAttribute modelTypeAttribute = null;

                String modelTypeName = modelType.getName();

                
                if ( modelTypeId != modelType.getId() )
                {
                    modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( modelTypeAttributeId );
                }
                
                else
                {
                    modelTypeAttribute = modelTypeAttributeMap.get( modelTypeAttributeId );
                    
                    if ( modelTypeAttribute == null )
                    {
                        return i18n.getString( "some_modelType_attribute_not_exist" );
                    }
                   
                    mandatory = BooleanUtils.toStringTrueFalse( modelTypeAttribute.isMandatory() );
                    
                }

                if ( modelTypeAttribute == null )
                {
                    continue;
                }
                
                if ( !ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
                {
                    continue;
                }
               
                // -------------------------------------------------------------
                // Find type of modelType attribute
                // -------------------------------------------------------------

                String modelTypeAttributeType = modelTypeAttribute.getValueType();

                
                // -------------------------------------------------------------
                // Find existing value of modelType Attribute
                // -------------------------------------------------------------

                ModelAttributeValue modelAttributeValue = null;
               

                String modelTypeAttributeValue = EMPTY;
                
                
                
                if ( modelTypeId != modelType.getId() )
                {
                    
                    Collection<ModelAttributeValue> modelAttributeValues = mapDataValue.get( modelTypeId );

                    if ( modelAttributeValues == null )
                    {
                       
                    }

                    modelAttributeValue = getValue( modelAttributeValues, modelTypeAttributeId );

                    modelTypeAttributeValue = modelAttributeValue != null ? modelAttributeValue.getValue() : modelTypeAttributeValue;
                }
                else
                {
                    modelAttributeValue = getValue( dataValues, modelTypeAttributeId );

                    if ( modelAttributeValue != null )
                    {
                        modelTypeAttributeValue = modelAttributeValue.getValue();
                    }
                }

                
                String appendCode = modelTypeAttributeCode.replaceFirst( "input", "select" );
                appendCode = appendCode.replace( "name=\"entryfield\"", jsCodeForOptions );

                // -------------------------------------------------------------
                // Add title
                // -------------------------------------------------------------

                if ( modelTypeAttributeCode.contains( "title=\"\"" ) )
                {
                    appendCode = appendCode.replace( "title=\"\"", "title=\"" + modelTypeAttribute.getId() + "."
                        + modelTypeAttribute.getName() + "-" + modelTypeAttributeType + "\" " );
                }
                else
                {
                    appendCode += "title=\"" + modelTypeAttribute.getId() + "." + modelTypeAttribute.getName() + "-"
                        + modelTypeAttributeType + "\" ";
                }

                appendCode += ">";
                
                
                appendCode += "<option value=\"\">" + i18n.getString( "Please select" ) + "</option>";
                
                for( ModelTypeAttributeOption modelTypeAttributeOption : modelTypeAttribute.getAttributeOptions() )
                {
                    appendCode += "<option value=\""+ modelTypeAttributeOption.getId() +"\">" + modelTypeAttributeOption.getName() + "</option>";
                }
                //appendCode += "<option value=\"true\">" + i18n.getString( "yes" ) + "</option>";
                //appendCode += "<option value=\"false\">" + i18n.getString( "no" ) + "</option>";
                
                //--------------------------------------------------------------
                // Insert title information - Data element id, name, type, min,
                // max
                // -------------------------------------------------------------

                /*
                if ( modelTypeAttributeCode.contains( "title=\"\"" ) )
                {
                    modelTypeAttributeCode = modelTypeAttributeCode.replace( "title=\"\"", "title=\"" + modelTypeAttribute.getId() + "."
                        + modelTypeAttribute.getName() + " (" + modelTypeAttributeType + ")\" " );
                }
                else
                {
                    modelTypeAttributeCode += "title=\"" + modelTypeAttribute.getId() + "." + modelTypeAttribute.getName() + " ("
                        + modelTypeAttributeType + ")\" ";
                }
*/
                // -------------------------------------------------------------
                // Insert value of modelType Attribute in output code
                // -------------------------------------------------------------
                
                //String appendCode = modelTypeAttributeCode;

                /*
                if ( appendCode.contains( "value=\"\"" ) )
                {
                    appendCode = appendCode.replace( "value=\"\"", "value=\"" + modelTypeAttributeValue + "\"" );
                }
                else
                {
                    appendCode += "value=\"" + modelTypeAttributeValue + "\"";
                }

                if ( modelTypeAttribute.getAttributeOptions() != null )
                {
                    appendCode += jsCodeForOptions;

                    appendCode = appendCode.replace( "$OPTIONS", modelTypeAttribute.getAttributeOptions().toString() );
                }

                appendCode += " />";
                */
                appendCode += "</select>";
                disabled = "";
                
                if ( modelTypeId != modelType.getId() )
                {
                    disabled = "disabled=\"\"";
                }
                
                // -----------------------------------------------------------
                // 
                // -----------------------------------------------------------

                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTEID", String.valueOf( modelTypeAttributeId ) );
                appendCode = appendCode.replace( "$MODELTYPEID", String.valueOf( modelTypeId ) );
                appendCode = appendCode.replace( "$MODELTYPENAME", modelTypeName );
                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTENAME", modelTypeAttribute.getName() );
                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTEVALUETYPE", modelTypeAttributeType );
                appendCode = appendCode.replace( "$DISABLED", disabled );
                appendCode = appendCode.replace( "$MANDATORY", mandatory );
                appendCode = appendCode.replace( "$SAVEMODE", "false" );
                appendCode = appendCode.replaceAll( "\\$", "\\\\\\$" );
                
                //System.out.println( "---appendCode---" + appendCode );
                
                ModelTypeAttributeMatcher.appendReplacement( sb, appendCode );
            }
        }

        ModelTypeAttributeMatcher.appendTail( sb );

        return sb.toString();
    }    
   
    
    private String populateCustomDataEntryForDate( String dataEntryFormCode, Collection<ModelAttributeValue> dataValues,
        String disabled, I18n i18n, ModelType modelType, Map<Integer, Collection<ModelAttributeValue>> mapDataValue )
    {
        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String jsCodeForDate = " name=\"entryfield\" $DISABLED data=\"{mandatory:$MANDATORY, modelTypeAttributeName:'$MODELTYPEATTRIBUTENAME'}\" onchange=\"saveVal( $MODELTYPEATTRIBUTEID )\" style=\" text-align:center;\" ";
        
        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String jQueryCalendar = "<script> " + "datePicker(\"$MODELTYPEID-$MODELTYPEATTRIBUTEID-val\", false)"
            + ";</script>";
       
        //System.out.println( "jQueryCalendar" + jQueryCalendar +  "--- $MODELTYPEID----" + modelType.getId());
        
        StringBuffer sb = new StringBuffer();


        // ---------------------------------------------------------------------
        // Pattern to match ModelTypeAttributes in the HTML code
        // ---------------------------------------------------------------------

        Pattern modelTypeAttributePattern = Pattern.compile( "(<input.*?)[/]?/>" );
        
        Matcher ModelTypeAttributeMatcher = modelTypeAttributePattern.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Pattern to extract modelTypeAttribute ID from modelTypeAttribute field
        // ---------------------------------------------------------------------

        // ---------------------------------------------------------------------
        // Iterate through all matching modelTypeAttribute fields
        // ---------------------------------------------------------------------
        
        Map<Integer, ModelTypeAttribute> modelTypeAttributeMap = getModelTypeAttributeMap( modelType );
        
        int modelTypeId = modelType.getId();
       
        while ( ModelTypeAttributeMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String mandatory = "null";
           

            String modelTypeAttributeCode = ModelTypeAttributeMatcher.group( 1 );
            
            Matcher identifierMatcher = IDENTIFIER_PATTERN_FIELD.matcher( modelTypeAttributeCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------

                //int modelTypeId = Integer.parseInt( identifierMatcher.group( 1 ) );
                
                int modelTypeAttributeId = Integer.parseInt( identifierMatcher.group( 1 ) );

                ModelTypeAttribute modelTypeAttribute = null;

                String modelTypeName = modelType.getName();
                

                if ( modelTypeId != modelType.getId() )
                {
                    modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( modelTypeAttributeId );
                }
                else
                {
                   
                    
                    modelTypeAttribute = modelTypeAttributeMap.get( modelTypeAttributeId );
                    
                    if ( modelTypeAttribute == null )
                    {
                        return i18n.getString( "some_modelType_attribute_not_exist" );
                    }
                   
                    mandatory = BooleanUtils.toStringTrueFalse( modelTypeAttribute.isMandatory() );
                    
                }

                if ( modelTypeAttribute == null )
                {
                    continue;
                }
                if ( !ModelTypeAttribute.TYPE_DATE.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
                {
                    continue;
                }

                // -------------------------------------------------------------
                // Find type of modelType attribute
                // -------------------------------------------------------------

                String modelTypeAttributeType = modelTypeAttribute.getValueType();
                
                
                // -------------------------------------------------------------
                // Find existing value of modelType Attribute
                // -------------------------------------------------------------

                ModelAttributeValue modelAttributeValue = null;
               

                String modelTypeAttributeValue = EMPTY;

                if ( modelTypeId != modelType.getId() )
                {
                    
                    
                    Collection<ModelAttributeValue> modelAttributeValues = mapDataValue.get( modelTypeId );

                    if ( modelAttributeValues == null )
                    {
                       
                    }

                    modelAttributeValue = getValue( modelAttributeValues, modelTypeAttributeId );

                    modelTypeAttributeValue = modelAttributeValue != null ? modelAttributeValue.getValue() : modelTypeAttributeValue;
                }
                else
                {
                    
                    modelAttributeValue = getValue( dataValues, modelTypeAttributeId );
                    
                    modelTypeAttributeValue = modelAttributeValue != null ? modelAttributeValue.getValue() : modelTypeAttributeValue;
                }


                // -------------------------------------------------------------
                // Insert value of modelTypeAttribute in output code
                // -------------------------------------------------------------

                if ( modelTypeAttributeCode.contains( "value=\"\"" ) )
                {
                    modelTypeAttributeCode = modelTypeAttributeCode.replace( "value=\"\"", "value=\"" + modelTypeAttributeValue + "\"" );
                }
                else
                {
                    modelTypeAttributeCode += "value=\"" + modelTypeAttributeValue + "\"";
                }

                // -------------------------------------------------------------
                // Insert title information - modelTypeAttribute id, name, type,
                // -------------------------------------------------------------

                if ( modelTypeAttributeCode.contains( "title=\"\"" ) )
                {
                    modelTypeAttributeCode = modelTypeAttributeCode.replace( "title=\"\"", "title=\"" + modelTypeAttribute.getId() + "."
                        + modelTypeAttribute.getName() + " (" + modelTypeAttributeType + ")\" " );
                }
                else
                {
                    modelTypeAttributeCode += "title=\"" + modelTypeAttribute.getId() + "." + modelTypeAttribute.getName() + " ("
                        + modelTypeAttributeType + ")\" ";
                }

                // -------------------------------------------------------------
                // Append Javascript code and meta data (type/min/max) for
                // persisting to output code, and insert value and type for
                // fields
                // -------------------------------------------------------------

                String appendCode = modelTypeAttributeCode + "/>";
                appendCode = appendCode.replace( "name=\"entryfield\"", jsCodeForDate );

               
                disabled = "";
                if ( modelTypeId != modelType.getId() )
                {
                    disabled = "disabled=\"\"";
                }

                appendCode += jQueryCalendar;

                // -------------------------------------------------------------
                // 
                // -------------------------------------------------------------

                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTEID", String.valueOf( modelTypeAttributeId ) );
                appendCode = appendCode.replace( "$MODELTYPEID", String.valueOf( modelTypeId ) );
                appendCode = appendCode.replace( "$MODELTYPENAME", modelTypeName );
                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTENAME", modelTypeAttribute.getName() );
                appendCode = appendCode.replace( "$MODELTYPEATTRIBUTETYPE", modelTypeAttributeType );
                appendCode = appendCode.replace( "$DISABLED", disabled );
                appendCode = appendCode.replace( "$MANDATORY", mandatory );
                appendCode = appendCode.replace( "$SAVEMODE", "false" );

                appendCode = appendCode.replaceAll( "\\$", "\\\\\\$" );
                
                
               // System.out.println( "---appendCode---" + appendCode );
                
                ModelTypeAttributeMatcher.appendReplacement( sb, appendCode );
            }
        }

        ModelTypeAttributeMatcher.appendTail( sb );

        return sb.toString();
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
    
    
    /**
     * Returns the value of the ModelAttributeValue in the Collection of DataValues
     * with the given data element identifier.
     */
    private ModelAttributeValue getValue( Collection<ModelAttributeValue> dataValues, int modelTypeAttributeId )
    {
        for ( ModelAttributeValue dataValue : dataValues )
        {
            if ( dataValue.getModelTypeAttribute().getId() == modelTypeAttributeId )
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
    @SuppressWarnings( "unused" )
    private Map<Integer, ModelTypeAttribute> getModelTypeAttributeMap( ModelType modelType )
    {
        Collection<ModelTypeAttribute> modelTypeAttributes =  modelType.getModelTypeAttributes();
        
        if ( modelType == null )
        {
            return null;
        }
        Map<Integer, ModelTypeAttribute> map = new HashMap<Integer, ModelTypeAttribute>();

        for ( ModelTypeAttribute attribute : modelTypeAttributes )
        {
            map.put( attribute.getId(), attribute );
        }

        return map;
    }
}
