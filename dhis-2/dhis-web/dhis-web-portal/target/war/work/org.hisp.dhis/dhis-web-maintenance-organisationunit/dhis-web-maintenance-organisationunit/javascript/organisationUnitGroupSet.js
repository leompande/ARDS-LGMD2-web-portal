/*
 * Depends on dhis-web-commons/lists/lists.js for List functionality
 */

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOrganisationUnitGroupSetDetails( context )
{
	jQuery.post( 'getOrganisationUnitGroupSet.action', { id: context.id },
		function ( json ) {
			setInnerHTML( 'nameField', json.organisationUnitGroupSet.name );
			setInnerHTML( 'descriptionField', json.organisationUnitGroupSet.description );
			
			var compulsory = json.organisationUnitGroupSet.compulsory;
			
			setInnerHTML( 'compulsoryField', compulsory == "true" ? i18n_yes : i18n_no );
			setInnerHTML( 'memberCountField', json.organisationUnitGroupSet.memberCount );
			
			showDetails();
	});
}

function showUpdateOrganisationUnitGroupSetForm( context ) {
  location.href = 'showUpdateOrganisationUnitGroupSetForm.action?id=' + context.id;
}

// -----------------------------------------------------------------------------
// Remove organisation unit group set
// -----------------------------------------------------------------------------

function removeOrganisationUnitGroupSet( context )
{
	removeItem( context.id, context.name, confirm_to_delete_org_unit_group_set, 'removeOrganisationUnitGroupSet.action' );
}

function changeCompulsory( value )
{
	if( value == 'true' ){
		addValidatorRulesById( 'selectedGroups', {required:true} );
	}else{
		removeValidatorRulesById( 'selectedGroups' );
	}
}

function validateAddOrganisationGroupSet( form )
{
	var url = "validateOrganisationUnitGroupSet.action?";
		url += getParamString( 'selectedGroups', 'selectedGroups' );

	jQuery.postJSON( url, function( json )
	{
		if( json.response == 'success' ){
			markValid( 'selectedGroups' );
			selectAllById( 'selectedGroups' );
			form.submit();
		}else{
			markInvalid( 'selectedGroups', json.message );				
		}
	});		
}