// Identifiers for which zero values are insignificant, also used in entry.js
var significantZeros = [];

// Array with associative arrays for each data element, populated in select.vm
var dataElements = [];

// Associative array with [indicator id, expression] for indicators in form,
// also used in entry.js
var indicatorFormulas = [];

// Array with associative arrays for each data set, populated in select.vm
var dataSets = [];

// Associative array with identifier and array of assigned data sets
var dataSetAssociationSets = [];

// Associate array with mapping between organisation unit identifier and data
// set association set identifier
var organisationUnitAssociationSetMap = [];

// Array with keys on form {dataelementid}-{optioncomboid}-min/max with min/max
// values
var currentMinMaxValueMap = [];

// Indicates whether any data entry form has been loaded
var dataEntryFormIsLoaded = false;

// Indicates whether meta data is loaded
var metaDataIsLoaded = false;

// Indicates whether meta data is loaded
var chapterIsShowed = false;

// Currently selected organisation unit identifier
var currentOrganisationUnitId = null;

// The current selected orgunit name
var currentOrganisationUnitName = "";

// Currently selected data set identifier
var currentChapterId = null;

// Currently selected data set identifier
var currentDataSetId = null;

// Currently selected period type name
var currentPeriodType = "";

// Current offset, next or previous corresponding to increasing or decreasing
// value with one
var currentPeriodOffset = 0;

// Username of user who marked the current data set as complete if any
var currentCompletedByUser = null;

// Period type object
var periodTypeFactory = new PeriodType();

// Instance of the StorageManager
var storageManager = new StorageManager();

var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_RED = '#ff8a8a';
var COLOR_ORANGE = '#ff6600';
var COLOR_WHITE = '#ffffff';
var COLOR_GREY = '#cccccc';
var COLOR_PINK = '#cd78cd';

var DEFAULT_TYPE = 'int';
var DEFAULT_NAME = '[unknown]';

var FORMTYPE_CUSTOM = 'custom';
var FORMTYPE_SECTION = 'section';
var FORMTYPE_DEFAULT = 'default';

/**
 * Page init. The order of events is:
 *
 * 1. Load ouwt 2. Load meta-data (and notify ouwt) 3. Check and potentially
 * download updated forms from server
 */

function hideExportDiv()
{
	hideById( 'inputCriteria' );
	showById( 'showButtonDiv' );
}

function showExportDiv()
{
	showById( 'inputCriteria' );
	hideById( 'showButtonDiv' );
}
 
function organisationUnitSelectedHospitals( orgUnits, orgUnitNames )
{
    currentOrganisationUnitId = orgUnits[0];
    currentOrganisationUnitName = orgUnitNames[0];

	loadDataSets( currentOrganisationUnitName );
}

selection.setListenerFunction( organisationUnitSelectedHospitals );

function loadDataSets( _unitName )
{
	if ( _unitName )
	{
		jQuery( '#selectedOrganisationUnit' ).html( _unitName );
		jQuery( '#currentOrganisationUnit' ).html( _unitName );
	
		jQuery.ajax({
			type: 'GET',
			url: 'loadDataSet.action',
			dataType: 'json',
			//async: false,
			success: function( json )
			{
				clearListById( 'selectedDataSetId' );

				if ( json.dataSets.length > 0 )
				{
					$( '#selectedDataSetId' ).removeAttr( 'disabled' );
					
					addOptionById( 'selectedDataSetId', '-1', '[ ' + i18n_select_data_set + ' ]' );

					jQuery.each( json.dataSets, function( i, item )
					{
						if ( item.id == currentDataSetId )
						{
							$('#selectedDataSetId').append('<option value=' + item.id  + ' formType="'+ item.formType + '" periodType="' + item.periodType + '" selected="true">' + item.name + '</option>');
						}
						else
						{
							$('#selectedDataSetId').append('<option value=' + item.id  + ' formType="'+ item.formType + '" periodType="' + item.periodType + '" >' + item.name + '</option>');
						}
					} );
					
					enable( 'selectedDataSetId' );

					var periodId = $( '#selectedPeriodId option:selected' ).val();
					var dataSetId = $( '#selectedDataSetId option:selected' ).val();
					var chapterId = $( '#chapterId option:selected' ).val();
					
					if ( (dataSetId && dataSetId != -1) && (currentDataSetId && currentDataSetId != -1) && (dataSetId = currentDataSetId) && (periodId && periodId != -1) )
					{
						loadDataValues( dataSetId, chapterId );
					} else {
						clearEntryForm();
					}
				} else {
					resetCriteriaDiv();
				}
			}
		});
	}
}

function addEventListeners()
{
    var dataSetId = $( '#selectedDataSetId option:selected' ).val();
	var formType = $( '#selectedDataSetId option:selected' ).attr('formType');

    $( '[name="entryfield"]' ).each( function( i )
    {
        var id = $( this ).attr( 'id' );
        var dataElementId = id.split( '-' )[0];
        var optionComboId = id.split( '-' )[1];
       
        $( this ).unbind( 'focus' );
        $( this ).unbind( 'blur' );
        $( this ).unbind( 'change' );
        $( this ).unbind( 'dblclick' );
        $( this ).unbind( 'keyup' );

        $( this ).focus( valueFocus );

        $( this ).blur( valueBlur );

        $( this ).change( function()
        {
            saveVal( dataElementId, optionComboId );
        } );

        $( this ).dblclick( function()
        {
            viewHist( dataElementId, optionComboId );
        } );

        $( this ).keyup( function(event)
        {
            keyPress( event, this );
        } );

		if ( formType != FORMTYPE_CUSTOM )
		{
        	$( this ).css( 'width', '100%' );
        	$( this ).css( 'text-align', 'center' );
		}
    } );

    $( '[name="entryselect"]' ).each( function( i )
    {
        var id = $( this ).attr( 'id' );
        var dataElementId = id.split( '-' )[0];
        var optionComboId = id.split( '-' )[1];

        $( this ).unbind( 'focus' );
        $( this ).unbind( 'change' );

        $( this ).focus( valueFocus );

        $( this ).blur( valueBlur );

        $( this ).change( function()
        {
            saveBoolean( dataElementId, optionComboId );
        } );

        $( this ).css( 'width', '100%' );
    } );
}

function getDataElementType( dataElementId )
{
	if ( dataElements[dataElementId] != null )
	{
		return dataElements[dataElementId];
	}

	//log( 'Data element not present in data set, falling back to default type: ' + dataElementId );
	return DEFAULT_TYPE;
}

function getOptionComboName( optionComboId )
{
	var span = $( '#' + optionComboId + '-optioncombo' );

	if ( span != null )
	{
		return span.text();
	}

	//log( 'Category option combo not present in form, falling back to default name: ' + optionComboId );
	return DEFAULT_NAME;
}

// ----------------------------------------------------------------------------
// OrganisationUnit Selection
// -----------------------------------------------------------------------------

/**
 * Returns an array containing associative array elements with id and name
 * properties. The array is sorted on the element name property.
 */
function getSortedDataSetList()
{
    var associationSet = organisationUnitAssociationSetMap[currentOrganisationUnitId];
    var orgUnitDataSets = dataSetAssociationSets[associationSet];

    var dataSetList = [];

    for ( i in orgUnitDataSets )
    {
        var dataSetId = orgUnitDataSets[i];
        var dataSetName = dataSets[dataSetId].name;

        var row = [];
        row['id'] = dataSetId;
        row['name'] = dataSetName;
        dataSetList[i] = row;
    }

    dataSetList.sort( function( a, b )
    {
        return a.name > b.name ? 1 : a.name < b.name ? -1 : 0;
    } );

    return dataSetList;
}

// -----------------------------------------------------------------------------
// Next/Previous Periods Selection
// -----------------------------------------------------------------------------

function nextPeriodsSelected()
{
    if ( currentPeriodOffset < 0 ) // Cannot display future periods
    {
        currentPeriodOffset++;
        displayPeriodsInternal();
    }
}

function previousPeriodsSelected()
{
    currentPeriodOffset--;
    displayPeriodsInternal();
}

function displayPeriodsInternal()
{
    var dataSetId = $( '#selectedDataSetId option:selected' ).val();
    var periodType = $( '#selectedDataSetId option:selected' ).attr('periodType');
    var periods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
    periods = periodTypeFactory.filterFuturePeriods( periods );

    clearListById( 'selectedPeriodId' );

	addOptionById( 'selectedPeriodId', '-1', '[ ' + i18n_select_period + ' ]' );
	
    for ( i in periods )
    {
        addOptionById( 'selectedPeriodId', periods[i].id, periods[i].name );
    }
}

// -----------------------------------------------------------------------------
// DataSet Selection
// -----------------------------------------------------------------------------

function dataSetSelected()
{
    clearEntryForm();
	
    $( '#currentPeriod' ).html( i18n_no_period_selected );
    $( '#currentDataElement' ).html( i18n_no_dataelement_selected );

	clearListById( 'selectedPeriodId' );
	
	$( '#selectedPeriodId' ).unbind( 'change' );
	$( '#selectedPeriodId' ).removeAttr( 'disabled' );
    $( '#prevButton' ).removeAttr( 'disabled' );
    $( '#nextButton' ).removeAttr( 'disabled' );

    var dataSetId = $( '#selectedDataSetId option:selected' ).val();

    if ( dataSetId != -1 )
    {
		var periodId = $( '#selectedPeriodId option:selected' ).val();
		var periodType = $( '#selectedDataSetId option:selected' ).attr('periodType');
		var periods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
		periods = periodTypeFactory.filterFuturePeriods( periods );
	
		addOptionById( 'selectedPeriodId', "-1", '[ ' + i18n_select_period + ' ]' );
	
        for ( i in periods )
        {
            addOptionById( 'selectedPeriodId', periods[i].id, periods[i].name );
        }

        if ( periodId && periodId != -1 && currentPeriodType && currentPeriodType == periodType )
        {
            $( '#selectedPeriodId' ).val( periodId );
        }
        else
        {
            clearEntryForm();
        }

        currentDataSetId = dataSetId;
		currentPeriodType = periodType;

		loadAttributeValues( dataSetId );
    } else {
		resetCriteriaDiv();
	}
}

function loadSubDataSets( dataSetId )
{
	$.getJSON( 'loadDepartments.action',
	{
		dataSetId: dataSetId
	},
	function( json ) 
	{
		var departmentList = jQuery( '#subDataSetId' );
		departmentList.empty();
	
		if ( json.department.length > 0 )
		{
			departmentList.append('<option value=-1>' + i18n_select_department + '</option>');

			for ( i in json.department ) 
			{ 
				departmentList.append('<option value=' + json.department[i].id + '>' + json.department[i].name + '</option>');
			}
			
			byId( 'inputCriteria' ).style.width = '900px';
			byId( 'inputCriteria' ).style.height = '250px';
			showById( 'departmentTitleDiv' );
			showById( 'departmentDiv' );
			
			jQuery( '#valueInput' ).unbind( 'change' );
			jQuery( '#value' ).unbind( 'select' );
		}
		else 
		{
			loadAttributeValues( dataSetId );
		}

		jQuery( '#selectedPeriodId' ).bind( 'change', periodSelected );
	} );
}

var arrAttributeValues = new Array();

function loadAttributeValues( dataSetId )
{
	jQuery( '#chapterId > option' ).first().attr( 'selected', true );

	$.getJSON( 'loadAttribueValues.action',
	{
		dataSetId: dataSetId
	}
	, function( json )
	{
		if ( json.attributeValues.length > 0 )
		{
			byId( 'inputCriteria' ).style.width = '504px';
			byId( 'inputCriteria' ).style.height = '100px';

			showById( 'chapterTR' );
			chapterIsShowed = true;
		}
		else
		{
			byId( 'inputCriteria' ).style.width = '504px';
			byId( 'inputCriteria' ).style.height = '80px';
			
			hideById( 'chapterTR' );
			chapterIsShowed = false;
			currentChapterId = null;
		}
		
		jQuery( '#selectedPeriodId' ).bind( 'change', periodSelected );
	} );
}

function loadAttributeValuesByChapter( chapterId )
{
	clearListById( 'value' );

	if ( chapterId && chapterId != -1 )
	{
		$.getJSON( '../dhis-web-spreadsheet-reporting/loadAttributeValuesByChapter.action',
		{
			chapterId: chapterId
		}
		, function( json )
		{			
			var tempArray = [];
		
			if ( json.values.length > 0 )
			{
				jQuery.each( json.values, function( i, item )
				{
					if ( jQuery.inArray( item.value, arrAttributeValues ) != -1 )
					{						
						tempArray.push( item.value );
					}
				} );

				jQuery.each( tempArray, function( i, item )
				{
					jQuery( '#value' ).append( '<option value="' + item + '">' + item + '</option>' );
				} );
				
				setFieldValue( 'valueInput', jQuery( '#value > option:first' ).val() );
			}
		} );
	}
	else
	{
		jQuery.each( arrAttributeValues, function( i, item )
		{
			jQuery( '#value' ).append( '<option value="' + item + '">' + item + '</option>' );
		} );

		setFieldValue( 'valueInput', jQuery( '#value > option:first' ).val() );
	}
}

// -----------------------------------------------------------------------------
// Period Selection
// -----------------------------------------------------------------------------

function periodSelected()
{
	var periodName = $( '#selectedPeriodId option:selected' ).text();
	var dataSetId = $( '#selectedDataSetId option:selected' ).val();
	var formLoaded = (getInnerHTML('contentDiv') != '');

	if ( dataSetId && dataSetId != -1 )
	{
		$( '#currentPeriod' ).html( periodName );

		var periodId = getFieldValue( 'selectedPeriodId' );
		var chapterId = getFieldValue( 'chapterId' );

		if ( periodId && periodId != -1 )
		{
			if ( chapterIsShowed && (currentChapterId == null || currentChapterId == -1) )
			{
				return;
			}
		
			if ( currentDataSetId && currentDataSetId == dataSetId )
			{
				if ( formLoaded )
				{
					loadDataValues( dataSetId, chapterId );
				} else {
					loadForm( dataSetId, "" );
				}
			} else {
				loadForm( dataSetId, "" );
			}
		} else {
			clearEntryForm();
		}
	} else {
		setHeaderDelayMessage( i18n_please_select_data_set );
	}
}

// -----------------------------------------------------------------------------
// Form
// -----------------------------------------------------------------------------

function resetCriteriaDiv()
{
	currentDataSetId = null;
	currentPeriodType = null;
	currentChapterId = null;
	
	clearListById( 'selectedPeriodId' );
	
	hideById( 'chapterTR' );
	
	byId( 'inputCriteria' ).style.width = '504px';
	byId( 'inputCriteria' ).style.height = '80px';

    $( '#currentPeriod' ).html( i18n_no_period_selected );
    $( '#currentDataElement' ).html( i18n_no_dataelement_selected );
	
    clearEntryForm();
}

function clearEntryForm()
{
    $( '#contentDiv' ).html( '' );

    currentPeriodOffset = 0;

    dataEntryFormIsLoaded = false;

    $( '#completenessDiv' ).hide();
    $( '#infoDiv' ).hide();
}

function loadForm( dataSetId, value )
{
	lockScreen();

    $( '#currentDataElement' ).html( i18n_no_dataelement_selected );

	var chapterId = getFieldValue( 'chapterId' );
	var	url = ( chapterId && chapterId != -1 ? 'loadICDForm.action' : 'loadForm.action' );

	$( '#contentDiv' ).load( url,
	{
		dataSetId : dataSetId,
		chapterId: chapterId,
		value: (value == undefined ? "" : value)
	},
	function ( responseText, textStatus, req )
	{
		if ( textStatus == "error" ) {
			unLockScreen();
			hideById( 'showReportButton' );
			hideById( 'ICDButtonDiv' );
			clearEntryForm();
			setHeaderDelayMessage( i18n_disconnect_server );
			return;
        }
		loadDataValues( dataSetId, chapterId );
	} );
}

function loadFormByChapter( chapterId )
{
	var periodId = getFieldValue( 'selectedPeriodId' );
	var dataSetId = getFieldValue( 'selectedDataSetId' );

	currentChapterId = chapterId;
	
	if ( dataSetId == null || dataSetId == -1 )
	{
		setHeaderDelayMessage( i18n_please_select_data_set );
		return;
	}
	
	if ( periodId == null || periodId == -1 )
	{
		setHeaderDelayMessage( i18n_please_select_period );
		return;
	}

	loadForm( dataSetId );
}

function loadDepartmentFormSelected()
{
    var periodName = $( '#selectedPeriodId option:selected' ).text();
    var dataSetId = $( '#subDataSetId option:selected' ).val();

    $( '#currentPeriod' ).html( periodName );

    var periodId = $( '#selectedPeriodId' ).val();

    if ( periodId && periodId != -1 && dataSetId != -1 )
    {
        lockScreen();
        loadForm( dataSetId, getFieldValue( 'valueInput' ) );
    }
	else
	{
		clearEntryForm();
	}
}

function loadDataValues( dataSetId, chapterId )
{
	lockScreen();

    $( '#completeButton' ).removeAttr( 'disabled' );
    $( '#undoButton' ).attr( 'disabled', 'disabled' );
    $( '#infoDiv' ).css( 'display', 'none' );
	showById( 'showReportButton' );

    insertDataValues( dataSetId, chapterId );
    displayEntryFormCompleted();
}

function insertDataValues( dataSetId, chapterId )
{
    var dataValueMap = [];
	currentMinMaxValueMap = []; // Reset

    var periodId = $( '#selectedPeriodId option:selected' ).val();

    // Clear existing values and colors, grey disabled fields

    $( '[name="entryfield"]' ).val( '' );
    $( '[name="entryselect"]' ).val( '' );

    $( '[name="entryfield"]' ).css( 'background-color', COLOR_WHITE );
    $( '[name="entryselect"]' ).css( 'background-color', COLOR_WHITE );

    $( '[name="min"]' ).html( '' );
    $( '[name="max"]' ).html( '' );

    $( '[name="entryfield"]' ).filter( ':disabled' ).css( 'background-color', COLOR_GREY );
	
    $.ajax( {
    	url: 'getDataValues.action',
    	data:
	    {
	        periodId : periodId,
	        dataSetId : dataSetId,
			chapterId: chapterId,
			attributeId: getFieldValue( 'attributeId' ),
			value: getFieldValue( 'valueInput' ),
	        organisationUnitId : currentOrganisationUnitId
	    },
	    dataType: 'json',
	    error: function() // disconnect to server
	    {
	    	$( '#contentDiv' ).show();
	    	$( '#completenessDiv' ).show();
	    	$( '#infoDiv' ).hide();
			setHeaderDelayMessage( i18n_disconnect_server );
			return;
	    },
	    success: function( json ) // online
	    {
	    	if ( json.locked )
	    	{
	    		$( '#contentDiv' ).hide();
	    		$( '#completenessDiv' ).hide();
	    		setHeaderDelayMessage( i18n_dataset_is_locked );
	    		return;
	    	}
	    	else
	    	{	    		
	    		$( '#contentDiv' ).show();
	    		$( '#completenessDiv' ).show();
	    	}
	    	
	        // Set data values, works for selects too as data value=select value

	        $.each( json.dataValues, function( i, value )
	        {
	            var fieldId = '#' + value.id + '-val';

	            if ( $( fieldId ) )
	            {
	                $( fieldId ).val( value.val );
	            }

	            dataValueMap[value.id] = value.val;
	        } );

	        // Set min-max values and colorize violation fields

	        $.each( json.minMaxDataElements, function( i, value )
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

	            currentMinMaxValueMap[minId] = value.min;
	            currentMinMaxValueMap[maxId] = value.max;
	        } );

	        // Update indicator values in form

	        updateIndicators();
	        updateDataElementTotals();

	        // Set completeness button

	        if ( json.complete )
	        {
	            $( '#completeButton' ).attr( 'disabled', 'disabled' );
	            $( '#undoButton' ).removeAttr( 'disabled' );

	            if ( json.storedBy )
	            {
	                $( '#infoDiv' ).show();
	                $( '#completedBy' ).html( json.storedBy );
	                $( '#completedDate' ).html( json.date );

	                currentCompletedByUser = json.storedBy;
	            }
	        }
	        else
	        {
	            $( '#completeButton' ).removeAttr( 'disabled' );
	            $( '#undoButton' ).attr( 'disabled', 'disabled' );
	            $( '#infoDiv' ).hide();
	        }

			showById( 'completenessDiv' );
			unLockScreen();
	    }
	} );
}

function displayEntryFormCompleted()
{
    addEventListeners();

    $( '#validationButton' ).removeAttr( 'disabled' );
}

function valueFocus( e )
{
    var id = e.target.id;

    var dataElementId = id.split( '-' )[0];
    var optionComboId = id.split( '-' )[1];

    var dataElementName = getDataElementName( dataElementId );
    var optionComboName = getOptionComboName( optionComboId );

    $( '#currentDataElement' ).html( dataElementName + ' ' + optionComboName );

    $( '#' + dataElementId + '-cell' ).addClass( 'currentRow' );
}

function valueBlur( e )
{
    var id = e.target.id;

    var dataElementId = id.split( '-' )[0];

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

// -----------------------------------------------------------------------------
// Data completeness
// -----------------------------------------------------------------------------

function validateCompleteDataSet()
{
    var confirmed = confirm( i18n_confirm_complete );

    if ( confirmed )
    {
        var params = storageManager.getCurrentCompleteDataSetParams();

        $.ajax( { url: 'getValidationViolations.action',
        	data: params,
        	dataType: 'json',
        	success: function( data )
	        {
	            registerCompleteDataSet( data );
	        },
	        error: function()
	        {
	            // no response from server, fake a positive result and save it
	            registerCompleteDataSet( { 'response' : 'success' } );
	        }
    	} );
    }
}

function registerCompleteDataSet( json )
{
    var params = storageManager.getCurrentCompleteDataSetParams();

	//storageManager.saveCompleteDataSet( params );

    $.ajax( {
    	url: 'registerCompleteDataSet.action',
    	data: params,
        dataType: 'json',
    	success: function(data)
        {
            if( data.status == 2 )
            {
                log( 'DataSet is locked' );
                setHeaderMessage( i18n_register_complete_failed_dataset_is_locked );
            }
            else
            {
                disableCompleteButton();

                //storageManager.clearCompleteDataSet( params );

                if ( json.response == 'input' )
                {
                    validate();
                }
            }
        },
	    error: function()
	    {
	    	disableCompleteButton();
	    }
    } );
}

function undoCompleteDataSet()
{
    var confirmed = confirm( i18n_confirm_undo );
    var params = storageManager.getCurrentCompleteDataSetParams();

    if ( confirmed )
    {
        $.ajax( {
        	url: 'undoCompleteDataSet.action',
        	data: params,
        	dataType: 'json',
        	success: function(data)
	        {
                if( data.status == 2 )
                {
                    log( 'DataSet is locked' );
                    setHeaderMessage( i18n_unregister_complete_failed_dataset_is_locked );
                }
                else
                {
                    disableUndoButton();
	                //storageManager.clearCompleteDataSet( params );
                }
	        },
	        error: function()
	        {
	            //storageManager.clearCompleteDataSet( params );
	        }
        } );
    }
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
	if ( currentCompletedByUser )
	{
		var url = '../dhis-web-commons-ajax-json/getUser.action';

		$.getJSON( url, { username:currentCompletedByUser }, function( json ) {
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

function displayValidationDialog()
{
    $( '#validationDiv' ).dialog( {
        modal : true,
        title : 'Validation',
        width : 800,
        height : 400
    } );
}

function validate()
{
    var periodId = $( '#selectedPeriodId option:selected' ).val();
    var dataSetId = $( '#selectedDataSetId option:selected' ).val();

    $( '#validationDiv' ).load( 'validate.action', {
        periodId : periodId,
        dataSetId : dataSetId,
        organisationUnitId : currentOrganisationUnitId
    }, function( response, status, xhr )
    {
        if ( status == 'error' )
        {
            window.alert( i18n_operation_not_available_offline );
        }
        else
        {
            displayValidationDialog();
        }
    } );
}

// -----------------------------------------------------------------------------
// History
// -----------------------------------------------------------------------------

function displayHistoryDialog( operandName )
{
    $( '#historyDiv' ).dialog( {
        modal : true,
        title : operandName,
        width : 580,
        height : 710
    } );
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

function viewHist( dataElementId, optionComboId )
{
    var periodId = $( '#selectedPeriodId' ).val();

    var dataElementName = getDataElementName( dataElementId );
    var optionComboName = getOptionComboName( optionComboId );
    var operandName = dataElementName + ' ' + optionComboName;

    $( '#historyDiv' ).load( 'viewHistory.action', {
        dataElementId : dataElementId,
        optionComboId : optionComboId,
        periodId : periodId,
        organisationUnitId : currentOrganisationUnitId
    }, function( response, status, xhr )
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

function closeCurrentSelection()
{
    $( '#currentSelection' ).fadeOut();
}

// -----------------------------------------------------------------------------
// Local storage of forms
// -----------------------------------------------------------------------------

/*
function updateForms()
{
    purgeLocalForms();
    updateExistingLocalForms();
    downloadRemoteForms();
}

function purgeLocalForms()
{
    var formIds = storageManager.getAllForms();

    for ( i in formIds )
    {
        var localId = formIds[i];

        if ( dataSets[localId] == null )
        {
            storageManager.deleteForm( localId );
            storageManager.deleteFormVersion( localId );
            log( 'Deleted locally stored form: ' + localId );
        }
    }

    log( 'Purged local forms' );
}

function updateExistingLocalForms()
{
    var formIds = storageManager.getAllForms();
    var formVersions = storageManager.getAllFormVersions();

    for ( i in formIds )
    {
        var localId = formIds[i];

		if( dataSets[localId] == null )
		{
			storageManager.downloadForm( localId, remoteVersion );
		}
		else
		{
			var remoteVersion = dataSets[localId].version;
			var localVersion = formVersions[localId];

			if ( remoteVersion == null || localVersion == null || remoteVersion != localVersion )
			{
				storageManager.downloadForm( localId, remoteVersion );
			}
		}
    }
}

function downloadRemoteForms()
{
    for ( dataSetId in dataSets )
    {
        var remoteVersion = dataSets[dataSetId].version;

        if ( !storageManager.formExists( dataSetId ) )
        {
            storageManager.downloadForm( dataSetId, getFieldValue( 'attributeId' ), byId( 'valueInput' ).value, remoteVersion );
        }
    }
}
*/

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

    var KEY_FORM_PREFIX = 'form-ds-';
    var KEY_FORM_VERSIONS = 'formversions-';
    var KEY_DATAVALUES = 'datavalues-';
    var KEY_COMPLETEDATASETS = 'completedatasets-';

    /**
     * Returns the total number of characters currently in the local storage.
     *
     * @return number of characters.
     */
    /*this.totalSize = function()
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
    };*/

    /**
     * Returns the total numbers of characters in stored forms currently in the
     * local storage.
     *
     * @return number of characters.
     */
    /*this.totalFormSize = function()
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
    };*/

    /**
     * Return the remaining capacity of the local storage in characters, ie. the
     * maximum size minus the current size.
     */
    /*this.remainingStorage = function()
    {
        return MAX_SIZE - this.totalSize();
    };*/

    /**
     * Saves the content of a data entry form.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @param html the form HTML content.
     * @return true if the form saved successfully, false otherwise.
     */
    /*this.saveForm = function( dataSetId, html )
    {
        var id = KEY_FORM_PREFIX + dataSetId;

        try
        {
            localStorage[id] = html;

            log( 'Successfully stored form: ' + dataSetId );
        } catch ( e )
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
    };*/

    /**
     * Gets the content of a data entry form.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @return the content of a data entry form.
     */
    /*this.getForm = function( dataSetId, attributeId, value )
    {
        var id = KEY_FORM_PREFIX + dataSetId + "_" + attributeId + "_" + value;

        return localStorage[id];
    };*/

    /**
     * Removes a form.
     *
     * @param dataSetId the identifier of the data set of the form.
     */
    /*this.deleteForm = function( dataSetId, attributeId, value )
    {
    	var id = KEY_FORM_PREFIX + dataSetId + "_" + attributeId + "_" + value;

        localStorage.removeItem( id );
    };*/

    /**
     * Returns an array of the identifiers of all forms.
     *
     * @return array with form identifiers.
     */
    /*this.getAllForms = function()
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
    };*/

    /**
     * Indicates whether a form exists.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @return true if a form exists, false otherwise.
     */
    /*this.formExists = function( dataSetId, attributeId, value )
    {
        var id = KEY_FORM_PREFIX + dataSetId + "_" + attributeId + "_" +  value;

        return localStorage[id] != null;
    };*/

    /**
     * Downloads the form for the data set with the given identifier from the
     * remote server and saves the form locally. Potential existing forms with
     * the same identifier will be overwritten. Updates the form version.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @param formVersion the version of the form of the remote data set.
     */
    /*this.downloadForm = function( dataSetId, attributeId, value, formVersion )
    {
        $.ajax( {
            url: 'loadForm.action',
            data:
            {
                dataSetId : dataSetId,
				attributeId: attributeId,
				value: value
            },
            dataSetId: dataSetId,
            formVersion: formVersion,
            dataType: 'text',
            success: function( data, textStatus, jqXHR )
            {
                storageManager.saveForm( this.dataSetId, attributeId, value, data );
                storageManager.saveFormVersion( this.dataSetId, attributeId, value, this.formVersion );
            }
        } );
    };*/

    /**
     * Saves a version for a form.
     *
     * @param the identifier of the data set of the form.
     * @param formVersion the version of the form.
     */
    /*this.saveFormVersion = function( dataSetId, attributeId, value, formVersion )
    {
		var id = dataSetId + "_" + attributeId + "_" + value;
		
        var formVersions = {};

        if ( localStorage[KEY_FORM_VERSIONS] != null )
        {
            formVersions = JSON.parse( localStorage[KEY_FORM_VERSIONS] );
        }

        formVersions[id] = formVersion;

        try
        {
            localStorage[KEY_FORM_VERSIONS] = JSON.stringify( formVersions );

            log( 'Successfully stored form version: ' + dataSetId );
        } catch ( e )
        {
            log( 'Max local storage quota reached, ignored form version: ' + dataSetId );
        }
    };*/

    /**
     * Returns the version of the form of the data set with the given
     * identifier.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @return the form version.
     */
    /*this.getFormVersion = function( dataSetId, attributeId, value )
    {
		var id = dataSetId + "_" + attributeId + "_" + value;
		
        if ( localStorage[KEY_FORM_VERSIONS] != null )
        {
            var formVersions = JSON.parse( localStorage[KEY_FORM_VERSIONS] );

            return formVersions[id];
        }

        return null;
    };*/

    /**
     * Deletes the form version of the data set with the given identifier.
     *
     * @param dataSetId the identifier of the data set of the form.
     */
    /*this.deleteFormVersion = function( dataSetId, attributeId, value )
    {
    	if ( localStorage[KEY_FORM_VERSIONS] != null )
        {
			var id = dataSetId + "_" + attributeId + "_" + value;
		
			var formVersions = JSON.parse( localStorage[KEY_FORM_VERSIONS] );

            if ( formVersions[id] != null )
            {
                delete formVersions[id];
                localStorage[KEY_FORM_VERSIONS] = JSON.stringify( formVersions );
            }
        }
    }*/

    /*this.getAllFormVersions = function()
    {
        return localStorage[KEY_FORM_VERSIONS] != null ? JSON.parse( localStorage[KEY_FORM_VERSIONS] ) : null;
    };*/

    /**
     * Saves a data value.
     *
     * @param dataValue The datavalue and identifiers in json format.
     */
    /*this.saveDataValue = function( dataValue )
    {
        var id = this.getDataValueIdentifier( dataValue.dataElementId, dataValue.optionComboId, dataValue.periodId,
                dataValue.organisationUnitId );

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
        } catch ( e )
        {
            log( 'Max local storage quota reached, ignored data value' );
        }
    };*/

    /**
     * Gets the value for the data value with the given arguments, or null if it
     * does not exist.
     *
     * @param dataElementId the data element identifier.
     * @param categoryOptionComboId the category option combo identifier.
     * @param periodId the period identifier.
     * @param organisationUnitId the organisation unit identifier.
     * @return the value for the data value with the given arguments, null if
     *         non-existing.
     */
    /*this.getDataValue = function( dataElementId, categoryOptionComboId, periodId, organisationUnitId )
    {
        var id = this.getDataValueIdentifier( dataElementId, categoryOptionComboId, periodId, organisationUnitId );

        if ( localStorage[KEY_DATAVALUES] != null )
        {
            var dataValues = JSON.parse( localStorage[KEY_DATAVALUES] );

            return dataValues[id];
        }

        return null;
    };*/

    /**
     * Removes the given dataValue from localStorage.
     *
     * @param dataValue The datavalue and identifiers in json format.
     */
    /*this.clearDataValueJSON = function( dataValue )
    {
        this.clearDataValue( dataValue.dataElementId, dataValue.optionComboId, dataValue.periodId,
                dataValue.organisationUnitId );
    };*/

    /**
     * Removes the given dataValue from localStorage.
     *
     * @param dataElementId the data element identifier.
     * @param categoryOptionComboId the category option combo identifier.
     * @param periodId the period identifier.
     * @param organisationUnitId the organisation unit identifier.
     */
    /*this.clearDataValue = function( dataElementId, categoryOptionComboId, periodId, organisationUnitId )
    {
        var id = this.getDataValueIdentifier( dataElementId, categoryOptionComboId, periodId, organisationUnitId );
        var dataValues = this.getAllDataValues();

        if ( dataValues[id] != null )
        {
            delete dataValues[id];
            localStorage[KEY_DATAVALUES] = JSON.stringify( dataValues );
        }
    };*/

    /**
     * Returns a JSON associative array where the keys are on the form <data
     * element id>-<category option combo id>-<period id>-<organisation unit
     * id> and the data values are the values.
     *
     * @return a JSON associative array.
     */
    /*this.getAllDataValues = function()
    {
        return localStorage[KEY_DATAVALUES] != null ? JSON.parse( localStorage[KEY_DATAVALUES] ) : null;
    };*/

    /**
     * Supportive method.
     */
    this.getDataValueIdentifier = function( dataElementId, categoryOptionComboId, periodId, organisationUnitId )
    {
        return dataElementId + '-' + categoryOptionComboId + '-' + periodId + '-' + organisationUnitId;
    };

    /**
     * Supportive method.
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
            'periodId' : $( '#selectedPeriodId option:selected' ).val(),
            'dataSetId' : $( '#selectedDataSetId option:selected' ).val(),
            'organisationUnitId' : currentOrganisationUnitId
        };

        return params;
    };

    /**
     * Gets all complete data set registrations as JSON.
     *
     * @return all complete data set registrations as JSON.
     */
    /*this.getCompleteDataSets = function()
    {
        if ( localStorage[KEY_COMPLETEDATASETS] != null )
        {
            return JSON.parse( localStorage[KEY_COMPLETEDATASETS] );
        }

        return null;
    };*/

    /**
     * Saves a complete data set registration.
     *
     * @param json the complete data set registration as JSON.
     */
    /*this.saveCompleteDataSet = function( json )
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

        localStorage[KEY_COMPLETEDATASETS] = JSON.stringify( completeDataSets );
    };*/

    /**
     * Removes the given complete data set registration.
     *
     * @param the complete data set registration as JSON.
     */
    /*this.clearCompleteDataSet = function( json )
    {
        var completeDataSets = this.getCompleteDataSets();
        var completeDataSetId = this.getCompleteDataSetId( json );

        if ( completeDataSets != null )
        {
            delete completeDataSets[completeDataSetId];

            if ( completeDataSets.length > 0 )
            {
                localStorage.remoteItem( KEY_COMPLETEDATASETS );
            }
            else
            {
                localStorage[KEY_COMPLETEDATASETS] = JSON.stringify( completeDataSets );
            }
        }
    };*/

    /**
     * Indicates whether there exists data values or complete data set
     * registrations in the local storage.
     *
     * @return true if local data exists, false otherwise.
     */
    /*this.hasLocalData = function()
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
    };*/
}

function getAttributes()
{
	clearListById( 'attributeId' );

	$.getJSON( '../dhis-web-commons-ajax-json/getAttributes.action',{}
	, function( json ) 
	{
		addOptionById( 'attributeId', '', i18n_select_attribute );
		
		for ( i in json.attributes ) 
		{ 
			$('#attributeId').append('<option value=' + json.attributes[i].id + '>' + json.attributes[i].name + '</option>');
		}
	} );
}

function getSuggestedAttrValue()
{
	clearListById( 'value' );
	$.getJSON( 'loadAttributeValuesByAttribute.action',
		{
			attributeId: getFieldValue( 'attributeId' )
		}
		, function( json ) 
		{
			addOptionById( 'value', '', i18n_select_attribute );
			
			for ( i in json.attributeValues ) 
			{ 
				$('#value').append('<option value=' + json.attributeValues[i].value + '>' + json.attributeValues[i].value + '</option>');
			}
			
			autoCompletedField();
		} );
}

function autoCompletedField()
{
	var select = jQuery( "#value" );
	$( "#valueButton" ).unbind('click');
	enable( 'valueButton' );
	var selected = select.children( "option:selected" );
	var value = selected.val() ? selected.text() : "";
	
	var input = jQuery( "#valueInput" )
		.insertAfter( select )
		.val( value )
		.autocomplete({
			delay: 0,
			minLength: 0,
			source: function( request, response ) {
				var matcher = new RegExp( $.ui.autocomplete.escapeRegex(request.term), "i" );
				response( select.children( "option" ).map(function() {
					var text = $( this ).text();
					if ( this.value && ( !request.term || matcher.test(text) ) )
					{
						return {
							label: text,
							value: text,
							option: this
						};
					}
				}) );
			},
			select: function( event, ui ) {
				ui.item.option.selected = true;
				setFieldValue( 'valueInput', ui.item.option.value );
				periodSelected();
			}
		}).addClass( "ui-widget ui-widget-content ui-corner-left" );

	input.data( "autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li></li>" )
			.data( "item.autocomplete", item )
			.append( "<a>" + item.label + "</a>" )
			.appendTo( ul );
	};

	input.keypress( function( e )
	{
		code= (e.keyCode ? e.keyCode : e.which);
		if ( code == 13 ) {
			periodSelected();
		}
	});
	
	showById('valueButton');
	var button = $( "#valueButton" )
		.attr( "title", i18n_show_all_items )
		.button({
			icons: {
				primary: "ui-icon-triangle-1-s"
			},
			text: false
		})
		.removeClass( "ui-corner-all" )
		.addClass( "ui-corner-right ui-button-icon" )
		.click(function() {
			// close if already visible
			if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
				input.autocomplete( "close" );
				periodSelected();
				return;
			}

			// work around a bug (likely same cause as #5265)
			$( this ).blur();

			// pass empty string as value to search for, displaying all results
			input.autocomplete( "search", "" );
			input.focus();
		});
}

function showICDReport()
{
	$('#showReportDiv').load( "showICDReport.action",
	{
		dataSetId: getFieldValue( 'selectedDataSetId' ),
		periodId: getFieldValue( 'selectedPeriodId' ),
		sourceId: currentOrganisationUnitId,
		chapterId: getFieldValue( 'chapterId' )
	}
	, function(){
		showById( 'ICDButtonDiv' );
	})/*.dialog({
		title: 'ICD REPORTING FORM',
		maximize: true, 
		closable: true,
		modal:false,
		overlay:{background:'#000000', opacity:0.1},
		width: 1160,
		height: 520
	})*/;
}

function toggleICDReportDiv()
{
	openICD = !openICD;

	if ( openICD ) {
		showById( 'showReportDiv' );
		setFieldValue( 'ICDResultButton', i18n_collpase_icd_report_result );
	} else {
		hideById( 'showReportDiv' );
		setFieldValue( 'ICDResultButton', i18n_explore_icd_report_result );
	}
}