
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showLineListOptionDetails( LineListOptionId)
{
    var request = new Request();
    request.setResponseTypeXML( 'lineListOption' );
    request.setCallbackSuccess( LineListOptionReceived );
    request.send( 'getLineListOption.action?id=' + LineListOptionId );
}

function LineListOptionReceived( LineListOptionElement )
{
   /*
	setFieldValue( 'idField', getElementValue( LineListOptionElement, 'id' ) );
    setFieldValue( 'nameField', getElementValue( LineListOptionElement, 'name' ) );
    setFieldValue( 'shortNameField', getElementValue( LineListOptionElement, 'shortName' ) );
   */
	setInnerHTML( 'idField', getElementValue( LineListOptionElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( LineListOptionElement, 'name' ) );
	setInnerHTML( 'shortNameField', getElementValue( LineListOptionElement, 'shortName' ) );
	
	
	// description may be null
	var description = getElementValue( LineListOptionElement, 'description' );
	setInnerHTML( 'descriptionField', description ? description : '[' + none + ']' );
	
    showDetails();
}

function getLineListOptions( LineListOptionGroupId, type )
{	
    var url = "getLineListOptions.action?";

    if ( LineListOptionGroupId == '[select]' )
    {
    	return;
    }

	if ( LineListOptionGroupId != null )
	{
		url += "LineListOptionGroupId=" + LineListOptionGroupId;
	}
	
    var request = new Request();
    request.setResponseTypeXML( 'LineListOption' );
    request.setCallbackSuccess( getLineListOptionsReceived );
    request.send( url );	
}

function getLineListOptionsReceived( xmlObject )
{	
	var availableLineListOptions = document.getElementById( "availableLineListOptions" );
	
	clearList( availableLineListOptions );
	
	var LineListOptions = xmlObject.getElementsByTagName( "LineListOption" );
	
	for ( var i = 0; i < LineListOptions.length; i++ )
	{
		var id = LineListOptions[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var LineListOptionName = LineListOptions[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
		var option = document.createElement( "option" );
		option.value = id;
		option.text = LineListOptionName;
		option.title = LineListOptionName;
		availableLineListOptions.add( option, null );
	}
}

// -----------------------------------------------------------------------------
// Remove data element
// -----------------------------------------------------------------------------

function removeLineListOption( LineListOptionId, LineListOptionName )
{
    //var result = window.confirm( i18n_confirm_delete + '\n\n' + LineListOptionName );
    
    var result = window.confirm( i18n_confirm_delete + '\n\n' + "Option Id =" + LineListOptionId + '\n\n' + "Option Name =" + LineListOptionName );
    
    if ( result )
    {
       	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeLineListOptionCompleted );
        request.send( 'removeLineListOption.action?id=' + LineListOptionId );
    }    
}

function removeLineListOptionCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'getLineListOptions.action';
    }
    else if ( type == 'error' )
    {
        
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Add data element
// -----------------------------------------------------------------------------

function validateAddLineListOption()
{

    var url = 'validateLineListOption.action?' +
        '&name=' + htmlEncode( getFieldValue( 'name' ) ) +
        '&shortName=' + htmlEncode( getFieldValue( 'shortName' ) );
    
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );    
    request.send( url );

    return false;
}

/**
 * Make a CGI parameter string for the given field name and set of values
 * 
 * @param fieldName name of the field to make a string for
 * @param values array of values to add to the string
 * @returns String on the form '&fieldName=value1...$fieldName=valueN'
 */
function makeValueString( fieldName, values )
{
	var valueStr = "";
	for ( var i = 0, value; value = values[i]; i++ )
	{
		valueStr += "&" + fieldName + "=" + value;
	}
	
	return valueStr;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'addLineListOptionForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_data_element_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

/**
 * Returns the first value of the specified select box
 * 
 * @param selectId
 * @return The first (or only) String value of the given select box, 
 * 		or null if no options are selected
 */
function getSelectValue( selectId )
{
	var select = document.getElementById( selectId );
	var option = select.options[select.selectedIndex];
	
	if ( option )
	{
		return option.value;
	}
	else
	{
		return null;
	}
}

/**
 * Returns the values for the specified select box
 * 
 * @param id id of the select box to get values for
 * @return Array of String values from the given select box,
 * 		or an empty array if no options are selected
 */
function getSelectValues( selectId )
{
	var select = document.getElementById( selectId );
	var values = [];
	for ( var i = 0, option; option = select.options[i]; i++ )
	{
		if ( option.selected )
		{
			values.push(option.value);
		}
	}
	
	return values;
}

/**
 * Returns the value for the specified checkbox
 * 
 * @param checkboxId id of the checkbox to get a value for
 * @return String value for the specified checkbox,
 * 		or null if the checkbox is not checked
 */
function getCheckboxValue( checkboxId )
{
	var checkbox = document.getElementById( checkboxId );
	
	return ( checkbox.checked ? checkbox.value : null );
}

/**
 * Returns the values for a set of inputs with the same name,
 * under a specified parent node.
 * 
 * @param parentId id of the parent node to limit the search to
 * @param fieldName form name of the inputs to get values for
 * @return Array with the String values for the specified inputs,
 * 		or an empty Array if no inputs with that name exist under the specified parent node
 */
function getInputValuesByParentId( parentId, fieldName )
{
	var node = document.getElementById(parentId);
	
	if ( ! node )
	{
		return [];
	}
	
	var inputs = node.getElementsByTagName("input");
	values = [];
	
	for ( var i = 0, input; input = inputs[i]; i++ )
	{
		if ( input.name == fieldName )
		{
			values.push(input.value);
		}
	}
	
	return values;	
}

// -----------------------------------------------------------------------------
// Update data element
// -----------------------------------------------------------------------------

function validateUpdateLineListOption()
{
    var url = 'validateLineListOption.action?' +
        '&id=' + getFieldValue( 'id' ) +
        '&name=' + htmlEncode( getFieldValue( 'name' ) ) +
        '&shortName=' + htmlEncode( getFieldValue( 'shortName' ) );
        
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.send( url );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'updateLineListOptionForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_data_element_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
