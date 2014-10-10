function organisationUnitSelected( orgUnits ){
	window.location = "getImportingParams.action";
}
selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// IMPORT DATA FROM EXCEL FILE INTO DATABASE
// -----------------------------------------------------------------------------

function importData()
{
	if ( importItemIds && importItemIds.length > 0 )
	{
		lockScreen();
		var params = 'importData.action?';
		
		for ( var i = 0 ; i < importItemIds.length ; i ++ )
		{
			params += 'importItemIds=' + importItemIds[i];
			params += (i < importItemIds.length-1) ? "&" : "";
		}
		
		jQuery.postJSON( params,
		{
			importReportId: byId('importReportId').value,
			periodId: byId('period').value
		}, function( json ) {
			unLockScreen();
			
			if ( json.response == "success" )
			{
				showSuccessMessage( json.message + " " + importItemIds.length + " " + i18n_data_value, 5000 );
			} else {
				showErrorMessage( json.message, 5000 );
			}
		});
	}
	else showWarningMessage( i18n_choose_import_item );
}

// -----------------------------------------------------------------------------
// PREVIEW DATA FLOW
// @param isImport This is a global variable which declared in preview.js
// -----------------------------------------------------------------------------

function getPreviewImportData()
{
	lockScreen();
	isImport = true;
	jQuery.post( "previewDataFlow.action", { importReportId: byId( "importReportId" ).value }, previewExportReportReceived, 'xml' );
}

isToggled = true;

function selectAllData( _this )
{
	if ( isToggled )
	{
		jQuery( _this ).val( i18n_unselect_all );
		
		for ( var i = 0 ; i < importlist.length ; i ++ )
		{
			importlist[i].className = 'ui-preview-table ui-preview-selected';
			
			idTemp = jQuery(importlist[i]).attr( 'id' ) + "_" + jQuery(importlist[i]).html();
			
			if ( jQuery.inArray(idTemp, importItemIds) != -1 )
			{
				importItemIds = jQuery.grep( importItemIds, function(value) {
					return value != idTemp
				});
			}
			else importItemIds.push( idTemp );
		}
	}
	else
	{
		jQuery( _this ).val( i18n_select_all );
		
		for ( var i = 0 ; i < importlist.length ; i ++ )
		{
			importlist[i].className = 'ui-preview-table ui-preview-unselected';
		}
		
		importItemIds.length = 0;
	}
	
	isToggled = !isToggled;	
}

// --------------------------------------------------------------------
// PERIOD TYPE
// --------------------------------------------------------------------

function getPeriodsByImportReport( importReportId ) {
	
	var url = 'getPeriodsByImportReport.action';
	var periodList = jQuery( '#period' );
	periodList.empty();
	
	jQuery.postJSON( url, { 'importReportId' : importReportId }, function ( json ) {
		for ( var i = 0 ; i < json.periods.length ; i ++ ) {
			periodList.append( '<option value="' + i + '">' + json.periods[i].name + '</option>' );
		}
	} );
}

function getImportingPeriod( url )
{
	var periodList = jQuery( '#period' );
	periodList.empty();
	
	jQuery.get( url, {}, function ( json ) {
		for ( var i = 0 ; i < json.periods.length ; i ++ ) {
			periodList.append( '<option value="' + i + '">' + json.periods[i].name + '</option>' );
		}
	} );
}

function validateUploadExcelImportByJSON() {

	jQuery( "#upload" ).upload( 'validateUploadExcelImport.action',
		{ 'draft': true },
		function ( data )
		{
			if ( data.response == 'error' )	{              
				setMessage( data.message );
			} else {
				uploadExcelImport();
			}
		}
	);
}

function validateUploadExcelImportByXML(){

	jQuery( "#upload" ).upload( 'validateUploadExcelImport.action',
		{ 'draft': true },
		function ( data )
		{
			data = data.getElementsByTagName('message')[0];
			var type = data.getAttribute("type");
			
			if ( type == 'error' ) {              
				setMessage( data.firstChild.nodeValue );
			} else {
				uploadExcelImport();
			}
		}, 'xml'
	);
}
	
function uploadExcelImport()
{	
	jQuery( "#upload" ).upload( 'uploadExcelImport.action',
		{ 'draft': true },
		function( data, e ) {
			try {
				window.location.reload();
			}
			catch(e) {
				alert(e);
			}
		}
	);
}

function rollbackImporting()
{
	jQuery.get( "rollbackImporting.action", {}, function( json ) {
		if ( json.response && json.response == "success" && json.message != "" ) {
			showSuccessMessage( json.message, 3500 );
		} else {
			showWarningMessage( i18n_no_value_rollbacked );
		}
	} );
}