jQuery(document).ready(
	function()
		{
			validation( 'validationCriteriaForm', function( form ){			
				form.submit();
			});
	
		checkValueIsExist( "name", "validateValidationCriteria.action", {id:getFieldValue('id')});
});