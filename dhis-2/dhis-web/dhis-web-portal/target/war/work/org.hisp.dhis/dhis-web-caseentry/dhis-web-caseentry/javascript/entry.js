
var MAX_DROPDOWN_DISPLAYED = 30;

//------------------------------------------------------------------------------
// Save value
//------------------------------------------------------------------------------

function saveVal( dataElementUid )
{
    var programStageUid = getProgramStageUid();
    var fieldId = programStageUid + '-' + dataElementUid + '-val';
    var field = byId( fieldId );

    if( field == null) return;

    var fieldValue = jQuery.trim( field.value );
    var arrData = jQuery( "#" + fieldId ).attr( 'data' ).replace( '{', '' ).replace( '}', '' ).replace( /'/g, "" ).split( ',' );
    var data = [];

    $.each(arrData, function() {
        var values = $.trimArray(this.split(':'));
        data[values[0]] = values[1];
    });

    var dataElementName = data['deName'];
    var type = data['deType'];

    field.style.backgroundColor = SAVING_COLOR;

    if ( fieldValue != '' ) {
        if ( type == 'int' || type == 'number' || type == 'positiveNumber' || type == 'negativeNumber' || type == 'zeroPositiveInt' ) {
            if ( type == 'int' && !isInt( fieldValue ) ) {
                field.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer + '\n\n' + dataElementName );

                field.focus();

                return;
            }
            else if ( type == 'number' && !isNumber( fieldValue ) ) {
                field.style.backgroundColor = '#ffcc00';
                window.alert( i18n_value_must_number + '\n\n' + dataElementName );
                field.focus();

                return;
            }
            else if ( type == 'positiveNumber' && !isPositiveInt( fieldValue ) ) {
                field.style.backgroundColor = '#ffcc00';
                window.alert( i18n_value_must_positive_integer + '\n\n' + dataElementName );
                field.focus();

                return;
            }
            else if ( type == 'negativeNumber' && !isNegativeInt( fieldValue ) ) {
                field.style.backgroundColor = '#ffcc00';
                window.alert( i18n_value_must_negative_integer + '\n\n' + dataElementName );
                field.focus();

                return;
            }
            else if ( type == 'zeroPositiveInt' && !isZeroOrPositiveInt( fieldValue ) ) {
                field.style.backgroundColor = '#ffcc00';
                window.alert( i18n_value_must_zero_or_positive_integer + '\n\n' + dataElementName );
                field.focus();

                return;
            }
        }
        else if ( type == 'date' ) {
            field.focus();
        }

    }
    
	var value = fieldValue;

    if ( type == 'trueOnly' ) {
        if ( field.checked )
            fieldValue = "true";
        else
            fieldValue = "";
    }

    var valueSaver = new ValueSaver( dataElementUid, fieldValue, type, SUCCESS_COLOR );
    valueSaver.save();
}

function saveOpt( dataElementUid )
{
	var programStageUid = getProgramStageUid();
	var field = byId( programStageUid + '-' + dataElementUid + '-val' );	
	field.style.backgroundColor = SAVING_COLOR;
	
	var valueSaver = new ValueSaver( dataElementUid, field.options[field.selectedIndex].value, 'bool', SUCCESS_COLOR );
    valueSaver.save();
}

function saveRadio( dataElementUid, value )
{
	var valueSaver = new ValueSaver( dataElementUid, value, 'bool', SUCCESS_COLOR );
    valueSaver.save();
}

function updateProvidingFacility( dataElementUid, checkField )
{
	var checked= checkField.checked;
    var facilitySaver = new FacilitySaver( dataElementUid, checked, SUCCESS_COLOR );
    facilitySaver.save();    
}

function saveExecutionDate( programId, programStageInstanceId, field )
{
	field.style.backgroundColor = SAVING_COLOR;
    var executionDateSaver = new ExecutionDateSaver( programId, programStageInstanceId, field.value, SUCCESS_COLOR );
    executionDateSaver.save();
	
    if( !jQuery("#entryForm").is(":visible") )
    {
        toggleContentForReportDate(true);
    }
}

function getProgramType() {
    var programType = jQuery( '.stage-object-selected' ).attr( 'programType' );

    if ( programType == undefined ) {
        programType = jQuery( '#programId option:selected' ).attr( 'programType' );
    }

    return programType;
}

function getProgramStageUid() {
    var programStageUid = jQuery( '.stage-object-selected' ).attr( 'psuid' );

    if ( programStageUid == undefined ) {
        programStageUid = jQuery( '#programId option:selected' ).attr( 'psuid' );
    }

    if ( programStageUid == undefined ) {
        programStageUid = jQuery( '#entryFormContainer [id=programStageUid]' ).val();
    }

    if ( programStageUid == undefined ) {
        programStageUid = jQuery( '#programStageUid' ).val();
    }

    return programStageUid;
}

/**
* Display data element name in selection display when a value field recieves
* focus.
* XXX May want to move this to a separate function, called by valueFocus.
* @param e focus event
* @author Hans S. Tommerholt
*/
function valueFocus(e) 
{
    //Retrieve the data element id from the id of the field
    var str = e.target.id;
	
    var match = /.*\[(.*)\]/.exec( str ); //value[-dataElementUid-]

    if( !match ) {
        return;
    }

    var deId = match[1];

    //Get the data element name
    var nameContainer = document.getElementById('value[' + deId + '].name');

    if( !nameContainer ) {
        return;
    }

    var name = '';

    var as = nameContainer.getElementsByTagName('a');

    if( as.length > 0 )	//Admin rights: Name is in a link
    {
        name = as[0].firstChild.nodeValue;
    }
    else {
        name = nameContainer.firstChild.nodeValue;
    }
}

function keyPress( event, field )
{
    var key = 0;

    if( event.charCode ) {
        key = event.charCode;
        /* Safari2 (Mac) (and probably Konqueror on Linux, untested) */
    }
    else {
        if( event.keyCode ) {
            key = event.keyCode;
            /* Firefox1.5 (Mac/Win), Opera9 (Mac/Win), IE6, IE7Beta2, Netscape7.2 (Mac) */
        }
        else {
            if( event.which ) {
                key = event.which;
                /* Older Netscape? (No browsers triggered yet) */
            }
        }
    }

    if( key == 13 ) {
        nextField = getNextEntryField(field);
        if( nextField ) {
            nextField.focus();
        }
        return true;
    }

    return true;
}

function getNextEntryField( field )
{
    var index = field.getAttribute( 'tabindex' );
	return $( '[name="entryfield"][tabindex="' + (++index) + '"]' );
}

//-----------------------------------------------------------------
// Save value for dataElement of type text, number, boolean, combo
//-----------------------------------------------------------------

function ValueSaver( dataElementId_, value_, dataElementType_, resultColor_  )
{
    var dataElementUid = dataElementId_;
	var providedElsewhereId = getFieldValue('programStageUid') + "-" + dataElementId_ + "-facility";
	var value = value_;
	var type = dataElementType_;
    var resultColor = resultColor_;

    this.save = function()
    {
        var params = 'dataElementUid=' + dataElementUid;
            params += '&programStageInstanceId=' + getFieldValue( 'programStageInstanceId' );

        params += '&providedElsewhere=';

		if( byId( providedElsewhereId ) != null )
			params += byId( providedElsewhereId ).checked;
		
		params += '&value=';

        if ( value != '' )
            params += htmlEncode( value );

        $.ajax({
           type: "POST",
           url: "saveValue.action",
           data: params,
           dataType: "xml",
           cache: false,
           success: function(result){
                handleResponse (result);
           },
           error: function(request) {
                handleHttpError (request);
           }
        });
    };
 
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );

        if ( code == 0 ) {
            markValue( resultColor );
        }
        else {
            if ( value != "" ) {
                markValue( ERROR );
                window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
            }
            else {
                markValue( resultColor );
            }
        }
    }
 
    function handleHttpError( errorCode )
    {
        if( getProgramType() == 3 && DAO.store ) {
            var data = {
                providedElsewhere: byId( providedElsewhereId ) != null ? byId( providedElsewhereId ).checked : false,
                value: value != '' ? htmlEncode( value ) : value
            };

            var dataValueKey = $( '#programStageInstanceId' ).val();
            var key = dataElementUid;

            DAO.store.get( 'dataValues', dataValueKey ).done( function ( obj ) {
                if ( !obj ) {
                    obj = {};
                    obj.executionDate = {};
                    obj.executionDate.programId = $( '#programId' ).val();
                    obj.executionDate.programStageInstanceId = dataValueKey;

                    var orgUnitId = $( '#orgunitId' ).val();
                    obj.executionDate.organisationUnitId = orgUnitId;
                    obj.executionDate.organisationUnit = organisationUnits[orgUnitId].n;
                }

                obj.executionDate.executionDate = $( '#executionDate' ).val();
                obj.executionDate.completed = $( '#completed' ).val();

                if ( !obj.values ) {
                    obj.values = {};
                }

                obj.id = dataValueKey;
                data.value = decodeURI(data.value);
                obj.values[key] = data;

                this.set( 'dataValues', obj );
                markValue( resultColor );
            } );
        } else {
            markValue( ERROR );
            window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
        }
    }
 
    function markValue( color )
    {
		var programStageUid = getProgramStageUid();
		var element = byId( programStageUid + "-" + dataElementUid + '-val' );
        element.style.backgroundColor = color;
    }
}

function FacilitySaver( dataElementId_, providedElsewhere_, resultColor_ )
{
    var programStageInstanceId = getFieldValue('programStageInstanceId');
	var dataElementUid = dataElementId_;
	var providedElsewhere = providedElsewhere_;
    var resultColor = resultColor_;

    this.save = function()
    {
		var params  = 'dataElementId=' + dataElementUid;
			params += '&providedElsewhere=' + providedElsewhere ;
			params += '&programStageInstanceId=' + programStageInstanceId;
		$.ajax({
			   type: "POST",
			   url: "saveProvidingFacility.action",
			   data: params,
			   dataType: "xml",
               cache: false,
			   success: function(result){
					handleResponse (result);
			   },
			   error: function(request,status,errorThrown) {
					handleHttpError (request);
			   }
			});
    };

    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
		
        if ( code != 0 )
        {
            window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
        }
    }

    function handleHttpError( errorCode )
    {
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }
}

function ExecutionDateSaver( programId_, programStageInstanceId_, executionDate_, resultColor_ )
{
    var programId = programId_;
    var programStageInstanceId = programStageInstanceId_;
    var executionDate = executionDate_;
    var resultColor = resultColor_;

    this.save = function()
    {
		var params  = "executionDate=" + executionDate;
			params += "&programId=" + programId;
			params += "&programStageInstanceId=" + programStageInstanceId;
			
		$.ajax({
			   type: "POST",
			   url: "saveExecutionDate.action",
			   data: params,
			   dataType: "json",
               cache: false,
			   success: function( json ){
					var selectedProgramStageInstance = jQuery( '#' + prefixId + getFieldValue('programStageInstanceId') );
					var box = jQuery(".stage-object-selected");
					var boxName = box.attr('psname') + "\n" + executionDate;
					box.val( boxName );
					box.attr( 'reportDate', executionDate );
					box.css('border-color', COLOR_LIGHTRED);
					box.css('background-color', COLOR_LIGHT_LIGHTRED);
					disableCompletedButton(false);
					setFieldValue( 'programStageUid', selectedProgramStageInstance.attr('psuid') );
					
					var fieldId = "value_" + programStageInstanceId + "_date";
					jQuery("#" + fieldId).val(executionDate);
					jQuery("#" + fieldId).css('background-color', SUCCESS_COLOR);
					jQuery('#executionDate').val(executionDate);
					jQuery("#org_" + programStageInstanceId ).html(getFieldValue("orgunitName"));
					showById('inputCriteriaDiv');
					handleResponse (json);
			   },
			   error: function(request,status,errorThrown) {
					handleHttpError (request);
			   }
			});
    };

    function handleResponse( json )
    {
		if(json.response=='success')
		{
            markValue( resultColor );
			if( getFieldValue('programStageInstanceId' )=='' )
			{
				var programStageInstanceId = json.message;
				setFieldValue('programStageInstanceId', programStageInstanceId);
				loadDataEntry( programStageInstanceId );
			}
			else
			{
				showById('entryFormContainer');
				showById('dataEntryFormDiv');
				showById('entryForm');
				showById('entryPostComment');
			}
        }
        else
        {
            if( executionDate != "")
            {
                markValue( ERROR );
                showWarningMessage( i18n_invalid_date );
            }
            else
            {
                markValue( ERROR );
				showWarningMessage( i18n_please_enter_report_date );
            }
			hideById('dataEntryFormDiv');
			hideById('inputCriteriaDiv');
        }
    }

    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }

    function markValue( color )
    {
        var element = document.getElementById( 'executionDate' );
           
        element.style.backgroundColor = color;
    }
}

//-----------------------------------------------------------------
//
//-----------------------------------------------------------------

function toggleContentForReportDate( show ) {
    if( show ) {
        jQuery("#entryForm").show();
        showById('entryPostComment');
    } else {
        jQuery("#entryForm").hide();
        hideById('entryPostComment');
    }
}

function doComplete( isCreateEvent ) {
    if( getFieldValue('validCompleteOnly') == "true" ) {
		$('#loading-bar').show();
		$('#loading-bar').dialog({
			modal:true,
			width: 330
		});

		$("#loading-bar").siblings(".ui-dialog-titlebar").hide();

		$.get( 'validateProgram.action', 
			{
				programStageInstanceId: jQuery('.stage-object-selected').attr('id').split('_')[1]
			}).done(function(html){
            $("#loading-bar").dialog("close");
            $('#validateProgramDiv').html(html);
            if( getFieldValue('violateValidation') == 'true' ) {
                $('#validateProgramDiv').dialog({
                    title: i18n_violate_validation,
                    maximize: true,
                    closable: true,
                    modal: true,
                    overlay: {background: '#000000', opacity: 0.1},
                    width: 800,
                    height: 450
                });
            }
            else {
                hideById('validateProgramDiv');
                runCompleteEvent(isCreateEvent);
            }
        }).fail(function() {
            $("#loading-bar").dialog("close");
            hideById('validateProgramDiv');
            runCompleteEvent(isCreateEvent);
        });
    }
    else {
        runCompleteEvent(isCreateEvent);
    }
}

function runCompleteEvent( isCreateEvent ) {
    var flag = false;

    $("#dataEntryFormDiv input[name='entryfield'],select[name='entryselect']").each(function() {
        $(this).parent().removeClass("errorCell");

        var arrData = $(this).attr('data').replace('{', '').replace('}', '').replace(/'/g, "").split(',');
        var data = [];

        $.each(arrData, function() {
            var values = this.split(':');
            values = $.trimArray(values);
            data[values[0]] = values[1];
        });

        var compulsory = data['compulsory'];

        if( compulsory == 'true' && ( !$(this).val() || $(this).val() == "undefined" ) ) {
            flag = true;
            $(this).parent().addClass("errorCell");
        }
    });

    if( flag ) {
        alert(i18n_error_required_field);
        return;
    } else {
        if( confirm(i18n_complete_confirm_message) ) {
            $.ajax({
                url: 'completeDataEntry.action',
                dataType: 'json',
                cache: false,
                data: {
                    programStageInstanceId: getFieldValue( 'programStageInstanceId' )
                },
                type: 'POST'
            } ).done(function(json) {
                $(".stage-object-selected").css('border-color', COLOR_GREEN);
                $(".stage-object-selected").css('background-color', COLOR_LIGHT_GREEN);

                var irregular = $('#entryFormContainer [name=irregular]').val();
                var displayGenerateEventBox = $('#entryFormContainer [name=displayGenerateEventBox]').val();
                var programInstanceId = $('#entryFormContainer [id=programInstanceId]').val();

                if( ( irregular == 'true' && displayGenerateEventBox == "true" )
                    || getFieldValue('allowGenerateNextVisit') == 'true' ) {
                    var programStageUid = getProgramStageUid();
                    showCreateNewEvent(programInstanceId, programStageUid);
                }

                if( getProgramType() == '2' || json.response == 'programcompleted' ) {
                    var completedRow = $('#td_' + programInstanceId).html();
                    $('#completedList').append('<option value="' + programInstanceId + '">' + getInnerHTML('infor_' + programInstanceId) + '</option>');
                }

                var blocked = $('#entryFormContainer [id=blockEntryForm]').val();
                if( blocked == 'true' ) {
                    blockEntryForm();
                }

                var remindCompleted = $('#entryFormContainer [id=remindCompleted]').val();

                if( remindCompleted == 'true' ) {
                    unenrollmentForm(programInstanceId, 1);
                }

                disableCompletedButton(true);

                var eventBox = jQuery('#ps_' + getFieldValue('programStageInstanceId'));
                eventBox.attr('status', 1);
                resetActiveEvent(eventBox.attr("pi"));

                hideLoader();

                if( isCreateEvent ) {
                    showAddEventForm(isCreateEvent);
                }
            } ).fail(function() {
                if ( getProgramType() == 3 ) {
                    var programStageInstanceId = getFieldValue('programStageInstanceId');

                    if( window.DAO && window.DAO.store ) {
                        $(".stage-object-selected").css('border-color', COLOR_GREEN);
                        $(".stage-object-selected").css('background-color', COLOR_LIGHT_GREEN);

                        DAO.store.get('dataValues', programStageInstanceId).done(function( obj ) {
                            if( !obj ) {
                                return;
                            }

                            obj.executionDate.completed = 'true';

                            DAO.store.set('dataValues', obj).done(function() {
                                var blocked = $('#entryFormContainer [id=blockEntryForm]').val();

                                if( blocked == 'true' ) {
                                    blockEntryForm();
                                }

                                disableCompletedButton(true);
                                hideLoader();

                                if( isCreateEvent ) {
                                    showAddEventForm(isCreateEvent);
                                }
                            });
                        });
                    }
                }
            });
		}
    }
}

function doUnComplete( isCreateEvent )
{	
	if( confirm(i18n_incomplete_confirm_message) )
	{
        $.ajax({
            url: 'uncompleteDataEntry.action',
            dataType: 'json',
            cache: false,
            data: {
                programStageInstanceId: getFieldValue( 'programStageInstanceId' )
            },
            type: 'POST'
        } ).done(function(json) {
            $(".stage-object-selected").css('border-color', COLOR_LIGHTRED);
            $(".stage-object-selected").css('background-color', COLOR_LIGHT_LIGHTRED);
            unblockEntryForm();
            disableCompletedButton(false);
            var eventBox = $('#ps_' + getFieldValue('programStageInstanceId'));
            eventBox.attr('status',2);
            resetActiveEvent( eventBox.attr("pi") );
        }).fail(function() {
            if ( getProgramType() == 3 ) {
                var programStageInstanceId = getFieldValue( 'programStageInstanceId' );

                if ( window.DAO && window.DAO.store ) {
                    DAO.store.get( 'dataValues', programStageInstanceId ).done( function ( obj ) {
                        if(!obj) {
                            return;
                        }

                        obj.executionDate.completed = 'false';
                        DAO.store.set( 'dataValues', obj );
                    } );
                }

                unblockEntryForm();
                disableCompletedButton(false);
            }
        });
	}
}

function blockEntryForm()
{
	jQuery("#entryFormContainer :input").each(function()
	{
		disable($(this).attr('id'));
		$(this).attr('disabled','disabled');
	});
	jQuery("#entryFormContainer").find(".ui-combobox").each(function()
	{
		jQuery(this).addClass('hidden');
	});
	jQuery('.auto-field').removeClass('optionset');
	jQuery('.date-field').each(function(){
		var id = jQuery(this).attr('id');
		jQuery('#delete_' + id ).hide();
	});
	jQuery('.date-field').removeClass('datefield');
	enable('uncompleteBtn');
}

function unblockEntryForm()
{
	jQuery("#entryFormContainer :input").each(function()
	{
		enable($(this).attr('id'));
		$(this).attr('disabled',false);
	});
	jQuery("#entryFormContainer").find(".ui-combobox").each(function()
	{
		jQuery(this).removeClass('hidden');
	});
	jQuery('.auto-field').addClass('optionset');
	jQuery('.date-field').each(function(){
		var id = jQuery(this).attr('id');
		jQuery('#delete_' + id ).show();
	});
	jQuery('.date-field').addClass('datefield');
}

TOGGLE = {
    init : function() {
        $(".togglePanel").each(function(){
            $(this).next("table:first").addClass("sectionClose");
            $(this).addClass("close");
            $(this).click(function(){
                var table = jQuery(this).next("table:first");
                if( table.hasClass("sectionClose")){
                    table.removeClass("sectionClose").addClass("sectionOpen");
                    $(this).removeClass("close").addClass("open");
                    window.scroll(0,$(this).position().top);
                }else if( table.hasClass("sectionOpen")){
                    table.removeClass("sectionOpen").addClass("sectionClose");
                    $(this).removeClass("open").addClass("close");
                }
            });
        });
    }
};

function checkAndSetCheckbox( $field, value ) {
    if( $field.attr('type') === 'checkbox' ) {
        if( value === "true" || value === true ) {
            $field.attr('checked', true);
        } else {
            $field.removeAttr('checked');
        }
    }
}

function checkAndSetRadio( $field, value ) {
    if( $field.attr('type') === 'radio' ) {
        var $fields = $("." + $field.attr('id'));

        $.each($fields, function() {
            var $f = $(this);

            if( $.trim(value) === $.trim($f.val()) ) {
                $f.attr("checked", true);
            }
        });

        return true;
    }

    return false;
}

function loadProgramStageInstance( programStageInstanceId, always ) {
    $( "#programStageInstanceId" ).val( programStageInstanceId );
    $( "#entryFormContainer input[id='programStageInstanceId']" ).val( programStageInstanceId );

    if(window.DAO !== undefined && window.DAO.store !== undefined ) {
        DAO.store.get( 'dataValues', programStageInstanceId ).done( function ( obj ) {
            if(obj ) {
                if(obj.values !== undefined) {
                    _.each( _.keys(obj.values), function(key, idx) {
                        var fieldId = getProgramStageUid() + '-' + key + '-val';
                        var field = $('#' + fieldId);

                        if ( field ) {
                            var value = obj.values[key].value;

                            if( !checkAndSetRadio(field, value) ) {
                                field.val(decodeURI(value));
                                checkAndSetCheckbox(field, value);
                            }
                        }
                    });
                }

                if ( obj.coordinate !== undefined ) {
                    $( '#longitude' ).val( obj.coordinate.longitude );
                    $( '#latitude' ).val( obj.coordinate.latitude );
                }

                if(obj.executionDate) {
                    $( "input[id='executionDate']" ).val( obj.executionDate.executionDate );
                    $("#entryFormContainer input[id='completed']").val(obj.executionDate.completed);
                    $( '#entryForm' ).removeClass( 'hidden' ).addClass( 'visible' );
                    $( '#inputCriteriaDiv' ).removeClass( 'hidden' );
                }

                $('#commentInput').attr('disabled', true);
                $('#commentButton').attr('disabled', true);
                $('#validateBtn').attr('disabled', true);

                if( always ) always();
            } else {
                loadProgramStageFromServer(programStageInstanceId).done(function() {
                    if( always ) always();
                });
            }

        });
    } else {
        loadProgramStageFromServer(programStageInstanceId).done(function() {
            if( always ) always();
        });
    }
}

function loadProgramStageFromServer( programStageInstanceId ) {
    return $.ajax({
        url: 'getProgramStageInstance.action',
        cache: false,
        data: {
            'programStageInstanceId': programStageInstanceId
        },
        type: 'GET',
        dataType: 'json'
    } ).done(function(data) {
        $( "#programStageInstanceId" ).val( data.id );
        $( "#entryFormContainer input[id='programStageInstanceId']" ).val( data.id );
        $( "#entryFormContainer input[id='incidentDate']" ).val( data.programInstance.dateOfIncident );
        $( "#entryFormContainer input[id='programInstanceId']" ).val( data.programInstance.id );
        $( "#entryFormContainer input[id='irregular']" ).val( data.programStage.irregular );
        $( "#entryFormContainer input[id='displayGenerateEventBox']" ).val( data.programStage.displayGenerateEventBox );
        $( "#entryFormContainer input[id='completed']" ).val( data.completed );
        $( "#entryFormContainer input[id='programStageId']" ).val( data.programStage.id  );
        $( "#entryFormContainer input[id='programStageUid']" ).val( data.programStage.uid  );
        $( "#entryFormContainer input[id='programId']" ).val( data.program.id );
        $( "#entryFormContainer input[id='validCompleteOnly']" ).val( data.programStage.validCompleteOnly );
        $( "#entryFormContainer input[id='currentUsername']" ).val( data.currentUsername );
        $( "#entryFormContainer input[id='blockEntryForm']" ).val( data.programStage.blockEntryForm );
        $( "#entryFormContainer input[id='remindCompleted']" ).val( data.programStage.remindCompleted );
        $( "#entryFormContainer input[id='displayOptionSetAsRadioButton']" ).val( data.displayOptionSetAsRadioButton );
        $( "#entryFormContainer input[id='allowGenerateNextVisit']" ).val( data.programStage.allowGenerateNextVisit );

        $( "input[id='dueDate']" ).val( data.dueDate );
        $( "input[id='executionDate']" ).val( data.executionDate );
        $( "#commentInput" ).val( data.comment );
        $( "#commentInput" ).height(data.comment.split('\n').length * 15  + 12);

        if ( data.program.type != '1' ) {
            hideById( 'newEncounterBtn' );
        }

        if ( data.program.type == '1' && data.programInstance.status == '1' ) {
            var blockEntry = getFieldValue('blockEntryForm');
            if( blockEntry == 'true' ){
                blockEntryForm();
            }
        }

        if(data.executionDate) {
            $( '#executionDate' ).val(data.executionDate);
            $( '#entryForm' ).removeClass( 'hidden' ).addClass( 'visible' );
            $( '#inputCriteriaDiv' ).removeClass( 'hidden' );
        }

        if ( data.programStage.captureCoordinates ) {
            $( '#longitude' ).val( data.longitude );
            $( '#latitude' ).val( data.latitude );
        }

        _.each( data.dataValues, function ( value, key ) {
            var fieldId = getProgramStageUid() + '-' + key + '-val';
            var field = $('#' + fieldId);

            if ( field ) {
                var value = value.value;

                if( !checkAndSetRadio(field, value) ) {
                    field.val(decodeURI(value));
                    checkAndSetCheckbox(field, value);
                }
            }
        } );

        $('#commentInput').removeAttr('disabled');
        $('#commentButton').removeAttr('disabled');
        $('#validateBtn').removeAttr('disabled');
    } );
}

function entryFormContainerOnReady()
{
	var currentFocus = undefined;
    var programStageInstanceId = getFieldValue( 'programStageInstanceId' );
	
    loadProgramStageInstance( programStageInstanceId, function() {
        if( jQuery("#entryFormContainer") ) {
            // Display entry form if excution-date is not null
            if ( jQuery( "#executionDate" ).val() == '' ) {
                hideById( 'entryForm' );
            }
            else if ( jQuery( "#executionDate" ).val() != '' ) {
                toggleContentForReportDate( true );
            }

            // Set buttons by completed-status of program-stage-instance
            var completed = $( "#entryFormContainer input[id='completed']" ).val();
            var blockEntry = $( "#entryFormContainer input[id='blockEntryForm']" ).val();

            if ( completed == 'true' ) {
                disable( 'completeBtn' );
                enable( 'uncompleteBtn' );
                if ( blockEntry == 'true' ) {
                    blockEntryForm();
                }
            }
            else {
                enable( 'completeBtn' );
                disable( 'uncompleteBtn' );
            }

            jQuery( "input[name='entryfield'],select[name='entryselect']" ).each( function () {
                jQuery( this ).focus( function () {
                    currentFocus = this;
                } );

                jQuery( this ).addClass( "inputText" );
            } );

            TOGGLE.init();

            jQuery( "#entryForm :input" ).each( function () {
                if ( jQuery( this ).attr( 'options' ) != null 
					&& jQuery( this ).attr( 'options' ) == 'true' 
					&& ( jQuery( this ).attr( 'disabled' ) == null  
						|| jQuery( this ).attr( 'disabled' ) != 'disabled' ) ){
                    autocompletedField( jQuery( this ).attr( 'id' ) );
                }
                else if ( jQuery( this ).attr( 'username' ) != null && jQuery( this ).attr( 'username' ) == 'true' ) {
                    autocompletedUsernameField( jQuery( this ).attr( 'id' ) );
                }
            } );
        }
    });
}

//------------------------------------------------------
// Run validation
//------------------------------------------------------

function runValidation()
{
	$('#loading-bar').show();
	$('#loading-bar').dialog({
		modal:true,
		width: 330
	});
	$("#loading-bar").siblings(".ui-dialog-titlebar").hide(); 
	
	var programStageInstanceId = jQuery('.stage-object-selected').attr('id').split('_')[1];
	$('#validateProgramDiv' ).load( 'validateProgram.action',
		{
			programStageInstanceId: programStageInstanceId
		},
		function(){
			$( "#loading-bar" ).dialog( "close" );
			
			$('#validateProgramDiv' ).dialog({
				title: i18n_violate_validation,
				maximize: true, 
				closable: true,
				modal:true,
				overlay:{background:'#000000', opacity:0.1},
				width: 800,
				height: 450
			});
		});
}

function searchOptionSet( uid, query, success ) {
    if(window.DAO !== undefined && window.DAO.store !== undefined ) {
        DAO.store.get( 'optionSets', uid ).done( function ( obj ) {
            if(obj) {
                var options = [];

                if(query == null || query == "") {
                    options = obj.optionSet.options.slice(0, MAX_DROPDOWN_DISPLAYED-1);
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
        url: "getOptions.action?id=" + uid + "&query=" + query,
        dataType: "json",
        cache: false,
        success: function ( data ) {
            success( $.map( data.options, function ( item ) {
                return {
                    label: item.o,
                    id: item.o
                };
            } ) );
        }
    } );
}

function autocompletedField( idField )
{
	var input = jQuery( "#" +  idField );
	if(input.attr('options')=='no')
	{
		return;
	}
	
	input.parent().width( input.width() + 50 );
	var dataElementUid = input.attr('id').split('-')[1];
	
	input.autocomplete({
		delay: 0,
		minLength: 0,
		source: function( request, response ){
            searchOptionSet( input.attr('optionset'), input.val(), response );
		},
		minLength: 0,
		select: function( event, ui ) {
			var fieldValue = ui.item.value;

			if ( !dhis2.trigger.invoke( "caseentry-value-selected", [dataElementUid, fieldValue] ) ) {
				input.val( "" );
				return false;
			}

			input.val( fieldValue );
			if ( !unSave ) {
				saveVal( dataElementUid );
			}
			input.autocomplete( "close" );
		},
		change: function( event, ui ) {
			if ( !ui.item ) {
				var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $(this).val() ) + "$", "i" ),
					valid = false;
				if ( !valid ) {
					$( this ).val( "" );
					if(!unSave)
						saveVal( dataElementUid );
					input.data( "autocomplete" ).term = "";
					return false;
				}
			}
		}
	})
	.addClass( "ui-widget" );
	
	input.data( "autocomplete" )._renderItem = function( ul, item ) {
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
		.button({
			icons: {
				primary: "ui-icon-triangle-1-s"
			},
			text: false
		})
		.addClass('small-button')
		.click(function() {
			if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
				input.autocomplete( "close" );
				return;
			}
			$( this ).blur();
			input.autocomplete( "search", "" );
			input.focus();
		});
}

function searchUsername( query, success ) {
    if(window.DAO !== undefined && window.DAO.usernames !== undefined ) {
        DAO.usernames.fetch('usernames', function(store, arr) {
            if ( arr.length > 0 ) {
                var obj = arr[0];
                var usernames = [];

                if(query == null || query == "") {
                    delete obj['key'];
                    usernames = obj.slice(0, MAX_DROPDOWN_DISPLAYED-1);
                } else {
                    query = query.toLowerCase();

                    _.each(obj, function(item, idx) {
                        if ( item.toLowerCase().indexOf( query ) != -1 ) {
                            usernames.push(item);
                        }
                    });
                }

                success( $.map( usernames, function ( item ) {
                    return {
                        label: item,
                        id: item
                    };
                } ) );
            } else {
                getUsername( query, success );
            }
        } );
    } else {
        getUsername( query, success );
    }
}

function getUsername( query, success ) {
    return $.ajax( {
        url: "getUsernameList.action?query=" + query,
        dataType: "json",
        cache: false,
        success: function ( data ) {
            success( $.map( data.usernames, function ( item ) {
                return {
                    label: item.u,
                    id: item.u
                };
            } ) );
        }
    } );
}

function autocompletedUsernameField( idField )
{
	var input = jQuery( "#" +  idField );
	input.parent().width( input.width() + 50 );
	var dataElementUid = input.attr('id').split('-')[1];
	
	input.autocomplete({
		delay: 0,
		minLength: 0,
		source: function( request, response ){
            searchUsername( input.val(), response );
		},
		minLength: 0,
		select: function( event, ui ) {
			var fieldValue = ui.item.value;
			
			if ( !dhis2.trigger.invoke( "caseentry-value-selected", [dataElementUid, fieldValue] ) ) {
				input.val( "" );
				return false;
			}
			
			input.val( fieldValue );			
			if ( !unSave ) {
				saveVal( dataElementUid );
			}
			input.autocomplete( "close" );
		},
		change: function( event, ui ) {
			if ( !ui.item ) {
				var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $(this).val() ) + "$", "i" ),
					valid = false;
				if ( !valid ) {
					$( this ).val( "" );
					if(!unSave)
						saveVal( dataElementUid );
					input.data( "autocomplete" ).term = "";
					return false;
				}
			}
		}
	})
	.addClass( "ui-widget" );
	
	input.data( "autocomplete" )._renderItem = function( ul, item ) {
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
		.button({
			icons: {
				primary: "ui-icon-triangle-1-s"
			},
			text: false
		})
		.addClass('small-button')
		.click(function() {
			if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
				input.autocomplete( "close" );
				return;
			}
			$( this ).blur();
			input.autocomplete( "search", "" );
			input.focus();
		});
}

function filterOnSection()
{
    var value = $( '#filterDataSetSection option:selected' ).val();
    
    if ( value == 'all' )
    {
        $( '.formSection' ).show();
    }
    else
    {
        $( '.formSection' ).hide();
        $( '#sec_' + value ).show();
    }
}
