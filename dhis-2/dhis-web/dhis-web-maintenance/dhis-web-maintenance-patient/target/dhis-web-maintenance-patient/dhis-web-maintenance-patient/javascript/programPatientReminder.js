$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

function showUpdateProgramPatientReminder( context ) {
  location.href = 'showUpdateProgramPatientReminder.action?programId=' + getFieldValue('programId') + "&id=" + context.id;
}

function removeProgramPatientReminder( context ) {
	var result = window.confirm( i18n_confirm_delete + "\n\n" + context.name );
    
    if ( result )
    {
    	setHeaderWaitMessage( i18n_deleting );
    	
    	$.postJSON("removeProgramPatientReminder.action",
    	    {
    	        programId: getFieldValue('programId'),
				reminderId: context.id		
    	    },
    	    function( json )
    	    { 
    	    	if ( json.response == "success" )
    	    	{
					jQuery( "tr#tr" + context.id ).remove();
	                
					jQuery( "table.listTable tbody tr" ).removeClass( "listRow listAlternateRow" );
	                jQuery( "table.listTable tbody tr:odd" ).addClass( "listAlternateRow" );
	                jQuery( "table.listTable tbody tr:even" ).addClass( "listRow" );
					jQuery( "table.listTable tbody" ).trigger("update");
  
					setHeaderDelayMessage( i18n_delete_success );
    	    	}
    	    	else if ( json.response == "error" )
    	    	{ 
					setHeaderMessage( json.message );
    	    	}
    	    }
    	);
    }
}

function showPatientReminderDetails( context ) {
 
  jQuery.getJSON("getPatientReminder.action", {
    id: context.id
  }, function( json ) {
    setInnerHTML('nameField', json.patientReminder.name);
    setInnerHTML('daysAllowedSendMessageField', json.patientReminder.daysAllowedSendMessage);
    setInnerHTML('templateMessageField', json.patientReminder.templateMessage);
    setInnerHTML('dateToCompareField', json.patientReminder.dateToCompare);
	
	var map = sendToMap();
	setInnerHTML( 'sendToField', map[json.patientReminder.sendTo] ); 
	
	var whenToSend = i18n_days_scheduled;
	if( json.patientReminder.whenToSend=='3'){
		whenToSend = i18n_complete_program;
	}
	else if( json.patientReminder.whenToSend=='1'){
		whenToSend = i18n_program_enrollment;
	}
	setInnerHTML('whenToSendField', whenToSend);
    
	var type = i18n_direct_sms;
	if( json.patientReminder.messageType=='2'){
		whenToSend = i18n_message;
	}
	else if( json.patientReminder.whenToSend=='3'){
		whenToSend = i18n_both;
	}
    setInnerHTML('messageTypeField', json.patientReminder.messageType);
	
    setInnerHTML('userGroupField', json.patientReminder.userGroup);
    showDetails();
  });
}

function sendToMap()
{
	var typeMap = [];
	typeMap['1'] = i18n_patient_sms_only;
	typeMap['2'] = i18n_orgunit_phone_number_sms_only;
	typeMap['3'] = i18n_attribute_users;
	typeMap['4'] = i18n_all_users_in_orgunit_registered;
	typeMap['5'] = i18n_user_group;
	return typeMap;
}

function showHideUserGroup() {
  jQuery(".sendTo").each(function( i, item ) {
    var numb = i + 1;
    if( item.value == 5 ) {
      showById('tr' + numb);
    }
    else
      hideById('tr' + numb);
  });
}

function insertParams( paramValue ) {
  var templateMessage = paramValue;
  insertTextCommon('templateMessage', templateMessage);
  getMessageLength();
}

function whenToSendOnChange(  ) {
  var whenToSend = getFieldValue('whenToSend' );
  if( whenToSend == "" ) {
    enable('dateToCompare');
    enable('daysAllowedSendMessage' );
    enable('time');
  }
  else {
    disable('dateToCompare');
    disable('daysAllowedSendMessage');
    disable('time');
  }
}

function getMessageLength( ) {
  var message = getFieldValue('templateMessage' );
  var length = 0;
  var idx = message.indexOf('{');
  while( idx >= 0 ) {
    length += message.substr(0, idx).length;
    var end = message.indexOf('}');
    if( end >= 0 ) {
      message = message.substr(end + 1, message.length);
      idx = message.indexOf('{');
    }
  }
  length += message.length;
  setInnerHTML('messageLengthTD' , length + " " + i18n_characters_without_params);
  if( length >= 160 ) {
    jQuery('#templateMessage').attr('maxlength', 160);
  }
  else {
    jQuery('#templateMessage').removeAttr('maxlength');
  }
}

function setRealDays( ) {
  var daysAllowedSendMessage = jQuery("#daysAllowedSendMessage");
  var time = jQuery("#time option:selected ").val();
  daysAllowedSendMessage.attr("realvalue", time * eval(daysAllowedSendMessage).val());
}

function onchangeUserGroup() {
  var value = document.getElementById('sendTo').value;
  
  if( value == "1" || value == "3" ) {
    setFieldValue('messageType', '1');
    disable('messageType');
  }
  else {
    enable('messageType');
  }
  
  if( value == "5" ){
	enable('userGroup');
  }
  else{
	disable('userGroup');
  }
}
