
// -----------------------------------------------------------------------------
// Load Period
// -----------------------------------------------------------------------------


function getFacilityDataSetPeriods()
{
	$( '#facilityDataEntryFormDiv' ).html( '' );
	
	/*
	$( '#selectedPeriodId' ).removeAttr( 'disabled' );
    $( '#prevButton' ).removeAttr( 'disabled' );
    $( '#nextButton' ).removeAttr( 'disabled' );
    */
    
    var dataSetId = $( '#dataSetId' ).val();

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
	
}


//-----------------------------------------------------------------------------
//Load pre and Next Period
//-----------------------------------------------------------------------------
function getFacilityAvailablePeriodsTemp( availablePeriodsId, selectedPeriodsId, year )
{	
	$( '#facilityDataEntryFormDiv' ).html( '' );
	
	var dataSetId = $( '#dataSetId' ).val();
    
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


//-----------------------------------------------------------------------------
//Load Facility Data Entry Form
//-----------------------------------------------------------------------------

function loadFacilityDataEntryForm()
{
	
	$( '#facilityDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );
	
	var dataSetId = $( '#dataSetId' ).val();
	
	var orgUnitId = $( '#organisationUnitId' ).val();
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#facilityDataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	   // jQuery('#loaderDiv').show();
	    
	    //contentDiv = 'facilityDataEntryFormDiv';
		
		jQuery('#facilityDataEntryFormDiv').load('loadFacilityDataEntryForm.action',
			{
				orgUnitId:orgUnitId,
				dataSetId:dataSetId,
				selectedPeriodId:selectedPeriodId,
			}, function()
			{
				showById('facilityDataEntryFormDiv');
				//jQuery('#loaderDiv').hide();
			});
		//hideLoader();
	}

}




