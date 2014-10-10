
var numberOfSelects = 0;

function selectAllAtLevel()
{
	var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'selectLevel.action?level=' + getListValue( 'levelList' ) );
}

function unselectAllAtLevel()
{
	var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'unselectLevel.action?level=' + getListValue( 'levelList' ) );
}

function selectGroup()
{
    var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'selectOrganisationUnitGroup.action?organisationUnitGroupId=' + getListValue( 'groupList' ) );
}

function unselectGroup()
{
    var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'unselectOrganisationUnitGroup.action?organisationUnitGroupId=' + getListValue( 'groupList' ) );
}

function unselectAll()
{
    var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'unselectAll.action' );
}

function selectReceived()
{
    selectionTree.buildSelectionTree();
}

function treeClicked()
{
    numberOfSelects++;
    
    setMessage( i18n_loading );
    
    document.getElementById( "submitButton" ).disabled = true;
}

function selectCompleted( selectedUnits )
{
    numberOfSelects--;
    
    if ( numberOfSelects <= 0 )
    {
        hideMessage();
        
        document.getElementById( "submitButton" ).disabled = false;
    }
}
