jQuery(document).ready(function() {
	validation2('updateOrganisationUnitGroupSetForm', function(form) {
	    form.submit();
	}, {
		'beforeValidateHandler' : function() {
			selectAllById('selectedGroups');
		},
		'rules' : getValidationRules("organisationUnitGroupSet")
	});

	changeCompulsory(getFieldValue('compulsory'));
});
