
function clearFolder( folderId )
{
	/*
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( clearFolderRecieved );
	  
	var requestString = "clearFolder.action";
	var params = 'selectedButton=' + folderId;	
	
	request.sendAsPost( params );
	request.send( requestString );
	*/
	$.post("clearFolder.action",
			{
				selectedButton : folderId
			},
			function (data)
			{
				clearFolderRecieved(data);
			},'xml');
	
}

function clearFolderRecieved( messageElement )
{
	var message = messageElement.firstChild.nodeValue;

    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
}


function downloadFolder( folderId )
{
	window.location.href="clearFolder.action?selectedButton="+folderId;
}