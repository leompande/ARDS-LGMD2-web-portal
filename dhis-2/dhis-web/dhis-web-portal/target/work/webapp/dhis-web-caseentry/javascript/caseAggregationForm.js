jQuery(document).ready(	function() {
	var rules = {
		dataSetId: {
			required:true
		},
		sDateLB: {
			required:true
		},
		eDateLB: {
			required:true
		}
	};
	
	validation2( 'caseAggregationForm', function(form) {
		validationCaseAggregation();
	},{
		'rules': rules
	});
}); 
