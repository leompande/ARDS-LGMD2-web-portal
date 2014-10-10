
// -----------------------------------------------------------------------------
// Save
// -----------------------------------------------------------------------------

var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_RED = '#ff8a8a';
var personID = 0;

function saveVal( dataElementId, optionComboId )
{
	var dataElementName = document.getElementById( 'value[' + dataElementId + '].name' ).innerHTML;
	
	saveValue( dataElementId, optionComboId, dataElementName, null );
}

function saveValue( attributeId, attributeName, dataType ) // ZeroValueSaveMode kept for CDE backwards compatibility
{
	
    var field = document.getElementById( attributeId );
    //var type = document.getElementById( attributeId ).innerHTML;   
    var type = dataType;
	var organisationUnitId = getFieldValue( 'organisationUnitId' );
	var personId = getFieldValue( 'personId' );
	var hrDataSetId = getFieldValue( 'hrDataSetId');
	var error = false;
	
    //field.style.backgroundColor = COLOR_YELLOW;
    
    if ( field.value && field.value != '' )
    {
        if ( type == 'integer' || type == 'double' )
        {
            if ( type == 'integer' && !isInt( field.value ) )
            {
            	field.style.backgroundColor = COLOR_RED;
            	window.alert( i18n_value_must_integer + '\n\n' + attributeName );
            	error = true;
            }  
            else if ( type == 'double' && !isNumber( field.value ) )
            {
            	field.style.backgroundColor = COLOR_RED;
                window.alert( i18n_value_must_number + '\n\n' + attributeName );
                error = true;
            } 
			else if ( type == 'double' && !isPositiveNumber( field.value ) )
            {
				field.style.backgroundColor = COLOR_RED;
                window.alert( i18n_value_must_positive_number + '\n\n' + attributeName );
                error = true;
            }      
        }
    }

    if (error == false)
    {
    	var valueSaver = new ValueSaver( attributeId, organisationUnitId, field.value, COLOR_GREEN, personId, hrDataSetId );    
    	valueSaver.save();
    }
}

/**
 * Supportive method.
 */
function alertField( field )
{
	field.style.backgroundColor = COLOR_YELLOW;
    field.select();
    field.focus();
    return false;
}

// -----------------------------------------------------------------------------
// Saver objects
// -----------------------------------------------------------------------------

function ValueSaver( dataElementId_, organisationUnitId_, value_, resultColor_, personId_, hrDataSetId_ )
{
    var attributeId = dataElementId_;
    var personId = personId_;
    var value = value_;
    var resultColor = resultColor_;
    var organisationUnitId = organisationUnitId_;
    var hrDataSetId = hrDataSetId_;
    var field = document.getElementById( attributeId );
    
    field.style.backgroundColor = resultColor;
    
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( validateInputDataCompleted );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );        
        request.send( 'saveValue.action?organisationUnitId=' + organisationUnitId + '&attributeId=' +
        		attributeId + '&value=' + value + '&personId=' + personId + '&hrDataSetId=' + hrDataSetId );
    };
    
    function validateInputDataCompleted( messageElement )
    {
     var type = messageElement.getAttribute( 'type' );
     var message = messageElement.firstChild.nodeValue;
     
     if ( type == 'success' )
    	 {
    	 field.style.backgroundColor = COLOR_GREEN;
    	 }

     else if ( type == 'error' )
     	{
         window.alert( i18n_adding_atttibute_failed + ':' + '\n' + message );
         alert ('System Error');
     	}
     else if ( type == 'input' )
     	{
    	 alert ('This Field Is Suppose to be Unique');
    	 field.style.backgroundColor = COLOR_RED;
     	} 
    }
    
    function handleHttpError( errorCode )
    {
        markValue( COLOR_RED );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }   
    
    function markValue( color )
    {
        var type = document.getElementById( 'attributeId' ).innerText;
        var element;
                  
        element = document.getElementById( 'attributeId');            

        element.style.backgroundColor = color;
    }
}



// -----------------------------------------------------------------------------
// Section
// -----------------------------------------------------------------------------

function openCloseSection( sectionId )
{
	var divSection = document.getElementById( sectionId );
	var sectionLabel = document.getElementById( sectionId + ":name" );	
	
	if( divSection.style.display == 'none' )
	{			
		divSection.style.display = ('block');
		sectionLabel.style.textAlign = 'center';
	}
	else
	{			
		divSection.style.display = ('none');
		sectionLabel.style.textAlign = 'left';
	}
}


function validateInputData()
{
	
	var url = 'validateAttribute.action?' +
			'nameField=' + getFieldValue( 'nameField' ) ;
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( validateInputDataCompleted );    
	request.send( url );        
	
	return false;
}

function validate(personId, hrDataSetId){
		
	var validateCompletePersonData = new ValidateCompletePersonData ( personId, hrDataSetId );
	validateCompletePersonData.save();
		
}

function ValidateCompletePersonData( personId_, hrDataSetId_ )
{
	var personId = personId_;
	var hrDataSetId = hrDataSetId_;
	
	this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( validateDataCompleted );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );        
        request.send( 'completeRegistration.action?hrDataSetId=' + hrDataSetId + '&personId=' +
        		personId );
    };
    
    function validateDataCompleted( messageElement )
    {
     var type = messageElement.getAttribute( 'type' );
     var message = messageElement.firstChild.nodeValue;
     
     

     if ( type == 'input' )
     {
    	 window.alert ('This Form Is Incomplete \n Please fill and required Fields');
     }
     else
     {
     	window.alert ('form is complete');
     	document.getElementById( "completeButton" ).disabled = true;
     }
     	
    }
    
    function handleHttpError( errorCode )
    {
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }
}
