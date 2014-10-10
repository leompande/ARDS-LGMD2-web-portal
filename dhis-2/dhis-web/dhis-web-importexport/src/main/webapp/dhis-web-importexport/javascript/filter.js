
// Global Variables
var filters = [];

// -----------------------------------------------------------------------------
// Document ready
// -----------------------------------------------------------------------------

jQuery( function() {
    tableSorter( "filterList" );
    loadFilters();
    
    dhis2.contextmenu.makeContextMenu({
        menuId: 'contextMenu',
        menuItemActiveClass: 'contextMenuItemActive',
        listId: 'filterTableBody'
      });
} );

// Create a new Filter form
function submitFilterForm( command )
{
    $( "input[name='command']" ).val( command );
    $( "#formFilter" ).submit();
}

// -----------------------------------------------------------------------------
// Load Filters
// -----------------------------------------------------------------------------

// Load Filters
function loadFilters()
{
    $.ajax( {
        type: "GET",
        url: "../api/filteredMetaData/getMetaDataFilters",
        dataType: "json",
        success: function( response ) {
            filters = response;
        },
        error: function( request, status, error ) {
            alert("Getting filters process failed.");
        }
    });
}

// Show Filter details
function showFilterDetails( context )
{
	var filterUid = context.uid;
	
    $( "#detailsArea" ).show( "fast" );
    for ( var i = 0; i < filters.length; i++ )
    {
        if ( filters[i].id == filterUid )
        {
            setInnerHTML( 'nameField', filters[i].name );
            setInnerHTML( 'descriptionField', filters[i].description );
        }
    }
}

// -----------------------------------------------------------------------------
// Export Filtered MetaData
// -----------------------------------------------------------------------------

// Start export
function exportFilterButton( context )
{
	var filterUid = context.uid;
	
    for ( var i = 0; i < filters.length; i++ )
    {
        if ( filters[i].id == filterUid )
        {
            $( "#exportJson" ).attr( "value", filters[i].jsonFilter );
            jQuery( "#exportDialog" ).dialog( {
                title: i18n_export,
                modal: true
            } );
        }
    }
}

// Export MetaData
function exportFilteredMetaData()
{
    var exportJson = {};
    exportJson.exportDependencies = $( "#exportDependencies" ).is( ":checked" ).toString();
    exportJson.jsonFilter = $( "#exportJson" ).val();

    $( "#exportJsonValue" ).val( JSON.stringify( exportJson ) );

    document.getElementById( 'exportForm' ).action = getURL();
    $( "#exportForm" ).submit();
    $( "#exportDialog" ).dialog( "close" );
}

// Generate Export URL
function getURL()
{
    var url = "../api/filteredMetaData";
    var format = $( "#format" ).val();
    var compression = $( "#compression" ).val();
    url += "." + format;

    if(compression == "zip")
    {
        url += ".zip";
    }
    else if(compression == "gz")
    {
        url += ".gz";
    }

    return url;
}

// -----------------------------------------------------------------------------
// Edit a Filter
// -----------------------------------------------------------------------------

// Edit a Filter
function editFilterButton( context )
{
	var filterUid = context.uid;
	
    for ( var i = 0; i < filters.length; i++ )
    {
        if ( filters[i].id == filterUid )
        {
            $( "input[name='name']" ).val( filters[i].name );
            $( "input[name='description']" ).val( filters[i].description );
            $( "input[name='uid']" ).val( filters[i].id );
            $( "input[name='jsonFilter']" ).val( filters[i].jsonFilter );
            $( "input[name='command']" ).val( "update" );
        }
    }

    $( "#formFilter" ).submit();
}

// -----------------------------------------------------------------------------
// Delete a Filter
// -----------------------------------------------------------------------------

// Delete a Filter
function removeFilterButton( context )
{
	var filterUid = context.uid;
	
    var filter = {};
    for ( var i = 0; i < filters.length; i++ )
    {
        if ( filters[i].id == filterUid )
        {
            filter = filters[i];
        }
    }

    var json = JSON.stringify( replaceIdWithUid( filter ) );

    $.ajax( {
        type: "POST",
        url: "../api/filteredMetaData/deleteFilter",
        contentType: "application/json",
        data: json,
        success: function ()
        {
            $( "#tr" + filter.uid ).remove();
        },
        error: function ( request, status, error )
        {
            alert( "Remove filter process failed." );
        }
    } );
}

// -----------------------------------------------------------------------------
// Utils
// -----------------------------------------------------------------------------

// Replace id with uid
function replaceIdWithUid( object )
{
    object.uid = object.id;
    delete object.id;
    return object;
}
