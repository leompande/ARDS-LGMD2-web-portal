
// OrgUnit GroupSet Change Function
function orgUnitGroupSetCB()
{
	var orgUnitGroupSetList = document.getElementById( 'orgUnitGroupSetListCB' );
	var orgUnitList = document.getElementById( 'orgUnitListCB' );
	if(document.getElementById( 'ougSetCB' ).checked)
	{
                $('#orgUnitGroupSetListCB').removeAttr('disabled');

		getOrgUnitGroups();		
	}
	else
	{
                $("#orgUnitGroupSetListCB").attr("disabled", "disabled");
	}
	clearList(orgUnitList);
}

// Removes slected orgunits from the Organisation List
function remOUFunction()
{
	var index = document.ChartGenerationForm.orgUnitListCB.options.length;
    var i=0;
    for(i=index-1;i>=0;i--)
    {
    	if(document.ChartGenerationForm.orgUnitListCB.options[i].selected)
    	document.ChartGenerationForm.orgUnitListCB.options[i] = null;
    }
}// remOUFunction end

// DataElement and Its options Change Function
function deSelectionChangeFuntion(evt)
{
    var availableDataElements = document.getElementById("availableDataElements");
    var selectedDataElements = document.getElementById("selectedDataElements");

	clearList(availableDataElements);
	clearList(selectedDataElements);
	
	lockScreen();
	getDataElements();
	unLockScreen();
}

// Category ListBox Change function
function categoryChangeFunction(evt)
{
        selCategory = $("select#categoryLB").val();

	if(selCategory == "period")
	{
                $("#facilityLB").attr("disabled", "disabled");
		var index = document.ChartGenerationForm.orgUnitListCB.options.length;
            for(i=0;i<index;i++)
            {
    		document.ChartGenerationForm.orgUnitListCB.options[0] = null;
            }
	}
	else
	{
            $('#facilityLB').removeAttr('disabled');
	}
}// categoryChangeFunction end
			          
//Facility ListBox Change Function
function facilityChangeFunction(evt)
{
    selFacility = $("select#facilityLB").val();
	if(selFacility == "children")
	{
		var index = document.ChartGenerationForm.orgUnitListCB.options.length;
        for(i=0;i<index;i++)
    	{
    		document.ChartGenerationForm.orgUnitListCB.options[0] = null;
    	}
	}
}// facilityChangeFunction end

// Indicator or Dataelement radio button changed function
function riradioSelection(evt)
{
    
	//selriRadioButton = evt.toElement.value;
  var criteria = $( "input[name='riRadio']:checked" ).val();
  if(criteria == 'dataElementsRadio')
  {
	  document.ChartGenerationForm.indicatorGroupId.disabled = true;
	  document.ChartGenerationForm.availableIndicators.disabled = true;
	  document.ChartGenerationForm.selectedIndicators.disabled = true;
	    
	  document.ChartGenerationForm.dataElementGroupId.disabled = false;
	  document.ChartGenerationForm.availableDataElements.disabled = false;
	  document.ChartGenerationForm.selectedDataElements.disabled = false;
      document.ChartGenerationForm.deSelection.disabled = false;
	    
	  document.ChartGenerationForm.aggDataCB.disabled = false;
  }// if block end
	else
	{
        document.ChartGenerationForm.indicatorGroupId.disabled = false;
	    document.ChartGenerationForm.availableIndicators.disabled = false;
	    document.ChartGenerationForm.selectedIndicators.disabled = false;
	    
	    document.ChartGenerationForm.dataElementGroupId.disabled = true;
	    document.ChartGenerationForm.availableDataElements.disabled = true;
	    document.ChartGenerationForm.selectedDataElements.disabled = true;
        document.ChartGenerationForm.deSelection.disabled = true;
	    
	    document.ChartGenerationForm.aggDataCB.checked = true;
	    document.ChartGenerationForm.aggDataCB.disabled = true;
	}
 // else end
}
// function riradioSelection end

// Selected Button (ie ViewSummary or ViewChart) Function

var tempselectedButtonIN ="";
function selButtonFunction(selButton)
{
	document.ChartGenerationForm.selectedButton.value = selButton;
	tempselectedButtonIN = selButton;
	
	formValidationsIndicator();
	
}

// selButtonFunction end


//Graphical Analysis Form Validations
function formValidations()
{
		
	//var selriRadioButton = document.ChartGenerationForm.riRadio.value;
	var selriRadioButton = $( "input[name='riRadio']:checked" ).val();
	//alert(selriRadioButton);
	//alert(criteria);
	//alert(document.ChartGenerationForm.riRadio.value);
	var selOUListLength = document.ChartGenerationForm.orgUnitListCB.options.length;
	var selDEListSize  = document.ChartGenerationForm.selectedDataElements.options.length;
	var selIndListSize  = document.ChartGenerationForm.selectedIndicators.options.length;
    sDateIndex    = document.ChartGenerationForm.sDateLB.selectedIndex;
    eDateIndex    = document.ChartGenerationForm.eDateLB.selectedIndex;
    category = document.ChartGenerationForm.categoryLB.selectedIndex;
    sDateTxt = document.ChartGenerationForm.sDateLB.options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
    eDateTxt = document.ChartGenerationForm.eDateLB.options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");
    categoryName = document.ChartGenerationForm.categoryLB.options[category].text;
        
    if(selOUListLength <= 0 && document.getElementById( 'ougSetCB' ).checked ) {alert("Please Select OrganisationUnitGroup");return false;}
    else if(selOUListLength <= 0 ) {alert("Please Select OrganisationUnit");return false;}
    else if(selriRadioButton == 'dataElementsRadio' && selDEListSize <= 0)	 {alert("Please Select DataElement(s)");return false;}
    else if(selriRadioButton == 'indicatorsRadio' && selIndListSize <= 0) {alert("Please Select Indicator(s)");return false;}
    else if(sDateIndex < 0) {alert("Please Select Starting Period");return false;}
    else if(eDateIndex < 0) {alert("Please Select Ending Period");return false;}
    else if(category < 0) {alert("Please Select Category");return false;}
    else if(sDate > eDate) {alert("Starting Date is Greater");return false;}

	var k=0;
	if(selriRadioButton == 'dataElementsRadio')
	{
		for(k=0;k<document.ChartGenerationForm.selectedDataElements.options.length;k++)
    	{
    		document.ChartGenerationForm.selectedDataElements.options[k].selected = true;
        } // for loop end
	}
	else
	{
		for(k=0;k<document.ChartGenerationForm.selectedIndicators.options.length;k++)
    	{
    		document.ChartGenerationForm.selectedIndicators.options[k].selected = true;
        } // for loop end
    }
    
    if(document.getElementById( 'ougSetCB' ).checked)
    {
    	if(document.ChartGenerationForm.orgUnitListCB.selectedIndex <= -1) 
    		{alert("Please Select OrganisationUnitGroup");return false;}
    }
    else
    {
    	for(k=0;k<selOUListLength;k++)
    	{
    		document.ChartGenerationForm.orgUnitListCB.options[k].selected = true;
    	}
	}
	
    var sWidth = 850;
	var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
  	return true;
} // formValidations Function End


function getSortedList(chartDisplayOptionValue)
{
	//var url = "getSortedData.action?chartDisplayOption=" + chartDisplayOptionValue;
	/*	
	var request = new Request();
	request.setResponseTypeXML( 'messages' );
	request.setCallbackSuccess( getSortedListReceived );
	//request.send( url );

    var requestString = "getSortedData.action";
    var params = "chartDisplayOption=" + chartDisplayOptionValue;
    request.sendAsPost( params );
    request.send( requestString );
    */
	$.post("getSortedData.action",
			{
				chartDisplayOption : chartDisplayOptionValue
			},
			function (data)
			{
				getSortedListReceived(data);
			},'xml');
}

function getSortedListReceived(xmlObject)
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

