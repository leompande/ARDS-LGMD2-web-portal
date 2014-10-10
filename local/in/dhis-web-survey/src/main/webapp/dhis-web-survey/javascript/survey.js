
// -----------------------------------------------------------------------------
// Delete Survey
// -----------------------------------------------------------------------------

var tmpSurveyId;

var tmpSource;

function removeSurvey( surveyId, surveyName )
{
  //var result = window.confirm( i18n_confirm_delete + '\n\n' + " Survey Id =" + surveyId + '\n\n' + " Survey Name ="  + surveyName );
  var result = window.confirm( i18n_confirm_delete + '\n\n' + " Survey Name ="  + surveyName );

  if ( result )
  {
	$.post("delSurvey.action",
		{
			surveyId : surveyId
		},
		function (data)
		{
			removeSurveyCompleted(data);
		},'xml');
		
  }
}

function removeSurveyCompleted( messageElement )
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
		window.location.href = 'index.action';
   }
}


function validateAddSurvey()
{
	$.post("validateSurvey.action",
		{
			name :  byId( 'name' ).value,
			shortName : byId( 'shortName' ).value
		},
		function (data)
		{
			addSurveyValidationCompleted(data);
		},'xml');
		
  return false;
}

function addSurveyValidationCompleted( messageElement )
{
  	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	var message = messageElement.firstChild.nodeValue;

	if ( type == 'success' )
	{
		// Both edit and add form has id='dataSetForm'  
		  
		var selectedList = document.getElementById( 'selectedList' );
		for(var k=0;k<selectedList.length;k++)
		{
			selectedList.options[k].selected = "true";
		}  
		document.forms['addSurveyForm'].submit();
	} 
    else if ( type == 'input' )
    {
		setMessage( message );
	}
}


function validateEditSurvey()
{
  $.post("validateSurvey.action",
		{
			name :  byId( 'name' ).value,
			shortName : byId( 'shortName' ).value,
			url : byId( 'url' ).value,
			surveyId : byId( 'surveyId' ).value
		},
		function (data)
		{
			editSurveyValidationCompleted(data);
		},'xml');

  return false;
}
function editSurveyValidationCompleted( messageElement )
{
  	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	var message = messageElement.firstChild.nodeValue;

	if ( type == 'success' )
	{
		// Both edit and add form has id='dataSetForm'
		var selectedList = document.getElementById( 'selectedList' );
		for(var k=0;k<selectedList.length;k++)
		{
			selectedList.options[k].selected = "true";
		} 
		  
		document.forms['editSurveyForm'].submit();
	}
	else if ( type == 'input' )
	{
		setMessage(message);
	}
}

// ----------------------------------------------------------------------
// List
// ----------------------------------------------------------------------

function initLists()
{
    var id;
	
	var list = document.getElementById( 'selectedList' );
	
    for ( id in surveyMembers )
    {
        list.add( new Option( surveyMembers[id], id ), null );
    }	
	
    list = document.getElementById( 'availableList' );
    
    for ( id in availableIndicators )
    {
        list.add( new Option( availableIndicators[id], id ), null );
    }
}

function filterSurveyMembers()
{
	var filter = document.getElementById( 'surveyMembersFilter' ).value;
    var list = document.getElementById( 'selectedList' );
    
    list.options.length = 0;
    
    for ( var id in surveyMembers )
    {
        var value = surveyMembers[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterAvailableIndicators()
{
	var filter = document.getElementById( 'availableIndicatorsFilter' ).value;
    var list = document.getElementById( 'availableList' );
    
    list.options.length = 0;
    
    for ( var id in availableIndicators )
    {
        var value = availableIndicators[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function addSurveyMembers()
{
	var list = document.getElementById( 'availableList' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        surveyMembers[id] = availableIndicators[id];
        
        delete availableIndicators[id];        
    }
    
    filterSurveyMembers();
    filterAvailableIndicators();
}

function removeSurveyMembers()
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

function filterByIndicatorGroup( selectedIndicatorGroup )
{
  var selectedList = document.getElementById( 'selectedList' );

 // var list = new Array();
  
  var params = 'indicatorGroupId=' + selectedIndicatorGroup;
  /*
  for ( var i = 0; i < selectedList.options.length; ++i)
  {
  	//params += '&selectedIndicators=' + selectedList.options[i].value;
  	params += '&selectedIndicators=' + selectedList.options[i].value;
	//list[i] = selectedList.options[i].value;
  }
  
  // Clear the list
  var availableList = document.getElementById( 'availableList' );

  availableList.options.length = 0;
  */
 // alert(list);
  //alert(list.length);
  
  /*
  var request = new Request();
  request.setResponseTypeXML( 'indicatorgroup' );
  request.setCallbackSuccess( filterByIndicatorGroupCompleted );
  //request.send( url );

  var requestString = "filterAvailableIndicatorsByIndicatorGroup.action";
 // var params = "indicatorGroupId=" + selectedIndicatorGroup + "&selectedIndicators=" + list;
  request.sendAsPost( params );
  request.send( requestString ); 
  */
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
  
  var listValue = "";

  // Clear the list
  var availableList = document.getElementById( 'availableList' );

  availableList.options.length = 0;

  for ( var i = 0; i < selectedList.options.length; ++i)
  {
	  listValue+='&selectedIndicators=' + selectedList.options[i].value;
  }
	//alert( listValue);
	$.post("filterAvailableIndicatorsByIndicatorGroup.action?"+listValue,
	  {
		indicatorGroupId : selectedIndicatorGroup
	  },
	  function (data)
	  {
		  filterByIndicatorGroupCompleted(data);
	  },'xml');
  
}

function filterByIndicatorGroupCompleted( indicatorGroup )
{
  var indicators = indicatorGroup.getElementsByTagName( 'indicators' )[0];
  var indicatorList = indicators.getElementsByTagName( 'indicator' );

  var availableList = document.getElementById( 'availableList' );
  
  for ( var i = 0; i < indicatorList.length; i++ )
  {
    var indicator = indicatorList[i];
    var name = indicator.firstChild.nodeValue;
    var id = indicator.getAttribute( 'id' );
   // var title = indicator.firstChild.nodeValue;

    availableList.add( new Option( name, id ), null );
  }
}

function showSurveyDetails( surveyId )
{
	$.get("getSurvey.action",
		{
			surveyId : surveyId
		},
		function (data)
		{
			surveyRecieved(data);
		},'xml');
}

function surveyRecieved( surveyElement )
{
 
  setInnerHTML( 'idField', getElementValue( surveyElement, 'id' ) );
  setInnerHTML( 'nameField', getElementValue( surveyElement, 'name' ) );
  setInnerHTML( 'indicatorCountField', getElementValue( surveyElement, 'indicatorCount' ) );
  
  var desCription = getElementValue( surveyElement, 'description' );
  setInnerHTML( 'descriptionField', desCription ? desCription : '[' + i18n_none + ']' );
  
  var url = getElementValue( surveyElement, 'url' );
  setInnerHTML( 'urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + i18n_none + ']' );
  
   showDetails();
}

