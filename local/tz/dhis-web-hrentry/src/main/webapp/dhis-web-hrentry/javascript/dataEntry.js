function viewPrgramStageRecords( programStageInstanceId ) 
{
	var url = 'viewProgramStageRecords.action?programStageInstanceId=' + programStageInstanceId;
	$('#contentDataRecord').dialog('destroy').remove();
    $('<div id="contentDataRecord">' ).load(url).dialog({
        title: 'ProgramStage',
		maximize: true, 
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 800,
        height: 400
    });
}


function organisationUnitSelected( orgUnits )
{
    window.location.href = 'dataEntrySelect.action';
}

selection.setListenerFunction( organisationUnitSelected );

//-----------------------------------------------------------------------------
//Search Patient
//-----------------------------------------------------------------------------

function validateSearch()
{	
	
    var url = 'validateSearch.action?' +
    'searchText=' + getFieldValue( 'searchText' );
	
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( searchValidationCompleted );
    request.send( url );

    return false;
}

function searchValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
	
    if ( type == 'success' )
    {
        var form = document.getElementById( 'searchForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_searching_patient_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

//-----------------------------------------------------------------------------
//View details
//-----------------------------------------------------------------------------

function showPatientDetails( patientId )
{
    var request = new Request();
    request.setResponseTypeXML( 'patient' );
    request.setCallbackSuccess( patientReceived );
    request.send( 'getPatient.action?id=' + patientId );
}

function patientReceived( patientElement )
{   
	// ----------------------------------------------------------------------------
	// Get common-information
    // ----------------------------------------------------------------------------
	
	var id = patientElement.getElementsByTagName( "id" )[0].firstChild.nodeValue;
	var fullName = patientElement.getElementsByTagName( "fullName" )[0].firstChild.nodeValue;   
	var gender = patientElement.getElementsByTagName( "gender" )[0].firstChild.nodeValue;   
	var dobType = patientElement.getElementsByTagName( "dobType" )[0].firstChild.nodeValue;   
	var birthDate = patientElement.getElementsByTagName( "dateOfBirth" )[0].firstChild.nodeValue;   
	var bloodGroup= patientElement.getElementsByTagName( "bloodGroup" )[0].firstChild.nodeValue;   
    
	var commonInfo =  '<strong>'  + i18n_id + ':</strong> ' + id + "<br>" 
					+ '<strong>' + i18n_full_name + ':</strong> ' + fullName + "<br>" 
					+ '<strong>' + i18n_gender + ':</strong> ' + gender+ "<br>" 
					+ '<strong>' + i18n_dob_type + ':</strong> ' + dobType+ "<br>" 
					+ '<strong>' + i18n_date_of_birth + ':</strong> ' + birthDate+ "<br>" 
					+ '<strong>' + i18n_blood_group  + ':</strong> ' + bloodGroup;

	setInnerHTML( 'commonInfoField', commonInfo );
	
	// ----------------------------------------------------------------------------
	// Get identifier
    // ----------------------------------------------------------------------------
	
	var identifiers = patientElement.getElementsByTagName( "identifier" );   
    
    var identifierText = '';
	
	for ( var i = 0; i < identifiers.length; i++ )
	{		
		identifierText = identifierText + identifiers[ i ].getElementsByTagName( "identifierText" )[0].firstChild.nodeValue + '<br>';		
	}
	
	setInnerHTML( 'identifierField', identifierText );
	
	
	
	// ----------------------------------------------------------------------------
	// Get attribute
    // ----------------------------------------------------------------------------
	
	var attributes = patientElement.getElementsByTagName( "attribute" );   
    
    var attributeValues = '';
	
	for ( var i = 0; i < attributes.length; i++ )
	{	
		attributeValues = attributeValues + '<strong>' + attributes[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue  + ':  </strong>' + attributes[ i ].getElementsByTagName( "value" )[0].firstChild.nodeValue + '<br>';		
	}
	attributeValues = ( attributeValues.length == 0 ) ? i18n_none : attributeValues;
	setInnerHTML( 'attributeField', attributeValues );
    
	// ----------------------------------------------------------------------------
	// Get programs
    // ----------------------------------------------------------------------------
	
    var programs = patientElement.getElementsByTagName( "program" );   
    
    var programName = '';
	
	for ( var i = 0; i < programs.length; i++ )
	{		
		programName = programName + programs[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue + '<br>';		
	}
	
	programName = ( programName.length == 0 ) ? i18n_none : programName;
	setInnerHTML( 'programField', programName );
   
	// ----------------------------------------------------------------------------
	// Show details
    // ----------------------------------------------------------------------------
	
    showDetails();
}

//------------------------------------------------------------------------------
// Save Execution Date
//------------------------------------------------------------------------------

function saveExecutionDate( programStageInstanceId, programStageInstanceName )
{
    var field = document.getElementById( 'executionDate' );
	
    field.style.backgroundColor = '#ffffcc';
	
    var executionDateSaver = new ExecutionDateSaver( programStageInstanceId, field.value, '#ccffcc' );
    executionDateSaver.save();
	
    if( !jQuery("#entryFormContainer").is(":visible") )
    {
        toggleContentForReportDate(true);
    }
}

//-----------------------------------------------------------------------------
// Date Saver objects
//-----------------------------------------------------------------------------

function ExecutionDateSaver( programStageInstanceId_, executionDate_, resultColor_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ffcc00';
	
    var programStageInstanceId = programStageInstanceId_;
    var executionDate = executionDate_;
    var resultColor = resultColor_;

    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        request.send( 'saveExecutionDate.action?programStageInstanceId=' + programStageInstanceId + '&executionDate=' + executionDate );
    };

    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        if ( code == 0 )
        {
            markValue( resultColor );
        }
        else
        {
            if(executionDate != "")
            {
                markValue( ERROR );
                window.alert( i18n_invalid_date );
            }
            else
            {
                markValue( resultColor );
            }
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

//------------------------------------------------------------------------------
//Save Execution Date
//------------------------------------------------------------------------------

function saveDateValue( dataElementId, dataElementName )
{
    var field = document.getElementById( 'value[' + dataElementId + '].date' );
    var providedByAnotherFacility = document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' ).checked;
 
    var dateSaver = new DateSaver( dataElementId, field.value, providedByAnotherFacility, '#ccffcc' );
    dateSaver.save();
	
}

//-----------------------------------------------------------------------------
//Date Saver objects
//-----------------------------------------------------------------------------

function DateSaver( dataElementId_, value_, providedByAnotherFacility_, resultColor_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ffcc00';
	
    var dataElementId = dataElementId_;
    var value = value_;
    var providedByAnotherFacility = providedByAnotherFacility_;
    var resultColor = resultColor_;

    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        request.send( 'saveDateValue.action?dataElementId=' + dataElementId + '&value=' + value + '&providedByAnotherFacility=' + providedByAnotherFacility );
    };

    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        if ( code == 0 )
        {
            markValue( resultColor );
        }
        else if(code == 1)
        {
            if(value != "")
            {
                var validationElement = rootElement.getElementsByTagName( 'validations' )[0];
                var dataelementList = rootElement.getElementsByTagName( 'validation' );
                var message = '';
                for ( var i = 0; i < dataelementList.length; i++ )
                {
                    message += "\n - " + dataelementList[i].firstChild.nodeValue;
                }

                markValue( ERROR );
                window.alert( i18n_violate_validation + message);
            }
            else
            {
                markValue( resultColor );
            }
        }
		else if(code == 2)
        {
			markValue( ERROR );
            window.alert( i18n_invalid_date + ":\n" + rootElement.getElementsByTagName( 'message' )[0].firstChild.nodeValue );
		}
        else
        {
            if(value != "")
            {
                markValue( ERROR );
                window.alert( i18n_invalid_date );
            }
            else
            {
                markValue( resultColor );
            }
        }
    }

    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }

    function markValue( color )
    {
        var element = document.getElementById( 'value[' + dataElementId + '].date' );
        
        element.style.backgroundColor = color;
    }
}

function DateSaverCustom( programStageId, dataElementId_, value_, providedByAnotherFacility_, resultColor_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ffcc00';
	
    var dataElementId = dataElementId_;
    var value = value_;
    var providedByAnotherFacility = providedByAnotherFacility_;
    var resultColor = resultColor_;

    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        request.send( 'saveDateValue.action?dataElementId=' + dataElementId + '&value=' + value + '&providedByAnotherFacility=' + providedByAnotherFacility );
    };

    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        if ( code == 0 )
        {
            markValue( resultColor );
        }
        else
        {
            if(value != "")
            {
                markValue( ERROR );
                window.alert( i18n_invalid_date );
            }
            else
            {
                markValue( resultColor );
            }
        }
    }

    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }

    function markValue( color )
    {
        var element = document.getElementById( 'value[' + programStageId + '].date.value[' + dataElementId + '].date' );
        
        element.style.backgroundColor = color;
    }
}

//------------------------------------------------------------------------------
//Save providing facility
//------------------------------------------------------------------------------

function updateProvidingFacility( dataElementId, checkedBox )
{
    checkedBox.style.backgroundColor = '#ffffcc';
    var providedByAnotherFacility = document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' ).checked;
	
    var checkBoxSaver = new CheckBoxSaver( dataElementId, providedByAnotherFacility, '#ccffcc' );
    checkBoxSaver.save();
    
}

function updateProvidingFacilityCustom( programStageId, dataElementId, checkedBox )
{
    //	checkedBox.style.backgroundColor = '#ffffcc';
    var providedByAnotherFacility = checkedBox.checked;
    var checkBoxSaver = new CustomCheckBoxSaver( programStageId, dataElementId, providedByAnotherFacility, '#ccffcc' );
    checkBoxSaver.save();
    
}


//-----------------------------------------------------------------------------
//Saver objects - checkbox
//-----------------------------------------------------------------------------

function CheckBoxSaver( dataElementId_, providedByAnotherFacility_, resultColor_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';
	
    var dataElementId = dataElementId_;
    var providedByAnotherFacility = providedByAnotherFacility_;
    var resultColor = resultColor_;

    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponseCheckBox );
        request.setCallbackError( handleHttpErrorCheckBox );
        request.setResponseTypeXML( 'status' );
        request.send( 'saveProvidingFacility.action?dataElementId=' + dataElementId + '&providedByAnotherFacility=' + providedByAnotherFacility );
    };

    function handleResponseCheckBox( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
   
        if ( code == 0 )
        {
            markValue( resultColor );
        }
        else
        {
            markValue( ERROR );
            window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
        }
    }

    function handleHttpErrorCheckBox( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }

    function markValue( color )
    {
        var element = document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' );
        element.style.backgroundColor = color; //need to find another option as it is difficult to set background color for checkbox
    }
}

function CustomCheckBoxSaver( programStageId, dataElementId_, providedByAnotherFacility_, resultColor_ )
{
    var SUCCESS = 'success';
    var ERROR = '#error';
	
    var dataElementId = dataElementId_;
    var providedByAnotherFacility = providedByAnotherFacility_;
    var resultColor = resultColor_;

    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponseCheckBox );
        request.setCallbackError( handleHttpErrorCheckBox );
        request.setResponseTypeXML( 'status' );
        request.send( 'saveProvidingFacility.action?dataElementId=' + dataElementId + '&providedByAnotherFacility=' + providedByAnotherFacility );
    };

    function handleResponseCheckBox( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        if ( code == 0 )
        {
            markValue( SUCCESS );
        }
        else
        {
            markValue( ERROR );
            window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
        }
    }

    function handleHttpErrorCheckBox( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }

    function markValue( result )
    {
        if( result == SUCCESS )
        {
            jQuery('label[for="'+programStageId+'_'+dataElementId+'_facility"]').toggleClass('checked');
        }
        else if( result == ERROR )
        {
            jQuery('label[for="'+programStageId+'_'+dataElementId+'_facility"]').removeClass('checked');
            jQuery('label[for="'+programStageId+'_'+dataElementId+'_facility"]').addClass('error');
        }
    }
}

//------------------------------------------------------------------------------
//Save
//------------------------------------------------------------------------------

function saveValue( dataElementId, dataElementName )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value' );
    var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerHTML;
    var providedByAnotherFacility = document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' ).checked;
    
    field.style.backgroundColor = '#ffffcc';
    
    if( field.value != '' )
    {
        if ( type == 'int' || type == 'number' || type == 'positiveNumber' || type == 'negativeNumber' )
        {
            if (  type == 'int' && !isInt( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer + '\n\n' + dataElementName );

                field.select();
                field.focus();

                return;
            }
			else if (  type == 'number' && !isNumber( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';
                window.alert( i18n_value_must_number + '\n\n' + dataElementName );
                field.select();
                field.focus();

                return;
            } 
			else if (  type == 'positiveNumber' && !isPositiveNumber( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';
                window.alert( i18n_value_must_positive_number + '\n\n' + dataElementName );
                field.select();
                field.focus();

                return;
            } 
			else if (  type == 'negativeNumber' && !isNegativeNumber( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';
                window.alert( i18n_value_must_negative_number + '\n\n' + dataElementName );
                field.select();
                field.focus();

                return;
            }
        }
    	
    }
    
    var valueSaver = new ValueSaver( dataElementId, field.value, providedByAnotherFacility, '#ccffcc', '' );
    valueSaver.save();
    
}

function saveValueCustom( this_ )
{
    var data = jQuery( this_ ).metadata({
        type:'attr',
        name:'data'
    });
	
    var providedByAnotherFacility = jQuery('input#'+data.programStageId+'_'+data.dataElementId).attr("checked");
    
    this_.style.backgroundColor = '#ffffcc';
    
    if( this_.value != '' )
    {
        if( data.dataElementType == 'int' )
        {
            if ( !isInt( this_.value ))
            {
                this_.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer + '\n\n' + data.dataElementName );

                this_.select();
                this_.focus();

                return;
            }
        }
    	
    }
    
    var valueSaver = new CustomValueSaver( data.dataElementId, this_.value, providedByAnotherFacility, '#ccffcc', '' );
    valueSaver.setProgramStageId( data.programStageId );
    valueSaver.setOptionComboId(data.optionComboId);
    valueSaver.setType(data.dataElementType);
    valueSaver.save();
    
}

function saveChoice( dataElementId, selectedOption )
{
    selectedOption.style.backgroundColor = '#ffffcc';
	
    var providedByAnotherFacility = document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' ).checked;
 
    var valueSaver = new ValueSaver( dataElementId, selectedOption.options[selectedOption.selectedIndex].value, providedByAnotherFacility, '#ccffcc', selectedOption );
    valueSaver.save();
}
function saveChoiceCustom( programStageId, dataElementId, selectedOption )
{
    selectedOption.style.backgroundColor = '#ffffcc';
	
    var providedByAnotherFacility = document.getElementById( programStageId+'_'+dataElementId+'_facility' ).checked;
 
    var valueSaver = new CustomValueSaver( dataElementId, selectedOption.options[selectedOption.selectedIndex].value, providedByAnotherFacility, '#ccffcc', selectedOption );
    valueSaver.setProgramStageId( programStageId );
    valueSaver.setType(jQuery(selectedOption).metadata({
        type:"attr",
        name:"data"
    }).dataElementType);
    valueSaver.save();
}


//-----------------------------------------------------------------------------
//Saver objects
//-----------------------------------------------------------------------------

function ValueSaver( dataElementId_, value_, providedByAnotherFacility_, resultColor_, selectedOption_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';
	
    var dataElementId = dataElementId_;
    var value = value_;
    var providedByAnotherFacility = providedByAnotherFacility_;
    var resultColor = resultColor_;
    var selectedOption = selectedOption_;
	
	
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        request.send( 'saveValue.action?dataElementId=' + dataElementId + '&value=' + value + '&providedByAnotherFacility=' + providedByAnotherFacility );
    };
 
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        if ( code == 0 )
        {
            markValue( resultColor );
        }
        else
        {
            if(value!="")
            {
                markValue( ERROR );
                window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
            }
            else
            {
                markValue( resultColor );
            }
        }
    }
 
    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }
 
    function markValue( color )
    {
		
        var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerHTML;
		
        var element;
     
        if ( type == 'bool' )
        {
            element = document.getElementById( 'value[' + dataElementId + '].boolean' );
        }
        else if( type == 'date' )
        {
            element = document.getElementById( 'value[' + dataElementId + '].date' );
        }
        else if( selectedOption )
        {
            element = selectedOption;
        }
        else
        {
            element = document.getElementById( 'value[' + dataElementId + '].value' );
        }
             
        element.style.backgroundColor = color;
    }
}

function CustomValueSaver( dataElementId_, value_, providedByAnotherFacility_, resultColor_, selectedOption_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';
	
    var dataElementId = dataElementId_;
    var value = value_;
    var providedByAnotherFacility = providedByAnotherFacility_;
    var resultColor = resultColor_;
    var selectedOption = selectedOption_;
    var optionComboId ;
    var programStageId;
    var type;
	
    this.setType = function( type_ )
    {
        type = type_;
    }
	
    this.setOptionComboId =  function( optionComboId_ )
    {
        optionComboId = optionComboId_;
    }
	
    this.setProgramStageId = function( programStageId_ )
    {
        programStageId = programStageId_;
    }
	
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
		
        if( optionComboId )
        {
            request.send( 'saveValue.action?dataElementId=' + dataElementId + '&optionComboId=' + optionComboId + '&value=' + value + '&providedByAnotherFacility=' + providedByAnotherFacility );
        }
        else
        {
            request.send( 'saveValue.action?dataElementId=' + dataElementId + '&value=' + value + '&providedByAnotherFacility=' + providedByAnotherFacility );
        }
		
		
    };
 
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        if ( code == 0 )
        {
            markValue( resultColor );
        }
        else
        {
            if(value!="")
            {
                markValue( ERROR );
                window.alert( i18n_invalid_date );
            }
            else
            {
                markValue( resultColor );
            }
        }
    }
 
    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }
 
    function markValue( color )
    {
		
        var element;
     
        if ( type == 'bool' )
        {
            element = document.getElementById( 'value[' + programStageId + '].boolean:value[' + dataElementId + '].boolean' );
        }
        else if( type == 'date' )
        {
            element = document.getElementById( 'value[' + programStageId + '].date:value[' + dataElementId + '].date' );
        }
        else if( selectedOption )
        {
            element = selectedOption;
        }
        else if ( optionComboId )
        {
            element = document.getElementById( 'value[' + programStageId + '].value:value[' + dataElementId + '].value:value[' + optionComboId + '].value');
        }
        else
        {
            element = document.getElementById( 'value[' + programStageId + '].value:value[' + dataElementId + '].value' );
        }
             
        element.style.backgroundColor = color;
    }
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
	
    var match = /.*\[(.*)\]/.exec( str ); //value[-dataElementId-]
	
    if ( ! match )
    {
        return;
    }

    var deId = match[1];
	
    //Get the data element name
    var nameContainer = document.getElementById('value[' + deId + '].name');
	
    if ( ! nameContainer )
    {
        return;
    }

    var name = '';
	
	
    var as = nameContainer.getElementsByTagName('a');

    if ( as.length > 0 )	//Admin rights: Name is in a link
    {
        name = as[0].firstChild.nodeValue;
    }
    else
    {
        name = nameContainer.firstChild.nodeValue;
    }
	
}

function keyPress( event, field )
{
    var key = 0;
    if ( event.charCode )
    {
        key = event.charCode; /* Safari2 (Mac) (and probably Konqueror on Linux, untested) */
    }
    else
    {
        if ( event.keyCode )
        {
            key = event.keyCode; /* Firefox1.5 (Mac/Win), Opera9 (Mac/Win), IE6, IE7Beta2, Netscape7.2 (Mac) */
        }
        else
        {
            if ( event.which )
            {
                key = event.which; /* Older Netscape? (No browsers triggered yet) */
            }
        }
    }
    
    if ( key == 13 ) /* CR */
    {
        nextField = getNextEntryField( field );
        if ( nextField )
        {
            nextField.focus(); /* Does not seem to actually work in Safari, unless you also have an Alert in between */
        }
        return true;
    }
    
    /* Illegal characters can be removed with a new if-block and return false */
    return true;
}

function getNextEntryField( field )
{
    var inputs = document.getElementsByName( "entryfield" );
    
    // Simple bubble sort
    for ( i = 0; i < inputs.length - 1; ++i )
    {
        for ( j = i + 1; j < inputs.length; ++j )
        {
            if ( inputs[i].tabIndex > inputs[j].tabIndex )
            {
                tmp = inputs[i];
                inputs[i] = inputs[j];
                inputs[j] = tmp;
            }
        }
    }
    
    i = 0;
    for ( ; i < inputs.length; ++i )
    {
        if ( inputs[i] == field )
        {
            break;
        }
    }
    
    if ( i == inputs.length - 1 )
    {
        // No more fields after this:
        return false;
    // First field:
    //return inputs[0];
    }
    else
    {
        return inputs[i + 1];
    }
}

//------------------------------------------------------
// Save value for dataElement of type date in entryscreen
//------------------------------------------------------
function saveDate( dataElementId , dataElementName )
{
    var providedByAnotherFacility ;
	
    if( document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' ) )
        providedByAnotherFacility = document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' ).checked;
	
    var field = document.getElementById('value['+dataElementId+'].date');
	
    field.style.backgroundColor = '#ffffcc';
	
    if( !isValidDate( field.value ) )
    {
        field.style.backgroundColor = '#ffcc00';
        window.alert('Incorrect format for date value. The correct format should be ' + dateFormat.replace('yy', 'yyyy') + '\n\n '+dataElementName );
		  
        field.select();
        field.focus();

        return;
    }
	
    var valueSaver = new ValueSaver( dataElementId, field.value, providedByAnotherFacility, '#ccffcc', '' );
    valueSaver.save();
	
}
function saveDateCustom(  this_ )
{
    jQuery(this_).css({
        "background-color":"#ffffcc"
    });

    var data = jQuery(this_).metadata({
        type:"attr",
        name:"data"
    });
    var providedByAnotherFacility = document.getElementById( data.programStageId+'_'+data.dataElementId+'_facility' ).checked;

    if(jQuery(this_).val()!="")
    { 
        if( !isValidDate( jQuery(this_).val() ) )
        {
            jQuery(this_).css({
                "background-color":"#ffcc00"
            });
            window.alert('Incorrect format for date value. The correct format should be ' + dateFormat.replace('yy', 'yyyy') +' \n\n '+data.dataElementName );
		  
            jQuery(this_).focus();

            return;
        }
    }
	
	var incidentDate = new Date( jQuery('#dueDate').val() );
	var inputtedDate = new Date(jQuery(this_).val());
	if( inputtedDate < incidentDate )
	{
		jQuery(this_).css({
                "background-color":"#ffcc00"
            });
            window.alert( i18n_date_less_incident_date );
		  
            jQuery(this_).focus();

            return;
	}
	
    var valueSaver = new CustomValueSaver( data.dataElementId, jQuery(this_).val(), providedByAnotherFacility, '#ccffcc', '' );
    valueSaver.setProgramStageId( data.programStageId );
    valueSaver.setType(data.dataElementType);
    valueSaver.save();
	
}

function selectDefaultForm()
{
    jQuery('#selectRecordingForm').submit();
}

function saveValueWithOptionComboId( this_ )
{
	
    var data = jQuery( this_ ).metadata({
        type:'attr',
        name:'data'
    });
	
    var providedByAnotherFacility = document.getElementById( 'value[' + data.programStageId + '].facility:value[' + data.dataElementId + '].facility' ).checked;
    
    this_.style.backgroundColor = '#ffffcc';
    
    if( this_.value != '' )
    {
        if( data.dataElementType == 'int' )
        {
            if ( !isInt( this_.value ))
            {
                this_.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer + '\n\n' + data.dataElementName );

                this_.select();
                this_.focus();

                return;
            }
        }
    }
    var valueSaver = new CustomValueSaver( dataElementId, field.value, providedByAnotherFacility, '#ccffcc', '' );
    valueSaver.setOptionComboId( data.optionComboId );
    valueSaver.setProgramStageId( data.programStageId );
    valueSaver.save();
}
function initCustomCheckboxes()
{
    jQuery('input[type=checkbox][name="providedByAnotherFacility"]').prettyCheckboxes();
}

jQuery(document).ready(function(){
    var currentFocus;
    if( jQuery("#entryFormContainer") ) {
        if( jQuery("#executionDate").val() )
        {
            jQuery("#startMsg").hide();
        }else
        {
            toggleContentForReportDate(false);
        }
	
        jQuery("#customEntryScreenContainer td").hover(
            function(){
                //				jQuery(this).siblings("td").removeClass("focusCell");
                var childrens = jQuery(this).children("input[name='entryfield'],select[name='entryselect']");
                
				if( jQuery(childrens[0]).is(":disabled")) {
                    //					jQuery(this).addClass("focusCell");
                    DRAG_DIV.showData(jQuery(childrens[0]).metadata({
                        "type":"attr",
                        "name":"data"
                    }));
                }
				
				
            },
            function(){
            //				if( currentFocus )
            //				{
            //					jQuery(currentFocus).focus();
            //				}
            //				jQuery(this).removeClass("focusCell");
            }
            );
		
        jQuery("input[name='entryfield'],select[name='entryselect']").each(function(){
            jQuery(this).focus(function(){
                currentFocus = this;
                //			jQuery(this).addClass("focusCell");
                DRAG_DIV.showData(jQuery(this).metadata({
                    "type":"attr",
                    "name":"data"
                }));
            });
            jQuery(this).blur(function(){
                //			jQuery(this).removeClass("focusCell");
                });
            jQuery(this).hover(
                function(){
                    //					jQuery(this).addClass("focusCell");
                    DRAG_DIV.showData(jQuery(this).metadata({
                        "type":"attr",
                        "name":"data"
                    }));
                },
                function()
                {
                    //					jQuery(this).removeClass("focusCell");
                    if(currentFocus){
                        //						jQuery(currentFocus).addClass("focusCell");
                        DRAG_DIV.showData(jQuery(currentFocus).metadata({
                            "type":"attr",
                            "name":"data"
                        }));
                    }
                //					jQuery(this).parent("td:first").removeClass("focusCell");
                }
                );
            jQuery(this).addClass("inputText");
        });
		
        TOGGLE.init();
    }
});

DRAG_DIV = {
    init : function()
    {
        var dragDiv = jQuery("#dragDiv");
        dragDiv.show();
        var left = screen.width - 500 ;
        var top = Math.round(jQuery("#startMsg").position().top )  ;
        dragDiv.css({
            'left': left+'px',
            'top': top+'px'
        });
        dragDiv.draggable();
    },
		
    showData : function(data)
    {
        jQuery("#orgUnitName").text(data.orgUnitName);
        jQuery("#programStageName").text(data.programStageName);
        jQuery("#dataelementName").text(data.dataElementName);
    },
		
    resetData : function()
    {
        jQuery("#orgUnitName").text("");
        jQuery("#programStageName").text("");
        jQuery("#dataelementName").text("");
    }
};
function toggleContentForReportDate(show)
{
    if( show ){
        jQuery("#startMsg").hide();
        jQuery("#entryFormContainer").show();
        jQuery("#completeBtn").removeAttr('disabled');
    }else {
        jQuery("#entryFormContainer").hide();
        jQuery("#completeBtn").attr('disabled', 'disabled');
        jQuery("#startMsg").show();
    }
}

function openChildRegistrationForm()
{
    var patientId = document.getElementById( "id" ).value;
	
    window.location.href = "../dhis-web-maintenance-patient/showAddRelationshipPatient.action?id="+patientId;
}

function doComplete()
{
    var flag = false;
    jQuery("#entryFormContainer input[name='entryfield'],select[name='entryselect']").each(function(){
        jQuery(this).parent().removeClass("errorCell");
        if( jQuery(this).metadata({
            "type":"attr",
            "name":"data"
        }).compulsory ){
            if( !jQuery(this).val() || jQuery(this).val() == "undifined" ){
                flag = true;
                jQuery(this).parent().addClass("errorCell");
            }
        }
    });
    if( flag ){
        alert(i18n_error_required_field);
        return;
    }else {
        if( confirm(i18n_complete_confirm_message) )
            jQuery("#completeDataEntryForm").submit();
    }
}

TOGGLE = {
    init : function(){
        jQuery(".togglePanel").each(function(){
            jQuery(this).next("table:first").addClass("sectionClose");
            jQuery(this).addClass("close");
            jQuery(this).click(function(){
                var table = jQuery(this).next("table:first");
                if( table.hasClass("sectionClose")){
                    //					jQuery(".sectionOpen").removeClass("sectionOpen").addClass("sectionClose");
                    //					jQuery(".togglePanel.open").removeClass("open").addClass("close");
                    table.removeClass("sectionClose").addClass("sectionOpen");
                    jQuery(this).removeClass("close").addClass("open");
                    window.scroll(0,jQuery(this).position().top);
                }else if( table.hasClass("sectionOpen")){
                    table.removeClass("sectionOpen").addClass("sectionClose");
                    jQuery(this).removeClass("open").addClass("close");
                }
            });
        });
    },
}
