// -----------------------------------------------------------------------------
// Organisation unit selection listener
// -----------------------------------------------------------------------------

$( document ).ready( function()
{
    selection.setAutoSelectRoot( false );
    selection.setRootUnselectAllowed( true );
    selection.setListenerFunction( organisationUnitSelected, true );
} );

function organisationUnitSelected( orgUnitIds )
{
    window.location.href = 'department.action';
}

function showRename( event )
{
	var jqsource = jQuery( "#" + this.id );
	var name = jqsource.html();
	_input = "<input type='text' style='width:" + name.length + "em'";
	_input += " onkeypress='renameByEnter( event, " + '"' + this.id + '"' + ', "' + name + '"' + ", this.value )'";
	_input += " onblur='renameByBlur( " + '"' + this.id + '"' + ', "' + name + '"' + ", this.value )'/>";

	jqsource.html( _input );
	jqsource.unbind( "click" );
	jqsource.find( "input" ).focus();
	jqsource.find( "input" ).val( name );
}

function renameByEnter( event, id, _old, _new )
{
	var key = event.keyCode || event.charCode || event.which;

	if ( key == 13 || key == 1 ) // Enter
	{
		var jqsource = jQuery( "#" + id );
		jqsource.bind( "click", showRename );

		if ( _old != _new )
		{
			jQuery.postUTF8( "renameDepartment.action",
			{
				id: id.substring(2, id.length),
				name: _new
			}, function( json )
			{
				if ( json.response == "success" )
				{
					jqsource.html( _new );
				} else {
					jqsource.html( _old );
					showWarningMessage( json.message );
				}
			} );
		}
		else { jqsource.html( _old ); }
	}
}

function renameByBlur( id, _old, _new )
{
	var jqsource = jQuery( "#" + id );
	jqsource.bind( "click", showRename );
	jqsource.html( _old );	
}

// -----------------------------------------------------------------------------
// Export to PDF
// -----------------------------------------------------------------------------

function exportPDF( type )
{
	var params = "type=" + type;
	
	exportPdfByType( type, params );
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOrganisationUnitDetails( unitId )
{
    jQuery.post( 'getDepartment.action',
		{ id: unitId }, function ( json ) {
		setInnerHTML( 'nameField', json.organisationUnit.name );
		setInnerHTML( 'shortNameField', json.organisationUnit.shortName );
		setInnerHTML( 'descriptionField', json.organisationUnit.description );
		setInnerHTML( 'openingDateField', json.organisationUnit.openingDate );

		var orgUnitCode = json.organisationUnit.code;
		setInnerHTML( 'codeField', orgUnitCode ? orgUnitCode : '[' + none + ']' );

		var closedDate = json.organisationUnit.closedDate;
		setInnerHTML( 'closedDateField', closedDate ? closedDate : '[' + none + ']' );

		var commentValue = json.organisationUnit.comment;
		setInnerHTML( 'commentField', commentValue ? commentValue.replace( /\n/g, '<br>' ) : '[' + none + ']' );

		var active = json.organisationUnit.active;
		setInnerHTML( 'activeField', active == 'true' ? yes : no );

		var url = json.organisationUnit.url;
		setInnerHTML( 'urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + none + ']' );

		var lastUpdated = json.organisationUnit.lastUpdated;
		setInnerHTML( 'lastUpdatedField', lastUpdated ? lastUpdated : '[' + none + ']' );

		showDetails();
	});
}

function showUpdateOrganisationUnit( unitId )
{
	jQuery.get( 'getDepartment.action', { id : unitId }, function( json )
	{
		setFieldValue( 'name', json.organisationUnit.name );
		setFieldValue( 'shortName', json.organisationUnit.shortName );
		
		var groups = json.organisationUnit.groups;

		if ( groups.length == 0 )
		{
			jQuery( "select[name=selectedGroups] option:first-child" ).attr( "selected", "selected" );
		}
		else {
			for ( var i in groups )
			{
				jQuery( "td#td" + groups[i].groupSetId + " select:first" ).val( groups[i].id );
			}
		}
	} );
}

// -----------------------------------------------------------------------------
// Remove organisation unit
// -----------------------------------------------------------------------------

function removeOrganisationUnit( unitId, unitName )
{
    removeItem( unitId, unitName, confirm_to_delete_org_unit, 'removeDepartment.action', subtree.refreshTree );
}

function nameChanged()
{
	/* fail quietly if previousName is not available */
	if( previousName === undefined ) {
		return;
	}
	
    var nameField = document.getElementById( 'name' );
    var shortNameField = document.getElementById( 'shortName' );
    var maxLength = parseInt( shortNameField.maxLength );
    
    if ( previousName != nameField.value
        && nameField.value.length <= maxLength
        && ( shortNameField.value == previousName
          || shortNameField.value.length == 0 ))
    {
            shortNameField.value = nameField.value;
    }
    
    previousName = nameField.value;
}