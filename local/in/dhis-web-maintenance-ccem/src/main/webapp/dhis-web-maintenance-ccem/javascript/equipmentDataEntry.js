
// -----------------------------------------------------------------------------
// Periods Selection
// -----------------------------------------------------------------------------

//function dataSetSelected()
//function getPeriods( periodType, periodId, periodId, timespan )
function getPeriods()
{
	$( '#dataEntryFormDiv' ).html( '' );
	
	$( '#selectedPeriodId' ).removeAttr( 'disabled' );
    $( '#prevButton' ).removeAttr( 'disabled' );
    $( '#nextButton' ).removeAttr( 'disabled' );
    
    //hideById('dataEntryFormDiv');
    //setInnerHTML('dataEntryFormDiv', '');
    
    
    var dataSetId = $( '#selectedDataSetId' ).val();
    //alert( dataSetId  );
    //var dataSetPeriod = dataSetId.split(":");
	
	//var dataSetId = dataSetPeriod[0];
	//var periodTypeId = dataSetPeriod[1];

	// var periodId = $( '#selectedPeriodId' ).val();
	
	$( "#periodId" ).removeAttr( "disabled" );
	
	var url = 'loadPeriods.action?dataSetId=' + dataSetId;
	
	var list = document.getElementById( 'selectedPeriodId' );
		
	clearList( list );
	    
	addOptionToList( list, '-1', '[ Select ]' );
	
    $.getJSON( url, function( json ) {
    	for ( i in json.periods ) {
    		//addOptionToList( list, i, json.periods[i].name );
    		addOptionToList( list, json.periods[i].externalId, json.periods[i].name );
    	}
    } );
	
		
	//var periodId = "";
	
	//alert( periodId );
	//var periodId = $( '#periodId' );
    
    
	//getAvailablePeriodsTemp( periodTypeId, periodId, periodId, timespan );
   
	//var periodType = dataSets[dataSetId].periodType;
   
	/*
	var periodType = "monthly";
    var periods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
    periods = periodTypeFactory.filterFuturePeriods( periods );

    if ( dataSetId && dataSetId != -1 )
    {
        clearListById( 'selectedPeriodId' );

        addOptionById( 'selectedPeriodId', '-1', '[ ' + i18n_select_period + ' ]' );

        for ( i in periods )
        {
            addOptionById( 'selectedPeriodId', periods[i].id, periods[i].name );
        }

        var previousPeriodType = currentDataSetId ? dataSets[currentDataSetId].periodType : null;

        if ( periodId && periodId != -1 && previousPeriodType && previousPeriodType == periodType )
        {
            showLoader();
            $( '#selectedPeriodId' ).val( periodId );
            loadForm( dataSetId );
        }
        else
        {
            clearEntryForm();
        }

        currentDataSetId = dataSetId;
    }
    */
}
/*
function getAvailablePeriodsPre( selectedDataSetId, periodId, periodId, timespan )
{
	var dataSetId = $( '#selectedDataSetId' ).val();
    
    var dataSetPeriod = dataSetId.split(":");
	
	var dataSetId = dataSetPeriod[0];
	var periodTypeId = dataSetPeriod[1];

	getAvailablePeriodsTemp( periodTypeId, periodId, periodId, timespan );
 
}

function getAvailablePeriodsNext( selectedDataSetId, periodId, periodId, timespan )
{
	var dataSetId = $( '#selectedDataSetId' ).val();

	var dataSetPeriod = dataSetId.split(":");
	
	var dataSetId = dataSetPeriod[0];
	var periodTypeId = dataSetPeriod[1];

	getAvailablePeriodsTemp( periodTypeId, periodId, periodId, timespan );
 
}
*/
// next and pre periods
function getAvailablePeriodsTemp( availablePeriodsId, selectedPeriodsId, year )
{	
	$( '#dataEntryFormDiv' ).html( '' );
	//clearEntryForm();
	var dataSetId = $( '#selectedDataSetId' ).val();
    
    //var dataSetPeriod = dataSetId.split(":");
	
	//var dataSetId = dataSetPeriod[0];
	//var periodTypeId = dataSetPeriod[1];
	
	/*
	$.getJSON( "getAvailableNextPrePeriods.action", {
		"dataSetId": dataSetId ,
		"year": year },
		function( json ) {
			var availableList = document.getElementById( availablePeriodsId );
			var selectedList = document.getElementById( selectedPeriodsId );
			clearList( availableList );
			
			//addOptionById( 'availableList', '-1', '[ ' + i18n_select_period + ' ]' );
			for ( var i = 0; i < json.periods.length; i++ )
			{
				if ( listContains( selectedList, json.periods[i].externalId ) == false )
				{
					addValue( availableList, json.periods[i].name, json.periods[i].externalId );
				}
			}			
		} );

	loadDataEntryForm();
	*/
	
	var availableList = document.getElementById( availablePeriodsId );
	var selectedList = document.getElementById( selectedPeriodsId );
	
	clearList( selectedList );
	
	addOptionToList( selectedList, '-1', '[ Select ]' );
	
	$.getJSON( "getAvailableNextPrePeriods.action", {
		"dataSetId": dataSetId ,
		"year": year },
		function( json ) {
			
			for ( i in json.periods ) {
	    		//addOptionToList( list, i, json.periods[i].name );
	    		addOptionToList( selectedList, json.periods[i].externalId, json.periods[i].name );
	    	}
			
		} );
}


/*
function getAvailablePeriods( periodTypeId, availablePeriodsId, selectedPeriodsId, year )
{
	$.getJSON( "../dhis-web-commons-ajax-json/getAvailablePeriods.action", {
		"periodType": $( "#" + periodTypeId ).val(),
		"year": year },
		function( json ) {
			var availableList = document.getElementById( availablePeriodsId );
			var selectedList = document.getElementById( selectedPeriodsId );
			clearList( availableList );
			
			for ( var i = 0; i < json.periods.length; i++ )
			{
				if ( listContains( selectedList, json.periods[i].externalId ) == false )
				{
					addValue( availableList, json.periods[i].name, json.periods[i].externalId );
				}
			}			
		} );
}
*/


function loadDataEntryForm()
{
	//alert("mmmmm");
	
	$( '#dataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );
	//alert("mmmmm");
	//setInnerHTML('dataEntryFormDiv', '');
	//hideById('dataEntryFormDiv');
	var dataSetId = $( '#selectedDataSetId' ).val();
    
    //var dataSetPeriod = dataSetId.split(":");
	
	//var dataSetId = dataSetPeriod[0];
	//var periodTypeId = dataSetPeriod[1];
	
	var organisationUnitId = $( '#organisationUnitId' ).val();
	var equipmentTypeId = $( '#equipmentTypeId' ).val();
	var equipmentId = $( '#equipmentId' ).val();
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#dataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
		//setInnerHTML('dataEntryFormDiv', '');
		//alert( dataSetId +"---"+ selectedPeriodId +"----"+ equipmentId );
		
		//hideById('dataEntryFormDiv');
	    jQuery('#loaderDiv').show();
	    
	    //contentDiv = 'dataEntryFormDiv';
		
		jQuery('#dataEntryFormDiv').load('loadDataEntryForm.action',
			{
				dataSetId:dataSetId,
				selectedPeriodId:selectedPeriodId,
				equipmentId:equipmentId
			}, function()
			{
				showById('dataEntryFormDiv');
				jQuery('#loaderDiv').hide();
			});
		hideLoader();
	}

}


function clearEntryForm()
{
    //$( '#contentDiv' ).html( '' );
    $( '#dataEntryFormDiv' ).html( '' );
    setInnerHTML('dataEntryFormDiv', '');
    //$( '#completenessDiv' ).hide();
    //$( '#infoDiv' ).hide();
}
