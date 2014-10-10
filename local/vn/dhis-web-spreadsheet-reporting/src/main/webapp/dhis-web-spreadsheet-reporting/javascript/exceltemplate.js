// ========================================================================================================================
// EXCEL TEMPLATE MANAGER
// ========================================================================================================================

// ---------------------------------------------------------------------------
// Regular Expression using for checking excel's file name
// ---------------------------------------------------------------------------
regPattern = /^[a-zA-Z][\w\s\d.]{5,50}\.xl(?:sx|s)$/;

// ---------------------------------------------------------------------------
// Dialog
// ---------------------------------------------------------------------------

function setUpDialog( elementId, title, width, height )
{
	var dialog = jQuery( '#'+elementId ).dialog({
		title: title,
		modal: true,
		autoOpen: false,
		minWidth: width,
		minHeight: height,
		width: width,
		height: height
	});
	
	return dialog;
}

function openDialog( _dialog )
{
	_dialog.dialog( 'open' );
}

function closeDialog( _dialog )
{
	_dialog.dialog( 'close' );
}

/*
*	Delete Excel Template
*/
function deleteExcelTemplate( fileName ) {

	if ( window.confirm( i18n_confirm_delete ) ) {
	
		jQuery.postUTF8( "deleteExcelTemplate.action", { fileName: fileName }, function( json )
		{			
			if ( json.response == 'error' ) {
				setMessage( json.message );
			}else {
				window.location.href = 'listAllExcelTemplates.action';
			}
		} );
	}
}

curTemplateName = '';
newTemplateName = '';

function openEditExcelTemplate( currentFileName )
{
	curTemplateName = currentFileName;
	setFieldValue( "newName", currentFileName );
	openDialog( dialog );
}

function applyingPatternForFileName( fileName )
{	
	return fileName.match( regPattern );
}

function validateRenamingExcelTemplate( fileName, columnIndex )
{	
	var list = byId( 'list' );
	var rows = list.getElementsByTagName( 'tr' );
	
	for ( var i = 0 ; i < rows.length ; i++ )
	{
		var cell = rows[i].getElementsByTagName( 'td' )[columnIndex -1];
		var value = cell.firstChild.nodeValue;
		
		if ( value.toLowerCase() == fileName.toLowerCase() )
		{
			disable( "excelTemplateButtonRename" );
			setMessage( i18n_file_exists );
			break;
		}
		else if ( applyingPatternForFileName( fileName ) == null )
		{
			disable( "excelTemplateButtonRename" );
			setMessage
			(
				'<b>' + i18n_filename_wellformed  + '</b><ul><li>'
				+ i18n_length_filename_min5_max30 + '</li><li>'
				+ i18n_use_only_letters_numbers_dot_only + '</li></ul>'
			);
			break;
		}
		else
		{
			enable( "excelTemplateButtonRename" );
			hideMessage();
		}
	}
}

/**
	param renamingMode::
	'RUS': Rename file name and update the system
	'RNUS': Rename file name but non-updating the system
*/
function checkingStatusExcelTemplate( newFileName, keyColumnIndex, statusColumnIndex )
{
    var list = byId( 'list' );
    var rows = list.getElementsByTagName( 'tr' );
    var flagRename = false;
	newTemplateName = newFileName;
	
	for ( var i = 0; i < rows.length; i++ )
    {
        var cell = rows[i].getElementsByTagName( 'td' )[keyColumnIndex-1];
        var value = cell.firstChild.nodeValue;
		cell = rows[i].getElementsByTagName( 'td' )[statusColumnIndex-1];
        var statusFile = cell.axis;
		
        if ( (value.toLowerCase() == curTemplateName.toLowerCase()) && (statusFile == "true") )
        {
            // File exists and being used
			if ( window.confirm(confirmRenamingMessage) )
			{
				renamingExcelTemplate( curTemplateName, newFileName, "RUS" );
			}
			else closeDialog( dialog );
			
			return;
        }
		else flagRename = true;
	}
	
	// File exists and pending
	if ( flagRename )
	{
		renamingExcelTemplate( curTemplateName, newFileName, "RNUS" );
	}
}

function renamingExcelTemplate( curFileName, newFileName, renamingMode )
{
	jQuery.postUTF8( "renameExcelTemplate.action", {
		newFileName: newFileName,
		curFileName: curFileName,
		renamingMode: renamingMode
	}, function( json ){
		if ( json.response == "success" )
		{
			closeDialog( dialog );
		
			if ( window.confirm( confirmUpdateSysMessage ) )
			{
				updateExportReportByTemplate();
			}
			else
			{
				window.location.href="listAllExcelTemplates.action?mode=" + mode + "&message=" + json.message;
			}
		}
		else if ( json.response == "input" )
		{
			window.location.href="listAllExcelTemplates.action?mode=" + mode + "&message=" + json.message;
		}
		else setMessage( json.message );
	} );
}

function updateExportReportByTemplate() {

	jQuery.postUTF8( "updateExportReportByTemplate.action", {
		curTemplateName: curTemplateName,
		newTemplateName: newTemplateName
	}, function( json ) {
		if ( json.response )
		{
			window.location.href="listAllExcelTemplates.action?mode=" + mode + "&message=" + json.message;
		}
	} );
}

//----------------------------------------------------------
// Validate Upload Excel Template
//----------------------------------------------------------

function validateUploadExcelManagement( fileName, columnIndex )
{
    var rows = byId( 'list' ).getElementsByTagName( 'tr' );
    
    for ( var i = 0; i < rows.length; i++ )
    {
        var cell = rows[i].getElementsByTagName( 'td' )[columnIndex-1];
        var value = cell.firstChild.nodeValue;
		
        if ( value.toLowerCase().indexOf( fileName.toLowerCase() ) != -1 )
        {
            // file is existsing
			return window.confirm( i18n_confirm_override );
        }
    }
	
	// normally upload
	return true;
}

function validateUploadExcelTemplate()
{        
	jQuery( "#upload" ).upload( 'validateUploadExcelTemplate.action',
		function(data)
		{
			data = data.getElementsByTagName('message')[0];
			var type = data.getAttribute("type");

			if ( type == 'error' )
			{                    
				setMessage( data.firstChild.nodeValue );
			}
			else if ( type == 'input' )
			{
				if ( !window.confirm( i18n_confirm_override ) ) return;
				else uploadExcelTemplate();
			}
			else uploadExcelTemplate();
		}, 'xml'
	);
}
	
function uploadExcelTemplate()
{
	jQuery( "#upload" ).upload( 'uploadExcelTemplate.action',
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