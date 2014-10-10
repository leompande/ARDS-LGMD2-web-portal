// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showAttributeOptionsDetails( attributeOptionsId )
{
    var request = new Request();
    request.setResponseTypeXML( 'attributeOptions' );
    request.setCallbackSuccess( attributeOptionsReceived );
    request.send( 'getAttributeOptions.action?id=' + attributeOptionsId );
}

function attributeOptionsReceived( attributeOptionsElement )
{
	setInnerHTML( 'idField', getElementValue( attributeOptionsElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( attributeOptionsElement, 'value' ) );	
     
    showDetails();
}


//-----------------------------------------------------------------------------
//Remove Attribute Option
//-----------------------------------------------------------------------------

function removeAttributeOptions( attributeOptionsId, name, attributeId)
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
 	var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removeAttributeOptionsCompleted );
     window.location.href = 'removeAttributeOptions.action?id=' + attributeOptionsId + '&attributeId=' +attributeId;
 }
}

function removeAttributeOptionsCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
     window.location.href = 'defineAttributeOptionsForm.action';
 }
 else if ( type = 'error' )
 {
     setFieldValue( 'warningField', message );
     
     showWarning();
 }
}
//-----------------------------------------------------------------------------
//Add Attribute  Options
//-----------------------------------------------------------------------------

function validateAddAttributeOptions()
{
	
	var url = 'validateAttributeOptions.action?' +
			'nameField=' + getFieldValue( 'nameField' ) ;
	
	var request = new Request();
request.setResponseTypeXML( 'message' );
request.setCallbackSuccess( addValidationCompleted );    
request.send( url );        

return false;
}

function addValidationCompleted( messageElement )
{
var type = messageElement.getAttribute( 'type' );
var message = messageElement.firstChild.nodeValue;

if ( type == 'success' )
{
   var form = document.getElementById( 'addAttributeOptionsForm' );        
   form.submit();
}
else if ( type == 'error' )
{
   window.alert( i18n_adding_atttibute_failed + ':' + '\n' + message );
}
else if ( type == 'input' )
{
   document.getElementById( 'message' ).innerHTML = message;
   document.getElementById( 'message' ).style.display = 'block';
} 
}

//-----------------------------------------------------------------------------
//Update Attribute  Option
//-----------------------------------------------------------------------------

function validateUpdateAttributeOptions()
{
	
var url = 'validateAttributeOptions.action?' + 
		'id=' + getFieldValue( 'id' ) +
		'&nameField=' + getFieldValue( 'nameField' ) ;
	
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
	var form = document.getElementById( 'updateAttributeOptionsForm' );        
   form.submit();
}
else if ( type == 'error' )
{
   window.alert( i18n_saving_program_failed + ':' + '\n' + message );
}
else if ( type == 'input' )
{
   document.getElementById( 'message' ).innerHTML = message;
   document.getElementById( 'message' ).style.display = 'block';
}
}