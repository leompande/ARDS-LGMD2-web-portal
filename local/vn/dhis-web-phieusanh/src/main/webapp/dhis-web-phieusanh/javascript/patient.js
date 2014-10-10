function addPatient()
{
	var registeredUnit = getFieldValue( 'wardLevel' );
	
	if ( registeredUnit && registeredUnit != -1 )
	{
		var args = getParamsForDiv('editPatientDiv');
		args += "registeredUnit=" + registeredUnit;

		$.ajax ({
			type: "POST",
			url: 'addPatient.action',
			data: args,
			success: function(json)
			{	
				var patientId = json.message.split('_')[0];
				var systemIdentifierId = json.message.split('_')[1];

				jQuery('#advSearchBox0 [id="searchText"]').val( systemIdentifierId );
				statusSearching = 1;

				showProgramEnrollmentSelectForm( patientId );
				jQuery('#resultSearchDiv').dialog('close');
			}
		});
	} else {
		showWarningMessage( i18n_please_select_address );
	}

    return false;
}

function updatePatient()
{
	var registeredUnit = getFieldValue( 'wardLevel' );
	
	if ( registeredUnit && registeredUnit != -1 )
	{
		var args = getParamsForDiv('editPatientDiv');
		args += "registeredUnit=" + registeredUnit;

		$.ajax( {
			type: "POST",
			url: 'updatePatient.action',
			data: args,
			success: function( json ) {
				showProgramEnrollmentSelectForm( getFieldValue('id') );
			}
		});
	} else {
		showWarningMessage( i18n_please_select_address );
	}
}