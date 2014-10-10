jQuery(document).ready(	function(){
	validation( 'addCaseAggregationForm', function(form){
		form.submit();
	});
	
	jQuery("#tabs").tabs();
	checkValueIsExist( "aggregationDataElementId", "validateCaseAggregation.action");
	byId('name').focus();
});	