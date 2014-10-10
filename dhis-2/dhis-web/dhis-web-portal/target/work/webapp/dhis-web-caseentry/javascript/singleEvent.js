
var _continue = false;

function orgunitSelected( orgUnits, orgUnitNames )
{	
	var width = jQuery('#programIdAddPatient').width();
	jQuery('#programIdAddPatient').width(width-30);
	showById( "programLoader" );
	disable('programIdAddPatient');
	hideById('addNewDiv');
	organisationUnitSelected( orgUnits, orgUnitNames );
	clearListById('programIdAddPatient');
	$.postJSON( 'singleEventPrograms.action', {}, function( json )
		{
			var count = 0;
			for ( i in json.programs ) {
				if( json.programs[i].type==2){
					jQuery( '#programIdAddPatient').append( '<option value="' + json.programs[i].id +'" programStageId="' + json.programs[i].programStageId + '" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
					count++;
				}
			}
			
			if(count==0){
				jQuery( '#programIdAddPatient').prepend( '<option value="" >' + i18n_none_program + '</option>' );
			}
			else if(count>1){
				jQuery( '#programIdAddPatient').prepend( '<option value="" selected>' + i18n_please_select + '</option>' );
				enable('addPatientBtn');
			}
			
			enableBtn();
			hideById('programLoader');
			jQuery('#programIdAddPatient').width(width);
			enable('programIdAddPatient');
		});
}
selection.setListenerFunction( orgunitSelected );

function showAddPatientForm()
{
	hideById('dataEntryMenu');
	showById('eventActionMenu');
	showById('nextEventLink');
	hideById('contentDiv');
	hideById('searchDiv');
	hideById('advanced-search');
	setInnerHTML('addNewDiv','');
	setInnerHTML('dataRecordingSelectDiv','');
	jQuery('#loaderDiv').show();
	jQuery('#addNewDiv').load('showEventWithRegistrationForm.action',
		{
			programId: getFieldValue('programIdAddPatient')
		}, function()
		{
			setInnerHTML('singleProgramName',jQuery('#programIdAddPatient option:selected').text());	unSave = true;
			showById('singleProgramName');
			showById('addNewDiv');
			jQuery('#loaderDiv').hide();
		});
}

function showUpdatePatientForm( patientId )
{
	hideById('dataEntryMenu');
	showById('eventActionMenu');
	hideById('nextEventLink');
	setInnerHTML('singleProgramName',jQuery('#programIdAddPatient option:selected').text());	
	showById('singleProgramName');
	setInnerHTML('addNewDiv','');
	unSave = false;
	showSelectedDataRecoding(patientId, getFieldValue('programIdAddPatient'));
}

function addEventForPatientForm( divname )
{
	jQuery("#" + divname + " [id=checkDuplicateBtn]").click(function() {
		checkDuplicate( divname );
	});
}

function validateData()
{
	var params = "programId=" + getFieldValue('programIdAddPatient') + "&" + getParamsForDiv('patientForm');
	$("#patientForm :input").attr("disabled", true);
	$("#entryForm :input").attr("disabled", true);
	$.ajax({
		type: "POST",
		url: 'validatePatient.action',
		data: params,
		success: function( data ){
			var type = jQuery(data).find('message').attr('type');
			var message = jQuery(data).find('message').text();
			
			if ( type == 'success' )
			{
				if( message == 0 ){
					removeDisabledIdentifier( );
					addPatient();
				}
				else if( message == 1 ){
					showErrorMessage( i18n_adding_patient_failed + ':' + '\n' + i18n_duplicate_identifier );
				}
				else if( message == 2 ){
					showErrorMessage( i18n_adding_patient_failed + ':' + '\n' + i18n_this_patient_could_not_be_enrolled_please_check_validation_criteria );
				}
			}
			else
			{
				$("#patientForm :input").attr("disabled", true);
				if ( type == 'error' )
				{
					showErrorMessage( i18n_adding_patient_failed + ':' + '\n' + message );
				}
				else if ( type == 'input' )
				{
					showWarningMessage( message );
				}
				else if( type == 'duplicate' )
				{
					showListPatientDuplicate(data, false);
				}
					
				$("#patientForm :input").attr("disabled", false);
			}
		}
    });	
}

function addPatient()
{
	$.ajax({
		type: "POST",
		url: 'addPatient.action',
		data: getParamsForDiv('patientForm'),
		success: function(json) {
			var patientId = json.message.split('_')[0];
			addData( getFieldValue('programIdAddPatient'), patientId );
		}
     });
}

function addData( programId, patientId )
{		
	var params = "programId=" + getFieldValue('programIdAddPatient');
		params += "&patientId=" + patientId;
		params += "&" + getParamsForDiv('entryForm');
		
	$.ajax({
		type: "POST",
		url: 'saveValues.action',
		data: params,
		success: function(json) {
			if( _continue==true )
			{
				$("#patientForm :input").attr("disabled", false);
				$("#entryForm :input").attr("disabled", false);
				jQuery('#patientForm :input').each(function()
				{
					var type=$( this ).attr('type');
					if(type=='checkbox'){
						this.checked = false;
					}
					if(type!='button'){
						$( this ).val('');
					}
					enable(this.id);
				});
				jQuery('#entryForm :input').each(function()
				{
					var type=$( this ).attr('type');
					if(type=='checkbox'){
						this.checked = false;
					}
					else if(type!='button'){
						$( this ).val('');
					}
				});
			}
			else
			{
				setInnerHTML('singleProgramName','');
				hideById('addNewDiv');
				if( getFieldValue('listAll')=='true'){
					listAllPatient();
				}
				else{
					showById('searchDiv');
					showById('contentDiv');
				}
			}
			showSuccessMessage( i18n_save_success );
		}
     });
    return false;
}

function showEntryFormDiv()
{
	hideById('singleEventForm');
	jQuery("#resultSearchDiv").dialog("close");
}

function removeDisabledIdentifier()
{
	jQuery("input.idfield").each(function(){
		if( jQuery(this).is(":disabled"))
			jQuery(this).val("");
	});
}

function backEventList()
{
	showById('dataEntryMenu');
	hideById('eventActionMenu');
	hideById('singleProgramName');
	showSearchForm();
	if( getFieldValue('listAll')=='true'){
		listAllPatient();
	}
	hideById('backBtnFromEntry');
}

// --------------------------------------------------------
// Check an available person allowed to enroll a program
// --------------------------------------------------------

function validateAllowEnrollment( patientId, programId  )
{	
	jQuery.getJSON( "validatePatientProgramEnrollment.action",
		{
			patientId: patientId,
			programId: programId
		}, 
		function( json ) 
		{    
			jQuery('#loaderDiv').hide();
			hideById('message');
			var type = json.response;
			if ( type == 'success' ){
				showSelectedDataRecoding(patientId, programId );
			}
			else if ( type == 'input' ){
				showWarningMessage( json.message );
			}
		});
}

function completedAndAddNewEvent()
{
	_continue=true;
	jQuery("#singleEventForm").submit();
}