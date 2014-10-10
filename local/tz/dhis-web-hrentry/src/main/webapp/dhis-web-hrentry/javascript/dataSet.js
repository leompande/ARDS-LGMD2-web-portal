// -----------------------------------------------------------------------------
// DataSet details form
// -----------------------------------------------------------------------------

function showDataSetDetails( dataSetId )
{
  var request = new Request();
  request.setResponseTypeXML( 'dataSet' );
  request.setCallbackSuccess( dataSetRecieved );
  request.send( 'getDataSet.action?dataSetId=' + dataSetId );
}

function dataSetRecieved( dataSetElement )
{
  setFieldValue( 'idField', getElementValue( dataSetElement, 'id' ) );
  setFieldValue( 'nameField', getElementValue( dataSetElement, 'name' ) );

  setFieldValue( 'frequencyField', getElementValue( dataSetElement, 'frequency' ) );

  setFieldValue( 'dataElementCountField', getElementValue( dataSetElement, 'dataElementCount' ) );

  setFieldValue( 'dataEntryFormField', getElementValue( dataSetElement, 'dataentryform' ) );

  showDetails();
}

// -----------------------------------------------------------------------------
// Delete DataSet
// -----------------------------------------------------------------------------

var tmpDataSetId;

var tmpSource;

function removeDataSet( dataSetId, dataSetName )
{
  removeItem( dataSetId, dataSetName, i18n_confirm_delete, 'delDataSet.action' );
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
// Validation
// ----------------------------------------------------------------------

function validateAddDataSet()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( addDataSetValidationCompleted ); 
  
  var params = 'name=' + getFieldValue( 'name' ) +
               '&shortName=' + getFieldValue( 'shortName' ) +
               '&code=' + getFieldValue( 'code' );
  request.sendAsPost( params );
  request.send( 'validateDataSet.action' );
  return false;
}

function addDataSetValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
      // Both edit and add form has id='dataSetForm'
	  selectAllById('selectedList');
	  
      document.forms['addDataSetForm'].submit();
  }
  /**
  else if ( type == 'error' )
  {
      window.alert( 'Adding the organisation unit failed with the following message:\n' + message );
  }
  */
  else if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
}

function validateEditDataSet()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( editDataSetValidationCompleted );

  var params = 'name=' + getFieldValue( 'name' ) +
               '&shortName=' + getFieldValue( 'shortName' ) +
               '&code=' + getFieldValue( 'code' ) +
  		       '&dataSetId=' + getFieldValue( 'dataSetId' );
  request.sendAsPost( params );
  request.send( 'validateDataSet.action' );
  return false;
}

function editDataSetValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
      // Both edit and add form has id='dataSetForm'
	  selectAllById('selectedList');

      document.forms['editDataSetForm'].submit();
  }
  /**
  else if ( type == 'error' )
  {
      window.alert( 'Adding the organisation unit failed with the following message:\n' + message );
  }
  */
  else if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
}
