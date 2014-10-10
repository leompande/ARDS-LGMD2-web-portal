
function formValidations()
{
    var orgUnitGroupListCB = document.getElementById("orgUnitGroupList");
    
    sDateIndex    = document.getElementById("sDateLB").selectedIndex;
    eDateIndex    = document.getElementById("eDateLB").selectedIndex;
    sDateTxt = document.getElementById("sDateLB").options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
    eDateTxt = document.getElementById("eDateLB").options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");

    if(sDateIndex < 0) {alert("Please Select Starting Period");return false;}
    else if(eDateIndex < 0) {alert("Please Select Ending Period");return false;}
    else if(sDate > eDate) {alert("Starting Date is Greater");return false;}
    
    var sWidth = 850;
	var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');

    return true;
} // formValidations Function End


function getDataElements()
{
	var dataElementGroupList = document.getElementById("dataElementGroupId");
    //var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
	var dataSetSectionId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
	
    var deSelectionList = document.getElementById("deSelection");    
    var deOptionValue = deSelectionList.options[ deSelectionList.selectedIndex ].value;
    
    if ( dataSetSectionId != null )
    {
    	lockScreen();
		$.post("getDataElementsForTA.action",
		{
			id:dataSetSectionId,
			deOptionValue:deOptionValue
		},
		function (data)
		{
			getDataElementsReceived(data);
		},'xml');
    }
}// getDataElements end      

function getDataElementsReceived( xmlObject )
{
    var availableDataElements = document.getElementById("availableDataElements");

    clearList(availableDataElements);

    var dataElements = xmlObject.getElementsByTagName("dataElement");

    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        
            var option = document.createElement("option");
            option.value = id;
            option.text = dataElementName;
            option.title = dataElementName;
            availableDataElements.add(option, null);
    }
    unLockScreen();
}
// getDataElementsReceived end

function getOUDeatilsForTaget( orgUnitIds )
{
	document.getElementById( "ougGroupSetCB" ).disabled = false;
	document.getElementById( "orgUnitGroupList" ).disabled = false;
	jQuery.postJSON("getOrgUnitName.action",{
  	  id : orgUnitIds[0]
   }, function( json ){
         setFieldValue( "ouNameTB",json.organisationUnit.name );
   });
}

function getOUDetailsForTargetRecevied(xmlObject)
{
	var element = dataelement.getElementsByTagName("orgunit");
    var orgUnitname = element[0].getElementsByTagName("OugUnitName")[0].firstChild.nodeValue;
    document.targetAnalysisForm.ouNameTB.value = orgUnitname;
}

function getOrgUnitGroupsDataElements() 
{
	var checked = byId('ougGroupSetCB').checked;
	clearListById('orgUnitGroupList');
	
	if (checked)
	{
		var ouGroupId = document.getElementById("orgUnitGroupList");
		for ( var i = 0; i < orgUnitGroupIds.length; i++) 
		{

			var option = document.createElement("option");
			option.value = orgUnitGroupIds[i];
			option.text = orgUnitGroupNames[i];
			option.title = orgUnitGroupNames[i];
			ouGroupId.add(option, null);
		}
	}
	else
	{
	//document.getElementById( "ougGroupSetCB" ).disabled = true;
	}
	//clearList( ouGroupId );
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


//formValidationsForDeTarget Function Start
function formValidationsForDeTarget()
{
	
	var avlDEListSize  = document.targetAnalysisForm.availableDataElements.options.length;//alert(selDEListSize);
	
	//var selOUListLength = document.ChartGenerationForm.orgUnitListCB.options.length;//alert(selOUListLength);
	var orgUnitGroupListCB = document.getElementById("orgUnitGroupList");
    
	var deTargettempSelButton = tempSelButton;
	
    sDateIndex    = document.getElementById("sDateLB").selectedIndex;
    eDateIndex    = document.getElementById("eDateLB").selectedIndex;
    sDateTxt = document.getElementById("sDateLB").options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
    eDateTxt = document.getElementById("eDateLB").options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");

    if(avlDEListSize <= 0 ){alert( "Please Select DataElement" );return false;}
    else if(sDateIndex < 0) {alert("Please Select Starting Period");return false;}
    else if(eDateIndex < 0) {alert("Please Select Ending Period");return false;}
    else if(sDate > eDate) {alert("Starting Date is Greater");return false;}
    else if(document.targetAnalysisForm.ouNameTB.value == "" || document.getElementById("ouNameTB") == null ){alert( "Please Select OrganisationUnit" );return false;}
    
    else if(document.getElementById( 'ougGroupSetCB' ).checked && orgUnitGroupListCB.selectedIndex < 0 ) 
	{
		alert( "Please select OrgUnitGroup" );
		return false;
    
	}
    /*
    var sWidth = 850;
	var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');

    return true;
    */
    generateChartDeTarget( deTargettempSelButton );
	
}    

// formValidationsForDeTarget Function End



function generateChartDeTarget( deTargettempSelButton )
{

	var url = "generateChartDeTarget.action?" + getParamsStringBySelected( 'orgUnitGroupList', 'orgUnitGroupList' );
	
	/*
	var url = "generateChartDataElement.action?";
		url += getParamString( 'selectedDataElements', 'selectedDataElements' ) + "&"
		url += getParamsStringBySelected( 'orgUnitGroupList', 'orgUnitGroupList' )+ "&"
		url += getParamString( 'orgUnitListCB', 'orgUnitListCB' )+ "&"
		url += getParamsStringBySelected( 'yearLB', 'yearLB' )+ "&"
		url += getParamsStringBySelected( 'periodLB', 'periodLB' )+ "&"
	*/	
	//alert(url);
	jQuery( "#contentDiv" ).load( url,
	{
		ouIDTB : getFieldValue( 'ouIDTB' ),
		sDateLB : getFieldValue( 'sDateLB' ),
		eDateLB : getFieldValue( 'eDateLB' ),
		availableDataElements : getFieldValue( 'availableDataElements' ),
		ougGroupSetCB : isChecked( 'ougGroupSetCB' ),
		selButton : deTargettempSelButton,
	} ).dialog( {
		title: 'DataElement Target Graphical Analysis',
		maximize: true, 
		closable: true,
		modal:true,
		overlay:{ background:'#000000', opacity:0.1 },
		width: 1000,
		height: 800
	} );
}

function getParamsStringBySelected( elementId, param )
{
	//alert( "getParamsStringBySelected" );
	var result = "";
	var list = jQuery( "#" + elementId ).children( ":selected" );
	
	list.each( function( i, item ){
		
		//result += param + "=" + item.value + "&";
		result += param + "=" + item.value;
		result += ( i < list.length - 1 ) ? "&" : "";
		
	});
	
	//result = result.substring( 0, list.length - 1 );
	alert( result );
	return result;
}



