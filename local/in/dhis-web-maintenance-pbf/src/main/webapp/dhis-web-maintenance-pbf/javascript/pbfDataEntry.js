
	var COLOR_GREEN = '#b9ffb9';
	var COLOR_YELLOW = '#fffe8c';
	var COLOR_RED = '#ff8a8a';
	var COLOR_ORANGE = '#ff6600';
	var COLOR_WHITE = '#ffffff';
	var COLOR_GREY = '#cccccc';
	var LocaleColor = 'black';
	
function orgUnitHasBeenSelected( orgUnitIds )
{    
	$( '#dataEntryFormDiv' ).html( '' );
	
	//document.getElementById('selectedOrgunitID').value = orgUnitIds;
	
	//alert( orgUnitIds );
	
	if( orgUnitIds != null && orgUnitIds != "" )
	{
		 $.getJSON( 'getOrganisationUnit.action', {orgUnitId:orgUnitIds[0]}
	        , function( json ) 
	        {
	            var type = json.response;
	            setFieldValue('orgUnitName', json.message );
	            setFieldValue('selectedOrgunitName', json.message );
	                
	            if( type == 'success' )
	            {
	            	window.location.href = "pbfDataEntry.action";
					enable('dataSetId');
					setFieldValue('selectedOrgunitID',orgUnitIds[0])
	                setFieldValue('orgUnitName', json.message );
	                setFieldValue('selectedOrgunitName', json.message );	                
	            }
	            else if( type == 'input' )
	            {
	                disable('dataSetId');
	                disable('selectedPeriodId');
	                disable('prevButton');
	                disable('nextButton');
	                
	                setFieldValue('orgUnitName', json.message );
	                setFieldValue('selectedOrgunitName', json.message );
	            }
	        } );		
	}
}

selection.setListenerFunction( orgUnitHasBeenSelected );


function loadDataEntryForm()
{
	var orgUnitId = $( '#selectedOrgunitID' ).val();
	var dataSetId = $( '#dataSetId' ).val();
	$( '#dataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );
	

	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" && dataSetId == "-1" )
	{
		$( '#dataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    jQuery('#loaderDiv').show();
	    
		jQuery('#dataEntryFormDiv').load('loadDataEntryForm.action',
			{
				orgUnitId:orgUnitId,
				dataSetId:dataSetId,
				selectedPeriodId:selectedPeriodId
			}, function()
			{
				showById('dataEntryFormDiv');
				jQuery('#loaderDiv').hide();
			});
		hideLoader();
	}

}

function saveValue(dataElementId,optionComboId)
{
	var period = document.getElementById("selectedPeriodId").value;
	var valueId = "dataelement"+dataElementId+":"+optionComboId;
	
	var fieldId = "#"+valueId;
	var defaultValue = document.getElementById(valueId).defaultValue;
	var value = document.getElementById(valueId).value;
	
	if(defaultValue != value)
	{
	var dataValue = {
        'dataElementId' : dataElementId,
        'optionComboId' : optionComboId,
        'organisationUnitId' : $("#selectedOrgunitID").val(),
        'periodIso' : period,
        'value' : value
    };
    jQuery.ajax( {
            url: 'saveValue.action',
            data: dataValue,
            dataType: 'json',
            success: handleSuccess,
            error: handleError
        } );
	}
	
	function handleSuccess( json )
	{
	    var code = json.c;

	    alert(code)
	    if ( code == '0' || code == 0) // Value successfully saved on server
	    {
	    	 markValue( fieldId, COLOR_GREEN );
	    }
	    else if ( code == 2 )
	    {
	        markValue( fieldId, COLOR_RED );
	        window.alert( i18n_saving_value_failed_dataset_is_locked );
	    }
	    else // Server error during save
	    {
	        markValue( fieldId, COLOR_RED );
	        window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
	    }            
	}

	function handleError( jqXHR, textStatus, errorThrown )
	{       
	    markValue( fieldId, COLOR_GREEN );
	}

	function markValue( fieldId, color )
	{
	    document.getElementById(valueId).style.backgroundColor = color;	   
	}
}


function savePBFDataValue( dataElementId, valueType )
{
	var period = document.getElementById("selectedPeriodId").value;
	var dataSetId = $( '#dataSetId' ).val();
	var valueId = "";
	if( valueType == 1 )
	{
		valueId = "pbfdv_qty_reported_"+dataElementId;
	}
	else
	{
		valueId = "pbfdv_qty_validated_"+dataElementId;
	}
	
	var fieldId = "#"+valueId;
	var defaultValue = document.getElementById(valueId).defaultValue;
	var value = document.getElementById( valueId ).value;
	
	if(defaultValue != value)
	{
		var dataValue = {
				'dataElementId' : dataElementId,
				'valueType' : valueType,
				'dataSetId' : dataSetId,
				'organisationUnitId' : $("#selectedOrgunitID").val(),
				'periodIso' : period,
				'value' : value
    };
    jQuery.ajax( {
            url: 'saveValue.action',
            data: dataValue,
            dataType: 'json',
            success: handleSuccess,
            error: handleError
        } );
	}
	
	function handleSuccess( json )
	{
	    var code = json.c;

	    alert(code)
	    if ( code == '0' || code == 0) // Value successfully saved on server
	    {
	    	 markValue( fieldId, COLOR_GREEN );
	    }
	    else if ( code == 2 )
	    {
	        markValue( fieldId, COLOR_RED );
	        window.alert( i18n_saving_value_failed_dataset_is_locked );
	    }
	    else // Server error during save
	    {
	        markValue( fieldId, COLOR_RED );
	        window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
	    }
	}

	function handleError( jqXHR, textStatus, errorThrown )
	{       
	    markValue( fieldId, COLOR_GREEN );
	}

	function markValue( fieldId, color )
	{
	    document.getElementById(valueId).style.backgroundColor = color;	   
	}
}

// load periods
function loadPeriods()
{
	$( '#dataEntryFormDiv' ).html( '' );
	
    var orgUnitId = $( '#selectedOrgunitID' ).val();

    var dataSetId = $( '#dataSetId' ).val();
	
	
	if ( dataSetId == "-1" )
	{
		showWarningMessage( i18n_select_dataset );
		
		document.getElementById( "selectedPeriodId" ).disabled = true;
		document.getElementById( "prevButton" ).disabled = true;
		document.getElementById( "nextButton" ).disabled = true;
		return false;
	}
	
	else
	{
		
		enable('selectedPeriodId');
		
		enable('prevButton');
		enable('nextButton');
				
		var url = 'loadPeriods.action?dataSetId=' + dataSetId;
		
		var list = document.getElementById( 'selectedPeriodId' );
			
		clearList( list );
		
		addOptionToList( list, '-1', '[ Select ]' );
		
	    $.getJSON( url, function( json ) {
	    	for ( i in json.periods ) {
	    		addOptionToList( list, json.periods[i].isoDate, json.periods[i].name );
	    	}
	    } );
		
	}
}


//next and pre periods
function getAvailablePeriodsTemp( availablePeriodsId, selectedPeriodsId, year )
{	
	$( '#dataEntryFormDiv' ).html( '' );
	
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
	    		addOptionToList( selectedList, json.periods[i].isoDate, json.periods[i].name );
	    	}
			
		} );
}













