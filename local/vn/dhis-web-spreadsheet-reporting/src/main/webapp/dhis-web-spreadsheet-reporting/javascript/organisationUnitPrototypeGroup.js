function beforeSubmit()
{
    memberValidator = jQuery( "#memberValidator" );
    memberValidator.children().remove();

    jQuery.each( jQuery( "#groupMembers" ).children(), function( i, item )
    {
        item.selected = 'selected';
        memberValidator.append( '<option value="' + item.value + '" selected="selected">' + item.value + '</option>' );
    } );
}

// -----------------------------------------------------------------------------
// Show OrganisationUnitPrototype Group details
// -----------------------------------------------------------------------------

function showOrgUnitPrototypeGroupDetails( groupId )
{
	jQuery.get( 'getOrgUnitPrototypeGroup.action',
		{ id: groupId }, function( json ) {
		setInnerHTML( 'nameField', json.orgUnitPrototypeGroup.name );
		setInnerHTML( 'memberCountField', json.orgUnitPrototypeGroup.memberCount );

		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove OrganisationUnitPrototype Group
// -----------------------------------------------------------------------------

function removeOrgUnitPrototypeGroup( groupId, groupName )
{
    removeItem( groupId, groupName, i18n_confirm_delete, 'removeOrgUnitPrototypeGroup.action' );
}
