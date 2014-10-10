/**
 * This file depends on form.js.
 * 
 * Format for the span/input identifiers for selectors:
 * 
 * {dataelementid}-{optioncomboid}-val // data value 
 * {dataelementid}-dataelement name of data element 
 * {optioncomboid}-optioncombo // name of category option combo 
 * {dataelementid}-cell // table cell for data element name
 * {dataelementid}-{optioncomboid}-min // min value for data value
 * {dataelementid}-{optioncomboid}-max // max value for data value
 */

// -----------------------------------------------------------------------------
// Save
// -----------------------------------------------------------------------------

var FORMULA_PATTERN = /#\{.+?\}/g;
var SEPARATOR = '.';
var EVENT_VALUE_SAVED = 'dhis-web-dataentry-value-saved';

function updateDataElementTotals()
{
	var currentTotals = [];
	
	$( 'input[name="total"]' ).each( function( index )
	{
		var targetId = $( this ).attr( 'dataelementid' );
		
		var totalValue = new Number();
		
		$( 'input[name="entryfield"]' ).each( function( index )
		{	
			var key = $( this ).attr( 'id' );
			var entryFieldId = key.substring( 0, key.indexOf( '-' ) );
			
			if ( targetId && $( this ).attr( 'value' ) && targetId == entryFieldId )
			{
				totalValue += new Number( $( this ).attr( 'value' ) );
			}
		} );
		
		$( this ).attr( 'value', totalValue );
	} );
}

/**
 * Updates all indicator input fields with the calculated value based on the
 * values in the input entry fields in the form.
 */
function updateIndicators()
{
    $( 'input[name="indicator"]' ).each( function( index )
    {
        var indicatorId = $( this ).attr( 'indicatorid' );

        var formula = dhis2.de.indicatorFormulas[indicatorId];
        
        if ( isDefined( formula ) )
        {        
	        var expression = generateExpression( formula );
	
	        if ( expression )
	        {
		        var value = eval( expression );
		        
		        value = isNaN( value ) ? '-' : roundTo( value, 1 );
		
		        $( this ).attr( 'value', value );
	        }
        }
        else
        {
        	log( 'Indicator does not exist in form: ' + indicatorId );
        }
    } );
}

/**
 * Parses the expression and substitues the operand identifiers with the value
 * of the corresponding input entry field.
 */
function generateExpression( expression )
{
    var matcher = expression.match( FORMULA_PATTERN );

    for ( k in matcher )
    {
        var match = matcher[k];

        // Remove brackets from expression to simplify extraction of identifiers

        var operand = match.replace( /[#\{\}]/g, '' );

        var dataElementId = operand.substring( 0, operand.indexOf( SEPARATOR ) );
        var categoryOptionComboId = operand.substring( operand.indexOf( SEPARATOR ) + 1, operand.length );

        var fieldId = '#' + dataElementId + '-' + categoryOptionComboId + '-val';

        var value = '0';
        
        if ( $( fieldId ).length )
        {
            value = $( fieldId ).val() ? $( fieldId ).val() : '0';
        }

        expression = expression.replace( match, value );
        
        // TODO signed numbers
    }

    return expression;
}

function saveVal( dataElementId, optionComboId, fieldId )
{
	var fieldIds = fieldId.split( "-" );

	if ( fieldIds.length > 3 )
	{
		dhis2.de.currentOrganisationUnitId = fieldIds[0];
	}

    fieldId = '#' + fieldId;

    var dataElementName = getDataElementName( dataElementId );
    var value = $( fieldId ).val();
    var type = getDataElementType( dataElementId );

    $( fieldId ).css( 'background-color', COLOR_YELLOW );

    var periodId = $( '#selectedPeriodId' ).val();

	if ( value == null )
	{
		value = '';
	}

	var warning = undefined;
	
    if ( value != '' )
    {
        if ( type == 'string' || type == 'int' || type == 'number' || type == 'positiveNumber' || type == 'negativeNumber' || type == 'zeroPositiveInt' )
        {
            if ( value.length > 255 )
            {
                return alertField( fieldId, i18n_value_too_long + ': ' + dataElementName );
            }
            if ( type == 'int' && !isInt( value ) )
            {
                return alertField( fieldId, i18n_value_must_integer + ': ' + dataElementName );
            }
            if ( type == 'number' && !isNumber( value ) )
            {
                return alertField( fieldId, i18n_value_must_number + ': ' + dataElementName );
            }
            if ( type == 'positiveNumber' && !isPositiveInt( value ) )
            {
                return alertField( fieldId, i18n_value_must_positive_integer + ': ' + dataElementName );
            }
            if ( type == 'negativeNumber' && !isNegativeInt( value ) )
            {
                return alertField( fieldId, i18n_value_must_negative_integer + ': ' + dataElementName );
            }
            if ( type == 'zeroPositiveInt' && !isZeroOrPositiveInt( value ) )
            {
                return alertField( fieldId, i18n_value_must_zero_or_positive_integer + ': ' + dataElementName );
            }
            if ( isValidZeroNumber( value ) )
            {
                // If value = 0 and zero not significant for data element, skip

                if ( dhis2.de.significantZeros.indexOf( dataElementId ) == -1 )
                {
                    $( fieldId ).css( 'background-color', COLOR_GREEN );
                    return false;
                }
            }

            var minString = dhis2.de.currentMinMaxValueMap[dataElementId + '-' + optionComboId + '-min'];
            var maxString = dhis2.de.currentMinMaxValueMap[dataElementId + '-' + optionComboId + '-max'];

            if ( minString && maxString ) // TODO if only one exists?
            {
                var valueNo = new Number( value );
                var min = new Number( minString );
                var max = new Number( maxString );

                if ( valueNo < min )
                {
                    warning = i18n_value_of_data_element_less + ': ' + min + '\n\n' + dataElementName;
                }

                if ( valueNo > max )
                {
                    warning = i18n_value_of_data_element_greater + ': ' + max + '\n\n' + dataElementName;
                }
            }
        }
    }
    
    var color = warning ? COLOR_ORANGE : COLOR_GREEN;
    
    var valueSaver = new ValueSaver( dataElementId,	periodId, optionComboId, value, fieldId, color );
    valueSaver.save();

    updateIndicators(); // Update indicators for custom form
    updateDataElementTotals(); // Update data element totals for custom forms
    
    if ( warning )
    {
    	window.alert( warning );
    }
}

function saveBoolean( dataElementId, optionComboId, fieldId )
{
    fieldId = '#' + fieldId;
    
    var value = $( fieldId + ' option:selected' ).val();

    $( fieldId ).css( 'background-color', COLOR_YELLOW );

    var periodId = $( '#selectedPeriodId' ).val();

    var valueSaver = new ValueSaver( dataElementId, periodId, optionComboId, value, fieldId, COLOR_GREEN );
    valueSaver.save();
}

function saveTrueOnly( dataElementId, optionComboId, fieldId )
{
    fieldId = '#' + fieldId;

    var value = $( fieldId ).is( ':checked' );
    
    value = ( value == true) ? value : undefined; // Send nothing if un-ticked

    $( fieldId ).css( 'background-color', COLOR_YELLOW );

    var periodId = $( '#selectedPeriodId' ).val();

    var valueSaver = new ValueSaver( dataElementId, periodId, optionComboId, value, fieldId, COLOR_GREEN );
    valueSaver.save();
}

/**
 * Supportive method.
 */
function alertField( fieldId, alertMessage )
{
    $( fieldId ).css( 'background-color', COLOR_YELLOW );
    $( fieldId ).select();
    $( fieldId ).focus();    
    window.alert( alertMessage );

    return false;
}

/**
 * Convenience method which can be used in custom form scripts. Do not change.
 */
function onValueSave( fn )
{
	$( 'body' ).off( EVENT_VALUE_SAVED ).on( EVENT_VALUE_SAVED, fn );
}

// -----------------------------------------------------------------------------
// Saver objects
// -----------------------------------------------------------------------------

/**
 * @param de data element identifier.
 * @param pe iso period.
 * @param co category option combo.
 * @param value value.
 * @param fieldId identifier of data input field.
 * @param resultColor the color code to set on input field for success.
 */
function ValueSaver( de, pe, co, value, fieldId, resultColor )
{
	var ou = getCurrentOrganisationUnit();
	
    var dataValue = {
        'de' : de,
        'co' : co,
        'ou' : ou,
        'pe' : pe,
        'value' : value
    };

    var cc = dhis2.de.getCurrentCategoryCombo();
    var cp = dhis2.de.getCurrentCategoryOptionsQueryValue();
    
    if ( cc && cp )
    {
    	dataValue.cc = cc;
    	dataValue.cp = cp;
    }
    
    this.save = function()
    {
    	dhis2.de.storageManager.saveDataValue( dataValue );

        $.ajax( {
            url: '../api/dataValues',
            data: dataValue,
            dataType: 'json',
            type: 'post',
            success: handleSuccess,
            error: handleError
        } );
    };
    
    function handleSuccess()
    {
    	dhis2.de.storageManager.clearDataValueJSON( dataValue );
        markValue( fieldId, resultColor );
        $( 'body' ).trigger( EVENT_VALUE_SAVED, dataValue );
    }

    function handleError( xhr, textStatus, errorThrown )
    {
    	if ( 409 == xhr.status ) // Invalid value or locked
    	{
    		markValue( fieldId, COLOR_RED );
    		setHeaderMessage( xhr.responseText );
    	}
    	else // No connection
    	{
    		setHeaderMessage( i18n_offline_notification );
    		markValue( fieldId, resultColor );
    	}
    }

    function markValue( fieldId, color )
    {
        $( fieldId ).css( 'background-color', color );
    }
}
