isAjax = true;
var generateResultParams = "";

function orgunitSelected( orgUnits, orgUnitNames )
{
	var width = jQuery('#programIdAddPatient').width();
	jQuery('#programIdAddPatient').width(width-30);
	showById( "programLoader" );
	disable('programIdAddPatient');
	disable('listPatientBtn');
	showById('mainLinkLbl');
	showById('searchDiv');
	hideById('listEventDiv');
	hideById('listEventDiv');
	hideById('patientDashboard');
	hideById('smsManagementDiv');
	hideById('sendSmsFormDiv');
	hideById('editPatientDiv');
	hideById('resultSearchDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationPatientDiv');

	clearListById('programIdAddPatient');
	$('#contentDataRecord').html('');
	setFieldValue('orgunitName', orgUnitNames[0]);
	setFieldValue('orgunitId', orgUnits[0]);
	jQuery.get("getPrograms.action",{}, 
		function(json)
		{
			var count = 0;
			for ( i in json.programs ) {
				if(json.programs[i].type==1){
					count++;
					jQuery( '#programIdAddPatient').append( '<option value="' + json.programs[i].id +'" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
				}
			}
			if(count==0){
				jQuery( '#programIdAddPatient').prepend( '<option value="" selected>' + i18n_none_program + '</option>' );
			}
			else if(count>1){
				jQuery( '#programIdAddPatient').prepend( '<option value="" selected>' + i18n_please_select + '</option>' );
				enable('listPatientBtn');
			}
			
			enableBtn();
			hideById('programLoader');
			jQuery('#programIdAddPatient').width(width);
			enable('programIdAddPatient');
		});
}

selection.setListenerFunction( orgunitSelected );

// --------------------------------------------------------------------
// List all events
// --------------------------------------------------------------------

function listAllPatient()
{
	hideById('listEventDiv');
	hideById('advanced-search');
	contentDiv = 'listEventDiv';
	$('#contentDataRecord').html('');
	hideById('advanced-search');
	eventList = 1;
	
	var date = new Date();
	var d = date.getDate() - 1;
	var m = date.getMonth();
	var y1 = date.getFullYear() - 100;
	var y2 = date.getFullYear() + 100;
	var startDate = jQuery.datepicker.formatDate( dateFormat, new Date(y1, m, d) );
	var endDate = jQuery.datepicker.formatDate( dateFormat, new Date(y2, m, d) );
	
	var programId = getFieldValue('programIdAddPatient');
	var searchTexts = "stat_" + programId + "_" 
				+ startDate + "_" + endDate + "_" 
				+ getFieldValue('orgunitId') + "_true_" 
				+ getFieldValue('statusEvent');
	var followup = "";
	if( byId('followup').checked ){
		followup = "followup=true";
	}
	
	generateResultParams = followup + "&programId=" + programId + "&searchTexts=" + searchTexts;
	
	showLoader();
	jQuery('#listEventDiv').load('getSMSPatientRecords.action?' + followup,
		{
			programId:programId,
			listAll:false,
			searchTexts: searchTexts
		}, 
		function()
		{
			showById('colorHelpLink');
			showById('listEventDiv');
			hideLoader();
		});
}

// --------------------------------------------------------------------
// Search events
// --------------------------------------------------------------------

followup = true;

function advancedSearch( params )
{
	setFieldValue('listAll', "false");
	$('#contentDataRecord').html('');
	$('#listEventDiv').html('');
	hideById('listEventDiv');
	showLoader();
	params += "&programId=" + getFieldValue('programIdAddPatient');
	generateResultParams = params;
	
	$.ajax({
		url: 'getSMSPatientRecords.action',
		type:"POST",
		data: params,
		success: function( html ){
			jQuery('#listEventDiv').html(html);
			showById('colorHelpLink');
			showById('listEventDiv');
			eventList = 2;
			setTableStyles();
			hideLoader();
		}
	});
}

function exportXlsFile()
{
	var url = "getActivityPlanRecords.action?type=xls&trackingReport=true&" + generateResultParams;
	window.location.href = url;
}

// --------------------------------------------------------------------
// program tracking form
// --------------------------------------------------------------------

function programTrackingList( programStageInstanceId, isSendSMS ) 
{
	hideById('listEventDiv');
	hideById('searchDiv');
	showLoader();
	setFieldValue('sendToList', "false");
	$('#smsManagementDiv' ).load("programTrackingList.action",
		{
			programStageInstanceId: programStageInstanceId
		}
		, function(){
			hideById('mainLinkLbl');
			hideById('mainFormLink');
			hideById('searchDiv');
			hideById('listEventDiv');
			showById('smsManagementDiv');
			hideLoader();
		});
}

// --------------------------------------------------------------------
// Send SMS 
// --------------------------------------------------------------------

function showSendSmsForm()
{
	jQuery('#sendSmsToListForm').dialog({
			title: i18n_send_message,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 420,
			height: 200
		});
}

function sendSmsToList()
{
	params = getSearchParams();
	params += "&msg=" + getFieldValue( 'smsMessage' );
	params += "&programStageInstanceId=" + getFieldValue('programStageInstanceId');
	$.ajax({
		url: 'sendSMSTotList.action',
		type:"POST",
		data: params,
		success: function( json ){
			if ( json.response == "success" ) {
				var programStageName = getFieldValue('programStageName');
				var currentTime = date.getHours() + ":" + date.getMinutes();
				jQuery('#commentTB').prepend("<tr><td>" + getFieldValue("currentDate") + " " + currentTime + "</td>"
						+ "<td>" + programStageName + "</td>"
						+ "<td>" + getFieldValue( 'smsMessage' ) + "</td></tr>");
				showSuccessMessage( json.message );
			}
			else {
				showErrorMessage( json.message );
			}
			jQuery('#sendSmsFormDiv').dialog('close')
		}
	});
}

// --------------------------------------------------------------------
// Post Comments/Send Message
// --------------------------------------------------------------------

function keypressOnMessage(event, field, programStageInstanceId )
{
	var key = getKeyCode( event );
	if ( key==13 ){ // Enter
		sendSmsOnePatient( field, programStageInstanceId );
	}
}

// --------------------------------------------------------------------
// Dashboard
// --------------------------------------------------------------------

function loadDataEntry( programStageInstanceId )
{
	setInnerHTML('dataEntryFormDiv', '');
	showById('dataEntryFormDiv');
	showById('executionDateTB');
	setFieldValue( 'dueDate', '' );
	setFieldValue( 'executionDate', '' );
	disable('validationBtn');
	disableCompletedButton(true);
	disable('uncompleteBtn');
	jQuery( 'input[id=programStageInstanceId]').val( programStageInstanceId );
	
	$('#executionDate').unbind("change");
	$('#executionDate').change(function() {
		saveExecutionDate( getFieldValue('programId'), programStageInstanceId, byId('executionDate') );
	});
	
	jQuery(".stage-object-selected").removeClass('stage-object-selected');
	var selectedProgramStageInstance = jQuery( '#' + prefixId + programStageInstanceId );
	selectedProgramStageInstance.addClass('stage-object-selected');
	setFieldValue( 'programStageId', selectedProgramStageInstance.attr('psid') );
	
	showLoader();	
	$( '#dataEntryFormDiv' ).load( "dataentryform.action", 
		{ 
			programStageInstanceId: programStageInstanceId
		},function()
		{
			setFieldValue( 'programStageInstanceId', programStageInstanceId );
			var executionDate = jQuery('#executionDate').val();
			var completed = jQuery('#entryFormContainer input[id=completed]').val();
			var irregular = jQuery('#entryFormContainer input[id=irregular]').val();
			var reportDateDes = jQuery("#ps_" + programStageInstanceId).attr("reportDateDes");
			setInnerHTML('reportDateDescriptionField',reportDateDes);
			enable('validationBtn');
			if( executionDate == '' )
			{
				disable('validationBtn');
			}
			else if( executionDate != '' && completed == 'false' )
			{
				disableCompletedButton(false);
			}
			else if( completed == 'true' )
			{
				disableCompletedButton(true);
			}
			resize();
			hideLoader();
			hideById('contentDiv'); 
			jQuery('#dueDate').focus();
		});
}

function entryFormContainerOnReady(){}

// --------------------------------------------------------------------
// Show main form
// --------------------------------------------------------------------

function onClickBackBtn()
{
	showById('mainLinkLbl');
	showById('searchDiv');
	showById('listEventDiv');
	hideById('migrationPatientDiv');
	hideById('smsManagementDiv');
	hideById('patientDashboard');
	
	if( eventList == 1){
		listAllPatient();
	}
	else if( eventList == 2){
		validateAdvancedSearch();
	}
}

// load program instance history
function programTrackingReport( programInstanceId )
{
	$('#programTrackingReportDiv').load("getProgramReportHistory.action", 
		{
			programInstanceId:programInstanceId
		}).dialog(
		{
			title:i18n_program_report,
			maximize:true, 
			closable:true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width:850,
			height:500
		});
}

function getProgramStageInstanceById(programStageInstanceId)
{
	$('#tab-2').load("getProgramStageInstanceById.action", 
	{
		programStageInstanceId:programStageInstanceId
	});
}
