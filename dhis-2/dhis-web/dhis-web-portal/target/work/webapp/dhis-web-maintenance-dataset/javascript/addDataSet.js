jQuery(document).ready(function() {
	validation2('addDataSetForm', function(form) {
		form.submit();
	}, {
		'beforeValidateHandler' : function() {
            $("#dataElementsSelectedList").find("option").attr("selected", "selected");
            $("#indicatorsSelectedList").find("option").attr("selected", "selected");
		},
		'rules' : getValidationRules("dataSet")
	});

	checkValueIsExist("code", "validateDataSet.action");
});
