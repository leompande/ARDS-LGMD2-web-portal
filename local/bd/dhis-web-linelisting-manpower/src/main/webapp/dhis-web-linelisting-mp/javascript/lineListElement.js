
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showLineListElementDetails( lineListElementId )
{
    var request = new Request();
    request.setResponseTypeXML( 'getLineListElements' );
    request.setCallbackSuccess( lineListElementReceived );
    request.send( 'getLineListElement.action?lineListElementId=' + lineListElementId );
}

function lineListElementReceived( lineListElementElement )
{
/* 
  setFieldValue( 'idField', getElementValue( lineListElementElement, 'id' ) );

  setFieldValue( 'nameField', getElementValue( lineListElementElement, 'name' ) );

  setFieldValue( 'shortNameField', getElementValue( lineListElementElement, 'shortName' ) );

  setFieldValue( 'dataTypeField', getElementValue( lineListElementElement, 'dataType' ) );

  setFieldValue( 'presentationTypeField', getElementValue( lineListElementElement, 'presentationType' ) );

  setFieldValue( 'memberCountField', getElementValue( lineListElementElement, 'memberCount' ) );
*/
	setInnerHTML( 'idField', getElementValue( lineListElementElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( lineListElementElement, 'name' ) );
	setInnerHTML( 'shortNameField', getElementValue( lineListElementElement, 'shortName' ) );
	
	// description may be null
	var description = getElementValue( lineListElementElement, 'description' );
	setInnerHTML( 'descriptionField', description ? description : '[' + none + ']' );
	
	setInnerHTML( 'dataTypeField', getElementValue( lineListElementElement, 'dataType' ) );
	setInnerHTML( 'presentationTypeField', getElementValue( lineListElementElement, 'presentationType' ) );
	setInnerHTML( 'memberCountField', getElementValue( lineListElementElement, 'memberCount' ) );
	
  showDetails();
}

// -----------------------------------------------------------------------------
// Delete line list element
// -----------------------------------------------------------------------------

function removeLineListElement( lineListElementId, lineListElementName )
{
    //var result = window.confirm( i18n_confirm_delete + '\n\n' + lineListElementName );
    var result = window.confirm( i18n_confirm_delete + '\n\nLine List Element Id: ' + lineListElementId + '\n\n Line List Element Name: ' + lineListElementName );
    
    if ( result )
    {
        var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeLineListElementCompleted );
        request.send( 'removeLineListElement.action?lineListElementId=' + lineListElementId);
    }
}

function removeLineListElementCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;

    if ( type == 'success' )
    {
        window.location.href = 'lineListElements.action';
    }
    else if ( type == 'error' )
    {
        setFieldValue( 'warningField', message );

        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Validate add line list element
// -----------------------------------------------------------------------------

function validateAddLineListElement()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( addLineListElementValidationCompleted ); 
  
  var requestString = 'validateLineListElement.action?name=' + document.getElementById( 'name' ).value +
                      '&shortName=' + document.getElementById( 'shortName' ).value +
                      '&dataType=' + document.getElementById( 'dataType' ).value +
                      '&presentationType=' + document.getElementById( 'presentationType' ).value;

  request.send( requestString );

  return false;
}

function addLineListElementValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
      var availableList = document.getElementById( 'availableList' );
      availableList.selectedIndex = -1;
        
	  selectAllById( 'selectedList' );
        
      document.getElementById('addLineListElementForm').submit();
  }

  else if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
}

// -----------------------------------------------------------------------------
// Validate update Line List Element
// -----------------------------------------------------------------------------

function validateUpdateLineListElement()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( editLineListElementValidationCompleted );

  request.send( 'validateLineListElement.action?lineListElementId=' + getFieldValue ( 'lineListElementId' ) +
  					  '&name=' + getFieldValue( 'name' ) +
                      '&shortName=' + getFieldValue( 'shortName' ) +
                      '&dataType=' + getFieldValue( 'dataType' ) +
                      '&presentationType=' + getFieldValue( 'presentationType' ) );

  return false;
}

function editLineListElementValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
      var availableList = document.getElementById( 'availableList' );
      availableList.selectedIndex = -1;

      selectAllById( 'selectedList' );
      document.getElementById('editLineListElementForm').submit();
  }
  else if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
}

// -----------------------------------------------------------------------------
// Select lists
// -----------------------------------------------------------------------------

function initListsLineListElement()
{
    var list = document.getElementById( 'selectedList' );
    var id;

    for ( id in lineListElementOptions )
    {
        list.add( new Option( lineListElementOptions[id], id ), null );
    }

    list = document.getElementById( 'availableList' );

    for ( id in availableLineListOptions )
    {
        list.add( new Option( availableLineListOptions[id], id ), null );
    }
}

function filterLineListElementOptions()
{
    var filter = document.getElementById( 'lineListElementOptionsFilter' ).value;
    var list = document.getElementById( 'selectedList' );
    
    list.options.length = 0;
    
    for ( var id in lineListElementOptions )
    {
        var value = lineListElementOptions[id];
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterAvailableLineListOptions()
{
    var filter = document.getElementById( 'availableLineListOptionsFilter' ).value;
    var list = document.getElementById( 'availableList' );
    list.options.length = 0;
    
    for ( var id in availableLineListOptions)
    {
        var value = availableLineListOptions[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function addLineListElementOptions()
{
    var list = document.getElementById( 'availableList' );
    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        lineListElementOptions[id] = availableLineListOptions[id];
        
        delete availableLineListOptions[id];
    }
    
    filterLineListElementOptions();
    filterAvailableLineListOptions();
}

function removeLineListElementOptions()
{
    var list = document.getElementById( 'selectedList' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableLineListOptions[id] = lineListElementOptions[id];
        
        delete lineListElementOptions[id];
    }
    
    filterLineListElementOptions();
    filterAvailableLineListOptions();
}

function moveUp( elementId )
{

	var withInList = document.getElementById( elementId );

	var index = withInList.selectedIndex;

	if ( index == -1 ) { return; }

	if( index - 1 < 0 ) { return; }//window.alert( 'Item cant be moved up');

	var option = new Option( withInList.options[index].text, withInList.options[index].value);
	var temp = new Option( withInList.options[index-1].text, withInList.options[index-1].value);

	withInList.options[index-1] = option;
	withInList.options[index-1].selected = true;
	withInList.options[index] = temp;

}

function moveDown( elementId )
{
	var withInList = document.getElementById( elementId );

	var index = withInList.selectedIndex;

	if ( index == -1 ) { return; }

	if( index + 1 == withInList.options.length ) { return; }//window.alert( 'Item cant be moved down');

	var option = new Option( withInList.options[index].text, withInList.options[index].value);
	var temp = new Option( withInList.options[index+1].text, withInList.options[index+1].value);

	withInList.options[index+1] = option;
	withInList.options[index+1].selected = true;
	withInList.options[index] = temp;

}