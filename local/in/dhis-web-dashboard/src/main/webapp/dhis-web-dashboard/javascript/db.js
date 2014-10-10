
// for filter by dataelement group or section
function checkedDataElementGroup()
{
	//var selectedDataElementGroups = document.getElementById("selectedDataElementGroups");
	//clearList(selectedDataElementGroups);
	jQuery('#availableDataElements').find('option').remove();
	jQuery('#selectedDataElements').find('option').remove();
	
	var chkValue=null;
	if(document.getElementById("chkDataGrp").checked==true)
	{
		chkValue='true';
	}
	else
	{
		chkValue='false';
	}	
	
	jQuery('#loaderDiv').show();
	jQuery('#dataElementGrpDiv').load('getDataElemetGrp.action',{
		checkValue : chkValue
	},
	function(){
		jQuery('#loaderDiv').hide();
		
	});	
	 
}

function getOUDeatilsForSurvey( orgUnitIds )
{
	jQuery.postJSON("getOrgUnitName.action",{
  	  id : orgUnitIds[0]
   }, function( json ){
         setFieldValue( "ouNameTB",json.organisationUnit.name );
   });
}


function getOUDeatilsForAA(orgUnitIds)
{
	/*
	var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
	var request = new Request();
	request.setResponseTypeXML( 'orgunit' );
	request.setCallbackSuccess( getOUDetailsForAARecevied );
	request.send( url );
	*/
	$.post("getOrgUnitDetails.action",
			{
				orgUnitId : orgUnitIds[0]
			},
			function (data)
			{
				getOUDetailsForAARecevied(data);
			},'xml');

}

function getOUDetailsForAARecevied(xmlObject)
{
    var orgUnits = xmlObject.getElementsByTagName("orgunit");
    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
        document.ChartGenerationForm.ouIDTB.value = id;
        document.ChartGenerationForm.ouNameTB.value = orgUnitName;
    }    		
}

function getOUDeatilsForGA(orgUnitIds)
{

	$.post("getOrgUnitDetails.action",
			{
				orgUnitId : orgUnitIds[0],
				type : 'ta'
			},
			function (data)
			{
				getOUDetailsForGARecevied(data);
			},'xml');

	/*
	var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsForGARecevied );

    var requestString = "getOrgUnitDetails.action";
    var params = "orgUnitId=" + orgUnitIds+"&type=ta";
    request.sendAsPost( params );
    request.send( requestString );
    */ 	
}

function getOUDetailsForGARecevied(xmlObject)
{
    var categoryIndex = document.ChartGenerationForm.categoryLB.selectedIndex;
    var facilityIndex =  document.ChartGenerationForm.facilityLB.selectedIndex;
    var index = 0;		
    var i=0;
		
    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        if(document.ChartGenerationForm.categoryLB.options[categoryIndex].value == "period" || document.ChartGenerationForm.facilityLB.options[facilityIndex].value == "children")
        {
            index = document.ChartGenerationForm.orgUnitListCB.options.length;
            for(i=0;i<index;i++)
            {
                document.ChartGenerationForm.orgUnitListCB.options[0] = null;
            }
            document.ChartGenerationForm.orgUnitListCB.options[0] = new Option(orgUnitName,id,false,false);
        }
        else
        {
            index = document.ChartGenerationForm.orgUnitListCB.options.length;
            for(i=0;i<index;i++)
            {
                if(id == document.ChartGenerationForm.orgUnitListCB.options[i].value) return;
            }
            document.ChartGenerationForm.orgUnitListCB.options[index] = new Option(orgUnitName,id,false,false);
        }
    }	
    		
}

function getOUDeatilsForDataStatus( orgUnitIds )
{
	jQuery.postJSON("getOrgUnitName.action",{
  	  id : orgUnitIds[0]
   }, function( json ){

	   var orgUnitId = json.organisationUnit.id;
	   var orgUnitName = json.organisationUnit.name;
	   var faciltyLB = document.getElementById("facilityLB");
	   var facilityIndex =  faciltyLB.selectedIndex;
	   var orgUnitListCB = document.getElementById("orgUnitListCB");
	   
	   if( faciltyLB.options[facilityIndex].value == "children" )
       {
           for( i = 0; i< orgUnitListCB.options.length; i++ )
           {
        	   orgUnitListCB.options[0] = null;
           }
           orgUnitListCB.options[0] = new Option( orgUnitName, orgUnitId, false, false );
       }
       else
       {
           for( i = 0; i < orgUnitListCB.options.length; i++ )
           {
               if( orgUnitId == orgUnitListCB.options[i].value) return;
           }
           orgUnitListCB.options[orgUnitListCB.options.length] = new Option( orgUnitName, orgUnitId, false, false );
       }
        
   });
}


function getOUDetails(orgUnitIds)
{
    
	$.post("getOrgUnitDetails.action",
			{
				orgUnitId : orgUnitIds[0]
			},
			function (data)
			{
				getOUDetailsRecevied(data);
			},'xml');
	
	/*
	var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsRecevied );

    var requestString = "getOrgUnitDetails.action";
    var params = "orgUnitId=" + orgUnitIds;
    request.sendAsPost( params );
    request.send( requestString );
    */
}

function getOUDetailsRecevied(xmlObject)
{
    var categoryIndex = document.ChartGenerationForm.categoryLB.selectedIndex;
    var facilityIndex =  document.ChartGenerationForm.facilityLB.selectedIndex;
    var index = 0;		
    var i=0;
		
    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        if(document.ChartGenerationForm.categoryLB.options[categoryIndex].value == "period" || document.ChartGenerationForm.facilityLB.options[facilityIndex].value == "children")
        {
            index = document.ChartGenerationForm.orgUnitListCB.options.length;
            for(i=0;i<index;i++)
            {
                document.ChartGenerationForm.orgUnitListCB.options[0] = null;
            }
            document.ChartGenerationForm.orgUnitListCB.options[0] = new Option(orgUnitName,id,false,false);
        }
        else
        {
            index = document.ChartGenerationForm.orgUnitListCB.options.length;
            for(i=0;i<index;i++)
            {
                if(id == document.ChartGenerationForm.orgUnitListCB.options[i].value) return;
            }
            document.ChartGenerationForm.orgUnitListCB.options[index] = new Option(orgUnitName,id,false,false);
        }
    }	
    		
}


function getOrgUnitGroups()
{
    var orgUnitGroupSetList = document.getElementById("orgUnitGroupSetListCB");
    var orgUnitGroupSetId = orgUnitGroupSetList.options[ orgUnitGroupSetList.selectedIndex ].value;
	
    if ( orgUnitGroupSetId != null )
    {	
		$.post("getOrgUnitGroups.action",
		{
			orgUnitGroupSetId:orgUnitGroupSetId
		},
		function (data)
		{
			getOrgUnitGroupsReceived(data);
		},'xml');
    }
}
function groupChangeFunction(evt)
{
    document.ChartGenerationForm.selectedGroup.value = true;
    document.ChartGenerationForm.orgUnitListCB.value = null;
} //groupChangeFunction end

function getOrgUnitGroupsReceived(xmlObject)
{
    var orgUnitGroupList = document.getElementById("orgUnitListCB");
    clearList(orgUnitGroupList);
	
    var orgUnitGroups = xmlObject.getElementsByTagName("orgunitgroup");

    for ( var i = 0; i < orgUnitGroups.length; i++ )
    {
        var id = orgUnitGroups[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitGroupName = orgUnitGroups[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        var option = document.createElement("option");
        option.value = id;
        option.text = orgUnitGroupName;
        option.title = orgUnitGroupName;
        orgUnitGroupList.add(option, null);
    }	
}

//--------------------------------------
//
//--------------------------------------

/*
function getDataElements()
{
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    //var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    var dataSetSectionId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    var deSelectionList = document.getElementById("deSelection");    
    var deOptionValue = deSelectionList.options[ deSelectionList.selectedIndex ].value;
    /*
    if ( dataSetSectionId == 0 )
    {
    	alert( dataSetSectionId );
    	document.getElementById( "availableDataElementsFilter" ).value = "";
    	document.getElementById( "availableDataElementsFilter" ).disabled = false;
    	return false;
    }
	*/
/*
    if ( dataSetSectionId != null )
    {
    	document.getElementById( "availableDataElementsFilter" ).value = "";
    	document.getElementById( "availableDataElementsFilter" ).disabled = true;
    	
        if ( dataSetSectionId == 0 )
        {
        	document.getElementById( "availableDataElementsFilter" ).value = "";
        	document.getElementById( "availableDataElementsFilter" ).disabled = false;
        }
        else
        {
        	document.getElementById( "availableDataElementsFilter" ).value = "";
        	document.getElementById( "availableDataElementsFilter" ).disabled = true;
        }
    	
        lockScreen();
    	$.post("getDataElements.action",
		{
			//id:dataElementGroupId,
			id:dataSetSectionId,
			deOptionValue:deOptionValue
		},
		function (data)
		{
			getDataElementsReceived(data);
		},'xml');
    }
}
*/
// getDataElements end           

// getDataElements Start  
function getDataElements()
{
    var checkValue = document.getElementById("hiddenChkValue").value;    
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;    
    
    var deSelectionList = document.getElementById("deSelection");    
    var deOptionValue = deSelectionList.options[ deSelectionList.selectedIndex ].value;
    
    if ( dataElementGroupId != null )
    {
    	document.getElementById( "availableDataElementsFilter" ).value = "";
    	document.getElementById( "availableDataElementsFilter" ).disabled = true;
    	
        if ( dataElementGroupId == 0 )
        {
        	document.getElementById( "availableDataElementsFilter" ).value = "";
        	document.getElementById( "availableDataElementsFilter" ).disabled = false;
        }
        else
        {
        	document.getElementById( "availableDataElementsFilter" ).value = "";
        	document.getElementById( "availableDataElementsFilter" ).disabled = true;
        }
    	
        lockScreen();
    	$.post("getDataElements.action",
		{
			 id:dataElementGroupId,
			 chkValue:checkValue,
			 deOptionValue:deOptionValue
		},
		function (data)
		{
			getDataElementsReceived(data);
		},'xml');
    }
}
// getDataElements end   



function getDataElementsWithOutOptionCombo()
{
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    //var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    var dataSetSectionId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    var deSelectionList = document.getElementById("deSelection");   
    
    if ( dataSetSectionId != null )
    {
    	lockScreen();
		$.post("getDataElements.action",
		{
			id:dataSetSectionId
		},
		function (data)
		{
			getDataElementsReceived(data);
		},'xml');
    }
}// getDataElements end          


function getDataElementsGroupsInDataSet()
{
    var dataSetOptionList = document.getElementById("dataSetId");
    var dataSetId = dataSetOptionList.options[ dataSetOptionList.selectedIndex ].value;
    
    var deGroupSelectionList = document.getElementById("deGroupSelectionList");   
    
    if ( dataSetId != null )
    {
		if ( dataElementGroupId != null )
		{
			$.post("getDataElementsGroupAndDataSetAction.action",
			{
				id:dataSetId
			},
			function (data)
			{
				getDataElementsGroupsReceived(data);
			},'xml');
		}
    }
}// getDataElementGroups end          


function getDataElementsReceived( xmlObject )
{
    var availableDataElements = document.getElementById("availableDataElements");
    var selectedDataElements = document.getElementById("selectedDataElements");

    clearList(availableDataElements);

    var dataElements = xmlObject.getElementsByTagName("dataElement");

    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        if ( listContains(selectedDataElements, id) == false )
        {
            var option = document.createElement("option");
            option.value = id;
            option.text = dataElementName;
            option.title = dataElementName;
            availableDataElements.add(option, null);
        }
    }
    unLockScreen(); 
// If the list of available dataelements is empty, an empty placeholder will be added
//addOptionPlaceHolder( availableDataElements );
}// getDataElementsReceived end


function getDataElementGroupsReceived( xmlObject )
{
    var availableDataElementGroups = document.getElementById("availableDataElementGroups");
    var selectedDataElementGroups = document.getElementById("selectedDataElementGroups");

    clearList(availableDataElementGroups);

    var dataElementGroups = xmlObject.getElementsByTagName("dataElementGroup");

    for ( var i = 0; i < dataElementGroups.length; i++ )
    {
        var id = dataElementGroups[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementGroupName = dataElementGroups[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        if ( listContains(selectedDataElementGroups, id) == false )
        {
            var option = document.createElement("option");
            option.value = id;
            option.text = dataElementGroupName;
            option.title = dataElementGroupName;
            availableDataElementGroups.add(option, null);
        }
    }
    
// If the list of available dataelement groups is empty, an empty placeholder will be added
// addOptionPlaceHolder( availableDataElementGroups );
}// getDataElementGroupsReceived end


function getIndicators()
{
    var indicatorGroupList = document.getElementById( "indicatorGroupId" );
    var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;
    
    /*
    if ( indicatorGroupId == "$ALL" )
    {
    	alert( indicatorGroupId );
    	document.getElementById( "availableIndicatorsFilter" ).value = "";
    	document.getElementById( "availableIndicatorsFilter" ).disabled = false;
    	return false;
    }
    */
    
    if ( indicatorGroupId != null )
    {
    	
        if ( indicatorGroupId != "$ALL" )
        {
        	//alert( indicatorGroupId );
        	document.getElementById( "availableIndicatorsFilter" ).value = "";
        	document.getElementById( "availableIndicatorsFilter" ).disabled = true;
        	//return false;
        }
        else
        {
        	document.getElementById( "availableIndicatorsFilter" ).value = "";
        	document.getElementById( "availableIndicatorsFilter" ).disabled = false;
        }
    	//document.getElementById( "availableIndicatorsFilter" ).value = "";
    	//document.getElementById( "availableIndicatorsFilter" ).disabled = false;
        lockScreen();
    	$.post("getIndicators.action",
			{
				id:indicatorGroupId
			},
			function (data)
			{
				getIndicatorsReceived(data);
			},'xml');
    }
}


function getIndicatorsForAnnual()
{
    var indicatorGroupList = document.getElementById( "indicatorGroupId" );
    var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;

    if ( indicatorGroupId != null )
    {
    	lockScreen();
    	$.post("getIndicators.action",
			{
				id:indicatorGroupId
			},
			function (data)
			{
				getIndicatorsReceived(data);
			},'xml');
    }
}

function getSurveyIndicators()
{
    var indicatorGroupList = document.getElementById( "indicatorGroupId" );
    var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;
    var surveyExist = "yes";
    if ( indicatorGroupId != null )
    {
    	/*
    	var url = "getIndicators.action?id=" + indicatorGroupId + "&surveyExist=" + surveyExist;
		var request = new Request();
		request.setResponseTypeXML('indicator');
		request.setCallbackSuccess(getIndicatorsReceived);
		request.send(url); 
    	*/
    	lockScreen();
    	$.post("getIndicators.action",
			{
				id : indicatorGroupId,
				surveyExist : surveyExist
			},
			function (data)
			{
				getIndicatorsReceived(data);
			},'xml');
			
			
    }
}

function getIndicatorsReceived( xmlObject )
{	
    var availableIndicators = document.getElementById( "availableIndicators" );
    var selectedIndicators = document.getElementById( "selectedIndicators" );
	
    clearList( availableIndicators );
	
    var indicators = xmlObject.getElementsByTagName( "indicator" );
	
    for ( var i = 0; i < indicators.length; i++ )
    {
        var id = indicators[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var indicatorName = indicators[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
        if ( listContains( selectedIndicators, id ) == false )
        {
            var option = document.createElement( "option" );
            option.value = id;
            option.text = indicatorName;
            availableIndicators.add( option, null );
        }
    }
    unLockScreen();
// If the list of available indicators is empty, an empty placeholder will be added
//addOptionPlaceHolder( availableIndicators );
}

function selectSingleOption()
{
    var facilityObj = document.getElementById( 'facilityLB' );
    var facilityVal = facilityObj.options[ facilityObj.selectedIndex ].value;
	
    var categoryObj = document.getElementById( 'categoryLB' );
    var categoryVal = categoryObj.options[ categoryObj.selectedIndex ].value;
	
    if( document.getElementById( 'ougSetCB' ).checked && ( facilityVal == "children" || categoryVal == "period" ) )
    {
        var orgUnitListObj = document.getElementById('orgUnitListCB');
	
        for( var i = 0; i < orgUnitListObj.length; i++ )
        {
            if( i != orgUnitListObj.selectedIndex )
                orgUnitListObj.options[i].selected = false;
        }
    }
}

