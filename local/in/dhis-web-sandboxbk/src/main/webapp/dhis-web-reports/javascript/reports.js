
function getOUDetails(orgUnitIds)
{
	var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
	
	var request = new Request();
	request.setResponseTypeXML( 'orgunit' );
	request.setCallbackSuccess( getOUDetailsRecevied );
	request.send( url );

	getReports();
}

function getOUDetailsRecevied(xmlObject)
{
		
	var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		var level = orgUnits[ i ].getElementsByTagName("level")[0].firstChild.nodeValue;
		
		
		document.reportForm.ouNameTB.value = orgUnitName;
		document.reportForm.ouLevelTB.value = level;	
    }    		
}

//--------------------------------------
//
//--------------------------------------
function getDataElements()
{
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
        
    if ( dataElementGroupId != null )
    {
        var url = "getDataElements.action?id=" + dataElementGroupId;
        var request = new Request();
        request.setResponseTypeXML('dataElement');
        request.setCallbackSuccess(getDataElementsReceived);
        request.send(url);
    }
}// getDataElements end           

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
}// getDataElementsReceived end

//---------------------------------------------------------------
// Get Periods 
//---------------------------------------------------------------
function getPeriods()
{
	var periodTypeList = document.getElementById( "periodTypeId" );
	var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
	var availablePeriods = document.getElementById( "availablePeriods" );
	
	if ( periodTypeId != "NA" )
	{		
		var url = "getPeriods.action?id=" + periodTypeId;
		
		var request = new Request();
	    request.setResponseTypeXML( 'period' );
	    request.setCallbackSuccess( getPeriodsReceived );
	    request.send( url );
	}
	else
	{
	    clearList( availablePeriods );
	    clearList( reportsList );
	}
	var ouId = document.reportForm.ouIDTB.value;
	
	getReports(ouId);
}

function getReports( ouId, reportListFileName )
{	
    var periodTypeList = document.getElementById( "periodTypeId" );
	var periodType = periodTypeList.options[ periodTypeList.selectedIndex ].value;
			
	if ( periodType != "NA" && ouId != null && ouId != "" )
	{		
		var url = "getReports.action?periodType=" + periodType + "&ouId="+ouId + "&reportListFileName="+reportListFileName;
		
		var request = new Request();
	    request.setResponseTypeXML( 'report' );
	    request.setCallbackSuccess( getReportsReceived );
	    request.send( url );
	}
}

function getReportsReceived( xmlObject )
{	
    var reportsList = document.getElementById( "reportList" );
	var orgUnitName = document.getElementById( "ouNameTB" );
    
    clearList( reportsList );
    
    var reports = xmlObject.getElementsByTagName( "report" );
    for ( var i = 0; i < reports.length; i++)
	{
		var id = reports[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var name = reports[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		var model = reports[ i ].getElementsByTagName( "model" )[0].firstChild.nodeValue;
		var fileName = reports[ i ].getElementsByTagName( "fileName" )[0].firstChild.nodeValue;
		var ouName = reports[ i ].getElementsByTagName( "ouName" )[0].firstChild.nodeValue;
	
		orgUnitName.value = ouName;			
	
		var option = document.createElement( "option" );
		option.value = id;
		option.text = name;
		reportsList.add( option, null );
		
		reportModels.put(id,model);
		reportFileNames.put(id,fileName);
	}
}

function getPeriodsReceived( xmlObject )
{	
	var availablePeriods = document.getElementById( "availablePeriods" );
	var selectedPeriods = document.getElementById( "selectedPeriods" );
	
	clearList( availablePeriods );
	
	var periods = xmlObject.getElementsByTagName( "period" );
	
	for ( var i = 0; i < periods.length; i++)
	{
		var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var startDate = periods[ i ].getElementsByTagName( "startDate" )[0].firstChild.nodeValue;
		var endDate = periods[ i ].getElementsByTagName( "endDate" )[0].firstChild.nodeValue;
		
		var periodName = startDate + " - " + endDate;
		
		if ( listContains( selectedPeriods, id ) == false )
		{						
			var option = document.createElement( "option" );
			option.value = id;
			option.text = periodName;
			availablePeriods.add( option, null );
		}			
	}
	
	// If the list of available periods is empty, an empty placeholder will be added
	addOptionPlaceHolder( availablePeriods );
}

