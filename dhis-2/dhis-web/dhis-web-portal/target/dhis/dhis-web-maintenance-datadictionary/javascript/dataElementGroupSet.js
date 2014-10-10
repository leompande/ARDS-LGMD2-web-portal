
$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

function beforeSubmit() {
  memberValidator = jQuery("#memberValidator");
  memberValidator.children().remove();

  jQuery.each(jQuery("#groupMembers").children(), function( i, item ) {
    item.selected = 'selected';
    memberValidator.append('<option value="' + item.value + '" selected="selected">' + item.value + '</option>');
  });
}

// -----------------------------------------------------------------------------
// Delete
// -----------------------------------------------------------------------------

function deleteDataElementGroupSet( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, "deleteDataElementGroupSet.action");
}

// -----------------------------------------------------------------------------
// Show Data Element Group Set details
// -----------------------------------------------------------------------------

function showDataElementGroupSetDetails( context ) {
  $.post('../dhis-web-commons-ajax-json/getDataElementGroupSet.action', { id: context.id },
    function( json ) {
      setInnerHTML('nameField', json.dataElementGroupSet.name);
      setInnerHTML('memberCountField', json.dataElementGroupSet.memberCount);

      showDetails();
    });
}

function showUpdateDataElementGroupSetForm( context ) {
  location.href = 'openUpdateDataElementGroupSet.action?id=' + context.id;
}
