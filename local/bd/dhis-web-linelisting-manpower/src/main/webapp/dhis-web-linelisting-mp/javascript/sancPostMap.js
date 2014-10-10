var selPostId = "";

function getPosts( )
{
	var linelistgroup = document.getElementById('lineListGroupId');
	var selLineListGroupId = linelistgroup.options[ linelistgroup.selectedIndex ].value;
	
	var postList = document.getElementById( "lineListOptId" );
	clearList( postList );
	addOption( 'lineListOptId', '[Select]', 'NA' );
	
	var deList = document.getElementById( "deId" );
	deList.options[0].selected = true;
	
	if( selLineListGroupId == 'NA' )
	{
		return;
	}
	
	jQuery.postJSON("getPosts.action",{
  	  lineListGroupId : selLineListGroupId
   }, function( json ){
	   for ( var i=0; i<json.linelistOptions.length; i++ )
	      {
	        var id = json.linelistOptions[i].id;
	        var name = json.linelistOptions[i].name;
	        selPostId = json.linelistOptions[i].llelement;
	        
	        addOption( 'lineListOptId', name, id );
	      }
   });
}


function getSelDataElement( )
{
	var postList = document.getElementById( "lineListOptId" );
	var selOptionId = postList.options[ postList.selectedIndex ].value;
	var deList = document.getElementById( "deId" );

	deList.options[0].selected = true;

	if( selOptionId == 'NA' )
	{
		return;
	}
	
	jQuery.postJSON("getPostDataElement.action",{
		lineListElementId : selPostId, 
		lineListOptionId : selOptionId
   }, function( json ){
	   		var id = json.selde[0].id; 
	   		for( var i=0; i<deList.options.length; i++)
	   		{
	   			if( deList.options[i].value == id )
	   			{
	   				deList.options[i].selected = true;
	   			}	
	   		}
   });
}


function saveSanctionedPostMapping()
{
	var postList = document.getElementById( "lineListOptId" );
	var selOptionId = postList.options[ postList.selectedIndex ].value;
	var deList = document.getElementById( "deId" );
	var selDeId = deList.options[ deList.selectedIndex ].value;
	var deArray = selDeId.split(":");
	
	if( selOptionId == 'NA' || selDeId == 'NA')
	{
		alert("Please select Post and corresponding dataelement");
		return;
	}
	
	jQuery.postJSON("saveSanctionedPostMapping.action",{
		lineListElementId : selPostId, 
		lineListOptionId : selOptionId,
		dataElementId : deArray[0],
		deCOCId : deArray[1]
   }, function( json ){
	   var statusMsg = json.saveMap[0].statusMsg; 
  		alert( statusMsg );
   });
}
