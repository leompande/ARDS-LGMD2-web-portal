isAjax = true;
function organisationUnitSelected( orgUnits, orgUnitNames )
{
    setFieldValue( 'orgunitname', orgUnitNames[0] );
}

selection.setListenerFunction( organisationUnitSelected );

function generatedStatisticalProgramReport()
{
	if(  getFieldValue('type') =='' ){
		hideById('statisticalReportDiv');
		hideById('detailsDiv');
		showLoader();
		jQuery( "#statisticalReportDiv" ).load( "generateStatisticalProgramReport.action",
		{
			programId: getFieldValue('programId'),
			startDate: getFieldValue('startDateField') + ' 00:00',
			endDate: getFieldValue( 'endDateField' ) + ' 23:59',
			facilityLB: $('input[name=facilityLB]:checked').val(),
		}, function() 
		{ 
			hideById('inputCriteria');
			showById('showDataBtn');
			showById('statisticalReportDiv');
			showById('reportTbl');
			hideLoader();
		});
	}
	else
	{
		setFieldValue('startDate', getFieldValue('startDateField') + ' 00:00');
		setFieldValue('endDate', getFieldValue( 'endDateField' ) + ' 23:59');
		byId('reportForm').submit();
	}
	
}

function detailsReport()
{
	hideById('viewRecordsDiv');
	showById('detailsDiv');
	showById('totalLbl');
	showById('programStageTitleLbl');
	hideById('patientNameLbl');
}
