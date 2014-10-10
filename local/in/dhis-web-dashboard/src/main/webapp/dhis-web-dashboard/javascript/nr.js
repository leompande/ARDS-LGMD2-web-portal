function ouSelCBChange()
{
	var ouSelCBId = document.getElementById( "ouSelCB" );
	var ouListCDId = document.getElementById( "orgUnitListCB" );
	var ouLevelId = document.getElementById( "orgUnitLevelCB" );

	if( ouSelCBId.checked )
	{
                 $('#orgUnitListCB').removeAttr('disabled');
                clearList( ouLevelId );
                $("#orgUnitLevelCB").attr("disabled", "disabled");
	}
	else
	{
            $('#orgUnitLevelCB').removeAttr('disabled');
            clearList( ouListCDId );
            $("#orgUnitListCB").attr("disabled", "disabled");
	}

    if( selOrgUnitId != null && selOrgUnitId != "NONE" && selOrgUnitId != "")
    {
        getOUDeatilsForNR( selOrgUnitId );
    }

}

function getOUDeatilsForNR( orgUnitIds )
{
	/*
    var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsForNRRecevied );
    //request.send( url );

    var requestString = "getOrgUnitDetails.action";
    var params = "orgUnitId=" + orgUnitIds+"&type=ta";
    request.sendAsPost( params );
    request.send( requestString ); 
	*/
	
	$.post("getOrgUnitDetails.action",
		{
			orgUnitId:orgUnitIds[0],
			type:"ta"
		},
		function (data)
		{
			getOUDetailsForNRRecevied(data);
		},'xml');
		
}

function getOUDetailsForNRRecevied(xmlObject)
{
	var ouSelCBId = document.getElementById( "ouSelCB" );
	var ouListCDId = document.getElementById( "orgUnitListCB" );
	var ouLevelId = document.getElementById( "orgUnitLevelCB" );

	var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        var ouLevel = orgUnits[ i ].getElementsByTagName("level")[0].firstChild.nodeValue;
        var maxOULevel = orgUnits[ i ].getElementsByTagName("maxoulevel")[0].firstChild.nodeValue;

    	if( ouSelCBId.checked )
    	{
    		ouListCDId.disabled = false;

    		clearList( ouLevelId );
    		ouLevelId.disabled = true;

        	for(var i=0; i < ouListCDId.options.length; i++)
	    	{
		        if( id == ouListCDId.options[i].value ) return;
        	}

        	ouListCDId.options[ouListCDId.options.length] = new Option(orgUnitName, id, false, false);
    	}
    	else
    	{
    		clearList( ouListCDId );

    		ouListCDId.options[ouListCDId.options.length] = new Option(orgUnitName,id,false,false);

    		getorgUnitLevels( ouLevel, maxOULevel );
    	}
    }
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
}

// Removes slected orgunits from the Organisation List
function remOUFunction()
{
	var ouListCDId = document.getElementById( "orgUnitListCB" );

    for( var i = ouListCDId.options.length-1; i >= 0; i-- )
    {
    	if( ouListCDId.options[i].selected )
    	{
    		ouListCDId.options[i] = null;
    	}
    }
}// remOUFunction end



// DataElement and Its options Change Function
function deSelectionChangeFuntion(evt)
{
    var availableDataElements = document.getElementById("availableDataElements");
    var selectedDataElements = document.getElementById("selectedDataElements");

	clearList(availableDataElements);
	clearList(selectedDataElements);
	
	getDataElements();
}
		          
//Facility ListBox Change Function
function facilityChangeFunction(evt)
{
	selFacility = evt.target.value;
	if(selFacility == "Immediate Children")
	{
		var index = document.ChartGenerationForm.orgUnitListCB.options.length;
        for(i=0;i<index;i++)
    	{
    		document.ChartGenerationForm.orgUnitListCB.options[0] = null;
    	}
	}
}// facilityChangeFunction end

// Selected Button (ie ViewSummary or ViewChart) Function
function selButtonFunction(selButton)
{
	document.ChartGenerationForm.selectedButton.value = selButton;
}// selButtonFunction end


//Graphical Analysis Form Validations
function formValidations()
{
	
	//var selDEListSize  = document.ChartGenerationForm.selectedDataElements.options.length;
	var orgUnitListCB = document.getElementById("orgUnitListCB");
	var orgUnitLevelCB = document.getElementById("orgUnitLevelCB");
    var ouSelCB = document.getElementById("ouSelCB");
    //sDateIndex    = document.ChartGenerationForm.sDateLB.selectedIndex;
    //eDateIndex    = document.ChartGenerationForm.eDateLB.selectedIndex;
    
    
	var startPeriodObj = document.getElementById('sDateLB');
	var endPeriodObj = document.getElementById('eDateLB');

	sDateTxt = startPeriodObj.options[startPeriodObj.selectedIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
    eDateTxt = endPeriodObj.options[endPeriodObj.selectedIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");
    
    //sDateTxt = document.ChartGenerationForm.sDateLB.options[sDateIndex].text;
    //sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM - y")),"yyyy-MM-dd");
    //eDateTxt = document.ChartGenerationForm.eDateLB.options[eDateIndex].text;
    //eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM - y")),"yyyy-MM-dd");

    if( ouSelCB.checked)
    {
    	if(orgUnitListCB.options.length <=0 ) { alert( "Please select OrgUnit(s)" ); return false; }
    }
    else if( orgUnitLevelCB.selectedIndex < 0 ) { alert( "Please select OrgUnitLevel" ); return false; }

    orgUnitListCB.disabled = false;
    if(sDateIndex < 0) {alert("Please Select Starting Period");return false;}
    else if(eDateIndex < 0) {alert("Please Select Ending Period");return false;}
    else if(sDate > eDate) {alert("Starting Date is Greater");return false;}
    
    //else if(selDEListSize <=0 ) {alert("Please Select Data elements");return false;}
	/*
	for(k=0;k<document.ChartGenerationForm.selectedDataElements.options.length;k++)
    	{
    		document.ChartGenerationForm.selectedDataElements.options[k].selected = true;
        } 
	// for l
	*/
    
    /*
    for(k = 0; k < orgUnitListCB.options.length; k++)
	{
		orgUnitListCB.options[k].selected = true;
	}

    var sWidth = 850;
	var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
  	return true;
  	*/
  	
  	generateNullReporterResult();
} 

function generateNullReporterResult()
{

	var url = "NullReporterResult.action?" + getParamString( 'orgUnitListCB', 'orgUnitListCB' )+  "&" + getParamsStringBySelected( 'orgUnitLevelCB', 'orgUnitLevelCB' );
	
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
		selectedDataSet : getFieldValue( 'selectedDataSet' ),
		sDateLB : getFieldValue( 'sDateLB' ),
		eDateLB : getFieldValue( 'eDateLB' ),
		includeZeros : isChecked( 'includeZeros' ),
		ouSelCB : isChecked( 'ouSelCB' ),
	} ).dialog( {
		title: 'Null Report',
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
	//alert( result );
	return result;
}


// formValidations Function End


//Getting corresponding Period List for Data Sets Null reporter. 
function getdSetPeriods()
{

	var dataSetList = document.getElementById("selectedDataSet");
    var dataSetId = dataSetList.options[ dataSetList.selectedIndex].value;
    
	$.post("getDataSetPeriods.action",
	{
		id : dataSetId
	},
	function (data)
	{
		getdSetPeriodsReceived(data);
	},'xml');
 	
}

function getdSetPeriodsReceived( xmlObject )
{	
	var sDateLB = document.getElementById( "sDateLB" );
    var eDateLB = document.getElementById( "eDateLB" );
		
    var periods = xmlObject.getElementsByTagName( "period" );
    
    if ( periods.length <= 0 )
    {
    	clearList( sDateLB );
        clearList( eDateLB );
    }

    for ( var i = 0; i < periods.length; i++ )
    {
        var periodType = periods[ i ].getElementsByTagName( "periodtype" )[0].firstChild.nodeValue;
		
        if(i ==0 )
        {
            if( periodType == curPeriodType )
            {
                break;
            }
            else
            {
                curPeriodType = periodType;
                clearList( sDateLB );
                clearList( eDateLB );
            }
        }
				
        var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var periodName = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;

        var option1 = document.createElement( "option" );
        option1.value = id;
        option1.text = periodName;
        sDateLB.add( option1, null );
			
        var option2 = document.createElement( "option" );
        option2.value = id;
        option2.text = periodName;
        eDateLB.add( option2, null);
    }
		
}
