function removeUserGroup( userGroupId, userGroupName )
{
    removeItem( userGroupId, userGroupName, i18n_confirm_delete, "removeUserGroup.action" );
}

function showUserGroupDetails( userGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'userGroup' );
    request.setCallbackSuccess( userGroupReceived );
    request.send( 'getUserGroup.action?userGroupId=' + userGroupId );
}

function userGroupReceived( userGroupElement )
{
    setInnerHTML( 'nameField', getElementValue( userGroupElement, 'name' ) );
    setInnerHTML( 'idField', getElementValue( userGroupElement, 'id' ) );
    setInnerHTML( 'noOfGroupField', getElementValue( userGroupElement, 'noOfUsers' ) );

    showDetails();
}

function addValidation ()
{
    var k =0 ;
	var xyz=document.getElementById("name");
	var abc =document.getElementById("groupMembers");
	var c  = abc.length ;
	if( xyz.value == "") {
    alert ("please select name ");
    return false;
    }
	else if ( c <=0 ){
    alert("please select at least one member");
    return false ;
    }else
	if (c >0 )
	{
		for( k =0;k<=c;k++)
		{
		abc.options[k].selected = true ;	
			
		}
	}
	
	return true;
}

function editValidation ()
{
alert("dev");
}


