var duplicate = false;
jQuery( document ).ready( function()
{
	showHideUserGroup();
	validation( 'updateProgramStageForm', function( form ){ 
		form.submit();
	}, function(){
		var selectedDataElementsValidator = jQuery( "#selectedDataElementsValidator" );
		selectedDataElementsValidator.empty();
		
		var compulsories = jQuery( "#compulsories" );
		compulsories.empty();
		
		var displayInReports = jQuery( "#displayInReports" );
		displayInReports.empty();
		
		var allowDateInFutures = jQuery( "#allowDateInFutures" );
		allowDateInFutures.empty();
		
		var daysAllowedSendMessages = jQuery( "#daysAllowedSendMessages" );
		daysAllowedSendMessages.empty();
		
		var templateMessages = jQuery( "#templateMessages" );
		templateMessages.empty();
		
		var allowProvidedElsewhere = jQuery( "#allowProvidedElsewhere" );
		allowProvidedElsewhere.empty();
		
		var sendTo = jQuery( "#sendTo" );
		sendTo.empty();
		
		var whenToSend = jQuery( "#whenToSend" );
		whenToSend.empty();
		
		var userGroup = jQuery( "#userGroup" );
		userGroup.empty();
		
		var messageType = jQuery( "#messageType" );
		messageType.empty();

		jQuery("#selectedList").find("tr").each( function( i, item ){ 
			
			selectedDataElementsValidator.append( "<option value='" + item.id + "' selected='true'>" + item.id + "</option>" );
			
			var compulsory = jQuery( item ).find( "input[name='compulsory']:first");
			var checked = compulsory.attr('checked') ? true : false;
			compulsories.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
			
			var allowProvided = jQuery( item ).find( "input[name='allowProvided']:first");
			checked = allowProvided.attr('checked') ? true : false;
			allowProvidedElsewhere.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
			
			var displayInReport = jQuery( item ).find( "input[name='displayInReport']:first");
			checked = displayInReport.attr('checked') ? true : false;
			displayInReports.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
		
			var allowDateInFuture = jQuery( item ).find( "input[name='allowDateInFuture']:first");
			checked = allowDateInFuture.attr('checked') ? true : false;
			allowDateInFutures.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
		});
		jQuery(".daysAllowedSendMessage").each( function( i, item ){ 
			var days = (jQuery(item).attr('realvalue')==undefined) ? 0 : jQuery(item).attr('realvalue');
			daysAllowedSendMessages.append( "<option value='" + days + "' selected='true'>" + days + "</option>" );
		});
		jQuery(".templateMessage").each( function( i, item ){ 
			templateMessages.append( "<option value='" + item.value + "' selected='true'>" + item.value + "</option>" );
		});
		jQuery(".sendTo").each( function( i, item ){ 
			sendTo.append( "<option value='" + item.value + "' selected='true'>" + item.value + "</option>" );
		});
		jQuery(".whenToSend").each( function( i, item ){ 
			whenToSend.append( "<option value='" + item.value + "' selected='true'>" + item.value + "</option>" );
		});
		jQuery(".messageType").each( function( i, item ){ 
			messageType.append( "<option value='" + item.value + "' selected='true'>" + item.value + "</option>" );
		});
		jQuery(".userGroup").each( function( i, item ){ 
			userGroup.append( "<option value='" + item.value + "' selected='true'>" + item.value + "</option>" );
		});
	});
	
	checkValueIsExist( "name", "validateProgramStage.action", {id:getFieldValue('programId'), programStageId:getFieldValue('id')});	
	
	jQuery("#availableList").dhisAjaxSelect({
		source: "../dhis-web-commons-ajax-json/getDataElements.action?domain=patient",
		iterator: "dataElements",
		connectedTo: 'selectedDataElementsValidator',
		handler: function(item) {
			var option = jQuery("<option />");
			option.text( item.name );
			option.attr( "value", item.id );
			
			if( item.optionSet == "true"){
				option.attr( "valuetype", "optionset" );
			}
			else{
				option.attr( "valuetype", item.type );
			}
			
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
