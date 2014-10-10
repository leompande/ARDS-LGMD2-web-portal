jQuery(document).ready(	function(){
	
	validation( 'updateCaseAggregationForm', function(form){
		form.submit();
	}); 
	
	checkValueIsExist( "aggregationDataElementId", "validateCaseAggregation.action", {id:getFieldValue('id')});
	
	byId('name').focus();
	jQuery("#tabs").tabs();
		
});	