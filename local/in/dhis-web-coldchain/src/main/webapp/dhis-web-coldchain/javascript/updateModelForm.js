jQuery(document).ready(	function(){
	validation( 'updateModelForm', function(form){
		form.submit();
	}, function(){
		isSubmit = true;
		
	});
	
	checkValueIsExist( "name", "validateModel.action", {id:getFieldValue('id')});
});
