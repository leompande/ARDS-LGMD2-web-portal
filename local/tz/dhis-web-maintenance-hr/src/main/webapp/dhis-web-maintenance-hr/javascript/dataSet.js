// -----------------------------------------------------------------------------
// DataSet details form
// -----------------------------------------------------------------------------

function showDataSetDetails( dataSetId )
{
  var request = new Request();
  request.setResponseTypeXML( 'dataSet' );
  request.setCallbackSuccess( dataSetRecieved );
  request.send( 'getHrDataSet.action?hrDataSetId=' + dataSetId );
}

function dataSetRecieved( dataSetElement )
{
	setInnerHTML( 'idField', getElementValue( dataSetElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( dataSetElement, 'name' ) );

	setInnerHTML( 'descriptionField', getElementValue( dataSetElement, 'description' ) );

	setInnerHTML( 'attributesCountField', getElementValue( dataSetElement, 'attributesCount' ) );

	setInnerHTML( 'dataEntryFormField', getElementValue( dataSetElement, 'dataentryform' ) );

  showDetails();
}

// -----------------------------------------------------------------------------
// Delete DataSet
// -----------------------------------------------------------------------------

function removeDataSet( dataSetId, name )
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
	
 	var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removeDatasetCompleted );     
     window.location.href = 'removeHrDataSet.action?id=' + dataSetId;
 }
}

function removeDatasetCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
     window.location.href = 'hrDataSet.action';
 }
 else if ( type = 'error' )
 {
     setFieldValue( 'warningField', message );
     
     showWarning();
 }
}
// ----------------------------------------------------------------------
// DataEntryForm
// ----------------------------------------------------------------------

function viewDataEntryForm( dataSetId )
{
	window.location.href = 'viewDataEntryForm.action?dataSetId=' + dataSetId;
}

// ----------------------------------------------------------------------
// Filter by DataElementGroup and PeriodType
// ----------------------------------------------------------------------

function filterByDataElementGroup( selectedDataElementGroup )
{
  var request = new Request();
  
  var requestString = 'filterAvailableDataElementsByDataElementGroup.action';
  
  var params = 'dataElementGroupId=' + selectedDataElementGroup;
	
  var selectedList = document.getElementById( 'selectedList' );

  for ( var i = 0; i < selectedList.options.length; ++i)
  {
  	params += '&selectedDataElements=' + selectedList.options[i].value;
  }

  // Clear the list
  var availableList = document.getElementById( 'availableList' );

  availableList.options.length = 0;

  request.setResponseTypeXML( 'dataElementGroup' );
  request.setCallbackSuccess( filterByDataElementGroupCompleted );
  request.sendAsPost( params );
  request.send( requestString );
}

function filterByDataElementGroupCompleted( dataElementGroup )
{
  var dataElements = dataElementGroup.getElementsByTagName( 'dataElements' )[0];
  var dataElementList = dataElements.getElementsByTagName( 'dataElement' );

  var availableList = document.getElementById( 'availableList' );
  
  for ( var i = 0; i < dataElementList.length; i++ )
  {
    var dataElement = dataElementList[i];
    var name = dataElement.firstChild.nodeValue;
    var id = dataElement.getAttribute( 'id' );

    availableList.add( new Option( name, id ), null );
  }
}

// ----------------------------------------------------------------------
// Add Dataset
// ----------------------------------------------------------------------

function validateAddDataSet()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( addDataSetValidationCompleted ); 
  
  var params = 'nameField=' + getFieldValue( 'nameField' );
  request.sendAsPost( params );
  request.send( 'validateHrDataSet.action' );
  return false;
}

function addDataSetValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
	  	 selectAllById('selectedAttributes');
	     var form = document.getElementById( 'addHrDataSetForm' );        
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

//----------------------------------------------------------------------
//Update Dataset
//----------------------------------------------------------------------

function validateUpdateDataSet()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( UpdateDataSetValidationCompleted );

  var params  = 'id=' + getFieldValue( 'id' );
	  params += '&nameField=' + getFieldValue( 'nameField' );	
  request.sendAsPost( params );
  request.send( 'validateHrDataSet.action' );
  return false;
}

function UpdateDataSetValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
  	 selectAllById('selectedAttributes');
     var form = document.getElementById( 'updateHrDataSetForm' );        
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