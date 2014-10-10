isAjax = true;

function orgunitSelected( orgUnits, orgUnitNames )
{
	hideById("listPatientDiv");
	setFieldValue('orgunitName', orgUnitNames[0]);
	setFieldValue('orgunitId', orgUnits[0]);
}

selection.setListenerFunction( orgunitSelected );

function displayCadendar()
{
	if( byId('useCalendar').checked )
	{
		hideById('showEventSince');
		hideById('showEventUpTo');
		showById('startDueDate');
		showById('endDueDate');
		datePickerInRange( 'startDueDate' , 'endDueDate', false );
	}
	else
	{
		showById('showEventSince');
		showById('showEventUpTo');
		hideById('startDueDate');
		hideById('endDueDate');
		jQuery('#delete_endDueDate').remove();
		jQuery('#delete_startDueDate').remove();
		jQuery('#startDueDate').datepicker("destroy");
		jQuery('#endDueDate').datepicker("destroy");
	}
}

function showActitityList()
{
	setFieldValue('listAll', "true");
	hideById('listPatientDiv');
	$('#contentDataRecord').html('');
	var facilityLB = $('input[name=facilityLB]:checked').val();
	var programId = getFieldValue('programIdAddPatient');
    var searchTexts = "stat_" + programId
        + "_" + getFieldValue('startDueDate')
        + "_" + getFieldValue('endDueDate');

    if( facilityLB == 'selected' ) {
        searchTexts += "_" + getFieldValue('orgunitId');
    }
    else if( facilityLB == 'all' ) {
        searchTexts += "_0";
    }
    else if( facilityLB == 'childrenOnly' ) {
        searchTexts += "_-1";
    }

    searchTexts += "_false_" + getFieldValue('statusEvent');

	showLoader();

    jQuery('#listPatientDiv').load('getActivityPlanRecords.action', {
        programId: programId,
        listAll: false,
        searchTexts: searchTexts
    }, function() {
        showById('colorHelpLink');
        showById('listPatientDiv');
        setTableStyles();
        hideLoader();
    });
}

function exportActitityList( type )
{
    var facilityLB = $('input[name=facilityLB]:checked').val();
    var params = "programId=" + getFieldValue('programIdAddPatient');

    params += "&type=xls";
    params += "&searchTexts=stat_" + getFieldValue('programIdAddPatient')
        + "_" + getFieldValue('startDueDate')
        + "_" + getFieldValue('endDueDate');

    if( facilityLB == 'selected' ) {
        params += "_" + getFieldValue('orgunitId');
    }
    else if( facilityLB == 'all' ) {
        params += "_0";
    }
    else if( facilityLB == 'childrenOnly' ) {
        params += "_-1";
    }

    params += "_false_" + getFieldValue('statusEvent');
    window.location.href = "getActivityPlanRecords.action?" + params;
}

// --------------------------------------------------------------------
// Patient program tracking
// --------------------------------------------------------------------

function loadDataEntryDialog( programStageInstanceId, programStageUid )
{
    jQuery('[id=programStageInstanceId]').val(programStageInstanceId);
    jQuery('.stage-object-selected').attr('psuid', programStageUid);

    $('#contentDataRecord' ).load("viewProgramStageRecords.action", {
        programStageInstanceId: programStageInstanceId
    }, function() {
        jQuery('#programStageUid').val(programStageUid);
        showById('reportDateDiv');
        showById('patientInforTB');
        showById('entryForm');
        showById('inputCriteriaDiv');
    }).dialog({
        title:i18n_program_stage,
        maximize:true,
        closable:true,
        modal:false,
        overlay:{background:'#000000', opacity:0.1},
        width:850,
        height:500
    });
}


function statusEventOnChange()
{
	if( !byId('useCalendar').checked )
	{
		var statusEvent = getFieldValue("statusEvent");

		if( statusEvent == '1_2_3_4'
			|| statusEvent == '3_4'
			|| statusEvent == '2_3_4' ){
			enable('showEventSince');
			enable('showEventUpTo');
			setDateRange();
		}
		else if( statusEvent == '3' ){
			disable('showEventSince');
			enable('showEventUpTo');
			setDateRange();
		}
		else{
			enable('showEventSince');
			disable('showEventUpTo');
			setDateRange();
		}
	}
}

function setDateRange()
{
	var statusEvent = getFieldValue("statusEvent");
	var date = new Date();
	var d = date.getDate();
	var m = date.getMonth();
	var y= date.getFullYear();

	var startDateSince = "";
	var endDateSince = "";
	var startDateUpTo = "";
	var endDateUpTo = "";
	var startDate = "";
	var endDate = "";

	// Get dateRangeSince
	var days = getFieldValue('showEventSince');

    if( days == 'ALL' ) {
        startDateSince = jQuery.datepicker.formatDate(dateFormat, new Date(y - 100, m, d));
    }
    else {
        startDateSince = jQuery.datepicker.formatDate(dateFormat, new Date(y, m, d + eval(days)));
    }

    endDateSince = jQuery.datepicker.formatDate( dateFormat, new Date() );

	// getDateRangeUpTo
	days = getFieldValue('showEventUpTo');
	startDateUpTo = jQuery.datepicker.formatDate( dateFormat, new Date() );
	endDateUpTo = "";
	if( days == 'ALL'){
		endDateUpTo = jQuery.datepicker.formatDate( dateFormat, new Date(y+100, m, d) ) ;
	}
	else{
		endDateUpTo = jQuery.datepicker.formatDate( dateFormat, new Date(y, m, d + eval(days)) ) ;
	}

	// check status to get date-range
    if( statusEvent == '1_2_3_4'
        || statusEvent == '3_4'
        || statusEvent == '2_3_4' ) {
        startDate = startDateSince;
        endDate = endDateUpTo;

    } else if( statusEvent == '3' ) {
        startDate = startDateUpTo;
        endDate = endDateUpTo;
    }
    else {
        startDate = startDateSince;
        endDate = endDateSince;
    }

    jQuery("#startDueDate").val(startDate);
	jQuery("#endDueDate").val(endDate);
}

function setDateRangeUpTo( days )
{
    if( days == "" ) {
        return;
    }

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    var startDate = jQuery.datepicker.formatDate(dateFormat, new Date());
    var endDate = "";

    if( days == 'ALL' ) {
        endDate = jQuery.datepicker.formatDate(dateFormat, new Date(y + 100, m, d));
    }
    else {
        d = d + eval(days);
        endDate = jQuery.datepicker.formatDate(dateFormat, new Date(y, m, d));
    }

    jQuery("#startDueDate").val(startDate);
    jQuery("#endDueDate").val(endDate);
}

function setDateRangeAll()
{
	var date = new Date();
	var d = date.getDate();
	var m = date.getMonth();
	var y= date.getFullYear();
}
