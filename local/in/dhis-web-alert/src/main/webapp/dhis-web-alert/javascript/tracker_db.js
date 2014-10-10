function viewPatientDataRecords( programId, orgUnitId, viewStatus ) 
{
	var url = 'getPatientDataRecords.action?orgUnitId=' + orgUnitId + "&programId=" + programId + "&viewStatus=" +viewStatus;
	$('#contentDataRecord').dialog('destroy').remove();
    $('<div id="contentDataRecord">' ).load(url).dialog({
        title: 'Benificiarywise ProgramStage Summary',
		maximize: true, 
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 1000,
        height: 550
    });
}
function showPatientDetails( patientId )
{	
	$.post("getPatientDetails.action",
			{
				id : patientId
			},
			function (data)
			{
				patientReceived(data);
			},'xml');

}

function patientReceived( patientElement )
{   
	// ----------------------------------------------------------------------------
	// Get common-information
    // ----------------------------------------------------------------------------
	var patientInfo = "";
	
	var id = patientElement.getElementsByTagName( "id" )[0].firstChild.nodeValue;
	var fullName = patientElement.getElementsByTagName( "fullName" )[0].firstChild.nodeValue;   
	var gender = patientElement.getElementsByTagName( "gender" )[0].firstChild.nodeValue;   
	var dobType = patientElement.getElementsByTagName( "dobType" )[0].firstChild.nodeValue;   
	var birthDate = patientElement.getElementsByTagName( "dateOfBirth" )[0].firstChild.nodeValue;   
	var bloodGroup= patientElement.getElementsByTagName( "bloodGroup" )[0].firstChild.nodeValue;   
    
	var commonInfo =  '<strong>id :</strong> ' + id + "<br>" 
					+ '<strong>name :</strong> ' + fullName + "<br>" 
					+ '<strong>Gender :</strong> ' + gender+ "<br>" 
					+ '<strong>DOB Type :</strong> ' + dobType+ "<br>" 
					+ '<strong>DOB :</strong> ' + birthDate+ "<br>" 
					+ '<strong>Blood Group :</strong> ' + bloodGroup;
	
	setInnerHTML( 'commonInfoField', commonInfo );

	patientInfo += 'id : ' + id + "\n" + 'name : ' + fullName + "\n" + 'Gender : ' + gender+ "\n" 
					+ 'DOB Type : ' + dobType+ "\n" + 'DOB : ' + birthDate+ "\n" + 'Blood Group : ' + bloodGroup;

	patientInfo += "\nIdentifier :";
	// ----------------------------------------------------------------------------
	// Get identifier
    // ----------------------------------------------------------------------------
	
	var identifiers = patientElement.getElementsByTagName( "identifier" );   
    
    var identifierText = '';
	
	for ( var i = 0; i < identifiers.length; i++ )
	{		
		identifierText = identifierText + identifiers[ i ].getElementsByTagName( "identifierText" )[0].firstChild.nodeValue + '<br>';
		patientInfo += "\n" + identifiers[ i ].getElementsByTagName( "identifierText" )[0].firstChild.nodeValue;
	}
	
	setInnerHTML( 'identifierField', identifierText );
	
	// ----------------------------------------------------------------------------
	// Get attribute
    // ----------------------------------------------------------------------------
	patientInfo += "\nAttribute:";
	var attributes = patientElement.getElementsByTagName( "attribute" );   
    
    var attributeValues = '';
	
	for ( var i = 0; i < attributes.length; i++ )
	{	
		attributeValues = attributeValues + '<strong>' + attributes[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue  + ':  </strong>' + attributes[ i ].getElementsByTagName( "value" )[0].firstChild.nodeValue + '<br>';
		patientInfo += "\n" + attributes[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue  + ': ' + attributes[ i ].getElementsByTagName( "value" )[0].firstChild.nodeValue;
	}
	attributeValues = ( attributeValues.length == 0 ) ? i18n_none : attributeValues;
	setInnerHTML( 'attributeField', attributeValues );
    
	// ----------------------------------------------------------------------------
	// Get programs
    // ----------------------------------------------------------------------------
	patientInfo += "\nProgram :";
    var programs = patientElement.getElementsByTagName( "program" );   
    
    var programName = '';
	
	for ( var i = 0; i < programs.length; i++ )
	{		
		programName = programName + programs[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue + '<br>';
		patientInfo += "\n" +programs[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
	}
	
	alert( patientInfo );
	
	//programName = ( programName.length == 0 ) ? i18n_none : programName;
	//setInnerHTML( 'programField', programName );
   
	// ----------------------------------------------------------------------------
	// Show details
    // ----------------------------------------------------------------------------
	
    //showDetails();
}