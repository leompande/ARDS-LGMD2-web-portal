jQuery( document ).ready( function()
{
	validation( 'updateEquipmentTypeForm', function( form ){ 
		form.submit() ;
	}, function(){
		var selectedEquipmentTypeAttributeValidator = jQuery( "#selectedEquipmentTypeAttributeValidator" );
		selectedEquipmentTypeAttributeValidator.empty();
		
		var display = jQuery( "#display" );
		display.empty();
		
		jQuery("#selectedEquipmentTypeAttributeList").find("tr").each( function( i, item ){ 
			
			selectedEquipmentTypeAttributeValidator.append( "<option value='" + item.id + "' selected='true'>" + item.id + "</option>" );
			
			var forDisplay = jQuery( item ).find( "input[name='forDisplay']:first");
			var checked = forDisplay.attr('checked') ? true : false;
			display.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
			
		});
	});
	
	checkValueIsExist( "name", "validateEquipmentType.action", {id:getFieldValue('id')});
	
	jQuery("#availableEquipmentTypeAttributeList").dhisAjaxSelect({
		source: "equipmentTypeAttributes.action",
		iterator: "equipmentTypeAttributes",
		connectedTo: 'selectedEquipmentTypeAttributeValidator',
		handler: function(item) {
			var option = jQuery("<option />");
			option.text( item.name );
			option.attr( "value", item.id );
			
			var flag = false;
			jQuery("#selectedEquipmentTypeAttributeList").find("tr").each( function( k, selectedItem ){ 
				if(selectedItem.id == item.id )
				{
					flag = true;
					return;
				}
			});
			
			if(!flag) return option;
		}
	});
});




/*

jQuery(document).ready(function() {
	validation2('updateEquipmentTypeForm', function(form) {
		form.submit();
	}, {
		'beforeValidateHandler' : function() {
            $("#selectedEquipmentTypeAttributeList").find("option").attr("selected", "selected");
		},
	});
	
	checkValueIsExist( "name", "validateEquipmentType.action", {id:getFieldValue('id')});	
	
	
	jQuery("#availableEquipmentTypeAttributeList").dhisAjaxSelect({
		source: "equipmentTypeAttributes.action",
		iterator: "equipmentTypeAttributes",
		connectedTo: 'selectedEquipmentTypeAttributeList',
		handler: function(item) {
			var option = jQuery("<option />");
			option.text( item.name );
			option.attr( "value", item.id );
			
			return option;
			
		}
	});		
	
	
});
*/