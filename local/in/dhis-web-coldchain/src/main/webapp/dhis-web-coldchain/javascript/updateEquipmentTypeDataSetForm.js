jQuery(document).ready(function() {
	validation2('updateEquipmentTypeDataSetForm', function(form) {
		form.submit();
	}, {
		'beforeValidateHandler' : function() {
            $("#selectedEquipmentTypeDataSetList").find("option").attr("selected", "selected");
		},
	});
	
	checkValueIsExist( "name", "validateEquipmentType.action", {id:getFieldValue('id')});	
	
	
	jQuery("#availableDataSetList").dhisAjaxSelect({
		source: "dataSetList.action",
		iterator: "dataSets",
		connectedTo: 'selectedEquipmentTypeDataSetList',
		handler: function(item) {
			var option = jQuery("<option />");
			option.text( item.name );
			option.attr( "value", item.id );
			
			return option;
			
		}
	});		
		
});
