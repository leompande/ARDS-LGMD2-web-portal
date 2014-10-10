
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showLineListGroupDetails( lineListGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'getLineListGroups' );
    request.setCallbackSuccess( lineListGroupReceived );
    request.send( 'getLineListGroup.action?id=' + lineListGroupId );
}

function lineListGroupReceived( lineListGroupElement )
{
   /*
	setFieldValue( 'nameField', getElementValue( lineListGroupElement, 'name' ) );
    setFieldValue( 'shortNameField', getElementValue( lineListGroupElement, 'shortName' ) );
    setFieldValue( 'periodTypeField', getElementValue( lineListGroupElement, 'periodType' ) );
    setFieldValue( 'descriptionField', getElementValue( lineListGroupElement, 'description' ) );
    setFieldValue( 'memberCountField', getElementValue( lineListGroupElement, 'memberCount' ) );
	*/
	
	byId('idField').innerHTML = lineListGroupElement.getElementsByTagName( 'id' )[0].firstChild.nodeValue;

	byId('nameField').innerHTML = lineListGroupElement.getElementsByTagName( 'name' )[0].firstChild.nodeValue;

	byId('shortNameField').innerHTML = lineListGroupElement.getElementsByTagName( 'shortName' )[0].firstChild.nodeValue;

	byId('periodTypeField').innerHTML = lineListGroupElement.getElementsByTagName( 'periodType' )[0].firstChild.nodeValue;

	//byId('descriptionField').innerHTML = lineListGroupElement.getElementsByTagName( 'description' )[0].firstChild.nodeValue;
	
	var description = getElementValue( lineListGroupElement, 'description' );
	setInnerHTML( 'descriptionField', description ? description : '[' + none + ']' );

	byId('memberCountField').innerHTML = lineListGroupElement.getElementsByTagName( 'memberCount' )[0].firstChild.nodeValue;

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove data element group
// -----------------------------------------------------------------------------

function removeLineListGroup( lineListGroupId, lineListGroupName )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + lineListGroupName );
    
    if ( result )
    {
        window.location.href = 'removeLineListGroup.action?id=' + lineListGroupId;
    }
}

// -----------------------------------------------------------------------------
// Add data element group
// -----------------------------------------------------------------------------

function validateAddLineListGroup()
{
    var selectedListOption	=	document.getElementById( 'selectedList' );
    var selectedListNumber	=	selectedListOption.options.length;
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    request.send( 'validateLineListGroupAdd.action?name=' + getFieldValue( 'name' ) + 
        '&shortName=' + htmlEncode( getFieldValue( 'shortName' ) ) +
        '&periodTypeSelect=' + htmlEncode( getFieldValue( 'periodTypeSelect' ) ) +  '&selectedListNumber=' + selectedListNumber );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;

    if ( type == 'success' )
    {
        var availableList = document.getElementById( 'availableList' );
        availableList.selectedIndex = -1;
        
        selectAllById( 'selectedList' );
        
        document.getElementById( 'addLineListGroupElementForm' ).submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_data_element_group_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        setMessage( message );
    }
}

// -----------------------------------------------------------------------------
// Update data element group
// -----------------------------------------------------------------------------

function validateUpdateLineListGroup()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    selectAllById( 'selectedList' );
    var selectedList = "";
    for ( var id in groupMembers )
    {
        selectedList = selectedList +","+ id ;
    }
    
    request.send( 'validateLineListGroup.action?id=' + getFieldValue( 'id' ) +
        '&name=' + getFieldValue( 'name' ) +
        '&shortName=' + getFieldValue( 'shortName' ) +
        '&selectedList=' + selectedList );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {

        var availableList = document.getElementById( 'availableList' );
        availableList.selectedIndex = -1;
        
        selectAllById( 'selectedList' );
        document.getElementById( 'showUpdateLineListGroupForm' ).submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_data_element_group_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        setMessage( message );
    }
}

// -----------------------------------------------------------------------------
// Select lists
// -----------------------------------------------------------------------------

function initLists()
{
    var list = document.getElementById( 'selectedList' );
    var id;
    var lastid=0;
    for ( id in groupMembers )
    {
        list.add( new Option( groupMembers[id], id ), null );
        lastid++;
    }
   // alert("lastid = "+lastid);
    list = document.getElementById( 'availableList' );

    for ( id in availableLineLists )
    {
        list.add( new Option( availableLineLists[id], id ), null );
    }
}

function filterGroupMembers()
{
    var filter = document.getElementById( 'groupMembersFilter' ).value;
    var list = document.getElementById( 'selectedList' );
    
    list.options.length = 0;
    
    for ( var id in groupMembers )
    {
        var value = groupMembers[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterAvailableLineListElements()
{
    var filter = document.getElementById( 'availableLineListElementsFilter' ).value;
    var list = document.getElementById( 'availableList' );
    list.options.length = 0;
    
    for ( var id in availableLineLists)
    {
        var value = availableLineLists[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function addGroupMembers()
{
    var list = document.getElementById( 'availableList' );
    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        groupMembers[id] = availableLineLists[id];
        
        delete availableLineLists[id];
    }
    
    filterGroupMembers();
    filterAvailableLineListElements();
}

function removeGroupMembers()
{
    var list = document.getElementById( 'selectedList' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableLineLists[id] = groupMembers[id];
        
        delete groupMembers[id];
    }
    
    filterGroupMembers();
    filterAvailableLineListElements();
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
