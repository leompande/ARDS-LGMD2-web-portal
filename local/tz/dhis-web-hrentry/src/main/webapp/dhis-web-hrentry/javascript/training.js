// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------


//-----------------------------------------------------------------------------
//Remove Training
//-----------------------------------------------------------------------------

function removeTraining( trainingId, name )
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
	
 	var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removeTrainingCompleted );     
     window.location.href = 'removeTraining.action?id=' + trainingId;
 }
}

function removeTrainingCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
     window.location.href = 'inServiceTraining.action';
 }
 else if ( type = 'error' )
 {
     setFieldValue( 'warningField', message );
     
     showWarning();
 }
}

//-----------------------------------------------------------------------------
// Add Training
//-----------------------------------------------------------------------------

function validateAddTraining()
{
	
	var url = 'validateTraining.action?' +
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
     var form = document.getElementById( 'addTrainingForm' );        
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
//Update Training
//-----------------------------------------------------------------------------

function validateUpdateTraining()
{
	
 var url = 'validateTraining.action?' + 
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
 	var form = document.getElementById( 'updateTrainingForm' );        
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