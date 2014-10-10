// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showTargetIndicatorDetails( targetIndicatorId )
{
    var url = 'getTargetIndicator.action?id=' + targetIndicatorId;
    var request = new Request();
    request.setResponseTypeXML( 'targetIndicator' );
    request.setCallbackSuccess( targetIndicatorReceived );
    request.send( url );
}

function targetIndicatorReceived( targetIndicatorElement )
{
	setInnerHTML( 'idField', getElementValue( targetIndicatorElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( targetIndicatorElement, 'name' ) );  
	setInnerHTML( 'orgunitGroupField', getElementValue( targetIndicatorElement, 'organisationUnitGroup' ) );
	setInnerHTML( 'attributeOptionGroupField', getElementValue( targetIndicatorElement, 'attributeOptionGroup' ) );
	setInnerHTML( 'yearField', getElementValue( targetIndicatorElement, 'year' ) );
	setInnerHTML( 'valueField', getElementValue( targetIndicatorElement, 'value' ) );
     
    showDetails();
}


//-----------------------------------------------------------------------------
//Remove TargetIndicator
//-----------------------------------------------------------------------------

function removeTargetIndicator( targetIndicatorId, name )
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
  
  var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removeTargetIndicatorCompleted );     
     window.location.href = 'removeTargetIndicator.action?id=' + targetIndicatorId;
 }
}

function removeTargetIndicatorCompleted( messageElement )
{
 var type = messageElement.getTargetIndicator( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
     window.location.href = 'hrTargetIndicator.action';
 }
 else if ( type = 'error' )
 {
     setFieldValue( 'warningField', message );
     
     showWarning();
 }
}

//-----------------------------------------------------------------------------
//Add TargetIndicator
//-----------------------------------------------------------------------------
function validateAddTargetIndicator()
{
  
  var url = 'validateTargetIndicator.action?' +
      'targetIndicatorName=' + getFieldValue( 'targetIndicatorName' ) ;
  
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
     var form = document.getElementById( 'addTargetIndicatorForm' );        
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
//Update Target Indicator
//-----------------------------------------------------------------------------

function validateUpdateTargetIndicator()
{
  
 var url = 'validateTargetIndicator.action?' + 
    'id=' + getFieldValue( 'id' ) +
    '&targetIndicatorName=' + getFieldValue( 'targetIndicatorName' ) ;
  
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
  var form = document.getElementById( 'updateTargetIndicatorForm' );        
     form.submit();
 }
 else if ( type == 'error' )
 {
     window.alert( i18n_saving_target_indicator_failed + ':' + '\n' + message );
 }
 else if ( type == 'input' )
 {
     document.getElementById( 'message' ).innerHTML = message;
     document.getElementById( 'message' ).style.display = 'block';
 }
}