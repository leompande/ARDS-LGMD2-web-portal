// ----------------------------------------------------------------
// organization Unit Selected
// ----------------------------------------------------------------
function organisationUnitSelected( orgUnits )
{   
	document.getElementById('selectedOrgunitID').value = orgUnits;
    
	equipmentTypeChange();
	loadAllEquipments();
	
	showById('selectDiv');
    disable('listAllEquipmentBtn');
    
    hideById('searchEquipmentDiv');
    hideById('listEquipmentDiv');
    hideById('editEquipmentDiv');
	hideById('resultSearchDiv');
    
	   $.getJSON( 'getOrganisationUnit.action', {orgunitId:orgUnits[0]}
        , function( json ) 
        {
            var type = json.response;
            setFieldValue('selectedOrgunitText', json.message );
                
            if( type == 'success' )
            {
            	//showById('searchEquipmentDiv');
				enable('listAllEquipmentBtn');
                setInnerHTML('warnmessage','');
                setFieldValue('selectedOrgunitText', json.message );
            }
            else if( type == 'input' )
            {
                setInnerHTML('warnmessage', i18n_can_not_register_patient_for_orgunit);
                disable('listPatientBtn');
            }
        } );
}
selection.setListenerFunction( organisationUnitSelected );

// ----------------------------------------------------------------
// On EquipmentType Change - Loading EquipmentTypeAttributes
// ----------------------------------------------------------------
//function equipmentTypeChange( equipmentTypeId )
function equipmentTypeChange()
{
	loadAllEquipments();
	var equipmentTypeId = $( '#equipmentType' ).val();
	if( equipmentTypeId == "0" )
		return;
	
	showById('selectDiv');
    disable('listAllEquipmentBtn');
    
    hideById('searchEquipmentDiv');
    hideById('listEquipmentDiv');
    hideById('editEquipmentDiv');
	hideById('resultSearchDiv');
	hideById('editEquipmentStatusDiv');
	
	jQuery('#loaderDiv').show();
	
	$.post("getEquipmentTypeAttributes.action",
			{
				id:equipmentTypeId
			},
			function(data)
			{
				showById('searchEquipmentDiv');
				enable('listAllEquipmentBtn');
				jQuery('#loaderDiv').hide();
				populateEquipmentTypeAttributes( data );
			},'xml');	
}

function populateEquipmentTypeAttributes( data )
{
	var searchingAttributeId = document.getElementById("searchingAttributeId");
	clearList( searchingAttributeId );
	
	var invenTypeAttribs = data.getElementsByTagName("equipmentType-type-attribute");
    for ( var i = 0; i < invenTypeAttribs.length; i++ )
    {
        var id = invenTypeAttribs[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = invenTypeAttribs[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        searchingAttributeId.add(option, null);
    }    	
}

//----------------------------------------------------------------
//On LoadAllEquipments
//----------------------------------------------------------------

function loadAllEquipments()
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var equipmentType = document.getElementById('equipmentType');
	var equipmentTypeId = equipmentType.options[ equipmentType.selectedIndex ].value;
	
	document.getElementById("searchText").value = "";
	
	if( equipmentTypeId == 0 )
	{	
		//alert("Plese select Equipmenttype");
		showWarningMessage( i18n_select_equipmenttype );
		return;
	}
	
    hideById('editEquipmentDiv');
	hideById('resultSearchDiv');
	hideById('editEquipmentStatusDiv');
	hideById('equipmentDataEntryDiv');
	
	showById('selectDiv');
	showById('searchEquipmentDiv');

	jQuery('#loaderDiv').show();
	contentDiv = 'listEquipmentDiv';
	isAjax = true;

	jQuery('#listEquipmentDiv').load('getEquipments.action',{
		listAll:true,
		orgUnitId:orgUnitId, 
		EquipmentTypeId:equipmentTypeId	
	},
	function(){
		statusSearching = 0;
		showById('listEquipmentDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}

//----------------------------------------------------------------
// Load Equipments On Filter by EquipmentType Attribute
//----------------------------------------------------------------

function loadEquipmentsByFilter( )
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var equipmentType = document.getElementById('equipmentType');
	var equipmentTypeId = equipmentType.options[ equipmentType.selectedIndex ].value;
	var searchText = document.getElementById('searchText').value;
	
	if( equipmentTypeId == 0 )
	{	
		//alert("Plese select Equipmenttype");
		showWarningMessage( i18n_select_equipmenttype );
		return;
	}
	
	var equipmentTypeAttribute = document.getElementById('searchingAttributeId');
	var equipmentTypeAttributeId = equipmentTypeAttribute.options[ equipmentTypeAttribute.selectedIndex ].value;
	hideById('editEquipmentDiv');
	hideById('resultSearchDiv');
	hideById('editEquipmentStatusDiv');
	hideById('equipmentDataEntryDiv');
	showById('selectDiv');
	showById('searchEquipmentDiv');
	

	jQuery('#loaderDiv').show();
	contentDiv = 'listEquipmentDiv';
	isAjax = true;
	
	jQuery('#listEquipmentDiv').load('getEquipments.action',{		
		orgUnitId:orgUnitId, 
		equipmentTypeId:equipmentTypeId,
		equipmentTypeAttributeId:equipmentTypeAttributeId,
		searchText:searchText
	},
	function(){
		statusSearching = 0;
		showById('listEquipmentDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}

//----------------------------------------------------------------
//Show EquipmentAttributeValue Status History
//----------------------------------------------------------------

function showEquipmentStatusHistoryForm( equipmentId )
{
	//hideById('listEquipmentDiv');
	//hideById('editEquipmentStatusDiv');
	//hideById('selectDiv');
	//hideById('searchEquipmentDiv');
	
	setInnerHTML('equipmentStatusHistoryDiv', '');
	
	//jQuery('#loaderDiv').show();
	
	jQuery('#equipmentStatusHistoryDiv').dialog('destroy').remove();
	jQuery('<div id="equipmentStatusHistoryDiv">' ).load( 'showEquipmentStatusHistoryForm.action?equipmentId='+equipmentId ).dialog({
		title: i18n_equipment_status_history,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 800,
		height: 450
	});
	
}

//----------------------------------------------------------------
//Show EquipmentAttributeValue Tracking Form
//----------------------------------------------------------------

function showEquipmentStatusForm( equipmentId )
{
	hideById('listEquipmentDiv');
	hideById('editEquipmentStatusDiv');
	hideById('selectDiv');
	hideById('searchEquipmentDiv');
	hideById('equipmentDataEntryDiv');
	hideById('editEquipmentDiv');
	
	setInnerHTML('editEquipmentStatusDiv', '');
	
	jQuery('#loaderDiv').show();
	jQuery('#editEquipmentStatusDiv').load('showEquipmentStatusForm.action',
		{
			equipmentId:equipmentId
		}, function()
		{
			showById('editEquipmentStatusDiv');
			jQuery('#searchEquipmentDiv').dialog('close');
			jQuery('#loaderDiv').hide();
		});
		
	jQuery('#resultSearchDiv').dialog('close');
}

function updateEquipmentStatus()
{
	$.ajax({
      type: "POST",
      url: 'updateEquipmentStatus.action',
      data: getParamsForDiv('editEquipmentStatusDiv'),
      success: function( json ) {
		loadAllEquipments();
      }
     });
}


//----------------------------------------------------------------
//Add EquipmentAttributeValue
//----------------------------------------------------------------

function showAddEquipmentForm()
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var equipmentType = document.getElementById('equipmentType');
	var equipmentTypeId = equipmentType.options[ equipmentType.selectedIndex ].value;
	if( equipmentTypeId == 0 )
	{	
		//alert("Plese select equipmenttype");
		showWarningMessage( i18n_select_equipmenttype );
		return;
	}

	hideById('listEquipmentDiv');
	hideById('selectDiv');
	hideById('searchEquipmentDiv');
	hideById('editEquipmentStatusDiv');
	hideById('equipmentDataEntryDiv');
	setInnerHTML('editEquipmentDiv', '');
	
	
	jQuery('#loaderDiv').show();
	jQuery('#editEquipmentDiv').load('showAddEquipmentForm.action',{
		orgUnitId:orgUnitId, 
		equipmentTypeId:equipmentTypeId
		}, 
		function()
		{
			showById('editEquipmentDiv');
			jQuery('#loaderDiv').hide();
		});	
}

function addEquipment()
{
	$.ajax({
      type: "POST",
      url: 'addEquipment.action',
      data: getParamsForDiv('editEquipmentDiv'),
      success: function(json) {
		var type = json.response;
		jQuery('#resultSearchDiv').dialog('close');
		loadAllEquipments();
      }
     });
    return false;
}

//----------------------------------------------------------------
//Update EquipmentAttributeValue
//----------------------------------------------------------------

function showUpdateEquipmentForm( equipmentId )
{
	hideById('listEquipmentDiv');
	hideById('selectDiv');
	hideById('searchEquipmentDiv');
	hideById('editEquipmentStatusDiv');
	hideById('equipmentDataEntryDiv');
	
	setInnerHTML('editEquipmentDiv', '');
	
	jQuery('#loaderDiv').show();
	jQuery('#editEquipmentDiv').load('showUpdateEquipmentForm.action',
		{
			equipmentId:equipmentId
		}, function()
		{
			showById('editEquipmentDiv');
			jQuery('#searchEquipmentDiv').dialog('close');
			jQuery('#loaderDiv').hide();
		});
		
	jQuery('#resultSearchDiv').dialog('close');
}

function updateEquipment()
{
	$.ajax({
      type: "POST",
      url: 'updateEquipment.action',
      data: getParamsForDiv('editEquipmentDiv'),
      success: function( json ) {
		loadAllEquipments();
      }
     });
}

function showEquipmentDataEntryForm( equipmentId )
{
	hideById('listEquipmentDiv');
	hideById('selectDiv');
	hideById('searchEquipmentDiv');
	hideById('editEquipmentStatusDiv');
	hideById('editEquipmentDiv');
	//hideById('equipmentDataEntryDiv');
	//alert("pppp");
	setInnerHTML('equipmentDataEntryDiv', '');
	
	//jQuery('#loaderDiv').show();
	jQuery('#equipmentDataEntryDiv').load('showEquipmentDataEntryForm.action',
		{
			equipmentId:equipmentId
		}, function()
		{
			showById('equipmentDataEntryDiv');
			jQuery('#searchEquipmentDiv').dialog('close');
			//jQuery('#loaderDiv').hide();
		});
		
	jQuery('#resultSearchDiv').dialog('close');
	
	//window.location.href = "showEquipmentDataEntryForm.action?equipmentId=" + equipmentId;
	
}

function editEquipmentDataEntryForm()
{
	$.ajax({
      type: "POST",
      url: 'saveDataEntryForm.action',
      data: getParamsForDiv('equipmentDataEntryDiv'),
      success: function( json ) {
		loadAllEquipments();
      }
     });
}

//-----------------------------------------------------------------------------
//Remove equipmentAttributeValue
//-----------------------------------------------------------------------------

function removeEquipment( equipmentId, name )
{
	removeItem( equipmentId, name, i18n_confirm_delete, 'removeEquipment.action' );
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

//----------------------------------------------------------------
//Show Equipment Details
//----------------------------------------------------------------
function showEquipmentDetails( equipmentId )
{
	hideById('editEquipmentStatusDiv');
	//hideById('selectDiv');
	//hideById('searchEquipmentDiv');
	
	setInnerHTML('editEquipmentDiv', '');
	
	jQuery('#equipmentStatusHistoryDiv').dialog('destroy').remove();
	jQuery('<div id="equipmentStatusHistoryDiv">' ).load( 'showEquipmentDetails.action?equipmentId='+equipmentId ).dialog({
		title: i18n_equipment_details,
		maximize: true, 
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 500,
		height: 450
	});
	
}


function modelDetails( modelId )
{	
	jQuery('#detailsModelInfo').load('getModelDetails.action',{
		id: modelId
	},
	function(){
		showById('detailsModelInfo');
	});
	
	/*
	jQuery.getJSON( 'getModelDetails.action', { id: modelId }, function ( json ) {
		//setInnerHTML( 'nameField', json.model.name );	
		//setInnerHTML( 'descriptionField', json.model.description );
		//setInnerHTML( 'modelTypeField', json.model.modelType );   
	   
		showById('detailsModelInfo');
		//showDetails();
	});
	
	//showById('detailsModelInfo');
	
	$('#detailsModelInfo').load("getModelDetails.action", 
				{
					id:modelId
				}
				, function( ){
				}).dialog({
					title: 'Model details',
					maximize: true, 
					closable: true,
					modal:true,
					overlay:{background:'#000000', opacity:0.1},
					width: 400,
					height: 400
				});;
	*/
}


//------------------------------------------------------------------------------
// EquipmentAttributeValue Routine Data Entry
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
//Update EquipmentAttributeValue Data
//------------------------------------------------------------------------------
/*
function showEquipmentDataEntryForm( equipmentId )
{
	
	window.location.href = "showEquipmentDataEntryForm.action?equipmentId=" + equipmentId;
	/*
	hideById('listEquipmentDiv');
	hideById('selectDiv');
	hideById('searchEquipmentDiv');
	hideById('editEquipmentStatusDiv');
	
	jQuery('#loaderDiv').show();
	jQuery('#editEquipmentDataDiv').load('showEquipmentDataEntryForm.action',
		{
			equipmentId:equipmentId
		}, function()
		{
			showById('editEquipmentDataDiv');
			jQuery('#loaderDiv').hide();
		});
		
	jQuery('#resultSearchDiv').dialog('close');
	
}
*/
function updateEquipmentDataEntry()
{
	$.ajax({
    type: "POST",
    url: 'updateEquipmentData.action',
    data: getParamsForDiv('editEquipmentDataDiv'),
    success: function( json ) {
		loadAllEquipments();
    }
   });
}
