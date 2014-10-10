isAjax = true;
function organisationUnitSelected( orgUnits, orgUnitNames )
{
    setFieldValue( 'orgunitname', orgUnitNames[0] );
}

selection.setListenerFunction( organisationUnitSelected );

function loadGeneratedReport()
{
	showLoader();
	
	jQuery( "#contentDiv" ).load( "generateReport.action",
	{
		programId: getFieldValue( 'programId' ),
		startDate: getFieldValue( 'startDate' ),
		endDate: getFieldValue( 'endDate' ),
		facilityLB: $('input[name=facilityLB]:checked').val()
	}, function() 
	{ 
		jQuery( "[name=newEncounterBtn]" ).addClass("hidden");
		jQuery( "[name=newEncounterBtn]" ).removeClass("show-new-event");
		jQuery( "[status=3]" ).attr("disabled", true);
		jQuery( "[status=4]" ).attr("disabled", true);
		jQuery( "[status=5]" ).attr("disabled", true);
		hideLoader();
		hideById( 'message' );
		showById( 'contentDiv' );
		resize();
	});
}

function loadDataEntry( programStageInstanceId ) 
{
	isReport = false;
	jQuery('#viewRecordsDiv' )
		.load( 'viewRecords.action?programStageInstanceId=' + programStageInstanceId
		,function(){
			showById('entryForm');
			hideById('entryPostComment');
			jQuery("#viewRecordsDiv :input" ).attr("disabled", true);
			jQuery("#viewRecordsDiv :input" ).datepicker("destroy");
			jQuery("#viewRecordsDiv :input" ).removeAttr("onclick");
			jQuery("#viewRecordsDiv :input" ).removeAttr("onchange");
			jQuery("#viewRecordsDiv :input" ).width("260");
			jQuery("#viewRecordsDiv" ).find('img').removeAttr("onclick");
			showById("reportTitle");
			hideById("patientInforTB");
			jQuery(".ui-combobox" ).hide();
			hideById('inputCriteriaDiv');
		})
		.dialog({
			title: i18n_reports,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 840,
			height: 400
		});
}

function entryFormContainerOnReady(){}
