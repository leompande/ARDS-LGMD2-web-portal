jQuery(document).ready(	function(){
	
	validation( 'uploadModelTypeImageForm', function(form){
		form.submit();
	}, function(){
		isSubmit = true;
		
	});
	
	checkValueIsExist( "name", "validateModelType.action", {id:getFieldValue('id')});	
});
