var DAO = DAO || {};

DAO.store = new dhis2.storage.Store( {
    name: 'dhis2',
    adapters: [ dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter ],
    objectStores: [ 'programs', 'programStages', 'optionSets', 'usernames', {
        name: 'dataValues',
        adapters: [ dhis2.storage.IndexedDBAdapter, dhis2.storage.DomLocalStorageAdapter, dhis2.storage.InMemoryAdapter ]
    } ]
} );

function loadPrograms() {
    var def = $.Deferred();

    $.ajax( {
        url: 'getProgramMetaData.action',
        dataType: 'json',
        cache: false
    } ).done(function ( data ) {
        var programs = _.values( data.metaData.programs );
        DAO.store.setAll( 'programs', programs ).then( function () {
            def.resolve( data.metaData );
        } );
    } ).fail( function () {
        def.resolve();
    } );

    return def.promise();
}

function loadProgramStages( metaData ) {
    if ( !metaData ) {
        return;
    }

    var def = $.Deferred();
    var promise = def.promise();

    _.each( _.values( metaData.programs ), function ( el ) {
        var id = el.programStages[0].id;
        promise = promise.then( makeProgramStageRequest( id ));
    } );

    promise = promise.then(function() {
        return $.Deferred().resolve(metaData);
    });

    def.resolve( metaData );

    return promise;
}

function makeProgramStageRequest( id ) {
    return function() {
        var data = createProgramStage( id );

        return $.ajax( {
            url: 'dataentryform.action',
            data: data,
            dataType: 'html',
            cache: false
        } ).done( function ( data ) {
            var obj = {};
            obj.id = id;
            obj.form = data;
            DAO.store.set( 'programStages', obj );
        } );
    }
}

function loadOptionSets( metaData ) {
    if ( !metaData ) {
        return;
    }

    var mainDef = $.Deferred();
    var mainPromise = mainDef.promise();

    var def = $.Deferred();
    var promise = def.promise();

    var builder = $.Deferred();
    var build = builder.promise();

    _.each( metaData.optionSets, function ( item ) {
        build = build.then(function() {
            var d = $.Deferred();
            var p = d.promise();
            DAO.store.get('optionSets', item.uid).done(function(obj) {
                if(!obj || obj.optionSet.version !== item.v) {
                    promise = promise.then( makeOptionSetRequest(item.uid) );
                }

                d.resolve();
            });

            return p;
        });
    } );

    if ( metaData.usernames ) {
        promise = promise.then( makeUsernameRequest() );
    }

    build.done(function() {
        def.resolve();

        promise = promise.done( function () {
            mainDef.resolve( metaData );
        } );
    });

    builder.resolve();

    return mainPromise;
}

function makeUsernameRequest() {
    return function() {
        return $.ajax( {
            url: 'getUsernames.action',
            dataType: 'json',
            cache: false
        }).done(function( data ) {
            var obj = {};
            obj.id = 'usernames';
            obj.usernames = data.usernames;
            DAO.store.set( 'usernames', obj );
        });
    }
}

function makeOptionSetRequest( id ) {
    return function() {
        return $.ajax({
            url: 'getOptionSet.action',
            data: { id: id },
            dataType: 'json',
            cache: false
        }).done(function(data) {
            var obj = {};
            obj.id = id;
            obj.optionSet = data.optionSet;
            DAO.store.set('optionSets', obj);
        });
    }
}

function updateOfflineEvents() {
    var no_offline_template = $( '#no-offline-event-template' );
    var no_offline_template_compiled = _.template( no_offline_template.html() );

    var offline_template = $( '#offline-event-template' );
    var offline_template_compiled = _.template( offline_template.html() );

    return DAO.store.getAll( 'dataValues' ).done( function ( arr ) {
        var orgUnitId = selection.getSelected();
        var programId = $( '#programId' ).val();

        var target = $( '#offlineEventList' );
        target.children().remove();

        if ( arr.length > 0 ) {
            var matched = false;

            $.each( arr, function ( idx, item ) {
                var event = item.executionDate;

                if ( event.organisationUnitId == orgUnitId && event.programId == programId ) {
                    event.index = idx + 1;
                    var html = offline_template_compiled( event );
                    target.append( html );
                    matched = true;
                }
            } );

            if ( !matched ) {
                target.append( no_offline_template_compiled() );
            }
        } else {
            target.append( no_offline_template_compiled() );
        }
    } );
}

function showOfflineEvents() {
    $( "#offlineListDiv table" ).removeClass( 'hidden' );
}

function hideOfflineEvents() {
    $( "#offlineListDiv table" ).addClass( 'hidden' );
}

var haveLocalData = false;

function checkOfflineData( callback ) {
    return DAO.store.getAll( 'dataValues' ).done( function ( arr ) {
        haveLocalData = arr.length > 0;
        if ( callback && typeof callback == 'function' ) callback( haveLocalData );
    } );
}

function uploadOfflineData( event ) {
    $.ajax( {
        url: 'uploadAnonymousEvent.action',
        contentType: 'application/json',
        data: JSON.stringify( event ),
        cache: false
    } ).done( function ( json ) {
        if ( json.response === 'success' ) {
            DAO.store.remove( 'dataValues', event.id ).done( function () {
                updateOfflineEvents();
                searchEvents( eval( getFieldValue( 'listAll' ) ) );
            } );
        }
    } );
}

function uploadLocalData() {
    setHeaderWaitMessage( i18n_uploading_data_notification );

    DAO.store.getAll( 'dataValues' ).done( function ( arr ) {
        if(arr.length == 0) {
            setHeaderDelayMessage( i18n_sync_success );
            return;
        }

        var deferred = $.Deferred();
        var promise = deferred.promise();

        $.each(arr, function(idx, item) {
            promise = promise.pipe(function () {
                uploadOfflineData( item );
            });
        });

        deferred.done(function() {
            setHeaderDelayMessage( i18n_sync_success );
        });

        deferred.resolve();
    });
}

function sync_failed_button() {
    var message = i18n_sync_failed
        + ' <button id="sync_button" type="button">' + i18n_sync_now + '</button>';

    setHeaderMessage( message );

    $( '#sync_button' ).bind( 'click', uploadLocalData );
}

$( document ).ready( function () {
    $.ajaxSetup( {
        type: 'POST',
        cache: false
    } );

    setHeaderWaitMessage(i18n_please_wait_loading);

    $("#programId").attr('disabled', true);

    $( "#orgUnitTree" ).one( "ouwtLoaded", function () {
        var def = $.Deferred();
        var promise = def.promise();

        promise = promise.then( DAO.store.open );
        promise = promise.then( loadPrograms );
        promise = promise.then( loadProgramStages );
        promise = promise.then( loadOptionSets );
        promise = promise.then( updateOfflineEvents );
        promise = promise.then( checkOfflineData );
        promise.then( function () {
            $("#programId").removeAttr('disabled');

            selection.setListenerFunction( organisationUnitSelected );

            dhis2.availability.startAvailabilityCheck();
            selection.responseReceived();
        } );

        def.resolve();
    } );

    $( document ).bind( 'dhis2.online', function ( event, loggedIn ) {
        if ( loggedIn ) {
            checkOfflineData(function(localData) {
                if ( localData ) {
                    var message = i18n_need_to_sync_notification
       	            	+ ' <button id="sync_button" type="button">' + i18n_sync_now + '</button>';

       	            setHeaderMessage( message );

       	            $( '#sync_button' ).bind( 'click', uploadLocalData );
                } else {
                    setHeaderDelayMessage( i18n_online_notification );
                }

                enableFiltering();
                searchEvents( eval( getFieldValue( 'listAll' ) ) );
                $('#commentInput').removeAttr('disabled');
                $('#commentButton').removeAttr('disabled');
                $('#validateBtn').removeAttr('disabled');
            });

            hideOfflineEvents();
        }
        else {
            var form = [
                '<form style="display:inline;">',
                '<label for="username">Username</label>',
                '<input name="username" id="username" type="text" style="width: 70px; margin-left: 10px; margin-right: 10px" size="10"/>',
                '<label for="password">Password</label>',
                '<input name="password" id="password" type="password" style="width: 70px; margin-left: 10px; margin-right: 10px" size="10"/>',
                '<button id="login_button" type="button">Login</button>',
                '</form>'
            ].join( '' );

            setHeaderMessage( form );
            ajax_login();

            showOfflineEvents();
        }
    } );

    $( document ).bind( 'dhis2.offline', function () {
        setHeaderMessage( i18n_offline_notification );
        $('#commentInput').attr('disabled', true);
        $('#commentInput').attr('disabled', true);
        $('#validateBtn').attr('disabled', true);
        disableFiltering();
        showOfflineEvents();
    } );
} );

function disableFiltering() {
    $('#listDiv').hide();
    $('#filterBtn').attr('disabled', true);
    $('#listBtn').attr('disabled', true);
    $('#incompleted').attr('disabled', true);
    $('#removeBtn').attr('disabled', true);
}

function enableFiltering() {
    var filtering = getFieldValue( 'programStageId' ) != undefined && getFieldValue( 'programStageId' ).length != 0;

    if ( filtering ) {
        $( '#filterBtn' ).removeAttr( 'disabled' );
        $( '#listBtn' ).removeAttr( 'disabled' );
        $( '#incompleted' ).removeAttr( 'disabled' );
        $( '#removeBtn' ).removeAttr( 'disabled' );
    }
}

function ajax_login()
{
    $( '#login_button' ).bind( 'click', function()
    {
        var username = $( '#username' ).val();
        var password = $( '#password' ).val();

        $.post( '../dhis-web-commons-security/login.action', {
            'j_username' : username,
            'j_password' : password
        } ).success( function()
        {
            var ret = dhis2.availability.syncCheckAvailability();

            if ( !ret )
            {
                alert( i18n_ajax_login_failed );
            }
        } );
    } );
}

function organisationUnitSelected( orgUnits, orgUnitNames ) {
    showById( 'dataEntryMenu' );
    hideById( 'eventActionMenu' );
    hideById( 'dataEntryInfor' );
    hideById( 'advanced-search' );
    hideById( 'minimized-advanced-search' );
    hideById( 'listDiv' );
    hideById( 'programName' );

    setFieldValue( "listAll", true );
    setFieldValue( "startDate", '' );
    setFieldValue( "endDate", '' );
    setFieldValue( "programStageId", '' );
    setFieldValue( "programId", '' );

    $( '#advancedSearchTB [name=searchText]' ).val( '' );

    setFieldValue( 'orgunitId', orgUnits[0] );
    setFieldValue( 'orgunitName', orgUnitNames[0] );
    hideById( 'listDiv' );
    hideById( 'dataEntryInfor' );

    DAO.store.getAll( 'programs' ).done( function (arr) {
        var programs = [];

        $.each( arr, function ( idx, item ) {
            if ( item.programAssociations.indexOf( orgUnits[0] ) != -1 ) {
                programs.push( item );
            }
        } );

        updateProgramList( programs );
    } );

    updateOfflineEvents();
}

function updateProgramList( arr ) {
    $( '#searchingAttributeIdTD [id=searchObjectId] option' ).remove();
    $( '#advancedSearchTB [id=searchObjectId] option' ).remove();
    clearListById( 'displayInReports' );
    clearListById( 'programId' );

    $( '#programId' ).append( '<option value="" psid="" reportDateDes="' + i18n_report_date + '">[' + i18n_please_select + ']</option>' );

    for ( var i = 0; i < arr.length; i++ ) {
        $( '#programId' ).append(
            '<option value="' + arr[i].id
            + '" puid="' + arr[i].uid
            + '" programType="' + arr[i].type
            + '" psid="' + arr[i].programStages[0].id
            + '" psuid="' + arr[i].programStages[0].uid
            + '" reportDateDes="' + arr[i].programStages[0].reportDateDescription + '">'
            + arr[i].name
            + '</option>' );
    }

    disableCriteriaDiv();
    showById( 'selectDiv' );
}

function disableCriteriaDiv() {
    disable( 'listBtn' );
    disable( 'addBtn' );
    disable( 'filterBtn' );
    disable( 'removeBtn' );
    $( '#criteriaDiv :input' ).each( function ( idx, item ) {
        disable( this.id );
    } );
}

function enableCriteriaDiv() {
    enable( 'listBtn' );
    enable( 'addBtn' );
    enable( 'filterBtn' );
    enable( 'removeBtn' );
    $( '#criteriaDiv :input' ).each( function ( idx, item ) {
        enable( this.id );
    } );
}

function getDataElements() {
    hideById( 'dataEntryInfor' );
    hideById( 'listDiv' );
    $( '#searchingAttributeIdTD [id=searchObjectId] option' ).remove();
    $( '#advancedSearchTB [id=searchObjectId] option' ).remove();
    var programStageId = $( '#programId option:selected' ).attr( 'psid' );
    setFieldValue( 'programStageId', programStageId );
    setInnerHTML( 'reportDateDescriptionField', $( '#programId option:selected' ).attr( 'reportDateDes' ) );
    setInnerHTML( 'reportDateDescriptionField2', $( '#programId option:selected' ).attr( 'reportDateDes' ) );

    if ( programStageId == '' ) {
        removeAllAttributeOption();
        disableCriteriaDiv();
        enable( 'orgunitName' );
        enable( 'programId' );
        hideById( 'listDiv' );
        setFieldValue( 'searchText' );
        updateOfflineEvents();
        return;
    }

    $.getJSON( "getProgramStageDataElementList.action", {
        programStageId: programStageId
    }, function ( json ) {
        $( '#advancedSearchTB [name=searchText]' ).val( '' );
        $( '.stage-object-selected' ).attr( 'psid', $( '#programId option:selected' ).attr( "psid" ) );

        clearListById( 'searchObjectId' );
        clearListById( 'displayInReports' );

        $('[name=searchObjectId]').append('<option value="" >[' + i18n_please_select + ']</option>');

        $.each(json.programStageDataElements, function() {
            $('[name=searchObjectId]').append('<option value="' + this.id + '" uid="' + this.uid + '" type="' + this.type + '">' + this.name + '</option>');

            if( this.displayInReports == 'true' ) {
                $('#displayInReports').append('<option value="' + this.id + '" uid="' + this.uid + '" ></option>');
            }
        });

        enableCriteriaDiv();
        validateSearchEvents( true );
    } ).fail(function() {
        enable( 'addBtn' );
    });

    updateOfflineEvents();
}

function dataElementOnChange( this_ ) {
    var container = $(this_).parent().parent().attr('id');
    var element = $('#' + container + ' [id=searchText]');
    var valueType = $('#' + container + ' [id=searchObjectId] option:selected').attr('type');

    if( valueType == 'date' ) {
        element.replaceWith(getDateField(container));
        datePickerValid('searchText_' + container);
    }
    else {
        $('#searchText_' + container).datepicker("destroy");
        $('#' + container + ' [id=dateOperator]').replaceWith("");

        if( valueType == 'bool' ) {
            element.replaceWith(getTrueFalseBox());
        }
        else if( valueType == 'optionset' ) {
            element.replaceWith(searchTextBox);
            autocompletedFilterField(container + " [id=searchText]", $(this_).find("option:selected").attr('uid'));
        }
        else if( valueType == 'username' ) {
            autocompletedUsernameField($(this_).attr('id'));
        }
        else {
            element.replaceWith(searchTextBox);
        }
    }
}

function autocompletedFilterField( idField, searchObjectId ) {
    var input = $( "#" + idField );
    input.css( "width", "237px" );
    input.autocomplete( {
        delay: 0,
        minLength: 0,
        source: function ( request, response ) {
            $.ajax( {
                url: "getOptions.action?id=" + searchObjectId + "&query=" + input.val(),
                dataType: "json",
                cache: false,
                success: function ( data ) {
                    response( $.map( data.options, function ( item ) {
                        return {
                            label: item.o,
                            id: item.o
                        };
                    } ) );
                }
            } );
        },
        select: function ( event, ui ) {
            input.val( ui.item.value );
            input.autocomplete( "close" );
        }
    } )
        .addClass( "ui-widget" );

    input.data( "autocomplete" )._renderItem = function ( ul, item ) {
        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append( "<a>" + item.label + "</a>" )
            .appendTo( ul );
    };

    var wrapper = this.wrapper = $( "<span style='width:200px'>" )
        .addClass( "ui-combobox" )
        .insertAfter( input );

    var button = $( "<a style='width:20px; margin-bottom:-5px;height:20px;'>" )
        .attr( "tabIndex", -1 )
        .attr( "title", i18n_show_all_items )
        .appendTo( wrapper )
        .button( {
            icons: {
                primary: "ui-icon-triangle-1-s"
            },
            text: false
        } )
        .addClass( 'small-button' )
        .click( function () {
            if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
                input.autocomplete( "close" );
                return;
            }
            $( this ).blur();
            input.autocomplete( "search", "" );
            input.focus();
        } );
}

function autocompletedUsernameField( idField ) {
    var input = $( "#" + idField );
    input.parent().width( input.width() + 200 );
    var dataElementId = input.attr( 'id' ).split( '-' )[1];

    input.autocomplete( {
        delay: 0,
        minLength: 0,
        source: function ( request, response ) {
            $.ajax( {
                url: "getUsernameList.action?query=" + input.val(),
                dataType: "json",
                cache: false,
                success: function ( data ) {
                    response( $.map( data.usernames, function ( item ) {
                        return {
                            label: item.u,
                            id: item.u
                        };
                    } ) );
                }
            } );
        },
        select: function ( event, ui ) {
            var fieldValue = ui.item.value;

            if ( !dhis2.trigger.invoke( "caseentry-value-selected", [dataElementId, fieldValue] ) ) {
                input.val( "" );
                return false;
            }

            input.val( fieldValue );
            if ( !unSave ) {
                saveVal( dataElementId );
            }
            input.autocomplete( "close" );
        },
        change: function ( event, ui ) {
            if ( !ui.item ) {
                var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $( this ).val() ) + "$", "i" ),
                    valid = false;
                if ( !valid ) {
                    $( this ).val( "" );
                    if ( !unSave )
                        saveVal( dataElementId );
                    input.data( "autocomplete" ).term = "";
                    return false;
                }
            }
        }
    } )
        .addClass( "ui-widget" );

    input.data( "autocomplete" )._renderItem = function ( ul, item ) {
        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append( "<a>" + item.label + "</a>" )
            .appendTo( ul );
    };

    var wrapper = this.wrapper = $( "<span style='width:200px'>" )
        .addClass( "ui-combobox" )
        .insertAfter( input );

    var button = $( "<a style='width:20px; margin-bottom:-5px;height:20px;'>" )
        .attr( "tabIndex", -1 )
        .attr( "title", i18n_show_all_items )
        .appendTo( wrapper )
        .button( {
            icons: {
                primary: "ui-icon-triangle-1-s"
            },
            text: false
        } )
        .addClass( 'small-button' )
        .click( function () {
            if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
                input.autocomplete( "close" );
                return;
            }
            $( this ).blur();
            input.autocomplete( "search", "" );
            input.focus();
        } );
}

function removeAllAttributeOption() {
    $( '#advancedSearchTB tbody tr' ).each( function ( i, item ) {
        if ( i > 0 ) {
            $( item ).remove();
        }
    } )
}

function validateSearchEvents( listAll ) {
    listAll = eval( listAll );
    setFieldValue( 'listAll', listAll );

    var flag = true;
    if ( !listAll ) {
        if ( getFieldValue( 'startDate' ) == "" || getFieldValue( 'endDate' ) == "" ) {
            showWarningMessage( i18n_specify_a_date );
            flag = false;
        }

        if ( flag && !listAll && $( '#filterBtn' ).attr( "disabled" ) == "disabled" ) {
            $( '#advancedSearchTB tr' ).each( function ( index, row ) {
                if ( index > 1 ) {
                    $( row ).find( ':input' ).each( function ( idx, item ) {
                        var input = $( item );
                        if ( input.attr( 'type' ) != 'button' && idx == 0 && input.val() == '' ) {
                            showWarningMessage( i18n_specify_data_element );
                            flag = false;
                        }
                    } )
                }
            } );
        }
    }

    if ( flag ) {
        searchEvents( listAll );
    }
}

function searchEvents( listAll ) {
    var search = getFieldValue( 'programStageId' ) != undefined && getFieldValue( 'programStageId' ).length != 0;

    if ( !search ) {
        return;
    }

    hideById( 'dataEntryInfor' );
    hideById( 'listDiv' );

    var params = '';
    $( '#displayInReports option' ).each( function ( i, item ) {
        var input = $( item );
        params += '&filterValues=de_' + input.val() + '_false_';
    } );

    if ( listAll ) {
        params += '&startDate=';
        params += '&endDate=';
    }
    else {
        var value = '';
        var searchingValue = '';
        params += '&startDate=' + getFieldValue( 'startDate' );
        params += '&endDate=' + getFieldValue( 'endDate' );
        if ( byId( "incompleted" ).checked ) {
            params += '&useCompletedEvents=false';
        }
        $( '#advancedSearchTB tr' ).each( function ( index, row ) {
            if ( index > 1 ) {
                $( row ).find( ':input' ).each( function ( idx, item ) {
                    var input = $( item );
                    if ( input.attr( 'type' ) != 'button' ) {
                        if ( idx == 0 && input.val() != '' ) {
                            searchingValue = 'de_' + input.val() + '_false_';
                        }
                        else if ( input.val() != '' ) {
                            value += $.trim( input.val() ).toLowerCase();
                        }
                    }
                } );

                if ( value != '' ) {
                    searchingValue += getValueFormula( value );
                    params += '&filterValues=' + searchingValue;
                }
                searchingValue = '';
                value = '';
            }
        } )
    }

    params += '&facilityLB=selected';
    params += '&orgunitIds=' + getFieldValue( 'orgunitId' );
    params += '&programStageId=' + $( '#programId option:selected' ).attr( 'psid' );
    params += '&userOrganisationUnit=false';
    params += '&userOrganisationUnitChildren=false';

    contentDiv = 'listDiv';
    showLoader();

    $.ajax( {
        type: "POST",
        url: 'searchEvents.action',
        data: params,
        dataType: 'text',
        cache: false,
        success: function ( data ) {
            if ( data.indexOf( "<!DOCTYPE" ) != 0 ) {
                hideById( 'dataEntryInfor' );
                setInnerHTML( 'listDiv', data );
            }

            hideLoader();
            showById( 'listDiv' );
        }
    } ).fail(function() {
        hideById( 'dataEntryInfor' );
        hideById( 'listDiv' );
    } ).always(function() {
        var searchInfor = (listAll) ? i18n_list_all_events : i18n_search_events_by_dataelements;
        setInnerHTML( 'searchInforTD', searchInfor );

        if ( !listAll && $( '#filterBtn' ).attr( "disabled" ) == "disabled" ) {
            showById( 'minimized-advanced-search' );
            hideById( 'advanced-search' );
        }
        else {
            hideById( 'minimized-advanced-search' );
            hideById( 'advanced-search' );
            showById( 'filterBtn' );
        }

        hideLoader();
    });
}

function getValueFormula( value ) {
    if ( value.indexOf( '"' ) != value.lastIndexOf( '"' ) ) {
        value = value.replace( /"/g, "'" );
    }

    var flag = value.match( /[>|>=|<|<=|=|!=]+[ ]*/ );

    if( value.indexOf("'") == -1 ) {
        if( flag == null ) {
            value = "='" + value + "'";
        }
        else {
            value = value.replace(flag, flag + "'");
            value += "'";
        }
    }
    else {
        if( flag == null ) {
            value = "=" + value;
        }
    }

    return value;
}

function removeEvent( programStageId ) {
    DAO.store.get('dataValues', programStageId).done(function(obj) {
        if(obj) {
            if( confirm(i18n_comfirm_delete_event) ) {
                DAO.store.remove('dataValues', programStageId).always(function() {
                    updateOfflineEvents();
                    // needed, seemed that from time-to-time the events are updated too early, could be idb related
                    setTimeout(updateOfflineEvents, 100);
                });
            }
        } else {
            removeItem( programStageId, '', i18n_comfirm_delete_event, 'removeCurrentEncounter.action' );
        }
    });
}

function showUpdateEvent( programStageInstanceId ) {
    hideById( 'dataEntryMenu' );
    showById( 'eventActionMenu' );
    $( "[name=eventActionLink]" ).hide();
    hideById( 'selectDiv' );
    hideById( 'searchDiv' );
    hideById( 'listDiv' );
    hideById( 'offlineListDiv' );
    setFieldValue( 'programStageInstanceId', programStageInstanceId );
	setInnerHTML( 'dataEntryFormDiv', '' );
    showLoader();

    service.displayProgramStage( getFieldValue( 'programStageId' ), programStageInstanceId, getFieldValue( 'orgunitId' ) );
	jQuery('.stage-object-selected').attr('id', 'ps_' + programStageInstanceId);
}

function backEventList() {
    showById( 'dataEntryMenu' );
    hideById( 'eventActionMenu' );
    hideById( 'dataEntryInfor' );
    hideById( 'programName' );
    showById( 'selectDiv' );
    showById( 'searchDiv' );
    showById( 'listDiv' );
    showById( 'offlineListDiv' );

    updateOfflineEvents();
    searchEvents( eval( getFieldValue( 'listAll' ) ) );
}

function showAddEventForm( isCreateEvent ) {
    showById( 'eventActionMenu' );
    $( "[name=eventActionLink]" ).hide();
    hideById( 'dataEntryMenu' );
    setInnerHTML( 'dataEntryFormDiv', '' );
    hideById( 'selectDiv' );
    hideById( 'searchDiv' );
    hideById( 'listDiv' );
    hideById( 'offlineListDiv' );
    showById( 'programName' );
    hideById( 'actionDiv' );
    showById( 'dataEntryInfor' );
    setFieldValue( 'programStageInstanceId', '0' );
    setInnerHTML( 'programName', $( '#programId option:selected' ).text() );
	setInnerHTML( 'orgunitNameInfor', getFieldValue("orgunitName"));
	if( isCreateEvent != undefined ){
		addNewEvent();
	}
}

function addNewEvent() {
    var programStageInstanceId = getFieldValue( 'programStageInstanceId' );
    var programId = $( '#programId option:selected' ).val();
    var executionDate = getFieldValue( 'executionDate' );
    var orgunitId = getFieldValue( 'orgunitId' );

    $( "#executionDate" ).css( 'background-color', SAVING_COLOR );

    service.saveExecutionDate( programId, programStageInstanceId, executionDate, orgunitId );
}

function completedAndAddNewEvent() {
    doComplete( true );
}

function removeEmptyEvents() {
    var result = window.confirm( i18n_confirm_remove_empty_events );

    if ( result ) {
        $.getJSON( "removeEmptyEvents.action",
            {
                programStageId: $( '#selectDiv [id=programId] option:selected' ).attr( 'psid' )
            },
            function ( json ) {
                if ( json.response == 'success' ) {
                    showSuccessMessage( i18n_remove_empty_events_success );
                    validateSearchEvents( true )
                }
            } );
    }
}

function removeCurrentEvent() {
    var programStageInstanceId = getFieldValue( 'programStageInstanceId' );

    DAO.store.get('dataValues', programStageInstanceId).done(function(obj) {
        if(obj) {
            if( confirm(i18n_comfirm_delete_event) ) {
                DAO.store.remove('dataValues', programStageInstanceId).always(function() {
                    setTimeout(backEventList, 200);
                });
            }
        } else {
            removeCurrentEventFromServer();
        }
    });
}

function removeCurrentEventFromServer() {
    var result = window.confirm( i18n_comfirm_delete_event );

    if ( result ) {
        $.postJSON( "removeCurrentEncounter.action", {
            "id": getFieldValue( 'programStageInstanceId' )
        },
        function ( json ) {
            if ( json.response == "success" ) {
                backEventList();
            }
            else if ( json.response == "error" ) {
                showWarningMessage( json.message );
            }
        } );
    }
}

function showFilterForm() {
    showById( 'advanced-search' );
    hideById( 'minimized-advanced-search' );
    disable( 'filterBtn' );
    setFieldValue( 'listAll', false );
}

function removeAllOption() {
    enable( 'filterBtn' );

    $( '#advancedSearchTB tr' ).each( function ( idx ) {
        if( idx > 2 ) {
            $(this).remove();
        }
        else if( idx == 2 ) {
            $(this).find(':input').each(function( idx, item ) {
                var input = $(item);
                if( input.attr('type') != 'button' ) {
                    input.val('');
                }
            });
        }
    } );

    $( '#searchObjectId' ).val( "" );
    $( '#searchText' ).val( "" );
    searchEvents( eval( getFieldValue( "listAll" ) ) );
}

function ajaxExecutionDate( programId, programStageInstanceId, executionDate, organisationUnitId ) {
    return $.ajax( {
        url: 'saveExecutionDate.action',
        data: createExecutionDate( programId, programStageInstanceId, executionDate, organisationUnitId ),
        type: 'POST',
        dataType: 'json',
        cache: false
    } );
}

// execution date module
var service = (function () {
    return {
        saveExecutionDate: function( programId, programStageInstanceId, executionDate, organisationUnitId ) {
            ajaxExecutionDate(programId, programStageInstanceId, executionDate, organisationUnitId).done(function ( json ) {
                if ( json.response == 'success' ) {
                    $( "#executionDate" ).css( 'background-color', SUCCESS_COLOR );
                    setFieldValue( 'programStageInstanceId', json.message );
					jQuery('.stage-object-selected').attr('id', json.message );
                    showUpdateEvent( json.message );
                }
                else {
                    $( "#executionDate" ).css( 'background-color', ERROR_COLOR );
                    showWarningMessage( json.message );
                }
            } ).fail( function () {
                if(programStageInstanceId == 0) {
                    DAO.store.getKeys( 'dataValues' ).done( function ( keys ) {
                        var i = 100;

                        for(; i<10000; i++) {
                            if( keys.indexOf("local" + i) == -1 ) break;
                        }

                        programStageInstanceId = "local"+i;

                        var data = {};
                        data.id = programStageInstanceId;
                        data.executionDate = createExecutionDate(programId, programStageInstanceId, executionDate, organisationUnitId);
                        data.executionDate.completed = 'false';

                        this.set( 'dataValues', data).done(function() {
                            setFieldValue( 'programStageInstanceId', programStageInstanceId );
                            $( "#executionDate" ).css( 'background-color', SUCCESS_COLOR );
                            showUpdateEvent( programStageInstanceId );
                        });
                    });
                } else {
                    // if we have a programStageInstanceId, just reuse that one
                    setFieldValue( 'programStageInstanceId', programStageInstanceId );
                    $( "#executionDate" ).css( 'background-color', SUCCESS_COLOR );
                    showUpdateEvent( programStageInstanceId );
                }
            } );
        },

        displayProgramStage: function( programStageId, programStageInstanceId, organisationUnitId ) {
            loadProgramStage( programStageId, programStageInstanceId, organisationUnitId, function ( data ) {
                $( '#dataEntryFormDiv' ).html( data );
                updateDataForm();
            },function () {
                $( '#dataEntryFormDiv' ).html( "<div class='message message-info'>Unable to load form.</div>" );
                hideById( 'loaderDiv' );
            } );
        }
    }
})();

function updateDataForm() {
    $( '#inputCriteriaDiv' ).remove();
    showById( 'programName' );
    showById( 'actionDiv' );
    var programName = $( '#programId option:selected' ).text();
    var programStageId = $( '#programId option:selected' ).attr( 'psid' );
    $( '.stage-object-selected' ).attr( 'psid', programStageId );
    setInnerHTML( 'programName', programName );
    $('#executionDate').css('width',430);
    $('#executionDate').css('margin-right',34);

    if ( getFieldValue( 'completed' ) == 'true' ) {
        disable( "completeBtn" );
        enable( "uncompleteBtn" );
    }
    else {
        enable( "completeBtn" );
        disable( "uncompleteBtn" );
    }

    hideById( 'loaderDiv' );
    showById( 'dataEntryInfor' );
    showById( 'entryFormContainer' );

    $( "#entryForm :input" ).each( function () {
        if ( ( $( this ).attr( 'options' ) != null && $( this ).attr( 'options' ) == 'true' )
            || ( $( this ).attr( 'username' ) != null && $( this ).attr( 'username' ) == 'true' ) ) {
            var input = $( this );
            input.parent().width( input.width() + 200 );
        }
    } );
}

function createExecutionDate( programId, programStageInstanceId, executionDate, organisationUnitId ) {
    var data = {};

    if(programId)
        data.programId = programId;

    if(programStageInstanceId)
        data.programStageInstanceId = programStageInstanceId;

    if(executionDate)
        data.executionDate = executionDate;

    if(organisationUnitId) {
        data.organisationUnitId = organisationUnitId;
        data.organisationUnit = organisationUnits[organisationUnitId].n;
    }

    return data;
}

function createProgramStage( programStageId, programStageInstanceId, organisationUnitId ) {
    var data = {};

    if(programStageId)
        data.programStageId = programStageId;

    if(programStageInstanceId)
        data.programStageInstanceId = programStageInstanceId;

    if(organisationUnitId)
        data.organisationUnitId = organisationUnitId;

    return data;
}

function loadProgramStage( programStageId, programStageInstanceId, organisationUnitId, success, fail ) {
    var data = createProgramStage( programStageId, programStageInstanceId, organisationUnitId );

    if( programStageId === undefined ) {
        programStageId = $('#programId option:selected').attr('psid');
    }

    DAO.store.get('programStages', programStageId ).done(function(obj) {
        if(success) success(obj.form);
    } ).fail(function() {
        $.ajax( {
            url: 'dataentryform.action',
            data: data,
            dataType: 'html',
            cache: false
        } ).done(function(data) {
            if(success) success(data);
        } ).fail(function() {
            if(fail) fail();
        });
    });
}
