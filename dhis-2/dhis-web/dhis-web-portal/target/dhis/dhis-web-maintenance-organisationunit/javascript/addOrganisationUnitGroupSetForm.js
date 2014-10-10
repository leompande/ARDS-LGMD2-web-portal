jQuery(document).ready(function() {
	validation2('addOrganisationUnitGroupSetForm', function(form) {
	    form.submit();
	}, {
		'beforeValidateHandler' : function() {
			selectAllById('selectedGroups');
		},
		'rules' : getValidationRules("organisationUnitGroupSet")
	});

	checkValueIsExist("name", "validateOrganisationUnitGroupSet.action");

	changeCompulsory(getFieldValue('compulsory'));
});
