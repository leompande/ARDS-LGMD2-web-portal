

function orgUnitHasBeenSelected( orgUnitIds )
{    
	$( '#dataEntryFormDiv' ).html( '' );
	
	document.getElementById('selectedOrgunitID').value = orgUnitIds;
	
	//alert( orgUnitIds );
	
	if( orgUnitIds != null && orgUnitIds != "" )
	{
		 $.getJSON( 'getOrganisationUnit.action', {orgunitId:orgUnitIds[0]}
	        , function( json ) 
	        {
	            var type = json.response;
	            setFieldValue('orgUnitName', json.message );
	            setFieldValue('selectedOrgunitName', json.message );
	                
	            if( type == 'success' )
	            {
					enable('equipmentTypeId');
	                setFieldValue('orgUnitName', json.message );
	                setFieldValue('selectedOrgunitName', json.message );
	            }
	            else if( type == 'input' )
	            {
	                disable('equipmentTypeId');
	                disable('equipmentId');
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



// Load Equipment and DataSet List
function loadEquipmentAndDataSet()
{
	$( '#dataEntryFormDiv' ).html( '' );
	
	var orgUnitId = $( '#selectedOrgunitID' ).val();
	var equipmentTypeId = $( '#equipmentTypeId' ).val();
	
	if ( equipmentTypeId == "-1" )
	{
		showWarningMessage( i18n_select_equipmenttype );
		
		document.getElementById( "equipmentId" ).disabled = true;
		document.getElementById( "dataSetId" ).disabled = true;
		
		document.getElementById( "selectedPeriodId" ).disabled = true;
		document.getElementById( "prevButton" ).disabled = true;
		document.getElementById( "nextButton" ).disabled = true;
		
		return false;
	}
	
	else
	{
		enable('equipmentId');
		enable('dataSetId');
		
		$.post("getEquipmentAndDataSetList.action",
				{
					equipmentTypeId:equipmentTypeId,
					orgUnitId:orgUnitId
				},
				function(data)
				{
					
					populateEquipmentDataSetList( data );
					//loadDataSets();				
				},'xml');
	}
	
}


function populateEquipmentDataSetList( data )
{
	var equipmentId = document.getElementById("equipmentId");
	clearList( equipmentId );
	
	var dataSetId = document.getElementById("dataSetId");
	clearList( dataSetId );
	
	var equipmentList = data.getElementsByTagName("equipment");
	
	equipmentId.options[0] = new Option( i18n_select_equipment, "-1" , false, false);
	
	for ( var i = 0; i < equipmentList.length; i++ )
	{
		var id = equipmentList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = equipmentList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		equipmentId.add(option, null);
	} 
	
	var dataSetList = data.getElementsByTagName("dataSet");
	
	dataSetId.options[0] = new Option( i18n_select_dataset, "-1" , false, false);
	
	for ( var i = 0; i <  dataSetList.length; i++ )
	{
		var id = dataSetList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = dataSetList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		dataSetId.add(option, null);
	} 
	
}



function equipmentChange()
{
	$( '#dataEntryFormDiv' ).html( '' );
	
    var orgUnitId = $( '#selectedOrgunitID' ).val();
	var equipmentTypeId = $( '#equipmentTypeId' ).val();
	
	var equipmentId = $( '#equipmentId' ).val();
	
	var dataSetId = $( '#dataSetId' ).val();
	
	if ( equipmentId == "-1" )
	{
		$( '#dataEntryFormDiv' ).html('');
		showWarningMessage( i18n_select_equipment );
		
		document.getElementById( "selectedPeriodId" ).disabled = true;
		document.getElementById( "prevButton" ).disabled = true;
		document.getElementById( "nextButton" ).disabled = true;
		return false;
	}
	
	else if ( dataSetId == "-1" )
	{	
		$( '#dataEntryFormDiv' ).html('');
		
		showWarningMessage( i18n_select_dataset );
		
		document.getElementById( "selectedPeriodId" ).disabled = true;
		document.getElementById( "prevButton" ).disabled = true;
		document.getElementById( "nextButton" ).disabled = true;
		return false;
	}
	
	else
	{
		loadPeriods();
		//loadDataEntryForm();
	}

}


function loadPeriods()
{
	$( '#dataEntryFormDiv' ).html( '' );
	
    //enable('selectedPeriodId');
    
    var orgUnitId = $( '#selectedOrgunitID' ).val();
	var equipmentTypeId = $( '#equipmentTypeId' ).val();
	
	var equipmentId = $( '#equipmentId' ).val();
	
	var dataSetId = $( '#dataSetId' ).val();
	
	if ( equipmentId == "-1" )
	{
		
		showWarningMessage( i18n_select_equipment );
		
		document.getElementById( "selectedPeriodId" ).disabled = true;
		document.getElementById( "prevButton" ).disabled = true;
		document.getElementById( "nextButton" ).disabled = true;
		return false;
	}
	
	else if ( dataSetId == "-1" )
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




function loadDataEntryForm()
{
	var orgUnitId = $( '#selectedOrgunitID' ).val();
	var equipmentTypeId = $( '#equipmentTypeId' ).val();
	
	var equipmentId = $( '#equipmentId' ).val();
	
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


function saveEquipmentDataEntryForm()
{
	$.ajax({
      type: "POST",
      url: 'saveDataEntryForm.action',
      data: getParamsForDiv('ccemDataEntryForm'),
      success: function( json ) {
		//loadAllEquipments();
    	  
    	window.location.href='index.action';  
      }
     });
}



//----------------------------------------------------------------
//Get Params form Div
//----------------------------------------------------------------

function getParamsForDiv( equipmentDiv )
{
	var params = '';
	
	jQuery("#" + equipmentDiv + " :input").each(function()
		{
			var elementId = $(this).attr('id');
			
			if( $(this).attr('type') == 'checkbox' )
			{
				var checked = jQuery(this).attr('checked') ? true : false;
				params += elementId + "=" + checked + "&";
			}
			else if( $(this).attr('type') != 'button' )
			{
				var value = "";
				if( jQuery(this).val() != '' )
				{
					value = htmlEncode(jQuery(this).val());
				}
				params += elementId + "="+ value + "&";
			}
			
		});
	
	//alert( params );
	
	return params;
}

