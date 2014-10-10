
// -----------------------------------------------------------------------------
// Delete Survey
// -----------------------------------------------------------------------------

//var tmpSurveyId;

//var tmpSource;

function removeDeTarget( deTargetId, deTargetName )
{
  //var result = window.confirm( i18n_confirm_delete + '\n\n' + " DeTarget Id =" + deTargetId + '\n\n' + " DeTarget Name ="  + deTargetName );
  var result = window.confirm( i18n_confirm_delete + '\n\n' + " DeTarget Name ="  + deTargetName );

  if ( result )
  {
	$.post("delDeTarget.action",
		{
		deTargetId : deTargetId
		},
		function (data)
		{
			removeDeTargetCompleted(data);
		},'xml');
		
  }
}

function removeDeTargetCompleted( messageElement )
{
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
  
    if ( type == 'error' )
   {
    	var message = messageElement.firstChild.nodeValue;
    	//alert( message );
		//setFieldValue( 'warningField', message );
		setInnerHTML( 'warningField', message );
		showWarning();
   }
   else
   {
		window.location.href = 'deTargetManagement.action';
   }
}
//validation for adding New DeTarget
function validateAddDeTarget()
{
	$.post("validateDeTarget.action",
		{
			name :  byId( 'name' ).value,
			shortName : byId( 'shortName' ).value
		},
		function (data)
		{
			addDeTargetValidationCompleted(data);
		},'xml');
		
  return false;
}

function addDeTargetValidationCompleted( messageElement )
{
  	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	var message = messageElement.firstChild.nodeValue;

	if ( type == 'success' )
	{
		var selectedList = document.getElementById( 'selectedList' );
		for(var k=0;k<selectedList.length;k++)
		{
			selectedList.options[k].selected = "true";
		}  
		document.forms['addDeTargetForm'].submit();
	} 
    else if ( type == 'input' )
    {
		setMessage( message );
	}
}


function validateEditDeTarget()
{
	//alert("inside update");
	$.post("validateDeTarget.action",
		{
			name :  byId( 'name' ).value,
			shortName : byId( 'shortName' ).value,
			url : byId( 'url' ).value,
			deTargetId : byId( 'deTargetId' ).value
		},
		function (data)
		{
			editDeTargetValidationCompleted(data);
		},'xml');

  return false;
}
function editDeTargetValidationCompleted( messageElement )
{
	//alert("inside update result");
  	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	var message = messageElement.firstChild.nodeValue;

	if ( type == 'success' )
	{
		var selectedList = document.getElementById( 'selectedList' );
		for(var k=0;k<selectedList.length;k++)
		{
			selectedList.options[k].selected = "true";
		} 
		  
		document.forms['editDeTargetForm'].submit();
	}
	else if ( type == 'input' )
	{
		setMessage(message);
	}
}

// ----------------------------------------------------------------------
// List
// ----------------------------------------------------------------------
/*
function initLists()
{
    var id;
	
	var list = document.getElementById( 'selectedList' );
	
    for ( id in deTargetMembers )
    {
        list.add( new Option( deTargetMembers[id], id ), null );
    }	
	
    list = document.getElementById( 'availableList' );
    
    for ( id in availableDataElements )
    {
        list.add( new Option( availableDataElements[id], id ), null );
    }
}
*/
// complette

/*
function filterDeTargetMembers()
{
	var filter = document.getElementById( 'deTargetMembersFilter' ).value;
    var list = document.getElementById( 'selectedList' );
    var deTargetMembers = document.getElementById( 'selectedList' ).value;
    list.options.length = 0;
    
    for ( var id in deTargetMembers )
    {
        var value = deTargetMembers[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}
*/
//complette
/*
function filterAvailableDataElements()
{
	var filter = document.getElementById( 'availableDataElementFilter' ).value;
    var list = document.getElementById( 'availableList' );
    var availableDataElements = document.getElementById( 'availableList' ).value;
    
    list.options.length = 0;
    
    for ( var id in availableDataElements )
    {
        var value = availableDataElements[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}
*//*
function addDeTargetMembers()
{
	var list = document.getElementById( 'availableList' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        deTargetMembers[id] = availableDataElements[id];
        
        delete availableDataElements[id];        
    }
    
    filterDeTargetMembers();
    filterAvailableDataElements();
}*/
/*
function removeDeTargetMembers()
{
	var list = document.getElementById( 'selectedList' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableIndicators[id] = surveyMembers[id];
        
        delete surveyMembers[id];        
    }
    
    filterSurveyMembers();
    filterAvailableIndicators();
}
*/
function filterByDataElementGroup( selectedDataElementGroup )
{
  var selectedList = document.getElementById( 'selectedList' );
  
  var availableList = document.getElementById( 'availableList' );

 // var list = new Array();
  
  var params = 'dataElementGroupId=' + selectedDataElementGroup;
  
  
  if ( selectedDataElementGroup == "" || selectedDataElementGroup == "ALL" )
  {
	  alert( "Please Select DataElement Group" );
	  clearList(availableList);
	  return false;
  }
  
  else
  {
  for ( var i = 0; i < selectedList.options.length; ++i)
  {
  	//params += '&selectedIndicators=' + selectedList.options[i].value;
  	params += '&selectedDataElements=' + selectedList.options[i].value;
	//list[i] = selectedList.options[i].value;
  }
  // Clear the list
  var availableList = document.getElementById( 'availableList' );

  availableList.options.length = 0;
  
 // alert(list);
  //alert(list.length);
  
  /*
  
  var request = new Request();
  request.setResponseTypeXML( 'indicatorgroup' );
  request.setCallbackSuccess( filterByDataElementGroupCompleted );
  //request.send( url );

  var requestString = "filterAvailableDataElementsByDataElementGroup.action";
 // var params = "indicatorGroupId=" + selectedIndicatorGroup + "&selectedIndicators=" + list;
  request.sendAsPost( params );
  request.send( requestString ); 
  } 
 */
	$.post("filterAvailableDataElementsByDataElementGroup.action",
			{
				dataElementGroupId : selectedDataElementGroup
			},
			function (data)
			{
				filterByDataElementGroupCompleted(data);
			},'xml');
	
  /* 
  
   $.post("filterAvailableIndicatorsByIndicatorGroup.action",
		{
			indicatorGroupId : selectedIndicatorGroup,
			selectedIndicators : list
		},
		function (data)
		{
			filterByIndicatorGroupCompleted(data);
		},'xml');
		*/
  }
  
}
function filterByDataElementGroupCompleted( xmlObject )
{
	//var indicators = indicatorGroup.getElementsByTagName( 'indicators' )[0];
	// var indicatorList = indicators.getElementsByTagName( 'indicator' );

  var availableList = document.getElementById( 'availableList' );
  var selectedList = document.getElementById( 'selectedList' );
  
 // var availableDataElements = document.getElementById("availableDataElements");
  //var selectedDataElements = document.getElementById("selectedServices");

  //clearList(availableList);

  var dataElements = xmlObject.getElementsByTagName("dataElement");
  //alert( "DataElement Group Received lent of Group member " + dataElements.length );
  for ( var i = 0; i < dataElements.length; i++ )
  {
      var id = dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
      var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
      if ( listContains(selectedList, id) == false )
      {
      var option = document.createElement("option");
      option.value = id;
      option.text = dataElementName;
      option.title = dataElementName;
      availableList.add( option, null );
      }
 
  }
}

function showDeTargetDetails( deTargetId )
{
	$.get("getDeTargetDetails.action",
		{
			deTargetId : deTargetId
		},
		function (data)
		{
			deTargetRecieved(data);
		},'xml');
}

function deTargetRecieved( deTargetElement )
{
 
  setInnerHTML( 'idField', getElementValue( deTargetElement, 'id' ) );
  setInnerHTML( 'nameField', getElementValue( deTargetElement, 'name' ) );
  setInnerHTML( 'dataElementSizeField', getElementValue( deTargetElement, 'dataElementSize' ) );
  
  var desCription = getElementValue( deTargetElement, 'description' );
  setInnerHTML( 'descriptionField', desCription ? desCription : '[' + i18n_none + ']' );
  
  var url = getElementValue( deTargetElement, 'url' );
  setInnerHTML( 'urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + i18n_none + ']' );
  
   showDetails();
}

