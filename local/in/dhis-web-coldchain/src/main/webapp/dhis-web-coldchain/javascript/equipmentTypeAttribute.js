// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showEquipmentTypeAttributeDetails( equipmentTypeAttributeId )
{
	jQuery.getJSON( 'getEquipmentTypeAttribute.action', { id: equipmentTypeAttributeId },
		function ( json ) {
			setInnerHTML( 'nameField', json.equipmentTypeAttribute.name );	
			setInnerHTML( 'descriptionField', json.equipmentTypeAttribute.description );
			
			var mandatory = ( json.equipmentTypeAttribute.mandatory == 'true') ? i18n_yes : i18n_no;
			setInnerHTML( 'mandatoryField', mandatory );
			
			setInnerHTML( 'valueTypeField', json.equipmentTypeAttribute.valueType );    
	   
			showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove InvenotryType Attribute
// -----------------------------------------------------------------------------
function removeEquipmentTypeAttribute( invenotryTypeAttributeId, name )
{
	removeItem( invenotryTypeAttributeId, name, i18n_confirm_delete, 'removeEquipmentTypeAttribute.action' );	
}

ATTRIBUTE_OPTION = 
{
	selectValueType : 	function (this_)
	{
		if ( jQuery(this_).val() == "COMBO" )
		{
			jQuery("#attributeComboRow").show();
			if( jQuery("#attrOptionContainer").find("input").length ==0 ) 
			{
				ATTRIBUTE_OPTION.addOption();
				ATTRIBUTE_OPTION.addOption();
			}
		}else {
			jQuery("#attributeComboRow").hide();
		}
	},
	checkOnSubmit : function ()
	{
		if( jQuery("#valueType").val() != "COMBO" ) 
		{
			jQuery("#attrOptionContainer").children().remove();
			return true;
		}else {
			$("input","#attrOptionContainer").each(function(){ 
				if( !jQuery(this).val() )
					jQuery(this).remove();
			});
			if( $("input","#attrOptionContainer").length < 2)
			{
				alert(i118_at_least_2_option);
				return false;
			}else return true;
		}
	},
	addOption : function ()
	{
		jQuery("#attrOptionContainer").append(ATTRIBUTE_OPTION.createInput());
	},
	remove : function (this_, optionId)
	{
		
		if( jQuery(this_).siblings("input").attr("name") != "attrOptions")
		{
			jQuery.get("removeInvenotryTypeAttributeOption.action?id="+optionId,function(data){
				if( data.response == "success")
				{
					jQuery(this_).parent().parent().remove();
					showSuccessMessage( data.message );
				}else 
				{
					showErrorMessage( data.message );
				}
			});
		}else
		{
			jQuery(this_).parent().parent().remove();
		}
	},
	removeInAddForm : function(this_)
	{
		jQuery(this_).parent().parent().remove();
	},
	createInput : function ()
	{
		return "<tr><td><input type='text' name='attrOptions' /><a href='#' style='text-decoration: none; margin-left:0.5em;' title='"+i18n_remove_option+"'  onClick='ATTRIBUTE_OPTION.remove(this,null)'>[ - ]</a></td></tr>";
	}
}