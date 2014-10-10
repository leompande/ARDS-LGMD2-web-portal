
jQuery( document ).ready( function()
		{
			validation( 'addEquipmentTypeForm', function( form ){ 
				form.submit();
			}, function(){
				
				//$("#selectedEquipmentTypeAttributeList").find("option").attr("selected", "selected");
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
			
			jQuery("#availableEquipmentTypeAttributeList").dhisAjaxSelect({
				source: "equipmentTypeAttributes.action",
				iterator: "equipmentTypeAttributes",
					connectedTo: 'selectedEquipmentTypeAttributeValidator',
					handler: function(item) {
						var option = jQuery("<option />");
						option.text( item.name );
						option.attr( "value", item.id );

						return option;
					}
				});
				
			checkValueIsExist("name", "validateEquipmentType.action");
		});


/*
jQuery(document).ready(function() {
	validation2('addEquipmentTypeForm', function(form) {
		form.submit();
	}, {
		'beforeValidateHandler' : function() {
            $("#selectedEquipmentTypeAttributeList").find("option").attr("selected", "selected");
		},
	});
	
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
	
	checkValueIsExist("name", "validateEquipmentType.action");
});
*/
