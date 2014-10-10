// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------


//-----------------------------------------------------------------------------
//Remove History
//-----------------------------------------------------------------------------

function removeHistory( historyId, name )
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
	
 	var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removeHistoryCompleted );     
     window.location.href = 'removeHistory.action?id=' + historyId;
 }
}

function removeHistoryCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
     window.location.href = 'history.action';
 }
 else if ( type = 'error' )
 {
     setFieldValue( 'warningField', message );
     
     showWarning();
 }
}

//-----------------------------------------------------------------------------
// Add History
//-----------------------------------------------------------------------------

function validateAddHistory()
{
	
	var url = 'validateHistory.action?' +
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
     var form = document.getElementById( 'addHistoryForm' );        
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


//-----------------------------------------------------------------------------
//History Type Selection
//-----------------------------------------------------------------------------

function historyTypeSelected(){
	
	//var CurrentAttributeId = $( '#' + attributeId ).val();
	var attributeId = $( '#historyType' ).val();
	
	if ( attributeId && attributeId != -1 )
	{
		
		var url = 'loadAttributeOption.action?attributeId=' + attributeId;
		
		var list = document.getElementById( 'history' );
		//alert( list );
		
	    clearList( list );
	    
	    
	    $.getJSON( url, function( json ) {
	    	addOptionToList( list, '-1', '[ Select Options ]' );
	    	for ( i in json.attributesOptions ) {
	    		addOptionToList( list, json.attributesOptions[i].id, json.attributesOptions[i].value );
	    	}
	    } );
	}
}