// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showAttributeGroupDetails( attributeGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'attributeGroup' );
    request.setCallbackSuccess( attributeGroupReceived );
    request.send( 'getAttributeGroup.action?id=' + attributeGroupId );
}

function attributeGroupReceived( attributeGroupElement )
{
	setInnerHTML( 'idField', getElementValue( attributeGroupElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( attributeGroupElement, 'name' ) );	
     
    showDetails();
}


//-----------------------------------------------------------------------------
//Remove Attribute Group
//-----------------------------------------------------------------------------

function removeAttributeGroup( attributeGroupId, name )
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
 	var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removeAttributeGroupCompleted );
     window.location.href = 'removeAttributeGroup.action?id=' + attributeGroupId;
 }
}

function removeAttributeGroupCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
     window.location.href = 'attributeGroup.action';
 }
 else if ( type = 'error' )
 {
     setFieldValue( 'warningField', message );
     
     showWarning();
 }
}
//-----------------------------------------------------------------------------
//Add Attribute Group
//-----------------------------------------------------------------------------

function validateAddAttributeGroup()
{
	var params  = 'nameField=' + getFieldValue( 'nameField' );		
		
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( addValidationCompleted );
	request.sendAsPost(params);	
	request.send( 'validateAttributeGroup.action' );        

 return false;
}

function addValidationCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
	 selectAllById('selectedAttributes');
     var form = document.getElementById( 'addAttributeGroupForm' );        
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
//Update Attribute Group
//-----------------------------------------------------------------------------

function validateUpdateAttributeGroup()
{
	var params  = 'id=' + getFieldValue( 'id' );
		params += '&nameField=' + getFieldValue( 'nameField' );	
	
	var request = new Request();
 request.setResponseTypeXML( 'message' );
 request.setCallbackSuccess( updateValidationGroupCompleted );   
 request.sendAsPost(params);
 request.send( 'validateAttributeGroup.action' );
     
 return false;
}

function updateValidationGroupCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
	selectAllById('selectedAttributes');
 	var form = document.getElementById( 'updateAttributeGroupForm' );        
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