$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

function showUpdateValidationCriteriaForm( context ) {
  location.href = 'showUpdateValidationCriteriaForm.action?id=' + context.id + '&programId=' + getFieldValue('programId');
}

// -----------------------------------------------------------------------------
// Remove Criteria
// -----------------------------------------------------------------------------

function removeCriteria( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeValidationCriteria.action');
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showValidationCriteriaDetails( context ) {
  jQuery.getJSON('getValidationCriteria.action', { id: context.id, programId: getFieldValue('programId') }, function( json ) {
    setInnerHTML('nameField', json.validationCriteria.name);
    setInnerHTML('descriptionField', json.validationCriteria.description);

    var property = json.validationCriteria.property;
    var operator = json.validationCriteria.operator;
    var value = json.validationCriteria.value;

    // get operator
    if( operator == 0 ) {
      operator = '=';
    } else if( operator == -1 ) {
      operator = '<';
    } else {
      operator = '>';
    }

    setInnerHTML('criteriaField', property + " " + operator + " " + value);
    showDetails();
  });
}

// ----------------------------------------------------------------------------------------
// Show div to Add or Update Validation-Criteria
// ----------------------------------------------------------------------------------------

function showDivValue() {
  var value = getFieldValue('value');
  var property = jQuery('#property option:selected');
  var propertyName = property.val();
  if( propertyName != '' && property.attr('opt')!='') {
	var opts = property.attr('opt').split(";");
	var selectField = "<select id='value' name='value' class=\"{validate:{required:true}}\" style=\"width:200px;\">";
	for(var i=1;i<opts.length;i++){
		selectField += "<option value=\"" + opts[i] + "\" >" + opts[i] + "</option>";
	}
	selectField += "</select>";
	setInnerHTML('valueTD', selectField);
  }
  else
  {
	var inputField = "<input id='value' name='value' class=\"{validate:{required:true}}\" style=\"width:200px;\"/>";
  }
  setFieldValue('value', value);
}

function fillValue( value ) {
  byId('value').value = value;
}
