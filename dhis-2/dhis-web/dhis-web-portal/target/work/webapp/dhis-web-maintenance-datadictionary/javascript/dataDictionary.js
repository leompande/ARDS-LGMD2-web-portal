
$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showDataDictionaryDetails( context ) {
	jQuery.post( 'getDataDictionary.action', { id: context.id },
		function ( json ) {
			setInnerHTML( 'nameField', json.dataDictionary.name );

			var description = json.dataDictionary.description;
			setInnerHTML( 'descriptionField', description ? description : '[' + i18n_none + ']' );

			var region = json.dataDictionary.region;
			setInnerHTML( 'regionField', region ? region : '[' + i18n_none + ']' );

			showDetails();
	});
}

// -----------------------------------------------------------------------------
// Change DataDictionary
// -----------------------------------------------------------------------------

function dataDictionaryChanged( list ) {
    var id = list.options[list.selectedIndex].value;

    var url = "setCurrentDataDictionary.action?id=" + id;

    window.location.href = url;
}

// -----------------------------------------------------------------------------
// Remove DataDictionary
// -----------------------------------------------------------------------------

function removeDataDictionary( context ) {
    removeItem( context.id, context.name, i18n_confirm_delete, 'removeDataDictionary.action' );
}

function showUpdateDataDictionaryForm( context ) {
  location.href = 'showUpdateDataDictionaryForm.action?id=' + context.id;
}
