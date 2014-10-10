
// Indicator or Dataelement radio button changed function
function riradioSelection(evt)
{
    var selriRadioButton = $( "input[name='riRadio']:checked" ).val();
       
    if(selriRadioButton == "dataElementsRadio")
    {
		document.ChartGenerationForm.indicatorGroupId.disabled = true;
	    document.ChartGenerationForm.availableIndicators.disabled = true;
	    document.ChartGenerationForm.selectedIndicators.disabled = true;
	    
	    document.ChartGenerationForm.dataElementGroupId.disabled = false;
	    document.ChartGenerationForm.availableDataElements.disabled = false;
	    document.ChartGenerationForm.selectedDataElements.disabled = false;
  	}
    // if block end
	else
	{
		document.ChartGenerationForm.indicatorGroupId.disabled = false;
	    document.ChartGenerationForm.availableIndicators.disabled = false;
	    document.ChartGenerationForm.selectedIndicators.disabled = false;
	    
	    document.ChartGenerationForm.dataElementGroupId.disabled = true;
	    document.ChartGenerationForm.availableDataElements.disabled = true;
	    document.ChartGenerationForm.selectedDataElements.disabled = true;
	}
    // else end
}
	// function riradioSelection end


function selButtonFunction( selButton )
{  	
	var riRadioButtonValue = $( "input[name='riRadio']:checked" ).val();
	
  	if( formValidations() )
  	{
  		if(selButton == "SurveyAnalysis")
  	 	{  	 		
			document.ChartGenerationForm.action = "viewSurveyAnalysisResult.action";
  	 		document.ChartGenerationForm.submit();
  	 	}  	 
  	 	else if ( selButton == "AnnualAnalysis" )
  	 	{
  	 		generateAnnualChart( riRadioButtonValue );
  	 		
  	 		//document.ChartGenerationForm.action = "generateAnnualData.action";
  	 		//document.ChartGenerationForm.submit();  	 	
  	 	}
  	 	else
  	 	{
  	 		//alert( riRadioButtonValue + "--" + selButton );
  	 	}
  	}  	 
}

function generateAnnualChart( riRadioButtonValue )
{
	var url = "generateAnnualData.action?" + getParamsStringBySelected( 'annualPeriodsListCB', 'annualPeriodsListCB' )+ "&" + getParamsStringBySelected( 'monthlyPeriodsListCB', 'monthlyPeriodsListCB' );
	
	//alert(url);
	jQuery( "#contentDiv" ).load( url,
	{
		ouIDTB : getFieldValue( 'ouIDTB' ),
		availableIndicators : getFieldValue( 'availableIndicators' ),
		availableDataElements : getFieldValue( 'availableDataElements' ),
		riRadio : riRadioButtonValue,
		//aggDataCB : isChecked( 'aggDataCB' ),
	} ).dialog( {
		title: 'Annual Graphical Analysis',
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

//Anaul Analysis Form Validations
function formValidations()
{
	var selOuId = document.ChartGenerationForm.ouIDTB.value;
	var availDEListIndex  = document.ChartGenerationForm.availableDataElements.selectedIndex;
	var availIndListIndex = document.ChartGenerationForm.availableIndicators.selectedIndex;
	
    annualPeriodListIndex    = document.ChartGenerationForm.annualPeriodsListCB.selectedIndex;
    monthlyPeriodListIndex    = document.ChartGenerationForm.monthlyPeriodsListCB.selectedIndex;

    if(selOuId == null || selOuId == "") {alert("Please Select OrganisationUnit");return false;}
    else if(selriRadioButton == "dataElementsRadio" && availDEListIndex < 0)	 {alert("Please Select DataElement");return false;}
    else if(selriRadioButton == "indicatorsRadio" && availIndListIndex < 0) {alert("Please Select Indicator");return false;}
    else if(annualPeriodListIndex < 0) {alert("Please Select Year(s)");return false;}
    else if(monthlyPeriodListIndex < 0) {alert("Please Select Month(s)");return false;}
 
   /*
    var sWidth = 850;
	var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
    
    */
  	return true;
  	
} 
// formValidations Function End

//Survey Analysis Form Validations
function formValidationsForSurvey()
{
	var selOuId = document.ChartGenerationForm.ouIDTB.value;
	var availIndListIndex = document.ChartGenerationForm.availableIndicators.selectedIndex;
	
    sDateIndex    = document.ChartGenerationForm.sDateLB.selectedIndex;
    eDateIndex    = document.ChartGenerationForm.eDateLB.selectedIndex;
    sDateTxt = document.ChartGenerationForm.sDateLB.options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
    eDateTxt = document.ChartGenerationForm.eDateLB.options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");


    if(selOuId == null || selOuId == "") {alert("Please Select OrganisationUnit");return false;}
    else if(availIndListIndex < 0) {alert("Please Select Indicator");return false;}
    else if(sDateIndex < 0) {alert("Please Select Starting Period");return false;}
    else if(eDateIndex < 0) {alert("Please Select Ending Period");return false;}
    else if(sDate > eDate) {alert("Starting Date is Greater");return false;}
	
    
    /*
    var sWidth = 850;
	var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
  	return true;
  	*/
    
  	generateChartSurvey();
    
}

// formValidations Function End


function generateChartSurvey()
{

	var url = "generateChartSurvey.action";
	
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
		availableIndicators : getFieldValue( 'availableIndicators' ),
		sDateLB : getFieldValue( 'sDateLB' ),
		eDateLB : getFieldValue( 'eDateLB' ),
	} ).dialog( {
		title: 'Survey Graphical Analysis',
		maximize: true, 
		closable: true,
		modal:true,
		overlay:{ background:'#000000', opacity:0.1 },
		width: 1000,
		height: 800
	} );
}
