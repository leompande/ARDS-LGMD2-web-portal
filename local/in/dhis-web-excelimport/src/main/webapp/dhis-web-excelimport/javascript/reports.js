

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
		//document.reportForm.ouLevelTB.value = level;	
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
		$.post("getDataElements.action",
		{
			id : dataElementGroupId
		},
		function (data)
		{
			getDataElementsReceived(data);
		},'xml');
    }
}
// getDataElements end           

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
}
// getDataElementsReceived end

//---------------------------------------------------------------
// Get Periods 
//---------------------------------------------------------------

function getPeriods()
{
  var periodTypeList = document.getElementById( "periodTypeId" );
  var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
  var availablePeriods = document.getElementById( "availablePeriods" );
  var reportList = document.getElementById( "reportList" );
  
  if ( periodTypeId != "NA" )
  { 
	  $.post("getPeriods.action",
		{
			id : periodTypeId
		},
		function (data)
		{
			getPeriodsReceived(data);
		},'xml');
  }
  else
  {
      clearList( availablePeriods );
      clearList( reportList );
  }
  var ouId = document.reportForm.ouIDTB.value;
  var reportListFileName = document.reportForm.reportListFileNameTB.value;
  
  
  getReports(ouId, reportListFileName);
}

function getReports( ouId, reportListFileName )
{ 
  var periodTypeList = document.getElementById( "periodTypeId" );
  var periodType = periodTypeList.options[ periodTypeList.selectedIndex ].value;
  //var reportListFileName = document.reportForm.reportListFileNameTB.value;
  var autogenvalue = document.getElementById( "autogen" ).value;
 // alert("file:" +reportListFileName + ", periodType :" + periodType + ", ouid:" + ouId);
          
  if ( periodType != "NA" && ouId != null && ouId != "" )
  { 
	  $.post("getReports.action",
		{
			periodType : periodType,
			ouId : ouId[0],
			reportListFileName : reportListFileName,
			autogenrep : autogenvalue
		},
		function (data)
		{
			getReportsReceived(data);
		},'xml');
  }

}

function getReportsReceived( xmlObject )
{	
    var reportsList = byId( "reportList" );
	var orgUnitName = byId( "ouNameTB" );
    
    clearList( reportsList );
    
	if(xmlObject == null){
		return;
	}
    var reports = xmlObject.getElementsByTagName( "report" );
    for ( var i = 0; i < reports.length; i++)
	{
		var id = reports[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var name = reports[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		var model = reports[ i ].getElementsByTagName( "model" )[0].firstChild.nodeValue;
		var fileName = reports[ i ].getElementsByTagName( "fileName" )[0].firstChild.nodeValue;
		var checkerFileName = reports[ i ].getElementsByTagName( "checkerFileName" )[0].firstChild.nodeValue;
		var datasetId = reports[ i ].getElementsByTagName( "datasetid" )[0].firstChild.nodeValue;
		var ouName = reports[ i ].getElementsByTagName( "ouName" )[0].firstChild.nodeValue;
	
		orgUnitName.value = ouName;			
	
		var option = document.createElement( "option" );
		option.value = id;
		option.text = name;
		reportsList.add( option, null );
	
		//$("#reportList").append("<option value='"+ id +"'>" + name + "</option>");
		reportModels.put( id, model );
		reportFileNames.put( id, fileName );
		checkerFileNames.put( id, checkerFileName );
		reportDatasets.put( id, datasetId );
	}
}

function getPeriodsReceived( xmlObject )
{	
	var availablePeriods = document.getElementById( "availablePeriods" );
	
	clearList( availablePeriods );
	
	var periods = xmlObject.getElementsByTagName( "period" );
	
	for ( var i = 0; i < periods.length; i++)
	{
		var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var periodName = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;
		
		var option = document.createElement( "option" );
		option.value = id;
		option.text = periodName;
		availablePeriods.add( option, null );
	}	
}

function submitImportForm()
{
	if (formValidations())
	{
	    setMessage( "Importing started");
		document.getElementById( "reportForm" ).submit();
	}
}
