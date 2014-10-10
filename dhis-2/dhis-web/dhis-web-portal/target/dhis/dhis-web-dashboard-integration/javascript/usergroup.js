$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

function editUserGroupForm( context ) {
  location.href = 'editUserGroupForm.action?userGroupId=' + context.id;
}

// -----------------------------------------------------------------------------
// Usergroup functionality
// -----------------------------------------------------------------------------

function showUserGroupDetails( context ) {
  jQuery.post('getUserGroup.action', { userGroupId: context.id },
    function( json ) {
      setInnerHTML('nameField', json.userGroup.name);
      setInnerHTML('noOfGroupField', json.userGroup.noOfUsers);

      showDetails();
    });
}

function removeUserGroup( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeUserGroup.action');
}
