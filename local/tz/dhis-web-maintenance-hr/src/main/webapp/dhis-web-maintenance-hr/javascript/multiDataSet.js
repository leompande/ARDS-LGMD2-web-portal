
// ----------------------------------------------------------------------
// List
// ----------------------------------------------------------------------

function initLists()
{
    var id;

	for ( id in selectedDataSets )
    {
        $("#selectedDataSets").append( $( "<option></option>" ).attr( "value",id ).text( selectedDataSets[id] )) ;
    }

    for ( id in availableDataSets )
    {
        $("#availableDataSets").append( $( "<option></option>" ).attr( "value",id ).text( availableDataSets[id] )) ;
    }
}

function openDiv( divId )
{
	setPositionCenter( divId );
	showById( divId );
	showDivEffect();
}

function closeDiv( divId )
{
	hideById( divId );
	deleteDivEffect();
}

/**
	If the user enter without pressing the OK button
*/
function checkKeyCodeAndValidateRenameField( event )
{
	if ( event.keyCode == 13 )
	{
		validateRenameDataSetEditor();
	}
}

/**
	EDITOR - Show the added new dataset form
*/
function showAddDataSetEditorForm()
{
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getPeriodTypesEditorReceived );
	request.send( "periodTypeListEditor.action" );
	
	openDiv( "addDataSetEditorDiv" );
}

function getPeriodTypesEditorReceived( xmlObject )
{
	clearListById( "frequencySelect" );
	var frequencySelectList = byId( "frequencySelect" );
	var periodTypes = xmlObject.getElementsByTagName('periodType');
	
	for ( var i = 0 ; i < periodTypes.length ; i++ )
	{
		var id = getElementValue( periodTypes.item(i), 'id' );
		var name = getElementValue( periodTypes.item(i), 'name' );
		
		addOptionToList( frequencySelectList, id, name );
	}
	
}

// ----------------------------------------------------------------------
// Validation add new DataSet
// ----------------------------------------------------------------------

function validateAddDataSetEditor()
{
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( addDataSetValidationEditorCompleted ); 

	var requestString = 'validateDataSet.action?name=' + getFieldValue( 'name' ) +
						'&shortName=' + getFieldValue( 'shortName' ) +
						'&code=' + getFieldValue( 'code' );
						
	request.send( requestString );

	return false;
}

function addDataSetValidationEditorCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;

	if ( type == 'success' )
	{
		// Both edit and add form has id='dataSetForm'
		document.forms['addDataSetEditorForm'].submit();
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

/**
	EDITOR - Show the renamed form
*/
function showRenameDataSetEditorForm()
{
	var availableList = byId( "availableDataSets" );
	
	try
	{
		var name = availableList.options[ availableList.selectedIndex ].text;
		byId( 'dateSetNameField' ).value = name;
		byId( 'addRenameDataSetButton' ).onclick = validateRenameDataSetEditor;
		openDiv( 'renameDataSetEditorDiv' );
	}
	catch(e)
	{
		alert( i18n_dataset_unselected );
	}
}

/**
	EDITOR - Validate renaming dataset
*/
function validateRenameDataSetEditor()
{
	var availableList = byId( "availableDataSets" );
	var id = availableList.options[ availableList.selectedIndex ].value;
	var name = byId( 'dateSetNameField' ).value;
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( validateRenameDataSetEditorCompleted );
	request.send( "validateRenameDataSetEditor.action?dataSetId=" + id + "&name=" + name );
}

function validateRenameDataSetEditorCompleted( xmlObject )
{
	var type = xmlObject.getAttribute( 'type' );
	
	if ( type == "input" )
	{
		alert( xmlObject.firstChild.nodeValue );
	}
	else
	{
		renameDataSetEditor();
	}
}

/**
	EDITOR - Rename dataset
*/
function renameDataSetEditor()
{
	var availableList = byId( "availableDataSets" );
	var id = availableList.options[ availableList.selectedIndex ].value;
	var name = byId( 'dateSetNameField' ).value;

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( renameDataSetEditorCompleted );
	request.send( "renameDataSetEditor.action?dataSetId=" + id + "&name=" + name );
}

function renameDataSetEditorCompleted( xmlObject )
{
	var type = xmlObject.getAttribute( 'type' );
	
	if ( type == "success" )
	{
		var list = byId( "availableDataSets" );
		list.options[ list.selectedIndex ].text = xmlObject.firstChild.nodeValue;
		
		setMessage( i18n_rename_success );
	}
	else
	{
		setMessage( xmlObject.firstChild.nodeValue );
	}
	
	closeDiv( "renameDataSetEditorDiv" );
}

/**
	EDITOR - Delete dataset
*/
function deleteDataSetEditor()
{
	var availableList = byId( "availableDataSets" );
	
	try
	{
		var id = availableList.options[ availableList.selectedIndex ].value;
		var name = availableList.options[ availableList.selectedIndex ].text;
		
		if ( window.confirm( i18n_confirm_delete + "\n\n[ " + name + " ]") )
		{
			var request = new Request();
			request.setResponseTypeXML( 'xmlObject' );
			request.setCallbackSuccess( deleteDataSetEditorCompleted );
			request.send( "deleteDataSetEditor.action?id=" + id );
		}
	}
	catch(e)
	{
		alert( i18n_dataset_unselected );
	}
}

function deleteDataSetEditorCompleted( xmlObject )
{
	var type = xmlObject.getAttribute( 'type' );
	
	if ( type == "success" )
	{
		var availableList = byId( "availableDataSets" );
		var message = xmlObject.firstChild.nodeValue;
		
		availableList.remove( availableList.selectedIndex );
		setMessage( message );
	}
	else if ( type == "error" )
	{
		setFieldValue( 'warningArea', xmlObject.firstChild.nodeValue );

		showWarning();
	}
}

