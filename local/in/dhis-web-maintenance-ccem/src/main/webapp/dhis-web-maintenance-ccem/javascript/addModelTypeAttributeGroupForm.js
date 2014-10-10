

jQuery( document ).ready( function()
{
	validation( 'addModelTypeAttributeGroupForm', function( form ){ 
		form.submit();
	}, function(){
		selectedModelTypeAttributesValidator = jQuery( "#selectedModelTypeAttributesValidator" );
		selectedModelTypeAttributesValidator.empty();
		
		jQuery("#selectedList").find("tr").each( function( i, item ){ 
			selectedModelTypeAttributesValidator.append( "<option value='" + item.id + "' selected='true'>" + item.id + "</option>" );
		});
	});
	
	var modelTypeId = document.getElementById("modelTypeId").value;
	//alert( modelTypeId );
	jQuery("#availableList").dhisAjaxSelect({
			source: "getModelTypeAttributes.action?modelTypeId="+ modelTypeId,
			iterator: "modelTypeAttributes",
			connectedTo: 'selectedModelTypeAttributesValidator',
			handler: function(item) {
				var option = jQuery("<option />");
				option.text( item.name );
				option.attr( "value", item.id );
				
				return option;
				
			}
		});
		
	checkValueIsExist( "name", "validateModelTypeAttributeGroup.action");	
});