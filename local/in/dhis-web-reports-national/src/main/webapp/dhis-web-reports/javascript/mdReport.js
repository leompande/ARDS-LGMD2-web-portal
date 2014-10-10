 
// form validation for MD report
function formValidationsForMDReport()
{
    var ouIdTb = document.getElementById("ouIDTB");
    var orgunitIdValue = ouIdTb.value;
	
	var selDeList = document.getElementById("selectedDataElements");
	var selDeListLength = selDeList.options.length;

    var ouLevelList = document.getElementById("orgUnitLevelCB");
    var ouLevelSelIndex = ouLevelList.selectedIndex;
    
    sDateIndex    = document.getElementById("selectedStartPeriodId").selectedIndex;
    eDateIndex    = document.getElementById("selectedEndPeriodId").selectedIndex;
    sDateTxt = document.getElementById("selectedStartPeriodId").options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
    eDateTxt = document.getElementById("selectedEndPeriodId").options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");
    
    
    
    if( orgunitIdValue == null || orgunitIdValue == "" || orgunitIdValue == " " ) 
    {
        alert("Please Select OrganisationUnit"); 
        return false;
    }
    else if( selDeListLength <= 0 ) 
    {
        alert("Please Select Dataelement(s)"); 
        return false;
    }
	else if( ouLevelSelIndex < 0 )
	{
		alert("Please Select OrgUnitLevel"); 
        return false;
	}
	else if(sDateIndex < 0) 
    {
    	alert("Please Select Starting Period");
    	return false;
    }
	else if(eDateIndex < 0) 
	{
		alert("Please Select Ending Period");
		return false;
	}
    
	else if(sDate > eDate) 
	{
		alert("Starting Date is Greater");
		return false;
	}
    
    for( var i = 0; i < selDeListLength; i++ )
    {
		selDeList.options[i].selected = true;
    }
    
    return true;
}
//filter available data elements list
function filterAvailableDataElements()
{
	var filter = document.getElementById( 'availableDataElementsFilter' ).value;
	var list = document.getElementById( 'availableDataElements' );

    list.options.length = 0;

	var selDeListId = document.getElementById( 'selectedDataElements' );
	var selDeLength = selDeListId.options.length;

	for ( var id in availableDataElements )
	{
		var value = availableDataElements[id];
		
		var flag = 1;
		for( var i = 0 ; i<selDeLength; i++ )
		{
			if( id == selDeListId.options[i].value )
			{
				flag =2;				
				break;
			}
		}
		if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 && (flag == 1) )
		{
			list.add( new Option( value, id ), null );
		}
	}
}


function enableCheckBox()
{
	var aggDataDropDown = document.getElementById("aggData");
	var aggData = aggDataDropDown.options[ aggDataDropDown.selectedIndex ].value;
	
	var excludeCheckBox = document.getElementById("excludeZeroData");
	
	if( aggData == "usecaptureddata" )
	{
		excludeCheckBox.checked = false;
		document.getElementById( "excludeZeroData" ).disabled = false;
	}
	else
	{
		excludeCheckBox.checked = false;
		document.getElementById( "excludeZeroData" ).disabled = true;
	}
	
}

// for orgUnit Details
function getOUDeatilsForMDReport( orgUnitIds )
{
    showOverlay();
	$.post("getOrgUnitDetailsForMDReport.action",
    {
        orgUnitId : orgUnitIds[0],
        type : 'ta'
    },
    function (data)
    {
        getOUDetailsForTARecevied(data);
    },'xml');
}

function getOUDetailsForTARecevied( xmlObject )
{
	var ouLevelId = document.getElementById( "orgUnitLevelCB" );
    var orgUnits = xmlObject.getElementsByTagName("orgunit");

	for ( var i = 0; i < orgUnits.length; i++ )
	{
		var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
		var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		var ouLevel = orgUnits[ i ].getElementsByTagName("level")[0].firstChild.nodeValue;
		var maxOULevel = orgUnits[ i ].getElementsByTagName("maxoulevel")[0].firstChild.nodeValue;
		 
		document.getElementById( "ouNameTB" ).value =  orgUnitName;
		
		getorgUnitLevels( ouLevel, maxOULevel );
	}   

	hideOverlay();
}

function getorgUnitLevels( ouLevel, maxOULevel )
{
	var ouLevelId = document.getElementById( "orgUnitLevelCB" );
	var j = 0;

	clearList( ouLevelId );

	var i = parseInt( ouLevel );

	for( i= i+1; i <= maxOULevel; i++ )
	{
		ouLevelId.options[j] = new Option("Level - "+i,i,false,false);
		
		j++;
	}

	if( j == 0 )
	{
		document.getElementById( "ViewReport" ).disabled = true;
	}
	else
	{
		document.getElementById( "ViewReport" ).disabled = false;
	}
}

function showOverlay() 
{
	var o = document.getElementById('overlay');
	o.style.visibility = 'visible';
	jQuery("#overlay").css({
		"height": jQuery(document).height()
	});
	jQuery("#overlayImg").css({
		"top":jQuery(window).height()/2
	});
}
function hideOverlay() 
{
	var o = document.getElementById('overlay');
	o.style.visibility = 'hidden';
}


