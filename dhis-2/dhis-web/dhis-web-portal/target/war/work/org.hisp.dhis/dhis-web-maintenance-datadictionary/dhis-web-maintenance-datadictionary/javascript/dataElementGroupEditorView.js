jQuery( document ).ready( function()
{
    loadAvailableGroups();
    getDataElementsByGroup();
    getAssignedDataElementGroups();

    jQuery( "#addDataElementGroupForm" ).dialog( {
        autoOpen : false,
        modal : true
    } );
    jQuery( "#tabs" ).tabs();
} );

function loadAvailableDataElements()
{
    var filter_1 = jQuery( '#view_1 #availableDataElementsFilter' ).val();
    var filter_2 = jQuery( '#view_2 #availableDataElementsFilter' ).val();
    var list_1 = jQuery( "#view_1 #availableDataElements" );
    var list_2 = jQuery( "#view_2 #availableDataElements2" );
    list_1.empty();
    list_2.empty();

    for ( var id in availableDataElements )
    {
        var text = availableDataElements[id];

        if ( text.toLowerCase().indexOf( filter_1.toLowerCase() ) != -1 )
        {
            list_1.append( '<option value="' + id + '">' + text + '</option>' );
            list_2.append( '<option value="' + id + '">' + text + '</option>' );
        }
    }

	sortList( 'availableDataElements', 'ASC' );
	sortList( 'availableDataElements2', 'ASC' );
    list_1.find( ":first" ).attr( "selected", "selected" );
    list_2.find( ":first" ).attr( "selected", "selected" );
}

function loadAvailableGroups()
{
    var filter_1 = jQuery( '#view_1 #dataElementGroupsFilter' ).val();
    var filter_2 = jQuery( '#view_2 #dataElementGroupsFilter' ).val();
    var list_1 = jQuery( "#view_1 #dataElementGroups" );
    var list_2 = jQuery( "#view_2 #availableGroups" );
    list_1.empty();
    list_2.empty();

    for ( var id in dataElementGroups )
    {
        var text = dataElementGroups[id];

        if ( text.toLowerCase().indexOf( filter_1.toLowerCase() ) != -1 )
        {
            list_1.append( '<option value="' + id + '">' + text + '</option>' );
            list_2.append( '<option value="' + id + '">' + text + '</option>' );
        }
    }

    sortList( 'dataElementGroups', 'ASC' );
    sortList( 'availableGroups', 'ASC' );
    list_1.find( ":first" ).attr( "selected", "selected" );
    list_2.find( ":first" ).attr( "selected", "selected" );

    var list_3 = jQuery( "#view_2 #assignedGroups" ).children();
    list_2.children().each( function( i, item )
    {
        list_3.each( function( k, it )
        {
            if ( it.value == item.value )
            {
                jQuery( item ).remove();
            }
        } );
    } );
}

function getDataElementsByGroup()
{
    loadAvailableDataElements();

	var id = jQuery( '#view_1 #dataElementGroups' ).val();
    var filter_1 = jQuery( '#view_1 #selectedDataElementsFilter' ).val();
    var list_1 = jQuery( "#view_1 #selectedDataElements" );
    list_1.empty();

    jQuery.postJSON( "../dhis-web-commons-ajax-json/getDataElements.action", {
        id : ( isNotNull( id ) ? id : -1 )
    }, function( json )
    {
        jQuery.each( json.dataElements, function( i, item )
        {
            var text = item.name;
            if ( text.toLowerCase().indexOf( filter_1.toLowerCase() ) != -1 )
            {
                list_1.append( '<option value="' + item.id + '">' + text + '</option>' );
            }
            jQuery( "#view_1 #availableDataElements" ).children().each( function( k, it )
            {
                if ( item.id == it.value )
                {
                    jQuery( it ).remove();
                }
            } );
        } );
    } );
}

function showAddGroup()
{
    jQuery( '#addDataElementGroupForm' ).dialog( 'option', 'title', i18n_new );
    jQuery( '#addDataElementGroupForm' ).dialog( 'option', 'buttons', [ {
        text : i18n_save,
        click : function()
        {
            jQuery.postJSON( "validateDataElementGroup.action", {
                name : function()
                {
                    return jQuery( '#addDataElementGroupForm #name' ).val();
                }
            }, function( json )
            {
                if ( json.response == 'success' )
                {
                    jQuery.postJSON( "addDataElementGroupEditor.action", {
                        name : function()
                        {
                            return jQuery( '#addDataElementGroupForm #name' ).val();
                        }
                    }, function( json )
                    {
                        dataElementGroups[json.dataElementGroup.id] = json.dataElementGroup.name;
                        loadAvailableGroups();
                        loadAvailableDataElements();
                        jQuery( "#view_1 #selectedDataElements" ).empty();
                        jQuery( '#addDataElementGroupForm' ).dialog( 'close' );
                    } );
                } else
                {
                    markInvalid( "addDataElementGroupForm #name", json.message );
                }
            } );
        }
    } ] );

    jQuery( '#addDataElementGroupForm' ).dialog( 'open' );
}

function showAddGroupView2()
{
    jQuery( '#addDataElementGroupForm' ).dialog( 'option', 'title', i18n_new );
    jQuery( '#addDataElementGroupForm' ).dialog( 'option', 'buttons', [ {
        text : i18n_save,
        click : function()
        {
            jQuery.postJSON( "validateDataElementGroup.action", {
                name : function()
                {
                    return jQuery( '#addDataElementGroupForm #name' ).val();
                }
            }, function( json )
            {
                if ( json.response == 'success' )
                {
                    jQuery.postJSON( "addDataElementGroupEditor.action", {
                        name : function()
                        {
                            return jQuery( '#addDataElementGroupForm #name' ).val();
                        }
                    }, function( json )
                    {
                        dataElementGroups[json.dataElementGroup.id] = json.dataElementGroup.name;
                        loadAvailableGroups();
                        jQuery( '#addDataElementGroupForm' ).dialog( 'close' );
                    } );
                } else
                {
                    markInvalid( "addDataElementGroupForm #name", json.message );
                }
            } );
        }
    } ] );
    jQuery( '#addDataElementGroupForm' ).dialog( 'open' );
}

function showUpdateGroup()
{
    var id = jQuery( "#view_1 #dataElementGroups" ).val();
    var text = jQuery( "#view_1 #dataElementGroups option[value=" + id + "]" ).text();
    jQuery( '#addDataElementGroupForm #name' ).val( text );

    jQuery( '#addDataElementGroupForm' ).dialog( 'option', 'buttons', [ {
        text : i18n_save,
        click : function()
        {
            jQuery.postJSON( "validateDataElementGroup.action", {
                id : id,
                name : function()
                {
                    return jQuery( '#addDataElementGroupForm #name' ).val();
                }
            }, function( json )
            {
                if ( json.response == 'success' )
                {
                    jQuery.postJSON( "renameDataElementGroupEditor.action", {
                        name : function()
                        {
                            return jQuery( '#addDataElementGroupForm #name' ).val();
                        },
                        id : id
                    }, function( json )
                    {
                        dataElementGroups[json.dataElementGroup.id] = json.dataElementGroup.name;
                        loadAvailableGroups();
                        jQuery( '#addDataElementGroupForm' ).dialog( 'close' );
                        setHeaderDelayMessage( i18n_update_success );
                    } );
                } else
                {
                    markInvalid( "addDataElementGroupForm #name", json.message );
                }
            } );
        }
    } ] );

    jQuery( '#addDataElementGroupForm' ).dialog( 'option', 'title', i18n_rename );
    jQuery( '#addDataElementGroupForm' ).dialog( 'open' );
}

function showUpdateGroup2()
{
    var id = jQuery( "#view_2 #availableGroups" ).val();
    var text = jQuery( "#view_2 #availableGroups option[value=" + id + "]" ).text();
    jQuery( '#addDataElementGroupForm #name' ).val( text );

    jQuery( '#addDataElementGroupForm' ).dialog( 'option', 'buttons', [ {
        text : i18n_save,
        click : function()
        {

            jQuery.postJSON( "validateDataElementGroup.action", {
                id : id,
                name : function()
                {
                    return jQuery( '#addDataElementGroupForm #name' ).val();
                }
            }, function( json )
            {
                if ( json.response == 'success' )
                {
                    jQuery.postJSON( "renameDataElementGroupEditor.action", {
                        name : function()
                        {
                            return jQuery( '#addDataElementGroupForm #name' ).val();
                        },
                        id : id
                    }, function( json )
                    {
                        dataElementGroups[json.dataElementGroup.id] = json.dataElementGroup.name;
                        loadAvailableGroups();
                        jQuery( '#addDataElementGroupForm' ).dialog( 'close' );
                        setHeaderDelayMessage( i18n_update_success );
                    } );
                } else
                {
                    markInvalid( "addDataElementGroupForm #name", json.message );
                }
            } );
        }
    } ] );

    jQuery( '#addDataElementGroupForm' ).dialog( 'option', 'title', i18n_rename );
    jQuery( '#addDataElementGroupForm' ).dialog( 'open' );
}

function deleteDataElemenGroup()
{
    if ( window.confirm( i18n_confirm_delete + '\n\n' + name ) )
    {
        var id = jQuery( "#view_1 #dataElementGroups" ).val();

        jQuery.postJSON( "deleteDataElemenGroupEditor.action", {
            id : id
        }, function( json )
        {
            if ( json.response == 'success' )
            {
                dataElementGroups.splice( id, 1 );
                loadAvailableGroups();
                setHeaderDelayMessage( json.message );
            } else
            {
            	setHeaderDelayMessage( json.message );
            }
        } );
    }
}

function deleteDataElemenGroupView2()
{
    if ( window.confirm( i18n_confirm_delete + '\n\n' + name ) )
    {
        var id = jQuery( "#view_2 #availableGroups" ).val()[0];

        jQuery.postJSON( "deleteDataElemenGroupEditor.action", {
            id : id
        }, function( json )
        {
            if ( json.response == 'success' )
            {
                dataElementGroups.splice( id, 1 );
                loadAvailableGroups();
                setHeaderDelayMessage( json.message );
            } else
            {
            	setHeaderDelayMessage( json.message );
            }
        } );
    }
}

function updateGroupMembers()
{
    var id = jQuery( "#view_1 #dataElementGroups" ).val();

    jQuery.getJSON( "updateDataElementGroupEditor.action?id=" + id + "&"
            + toQueryString( '#view_1 #selectedDataElements', 'groupMembers' ), function( json )
    {
    	setHeaderDelayMessage( i18n_update_success );
    } );
}

function toQueryString( jQueryString, paramName )
{
    var p = "";
    jQuery( jQueryString ).children().each( function( i, item )
    {
        item.selected = "selected";
        p += paramName + "=" + item.value + "&";
    } );
    return p;
}

// View 2

function getAssignedDataElementGroups()
{
    loadAvailableGroups();

    var id = jQuery( "#view_2 #availableDataElements2" ).val();
    var list_2 = jQuery( "#view_2 #assignedGroups" );
    list_2.empty();

    jQuery.postJSON( "getAssignedDataElementGroups.action", {
        dataElementId : ( isNotNull( id ) ? id : -1 )
    }, function( json )
    {
        jQuery.each( json.dataElementGroups, function( i, item )
        {
            list_2.append( '<option value="' + item.id + '">' + item.name + '</option>' );

            jQuery( "#view_2 #availableGroups" ).children().each( function( k, it )
            {
                if ( item.id == it.value )
                {
                    jQuery( it ).remove();
                }
            } );

        } );

    } );
}

function assignGroupsForDataElement()
{
    var dataElementId = jQuery( "#view_2 #availableDataElements2" ).val();

    jQuery.getJSON( "asignGroupsForDataElement.action?dataElementId=" + dataElementId + "&"
            + toQueryString( '#view_2 #assignedGroups', 'dataElementGroups' ), function( json )
    {
    	setHeaderDelayMessage( i18n_update_success );
    } );
}
