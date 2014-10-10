
$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUpdatePatientAttributeForm( context ) {
  location.href = 'showUpdatePatientAttributeForm.action?id=' + context.id;
}

function showPatientAttributeDetails( context ) {
	jQuery.getJSON( 'getPatientAttribute.action', { id: context.id },
		function ( json ) {
			setInnerHTML( 'nameField', json.patientAttribute.name );	
			setInnerHTML( 'descriptionField', json.patientAttribute.description );
			var mandatory = ( json.patientAttribute.mandatory == 'true') ? i18n_yes : i18n_no;
			setInnerHTML( 'mandatoryField', mandatory );
			var inherit = ( json.patientAttribute.inherit == 'true') ? i18n_yes : i18n_no;
			setInnerHTML( 'inheritField', inherit );
			
			var valueType = json.patientAttribute.valueType;
			var typeMap = patientAttributeTypeMap();
			setInnerHTML( 'valueTypeField', typeMap[valueType] );    
			
			showDetails();
	});
}

function patientAttributeTypeMap()
{
	var typeMap = [];
	typeMap['number'] = i18n_number;
	typeMap['string'] = i18n_text;
	typeMap['bool'] = i18n_yes_no;
	typeMap['trueOnly'] = i18n_yes_only;
	typeMap['date'] = i18n_date;
	typeMap['phoneNumber'] = i18n_phone_number;
	typeMap['trackerAssociate'] = i18n_tracker_associate;
	typeMap['combo'] = i18n_attribute_combo_type;
	return typeMap;
}

// -----------------------------------------------------------------------------
// Remove Patient Attribute
// -----------------------------------------------------------------------------

function removePatientAttribute( context )
{
	removeItem( context.id, context.name, i18n_confirm_delete, 'removePatientAttribute.action' );
}

ATTRIBUTE_OPTION = 
{
	selectValueType : 	function (this_)
	{
		if ( jQuery(this_).val() == "combo" )
		{
			showById("attributeComboRow");
			if( jQuery("#attrOptionContainer").find("input").length ==0 ) 
			{
				ATTRIBUTE_OPTION.addOption();
				ATTRIBUTE_OPTION.addOption();
			}
		}
		else if (jQuery(this_).val() == "calculated"){
			if( jQuery("#availableAttribute option").length == 0 )
			{
				jQuery.getJSON( 'getCalPatientAttributeParams.action', { },
					function ( json ) {
						var patientAttributes = jQuery("#availableAttribute");
						patientAttributes.append( "<option value='[current_date:0]' title='" + i18n_current_date + "'>" + i18n_current_date + "</option>" );
						for ( i in json.programs ) 
						{ 
							var id = "[PG:" + json.programs[i].id + ".dateOfIncident]";
							patientAttributes.append( "<option value='" + id + "' title='" + json.programs[i].name + "( " +  i18n_incident_date + " )" + "'>" + json.programs[i].name + "( " +  i18n_incident_date + " )" + "</option>" );
							var id = "[PG:" + json.programs[i].id + ".enrollmentDate]";
							patientAttributes.append( "<option value='" + id + "' title='" + json.programs[i].name + "( " +  i18n_enrollment_date + " )" + "'>" + json.programs[i].name + "( " +  i18n_enrollment_date + " )" + "</option>" );
						}
						for ( i in json.patientAttributes ) 
						{ 
							var id = "[CA:" + json.patientAttributes[i].id + "]";
							patientAttributes.append( "<option value='" + id + "' title='" + json.patientAttributes[i].name + "'>" + json.patientAttributes[i].name + "</option>" );
						}
				});
			}
			hideById("attributeComboRow");
		}
		else
		{
			hideById("attributeComboRow");
		}
		
	},
	checkOnSubmit : function ()
	{
		if( jQuery("#valueType").val() != "combo" ) 
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
			jQuery.get("removePatientAttributeOption.action?id="+optionId,function(data){
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
