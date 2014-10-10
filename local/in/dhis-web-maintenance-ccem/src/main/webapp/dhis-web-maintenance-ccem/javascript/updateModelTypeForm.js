jQuery( document ).ready( function()
{validation( 'updateModelTypeForm', function( form ){ 
		form.submit() ;
	}, function(){
		selectedModelTypeAttributesValidator = jQuery( "#selectedModelTypeAttributesValidator" );
		selectedModelTypeAttributesValidator.empty();
		
		jQuery("#selectedList").find("tr").each( function( i, item ){ 
			selectedModelTypeAttributesValidator.append( "<option value='" + item.id + "' selected='true'>" + item.id + "</option>" );
			
		});
	});
	
	checkValueIsExist( "name", "validateModelType.action", {id:getFieldValue('id')});	
	
	jQuery("#availableList").dhisAjaxSelect({
		source: "getModelTypeAttributes.action",
		iterator: "modelTypeAttributes",
		connectedTo: 'selectedModelTypeAttributesValidator',
		handler: function(item) {
			var option = jQuery("<option />");
			option.text( item.name );
			option.attr( "value", item.id );
			
			var flag = false;
			jQuery("#selectedList").find("tr").each( function( k, selectedItem ){ 
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