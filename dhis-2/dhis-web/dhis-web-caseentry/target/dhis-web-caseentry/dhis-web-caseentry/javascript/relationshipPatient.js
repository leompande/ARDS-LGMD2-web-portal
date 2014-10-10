
//------------------------------------------------------------------------------
// Add Relationship
//------------------------------------------------------------------------------

function showAddRelationship( patientId )
{
	hideById('listRelationshipDiv');
	
	jQuery('#loaderDiv').show();
	jQuery('#addRelationshipDiv').load('showAddRelationshipForm.action',
		{
			patientId:patientId
		}, function()
		{
			showById('addRelationshipDiv');
			jQuery('#loaderDiv').hide();
		});
}

// -----------------------------------------------------------------------------
// Add Relationship Patient
// -----------------------------------------------------------------------------


//remove value of all the disabled identifier fields
//an identifier field is disabled when its value is inherited from another person ( underAge is true ) 
//we don't save inherited identifiers. Only save the representative id.
function removeRelationshipDisabledIdentifier()
{
	jQuery("#addRelationshipPatientForm :input.idfield").each(function(){
		if( jQuery(this).is(":disabled"))
			jQuery(this).val("");
	});
}


//------------------------------------------------------------------------------
// Relationship partner
//------------------------------------------------------------------------------

function manageRepresentative( patientId, partnerId )
{		
	$('#relationshipDetails').dialog('destroy').remove();
	$('<div id="relationshipDetails">' ).load( 'getPartner.action', 
		{
			patientId: patientId,
			partnerId: partnerId
		}).dialog({
			title: i18n_set_as_representative,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 400,
			height: 300
		});
}

function saveRepresentative( patientId, representativeId, copyAttribute )
{
	$.post( 'saveRepresentative.action', 
		{ 
			patientId:patientId, 
			representativeId: representativeId,
			copyAttribute: copyAttribute	
		}, saveRepresentativeCompleted );
}

function saveRepresentativeCompleted( messageElement )
{
	var type = $(messageElement).find('message').attr('type');
	var message = $(messageElement).find('message').text();
		
	if( type == 'success' )
	{
		jQuery('#relationshipDetails').dialog('close');
	}	
	else if( type == 'error' )
	{
		showErrorMessage( i18n_saving_representative_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		showWarningMessage( message );
	}
}

function removeRepresentative( patientId, representativeId )
{	
	$.post( 'removeRepresentative.action', 
		{ 
			patientId:patientId, 
			representativeId: representativeId 
		}, removeRepresentativeCompleted );
}

function removeRepresentativeCompleted( messageElement )
{
	var type = $(messageElement).find('message').attr('type');
	var message = $(messageElement).find('message').text();
	
	if( type == 'success' )
	{
		$('#relationshipDetails').dialog('close');
	}	
	else if( type == 'error' )
	{
		showErrorMessage( i18n_removing_representative_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		showWarningMessage( message );
	}
}

//-----------------------------------------------------------------------------
// Search Relationship Partner
//-----------------------------------------------------------------------------

function validateSearchPartner()
{
	hideById('searchRelationshipDiv');
	$.ajax({
		url: 'validateSearchRelationship.action',
		type:"POST",
		data: getParamsForDiv('relationshipSelectForm'),
		dataType: "xml",
		success: searchValidationCompleted
		}); 
}

function searchValidationCompleted( messageElement )
{
	messageElement = messageElement.getElementsByTagName( 'message' )[0];
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if( type == 'success' )
	{
		jQuery('#loaderDiv').show();
		$.ajax({
			type: "GET",
			url: 'searchRelationshipPatient.action',
			data: getParamsForDiv('relationshipSelectForm'),
			success: function( html ) {
				setInnerHTML('searchRelationshipDiv',html);
				showById('searchRelationshipDiv');
				jQuery('#loaderDiv').hide();
			}
		});
		return false;
	}
	else if( type == 'error' )
	{
		setHeaderMessage( i18n_searching_patient_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		setHeaderMessage( message );
	}
}

function validateAddRelationship(partnerId)
{
	var relationshipTypeId = jQuery( '#relationshipSelectForm [id=relationshipTypeId] option:selected' ).val();	
	if( relationshipTypeId==''){
		setHeaderMessage( i18n_please_select_relationship_type );
		return;
	}
	if( partnerId==null){
		setHeaderMessage( i18n_please_select_a_patient_for_setting_relationship );
		return;
	}
	addRelationship(partnerId);
}

function addRelationship(partnerId) 
{
	var relationshipTypeId = jQuery( '#relationshipSelectForm [id=relationshipTypeId] option:selected' ).val();	
	var relTypeId = relationshipTypeId.substr( 0, relationshipTypeId.indexOf(':') );
	var relName = relationshipTypeId.substr( relationshipTypeId.indexOf(':') + 1, relationshipTypeId.length );
	
	var params = 'patientId=' + getFieldValue('patientId') + 
		'&partnerId=' + partnerId + 
		'&relationshipTypeId=' + relTypeId +
		'&relationshipName=' + relName ;
	
	$.ajax({
		url: 'saveRelationship.action',
		type:"POST",
		data: params,
		dataType: "xml",
		success:  function( messageElement ) {
			messageElement = messageElement.getElementsByTagName( 'message' )[0];
			var type = messageElement.getAttribute( 'type' );
			var message = messageElement.firstChild.nodeValue;
			
			if( type == 'success' ){
				jQuery('#searchRelationshipDiv [id=tr' + partnerId + ']').css("background-color","#C0C0C0")
				setHeaderMessage( i18n_save_success );
			}	
			else if( type == 'error' ){
				setHeaderMessage( i18n_adding_relationship_failed + ':' + '\n' + message );
			}
			else if( type == 'input' ){
				setHeaderMessage( message );
			}
		}
	}); 
		
	return false;
}

//------------------------------------------------------------------------------
// Remove Relationship
//------------------------------------------------------------------------------

function removeRelationship( relationshipId, patientA, aIsToB, patientB )
{	
	removeItem( relationshipId, patientA + ' is ' + aIsToB + ' to ' + patientB, i18n_confirm_delete_relationship, 'removeRelationship.action' );
}