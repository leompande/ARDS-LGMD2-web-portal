
function getOUDeatilsForGADataElements( orgUnitIds )
{
	document.getElementById( "ougGroupSetCB" ).disabled = false;
	document.getElementById( "orgUnitGroupList" ).disabled = false;
	document.getElementById( "categoryLB" ).disabled = false;
	document.getElementById( "periodTypeLB" ).disabled = false;
	document.getElementById( "yearLB" ).disabled = false;
	document.getElementById( "periodLB" ).disabled = false;

	jQuery.postJSON("getOrgUnitName.action",{
  	  id : orgUnitIds[0]
   }, function( json ){

		var ouListCBId = document.getElementById( "orgUnitListCB" );
		var categoryLB = document.getElementById( "categoryLB");
		var categoryIndex = categoryLB.selectedIndex;
	    var index = 0;
	    var orgUnitId = json.organisationUnit.id;
	    var orgUnitName = json.organisationUnit.name;

    	currentOrgUnitId = orgUnitId;
        currentOrgUnitName = orgUnitName;
        
        if( categoryLB.options[categoryIndex].value == "period" || categoryLB.options[categoryIndex].value == "children" )
        {
            for( i = 0; i < ouListCBId.options.length; i++ )
            {
                ouListCBId.options[0] = null;
            }
            ouListCBId.options[0] = new Option( orgUnitName, orgUnitId, false, false );
        }
        else if( categoryLB.options[categoryIndex].value == "random" && document.getElementById( 'ougGroupSetCB' ).checked )
        {
            for( i = 0; i < ouListCBId.options.length; i++ )
            {
            	ouListCBId.options[0] = null;
            }
            ouListCBId.options[0] = new Option( orgUnitName, orgUnitId, false, false );
        }
        else
        {
            for( i = 0; i < ouListCBId.options.length; i++ )
            {
                if( orgUnitId == ouListCBId.options[i].value ) return;
            }
            ouListCBId.options[ ouListCBId.options.length ] = new Option( orgUnitName, orgUnitId, false, false );
        }
   });
}

// function for getting periods
function getPeriods()
{
	var periodTypeList = document.getElementById("periodTypeLB");
	var periodTypeId = periodTypeList.options[periodTypeList.selectedIndex].value;

	var periodLB = document.getElementById("periodLB");
	
	var yearLB = document.getElementById("yearLB");
	 
	periodLB.disabled = false;

	clearList(periodLB);
	//clearList(yearLB);

	if (periodTypeId == monthlyPeriodTypeName) 
	{
		getRegularPeriodYear();
		for (i = 0; i < monthNames.length; i++) 
		{
			periodLB.options[i] = new Option(monthNames[i], i, false, false);
		}
	}
    else if( periodTypeId == dailyPeriodTypeName )
    {
    	// alert( monthDays.length );
    	//alert( days.length );
    	getRegularPeriodYear();
		
    	for( i= 0; i < days.length; i++ )
        {
            periodLB.options[i] = new Option(days[i],days[i],false,false);
        }
    }
	else if (periodTypeId == quarterlyPeriodTypeName)
	{
		getRegularPeriodYear();
		
		for (i = 0; i < quarterNames.length; i++) 
		{
			periodLB.options[i] = new Option(quarterNames[i], i, false, false);
		}
	} 
	else if (periodTypeId == sixmonthPeriodTypeName) 
	{
		getRegularPeriodYear();
		
		for (i = 0; i < halfYearNames.length; i++)
		{
			periodLB.options[i] = new Option(halfYearNames[i], i, false, false);
		}
	} 
	else if (periodTypeId == yearlyPeriodTypeName) 
	{
		getRegularPeriodYear();
		
		periodLB.disabled = true;
	}
    else if( periodTypeId == weeklyPeriodTypeName )
    {
    	//getRegularPeriodYear();
    	
        if( yearLB.selectedIndex < 0 ) 
        {
            alert("Please select Year(s) First");
            return false;
        }
        else
        {
        	getWeeks();
        }

    }
    else if( periodTypeId == financialAprilPeriodType )
    {
    	//getFinacialYear();
    	getFinacialPeriodYear();
    	
		for (i = 0; i < financialMonthNames.length; i++)
		{
			periodLB.options[i] = new Option(financialMonthNames[i], i, false, false);
		}
    }
	
}

function getRegularPeriodYear()
{
	var yearLB = document.getElementById("yearLB");
	clearList(yearLB);
	
	for (i = 0; i < regularYear.length; i++)
	{
		//yearLB.options[i] = new Option(regularYear[i], i, false, false);
		
        var option = document.createElement( "option" );
        option.value = regularYear[i];
        option.text = regularYear[i];
        option.title = regularYear[i];
        yearLB.add( option, null );
	}
	
}

function getFinacialPeriodYear()
{
	var yearLB = document.getElementById("yearLB");
	clearList(yearLB);
	
	for (i = 0; i < financialYear.length; i++)
	{
	    //yearLB.options[i] = new Option(regularYear[i], i, false, false);
		
        var option = document.createElement( "option" );
        option.value = financialYear[i];
        option.text = financialYear[i];
        option.title = financialYear[i];
        yearLB.add( option, null );
	}
	
}


// function for getting periods ends

//getting weekly Period
function getWeeklyPeriod()
{
    var periodTypeList = document.getElementById( "periodTypeLB" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
    
    if( periodTypeId == weeklyPeriodTypeName )
    {
    	getWeeks();
    }
    
}
//get week period Ajax calling
function getWeeks()
{
	//var periodTypeName = weeklyPeriodTypeName;
	var yearListObj = document.getElementById('yearLB');
	var yearListval = yearListObj.options[ yearListObj.selectedIndex ].value;
	//alert(yearListval); 
	var year = yearListval.split( "-" )[0] ;
	var yearList = "" ;
	
	var yearLB = document.getElementById("yearLB");
    for(k = 0; k < yearLB.options.length; k++)
    {
    	if ( yearLB.options[k].selected == true )
    	{
    		yearList += yearLB.options[k].value + ";" ;
    	}
    	//yearLB.add[yearLB.selectedIndex];
    }
   
    	// alert( "Year List is : " +yearList );
	
	$.post("getWeeklyPeriod.action",
			{
			 	//periodTypeName:weeklyPeriodTypeName,
				yearList:yearList
			},
			function (data)
			{
				getWeeklyPeriodReceived(data);
			},'xml');
}
// week rang received
function getWeeklyPeriodReceived( xmlObject )
{	
	var periodList = document.getElementById( "periodLB" );
	
	clearList( periodList );
	
	var weeklyperiodList = xmlObject.getElementsByTagName( "weeklyPeriod" );
	
	for ( var i = 0; i < weeklyperiodList.length; i++ )
	{
	    var weeklyPeriodName = weeklyperiodList[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
	        var option = document.createElement( "option" );
	        option.value = weeklyPeriodName;
	        option.text = weeklyPeriodName;
	        option.title = weeklyPeriodName;
	        periodList.add( option, null );
	}
}
/*
function trim(str) {
    return str.replace(/^\s+|\s+$/g,'');
} 

function trim(str) {
    while (str.substring(0, 1) == '') {
        str = str.substring(1, str.length);
    }
    while (str.substring(str.length - 1, str.length) == '') {
        str = str.substring(0, str.length - 1);
    }
    //alert( str );
    return str;
} 
*/

//get Financial Year  calling
function getFinacialYear()
{
	$.post("getFinacialYear.action",
			{
			 
			},
			function (data)
			{
				getFinacialYearReceived(data);
			},'xml');
}

//week rang received
function getFinacialYearReceived( xmlObject )
{	
	//var periodList = document.getElementById( "periodLB" );
	var yearLBList = document.getElementById("yearLB");
	clearList( yearLBList );
	
	var finacialYearList = xmlObject.getElementsByTagName( "finacialYear" );
	
	for ( var i = 0; i < finacialYearList.length; i++ )
	{
	    var finacialYearName = finacialYearList[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
	        var option = document.createElement( "option" );
	        option.value = finacialYearName;
	        option.text = finacialYearName;
	        option.title = finacialYearName;
	        yearLBList.add( option, null );
	}
}	



// OrgUnit GroupSet Change Function
/*
function orgUnitGroupSetCB1() {
	var orgUnitGroupSetList = document.getElementById('orgUnitGroupSetListCB');
	var orgUnitList = document.getElementById('orgUnitListCB');
	if (document.getElementById('ougSetCB').checked) {
		$('#orgUnitGroupSetListCB').removeAttr('disabled');

		getOrgUnitGroups();
	} else {
		$("#orgUnitGroupSetListCB").attr("disabled", "disabled");
	}
	clearList(orgUnitList);
}
*/

function getOrgUnitGroupsDataElements() 
{
	var checked = byId('ougGroupSetCB').checked;
	clearListById('orgUnitGroupList');
	clearListById('orgUnitListCB');
	
	document.ChartGenerationForm.orgUnitListCB.options[0] = new Option(currentOrgUnitName,currentOrgUnitId,false,false);

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

//Category ListBox Change function
function categoryChangeFunction1(evt)
{
    selCategory = $("select#categoryLB").val();
    
   // var currentOrgUnitName = "";
	//var currentOrgUnitId = "";
	if(selCategory == "period" || selCategory == "children" )
	{
		clearListById('orgUnitListCB');
		document.ChartGenerationForm.orgUnitListCB.options[0] = new Option( currentOrgUnitName,currentOrgUnitId,false,false);
	}
	else
	{
           	// $('#facilityLB').removeAttr('disabled');
	}
} 	
// categoryChangeFunction end
			          
//Removes slected orgunits from the Organisation List
function remOUFunction()
{
	var index = document.ChartGenerationForm.orgUnitListCB.options.length;
	var i = 0;
	for (i = index - 1; i >= 0; i--)
	{
		if (document.ChartGenerationForm.orgUnitListCB.options[i].selected)
			document.ChartGenerationForm.orgUnitListCB.options[i] = null;
	}
}	
	// remOUFunction end

// singleSelectionOption OrgUnitGroup
function selectSingleOptionOrgUnitGroup()
{
	 //alert("inside single selection");
	var categoryObj = document.getElementById( 'categoryLB' );// view by
    var categoryVal = categoryObj.options[ categoryObj.selectedIndex ].value;
	
    var orgGroupObj = document.getElementById( 'orgUnitGroupList' ); // org unit group
    var orgGroupVal = orgGroupObj.options[ orgGroupObj.selectedIndex ].value;
    
//    var categoryObj = document.getElementById( 'categoryLB' );
//    var categoryVal = categoryObj.options[ categoryObj.selectedIndex ].value;
	
    if( document.getElementById( 'ougGroupSetCB' ).checked &&  categoryVal == "period"  )
    {
        var orgUnitGroupListObj = document.getElementById('orgUnitGroupList');
	
        for( var i = 0; i < orgUnitGroupListObj.length; i++ )
        {
            if( i != orgUnitGroupListObj.selectedIndex )
            	orgUnitGroupListObj.options[i].selected = false;
        }
    }
}

//  singleSelectionOption OrgUnit
/*
function selectSingleOrgUnitOption()
{
    var orgUnitObj = document.getElementById( 'categoryLB' ); //view by 
    var orgUnitVal = orgUnitObj.options[ orgUnitObj.selectedIndex ].value;
	
   // var categoryObj = document.getElementById( 'orgUnitGroup' );// org unit group
   // var categoryVal = categoryObj.options[ categoryObj.selectedIndex ].value;
	
    if( document.getElementById( 'ougGroupSetCB' ).checked && ( orgUnitVal == "random" ))
    {
        var orgUnitListObj = document.getElementById('orgUnitListCB');
	
        for( var i = 0; i < orgUnitListObj.length; i++ )
        {
            if( i != orgUnitListObj.selectedIndex )
                orgUnitListObj.options[i].selected = false;
        }
    }
}
*/

// Selected Button (ie ViewSummary or ViewChart) Function
var tempselectedButtonDE ="";
function selButtonFunction1(selButton)
{
	document.ChartGenerationForm.selectedButton.value = selButton;
	tempselectedButtonDE = selButton;
	
	formValidationsDataElement();
	
	
}


// selButtonFunction end


//Graphical Analysis Form Validations
function formValidationsDataElement()
{
	//var selectedServices = document.getElementById("selectedServices");

	var selOUListLength = document.ChartGenerationForm.orgUnitListCB.options.length;//alert(selOUListLength);
	var selDEListSize  = document.ChartGenerationForm.selectedDataElements.options.length;//alert(selDEListSize);
	
	var orgUnitListCB = document.getElementById("orgUnitListCB");
	var selectedDataElements = document.getElementById("selectedDataElements");
	
	var orgUnitGroupCB = document.getElementById("orgUnitGroupList");
	
	var selOUGroupListLength = document.ChartGenerationForm.orgUnitGroupList.options.length;
	
	var selyearLB = document.getElementById("yearLB");
    var selperiodLB = document.getElementById("periodLB");
  
    var periodTypeList = document.getElementById( "periodTypeLB" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;//alert(periodTypeId);
	
    var k = 0;

    if(  selDEListSize <= 0 ) 
		{
	        alert( "Please Select DataElement(s)" );
	        return false;
		}
    
    else if(  selOUListLength <= 0 ) 
		{
	        alert( "Please Select OrganisationUnit" );
	        return false;
		}
   
    else if(document.getElementById( 'ougGroupSetCB' ).checked && orgUnitGroupCB.selectedIndex < 0 ) 
    	{
    		alert( "Please select OrgUnitGroup" );
    		return false;
    	/*if( orgUnitGroupCB.selectedIndex < 0 ) 
	    	{
	            alert( "Please select OrgUnitGroup" );
	            
	        }*/
    	}	
    else if( periodTypeId == yearlyPeriodTypeName )
	   {
	       if( selyearLB.selectedIndex < 0 ) 
	       {
	           alert("Please select Year(s)");
	           return false;
	       }
	   }
   else
   {
       if( selyearLB.selectedIndex < 0 ) 
       {
           alert("Please select Year(s)");
           return false;
       }
       if( selperiodLB.selectedIndex < 0 ) 
       {
           alert("Please select Period(s)");
           return false;
       }
   }
 
    /*
	if( selDEListSize > 0 )
	{
		for(k=0;k<document.ChartGenerationForm.selectedDataElements.options.length;k++)
    	{
    		document.ChartGenerationForm.selectedDataElements.options[k].selected = true;
        } 
	}

    if( selOUListLength > 0 )
    {
    	for(k = 0; k < orgUnitListCB.options.length; k++)
        {
    		orgUnitListCB.options[k].selected = true;
        }
    }
  
    var sWidth = 1000;
	var sHeight = 1000;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindowDataElement','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
  	return true;
  	*/
    
  	generateChartDataElement();
} 

// formValidations Function DataElements End 


function generateChartDataElement()
{

	var url = "generateChartDataElement.action?" + getParamString( 'selectedDataElements', 'selectedDataElements' ) + "&"
	          + getParamsStringBySelected( 'orgUnitGroupList', 'orgUnitGroupList' )+ "&" + getParamString( 'orgUnitListCB', 'orgUnitListCB' )+ "&"
	          + getParamsStringBySelected( 'yearLB', 'yearLB' )+ "&" + getParamsStringBySelected( 'periodLB', 'periodLB' ) ;
	
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
		deSelection : getFieldValue( 'deSelection' ),
		categoryLB : getFieldValue( 'categoryLB' ),
		periodTypeLB : getFieldValue( 'periodTypeLB' ),
		ougGroupSetCB : isChecked( 'ougGroupSetCB' ),
		aggDataCB : isChecked( 'aggDataCB' ),
		selectedButton : tempselectedButtonDE,
	} ).dialog( {
		title: 'Data Element Wise Graphical Analysis',
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
 
/*
 
function generateChartDataElement()
{
    
	   var yearLBString = "";
       var listYearLB = jQuery( "select[id=yearLB] option:selected" );
       listYearLB.each( function( i, item ){
               yearLBString += "yearLB=" + item.value;
               yearLBString += ( i < listYearLB.length - 1 ) ? "&" : "";
       });	

       var periodLBString = "";
       var listPeriodLB = jQuery( "select[id=periodLB] option:selected" );
       listPeriodLB.each( function( i, item ){
    	   periodLBString += "periodLB=" + item.value;
    	   periodLBString += ( i < listPeriodLB.length - 1 ) ? "&" : "";
       });	
       

       
       var listValue = "";

       // Clear the list
       var availableList = document.getElementById( 'availableList' );

       availableList.options.length = 0;

       for ( var i = 0; i < selectedList.options.length; ++i)
       {
     	  listValue+='&selectedIndicators=' + selectedList.options[i].value;
       }       
       
jQuery( "#contentDiv" ).load('generateChartDataElement.action?' +
       getQueryStringFromList ('selectedDataElements') + "&"
       + getQueryStringFromList ('orgUnitListCB') + "&"
       + getQueryStringFromList ('orgUnitGroupList') + "&"
       + yearLBString  + "&" + periodLBString,
             {
                     dataElementGroupId: jQuery('select[id=dataElementGroupId ] option:selected').val(),
                     deSelection:   jQuery('select[id=deSelection ] option:selected').val(),
                     periodTypeLB : jQuery('select[id=periodTypeLB ] option:selected').val(),
                     categoryLB : jQuery('select[id=categoryLB ] option:selected').val(),
                     ougGroupSetCB : isChecked( 'ougGroupSetCB' ),
             		 aggDataCB : isChecked( 'aggDataCB' ),
                     //selectedDataElements:
             } ).dialog({
                     title: "Load jQuery.diaglog insteads of popup window",
                     maximize: true,
                     closable: true,
                     modal:true,
                     overlay:{background:'#000000', opacity:0.1},
                     width: 1000,
                     height: 1000
             });

}

*/


//Graphical Analysis Form Validation Indicators
function formValidationsIndicator()
{
	//var selectedServices = document.getElementById("selectedServices");

	var selOUListLength = document.ChartGenerationForm.orgUnitListCB.options.length;//alert(selOUListLength);
	var selIndicatorsListSize  = document.ChartGenerationForm.selectedIndicators.options.length;//alert(selDEListSize);
	
	var orgUnitListCB = document.getElementById("orgUnitListCB");
	var selectedIndicators = document.getElementById("selectedIndicators");
	
	var orgUnitGroupCB = document.getElementById("orgUnitGroupList");
	
	var selOUGroupListLength = document.ChartGenerationForm.orgUnitGroupList.options.length;
	
	var selyearLB = document.getElementById("yearLB");
    var selperiodLB = document.getElementById("periodLB");
  
    var periodTypeList = document.getElementById( "periodTypeLB" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;//alert(periodTypeId);
	
    var k = 0;

    if(  selIndicatorsListSize <= 0 ) 
		{
	        alert( "Please Select Indicator(s)" );
	        return false;
		}
    
    else if(  selOUListLength <= 0 ) 
		{
	        alert( "Please Select OrganisationUnit" );
	        return false;
		}
   
    else if(document.getElementById( 'ougGroupSetCB' ).checked && orgUnitGroupCB.selectedIndex < 0 ) 
    	{
    		alert( "Please select OrgUnitGroup" );
    		return false;
    	/*if( orgUnitGroupCB.selectedIndex < 0 ) 
	    	{
	            alert( "Please select OrgUnitGroup" );
	            
	        }*/
    	}	
    else if( periodTypeId == yearlyPeriodTypeName )
	   {
	       if( selyearLB.selectedIndex < 0 ) 
	       {
	           alert("Please select Year(s)");
	           return false;
	       }
	   }
   else
   {
       if( selyearLB.selectedIndex < 0 ) 
       {
           alert("Please select Year(s)");
           return false;
       }
       if( selperiodLB.selectedIndex < 0 ) 
       {
           alert("Please select Period(s)");
           return false;
       }
   }
  
 /*
	if( selIndicatorsListSize > 0 )
	{
		for(k=0;k<document.ChartGenerationForm.selectedIndicators.options.length;k++)
    	{
    		document.ChartGenerationForm.selectedIndicators.options[k].selected = true;
        } 
	}

    if( selOUListLength > 0 )
    {
    	for(k = 0; k < orgUnitListCB.options.length; k++)
        {
    		orgUnitListCB.options[k].selected = true;
        }
    }
  
    var sWidth = 1000;
	var sHeight = 1000;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindowIndicator','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
  	
  	return true;
  	*/
  	generateChartIndicator();
} 


function generateChartIndicator()
{

	var url = "generateChartIndicator.action?" + getParamString( 'selectedIndicators', 'selectedIndicators' ) + "&"
	          + getParamsStringBySelected( 'orgUnitGroupList', 'orgUnitGroupList' )+ "&" + getParamString( 'orgUnitListCB', 'orgUnitListCB' )+ "&"
	          + getParamsStringBySelected( 'yearLB', 'yearLB' )+ "&" + getParamsStringBySelected( 'periodLB', 'periodLB' ) ;
	
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
		categoryLB : getFieldValue( 'categoryLB' ),
		periodTypeLB : getFieldValue( 'periodTypeLB' ),
		ougGroupSetCB : isChecked( 'ougGroupSetCB' ),
		aggDataCB : isChecked( 'aggDataCB' ),
		selectedButton : tempselectedButtonIN,
	} ).dialog( {
		title: 'Indicator Wise Graphical Analysis',
		maximize: true, 
		closable: true,
		modal:true,
		overlay:{ background:'#000000', opacity:0.1 },
		width: 1000,
		height: 800
	} );
}


// formValidations Function Indicators End

 
//filter available indicators list
function filterAvailableIndicators()
{
	var filter = document.getElementById( 'availableIndicatorsFilter' ).value;
    var list = document.getElementById( 'availableIndicators' );
    
    list.options.length = 0;
    
    var selIndListId = document.getElementById( 'selectedIndicators' );
    var selIndLength = selIndListId.options.length;
    
    for ( var id in availableIndicators )
    {
    	//alert( "id : " + id );
        var value = availableIndicators[id];
        
        var flag = 1;
        for( var i =0 ; i<selIndLength; i++ )
        {
        	//alert( selIndListId.options[i].text );
        	//alert( selIndListId.options[i].value );
        	if( id == selIndListId.options[i].value )
        		{
        		flag =2;
        		//alert("aaaa");
        		break;
        		}
        }
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 && (flag == 1) )
        {
            list.add( new Option( value, id ), null );
        }
        //alert( flag );
    }
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
        for( var i =0 ; i<selDeLength; i++ )
        {
        	if( id == selDeListId.options[i].value )
        		{
        		flag =2;
        		//alert("aaaa");
        		break;
        		}
        }
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 && (flag == 1) )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

//Chart Display Option change for sorting
function getSortedListIndicator(chartDisplayOptionValue)
{
    //var url = "getSortedData.action?chartDisplayOption=" + chartDisplayOptionValue;
    /*
    var request = new Request();
    request.setResponseTypeXML( 'messages' );
    request.setCallbackSuccess( getSortedListIndicatorReceived );
    //request.send( url );
    
   	alert("inside sorting function");
    var requestString = "getSortedIndicatorData.action";
    var params = "chartDisplayOption=" + chartDisplayOptionValue;
    request.sendAsPost( params );
    request.send( requestString );
    */
    //alert("inside sorting function");
	$.post("getSortedIndicatorData.action",
			{
				chartDisplayOption : chartDisplayOptionValue
			},
			function (data)
			{
				getSortedListIndicatorReceived(data);
			},'xml');
    
    
}

function getSortedListIndicatorReceived(xmlObject)
{   
    var messages = xmlObject.getElementsByTagName("message");
    document.getElementById("headingInfoId").innerHTML = "";
    document.getElementById("testId").value = "";
    
    for ( var i = 0; i < messages.length; i++ )
    {
        var hinfo = messages[ i ].firstChild.nodeValue;
        //document.getElementById("headingInfoId").innerHTML += hinfo;
        document.getElementById("testId").value += hinfo;
    }
    
    document.getElementById("headingInfoId").innerHTML = document.getElementById("testId").value;           
}

function getSortedListDataElement(chartDisplayOptionValue)
{
	//var url = "getSortedData.action?chartDisplayOption=" + chartDisplayOptionValue;
    /*    
    var request = new Request();
    request.setResponseTypeXML( 'messages' );
    request.setCallbackSuccess( getSortedListReceived );
    //request.send( url );

    var requestString = "getSortedDataElement.action";
    var params = "chartDisplayOption=" + chartDisplayOptionValue;
    request.sendAsPost( params );
    request.send( requestString );
    */
    //alert("inside sorting function");
	$.post("getSortedDataElement.action",
			{
				chartDisplayOption : chartDisplayOptionValue
			},
			function (data)
			{
				getSortedListDataElementReceived(data);
			},'xml');

}

function getSortedListDataElementReceived(xmlObject)
{   
    var messages = xmlObject.getElementsByTagName("message");
    document.getElementById("headingInfoId").innerHTML = "";
    document.getElementById("testId").value = "";
    
    for ( var i = 0; i < messages.length; i++ )
    {
        var hinfo = messages[ i ].firstChild.nodeValue;
        //document.getElementById("headingInfoId").innerHTML += hinfo;
        document.getElementById("testId").value += hinfo;
    }
    
    document.getElementById("headingInfoId").innerHTML = document.getElementById("testId").value;           
}


