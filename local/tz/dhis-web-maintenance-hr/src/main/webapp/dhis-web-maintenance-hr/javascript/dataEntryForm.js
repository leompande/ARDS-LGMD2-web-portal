
// -----------------------------------------------------------------------------
// Delete DataEntryForm
// -----------------------------------------------------------------------------

function removeDataEntryForm( dataEntryFormId, dataEntryFormName )
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( removeDataEntryFormCompleted );
 
  var requestString = 'delDataEntryForm.action?dataEntryFormId=' + dataEntryFormId;
  var result = window.confirm( i18n_confirm_delete + '\n\n' + dataEntryFormName );

  if ( result )
  {
    request.send( requestString );
  }
  
  return false;
}

function removeDataEntryFormCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
  else
  {
  	window.location.href = 'index.action';
  }
}


// ----------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------

function validateDataEntryForm()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  if(autoSave == false){
	request.setCallbackSuccess( dataEntryFormValidationCompleted );
  }
  else{
	request.setCallbackSuccess( autoSaveDataEntryFormValidationCompleted );
  }
  
  var requestString = 'validateDataEntryForm.action';
  
  var params = 'name=' + document.getElementById( 'nameField' ).value;
  
  if(stat == "ADD")
  {
  	
  }	
  else
  {
    params += '&dataEntryFormId=' + dataEntryFormId;      
  }        

  params += '&dataSetId=' + document.getElementById( 'dataSetIdField' ).value;
    
  request.sendAsPost( params );
  request.send( requestString );

  return false;
}

function dataEntryFormValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {  
      document.forms['saveDataEntryForm'].submit();
  }
  else if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
  else if ( type == 'mismatch' )
  {
    var result = window.confirm( message );

    if ( result )
    {
      document.forms['saveDataEntryForm'].submit();
    }
  }
}

// -----------------------------------------------------------------------------
// Find Selected DataElement Count in the DataEntryForm
// -----------------------------------------------------------------------------

function findDataElementCount()
{
  var request = new Request();
  request.setResponseTypeXML( 'dataSet' );
  request.setCallbackSuccess( findDataElementCountCompleted );

  // Clear the list
  var dataElementList = document.getElementById( 'dataElementSelector' );
  dataElementList.options.length = 0;

  var requestString = 'getSelectedDataElements.action';
  
  var params = 'dataSetId=' + document.getElementById( 'dataSetIdField' ).value;
        
  params += '&designCode=' + htmlCode;
  
  request.sendAsPost( params );
  request.send( requestString );

  return false;
}

function findDataElementCountCompleted( dataSetElement )
{
  var dataElements = dataSetElement.getElementsByTagName( 'dataElements' )[0];
  var dataElementList = dataElements.getElementsByTagName( 'dataElement' );

  var dataElementSelector = document.getElementById( 'dataElementSelector' );
  
  for ( var i = 0; i < dataElementList.length; i++ )
  {
    var dataElement = dataElementList[i];
    var name = dataElement.firstChild.nodeValue;
    var id = dataElement.getAttribute( 'id' );	
		
	var option = new Option( name, id );
	    
    dataElementSelector.add( option, null );
  }
}


function onloadFunction()
{ 	
  htmlCode = FCK.GetXHTML( FCKConfig.FormatSource );
  findDataElementCount();
} 
// -----------------------------------------------------------------------------
// Auto-save DataEntryForm
// -----------------------------------------------------------------------------

function autoSaveDataEntryFormValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {  
     autoSaveDataEntryForm();
  }
  else if ( type == 'input' )
  {
	 setMessage( message );
  }
  else if ( type == 'mismatch' )
  {
    var result = window.confirm( message );

    if ( result )
    {
      autoSaveDataEntryForm();
    }
  }
}

function autoSaveDataEntryForm(){
	
	var field = FCKeditorAPI.GetInstance('designTextarea');
    
	var designTextarea = htmlEncode(field.GetHTML(true));

	var request = new Request();
	request.setResponseTypeXML( 'dataSet' );
	request.setCallbackSuccess( function (xmlObject){setMessage(i18n_save_success);} );
	  
	var params = 'nameField=' + getFieldValue('nameField');
		params += '&designTextarea=' + designTextarea;
		params += '&dataSetIdField=' + getFieldValue('dataSetIdField');
		
	if(byId('dataEntryFormId') != null){
		params += '&dataEntryFormId=' + getFieldValue('dataEntryFormId');
	}
	
	request.sendAsPost(params);
	request.send('autoSaveDataEntryForm.action');
	
}