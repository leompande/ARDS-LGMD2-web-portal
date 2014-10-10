
function transferFacilityData()
{
	var previousYear = $( '#previousYear' ).val();
	var currentYear = $( '#currentYear' ).val();
	
	var message = "Are you sure to copy/transfer data from " + previousYear + " to " + currentYear +  " for all health facilities? \n data will be override if already exists."
	
	var result = window.confirm( message );
	
	if( result )
	{        
		$( '#transferFacilityDataResultDiv' ).html( ' ' );
		
		jQuery('#loaderDiv').show();
		document.getElementById( "transfer" ).disabled = true;
		
		jQuery('#transferFacilityDataResultDiv').load('transferFacilityDataResult.action',
			{
				//selOrgUnitId:selOrgUnitId,
				//sDateLB:sDateLB,
				//eDateLB:eDateLB
			}, function()
			{
				showById('transferFacilityDataResultDiv');
				document.getElementById( "transfer" ).disabled = false;
				jQuery('#loaderDiv').hide();
			});
	}
}	