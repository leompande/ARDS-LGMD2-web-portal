jQuery(document).ready(	function(){
	validation( 'updatePatientAttributeForm', function(form){
		if( isSubmit && ATTRIBUTE_OPTION.checkOnSubmit() ) {
			form.submit(i18n_field_is_required);
		}
	}, function(){
		isSubmit = true;
		
		var fields = $("#addPatientAttributeForm").serializeArray();
		jQuery.each(fields, function(i, field) {
			if(  field.name.match("^attrOption")=='attrOption' && field.value == ""){
				setInnerHTML("attrMessage", i18n_field_is_required);
				isSubmit = false;
			}
		});
	});
	
	ATTRIBUTE_OPTION.selectValueType(byId("valueType"));
	
	checkValueIsExist( "name", "validatePatientAttribute.action", {id:getFieldValue('id')});
});		