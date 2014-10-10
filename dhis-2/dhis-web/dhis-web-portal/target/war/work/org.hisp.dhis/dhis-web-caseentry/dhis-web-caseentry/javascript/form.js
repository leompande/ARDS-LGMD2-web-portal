
function organisationUnitSelected( orgUnits, orgUnitNames )
{
	jQuery('#createNewEncounterDiv').dialog('close');
	setInnerHTML( 'contentDiv', '' );
	setFieldValue( 'orgunitName', orgUnitNames[0] );
	
	hideById('dataEntryFormDiv');
	hideById('dataRecordingSelectDiv');
	showById('searchDiv');
	
	enable('searchObjectId');
	jQuery('#searchText').removeAttr('readonly');
	enable('searchBtn');	
	enable('listPatientBtn');
}
//------------------------------------------------------------------------------
// Load data entry form
//------------------------------------------------------------------------------

function loadDataEntry( programStageInstanceId )
{
	setInnerHTML('dataEntryFormDiv', '');
	showById('executionDateTB');
	showById('dataEntryFormDiv');
	setFieldValue( 'dueDate', '' );
	setFieldValue( 'executionDate', '' );
	disableCompletedButton(true);
	disable('uncompleteBtn');
	jQuery( 'input[id=programStageInstanceId]').val(programStageInstanceId );
			
	showLoader();	
	$( '#dataEntryFormDiv' ).load( "dataentryform.action", 
		{ 
			programStageInstanceId: programStageInstanceId
		},function( )
		{
			var executionDate = jQuery('#dataRecordingSelectDiv input[id=executionDate]').val();
			var completed = jQuery('#entryFormContainer input[id=completed]').val();
			var irregular = jQuery('#entryFormContainer input[id=irregular]').val();
			showById('inputCriteriaDiv');
			if( executionDate != '' && completed == 'false' )
			{
				disableCompletedButton(false);
			}
			else if( completed == 'true' )
			{
				disableCompletedButton(true);
			}
			hideLoader();
			hideById('contentDiv'); 
		} );
}

//--------------------------------------------------------------------------------------------
// Show search-form
//--------------------------------------------------------------------------------------------

function showSearchForm()
{
	hideById('dataRecordingSelectDiv');
	hideById('dataEntryFormDiv');
	hideById('addNewDiv');
	showById('searchDiv');
	showById('contentDiv');
	showById('mainLinkLbl');
	jQuery('#createNewEncounterDiv').dialog('close');
	jQuery('#resultSearchDiv').dialog('close');
}

//--------------------------------------------------------------------------------------------
// Show all patients in select orgunit
//--------------------------------------------------------------------------------------------

isAjax = true;
function listAllPatient()
{
	hideById('advanced-search');
	showLoader();
	jQuery('#contentDiv').load( 'listAllPatients.action',{
			listAll:false,
			programId:	getFieldValue("programIdAddPatient"),
			searchTexts: "prg_" + getFieldValue("programIdAddPatient"),
			searchByUserOrgunits: false,
			searchBySelectedOrgunit:true
		},
		function()
		{
			hideById('dataRecordingSelectDiv');
			hideById('dataEntryFormDiv');
			showById('searchDiv');
			setInnerHTML('searchInforTD', i18n_list_all_patients );
			setFieldValue('listAll', true);
			hideLoader();
		});
}

//-----------------------------------------------------------------------------
// Search Patient
//-----------------------------------------------------------------------------

function searchPatientsOnKeyUp( event )
{
	var key = getKeyCode( event );
	
	if ( key==13 )// Enter
	{
		validateAdvancedSearch();
	}
}

function getKeyCode(e)
{
	 if (window.event)
		return window.event.keyCode;
	 return (e)? e.which : null;
}

//--------------------------------------------------------------------------------------------
// Show selected data-recording
//--------------------------------------------------------------------------------------------

function showSelectedDataRecoding( patientId, programId )
{
	showLoader();
	hideById('searchDiv');
	hideById('dataEntryFormDiv');
	jQuery('#dataRecordingSelectDiv').load( 'selectDataRecording.action', 
		{
			patientId: patientId
		},
		function()
		{
			showById('dataRecordingSelectDiv');
			hideLoader();
			hideById('contentDiv');
			hideById('contentDiv');
			hideById('mainLinkLbl');
			setInnerHTML('singleProgramName',jQuery('#programIdAddPatient option:selected').text());
			loadProgramStages( patientId, programId )
		});
}

function advancedSearch( params )
{
	$.ajax({
		url: 'searchPatient.action',
		type:"POST",
		data: params,
		success: function( html ){
				statusSearching = 1;
				setInnerHTML( 'contentDiv', html );
				showById('contentDiv');
				setInnerHTML('searchInforTD', i18n_search_patients );
				setFieldValue('listAll',false);
				jQuery( "#loaderDiv" ).hide();
			}
		});
}

//--------------------------------------------------------------------------------------------
// Load program-stages by the selected program
//--------------------------------------------------------------------------------------------

function loadProgramStages( patientId, programId )
{
	jQuery.getJSON( "loadProgramStageInstances.action",
		{
			patientId:patientId,
			programId: programId
		},  
		function( json ) 
		{   
			if( json.programStageInstances == 0)
			{
				createProgramInstance( patientId, programId );
			}
			else
			{
				jQuery("#selectForm [id=programStageId]").attr('psid', json.programStageInstances[0].programStageId);	
				loadDataEntry( json.programStageInstances[0].id );
			}
		});
}

function createProgramInstance( patientId, programId )
{
	jQuery.postJSON( "saveProgramEnrollment.action",
		{
			patientId: patientId,
			programId: programId,
			dateOfIncident: getCurrentDate(),
			enrollmentDate: getCurrentDate()
		}, 
		function( json ) 
		{
			jQuery("#selectForm [id=programStageId]").attr('psid', json.programStageId);	
			loadDataEntry( json.activeProgramStageInstanceId );
		});
};		
