// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showAttributeDetails( attributeId )
{
    var request = new Request();
    request.setResponseTypeXML( 'attribute' );
    request.setCallbackSuccess( attributeReceived );
    request.send( 'getAttribute.action?id=' + attributeId );
}

function attributeReceived( attributeElement )
{
	setInnerHTML( 'idField', getElementValue( attributeElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( attributeElement, 'name' ) );	
	setInnerHTML( 'captionField', getElementValue( attributeElement, 'caption' ) );
	setInnerHTML( 'descriptionField', getElementValue( attributeElement, 'description' ) );
     
    showDetails();
}


//-----------------------------------------------------------------------------
//Remove Attribute
//-----------------------------------------------------------------------------

function removeAttribute( attributeId, name )
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
	
 	var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removeAttributeCompleted );     
     window.location.href = 'removeAttribute.action?id=' + attributeId;
 }
}

function removeAttributeCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
     window.location.href = 'hrAttribute.action';
 }
 else if ( type = 'error' )
 {
     setFieldValue( 'warningField', message );
     
     showWarning();
 }
}

//-----------------------------------------------------------------------------
// Add Attribute
//-----------------------------------------------------------------------------

function validateAddAttribute()
{
	
	var url = 'validateAttribute.action?' +
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
     var form = document.getElementById( 'addAttributeForm' );        
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
//Update Attribute
//-----------------------------------------------------------------------------

function validateUpdateAttribute()
{
	
 var url = 'validateAttribute.action?' + 
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
 	var form = document.getElementById( 'updateAttributeForm' );        
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