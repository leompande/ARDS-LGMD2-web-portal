jQuery(document).ready(	function(){
	validation( 'updateModelTypeAttributeForm', function(form){
		if( isSubmit ) {
			form.submit(i18n_field_is_required);
		}
	}, function(){
		isSubmit = true;
		
		var fields = $("#updateModelTypeAttributeForm").serializeArray();
		jQuery.each(fields, function(i, field) {
			/*
			if(  field.name.match("^attrOption")=='attrOption' && field.value == ""){
				setInnerHTML("attrMessage", i18n_field_is_required);
				isSubmit = false;
			}
			*/
		});
	});
	
	checkValueIsExist( "name", "validateModelTypeAttribute.action", {id:getFieldValue('id')});
});		