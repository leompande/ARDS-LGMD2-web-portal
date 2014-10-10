$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUpdatePatientAttributeGroupForm( context ) {
  location.href = 'showUpdatePatientAttributeGroupForm.action?id=' + context.id;
}

function showPatientAttributeGroupDetails( context ) {
  jQuery.getJSON('getPatientAttributeGroup.action', { id: context.id },
    function( json ) {
      setInnerHTML('nameField', json.patientAttributeGroup.name);
      setInnerHTML('descriptionField', json.patientAttributeGroup.description);
      setInnerHTML('noAttributeField', json.patientAttributeGroup.noAttribute);

      showDetails();
    });
}

// -----------------------------------------------------------------------------
// Remove Patient Attribute
// -----------------------------------------------------------------------------

function removePatientAttributeGroup( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removePatientAttributeGroup.action');
}
