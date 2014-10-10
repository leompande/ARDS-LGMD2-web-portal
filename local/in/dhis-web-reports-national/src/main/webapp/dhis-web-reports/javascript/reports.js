

function getSelectedOrgUnit( orgUnitIds )
{
	jQuery.postJSON("getOrgUnitName.action",{
    	  id : orgUnitIds[0]
     }, function( json ){
           setFieldValue( "ouNameTB",json.organisationUnit.name );
     });
}

function responseGetSelectedOrgUnitName( orgunit )
{
    var element = dataelement.getElementsByTagName("orgunit");
    var orgUnitname = element[0].getElementsByTagName("OugUnitName")[0].firstChild.nodeValue;
    document.reportForm.ouNameTB.value = orgUnitname;
}


function checkStartDate( dtStr )
{
	
	if( isDate( dtStr ) )
	{
		var splitDate = dtStr.split("-");
		var temDay = splitDate[2];
		if( parseInt( temDay,10 ) > 1 )
		{
			alert("Please select start day of the month");
			return false;
		}		
		return true;
	}
	else
	{
		return false;
	}
}

function checkEndDate( dtStr )
{
	if( isDate( dtStr ) )
	{
		var splitDate = dtStr.split("-");
		var temDay = splitDate[2];
		if( parseInt( temDay,10 ) < 30 )
		{
			alert("Please select end day of the month");
			return false;
		}		
		return true;
	}
	else
	{
		return false;
	}
}

function getOUDetails(orgUnitIds)
{
	/* //var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
	
    var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsRecevied );
    //request.send( url );

    var requestString = "getOrgUnitDetails.action";
    var params = "orgUnitId=" + orgUnitIds;
    request.sendAsPost( params );
    request.send( requestString ); */
	
	$.post("getOrgUnitDetails.action",
			{
				orgUnitId : orgUnitIds
			},
			function (data)
			{
				getOUDetailsRecevied(data);
			},'xml');

    getReports();
}

function getOUDetailsForOuWiseProgressReport(orgUnitIds)
{
    /* //var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;

    var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsRecevied );
    //request.send( url );

    var requestString = "getOrgUnitDetails.action";
    var params = "orgUnitId=" + orgUnitIds;
    request.sendAsPost( params );
    request.send( requestString ); */
	
	$.post("getOrgUnitDetails.action",
		{
			orgUnitId : orgUnitIds
		},
		function (data)
		{
			getOUDetailsRecevied(data);
		},'xml');

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
        /* //var url = "getDataElements.action?id=" + dataElementGroupId;
        var request = new Request();
        request.setResponseTypeXML('dataElement');
        request.setCallbackSuccess(getDataElementsReceived);
        //request.send(url);

        var requestString = "getDataElements.action";
        var params = "id=" + dataElementGroupId;
        request.sendAsPost( params );
        request.send( requestString ); */
		
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
            /* var option = document.createElement("option");
            option.value = id;
            option.text = dataElementName;
            option.title = dataElementName;
            availableDataElements.add(option, null); */
			
			$("#availableDataElements").append("<option value='"+ id +"' title='" + dataElementName + "'>" + dataElementName + "</option>");
        }
    }    
}// getDataElementsReceived end

//------------------------------------------------------------------------------
// Get Periods 
//------------------------------------------------------------------------------

function getPeriods()
{
    var periodTypeList = document.getElementById( "periodTypeId" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
    var availablePeriods = document.getElementById( "availablePeriods" );
    var reportsList = document.getElementById( "reportList" );
  
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

        document.reportForm.generate.disabled=false;
        var ouId = document.reportForm.ouIDTB.value;
        var reportTypeName = document.reportForm.reportTypeNameTB.value;
  
        getReports( ouId, reportTypeName );
    }
    else
    {    
        document.reportForm.generate.disabled=true;
        clearList( availablePeriods );
        clearList( reportsList );
    }
}

function getPeriodsForCumulative()
{
    //document.reportForm.generate.disabled=false;
    var periodTypeList = document.getElementById( "periodTypeId" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
    var reportsList = document.getElementById( "reportList" );
	
    if ( periodTypeId != "NA" )
    {
        var ouId = document.reportForm.ouIDTB.value;
        var reportTypeName = document.reportForm.reportTypeNameTB.value;
  
        getReports(ouId, reportTypeName);
        document.reportForm.generate.disabled=false;
    }
    else
    {
    
        document.reportForm.generate.disabled=true;
        clearList( reportsList );
        jQuery("#startDate").val("");
        jQuery("#endDate").val("");
        document.reportForm.startDate = "";
        document.reportForm.endDate = " ";
    }
 
}

/*
function getReports( ouId, reportListFileName )
{ 
    var periodTypeList = document.getElementById( "periodTypeId" );
    var periodType = periodTypeList.options[ periodTypeList.selectedIndex ].value;
    var autogenvalue = document.getElementById( "autogen" ).value;
          
    if ( periodType != "NA" && ouId != null && ouId != "" )
    {
        //var url = "getReports.action?periodType=" + periodType + "&ouId="+ouId + "&reportListFileName="+reportListFileName+"&autogenrep="+autogenvalue;
    
        var request = new Request();
        request.setResponseTypeXML( 'report' );
        request.setCallbackSuccess( getReportsReceived );
        //request.send( url );

        var requestString = "getReports.action";
//      var params = "periodType=" + periodType + "&ouId="+ouId + "&reportListFileName="+reportListFileName+"&autogenrep="+autogenvalue;
        var params = "periodType=" + periodType + "&ouId="+ouId + "&reportTypeName="+reportTypeName+"&autogenrep="+autogenvalue;
        request.sendAsPost( params );
        request.send( requestString );
    }
}
*/


function getReports( ouId, reportTypeName )
{ 
	var periodTypeList = byId( "periodTypeId" );
	if( periodTypeList == null || periodTypeList.options.length == 0)
	{
		return;
	}
    var periodType = periodTypeList.options[ periodTypeList.selectedIndex ].value;
    var autogenvalue = byId( "autogen" ).value;
          
    if ( periodType != "NA" && ouId != null && ouId != "" )
    {
    	/*  var request = new Request();
        request.setResponseTypeXML( 'report' );
        request.setCallbackSuccess( getReportsReceived );

        var requestString = "getReports.action";
        var params = "periodType=" + periodType + "&ouId="+ouId + "&reportTypeName="+reportTypeName + "&autogenrep="+autogenvalue;
        request.sendAsPost( params );
        request.send( requestString ); */
		
		$.post("getReports.action",
		{
			periodType : periodType,
			ouId : ouId,
			reportTypeName : reportTypeName,
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
	
        /* var option = document.createElement( "option" );
        option.value = id;
        option.text = name;
        reportsList.add( option, null ); */
		
		$("#reportList").append("<option value='"+ id +"'>" + name + "</option>");
		
        reportModels.put(id,model);
        reportFileNames.put(id,fileName);
    }
}

function getPeriodsReceived( xmlObject )
{	
    var availablePeriods = document.getElementById( "availablePeriods" );
	
    clearList( availablePeriods );
	
    var periods = xmlObject.getElementsByTagName( "period" );
    if( periods.length <= 0 )
    {
        document.reportForm.generate.disabled=true;
    }
    for ( var i = 0; i < periods.length; i++)
    {
        var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var periodName = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;
		
		$("#availablePeriods").append("<option value='"+ id +"'>" + periodName + "</option>");
    }	
}


// -----------------------------------------------------------------------------
// Date Validation
// -----------------------------------------------------------------------------

// Declaring valid date character, minimum year and maximum year
var dtCh= "-";
var minYear=1900;
var maxYear=2100;

function isInteger(s)
{
    var i;
    for (i = 0; i < s.length; i++)
    {
        // Check that current character is number.
        var c = s.charAt(i);
        if (((c < "0") || (c > "9"))) return false;
    }
    // All characters are numbers.
    return true;
}

function stripCharsInBag(s, bag)
{
    var i;
    var returnString = "";
  
    // Search through string's characters one by one.
    // If character is not in bag, append to returnString.
    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }
  
    return returnString;
}

function daysInFebruary (year)
{
    // February has 29 days in any year evenly divisible by four,
    // EXCEPT for centurial years which are not also divisible by 400.
  
    return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
}

function DaysArray(n) 
{
    for (var i = 1; i <= n; i++)
    {
        this[i] = 31
        if (i==4 || i==6 || i==9 || i==11) {
            this[i] = 30
        }
        if (i==2) {
            this[i] = 29
        }
    }
  
    return this
}

function isDate(dtStr)
{
    var daysInMonth = DaysArray(12)
    var pos1=dtStr.indexOf(dtCh)
    var pos2=dtStr.indexOf(dtCh,pos1+1)

    var strYear=dtStr.substring(0,pos1)
    var strMonth=dtStr.substring(pos1+1,pos2)
    var strDay=dtStr.substring(pos2+1)

    var strMonthWithZero = strMonth
    var strDayWithZero = strDay
    //var strDay=dtStr.substring(pos1+1,pos2)
    //var strYear=dtStr.substring(pos2+1)
    var currentDate= new Date();
    var mm = currentDate.getMonth()+1;
    var dd = currentDate.getDate();
    ms = new String(mm);
    ds = new String(dd);
    if ( ms.length == 1 ) ms = "0" + ms;
    if ( ds.length == 1 ) ds = "0" + ds;
    var dateString = currentDate.getFullYear() + "-" + ms + "-" + ds;
    
    strYr=strYear
    if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
    if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
    for (var i = 1; i <= 3; i++)
    {
        if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
    }
    month=parseInt(strMonth)
    day=parseInt(strDay)
    year=parseInt(strYr)
    if (pos1==-1 || pos2==-1 || strMonthWithZero.length<2 || strDayWithZero.length<2)
    {
        alert("The date format should be : yyyy-mm-dd")
        return false
    }
  
    if (strMonth.length<1 || month<1 || month>12)
    {
        alert("Please enter a valid month")
        return false
    }
    if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month])
    {
        alert("Please enter a valid day")
        return false
    }
    if (strYear.length != 4 || year==0 || year<minYear || year>maxYear)
    {
        alert("Please enter a valid 4 digit year between "+minYear+" and "+maxYear)
        return false
    }
    if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false)
    {
        alert("Please enter a valid date")
        return false
    }
    if(dtStr > dateString)
    {
       if( currentDate.getFullYear() == year &&  mm == month )
       {
    	   
       }
       else
       {
    	   alert("The Date is greater than Today's Date");
           return false; 
       }
    	
    	
    	//alert("The Date is greater than Today's Date");
        //return false;
    }

    return true
}

function isInteger(s)
{
    var n = trim(s);
    return n.length > 0 && !(/[^0-9]/).test(n);
}

// -----------------------------------------------------------------------------
// String Trim
// -----------------------------------------------------------------------------

function trim( stringToTrim ) 
{
    return stringToTrim.replace(/^\s+|\s+$/g,"");
}


//-----------------------------------------------------------------------------
//FormValidations for ED Report
//-----------------------------------------------------------------------------
function formValidationsForEDReport()
{
		
	var startPeriodObj = document.getElementById('selectedStartPeriodId');
	var endPeriodObj = document.getElementById('selectedEndPeriodId');
	var indicatorGroupObj = document.getElementById('indicatorGroupId');
	
	sDateTxt = startPeriodObj.options[startPeriodObj.selectedIndex].text;
	sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
	eDateTxt = endPeriodObj.options[endPeriodObj.selectedIndex].text;
	eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");
 
	if(sDate > eDate) 
	{
		alert( "Starting Date is Greater" );return false;
	}
	else if( indicatorGroupObj.options[indicatorGroupObj.selectedIndex].value == "ALL" )
	{
		alert( "Please Select Indicator Group" );return false;
	}
	return true;
} // formValidations Function End
