// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------
function showWarning()
{
    $( '#warningArea' ).show( "fast" );
}
function hideWarning()
{
    $( '#warningArea' ).hide( "fast" );
}
function setFieldValue( fieldId, value )
{
    jQuery("#" + fieldId).val( value );
}
function selectAllElementsById( listId1 ) {
    var list1 = document.getElementById( listId1 );
    for ( var i = 0; i < list1.options.length; i++ ) {
        list1.options[i].selected = true;
    }
}

function showCriteriaDetails( criteriaId )
{
    var request = new Request();
    request.setResponseTypeXML( 'criteriaIndicator' );
    request.setCallbackSuccess( criteriaReceived );
    request.send( 'getCriteria.action?id=' + criteriaId );
}

function criteriaReceived( attributeElement )
{
	setInnerHTML( 'idField', getElementValue( attributeElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( attributeElement, 'name' ) );
	setInnerHTML( 'attributeField', getElementValue(attributeElement, 'attribute'));
     
    showDetails();
}


//-----------------------------------------------------------------------------
//Remove Criteria
//-----------------------------------------------------------------------------

function removeCriteria( criteriaId, name )
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
	
 	var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removeCriteriaCompleted );
     request.send( 'removeCriteria.action?id=' + criteriaId );
 }
}

function removeCriteriaCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
	 document.getElementById( 'warningField' ).innerHTML = message;
     document.getElementById( 'warningField' ).style.display = 'block';
     jQuery.growlUI( i18n_warning, message, 'warningArea', 15 );
 }
 else if ( type = 'error' )
 {
     document.getElementById( 'warningField' ).innerHTML = message;
     document.getElementById( 'warningField' ).style.display = 'block';
     jQuery.growlUI( i18n_warning, message, 'warningArea', 15 );
 }
 window.location = 'criteria.action';
}


//-----------------------------------------------------------------------------
// Add Criteria
//-----------------------------------------------------------------------------

function validateAddCriteria()
{
	
	var url = 'validateCriteria.action?' +
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
	 selectAllElementsById( 'selectedAttributeOptions')
     var form = document.getElementById( 'addCriteriaForm' );        
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
//Update AggregateIndicator
//-----------------------------------------------------------------------------

function validateUpdateCriteria()
{
	
 var url = 'validateCriteria.action?' + 
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
	selectAllElementsById( 'selectedAttributeOptions' )
 	var form = document.getElementById( 'updateCriteriaForm' );        
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