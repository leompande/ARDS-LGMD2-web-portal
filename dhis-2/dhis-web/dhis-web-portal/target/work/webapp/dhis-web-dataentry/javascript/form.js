
dhis2.util.namespace( 'dhis2.de' );

// whether current user has any organisation units
dhis2.de.emptyOrganisationUnits = false;

// Identifiers for which zero values are insignificant, also used in entry.js
dhis2.de.significantZeros = [];

// Array with associative arrays for each data element, populated in select.vm
dhis2.de.dataElements = [];

// Associative array with [indicator id, expression] for indicators in form,
// also used in entry.js
dhis2.de.indicatorFormulas = [];

// Array with associative arrays for each data set, populated in select.vm
dhis2.de.dataSets = [];

// Maps input field to optionSet
dhis2.de.optionSets = {};

// Associative array with identifier and array of assigned data sets
dhis2.de.dataSetAssociationSets = [];

// Associate array with mapping between organisation unit identifier and data
// set association set identifier
dhis2.de.organisationUnitAssociationSetMap = [];

// Default category combo uid
dhis2.de.defaultCategoryCombo = undefined;

// Category combinations for data value attributes
dhis2.de.categoryCombos = {};

// Categories for data value attributes
dhis2.de.categories = {};

// Array with keys on form {dataelementid}-{optioncomboid}-min/max with min/max
// values
dhis2.de.currentMinMaxValueMap = [];

// Indicates whether any data entry form has been loaded
dhis2.de.dataEntryFormIsLoaded = false;

// Indicates whether meta data is loaded
dhis2.de.metaDataIsLoaded = false;

// Currently selected organisation unit identifier
dhis2.de.currentOrganisationUnitId = null;

// Currently selected data set identifier
dhis2.de.currentDataSetId = null;

// Array with category objects, null if default category combo / no categories
dhis2.de.currentCategories = null;

// Current offset, next or previous corresponding to increasing or decreasing value
dhis2.de.currentPeriodOffset = 0;

// Username of user who marked the current data set as complete if any
dhis2.de.currentCompletedByUser = null;

// Period type object
dhis2.de.periodTypeFactory = new PeriodType();

// Instance of the StorageManager
dhis2.de.storageManager = new StorageManager();

// Indicates whether current form is multi org unit
dhis2.de.multiOrganisationUnit = false;

// "organisationUnits" object inherited from ouwt.js

var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_RED = '#ff8a8a';
var COLOR_ORANGE = '#ff6600';
var COLOR_WHITE = '#fff';
var COLOR_GREY = '#ccc';

var COLOR_BORDER_ACTIVE = '#73ad72';
var COLOR_BORDER = '#aaa';

var DEFAULT_TYPE = 'int';
var DEFAULT_NAME = '[unknown]';

var FORMTYPE_CUSTOM = 'custom';
var FORMTYPE_SECTION = 'section';
var FORMTYPE_MULTIORG_SECTION = 'multiorg_section';
var FORMTYPE_DEFAULT = 'default';

var EVENT_FORM_LOADED = "dhis-web-dataentry-form-loaded";

var MAX_DROPDOWN_DISPLAYED = 30;

var DAO = DAO || {};

function getCurrentOrganisationUnit() 
{
    if ( $.isArray( dhis2.de.currentOrganisationUnitId ) ) 
    {
        return dhis2.de.currentOrganisationUnitId[0];
    }

    return dhis2.de.currentOrganisationUnitId;
}

DAO.store = new dhis2.storage.Store( {
    name: 'dhis2',
    adapters: [ dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter ],
    objectStores: [ 'optionSets' ]
} );

( function( $ ) {
    $.safeEach = function( arr, fn ) 
    {
        if ( arr )
        {
            $.each( arr, fn );
        }
    };
} )( jQuery );

selection.setListenerFunction( organisationUnitSelected );

/**
 * Page init. The order of events is:
 *
 * 1. Load ouwt 2. Load meta-data (and notify ouwt) 3. Check and potentially
 * download updated forms from server
 */
$( document ).ready( function()
{
    $.ajaxSetup( {
        type: 'POST',
        cache: false
    } );

    $( '#loaderSpan' ).show();

    $( '#orgUnitTree' ).one( 'ouwtLoaded', function()
    {
        log( 'Ouwt loaded' );
        loadMetaData();
   } );

    $( document ).bind( 'dhis2.online', function( event, loggedIn )
	{
	    if ( loggedIn )
	    {
	        if ( dhis2.de.storageManager.hasLocalData() )
	        {
	            var message = i18n_need_to_sync_notification
	            	+ ' <button id="sync_button" type="button">' + i18n_sync_now + '</button>';

	            setHeaderMessage( message );

	            $( '#sync_button' ).bind( 'click', uploadLocalData );
	        }
	        else
	        {
	            if ( dhis2.de.emptyOrganisationUnits ) {
	                setHeaderMessage( i18n_no_orgunits );
	            } 
	            else {
	                setHeaderDelayMessage( i18n_online_notification );
	            }
            }
	    }
	    else
	    {
            var form = [
                '<form style="display:inline;">',
                    '<label for="username">Username</label>',
                    '<input name="username" id="username" type="text" style="width: 70px; margin-left: 10px; margin-right: 10px" size="10"/>',
                    '<label for="password">Password</label>',
                    '<input name="password" id="password" type="password" style="width: 70px; margin-left: 10px; margin-right: 10px" size="10"/>',
                    '<button id="login_button" type="button">Login</button>',
                '</form>'
            ].join('');

            setHeaderMessage( form );
	        ajax_login();
	    }
	} );

    $( document ).bind( 'dhis2.offline', function()
    {
      if ( dhis2.de.emptyOrganisationUnits ) {
          setHeaderMessage( i18n_no_orgunits );
      } 
      else {
          setHeaderMessage( i18n_offline_notification );
      }
    } );

    dhis2.availability.startAvailabilityCheck();
} );

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

function loadMetaData()
{
    var KEY_METADATA = 'metadata';

    $.ajax( {
    	url: 'getMetaData.action',
    	dataType: 'json',
    	success: function( json )
	    {
	        sessionStorage[KEY_METADATA] = JSON.stringify( json.metaData );
	    },
	    complete: function()
	    {
	        var metaData = JSON.parse( sessionStorage[KEY_METADATA] );

	        dhis2.de.emptyOrganisationUnits = metaData.emptyOrganisationUnits;
	        dhis2.de.significantZeros = metaData.significantZeros;
	        dhis2.de.dataElements = metaData.dataElements;
	        dhis2.de.indicatorFormulas = metaData.indicatorFormulas;
	        dhis2.de.dataSets = metaData.dataSets;
	        dhis2.de.optionSets = metaData.optionSets;
	        dhis2.de.dataSetAssociationSets = metaData.dataSetAssociationSets;
	        dhis2.de.organisationUnitAssociationSetMap = metaData.organisationUnitAssociationSetMap;
	        dhis2.de.defaultCategoryCombo = metaData.defaultCategoryCombo;
	        dhis2.de.categoryCombos = metaData.categoryCombos;
	        dhis2.de.categories = metaData.categories;

	        dhis2.de.metaDataIsLoaded = true;
	        selection.responseReceived(); // Notify that meta data is loaded
	        $( '#loaderSpan' ).hide();
	        log( 'Meta-data loaded' );

	        updateForms();
	    }
	} );
}

function uploadLocalData()
{
    if ( !dhis2.de.storageManager.hasLocalData() )
    {
        return;
    }

    var dataValues = dhis2.de.storageManager.getAllDataValues();
    var completeDataSets = dhis2.de.storageManager.getCompleteDataSets();

    setHeaderWaitMessage( i18n_uploading_data_notification );

    var dataValuesArray = dataValues ? Object.keys( dataValues ) : [];
    var completeDataSetsArray = completeDataSets ? Object.keys( completeDataSets ) : [];

    function pushCompleteDataSets( array )
    {
        if ( array.length < 1 )
        {
            return;
        }

        var key = array[0];
        var value = completeDataSets[key];

        log( 'Uploaded complete data set: ' + key + ', with value: ' + value );

        $.ajax( {
            url: 'registerCompleteDataSet.action',
            data: value,
            dataType: 'json',
            success: function( data, textStatus, jqXHR )
            {
                if ( data.status == 2 )
                {
                    log( 'DataSet is locked' );
                    setHeaderMessage( i18n_register_complete_failed_dataset_is_locked );
                }
                else
                {
                    log( 'Successfully saved complete dataset with value: ' + value );
                    dhis2.de.storageManager.clearCompleteDataSet( value );
                    ( array = array.slice( 1 ) ).length && pushCompleteDataSets( array );

                    if ( array.length < 1 )
                    {
                        setHeaderDelayMessage( i18n_sync_success );
                    }
                }
            },
            error: function( jqXHR, textStatus, errorThrown )
            {
                var message = i18n_sync_failed
                    + ' <button id="sync_button" type="button">' + i18n_sync_now + '</button>';

                setHeaderMessage( message );

                $( '#sync_button' ).bind( 'click', uploadLocalData );
            }
        } );
    }

    ( function pushDataValues( array )
    {
        if ( array.length < 1 )
        {
            setHeaderDelayMessage( i18n_online_notification );

            pushCompleteDataSets( completeDataSetsArray );

            return;
        }

        var key = array[0];
        var value = dataValues[key];

        if ( value.value.length > 254 )
        {
            value.value = value.value.slice(0, 254);
        }

        log( 'Uploading data value: ' + key + ', with value: ' + value );

        $.ajax( {
            url: '../api/dataValues',
            data: value,
            dataType: 'json',
            type: 'post',
            success: function( data, textStatus, xhr )
            {
            	dhis2.de.storageManager.clearDataValueJSON( value );
                log( 'Successfully saved data value with value: ' + value );
                ( array = array.slice( 1 ) ).length && pushDataValues( array );

                if ( array.length < 1 && completeDataSetsArray.length > 0 )
                {
                    pushCompleteDataSets( completeDataSetsArray );
                }
                else
                {
                    setHeaderDelayMessage( i18n_sync_success );
                }
            },
            error: function( xhr, textStatus, errorThrown )
            {
            	if ( 409 == xhr.status ) // Invalid value or locked
            	{
            		// Ignore value for now TODO needs better handling for locking
            		
            		dhis2.de.storageManager.clearDataValueJSON( value );
            	}
            	else // Connection lost during upload
            	{
	                var message = i18n_sync_failed
	                    + ' <button id="sync_button" type="button">' + i18n_sync_now + '</button>';
	
	                setHeaderMessage( message );
	                $( '#sync_button' ).bind( 'click', uploadLocalData );
            	}
            }
        } );
    } )( dataValuesArray );
}

function addEventListeners()
{
    var dataSetId = $( '#selectedDataSetId' ).val();
	var formType = dhis2.de.dataSets[dataSetId].type;

    $( '.entryfield' ).each( function( i )
    {
        var id = $( this ).attr( 'id' );

        var split = splitFieldId( id );
        var dataElementId = split.dataElementId;
        var optionComboId = split.optionComboId;
        dhis2.de.currentOrganisationUnitId = split.organisationUnitId;

        var type = getDataElementType( dataElementId );

        $( this ).unbind( 'focus' );
        $( this ).unbind( 'blur' );
        $( this ).unbind( 'change' );
        $( this ).unbind( 'dblclick' );
        $( this ).unbind( 'keyup' );

        $( this ).focus( valueFocus );

        $( this ).blur( valueBlur );

        $( this ).change( function()
        {
            saveVal( dataElementId, optionComboId, id );
        } );

        $( this ).dblclick( function()
        {
            viewHist( dataElementId, optionComboId );
        } );

        $( this ).keyup( function( event )
        {
            keyPress( event, this );
        } );

		if ( formType != FORMTYPE_CUSTOM )
		{
        	$( this ).css( 'width', '100%' );
		}

        if ( type == 'date' )
        {
            $( this ).css( 'width', '100%' );
            datePicker( id );
        }
    } );
    
    $( '.entryfield' ).not( '.entryarea' ).each( function( i )
    {
		if ( formType != FORMTYPE_CUSTOM )
		{
        	$( this ).css( 'text-align', 'center' );
		}
    } );

    $( '.entryarea' ).each( function( i )
    {
    	$( this ).css( 'min-width', '264px' );
    	$( this ).css( 'min-height', '45px' );
    } );

    $( '.entryselect' ).each( function( i )
    {
        var id = $( this ).attr( 'id' );
        var split = splitFieldId( id );

        var dataElementId = split.dataElementId;
        var optionComboId = split.optionComboId;

        $( this ).unbind( 'focus' );
        $( this ).unbind( 'change' );

        $( this ).focus( valueFocus );

        $( this ).blur( valueBlur );

        $( this ).change( function()
        {
            saveBoolean( dataElementId, optionComboId, id );
        } );

        $( this ).css( 'width', '88%' );
        $( this ).css( 'margin-right', '2px' );
    } );

    $( '.entrytrueonly' ).each( function( i )
    {
        var id = $( this ).attr( 'id' );
        var split = splitFieldId( id );

        var dataElementId = split.dataElementId;
        var optionComboId = split.optionComboId;

        $( this ).unbind( 'focus' );
        $( this ).unbind( 'change' );

        $( this ).focus( valueFocus );
        $( this ).blur( valueBlur );

        $( this ).change( function()
        {
            saveTrueOnly( dataElementId, optionComboId, id );
        } );

        $( this ).css( 'width', '60%' );
    } );

    $( '.entryoptionset' ).each( function( i )
    {
        var id = $( this ).attr( 'id' );
        var split = splitFieldId( id );

        var dataElementId = split.dataElementId;
        var optionComboId = split.optionComboId;

        $( this ).unbind( 'focus' );
        $( this ).unbind( 'change' );

        $( this ).focus( valueFocus );
        $( this ).blur( valueBlur );

        $( this ).change( function()
        {
            saveVal( dataElementId, optionComboId, id );
        } );

        if ( formType != FORMTYPE_CUSTOM ) 
        {
            $( this ).css( 'width', '80%' );
            $( this ).css( 'text-align', 'center' );
        }
    } );

    $( '.commentlink' ).each( function( i )
    {
        var id = $( this ).attr( 'id' );
        var split = splitFieldId( id );

        var dataElementId = split.dataElementId;
        var optionComboId = split.optionComboId;

        $( this ).unbind( 'click' );
        
        $( this ).attr( "src", "../images/comment.png" );
        $( this ).attr( "title", i18n_view_comment );
        
        $( this ).css( "cursor", "pointer" );
        
        $( this ).click ( function() {
        	viewHist( dataElementId, optionComboId );
        } );
    } );
}

function resetSectionFilters()
{
    $( '#filterDataSetSectionDiv' ).hide();
    $( '.formSection' ).show();
}

function clearSectionFilters()
{
    $( '#filterDataSetSection' ).children().remove();
    $( '#filterDataSetSectionDiv' ).hide();
    $( '.formSection' ).show();
}

function clearPeriod()
{
    clearListById( 'selectedPeriodId' );
    clearEntryForm();
}

function clearEntryForm()
{
    $( '#contentDiv' ).html( '' );

    dhis2.de.currentPeriodOffset = 0;

    dhis2.de.dataEntryFormIsLoaded = false;

    $( '#completenessDiv' ).hide();
    $( '#infoDiv' ).hide();
}

function loadForm()
{
	var dataSetId = dhis2.de.currentDataSetId;
	
	dhis2.de.currentOrganisationUnitId = selection.getSelected()[0];

    if ( !dhis2.de.multiOrganisationUnit && dhis2.de.storageManager.formExists( dataSetId ) )
    {
        log( 'Loading form locally: ' + dataSetId );

        var html = dhis2.de.storageManager.getForm( dataSetId );

        $( '#contentDiv' ).html( html );

        if ( dhis2.de.dataSets[dataSetId].renderAsTabs ) {
            $( "#tabs" ).tabs();
        }

        enableSectionFilter();

        loadDataValues();
        insertOptionSets();
    }
    else
    {
        log( 'Loading form remotely: ' + dataSetId );

        $( '#contentDiv' ).load( 'loadForm.action', 
        {
            dataSetId : dataSetId,
            multiOrganisationUnit: dhis2.de.multiOrganisationUnit ? getCurrentOrganisationUnit() : ''
        }, 
        function() 
        {
            if ( !dhis2.de.multiOrganisationUnit )
            {
                if ( dhis2.de.dataSets[dataSetId].renderAsTabs ) {
                    $( "#tabs" ).tabs();
                }

                enableSectionFilter();
            }
            else
            {
                $( '#currentOrganisationUnit' ).html( i18n_no_organisationunit_selected );
            }

            insertOptionSets();
            loadDataValues();
        } );
    }
}

//------------------------------------------------------------------------------
// Section filter
//------------------------------------------------------------------------------

function enableSectionFilter()
{
    var $sectionHeaders = $( '.formSection .cent h3' );
    clearSectionFilters();

    if ( $sectionHeaders.size() > 1)
    {
        $( '#filterDataSetSection' ).append( "<option value='all'>" + i18n_show_all_sections + "</option>" );

        $sectionHeaders.each( function( idx, value ) 
        {
            $( '#filterDataSetSection' ).append( "<option value='" + idx + "'>" + value.innerHTML + "</option>" );
        } );

        $( '#filterDataSetSectionDiv' ).show();
    }
    else
    {
        $( '#filterDataSetSectionDiv' ).hide();
    }
}

function filterOnSection()
{
    var $filterDataSetSection = $( '#filterDataSetSection' );
    var value = $filterDataSetSection.val();

    if ( value == 'all' )
    {
        $( '.formSection' ).show();
    }
    else
    {
        $( '.formSection' ).hide();
        $( $( '.formSection' )[value] ).show();
    }
}

function filterInSection( $this )
{
    var $tbody = $this.parent().parent().parent();
    var $trTarget = $tbody.find( 'tr:not([colspan])' );

    if ( $this.val() == '' )
    {
        $trTarget.show();
    }
    else
    {
        var $trTargetChildren = $trTarget.find( 'td:first-child' );

        $trTargetChildren.each( function( idx, item ) 
        {
            var text1 = $this.val().toUpperCase();
            var text2 = $( item ).find( 'span' ).html().toUpperCase();

            if ( text2.indexOf( text1 ) >= 0 )
            {
                $( item ).parent().show();
            }
            else
            {
                $( item ).parent().hide();
            }
        } );
    }

    refreshZebraStripes( $tbody );
}

//------------------------------------------------------------------------------
// Supportive methods
//------------------------------------------------------------------------------

/**
 * Splits an id based on the multi org unit variable.
 */
function splitFieldId( id )
{
    var split = {};

    if ( dhis2.de.multiOrganisationUnit )
    {
        split.organisationUnitId = id.split( '-' )[0];
        split.dataElementId = id.split( '-' )[1];
        split.optionComboId = id.split( '-' )[2];
    }
    else
    {
        split.organisationUnitId = getCurrentOrganisationUnit();
        split.dataElementId = id.split( '-' )[0];
        split.optionComboId = id.split( '-' )[1];
    }

    return split;
}

function refreshZebraStripes( $tbody )
{
    $tbody.find( 'tr:not([colspan]):visible:even' ).find( 'td:first-child' ).removeClass( 'reg alt' ).addClass( 'alt' );
    $tbody.find( 'tr:not([colspan]):visible:odd' ).find( 'td:first-child' ).removeClass( 'reg alt' ).addClass( 'reg' );
}

function getDataElementType( dataElementId )
{
	if ( dhis2.de.dataElements[dataElementId] != null )
	{
		return dhis2.de.dataElements[dataElementId];
	}

	log( 'Data element not present in data set, falling back to default type: ' + dataElementId );
	return DEFAULT_TYPE;
}

function getDataElementName( dataElementId )
{
	var span = $( '#' + dataElementId + '-dataelement' );

	if ( span != null )
	{
		return span.text();
	}

	log( 'Data element not present in form, falling back to default name: ' + dataElementId );
	return DEFAULT_NAME;
}

function getOptionComboName( optionComboId )
{
	var span = $( '#' + optionComboId + '-optioncombo' );

	if ( span != null )
	{
		return span.text();
	}

	log( 'Category option combo not present in form, falling back to default name: ' + optionComboId );
	return DEFAULT_NAME;
}

// ----------------------------------------------------------------------------
// OrganisationUnit Selection
// -----------------------------------------------------------------------------

function organisationUnitSelected( orgUnits, orgUnitNames, children )
{
	if ( dhis2.de.metaDataIsLoaded == false )
	{
	    return false;
	}

	dhis2.de.currentOrganisationUnitId = orgUnits[0];
    var organisationUnitName = orgUnitNames[0];

    $( '#selectedOrganisationUnit' ).val( organisationUnitName );
    $( '#currentOrganisationUnit' ).html( organisationUnitName );

    var dataSetList = getSortedDataSetList();

    $( '#selectedDataSetId' ).removeAttr( 'disabled' );

    var dataSetId = $( '#selectedDataSetId' ).val();
    var periodId = $( '#selectedPeriodId' ).val();

    clearListById( 'selectedDataSetId' );
    addOptionById( 'selectedDataSetId', '-1', '[ ' + i18n_select_data_set + ' ]' );

    var dataSetValid = false;
    var multiDataSetValid = false;

    $.safeEach( dataSetList, function( idx, item ) 
    {
        addOptionById( 'selectedDataSetId', item.id, item.name );

        if ( dataSetId == item.id )
        {
            dataSetValid = true;
        }
    } );

    if ( children )
    {
        var childrenDataSets = getSortedDataSetListForOrgUnits( children );

        if ( childrenDataSets && childrenDataSets.length > 0 )
        {
            $( '#selectedDataSetId' ).append( '<optgroup label="' + i18n_childrens_forms + '">' );

            $.safeEach( childrenDataSets, function( idx, item )
            {
                if ( dataSetId == item.id && dhis2.de.multiOrganisationUnit )
                {
                    multiDataSetValid = true;
                }

                $( '<option />' ).attr( 'data-multiorg', true ).attr( 'value', item.id).html(item.name).appendTo( '#selectedDataSetId' );
            } );

            $( '#selectDataSetId' ).append( '</optgroup>' );
        }
    }

    if ( !dhis2.de.multiOrganisationUnit && dataSetValid && dataSetId != null ) {
        $( '#selectedDataSetId' ).val( dataSetId ); // Restore selected data set

        if ( dhis2.de.inputSelected() && dhis2.de.dataEntryFormIsLoaded ) {
            resetSectionFilters();
            showLoader();
            loadDataValues();
        }
    } 
    else if ( dhis2.de.multiOrganisationUnit && multiDataSetValid && dataSetId != null ) {
        $( '#selectedDataSetId' ).val( dataSetId ); // Restore selected data set
        dataSetSelected();
    }
    else {
    	dhis2.de.multiOrganisationUnit = false;

        clearSectionFilters();
        clearPeriod();
        dhis2.de.clearAttributes();
    }
}

/**
 * Returns an array containing associative array elements with id and name
 * properties. The array is sorted on the element name property.
 */
function getSortedDataSetList( orgUnit )
{
    var associationSet = orgUnit !== undefined ? dhis2.de.organisationUnitAssociationSetMap[orgUnit] : dhis2.de.organisationUnitAssociationSetMap[getCurrentOrganisationUnit()];
    var orgUnitDataSets = dhis2.de.dataSetAssociationSets[associationSet];

    var dataSetList = [];

    $.safeEach( orgUnitDataSets, function( idx, item ) 
    {
        var dataSetId = orgUnitDataSets[idx];
        var dataSetName = dhis2.de.dataSets[dataSetId].name;

        var row = [];
        row['id'] = dataSetId;
        row['name'] = dataSetName;
        dataSetList[idx] = row;
    } );

    dataSetList.sort( function( a, b )
    {
        return a.name > b.name ? 1 : a.name < b.name ? -1 : 0;
    } );

    return dataSetList;
}

function getSortedDataSetListForOrgUnits( orgUnits )
{
    var dataSetList = [];

    $.safeEach( orgUnits, function( idx, item )
    {
        dataSetList.push.apply( dataSetList, getSortedDataSetList(item) )
    } );

    var filteredDataSetList = [];

    $.safeEach( dataSetList, function( idx, item ) 
    {
        var formType = dhis2.de.dataSets[item.id].type;
        var found = false;

        $.safeEach( filteredDataSetList, function( i, el ) 
        {
            if( item.name == el.name )
            {
                found = true;
            }
        } );

        if ( !found && ( formType == FORMTYPE_SECTION || formType == FORMTYPE_DEFAULT ) )
        {
            filteredDataSetList.push(item);
        }
    } );

    return filteredDataSetList;
}

// -----------------------------------------------------------------------------
// DataSet Selection
// -----------------------------------------------------------------------------

function dataSetSelected()
{
    $( '#selectedPeriodId' ).removeAttr( 'disabled' );
    $( '#prevButton' ).removeAttr( 'disabled' );
    $( '#nextButton' ).removeAttr( 'disabled' );

    var x = dhis2.de.currentDataSetId;
    
    var dataSetId = $( '#selectedDataSetId' ).val();
    var periodId = $( '#selectedPeriodId' ).val();

    var previousDataSetValid = ( dhis2.de.currentDataSetId && dhis2.de.currentDataSetId != -1 );    
    var previousPeriodType = previousDataSetValid ? dhis2.de.dataSets[dhis2.de.currentDataSetId].periodType : null;

    dhis2.de.currentDataSetId = dataSetId;
    
    if ( dataSetId && dataSetId != -1 )
    {
	    var periodType = dhis2.de.dataSets[dataSetId].periodType;
	    var allowFuturePeriods = dhis2.de.dataSets[dataSetId].allowFuturePeriods;
	    var periods = dhis2.de.periodTypeFactory.get( periodType ).generatePeriods( dhis2.de.currentPeriodOffset );
	    periods = dhis2.de.periodTypeFactory.reverse( periods );
	    
	    if ( allowFuturePeriods == false )
	    {
	    	periods = dhis2.de.periodTypeFactory.filterFuturePeriods( periods );
	    }

        clearListById( 'selectedPeriodId' );
        clearSectionFilters();

        dhis2.de.currentCategories = dhis2.de.getCategories( dataSetId );
        
        dhis2.de.multiOrganisationUnit = !!$( '#selectedDataSetId :selected' ).data( 'multiorg' );

        var attributeMarkup = dhis2.de.getAttributesMarkup();
        $( '#attributeComboDiv' ).html( attributeMarkup );

        if ( periods.length > 0 )
        {
        	addOptionById( 'selectedPeriodId', '-1', '[ ' + i18n_select_period + ' ]' );
        }
        else
        {
        	addOptionById( 'selectedPeriodId', '-1', i18n_no_periods_click_prev_year_button );
        }
        
        $.safeEach( periods, function( idx, item )
        {
            addOptionById( 'selectedPeriodId', item.iso, item.name );
        } );

        if ( dhis2.de.inputSelected() && previousPeriodType && previousPeriodType == periodType )
        {
            showLoader();            
            $( '#selectedPeriodId' ).val( periodId );
            loadForm();
        }
        else
        {
            clearEntryForm();
        }
    }
    else
    {
        clearEntryForm();
        dhis2.de.clearAttributes();
    }
}

// -----------------------------------------------------------------------------
// Period Selection
// -----------------------------------------------------------------------------

function periodSelected()
{
    var periodName = $( '#selectedPeriodId :selected' ).text();
    var dataSetId = $( '#selectedDataSetId' ).val();

    $( '#currentPeriod' ).html( periodName );

    var periodId = $( '#selectedPeriodId' ).val();
	
    if ( dhis2.de.inputSelected() )
    {    	
        showLoader();

        if ( dhis2.de.dataEntryFormIsLoaded )
        {
            loadDataValues();
        }
        else
        {
            loadForm();
        }
    }
}

function nextPeriodsSelected()
{
    if ( dhis2.de.currentPeriodOffset < 0 ) // Cannot display future periods
    {
    	dhis2.de.currentPeriodOffset++;
        displayPeriodsInternal();
    }
}

function previousPeriodsSelected()
{
	dhis2.de.currentPeriodOffset--;
    displayPeriodsInternal();
}

function displayPeriodsInternal()
{
    var dataSetId = $( '#selectedDataSetId' ).val();
    var periodType = dhis2.de.dataSets[dataSetId].periodType;
    var allowFuturePeriods = dhis2.de.dataSets[dataSetId].allowFuturePeriods;
    var periods = dhis2.de.periodTypeFactory.get( periodType ).generatePeriods( dhis2.de.currentPeriodOffset );
    periods = dhis2.de.periodTypeFactory.reverse( periods );
    
    if ( allowFuturePeriods == false )
    {
    	periods = dhis2.de.periodTypeFactory.filterFuturePeriods( periods );
    }

    clearListById( 'selectedPeriodId' );

    if ( periods.length > 0 )
    {
    	addOptionById( 'selectedPeriodId', '-1', '[ ' + i18n_select_period + ' ]' );
    }
    else
    {
    	addOptionById( 'selectedPeriodId', '-1', i18n_no_periods_click_prev_year_button );
    }
    
    $.safeEach( periods, function( idx, item ) 
    {
        addOptionById( 'selectedPeriodId', item.iso, item.name );
    } );
}

//------------------------------------------------------------------------------
// Attributes / Categories Selection
//------------------------------------------------------------------------------

/**
* Returns an array of category objects for the given data set identifier. Categories 
* are looked up using the category combo of the data set. Null is returned if 
* the given data set has the default category combo.
*/
dhis2.de.getCategories = function( dataSetId )
{
	var dataSet = dhis2.de.dataSets[dataSetId];
	
	if ( !dataSet || !dataSet.categoryCombo || dhis2.de.defaultCategoryCombo === dataSet.categoryCombo ) {
		return null;
	}
	
	var categoryCombo = dhis2.de.categoryCombos[dataSet.categoryCombo];
	
	var categories = [];
	
	$.safeEach( categoryCombo.categories, function( idx, cat ) {
		var category = dhis2.de.categories[cat];
		categories.push( category );
	} );
	
	return categories;
};

/**
 * Indicates whether all present categories have been selected. True is returned
 * if no categories are present. False is returned if less selections have been
 * made thant here are categories present.
 */
dhis2.de.categoriesSelected = function()
{
	if ( !dhis2.de.currentCategories || dhis2.de.currentCategories.length == 0 ) {
		return true; // No categories present which can be selected
	}
	
	var options = dhis2.de.getCurrentCategoryOptions();
	
	if ( !options || options.length < dhis2.de.currentCategories.length ) {
		return false; // Less selected options than categories present
	}
	
	return true;
};

/**
* Returns attribute category combo identifier. Based on the dhis2.de.currentDataSetId 
* global variable. Returns null if there is no current data set or if current 
* data set has the default category combo.
*/
dhis2.de.getCurrentCategoryCombo = function()
{
	var dataSet = dhis2.de.dataSets[dhis2.de.currentDataSetId];
	
	if ( !dataSet || !dataSet.categoryCombo || dhis2.de.defaultCategoryCombo === dataSet.categoryCombo ) {
		return null;
	}
	
	return dataSet.categoryCombo;
};

/**
* Returns an array of the currently selected attribute category option identifiers. 
* Based on the dhis2.de.currentCategories global variable. Returns null if there 
* are no current categories.
*/
dhis2.de.getCurrentCategoryOptions = function()
{
	if ( !dhis2.de.currentCategories || dhis2.de.currentCategories.length == 0 ) {
		return null;
	}
	
	var options = [];
	
	$.safeEach( dhis2.de.currentCategories, function( idx, category ) {
		var option = $( '#category-' + category.id ).val();
		
		if ( option && option != -1 ) {
			options.push( option );
		}
	} );
	
	return options;
};

/**
 * Returns a query param value for the currently selected category options where
 * each option is separated by the ; character.
 */
dhis2.de.getCurrentCategoryOptionsQueryValue = function()
{
	if ( !dhis2.de.getCurrentCategoryOptions() ) {
		return null;
	}
	
	var value = '';
	
	$.safeEach( dhis2.de.getCurrentCategoryOptions(), function( idx, option ) {
		value += option + ';';
	} );
	
	if ( value ) {
		value = value.slice( 0, -1 );
	}
	
	return value;
}

/**
* Returns markup for drop down boxes to be put in the selection box for the
* given categories. The empty string is returned if no categories are given. 
*/
dhis2.de.getAttributesMarkup = function()
{
	var html = '';
	
	if ( !dhis2.de.currentCategories || dhis2.de.currentCategories.length == 0 ) {
		return html;
	}
	
	$.safeEach( dhis2.de.currentCategories, function( idx, category ) {
		html += '<div class="selectionBoxRow">';
		html += '<div class="selectionLabel">' + category.name + '</div>&nbsp;';
		html += '<select id="category-' + category.id + '" class="selectionBoxSelect" onchange="dhis2.de.attributeSelected(\'' + category.id + '\')">';
		html += '<option value="-1">[ ' + i18n_select_option + ' ]</option>';
		
		$.safeEach( category.options, function( idx, option ) {
			html += '<option value="' + option.id + '">' + option.name + '</option>';
		} );
		
		html += '</select>';
		html += '</div>';
	} );

	return html;
};

/**
 * Clears the markup for attribute select lists.
 */
dhis2.de.clearAttributes = function()
{
	$( '#attributeComboDiv' ).html( '' );
};

/**
 * Callback for changes in attribute select lists.
 */
dhis2.de.attributeSelected = function( categoryId )
{
	if ( dhis2.de.inputSelected() ) {    	
        showLoader();

        if ( dhis2.de.dataEntryFormIsLoaded ) {
            loadDataValues();
        }
        else {
            loadForm();
        }
    }
};

// -----------------------------------------------------------------------------
// Form
// -----------------------------------------------------------------------------

/**
 * Indicates whether all required inpout selections have been made.
 */
dhis2.de.inputSelected = function()
{
    var dataSetId = $( '#selectedDataSetId' ).val();
    var periodId = $( '#selectedPeriodId' ).val();

	if (
	    dhis2.de.currentOrganisationUnitId &&
	    dataSetId && dataSetId != -1 &&
	    periodId && periodId != -1 &&
	    dhis2.de.categoriesSelected() ) {
		return true;
	}

	return false;
};

function loadDataValues()
{
    $( '#completeButton' ).removeAttr( 'disabled' );
    $( '#undoButton' ).attr( 'disabled', 'disabled' );
    $( '#infoDiv' ).css( 'display', 'none' );

    dhis2.de.currentOrganisationUnitId = selection.getSelected()[0];

    getAndInsertDataValues();
    displayEntryFormCompleted();
}

function getAndInsertDataValues()
{
    var periodId = $( '#selectedPeriodId' ).val();
    var dataSetId = $( '#selectedDataSetId' ).val();

    // Clear existing values and colors, grey disabled fields

    $( '.entryfield' ).val( '' );
    $( '.entryselect' ).val( '' );
    $( '.entrytrueonly' ).removeAttr( 'checked' );
    $( '.entryoptionset' ).val( '' );

    $( '.entryfield' ).css( 'background-color', COLOR_WHITE ).css( 'border', '1px solid ' + COLOR_BORDER );
    $( '.entryselect' ).css( 'background-color', COLOR_WHITE ).css( 'border', '1px solid ' + COLOR_BORDER );
    $( '.indicator' ).css( 'background-color', COLOR_WHITE ).css( 'border', '1px solid ' + COLOR_BORDER );
    $( '.entrytrueonly' ).css( 'background-color', COLOR_WHITE );
    $( '.entryoptionset' ).css( 'background-color', COLOR_WHITE );

    $( '[name="min"]' ).html( '' );
    $( '[name="max"]' ).html( '' );

    $( '.entryfield' ).filter( ':disabled' ).css( 'background-color', COLOR_GREY );
    
    var params = {
		periodId : periodId,
        dataSetId : dataSetId,
        organisationUnitId : getCurrentOrganisationUnit(),
        multiOrganisationUnit: dhis2.de.multiOrganisationUnit
    };

    var cc = dhis2.de.getCurrentCategoryCombo();
    var cp = dhis2.de.getCurrentCategoryOptionsQueryValue();
    
    if ( cc && cp )
    {
    	params.cc = cc;
    	params.cp = cp;
    }
    
    $.ajax( {
    	url: 'getDataValues.action',
    	data: params,
	    dataType: 'json',
	    error: function() // offline
	    {
	    	$( '#completenessDiv' ).show();
	    	$( '#infoDiv' ).hide();
	    	
	    	var json = getOfflineDataValueJson( params );
	    	
	    	insertDataValues( json );
	    },
	    success: function( json ) // online
	    {
	    	insertDataValues( json );
        },
        complete: function()
        {
	    	$( '.indicator' ).attr( 'readonly', 'readonly' );	    	
        $( document ).trigger('dhis2.de.event.dataValuesLoaded');
        }
	} );
}

function getOfflineDataValueJson( params )
{
	var dataValues = dhis2.de.storageManager.getDataValuesInForm( params );
	var complete = dhis2.de.storageManager.hasCompleteDataSet( params );
	
	var json = {};
	json.dataValues = new Array();
	json.locked = false;
	json.complete = complete;
	json.date = "";
	json.storedBy = "";
		
	for ( var i = 0; i < dataValues.length; i++ )
	{
		var dataValue = dataValues[i];
		
		json.dataValues.push( { 
			'id': dataValue.de + '-' + dataValue.co,
			'val': dataValue.value
		} );
	}
	
	return json;
}

function insertDataValues( json )
{
    var dataValueMap = []; // Reset
    dhis2.de.currentMinMaxValueMap = []; // Reset
    
	if ( json.locked )
	{
        $( '#contentDiv input').attr( 'readonly', 'readonly' );
        $( '.entryoptionset').autocomplete( 'disable' );
        $( '.sectionFilter').removeAttr( 'disabled' );
        $( '#completenessDiv' ).hide();
		setHeaderDelayMessage( i18n_dataset_is_locked );
	}
	else
	{
        $( '.entryoptionset' ).autocomplete( 'enable' );
        $( '#contentDiv input' ).removeAttr( 'readonly' );
		$( '#completenessDiv' ).show();
	}
	
    // Set data values, works for selects too as data value=select value

    $.safeEach( json.dataValues, function( i, value )
    {
        var fieldId = '#' + value.id + '-val';
        var commentId = '#' + value.id + '-comment';

        if ( $( fieldId ).length > 0 ) // Set values
        {
            if ( $( fieldId ).attr( 'name' ) == 'entrytrueonly' && 'true' == value.val ) 
            {
                $( fieldId ).attr( 'checked', true );
            } 
            else 
            {
                $( fieldId ).val( value.val );
            }
        }
        
        if ( 'true' == value.com ) // Set active comments
        {
            if ( $( commentId ).length > 0 )
            {
                $( commentId ).attr( 'src', '../images/comment_active.png' );
            }
            else if ( $( fieldId ).length > 0 )
            {
                $( fieldId ).css( 'border-color', COLOR_BORDER_ACTIVE )
            }	            		
        }
        
        dataValueMap[value.id] = value.val;
    } );

    // Set min-max values and colorize violation fields

    if ( !json.locked ) 
    {
        $.safeEach( json.minMaxDataElements, function( i, value )
        {
            var minId = value.id + '-min';
            var maxId = value.id + '-max';

            var valFieldId = '#' + value.id + '-val';

            var dataValue = dataValueMap[value.id];

            if ( dataValue && ( ( value.min && new Number( dataValue ) < new Number(
                value.min ) ) || ( value.max && new Number( dataValue ) > new Number( value.max ) ) ) )
            {
                $( valFieldId ).css( 'background-color', COLOR_ORANGE );
            }

            dhis2.de.currentMinMaxValueMap[minId] = value.min;
            dhis2.de.currentMinMaxValueMap[maxId] = value.max;
        } );
    }

    // Update indicator values in form

    updateIndicators();
    updateDataElementTotals();

    // Set completeness button

    if ( json.complete && !json.locked)
    {
        $( '#completeButton' ).attr( 'disabled', 'disabled' );
        $( '#undoButton' ).removeAttr( 'disabled' );

        if ( json.storedBy )
        {
            $( '#infoDiv' ).show();
            $( '#completedBy' ).html( json.storedBy );
            $( '#completedDate' ).html( json.date );

            dhis2.de.currentCompletedByUser = json.storedBy;
        }
    }
    else
    {
        $( '#completeButton' ).removeAttr( 'disabled' );
        $( '#undoButton' ).attr( 'disabled', 'disabled' );
        $( '#infoDiv' ).hide();
    }

    if ( json.locked ) 
    {
        $( '#contentDiv input' ).css( 'backgroundColor', '#eee' );
        $( '.sectionFilter' ).css( 'backgroundColor', '#fff' );
    }
}

function displayEntryFormCompleted()
{
    addEventListeners();

    $( '#validationButton' ).removeAttr( 'disabled' );
    $( '#validateButton' ).removeAttr( 'disabled' );

    dhis2.de.dataEntryFormIsLoaded = true;
    hideLoader();
    
    $( 'body' ).trigger( EVENT_FORM_LOADED );
}

function valueFocus( e )
{
    var id = e.target.id;

    var split = splitFieldId( id );
    var dataElementId = split.dataElementId;
    var optionComboId = split.optionComboId;
    dhis2.de.currentOrganisationUnitId = split.organisationUnitId;

    var dataElementName = getDataElementName( dataElementId );
    var optionComboName = getOptionComboName( optionComboId );
    var organisationUnitName = organisationUnits[getCurrentOrganisationUnit()].n;

    $( '#currentOrganisationUnit' ).html( organisationUnitName );
    $( '#currentDataElement' ).html( dataElementName + ' ' + optionComboName );

    $( '#' + dataElementId + '-cell' ).addClass( 'currentRow' );
}

function valueBlur( e )
{
    var id = e.target.id;

    var split = splitFieldId( id );
    var dataElementId = split.dataElementId;

    $( '#' + dataElementId + '-cell' ).removeClass( 'currentRow' );
}

function keyPress( event, field )
{
    var key = event.keyCode || event.charCode || event.which;

    var focusField = ( key == 13 || key == 40 ) ? getNextEntryField( field )
            : ( key == 38 ) ? getPreviousEntryField( field ) : false;

    if ( focusField )
    {
        focusField.focus();
    }
}

function getNextEntryField( field )
{
    var index = field.getAttribute( 'tabindex' );

    field = $( 'input[name="entryfield"][tabindex="' + ( ++index ) + '"]' );

    while ( field )
    {
        if ( field.is( ':disabled' ) || field.is( ':hidden' ) )
        {
            field = $( 'input[name="entryfield"][tabindex="' + ( ++index ) + '"]' );
        }
        else
        {
            return field;
        }
    }
}

function getPreviousEntryField( field )
{
    var index = field.getAttribute( 'tabindex' );

    field = $( 'input[name="entryfield"][tabindex="' + ( --index ) + '"]' );

    while ( field )
    {
        if ( field.is( ':disabled' ) || field.is( ':hidden' ) )
        {
            field = $( 'input[name="entryfield"][tabindex="' + ( --index ) + '"]' );
        }
        else
        {
            return field;
        }
    }
}

/**
 * Convenience method which can be used in custom form scripts. Do not change.
 */
function onFormLoad( fn )
{
	$( 'body' ).off( EVENT_FORM_LOADED ).on( EVENT_FORM_LOADED, fn );
}

// -----------------------------------------------------------------------------
// Data completeness
// -----------------------------------------------------------------------------

function registerCompleteDataSet()
{
	if ( !confirm( i18n_confirm_complete ) )
	{
		return false;
	}
	
	validate( true, function() {	
	    var params = dhis2.de.storageManager.getCurrentCompleteDataSetParams();
        params.organisationUnitId = getCurrentOrganisationUnit();
        params.multiOrganisationUnit = dhis2.de.multiOrganisationUnit;

        var cc = dhis2.de.getCurrentCategoryCombo();
        var cp = dhis2.de.getCurrentCategoryOptionsQueryValue();
        
        if ( cc && cp )
        {
        	params.cc = cc;
        	params.cp = cp;
        }
        
        dhis2.de.storageManager.saveCompleteDataSet( params );
	
	    $.ajax( {
	    	url: 'registerCompleteDataSet.action',
	    	data: params,
	        dataType: 'json',
	    	success: function(data)
	        {
	            if ( data.status == 2 )
	            {
	                log( 'Data set is locked' );
	                setHeaderMessage( i18n_register_complete_failed_dataset_is_locked );
	            }
	            else
	            {
	                disableCompleteButton();
	
	                dhis2.de.storageManager.clearCompleteDataSet( params );
	            }
	        },
		    error: function()
		    {
		    	disableCompleteButton();
		    }
	    } );
	} );
}

function undoCompleteDataSet()
{
	if ( !confirm( i18n_confirm_undo ) )
	{
		return false;
	}
	
    var params = dhis2.de.storageManager.getCurrentCompleteDataSetParams();
    params.organisationUnitId = getCurrentOrganisationUnit();
    params.multiOrganisationUnit = dhis2.de.multiOrganisationUnit;

    var cc = dhis2.de.getCurrentCategoryCombo();
    var cp = dhis2.de.getCurrentCategoryOptionsQueryValue();
    
    if ( cc && cp )
    {
    	params.cc = cc;
    	params.cp = cp;
    }
    
    $.ajax( {
    	url: 'undoCompleteDataSet.action',
    	data: params,
    	dataType: 'json',
    	success: function(data)
        {
            if ( data.status == 2 )
            {
                log( 'Data set is locked' );
                setHeaderMessage( i18n_unregister_complete_failed_dataset_is_locked );
            }
            else
            {
                disableUndoButton();
                dhis2.de.storageManager.clearCompleteDataSet( params );
            }

        },
        error: function()
        {
        	dhis2.de.storageManager.clearCompleteDataSet( params );
        }
    } );
}

function disableUndoButton()
{
    $( '#completeButton' ).removeAttr( 'disabled' );
    $( '#undoButton' ).attr( 'disabled', 'disabled' );
}

function disableCompleteButton()
{
    $( '#completeButton' ).attr( 'disabled', 'disabled' );
    $( '#undoButton' ).removeAttr( 'disabled' );
}

function displayUserDetails()
{
	if ( dhis2.de.currentCompletedByUser )
	{
		var url = '../dhis-web-commons-ajax-json/getUser.action';

		$.getJSON( url, { username: dhis2.de.currentCompletedByUser }, function( json ) 
		{
			$( '#userFullName' ).html( json.user.firstName + ' ' + json.user.surname );
			$( '#userUsername' ).html( json.user.username );
			$( '#userEmail' ).html( json.user.email );
			$( '#userPhoneNumber' ).html( json.user.phoneNumber );
			$( '#userOrganisationUnits' ).html( joinNameableObjects( json.user.organisationUnits ) );
			$( '#userUserRoles' ).html( joinNameableObjects( json.user.roles ) );

			$( '#completedByDiv' ).dialog( {
	        	modal : true,
	        	width : 350,
	        	height : 350,
	        	title : 'User'
	    	} );
		} );
	}
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function displayValidationDialog( data, height )
{
	height = isDefined( height ) ? height : 500;
	
	$( '#validationDiv' ).html( data );
	
    $( '#validationDiv' ).dialog( {
        modal: true,
        title: 'Validation',
        width: 920,
        height: height
    } );
}

function validate( ignoreSuccessfulValidation, successCallback )
{
	var compulsoryCombinationsValid = validateCompulsoryCombinations();
	
	// Check for compulsory combinations and return false if violated
	
	if ( !compulsoryCombinationsValid )
	{
    	var html = '<h3>' + i18n_validation_result + ' &nbsp;<img src="../images/warning_small.png"></h3>' +
        	'<p class="bold">' + i18n_all_values_for_data_element_must_be_filled + '</p>';
		
    	displayValidationDialog( html, 300 );
	
		return false;
	}

	// Check for validation rules and whether complete is only allowed if valid
	
	var successHtml = '<h3>' + i18n_validation_result + ' &nbsp;<img src="../images/success_small.png"></h3>' +
		'<p class="bold">' + i18n_successful_validation + '</p>';

	var validCompleteOnly = dhis2.de.dataSets[dhis2.de.currentDataSetId].validCompleteOnly;

    var params = dhis2.de.storageManager.getCurrentCompleteDataSetParams();
	params['organisationUnitId'] = getCurrentOrganisationUnit();
    params['multiOrganisationUnit'] = dhis2.de.multiOrganisationUnit;

    $( '#validationDiv' ).load( 'validate.action', params, function( response, status, xhr ) {
    	var success = null;
    	
        if ( status == 'error' && !ignoreSuccessfulValidation )
        {
            window.alert( i18n_operation_not_available_offline );
            success = true;  // Accept if offline
        }
        else
        {
        	var hasViolations = isDefined( response ) && $.trim( response ).length > 0;
        	var success = !( hasViolations && validCompleteOnly );
        	
        	if ( hasViolations )
        	{
        		displayValidationDialog( response, 500 );
        	}
        	else if ( !ignoreSuccessfulValidation )
        	{
        		displayValidationDialog( successHtml, 200 );
        	}        	
        }
        
        if ( success && $.isFunction( successCallback ) )
        {
        	successCallback.call();
        }
    } );
}

function validateCompulsoryCombinations()
{
	var fieldCombinationRequired = dhis2.de.dataSets[dhis2.de.currentDataSetId].fieldCombinationRequired;
	
    if ( fieldCombinationRequired )
    {
        var violations = false;

        $( '.entryfield' ).add( '[name="entryselect"]' ).each( function( i )
        {
            var id = $( this ).attr( 'id' );

            var split = splitFieldId( id );
            var dataElementId = split.dataElementId;
            var hasValue = $.trim( $( this ).val() ).length > 0;
            
            if ( hasValue )
            {
            	$selector = $( '[name="entryfield"][id^="' + dataElementId + '-"]' ).
            		add( '[name="entryselect"][id^="' + dataElementId + '-"]' );
				
                $selector.each( function( i )
                {
                    if ( $.trim( $( this ).val() ).length == 0 )
                    {
                        violations = true;						
                        $selector.css( 'background-color', COLOR_RED );						
                        return false;
                    }
                } );
            }
        } );
		
        if ( violations )
        {
            return false;
        }
    }
	
	return true;
}

// -----------------------------------------------------------------------------
// History
// -----------------------------------------------------------------------------

function displayHistoryDialog( operandName )
{
    $( '#historyDiv' ).dialog( {
        modal: true,
        title: operandName,
        width: 580,
        height: 660
    } );
}

function viewHist( dataElementId, optionComboId )
{
    var periodId = $( '#selectedPeriodId' ).val();

	if ( dataElementId && optionComboId && periodId && periodId != -1 )
	{
	    var dataElementName = getDataElementName( dataElementId );
	    var optionComboName = getOptionComboName( optionComboId );
	    var operandName = dataElementName + ' ' + optionComboName;
	
	    var params = {
    		dataElementId : dataElementId,
	        optionComboId : optionComboId,
	        periodId : periodId,
	        organisationUnitId : getCurrentOrganisationUnit()
	    };

	    var cc = dhis2.de.getCurrentCategoryCombo();
	    var cp = dhis2.de.getCurrentCategoryOptionsQueryValue();
	    
	    if ( cc && cp )
	    {
	    	params.cc = cc;
	    	params.cp = cp;
	    }
	    
	    $( '#historyDiv' ).load( 'viewHistory.action', params, 
	    function( response, status, xhr )
	    {
	        if ( status == 'error' )
	        {
	            window.alert( i18n_operation_not_available_offline );
	        }
	        else
	        {
	            displayHistoryDialog( operandName );
	        }
	    } );
	}
}

function closeCurrentSelection()
{
    $( '#currentSelection' ).fadeOut();
}

// -----------------------------------------------------------------------------
// Local storage of forms
// -----------------------------------------------------------------------------

function updateForms()
{
    purgeLocalForms();
    updateExistingLocalForms();
    downloadRemoteForms();

    DAO.store.open().done( function() {
        loadOptionSets();
    });
}

function purgeLocalForms()
{
    var formIds = dhis2.de.storageManager.getAllForms();

    $.safeEach( formIds, function( idx, item ) 
    {
        if ( dhis2.de.dataSets[item] == null )
        {
        	dhis2.de.storageManager.deleteForm( item );
        	dhis2.de.storageManager.deleteFormVersion( item );
            log( 'Deleted locally stored form: ' + item );
        }
    } );

    log( 'Purged local forms' );
}

function updateExistingLocalForms()
{
    var formIds = dhis2.de.storageManager.getAllForms();
    var formVersions = dhis2.de.storageManager.getAllFormVersions();

    $.safeEach( formIds, function( idx, item ) 
    {
        var remoteVersion = dhis2.de.dataSets[item].version;
        var localVersion = formVersions[item];

        if ( remoteVersion == null || localVersion == null || remoteVersion != localVersion )
        {
        	dhis2.de.storageManager.downloadForm( item, remoteVersion );
        }
    } );
}

function downloadRemoteForms()
{
    $.safeEach( dhis2.de.dataSets, function( idx, item ) 
    {
        var remoteVersion = item.version;

        if ( !dhis2.de.storageManager.formExists( idx ) && !item.skipOffline )
        {
        	dhis2.de.storageManager.downloadForm( idx, remoteVersion );
        }
    } );
}

// TODO break if local storage is full

// -----------------------------------------------------------------------------
// StorageManager
// -----------------------------------------------------------------------------

/**
 * This object provides utility methods for localStorage and manages data entry
 * forms and data values.
 */
function StorageManager()
{
    var MAX_SIZE = new Number( 2600000 );
    var MAX_SIZE_FORMS = new Number( 1600000 );

    var KEY_FORM_PREFIX = 'form-';
    var KEY_FORM_VERSIONS = 'formversions';
    var KEY_DATAVALUES = 'datavalues';
    var KEY_COMPLETEDATASETS = 'completedatasets';

    /**
     * Returns the total number of characters currently in the local storage.
     *
     * @return number of characters.
     */
    this.totalSize = function()
    {
        var totalSize = new Number();

        for ( var i = 0; i < localStorage.length; i++ )
        {
            var value = localStorage.key( i );

            if ( value )
            {
                totalSize += value.length;
            }
        }

        return totalSize;
    };

    /**
     * Returns the total numbers of characters in stored forms currently in the
     * local storage.
     *
     * @return number of characters.
     */
    this.totalFormSize = function()
    {
        var totalSize = new Number();

        for ( var i = 0; i < localStorage.length; i++ )
        {
            if ( localStorage.key( i ).substring( 0, KEY_FORM_PREFIX.length ) == KEY_FORM_PREFIX )
            {
                var value = localStorage.key( i );

                if ( value )
                {
                    totalSize += value.length;
                }
            }
        }

        return totalSize;
    };

    /**
     * Return the remaining capacity of the local storage in characters, ie. the
     * maximum size minus the current size.
     */
    this.remainingStorage = function()
    {
        return MAX_SIZE - this.totalSize();
    };

    /**
     * Saves the content of a data entry form.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @param html the form HTML content.
     * @return true if the form saved successfully, false otherwise.
     */
    this.saveForm = function( dataSetId, html )
    {
        var id = KEY_FORM_PREFIX + dataSetId;

        try
        {
            localStorage[id] = html;

            log( 'Successfully stored form: ' + dataSetId );
        } 
        catch ( e )
        {
            log( 'Max local storage quota reached, ignored form: ' + dataSetId );
            return false;
        }

        if ( MAX_SIZE_FORMS < this.totalFormSize() )
        {
            this.deleteForm( dataSetId );

            log( 'Max local storage quota for forms reached, ignored form: ' + dataSetId );
            return false;
        }

        return true;
    };

    /**
     * Gets the content of a data entry form.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @return the content of a data entry form.
     */
    this.getForm = function( dataSetId )
    {
        var id = KEY_FORM_PREFIX + dataSetId;

        return localStorage[id];
    };

    /**
     * Removes a form.
     *
     * @param dataSetId the identifier of the data set of the form.
     */
    this.deleteForm = function( dataSetId )
    {
    	var id = KEY_FORM_PREFIX + dataSetId;

        localStorage.removeItem( id );
    };

    /**
     * Returns an array of the identifiers of all forms.
     *
     * @return array with form identifiers.
     */
    this.getAllForms = function()
    {
        var formIds = [];

        var formIndex = 0;

        for ( var i = 0; i < localStorage.length; i++ )
        {
            var key = localStorage.key( i );

            if ( key.substring( 0, KEY_FORM_PREFIX.length ) == KEY_FORM_PREFIX )
            {
                var id = key.split( '-' )[1];

                formIds[formIndex++] = id;
            }
        }

        return formIds;
    };

    /**
     * Indicates whether a form exists.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @return true if a form exists, false otherwise.
     */
    this.formExists = function( dataSetId )
    {
        var id = KEY_FORM_PREFIX + dataSetId;

        return localStorage[id] != null;
    };

    /**
     * Downloads the form for the data set with the given identifier from the
     * remote server and saves the form locally. Potential existing forms with
     * the same identifier will be overwritten. Updates the form version.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @param formVersion the version of the form of the remote data set.
     */
    this.downloadForm = function( dataSetId, formVersion )
    {
        $.ajax( {
            url: 'loadForm.action',
            data:
            {
                dataSetId : dataSetId
            },
            dataSetId: dataSetId,
            formVersion: formVersion,
            dataType: 'text',
            success: function( data, textStatus, jqXHR )
            {
            	dhis2.de.storageManager.saveForm( this.dataSetId, data ); //TODO
            	dhis2.de.storageManager.saveFormVersion( this.dataSetId, this.formVersion );
            }
        } );
    };

    /**
     * Saves a version for a form.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @param formVersion the version of the form.
     */
    this.saveFormVersion = function( dataSetId, formVersion )
    {
        var formVersions = {};

        if ( localStorage[KEY_FORM_VERSIONS] != null )
        {
            formVersions = JSON.parse( localStorage[KEY_FORM_VERSIONS] );
        }

        formVersions[dataSetId] = formVersion;

        try
        {
            localStorage[KEY_FORM_VERSIONS] = JSON.stringify( formVersions );

            log( 'Successfully stored form version: ' + dataSetId );
        } 
        catch ( e )
        {
            log( 'Max local storage quota reached, ignored form version: ' + dataSetId );
        }
    };

    /**
     * Returns the version of the form of the data set with the given
     * identifier.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @return the form version.
     */
    this.getFormVersion = function( dataSetId )
    {
        if ( localStorage[KEY_FORM_VERSIONS] != null )
        {
            var formVersions = JSON.parse( localStorage[KEY_FORM_VERSIONS] );

            return formVersions[dataSetId];
        }

        return null;
    };

    /**
     * Deletes the form version of the data set with the given identifier.
     *
     * @param dataSetId the identifier of the data set of the form.
     */
    this.deleteFormVersion = function( dataSetId )
    {
    	if ( localStorage[KEY_FORM_VERSIONS] != null )
        {
            var formVersions = JSON.parse( localStorage[KEY_FORM_VERSIONS] );

            if ( formVersions[dataSetId] != null )
            {
                delete formVersions[dataSetId];
                localStorage[KEY_FORM_VERSIONS] = JSON.stringify( formVersions );
            }
        }
    };

    this.getAllFormVersions = function()
    {
        return localStorage[KEY_FORM_VERSIONS] != null ? JSON.parse( localStorage[KEY_FORM_VERSIONS] ) : null;
    };

    /**
     * Saves a data value.
     *
     * @param dataValue The datavalue and identifiers in json format.
     */
    this.saveDataValue = function( dataValue )
    {
        var id = this.getDataValueIdentifier( dataValue.de, 
        		dataValue.co, dataValue.pe, dataValue.ou );

        var dataValues = {};

        if ( localStorage[KEY_DATAVALUES] != null )
        {
            dataValues = JSON.parse( localStorage[KEY_DATAVALUES] );
        }

        dataValues[id] = dataValue;

        try
        {
            localStorage[KEY_DATAVALUES] = JSON.stringify( dataValues );

            log( 'Successfully stored data value' );
        } 
        catch ( e )
        {
            log( 'Max local storage quota reached, not storing data value locally' );
        }
    };

    /**
     * Gets the value for the data value with the given arguments, or null if it
     * does not exist.
     *
     * @param de the data element identifier.
     * @param co the category option combo identifier.
     * @param pe the period identifier.
     * @param ou the organisation unit identifier.
     * @return the value for the data value with the given arguments, null if
     *         non-existing.
     */
    this.getDataValue = function( de, co, pe, ou )
    {
        var id = this.getDataValueIdentifier( de, co, pe, ou );

        if ( localStorage[KEY_DATAVALUES] != null )
        {
            var dataValues = JSON.parse( localStorage[KEY_DATAVALUES] );

            return dataValues[id];
        }

        return null;
    };
    
    /**
     * Returns the data values for the given period and organisation unit 
     * identifiers as an array.
     * 
     * @param json object with periodId and organisationUnitId properties.
     */
    this.getDataValuesInForm = function( json )
    {
    	var dataValues = this.getDataValuesAsArray();
    	var valuesInForm = new Array();
    	
		for ( var i = 0; i < dataValues.length; i++ )
		{
			var val = dataValues[i];
			
			if ( val.pe == json.periodId && val.ou == json.organisationUnitId )
			{
				valuesInForm.push( val );
			}
		}
    	
    	return valuesInForm;
    }

    /**
     * Removes the given dataValue from localStorage.
     *
     * @param dataValue The datavalue and identifiers in json format.
     */
    this.clearDataValueJSON = function( dataValue )
    {
        this.clearDataValue( dataValue.de, dataValue.co, dataValue.pe,
                dataValue.ou );
    };

    /**
     * Removes the given dataValue from localStorage.
     *
     * @param de the data element identifier.
     * @param co the category option combo identifier.
     * @param pe the period identifier.
     * @param ou the organisation unit identifier.
     */
    this.clearDataValue = function( de, co, pe, ou )
    {
        var id = this.getDataValueIdentifier( de, co, pe, ou );
        var dataValues = this.getAllDataValues();

        if ( dataValues != null && dataValues[id] != null )
        {
            delete dataValues[id];
            localStorage[KEY_DATAVALUES] = JSON.stringify( dataValues );
        }
    };

    /**
     * Returns a JSON associative array where the keys are on the form <data
     * element id>-<category option combo id>-<period id>-<organisation unit
     * id> and the data values are the values.
     *
     * @return a JSON associative array.
     */
    this.getAllDataValues = function()
    {
        return localStorage[KEY_DATAVALUES] != null ? JSON.parse( localStorage[KEY_DATAVALUES] ) : null;
    };
    
    /**
     * Returns all data value objects in an array. Returns an empty array if no
     * data values exist. Items in array are guaranteed not to be undefined.
     */
    this.getDataValuesAsArray = function()
    {
    	var values = new Array();
    	var dataValues = this.getAllDataValues();
    	
    	if ( undefined === dataValues )
    	{
    		return values;
    	}
    	
    	for ( i in dataValues )
    	{
    		if ( dataValues.hasOwnProperty( i ) && undefined !== dataValues[i] )
    		{
    			values.push( dataValues[i] );
    		}
    	}
    	
    	return values;
    }

    /**
     * Generates an identifier.
     */
    this.getDataValueIdentifier = function( de, co, pe, ou )
    {
        return de + '-' + co + '-' + pe + '-' + ou;
    };

    /**
     * Generates an identifier.
     */
    this.getCompleteDataSetId = function( json )
    {
        return json.periodId + '-' + json.dataSetId + '-' + json.organisationUnitId;
    };

    /**
     * Returns current state in data entry form as associative array.
     *
     * @return an associative array.
     */
    this.getCurrentCompleteDataSetParams = function()
    {
        var params = {
            'periodId' : $( '#selectedPeriodId' ).val(),
            'dataSetId' : $( '#selectedDataSetId' ).val(),
            'organisationUnitId' : getCurrentOrganisationUnit()
        };

        return params;
    };

    /**
     * Gets all complete data set registrations as JSON.
     *
     * @return all complete data set registrations as JSON.
     */
    this.getCompleteDataSets = function()
    {
        if ( localStorage[KEY_COMPLETEDATASETS] != null )
        {
            return JSON.parse( localStorage[KEY_COMPLETEDATASETS] );
        }

        return null;
    };

    /**
     * Saves a complete data set registration.
     *
     * @param json the complete data set registration as JSON.
     */
    this.saveCompleteDataSet = function( json )
    {
        var completeDataSets = this.getCompleteDataSets();
        var completeDataSetId = this.getCompleteDataSetId( json );

        if ( completeDataSets != null )
        {
            completeDataSets[completeDataSetId] = json;
        }
        else
        {
            completeDataSets = {};
            completeDataSets[completeDataSetId] = json;
        }

        try
        {
        	localStorage[KEY_COMPLETEDATASETS] = JSON.stringify( completeDataSets );
        	
        	log( 'Successfully stored complete registration' );
        }
        catch ( e )
        {
        	log( 'Max local storage quota reached, not storing complete registration locally' );
        }
    };
    
    /**
     * Indicates whether a complete data set registration exists for the given
     * argument.
     * 
     * @param json object with periodId, dataSetId, organisationUnitId properties.
     */
    this.hasCompleteDataSet = function( json )
    {
    	var id = this.getCompleteDataSetId( json );
    	var registrations = this.getCompleteDataSets();
    	
        if ( null != registrations && undefined !== registrations && undefined !== registrations[id] )
        {
            return true;
        }
    	
    	return false;    	
    }

    /**
     * Removes the given complete data set registration.
     *
     * @param json the complete data set registration as JSON.
     */
    this.clearCompleteDataSet = function( json )
    {
        var completeDataSets = this.getCompleteDataSets();
        var completeDataSetId = this.getCompleteDataSetId( json );

        if ( completeDataSets != null )
        {
            delete completeDataSets[completeDataSetId];

            if ( completeDataSets.length > 0 )
            {
                localStorage.removeItem( KEY_COMPLETEDATASETS );
            }
            else
            {
                localStorage[KEY_COMPLETEDATASETS] = JSON.stringify( completeDataSets );
            }
        }
    };

    /**
     * Indicates whether there exists data values or complete data set
     * registrations in the local storage.
     *
     * @return true if local data exists, false otherwise.
     */
    this.hasLocalData = function()
    {
        var dataValues = this.getAllDataValues();
        var completeDataSets = this.getCompleteDataSets();

        if ( dataValues == null && completeDataSets == null )
        {
            return false;
        }
        else if ( dataValues != null )
        {
            if ( Object.keys( dataValues ).length < 1 )
            {
                return false;
            }
        }
        else if ( completeDataSets != null )
        {
            if ( Object.keys( completeDataSets ).length < 1 )
            {
                return false;
            }
        }

        return true;
    };
}

// -----------------------------------------------------------------------------
// Option set
// -----------------------------------------------------------------------------

function searchOptionSet( uid, query, success ) 
{
    if ( window.DAO !== undefined && window.DAO.store !== undefined ) {
        DAO.store.get( 'optionSets', uid ).done( function ( obj ) {
            if ( obj ) {
                var options = [];

                if ( query == null || query == '' ) {
                    options = obj.optionSet.options.slice( 0, MAX_DROPDOWN_DISPLAYED - 1 );
                } else {
                    query = query.toLowerCase();

                    for ( var idx=0, len = obj.optionSet.options.length; idx < len; idx++ ) {
                        var item = obj.optionSet.options[idx];

                        if ( options.length >= MAX_DROPDOWN_DISPLAYED ) {
                            break;
                        }

                        if ( item.toLowerCase().indexOf( query ) != -1 ) {
                            options.push( item );
                        }
                    }
                }

                success( $.map( options, function ( item ) {
                    return {
                        label: item,
                        id: item
                    };
                } ) );
            } else {
                getOptions( uid, query, success );
            }
        } );
    } else {
        getOptions( uid, query, success );
    }
}

function getOptions( uid, query, success ) {
    $.ajax( {
        url: '../api/optionSets/' + uid + '.json?links=false&q=' + query,
        dataType: "json",
        cache: false,
        type: 'GET',
        success: function ( data ) {
            success( $.map( data.options, function ( item ) {
                return {
                    label: item,
                    id: item
                };
            } ) );
        }
    } );
}

function loadOptionSets() {
    var options = _.values( dhis2.de.optionSets );
    var uids = [];

    var deferred = $.Deferred();
    var promise = deferred.promise();

    _.each( options, function ( item, idx ) {
        if ( uids.indexOf( item.uid ) == -1 ) {
            DAO.store.get( 'optionSets', item.uid ).done( function( obj ) {
                if( !obj || obj.optionSet.version !== item.v ) {
                    promise = promise.then( function () {
                        return $.ajax( {
                            url: '../api/optionSets/' + item.uid + '.json?links=false',
                            type: 'GET',
                            cache: false
                        } ).done( function ( data ) {
                            log( 'Successfully stored optionSet: ' + item.uid );

                            var obj = {};
                            obj.id = item.uid;
                            obj.optionSet = data;
                            DAO.store.set( 'optionSets', obj );
                        } );
                    } );

                    uids.push( item.uid );
                }
            });
        }
    } );

    promise = promise.then( function () {
    } );

    deferred.resolve();
}

function insertOptionSets() {
    $( '.entryoptionset').each( function( idx, item ) {
    	var optionSetKey = splitFieldId(item.id);

        if ( dhis2.de.multiOrganisationUnit ) {
        	item = optionSetKey.organisationUnitId + '-' + optionSetKey.dataElementId + '-' + optionSetKey.optionComboId;
        } 
        else {
        	item = optionSetKey.dataElementId + '-' + optionSetKey.optionComboId;
        }

        item = item + '-val';
        optionSetKey = optionSetKey.dataElementId + '-' + optionSetKey.optionComboId;
        autocompleteOptionSetField( item, dhis2.de.optionSets[optionSetKey].uid );
    } );
}

function autocompleteOptionSetField( idField, optionSetUid ) {
    var input = jQuery( '#' + idField );

    if ( !input ) {
        return;
    }

    input.css( 'width', '85%' );
    input.autocomplete( {
        delay: 0,
        minLength: 0,
        source: function ( request, response ) {
            searchOptionSet( optionSetUid, input.val(), response );
        },
        select: function ( event, ui ) {
            input.val( ui.item.value );
            input.autocomplete( 'close' );
            input.change();
        }
    } ).addClass( 'ui-widget' );

    input.data( 'autocomplete' )._renderItem = function ( ul, item ) {
        return $( '<li></li>' )
            .data( 'item.autocomplete', item )
            .append( '<a>' + item.label + '</a>' )
            .appendTo( ul );
    };

    var wrapper = this.wrapper = $( '<span style="width:200px">' )
        .addClass( 'ui-combobox' )
        .insertAfter( input );

    var button = $( '<a style="width:20px; margin-bottom:-5px;height:20px;">' )
        .attr( 'tabIndex', -1 )
        .attr( 'title', i18n_show_all_items )
        .appendTo( wrapper )
        .button( {
            icons: {
                primary: 'ui-icon-triangle-1-s'
            },
            text: false
        } )
        .addClass( 'small-button' )
        .click( function () {
            if ( input.autocomplete( 'widget' ).is( ':visible' ) ) {
                input.autocomplete( 'close' );
                return;
            }
            $( this ).blur();
            input.autocomplete( 'search', '' );
            input.focus();
        } );
}

// -----------------------------------------------------------------------------
// Various
// -----------------------------------------------------------------------------

function printBlankForm()
{
	$( '#contentDiv input, select' ).css( 'display', 'none' );
	window.print();
	$( '#contentDiv input, select' ).css( 'display', '' );	
}
