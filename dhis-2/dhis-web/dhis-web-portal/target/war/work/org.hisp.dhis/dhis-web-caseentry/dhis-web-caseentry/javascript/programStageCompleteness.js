isAjax = true;

$(function() {
  selectionTreeSelection.setListenerFunction( orgunitSelected );
});

function orgunitSelected( orgUnits, orgUnitNames )
{
	showById('selectDiv');
	hideById('showDataBtn');
	hideById("listPatientDiv");
	setFieldValue('orgunitId', orgUnits[0]);
}

function generateStageCompleteness()
{
	hideById('selectDiv');
	showById('showDataBtn');
	showLoader();
	jQuery('#completenessDiv').load('generateProgramStageCompleteness.action',
		{
			programId: getFieldValue('programId'),
			startDate: getFieldValue('startDate'),
			endDate: getFieldValue('endDate'),
			facilityLB: $('input[name=facilityLB]:checked').val()
		}, 
		function()
		{
			showById('completenessDiv');
			setTableStyles();
			hideLoader();
		});
}

function exportStageCompleteness( type )
{
	var params = "type=" + type;
	params += "&programId=" + getFieldValue('programId');
	params += "&startDate=" + getFieldValue('startDate');
	params += "&endDate=" + getFieldValue('endDate');
	params += "&facilityLB=" + $('input[name=facilityLB]:checked').val();
	
	var url = "generateProgramStageCompleteness.action?" + params;
	window.location.href = url;
}
