// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPersonDetails( personId )
{
    var url = 'getPerson.action?id=' + personId;
    var request = new Request();
    request.setResponseTypeXML( 'person' );
    request.setCallbackSuccess( personReceived );
    request.send( url );
}

function personReceived( personElement )
{
  setFieldValue( 'idField', getElementValue( personElement, 'id' ) );
  setFieldValue( 'firstNameField', getElementValue( personElement, 'firstName' ) );  
  setFieldValue( 'middleNameField', getElementValue( personElement, 'middleName' ) );
  setFieldValue( 'lastNameField', getElementValue( personElement, 'lastName' ) );
  setFieldValue( 'birthDateField', getElementValue( personElement, 'birthDate' ) );
  setFieldValue( 'nationalityField', getElementValue( personElement, 'nationality' ) );
  setFieldValue( 'dataSetField', getElementValue( personElement, 'dataSet' ) );
  setFieldValue( 'valueCountField', getElementValue( personElement, 'valueCount' ) );
  setFieldValue( 'historyCountField', getElementValue( personElement, 'historyCount' ) );
  setFieldValue( 'trainingCountField', getElementValue( personElement, 'trainingCount' ) );
  
  showDetails();
}


//-----------------------------------------------------------------------------
//Remove Person
//-----------------------------------------------------------------------------

function removePerson( personId, name )
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
  
  var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removePersonCompleted );     
     window.location.href = 'removePerson.action?id=' + personId;
 }
}

function removePersonCompleted( messageElement )
{
 var type = messageElement.getPerson( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
     window.location.href = 'hrPerson.action';
 }
 else if ( type = 'error' )
 {
     setFieldValue( 'warningField', message );
     
     showWarning();
 }
}

//-----------------------------------------------------------------------------
//Add Person
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