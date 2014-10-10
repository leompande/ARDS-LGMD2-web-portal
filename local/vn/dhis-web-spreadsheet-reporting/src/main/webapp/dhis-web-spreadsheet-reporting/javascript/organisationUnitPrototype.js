// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOrgUnitPrototypeDetails( id )
{
	jQuery.post( 'getOrgUnitPrototype.action', { id:id }, function( json )
	{
		setInnerHTML( 'nameField', json.orgUnitPrototype.name );
		setInnerHTML( 'shortNameField', json.orgUnitPrototype.shortName );
		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove OrganisationUnitPrototype
// -----------------------------------------------------------------------------

function removeOrgUnitPrototype( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'removeOrgUnitPrototype.action' );
}