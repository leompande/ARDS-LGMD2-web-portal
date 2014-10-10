// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showAttributeOptionGroupDetails( attributeOptionGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'attributeOptionGroup' );
    request.setCallbackSuccess( attributeOptionGroupReceived );
    request.send( 'getAttributeOptionGroup.action?id=' + attributeOptionGroupId );
}

function attributeOptionGroupReceived( attributeOptionGroupElement )
{
	setInnerHTML( 'idField', getElementValue( attributeOptionGroupElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( attributeOptionGroupElement, 'name' ) );
	setInnerHTML( 'attributeOptionsCountField', getElementValue( attributeOptionGroupElement, 'attributeOptionsCount' ) );
     
    showDetails();
}


//-----------------------------------------------------------------------------
//Remove Attribute Option Group
//-----------------------------------------------------------------------------

function removeAttributeOptionGroup( attributeOptionGroupId, name )
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
 	var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removeAttributeOptionGroupCompleted );
     window.location.href = 'removeAttributeOptionGroup.action?id=' + attributeOptionGroupId;
 }
}

function removeAttributeOptionGroupCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
     window.location.href = 'attributeOptionGroup.action';
 }
 else if ( type = 'error' )
 {
     setFieldValue( 'warningField', message );
     
     showWarning();
 }
}
//-----------------------------------------------------------------------------
//Add Attribute Option Group
//-----------------------------------------------------------------------------

function validateAddAttributeOptionGroup()
{
	var params  = 'nameField=' + getFieldValue( 'nameField' );		
		params += '&' + getParamString( 'selectedAttributes' );
		
	var request = new Request();
 request.setResponseTypeXML( 'message' );
 request.setCallbackSuccess( addValidationCompleted );
	request.sendAsPost(params);	
 request.send( 'validateAttributeOptionGroup.action' );        

 return false;
}

function addValidationCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
		selectAllById('selectedAttributes');
     var form = document.getElementById( 'addAttributeOptionGroupForm' );        
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