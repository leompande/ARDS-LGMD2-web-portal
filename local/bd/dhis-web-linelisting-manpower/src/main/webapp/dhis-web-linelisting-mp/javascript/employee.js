//------------------------------------------------------------------------------
//Remove Employee
//------------------------------------------------------------------------------

function removeEmployee( pdsCode, name )
{
	removeItem( pdsCode, name, i18n_confirm_delete, 'removeEmployee.action' );
}

function showEmployeeDetails( pdsCode )
{
	var request = new Request();
    request.setResponseTypeXML( 'employee' );
    request.setCallbackSuccess( employeeReceived );
    request.send( 'getEmployee.action?pdsCode=' + pdsCode );
}

function employeeReceived( employeeElement )
{
	setInnerHTML( 'pdsCodeField', getElementValue( employeeElement, 'pdsCode' ) );
    setInnerHTML( 'nameField', getElementValue( employeeElement, 'name' ) );
    setInnerHTML( 'dateOfBirthField', getElementValue( employeeElement, 'dateOfBirth' ) );
    setInnerHTML( 'lprDateField', getElementValue( employeeElement, 'lprDate' ) );
    setInnerHTML( 'sexField', getElementValue( employeeElement, 'sex' ) );
    setInnerHTML( 'joinDateToGovtServiceField', getElementValue( employeeElement, 'joinDateToGovtService' ) );
    setInnerHTML( 'resAddressField', getElementValue( employeeElement, 'resAddress' ) );
    setInnerHTML( 'contactNumberField', getElementValue( employeeElement, 'contactNumber' ) );
    setInnerHTML( 'emergencyContactNumberField', getElementValue( employeeElement, 'emergencyContactNumber' ) );
    
    showDetails();
}

function lprChanged()
{
    var dobField = document.getElementById( 'dob' );
    var lprDateField = document.getElementById( 'lprDate' );
    
    var dob = dobField.value;
    var partsOfDob = dob.split("-");
    
    var dobYear = parseInt( partsOfDob[0] );
    var lprYear = dobYear + lpr_Period;
    lprDateField.value = lprYear + "-" + partsOfDob[1] + "-" +partsOfDob[2];
    
}

function validateDate( dateType )
{
	var sourceDate = document.getElementById('dob').value;
	var compareDate = dateType.value;
	if ( sourceDate > compareDate)
	{
		alert("This Date can not be before than Date of Birth");
		dateType.value = "";
		setTimeout(function(){
			dateType.focus();dateType.select();
	    },2);
	}
}
