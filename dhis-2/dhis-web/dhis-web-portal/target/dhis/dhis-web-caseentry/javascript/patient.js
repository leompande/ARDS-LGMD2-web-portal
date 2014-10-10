function organisationUnitSelected( orgUnits, orgUnitNames )
{	
	showById('selectDiv');
	showById('searchDiv');
	showById( "programLoader" );
	disable('programIdAddPatient');
	showById('mainLinkLbl');
	hideById('listPatientDiv');
	hideById('editPatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationPatientDiv');
	hideById('patientDashboard');
	enable('listPatientBtn');
	enable('addPatientBtn');
	enable('advancedSearchBtn');
	enable('searchObjectId');
	setInnerHTML('patientDashboard','');
	setInnerHTML('editPatientDiv','');
	
	setFieldValue("orgunitName", orgUnitNames[0]);
	
	clearListById('programIdAddPatient');
	jQuery.get("getAllPrograms.action",{}, 
		function(json)
		{
			jQuery( '#programIdAddPatient').append( '<option value="">' + i18n_view_all + '</option>' );
			for ( i in json.programs ) {
				if(json.programs[i].type==1){
					jQuery( '#programIdAddPatient').append( '<option value="' + json.programs[i].id +'" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
				}
			}
			enableBtn();
			hideById('programLoader');
			enable('programIdAddPatient');
		});
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// List && Search patients
// -----------------------------------------------------------------------------

function Patient()
{
	var patientId;
	
	this.advancedSearch = function(params)
	{
		$.ajax({
			url: 'searchRegistrationPatient.action',
			type:"POST",
			data: params,
			success: function( html ){
					setTableStyles();
					statusSearching = 1;
					setInnerHTML( 'listPatientDiv', html );
					showById('listPatientDiv');
					setFieldValue('listAll',false);
					showById('hideSearchCriteriaDiv');
					jQuery( "#loaderDiv" ).hide();
				}
			});
	};
	
	this.validate = function( programId )
	{
		setMessage('');
		if( jQuery('.underAge').prop('checked')=='true' ){
			if ( getFieldValue('representativeId') == '' )
			{
				setMessage( i18n_please_choose_representative_for_this_under_age_patient );
				$("#patientForm :input").attr("disabled", false);
				$("#patientForm").find("select").attr("disabled", false);
				return false;
			}
			
			if ( getFieldValue('relationshipTypeId') == '' )
			{
				setMessage( i18n_please_choose_relationshipType_for_this_under_age_patient );
				$("#patientForm :input").attr("disabled", false);
				$("#patientForm").find("select").attr("disabled", false);
				return false;
			}
		}
		
		var params = "";
		if( programId !== "undefined" ){
			params = "programId=" + programId + "&" 
		}
		params += getParamsForDiv('patientForm');
		$("#patientForm :input").attr("disabled", true);
		$("#patientForm").find("select").attr("disabled", true);
		var json = null;
		$.ajax({
			type: "POST",
			url: 'validatePatient.action',
			data: params,
			datatype: "json",
			async: false,
			success: function(data) {
				json = data;
			}
		});

		var response = json.response;
		var message = json.message;
		
		if ( response == 'success' )
		{
			if( message == 0 ){
				removeDisabledIdentifier();
				return true;
			}
			else {
			
				if( message == 1 ){
					setMessage( i18n_adding_patient_failed + ':' + '\n' + i18n_duplicate_identifier );
				}
				else if( message == 2 ){
					setMessage( i18n_adding_patient_failed + ':' + '\n' + i18n_this_patient_could_not_be_enrolled_please_check_validation_criteria );
				}
				
				$("#patientForm :input").attr("disabled", false);
				$("#patientForm").find("select").attr("disabled", false);
				return false;
			}
		}
		else
		{
			if ( response == 'error' )
			{
				setMessage( i18n_adding_patient_failed + ':' + '\n' + message );
			}
			else if ( response == 'input' )
			{
				setMessage( message );
			}
			else if( response == 'duplicate' )
			{
				showListPatientDuplicate(data, false);
			}
			
			$("#patientForm :input").attr("disabled", false);
			$("#patientForm").find("select").attr("disabled", false);
			return false;
		}
	};
	
	this.add = function( programId, related, params, isContinue)
	{			
		if( !this.validate(programId) ) return;
		
		 $.ajax({
		  type: "POST",
		  url: 'addPatient.action',
		  data: params,
		  success: function(json) {
			if(json.response=='success')
			{
				var patientUid = json.message.split('_')[0];
				var patientId = json.message.split('_')[1];
				var	dateOfIncident = jQuery('#patientForm [id=dateOfIncident]').val();
				var enrollmentDate = jQuery('#patientForm [id=enrollmentDate]').val();
				
				// Enroll patient into the program
				if( programId && enrollmentDate!=undefined )
				{
					jQuery.postJSON( "saveProgramEnrollment.action",
					{
						patientId: patientId,
						programId: programId,
						dateOfIncident: dateOfIncident,
						enrollmentDate: enrollmentDate
					}, 
					function( json ) 
					{    
						if(isContinue){
							jQuery("#patientForm :input").each( function(){
								var type = $(this).attr('type'),
									id = $(this).attr('id');
								
								if( type != 'button' && type != 'submit' && id != 'enrollmentDate' )
								{
									$(this).val("");
								}
							});
							$("#patientForm :input").prop("disabled", false);
							$("#patientForm").find("select").prop("disabled", false);
						}
						else{
							showPatientDashboardForm( patientUid );
						}
					});
				}
				else if(isContinue){
						jQuery("#patientForm :input").each( function(){
							var type = $(this).attr('type'),
								id = $(this).attr('id');
						
							if( type != 'button' && type != 'submit' && id != 'enrollmentDate' )
							{
								$(this).val("");
							}
						});
						$("#patientForm :input").prop("disabled", false);
						$("#patientForm").find("select").prop("disabled", false);
				}
				else
				{
					$("#patientForm :input").attr("disabled", false);
					$("#patientForm").find("select").attr("disabled", false);
					showPatientDashboardForm( patientUid );
				}
			}
		  }
		 });
	};
	
	this.update = function()
	{
		if( !this.validate(getFieldValue('programIdAddPatient')) ) return;
		
		var params = 'programId=' + getFieldValue('programIdAddPatient') 
		+ '&' + getParamsForDiv('editPatientDiv');
		$.ajax({
		  type: "POST",
		  url: 'updatePatient.action',
		  data: params,
		  success: function( json ) {
				showPatientDashboardForm( getFieldValue('uid') );
				$("#patientForm :input").attr("disabled", false);
				$("#patientForm").find("select").attr("disabled", false);
		  }
		 });
	};
	
	this.remove = function( confirm_delete_patient )
	{
		removeItem( this.patientId, "", confirm_delete_patient, 'removePatient.action' );
	};
	
}

Patient.listAll = function()
{
	jQuery('#loaderDiv').show();
	contentDiv = 'listPatientDiv';
	if( getFieldValue('programIdAddPatient')=='')
	{
		jQuery('#listPatientDiv').load('searchRegistrationPatient.action',{
				listAll:true
			},
			function(){
				setTableStyles();
				statusSearching = 0;
				showById('listPatientDiv');
				jQuery('#loaderDiv').hide();
			});
	}
	else 
	{
		jQuery('#listPatientDiv').load('searchRegistrationPatient.action',{
				listAll:false,
				searchByUserOrgunits: false,
				searchBySelectedOrgunit: true,
				programId: getFieldValue('programIdAddPatient'),
				searchTexts: 'prg_' + getFieldValue('programIdAddPatient'),
				statusEnrollment: getFieldValue('statusEnrollment')
			},
			function(){
				setTableStyles();
				statusSearching = 0;
				showById('listPatientDiv');
				jQuery('#loaderDiv').hide();
			});
	}
	
}

function listAllPatient()
{
	jQuery('#loaderDiv').show();
	hideById('listPatientDiv');
	hideById('editPatientDiv');
	hideById('migrationPatientDiv');
	hideById('advanced-search');
	showById('searchByIdTR');
	Patient.listAll();
}

function advancedSearch( params )
{
	var patient = new Patient();
	patient.advancedSearch( params );
}

// -----------------------------------------------------------------------------
// Remove patient
// -----------------------------------------------------------------------------

function removePatient( patientId )
{
	var patient = new Patient();
	patient.patientId = patientId;
	patient.remove( i18n_confirm_delete_patient );
}

// -----------------------------------------------------------------------------
// Add Patient
// -----------------------------------------------------------------------------

function addPatient( programId, related, isContinue )
{		
	var patient = new Patient();
	var params = 'programId=' + programId + '&' + getParamsForDiv('patientForm');
	patient.add(programId,related,params, isContinue );
	registrationProgress = true;
    return false;
}

function updatePatient()
{		
	var patient = new Patient();
	var params = getParamsForDiv('patientForm');
	patient.update();
    return false;
}

function showAddPatientForm( patientId, programId, relatedProgramId, related )
{
	hideById('listPatientDiv');
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('migrationPatientDiv');
	hideById('listRelationshipDiv');
	setInnerHTML('addRelationshipDiv','');
	setInnerHTML('patientDashboard','');
	
	jQuery('#loaderDiv').show();
	jQuery('#editPatientDiv').load('showAddPatientForm.action',
		{
			programId: programId,
			patientId: patientId,
			relatedProgramId: relatedProgramId,
			related: related
		}, function()
		{
			showById('editPatientDiv');
			if(related){
				setFieldValue('relationshipId',patientId);
			}
			jQuery('#loaderDiv').hide();
		});
	
}

// ----------------------------------------------------------------
// Click Back to main form
// ----------------------------------------------------------------

function onClickBackBtn()
{
	showById('mainLinkLbl');
	showById('selectDiv');
	showById('searchDiv');
	showById('listPatientDiv');
	
	hideById('editPatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationPatientDiv');
	setInnerHTML('patientDashboard','');
	loadPatientList();
}

function loadPatientList()
{
	hideById('editPatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('dataRecordingSelectDiv');
	hideById('dataEntryFormDiv');
	hideById('migrationPatientDiv');
	setInnerHTML('patientDashboard','');
	setInnerHTML('editPatientDiv','');

	showById('mainLinkLbl');
	showById('selectDiv');
	showById('searchDiv');
	if(statusSearching==2)
	{
		return;
	}
	else if( statusSearching == 0)
	{
		Patient.listAll();
	}
	else if( statusSearching == 1 )
	{
		validateAdvancedSearch();
	}
	else if( statusSearching == 3 )
	{
		showById('listPatientDiv');
	}
}

//------------------------------------------------------------------------------
// Load data entry form
//------------------------------------------------------------------------------

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
	setFieldValue( 'programStageInstanceId', programStageInstanceId );
			
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
		},function( )
		{
			var editDataEntryForm = getFieldValue('editDataEntryForm');
			if(editDataEntryForm=='true')
			{
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
				else if( executionDate != ''){
					if ( completed == 'false' ){
						disableCompletedButton(false);
					}
					else if( completed == 'true' ){
						disableCompletedButton(true);
					}
				}
				
				$(window).scrollTop(200);
			}
			else
			{
				blockEntryForm();
				disable('executionDate');
				hideById('inputCriteriaDiv');
			}
			
			resize();
			hideLoader();
			hideById('contentDiv');
			
			if(registrationProgress)
			{
				var reportDateToUse = selectedProgramStageInstance.attr('reportDateToUse');
				if(reportDateToUse != "undefined" && reportDateToUse!='' && $('#executionDate').val() == '' ){
					$('#executionDate').val(reportDateToUse);
					$('#executionDate').change();
				}
			}
			registrationProgress = false;
		
		} );
	
}
