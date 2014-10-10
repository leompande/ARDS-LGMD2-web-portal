jQuery( document ).ready( function()
{
	validation( 'addModelTypeForm', function( form ){ 
		form.submit();
	}, function(){
		selectedModelTypeAttributesValidator = jQuery( "#selectedModelTypeAttributesValidator" );
		selectedModelTypeAttributesValidator.empty();
		
		jQuery("#selectedList").find("tr").each( function( i, item ){ 
			selectedModelTypeAttributesValidator.append( "<option value='" + item.id + "' selected='true'>" + item.id + "</option>" );
		});
	});
	
	jQuery("#availableList").dhisAjaxSelect({
			source: "getModelTypeAttributes.action",
			iterator: "modelTypeAttributes",
			connectedTo: 'selectedModelTypeAttributesValidator',
			handler: function(item) {
				var option = jQuery("<option />");
				option.text( item.name );
				option.attr( "value", item.id );
				
				return option;
				
			}
		});
		
	checkValueIsExist( "name", "validateModelType.action");	
});

