// for filter by dataelement group or section
function checkedDataElementGroup()
{
	//var selectedDataElementGroups = document.getElementById("selectedDataElementGroups");
	//clearList(selectedDataElementGroups);
	jQuery('#availableDataElements').find('option').remove();
	jQuery('#selectedServices').find('option').remove();
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
	jQuery('#dataElementGrpDiv2').load('getDataElemetGrp.action',{
		checkValue : chkValue
	},
	function(){
		jQuery('#loaderDiv').hide();
		
	});	
}


function ouListSelection()
{
	var orgUnitSelListCB = document.getElementById( "orgUnitSelListCB" );
	var orgUnitSelListCBVal = orgUnitSelListCB.options[ orgUnitSelListCB.selectedIndex ].value;

	if( orgUnitSelListCBVal == "orgUnitGroupRadio" )
	{
		getOrgUnitGroups();
	}
	else if( orgUnitSelListCBVal == "orgUnitSelectedRadio" )
	{
		ouSelCBChange();
	}
	else
	{
		ouLevelSelected();
	}
}
/*
function ouradioSelection( evt )
{
	var excelItems = byId('ouRadio');
	
	if(excelItems.checked && excelItems.getAttribute('value') == "orgUnitGroupRadio")
	{
		getOrgUnitGroups();
	}
	else if(excelItems.checked && excelItems.getAttribute('value') == "orgUnitSelectedRadio")
	{
		ouSelCBChange();
	}
	else
	{
		ouLevelSelected();
	}
}
*/

function getOrgUnitGroups()
{
	var ouLevelId = document.getElementById( "orgUnitLevelCB" );
	
	$('#orgUnitLevelCB').removeAttr('disabled');
	clearList( ouLevelId );
	$("#orgUnitListCB").attr("disabled", "disabled");
	
	for(var i=0; i < orgUnitGroupIds.length; i++)
	{
        var option = document.createElement("option");
        option.value = orgUnitGroupIds[i];
        option.text = orgUnitGroupNames[i];
        option.title = orgUnitGroupNames[i];
        ouLevelId.add(option, null);
	}
	
	/*
    if( selOrgUnitId != null && selOrgUnitId != "NONE" && selOrgUnitId != "")
    {
        getOUDeatilsForTA( selOrgUnitId );
    }
    */
}


function ouSelCBChange()
{
    var ouListCDId = document.getElementById( "orgUnitListCB" );
    var ouLevelId = document.getElementById( "orgUnitLevelCB" );
	
    //if( ouSelCBId.checked )
    {
        $('#orgUnitListCB').removeAttr('disabled');
		
        clearList( ouLevelId );
         $("#orgUnitLevelCB").attr("disabled", "disabled");
        $('#ViewReport').removeAttr('disabled');
    }
   /*
    else
    {
	$('#orgUnitLevelCB').removeAttr('disabled');
        clearList( ouListCDId );
        $("#orgUnitListCB").attr("disabled", "disabled");
        
    }
	*/
    if( selOrgUnitId != null && selOrgUnitId != "NONE" && selOrgUnitId != "")
    {
        getOUDeatilsForTA( selOrgUnitId );
    }	
}

function ouLevelSelected()
{
    var ouListCDId = document.getElementById( "orgUnitListCB" );
    var ouLevelId = document.getElementById( "orgUnitLevelCB" );

	$('#orgUnitLevelCB').removeAttr('disabled');
    clearList( ouListCDId );
    $("#orgUnitListCB").attr("disabled", "disabled");

    if( selOrgUnitId != null && selOrgUnitId != "NONE" && selOrgUnitId != "")
    {
        getOUDeatilsForTA( selOrgUnitId );
    }
}


function aggPeriodCBChange()
{
    var aggPeriodCBId = document.getElementById( "aggPeriodCB" );
	
    if( aggPeriodCBId.checked )
    {
        var periodTypeList = document.getElementById( "periodTypeLB" );
		
        for( var i =0; i < periodTypeList.options.length; i++ )
        {
            if( periodTypeList.options[i].text == monthlyPeriodTypeName )
            {
                periodTypeList.options[i].selected = true;
                break;
            }
        }
		
        getPeriods();
		
        periodTypeList.disabled = true;
    }
    else
    {
        var periodTypeList = document.getElementById( "periodTypeLB" );
		
        for( var i =0; i < periodTypeList.options.length; i++ )
        {
            if( periodTypeList.options[i].text == monthlyPeriodTypeName )
            {
                periodTypeList.options[i].selected = true;
                break;
            }
        }
		
        getPeriods();
		
        periodTypeList.disabled = false;
    }
}

function moveup( movelistId )
{
    var moveList = document.getElementById( movelistId );
	
    var selIndex = moveList.selectedIndex;
	
    if( selIndex <= 0 ) return;
		
    var tempOptionText = moveList.options[ selIndex ].text;
    var tempOptionValue = moveList.options[ selIndex ].value;
	
    moveList.options[ selIndex ].text = moveList.options[ selIndex -1 ].text;
    moveList.options[ selIndex ].value = moveList.options[ selIndex -1 ].value;
	
    moveList.options[ selIndex - 1 ].text = tempOptionText;
    moveList.options[ selIndex - 1 ].value = tempOptionValue;
	
	
    moveList.options[ selIndex ].selected = false;
    moveList.options[ selIndex - 1 ].selected = true;
		
}

function movedown( movelistId )
{
    var moveList = document.getElementById( movelistId );
	
    var selIndex = moveList.selectedIndex;
	
    if( selIndex >= moveList.options.length-1 ) return;
		
    var tempOptionText = moveList.options[ selIndex ].text;
    var tempOptionValue = moveList.options[ selIndex ].value;
	
    moveList.options[ selIndex ].text = moveList.options[ selIndex + 1 ].text;
    moveList.options[ selIndex ].value = moveList.options[ selIndex + 1 ].value;
	
    moveList.options[ selIndex + 1 ].text = tempOptionText;
    moveList.options[ selIndex + 1 ].value = tempOptionValue;

	
    moveList.options[ selIndex ].selected = false;
    moveList.options[ selIndex + 1 ].selected = true;
}

function moveSelectedServices( fromListId, targetListId1, targetListId2 )
{
    var fromList = document.getElementById( fromListId );
    var targetList1 = document.getElementById( targetListId1 );
    var targetList2 = document.getElementById( targetListId2 );
    
    if ( fromList.selectedIndex == -1 )
    {
        return;
    }

    while ( fromList.selectedIndex > -1 )
    {
        option = fromList.options[ fromList.selectedIndex ];
        var optValue = option.value;
        var partsOfOptVal = new Array();
        partsOfOptVal = optValue.split(":");
        if(partsOfOptVal[0] == "D")
        {
            fromList.remove( fromList.selectedIndex );
            targetList1.add(option, null);
            option.selected = true;
        } 
        else
        {
            fromList.remove( fromList.selectedIndex );
            targetList2.add(option, null);
            option.selected = true;
        }        
    }
}


// DataElement and Its options Change Function
function deSelectionChangeFuntion( listId1, listId2 )
{
    var list1 = document.getElementById( listId1 );
    var list2 = document.getElementById( listId2 );

    clearList( list1 );
	
    for(var i=list2.options.length-1; i >= 0; i--)
    {
        option = list2.options[ i ];
        var optValue = option.value;
        var partsOfOptVal = new Array();
        partsOfOptVal = optValue.split(":");
        if(partsOfOptVal[0] == "D")
        {
            list2.remove( i );
        }
    }
	
    getDataElements();
}
// getDataElements start  
function getDataElements()
{
	var checkValue = document.getElementById("hiddenChkValue").value;
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    //var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    var dataSetSectionId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    var deSelectionList = document.getElementById("deSelection");    
    var deOptionValue = deSelectionList.options[ deSelectionList.selectedIndex ].value;
    
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
        
    	$.post("getDataElementsForTA.action",
		{
			id:dataSetSectionId,
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



/*
function getDataElements()
{
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    //var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    var dataSetSectionId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    var deSelectionList = document.getElementById("deSelection");    
    var deOptionValue = deSelectionList.options[ deSelectionList.selectedIndex ].value;
    
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
}
*/
// getDataElements end           

function getDataElementsReceived( xmlObject )
{
    var availableDataElements = document.getElementById("availableDataElements");
    var selectedDataElements = document.getElementById("selectedServices");

    clearList(availableDataElements);

    var dataElements = xmlObject.getElementsByTagName("dataElement");

    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = "D:"+dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
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
}
// getDataElementsReceived end

function getIndicators()
{
    var indicatorGroupList = document.getElementById( "indicatorGroupId" );
    var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;
	
    if ( indicatorGroupId != null )
    {
        if ( indicatorGroupId != 0 )
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

function getIndicatorsReceived( xmlObject )
{	
    var availableIndicators = document.getElementById( "availableIndicators" );
    var selectedIndicators = document.getElementById( "selectedServices" );
	
    clearList( availableIndicators );
	
    var indicators = xmlObject.getElementsByTagName( "indicator" );
	
    for ( var i = 0; i < indicators.length; i++ )
    {
        var id = "I:"+indicators[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var indicatorName = indicators[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
        if ( listContains( selectedIndicators, id ) == false )
        {
            var option = document.createElement( "option" );
            option.value = id;
            option.text = indicatorName;
            availableIndicators.add( option, null );
        }
    }
}

// getting period List
function getPeriods()
{
    var periodTypeList = document.getElementById( "periodTypeLB" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
	
    var yearLB = document.getElementById("yearLB");
    
    var periodLB = document.getElementById( "periodLB" );
	
    periodLB.disabled = false;
	
    clearList( periodLB );
		
    if( periodTypeId == monthlyPeriodTypeName )
    {
    	getRegularPeriodYear();
    	
    	for( i= 0; i < monthNames.length; i++ )
        {
            periodLB.options[i] = new Option(monthNames[i],i,false,false);
        }
    }
    
    else if( periodTypeId == dailyPeriodTypeName )
    {
    	getRegularPeriodYear();
    	
    	for( i= 0; i < days.length; i++ )
        {
            periodLB.options[i] = new Option(days[i],days[i],false,false);
        }
    }
    else if( periodTypeId == quarterlyPeriodTypeName )
    {
    	getRegularPeriodYear();
    	
    	for( i= 0; i < quarterNames.length; i++ )
        {
            periodLB.options[i] = new Option(quarterNames[i],i,false,false);
        }
    }
    else if( periodTypeId == sixmonthPeriodTypeName )
    {
    	getRegularPeriodYear();
    	
    	for( i= 0; i < halfYearNames.length; i++ )
        {
            periodLB.options[i] = new Option(halfYearNames[i],i,false,false);
        }
    }
    else if( periodTypeId == yearlyPeriodTypeName )
    {
    	getRegularPeriodYear();
    	
    	periodLB.disabled = true;
    }
    
    else if( periodTypeId == weeklyPeriodTypeName )
    {
    	getRegularPeriodYear();
    	
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
	var yearListObj = document.getElementById('yearLB');
	var yearListval = yearListObj.options[ yearListObj.selectedIndex ].value;
	var year = yearListval.split( "-" )[0] ;
	var yearList = "" ;
	
	var yearLB = document.getElementById("yearLB");
    for(k = 0; k < yearLB.options.length; k++)
    {
    	if ( yearLB.options[k].selected == true )
    	{
    		yearList += yearLB.options[k].value + ";" ;
    	}
    }
	
	$.post("getWeeklyPeriod.action",
			{
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

function getPeriodsReceived( xmlObject )
{	
    var startDateList = document.getElementById( "sDateLB" );
    var endDateList = document.getElementById( "eDateLB" );
	
    clearList( startDateList );
    clearList( endDateList );
	
    var periods = xmlObject.getElementsByTagName( "period" );
	
    for ( var i = 0; i < periods.length; i++)
    {
        var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        //var startDate = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;
        //var endDate = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;
        var periodName = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;
		
        var option1 = document.createElement( "option" );
        option1.value = id;
        option1.text = periodName;

        var option2 = document.createElement( "option" );
        option2.value = id;
        option2.text = periodName;
		
        startDateList.add( option1, null );
		
        endDateList.add( option2, null );
    }
}

function getOUDeatilsForTA( orgUnitIds )
{
	showOverlay();
	$.post("getOrgUnitDetails.action",
		{
			orgUnitId : orgUnitIds[0],
			type : 'ta'
		},
		function (data)
		{
			getOUDetailsForTARecevied(data);
		},'xml');
}

function getOUDetailsForTARecevied(xmlObject)
{
    var ouListCDId = document.getElementById( "orgUnitListCB" );
    var ouLevelId = document.getElementById( "orgUnitLevelCB" );
    //var ouRadioVal = $( "input[name='ouRadio']:checked" ).val();
	var orgUnitSelListCB = document.getElementById( "orgUnitSelListCB" );
	var ouRadioVal = orgUnitSelListCB.options[ orgUnitSelListCB.selectedIndex ].value;

    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        var ouLevel = orgUnits[ i ].getElementsByTagName("level")[0].firstChild.nodeValue;
        var maxOULevel = orgUnits[ i ].getElementsByTagName("maxoulevel")[0].firstChild.nodeValue;
         
        if( ouRadioVal == "orgUnitSelectedRadio"  )
        {
            ouListCDId.disabled = false;
            clearList( ouLevelId );
            document.getElementById( "ViewReport" ).disabled = false;
    		
            clearList( ouLevelId );
            ouLevelId.disabled = true;
            
            var flag = 0;
            for(var i=0; i < ouListCDId.options.length; i++)
            {
                if( id == ouListCDId.options[i].value ) 
                {
                	flag = 1;
                	break;
                }
            }
            if( flag == 0 )
            {
            	ouListCDId.options[ouListCDId.options.length] = new Option(orgUnitName, id, false, false);
            }
        }
        else 
        {
            clearList( ouListCDId );
    		
            ouListCDId.options[ouListCDId.options.length] = new Option(orgUnitName,id,false,false);
            //var selouRadioButton = $( "input[name='ouRadio']:checked" ).val();
            if( ouRadioVal == "orgUnitGroupRadio" )
            {
            	getOrgUnitGroups();
            }
            else
            {
                getorgUnitLevels( ouLevel, maxOULevel );
            }            
        }
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


function formValidations()
{
    var selectedServices = document.getElementById("selectedServices");
    var orgUnitListCB = document.getElementById("orgUnitListCB");
    var selOUListLength = document.tabularAnalysisForm.orgUnitListCB.options.length;
    var orgUnitLevelCB = document.getElementById("orgUnitLevelCB");
    var yearLB = document.getElementById("yearLB");
    var periodLB = document.getElementById("periodLB");
    var ouSelCB = document.getElementById("ouSelCB");
    var aggPeriodCB = document.getElementById("aggPeriodCB");
    var periodTypeList = document.getElementById( "periodTypeLB" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
	var orgUnitSelListCB = document.getElementById( "orgUnitSelListCB" );
	var ouRadioVal = orgUnitSelListCB.options[ orgUnitSelListCB.selectedIndex ].value;

    var k=0;
    if( selectedServices.options.length <= 0 ) 
    {
        alert("Please select DataElement/Indicator(s)");
        return false;
    }
	
    if( periodTypeId == yearlyPeriodTypeName )
    {
        if( yearLB.selectedIndex < 0 ) 
        {
            alert("Please select Year(s)");
            return false;
        }
    }
    else
    {
        if( yearLB.selectedIndex < 0 ) 
        {
            alert("Please select Year(s)");
            return false;
        }
        if( periodLB.selectedIndex < 0 ) 
        {
            alert("Please select Period(s)");
            return false;
        }
    }
	
    if( ouRadioVal == "orgUnitSelectedRadio" )
    {
    	if( selOUListLength <= 0 )
        {
            alert( "Please select OrgUnit(s)" );
            return false;
        }
    }
    else if( ouRadioVal == "orgUnitGroupRadio" ) 
    { 
    	if( orgUnitLevelCB.selectedIndex < 0 ) 
    	{
            alert( "Please select OrgUnitGroup" );
            return false;
        }
    }
    else if( ouRadioVal == "orgUnitLevelRadio" )
    {
	   if(orgUnitLevelCB.selectedIndex < 0  ) 
	   {
           alert( "Please select OrgUnitLevel" );
           return false;
       }
    }
	   
    orgUnitListCB.disabled = false;
    for(k = 0; k < selectedServices.options.length; k++)
    {
        selectedServices.options[k].selected = true;
    }
	
    for(k = 0; k < orgUnitListCB.options.length; k++)
    {
        orgUnitListCB.options[k].selected = true;
    }

    return true;
} // formValidations Function End

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

//filter available indicators list
function filterAvailableIndicators()
{
	var filter = document.getElementById( 'availableIndicatorsFilter' ).value;
    var list = document.getElementById( 'availableIndicators' );
    
    list.options.length = 0;
    
    var selIndListId = document.getElementById( 'selectedServices' );
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
    
    var selDeListId = document.getElementById( 'selectedServices' );
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
