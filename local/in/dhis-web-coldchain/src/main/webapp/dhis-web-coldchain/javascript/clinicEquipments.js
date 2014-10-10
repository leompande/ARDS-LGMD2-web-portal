// ----------------------------------------------------------------
// organization Unit Selected
// -------------------------------------showEquipmentDetails---------------------------

window.onload=function(){
	jQuery('#equipmentStatusHistoryDiv').dialog({autoOpen: false});	
	jQuery('#editEquipmentStatusDiv').dialog({autoOpen: false});
	jQuery('#editEquipmentStatusDiv').dialog({autoOpen: false});
	jQuery('#equipmentDataEntryDiv').dialog({autoOpen: false});
	jQuery('#editEquipmentDiv').dialog({autoOpen: false});
	jQuery('#equipmentDetailsDiv').dialog({autoOpen: false});
	jQuery('#fullOrgUnitDetailsDiv').dialog({autoOpen: false});
	jQuery('#updateOrgUnitDetailsDiv').dialog({autoOpen: false});
	jQuery('#facilityDataEntryDiv').dialog({autoOpen: false});
}
function organisationUnitSelected( orgUnits )
{   
	
	document.getElementById('selectedOrgunitID').value = orgUnits;
	
	//document.getElementById('selOrgunitID').value = orgUnits;
	
	hideById('selectOrgUnitDiv');
	//hideById('orgUnitDetailsDiv');
	document.getElementById('overlay').style.visibility = 'visible';
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{
		
		orgUnitId:orgUnits[0]
	},
	function(){		
		showById('orgUnitDetailsDiv');
		loadAllEquipments();
		equipmentTypeChange();		
	});
	
}
selection.setListenerFunction( organisationUnitSelected );

function loadAllEquipments()
{
	document.getElementById('overlay').style.visibility = 'visible';
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var equipmentType = document.getElementById('equipmentType');
	var equipmentTypeId = equipmentType.options[ equipmentType.selectedIndex ].value;
	//alert( equipmentTypeId );
	
	document.getElementById("searchText").value = "";
	
	var filteredOrgUnitList = document.getElementById('filteredOrgUnitList');
	
	//alert( filteredOrgUnitList );
	
	if( equipmentTypeId == 0 )
	{	
		//alert("Plese select Equipmenttype");
		showWarningMessage( i18n_select_equipmenttype );
		return;
	}
	
	hideById('selectOrgUnitDiv');
	showById('orgUnitDetailsDiv');

	//jQuery('#loaderDiv').show();
	contentDiv = 'listEquipmentDiv';
	isAjax = true;
	
	jQuery('#listEquipmentDiv').load('getEquipments.action',{
		listAll:true,
		orgUnitId:orgUnitId,
		//filteredOrgUnitList:getParamsStringBySelected( filteredOrgUnitList ),
		EquipmentTypeId:equipmentTypeId	
	},
	function(){
		statusSearching = 0;
		showById('listEquipmentDiv');
		document.getElementById('overlay').style.visibility = 'hidden';
	});
	//hideLoader();
}



function loadAllEquipmentsByOrgUnitFilter()
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var equipmentType = document.getElementById('equipmentType');
	var equipmentTypeId = equipmentType.options[ equipmentType.selectedIndex ].value;
	
	//alert( equipmentTypeId );
	
	//document.getElementById("searchText").value = "";
	
	//var filteredOrgUnitList = document.getElementById('filteredOrgUnitList');
	
	//alert( "inside  loadAllEquipmentsByOrgUnitFilter " );
	
	if( equipmentTypeId == 0 )
	{	
		//alert("Plese select Equipmenttype");
		showWarningMessage( i18n_select_equipmenttype );
		return;
	}
	
	hideById('selectOrgUnitDiv');
	showById('orgUnitDetailsDiv');

	//jQuery('#loaderDiv').show();
	contentDiv = 'listEquipmentDiv';
	isAjax = true;

	document.getElementById('overlay').style.visibility = 'visible';
	var url = "getEquipments.action?" + getParamString( 'filteredOrgUnitList', 'filteredOrgUnitList' )
	
	jQuery('#listEquipmentDiv').load(url,
	{
		listAll:true,
		orgUnitId:orgUnitId,
		EquipmentTypeId:equipmentTypeId
	},
	function(){
		statusSearching = 0;
		showById('listEquipmentDiv');
		
		equipmentTypeChangeForFilteredOrgUnit();
		document.getElementById('overlay').style.visibility = 'hidden';
		//jQuery('#loaderDiv').hide();
	});
	//hideLoader();
}

function equipmentTypeChangeForFilteredOrgUnit()
{
	//loadAllEquipments();
	var equipmentTypeId = $( '#equipmentType' ).val();
	if( equipmentTypeId == "0" ) return;
	
	hideById('selectOrgUnitDiv');
	showById('orgUnitDetailsDiv');
	
	//showById('selectDiv');
	//disable('listAllEquipmentBtn');
 
	//jQuery('#loaderDiv').show();
	document.getElementById('overlay').style.visibility = 'visible';
	$.post("getEquipmentTypeAttributeList.action",
			{
				id:equipmentTypeId
			},
			function(data)
			{
				showById('listEquipmentDiv');
				populateEquipmentTypeAttributes( data );
			},'xml');	
}


function getParamsStringBySelected( elementId )
{
	//alert( "getParamsStringBySelected" );
	var result = "";
	var list = jQuery( "#" + elementId ).children( ":selected" );
	
	list.each( function( i, item ){
		
		result += elementId + "=" + item.value;
		result += ( i < list.length - 1 ) ? "&" : "";
		
	});
	
	//alert( result );
	return result;
}

//----------------------------------------------------------------
//On EquipmentType Change - Loading EquipmentTypeAttributes
//----------------------------------------------------------------
//function equipmentTypeChange( equipmentTypeId )
function equipmentTypeChange()
{
	//loadAllEquipments();
	var equipmentTypeId = $( '#equipmentType' ).val();
	
	var equipmentType = document.getElementById('equipmentType');
	
	var equipmentTypeName = equipmentType.options[ equipmentType.selectedIndex ].text;

	if( equipmentTypeId == "0" ) return;
	
	
	//alert( equipmentTypeName );
	
	if( equipmentTypeName == "Ice packs" )
	{
		showById('addEquipmentIcePacksTD');
		hideById('addEquipmentTD');
	}
	
	else
	{
		showById('addEquipmentTD');
		hideById('addEquipmentIcePacksTD');
	}
	
	hideById('selectOrgUnitDiv');
	showById('orgUnitDetailsDiv');
	
	//showById('selectDiv');
	//disable('listAllEquipmentBtn');
 
	//jQuery('#loaderDiv').show();
	document.getElementById('overlay').style.visibility = 'visible';
	$.post("getEquipmentTypeAttributeList.action",
			{
				id:equipmentTypeId
			},
			function(data)
			{
				showById('listEquipmentDiv');
				
				populateEquipmentTypeAttributes( data );
				loadAllEquipments();				
			},'xml');
}


function equipmentTypeChangeForDisplay()
{
	//loadAllEquipments();
	var equipmentTypeId = $( '#equipmentType' ).val();
	if( equipmentTypeId == "0" ) return;
	
	//showById('selectDiv');
	//disable('listAllEquipmentBtn');
 
	//jQuery('#loaderDiv').show();	
	$.post("getEquipmentTypeAttributes.action",
			{
				id:equipmentTypeId
			},
			function(data)
			{
				populateEquipmentTypeAttributes( data );				
			},'xml');	
}


function populateEquipmentTypeAttributes( data )
{
	var searchingAttributeId = document.getElementById("searchingAttributeId");
	clearList( searchingAttributeId );
	
	var model_name_text = "Model Name";
	var model_name_value = "modelname";
	
	var orunit_name_text = "OrgUnit Name";
	var orgunit_name_value = "orgunitname";
	
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
	$("#searchingAttributeId").append("<option value='"+ model_name_value + "' title='" + model_name_text + "' text='" + model_name_text + "'>" + model_name_text + "</option>");
	$("#searchingAttributeId").append("<option value='"+ orgunit_name_value + "' title='" + orunit_name_text + "' text='" + orunit_name_text + "'>" + orunit_name_text + "</option>");

}

/**
* Hides the document element with the given identifier.
* 
* @param id the element identifier.
*/
function hideFilter()
{
	hideById('filterDiv');
	//alert("fffff");
	showById('searchingAttributeTD');
	showById('searchingTextTD');
	showById('searchDiv');
	showById('clearDiv');
	//jQuery("#" + 'filterDiv').hide();
 
	//jQuery("#" + 'clearDiv' ).show();
}

function hideClear()
{
	//alert("ccccc");
	//jQuery("#" + 'clearDiv').hide();
	hideById('clearDiv');
	hideById('searchDiv');
	hideById('searchingTextTD');
	hideById('searchingAttributeTD');
	
	equipmentTypeChange();
	//loadAllEquipments();
	showById('filterDiv');
	//jQuery("#" + 'filterDiv' ).show();
}


function hideOrgFilter()
{
	hideById('filterOrgDiv');
	
	showById('searchingOrgUnitAttributeTD');
	
	searchingOrgUnitFilterOptionOnChange();
	
	//showById('searchingOrgTextTD');
	//showById('searchingOrgUnitGroupSetMemberTD');
	//showById('searchOrgDiv');
	showById('clearOrgDiv');
	//jQuery("#" + 'filterDiv').hide();
 
	//jQuery("#" + 'clearDiv' ).show();
}

function hideOrgClear()
{
	//alert("ccccc");
	//jQuery("#" + 'clearDiv').hide();
	
	hideById('clearOrgDiv');
	hideById('searchOrgDiv');
	hideById('searchingOrgTextTD');
	hideById('searchingOrgUnitAttributeTD');
	
	hideById('searchingOrgUnitGroupSetMemberTD');
	hideById('searchingOrgUnitOwnerShipGroupSetMemberTD');
	hideById('selectOrgUnitDiv');
	//hideById('orgUnitDetailsDiv');
	
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	document.getElementById('overlay').style.visibility = 'visible';
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{
		
		orgUnitId:orgUnitId
	},
	function(){
		showById('orgUnitDetailsDiv');
		equipmentTypeChange();
		//loadAllEquipments();
	});
	
	
	//equipmentTypeChange();
	//loadAllEquipments();
	showById('filterOrgDiv');
	//jQuery("#" + 'filterDiv' ).show();
}

//function searchingOrgUnitFilterOptionOnChange( searchingOrgUnitFilterOptionId )
function searchingOrgUnitFilterOptionOnChange()
{
	var orgUnitFilterOptionId = $( '#searchingOrgUnitFilterOptionId' ).val();
	
	var orgUnitFilterOption = document.getElementById('searchingOrgUnitFilterOptionId');
	
	var orgUnitFilterOptionName = orgUnitFilterOption.options[ orgUnitFilterOption.selectedIndex ].value;
	
	//alert( orgUnitFilterOptionName );
	
	if( orgUnitFilterOptionName ==  "Facility Type" )
	{
		hideById('filterOrgDiv');
		showById('searchingOrgUnitGroupSetMemberTD');
		hideById('searchingOrgUnitOwnerShipGroupSetMemberTD');
		hideById('searchingOrgTextTD');
		hideById('searchOrgDiv');
		showById('clearOrgDiv');
	}
	
	else if( orgUnitFilterOptionName ==  "Ownership" )
	{
		hideById('filterOrgDiv');
		showById('searchingOrgUnitOwnerShipGroupSetMemberTD');
		hideById('searchingOrgUnitGroupSetMemberTD');
		hideById('searchingOrgTextTD');
		hideById('searchOrgDiv');
		showById('clearOrgDiv');
	}
	
	else
	{
		hideById('filterOrgDiv');
		hideById('searchingOrgUnitGroupSetMemberTD');
		hideById('searchingOrgUnitOwnerShipGroupSetMemberTD');
		showById('searchingOrgTextTD');
		showById('searchOrgDiv');
		showById('clearOrgDiv');
	}
	
	var searchOrgText = document.getElementById('searchOrgText').value;
	document.getElementById("searchOrgText").value = "";
}



//----------------------------------------------------------------
//Load Equipments On Filter by EquipmentType Attribute
//----------------------------------------------------------------

function loadEquipmentsByFilter()
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var equipmentType = document.getElementById('equipmentType');
	var equipmentTypeId = equipmentType.options[ equipmentType.selectedIndex ].value;
	var searchText = document.getElementById('searchText').value;
	
	if( equipmentTypeId == 0 )
	{	
		return;
	}
	
	var equipmentTypeAttribute = document.getElementById('searchingAttributeId');
	var equipmentTypeAttributeId = equipmentTypeAttribute.options[ equipmentTypeAttribute.selectedIndex ].value;
	
	hideById('selectOrgUnitDiv');
	showById('orgUnitDetailsDiv');
	
	/*
	var url = "getEquipments.action?" + getParamString( 'filteredOrgUnitList', 'filteredOrgUnitList' )
	
	jQuery('#listEquipmentDiv').load(url,
	{
		listAll:true,
		orgUnitId:orgUnitId,
		EquipmentTypeId:equipmentTypeId
	},
	*/
	
	//jQuery('#loaderDiv').show();
	contentDiv = 'listEquipmentDiv';
	
	isAjax = true;
	document.getElementById('overlay').style.visibility = 'visible';
	var url = "getEquipments.action?" + getParamString( 'filteredOrgUnitList', 'filteredOrgUnitList' )
	
	jQuery('#listEquipmentDiv').load(url,
	{		
		orgUnitId:orgUnitId, 
		equipmentTypeId:equipmentTypeId,
		equipmentTypeAttributeId:equipmentTypeAttributeId,
		searchText:searchText
	},
	function(){
		statusSearching = 0;
		showById('listEquipmentDiv');
		document.getElementById('overlay').style.visibility = 'hidden';
		//jQuery('#loaderDiv').hide();
	});
	//hideLoader();
}

function searchingAttributeOnChange( equipmentTypeAttributeId )
{
	//alert( equipmentTypeAttributeId );
	var searchText = document.getElementById('searchText').value;
	document.getElementById("searchText").value = "";
}


function isEnter( e )
{
	if ( e.keyCode == 13) 
    {   
		//alert( e.keycode );
		loadEquipmentsByFilter();
		//return false;
    }   
}





function searchingOrgUnitAttributeOnChange( orgUnitAttributeId )
{
	var searchOrgText = document.getElementById('searchOrgText').value;
	document.getElementById("searchOrgText").value = "";
}



function isOrgUnitEnter( e )
{
	if ( e.keyCode == 13) 
    {   
		loadOrgUnitsByFilter();
    }   
}

function loadOrgUnitsByFilter()
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	
	var searchOrgText = document.getElementById('searchOrgText').value;
	
	var orgUnitAttribute = document.getElementById('searchingOrgUnitFilterOptionId');
	var searchingOrgUnitFilterOptionId = orgUnitAttribute.options[ orgUnitAttribute.selectedIndex ].value;
	
	
	if( searchingOrgUnitFilterOptionId ==  "Facility Type" )
	{
		var orgUnitGroup = document.getElementById('searchingOrgUnitGroupId');
		var searchingOrgUnitGroupId = orgUnitGroup.options[ orgUnitGroup.selectedIndex ].value;
		
	}
	
	else if( searchingOrgUnitFilterOptionId ==  "Ownership" )
	{
		var orgUnitGroup = document.getElementById('searchingOrgUnitGroupSetOwnerShipId');
		var searchingOrgUnitGroupId = orgUnitGroup.options[ orgUnitGroup.selectedIndex ].value;
	}
	
	/*
	var orgUnitGroup = document.getElementById('searchingOrgUnitGroupId');
	var searchingOrgUnitGroupId = orgUnitGroup.options[ orgUnitGroup.selectedIndex ].value;
	*/
	
	//alert( searchingOrgUnitFilterOptionId + " -- "  + searchingOrgUnitGroupId );
	
	
	hideById('selectOrgUnitDiv');
	//hideById('searchOrgDiv');
	
	//showById('searchOrgDiv');
	
	showById('orgUnitDetailsDiv');
	

	//jQuery('#loaderDiv').show();
	
	//contentDiv = 'orgUnitDetailsDiv';
	
	contentDiv = 'orgUnitDetailsDiv';
	
	isAjax = true;
	document.getElementById('overlay').style.visibility = 'visible';
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{		
		orgUnitId:orgUnitId, 
		searchingOrgUnitFilterOptionId:searchingOrgUnitFilterOptionId,
		searchingOrgUnitGroupId:searchingOrgUnitGroupId,
		searchOrgText:searchOrgText,
		listFilterOrgUnit:true
	},
	function(){
		
		
		showById('orgUnitDetailsDiv');
		hideById('filterOrgDiv');
		
		showById('searchingOrgUnitAttributeTD');
		showById('searchingOrgUnitOwnerShipGroupSetMemberTD');
		//showById('searchingOrgUnitGroupSetMemberTD');
		//showById('searchingOrgTextTD');
		//showById('searchOrgDiv');
		showById('clearOrgDiv');
		
		
		
		loadAllEquipmentsByOrgUnitFilter();
		
		//equipmentTypeChange();
		
		
		
		//jQuery('#loaderDiv').hide();
		//document.getElementById('overlay').style.visibility = 'hidden';
	});
	//hideLoader();
}


/**
* Shows the document element with the given identifier.
* 
* @param id the element identifier.
*/
/*
function showById( id )
{
 jQuery("#" + id).show();
}
*/

var tempClinicName = "";
var tempModelName = "";

function showEquipmentStatusHistoryForm( equipmentId , clinicName, modelName )
{
	tempClinicName = clinicName;
	tempModelName = modelName;
	
	jQuery('#equipmentStatusHistoryDiv').dialog('destroy').remove();
	//document.getElementById('overlay').style.visibility = 'visible';
	jQuery('<div id="equipmentStatusHistoryDiv">' ).load( 'showEquipmentStatusHistoryForm.action?equipmentId='+equipmentId ).dialog({
		title: tempModelName + " at " + tempClinicName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 800,
		height: 450
	});
	//document.getElementById('overlay').style.visibility = 'hidden';
}

function closewindow()
{
	jQuery('#equipmentStatusHistoryDiv').dialog('destroy').remove();
	//hideById('equipmentStatusHistoryDiv');
	//setInnerHTML('equipmentStatusHistoryDiv', '');
	//$.modal.close();
}


function showEquipmentStatusForm( equipmentId )
{
	
	jQuery('#equipmentStatusHistoryDiv').dialog('close');
	
	jQuery('#editEquipmentStatusDiv').dialog('destroy').remove();
	//document.getElementById('overlay').style.visibility = 'visible';
	jQuery('<div id="editEquipmentStatusDiv">' ).load( 'showEquipmentStatusForm.action?equipmentId='+ equipmentId ).dialog({
		title: tempModelName + " at " + tempClinicName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 600,
		height: 350
	});
	//document.getElementById('overlay').style.visibility = 'hidden';
	
	/*
	jQuery('#editEquipmentStatusDiv').load('showEquipmentStatusForm.action',
		{
			equipmentId:equipmentId
		}, function()
		{
			showById('editEquipmentStatusDiv');
			jQuery('#searchEquipmentDiv').dialog('close');
			jQuery('#loaderDiv').hide();
		});
	*/	
	
}

function updateEquipmentStatus()
{
	var tempEquipmentId = document.getElementById('equipmentId').value;
	var tempEquipmentOrgUnitName = document.getElementById('equipmentOrgUnitName').value;
	var tempEquipmentModelName = document.getElementById('equipmentModelName').value;
	
	//alert(tempEquipmentId + "--" + tempEquipmentOrgUnitName + "--" + tempEquipmentModelName );
	//document.getElementById('overlay').style.visibility = 'visible';
	jQuery('#loaderDiv').show();
	
	$.ajax({
      type: "POST",
      url: 'updateEquipmentStatus.action',
      data: getParamsForDiv('editEquipmentStatusDiv'),
      success: function( json ) {
		//loadAllEquipments();
		jQuery('#editEquipmentStatusDiv').dialog('destroy').remove();
		equipmentTypeChange();
		jQuery('#loaderDiv').hide();
		showEquipmentStatusHistoryForm( tempEquipmentId , tempEquipmentOrgUnitName, tempEquipmentModelName );
		//document.getElementById('overlay').style.visibility = 'hidden';
	}
     });
}


function closewindow2()
{
	var tempEquipmentId = $( '#equipmentId' ).val();
	//alert(tempEquipmentId);
			
	var tempEquipmentId = document.getElementById('equipmentId').value;
	var tempEquipmentOrgUnitName = document.getElementById('equipmentOrgUnitName').value;
	var tempEquipmentModelName = document.getElementById('equipmentModelName').value;
	
	//alert(tempEquipmentId + "--" + tempEquipmentOrgUnitName + "--" + tempEquipmentModelName );
	jQuery('#editEquipmentStatusDiv').dialog('destroy').remove();
	showEquipmentStatusHistoryForm( tempEquipmentId , tempEquipmentOrgUnitName, tempEquipmentModelName );
	
	//hideById('equipmentStatusHistoryDiv');
	//setInnerHTML('equipmentStatusHistoryDiv', '');
	//$.modal.close();
}


function showEquipmentDataEntryForm( equipmentId , clinicName, modelName )
{
	//jQuery('#loaderDiv').show();
	
	var orgUnitName = clinicName;
	var equipmentModelName = modelName;
	
	jQuery('#equipmentDataEntryDiv').dialog('destroy').remove();
	//document.getElementById('overlay').style.visibility = 'visible';
	jQuery('<div id="equipmentDataEntryDiv">' ).load( 'showEquipmentDataEntryForm.action?equipmentId='+ equipmentId ).dialog({
		title: equipmentModelName + " at " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	//document.getElementById('overlay').style.visibility = 'hidden';
	/*
	
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
	
	*/
	//window.location.href = "showEquipmentDataEntryForm.action?equipmentId=" + equipmentId;
	
}

function editEquipmentDataEntryForm()
{
	$.ajax({
      type: "POST",
      url: 'saveDataEntryForm.action',
      data: getParamsForDiv('equipmentDataEntryDiv'),
      success: function( json ) {
		//loadAllEquipments();
		jQuery('#equipmentDataEntryDiv').dialog('destroy').remove();
		//equipmentTypeChange();
      }
     });
}


function closewindow3()
{
	jQuery('#equipmentDataEntryDiv').dialog('destroy').remove();
}

function showAddEquipmentForm( orgUnitId, orgUnitName )
{
	//var orgUnitId = document.getElementById('selectedOrgunitID').value;
	
	var orgUnitId = orgUnitId;
	
	var orgUnitName = orgUnitName;
	var equipmentType = document.getElementById('equipmentType');
	var equipmentTypeId = equipmentType.options[ equipmentType.selectedIndex ].value;
	var equipmentTypeName = equipmentType.options[ equipmentType.selectedIndex ].text;
	
	//alert( equipmentTypeName );
	if( equipmentTypeId == 0 )
	{	
		//alert("Plese select equipmenttype");
		showWarningMessage( i18n_select_equipmenttype );
		return;
	}

	
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
	jQuery('<div id="editEquipmentDiv">' ).load( 'showAddEquipmentForm.action?equipmentTypeId='+ equipmentTypeId + "&orgUnitId=" + orgUnitId ).dialog({
		title: 'Add New ' + equipmentTypeName + ' to ' + " " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
	
	/*
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
	*/
}


function showAddEquipmentIcePacksForm( orgUnitId, orgUnitName )
{
	//var orgUnitId = document.getElementById('selectedOrgunitID').value;
	
	var orgUnitId = orgUnitId;
	var orgUnitName = orgUnitName;
	var equipmentType = document.getElementById('equipmentType');
	var equipmentTypeId = equipmentType.options[ equipmentType.selectedIndex ].value;
	var equipmentTypeName = equipmentType.options[ equipmentType.selectedIndex ].text;
	
	//alert( equipmentTypeName );
	if( equipmentTypeId == 0 )
	{	
		//alert("Plese select equipmenttype");
		showWarningMessage( i18n_select_equipmenttype );
		return;
	}

	jQuery('#editEquipmentDiv').dialog('destroy').remove();
	jQuery('<div id="editEquipmentDiv">' ).load( 'showAddEquipmentIcePacksForm.action?equipmentTypeId='+ equipmentTypeId + "&orgUnitId=" + orgUnitId ).dialog({
		title: 'Add New ' + equipmentTypeName + ' to ' + " " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
}




function getEquipmentTypeIcePacksData()
{
	
	$( '#loadEquipmentIcePacksDataForm' ).html('');
	
	$( '#saveButtonIcePacks' ).removeAttr( 'disabled' );

	
	var equipmentTypeId = $( '#equipmentTypeIcePacksId' ).val();
	
	var healthFacility = $( '#healthFacility' ).val();
    
	if ( healthFacility == "-1" )
	{
		$( '#loadEquipmentIcePacksDataForm' ).html('');
		document.getElementById( "saveButtonIcePacks" ).disabled = true;
		return false;
	}
	
	else
	{
	    jQuery('#loaderDiv').show();
	    
		jQuery('#loadEquipmentIcePacksDataForm').load('loadEquipmentIcePacksData.action',
			{
				equipmentTypeId:equipmentTypeId,
				orgUnitId:healthFacility,
			}, function()
			{
				showById('loadEquipmentIcePacksDataForm');
				jQuery('#loaderDiv').hide();
			});
		hideLoader();
	}

}


function addEquipment()
{
	$.ajax({
      type: "POST",
      url: 'addEquipment.action',
      data: getParamsForDiv('editEquipmentDiv'),
      success: function(json) {
		var type = json.response;
		//jQuery('#resultSearchDiv').dialog('close');
		//loadAllEquipments();
		
		jQuery('#editEquipmentDiv').dialog('destroy').remove();
		equipmentTypeChange();
		
      }
     });
    //return false;
}

function addUpdateEquipmentIcePacks()
{
	$.ajax({
      type: "POST",
      url: 'addUpdateEquipmentIcePacks.action',
      data: getParamsForDiv('editEquipmentDiv'),
      success: function(json) {
		var type = json.response;
		//jQuery('#resultSearchDiv').dialog('close');
		//loadAllEquipments();
		
		jQuery('#editEquipmentDiv').dialog('destroy').remove();
		equipmentTypeChange();
		
      }
     });
    //return false;
}





function closewindowEquipmentIcePacksData()
{
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
}

function closewindow4()
{
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
}

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


function showEquipmentDetails( equipmentId , clinicName, modelName,equipmentTypeName )
{
	
	var orgUnitName = clinicName;
	var equipmentModelName = modelName;
	
	jQuery('#equipmentDetailsDiv').dialog('destroy').remove();
	jQuery('<div id="equipmentDetailsDiv">' ).load( 'showEquipmentDetails.action?equipmentId='+ equipmentId ).dialog({
		title: equipmentModelName + " at " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
	/*
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
	*/
}

function closewindow5()
{
	jQuery('#equipmentDetailsDiv').dialog('destroy').remove();
}



//----------------------------------------------------------------
//Update EquipmentAttributeValue
//----------------------------------------------------------------

function showUpdateEquipmentForm( equipmentId , clinicName, modelName )
{
	
	var orgUnitName = clinicName;
	var equipmentModelName = modelName;
	
	jQuery('#equipmentDetailsDiv').dialog('close');
	
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
	jQuery('<div id="editEquipmentDiv">' ).load( 'showUpdateEquipmentForm.action?equipmentId='+ equipmentId ).dialog({
		title: equipmentModelName + " at " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
	/*
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
	
	*/
}

function updateEquipment()
{
		var tempEquipmentId = document.getElementById('equipmentID').value;
		var tempEquipmentOrgUnitName = document.getElementById('equipmentOrgUnitName').value;
		var tempEquipmentModelName = document.getElementById('equipmentModelName').value;
		
		$.ajax({
	    type: "POST",
	    url: 'updateEquipment.action',
	    data: getParamsForDiv('editEquipmentDiv'),
	    	success: function( json ) {
			//loadAllEquipments();
			
			jQuery('#editEquipmentDiv').dialog('destroy').remove();
			equipmentTypeChange();
			showEquipmentDetails( tempEquipmentId , tempEquipmentOrgUnitName, tempEquipmentModelName );
			}
		});
}

function closeUpdateWindow()
{
	//var tempEquipmentId = $( '#equipmentID' ).val();
	//alert(tempEquipmentId);
			
	var tempEquipmentId = document.getElementById('equipmentID').value;
	var tempEquipmentOrgUnitName = document.getElementById('equipmentOrgUnitName').value;
	var tempEquipmentModelName = document.getElementById('equipmentModelName').value;
	
	//alert(tempEquipmentId + "--" + tempEquipmentOrgUnitName + "--" + tempEquipmentModelName );
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
	showEquipmentDetails( tempEquipmentId , tempEquipmentOrgUnitName, tempEquipmentModelName );
	
	//hideById('equipmentStatusHistoryDiv');
	//setInnerHTML('equipmentStatusHistoryDiv', '');
	//$.modal.close();
}

function removeEquipment( equipmentId , clinicName, modelName )
{
	var itemName = modelName + " at " + clinicName;
	
	//removeItem( equipmentId, name, i18n_confirm_delete, 'removeEquipment.action' );
	
	var result = window.confirm( i18n_confirm_delete + "\n\n" + itemName );
	
	if ( result )
	{
		$.ajax({
		    type: "POST",
		    url: 'removeEquipment.action?id='+ equipmentId,
		    	//data: getParamsForDiv('editEquipmentDiv'),
		    	success: function( json ) {
				jQuery('#editEquipmentDiv').dialog('destroy').remove();
				equipmentTypeChange();
				}
			});
		
		/*
		$.post("removeEquipment.action",
				{
					id:equipmentId
				},
				function( data )
				{
					jQuery('#editEquipmentDiv').dialog('destroy').remove();
					
				},'xml');
		*/		
		
		//window.location.href = 'removeEquipment.action?id=' + equipmentId;
		
	}
}


function showFullOrgUnitDetails( orgUnitId, orgUnitName )
{	
	var orgUnitId = orgUnitId;
	var orgUnitName = orgUnitName;
	//lockScreen();
	//jQuery('#loaderDiv').show();	
	jQuery('#fullOrgUnitDetailsDiv').dialog('destroy').remove();
	document.getElementById('overlay').style.visibility = 'visible';
	jQuery('<div id="fullOrgUnitDetailsDiv">' ).load( 'getFullOrganisationUnitDetails.action?orgUnitId='+ orgUnitId ).dialog({
		title: 'Full Details of ' + " " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.45},
		width: 800,
		height: 600
	});		
	//jQuery('#loaderDiv').hide();
	document.getElementById('overlay').style.visibility = 'hidden';
	//unLockScreen();
	
	/*
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
	jQuery('<div id="editEquipmentDiv">' ).load( 'showAddEquipmentForm.action?equipmentTypeId='+ equipmentTypeId + "&orgUnitId=" + orgUnitId ).dialog({
		title: 'Add New Unit to ' + " " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	*/

}

function closeFullOrgDetailsWindow()
{
	jQuery('#fullOrgUnitDetailsDiv').dialog('destroy').remove();
	
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{
		
		orgUnitId:orgUnitId
	},
	function(){
		showById('orgUnitDetailsDiv');
		equipmentTypeChange();
	});
	
}


//----------------------------------------------------------------
//Update OrganisationUnit and Close Window
//----------------------------------------------------------------

function showUpdateOrganisationUnitForm( organisationUnitId, organisationUnitName )
{
	
	var organisationUnitId = organisationUnitId;
	var organisationUnitName = organisationUnitName;
	
	jQuery('#fullOrgUnitDetailsDiv').dialog('close');
	
	jQuery('#updateOrgUnitDetailsDiv').dialog('destroy').remove();
	jQuery('<div id="updateOrgUnitDetailsDiv">' ).load( 'showUpdateOrganisationUnitForm.action?organisationUnitId='+ organisationUnitId ).dialog({
		title: organisationUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 800,
		height: 600
	});
}

function updateOrganisationUnit()
{
	//alert("updateOrganisationUnit");
	var orgUnitId = document.getElementById('orgUnitId').value;
	var orgUnitName = document.getElementById('orgUnitName').value;
	
	//alert( orgUnitId +" --- " + orgUnitName );
	
	//url: 'updateOrganisationUnit.action?jsonAttributeValues=' + jsonAttributeValues
	
	$.ajax({
    type: "POST",
    url: 'updateOrganisationUnit.action',
    data: getParamsForDiv('updateOrgUnitDetailsDiv'),
    
    success: function( json ) {
		jQuery('#updateOrgUnitDetailsDiv').dialog('destroy').remove();
		
		//alert( url +" -- "+ data );
		showFullOrgUnitDetails( orgUnitId, orgUnitName );
		}
	});
}


function closeUpdateOrganisationUnitWindow()
{
	var orgUnitId = document.getElementById('orgUnitId').value;
	var orgUnitName = document.getElementById('orgUnitName').value;
	
	jQuery('#updateOrgUnitDetailsDiv').dialog('destroy').remove();
	showFullOrgUnitDetails( orgUnitId, orgUnitName );
	
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{
		
		orgUnitId:orgUnitId
	},
	function(){
		showById('orgUnitDetailsDiv');
		equipmentTypeChange();
	});
	
	
	
	
	
}



function showUpdateFacilityDataEntryForm( organisationUnitId, organisationUnitName )
{

	var orgUnitId = organisationUnitId;
	var orgUnitName = organisationUnitName;
	
	jQuery('#fullOrgUnitDetailsDiv').dialog('close');
	
	jQuery('#facilityDataEntryDiv').dialog('destroy').remove();
	jQuery('<div id="facilityDataEntryDiv">' ).load( 'showUpdateFacilityDataEntryForm.action?orgUnitId='+ organisationUnitId ).dialog({
		title: " Edit " + orgUnitName + " data",
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 800,
		height: 600
	});
}


function closeFacilityDataEntryWindow()
{
	var orgUnitId = document.getElementById('organisationUnitId').value;
	var orgUnitName = document.getElementById('organisationUnitName').value;
	
	jQuery('#facilityDataEntryDiv').dialog('destroy').remove();
	showFullOrgUnitDetails( orgUnitId, orgUnitName );
	
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{
		
		orgUnitId:orgUnitId
	},
	function(){
		showById('orgUnitDetailsDiv');
		equipmentTypeChange();
	});
	
}


function updateFacilityDataEntryForm()
{
	var orgUnitId = document.getElementById('organisationUnitId').value;
	var orgUnitName = document.getElementById('organisationUnitName').value;
	
	//alert( orgUnitId + "--" + orgUnitName);
	
	$.ajax({
      type: "POST",
      url: 'saveFacilityDataEntryForm.action',
      data: getParamsForDiv('facilityDataEntryDiv'),
      success: function( json ) {
		jQuery('#facilityDataEntryDiv').dialog('destroy').remove();
		showFullOrgUnitDetails( orgUnitId, orgUnitName );
      }
     });
}

			
