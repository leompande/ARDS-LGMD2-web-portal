$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUpdatePatientIdentifierTypeForm( context ) {
  location.href = 'showUpdatePatientIdentifierTypeForm.action?id=' + context.id;
}

function showPatientIdentifierTypeDetails( context ) {
  jQuery.getJSON('getPatientIdentifierType.action', { id: context.id },
    function( json ) {
      setInnerHTML('nameField', json.patientIdentifierType.name);
      setInnerHTML('descriptionField', json.patientIdentifierType.description);

      var boolValueMap = { 'true': i18n_yes, 'false': i18n_no };
      var boolType = json.patientIdentifierType.mandatory;
      setInnerHTML('mandatoryField', boolValueMap[boolType]);

      boolType = json.patientIdentifierType.related;
      setInnerHTML('relatedField', boolValueMap[boolType]);
      setInnerHTML('noCharsField', json.patientIdentifierType.noChars);

      var valueTypeMap = { 'string': i18n_string, 'number': i18n_number, 'letter': i18n_letter_only, 'orgunitCount': i18n_orgunit_count };
      var valueType = json.patientIdentifierType.type;
      setInnerHTML('typeField', valueTypeMap[valueType]);

      showDetails();
    });
}

// -----------------------------------------------------------------------------
// Remove Patient Identifier Type
// -----------------------------------------------------------------------------

function removePatientIdentifierType( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removePatientIdentifierType.action');
}

function typeOnChange() {
  var type = getFieldValue('type');
  if( type == 'localId' ) {
    jQuery('[name=localIdField]').show();
  }
  else {
    jQuery('[name=localIdField]').hide();
  }
}
