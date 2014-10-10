// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------
function selectAllElementsById( listId1, listId2 ) {
    var list1 = document.getElementById( listId1 );
    var list2 = document.getElementById( listId2 );
    for ( var i = 0; i < list1.options.length; i++ ) {
        list1.options[i].selected = true;
    }
    for ( var i = 0; i < list2.options.length; i++ ) {
        list2.options[i].selected = true;
    }
}

function showAggregateIndicatorDetails( aggregateIndicatorId )
{
    var request = new Request();
    request.setResponseTypeXML( 'aggregateIndicator' );
    request.setCallbackSuccess( aggregateIndicatorReceived );
    request.send( 'getAggregateIndicator.action?id=' + aggregateIndicatorId );
}

function aggregateIndicatorReceived( attributeElement )
{
	setInnerHTML( 'idField', getElementValue( attributeElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( attributeElement, 'name' ) );
	setInnerHTML( 'dataElementField', getElementValue(attributeElement, 'dataElement'));
	setInnerHTML( 'criteriasField', getElementValue( attributeElement, 'criterias' ) );
	setInnerHTML( 'attributeOptionsField', getElementValue( attributeElement, 'attributeOptions' ) );
     
    showDetails();
}


//-----------------------------------------------------------------------------
//Remove AggregateIndicator
//-----------------------------------------------------------------------------

function removeAggregateIndicator( aggregateIndicatorId, name )
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
	
 	var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removeAggregateIndicatorCompleted );     
     window.location.href = 'removeAggregateIndicator.action?id=' + aggregateIndicatorId;
 }
}

function removeAggregateIndicatorCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
     window.location.href = 'aggregateIndicator.action';
 }
 else if ( type = 'error' )
 {
     setFieldValue( 'warningField', message );
     
     showWarning();
 }
}


//-----------------------------------------------------------------------------
// Add AggregateIndicator
//-----------------------------------------------------------------------------

function validateAddAggregateIndicator()
{
	
	var url = 'validateAggregateIndicator.action?' +
			'nameField=' + getFieldValue( 'nameField' ) ;
	
	var request = new Request();
 request.setResponseTypeXML( 'message' );
 request.setCallbackSuccess( addValidationCompleted );    
 request.send( url );        

 return false;
}

function addValidationCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
	 selectAllElementsById( 'selectedAttributeOptions', 'selectedCriterias' )
     var form = document.getElementById( 'addAggregateIndicatorForm' );        
     form.submit();
 }
 else if ( type == 'error' )
 {
     window.alert( i18n_adding_atttibute_failed + ':' + '\n' + message );
 }
 else if ( type == 'input' )
 {
     document.getElementById( 'message' ).innerHTML = message;
     document.getElementById( 'message' ).style.display = 'block';
 } 
}

//-----------------------------------------------------------------------------
//Update AggregateIndicator
//-----------------------------------------------------------------------------

function validateUpdateAggregateIndicator()
{
	
 var url = 'validateAggregateIndicator.action?' + 
 		'id=' + getFieldValue( 'id' ) +
 		'&nameField=' + getFieldValue( 'nameField' ) ;
	
	var request = new Request();
 request.setResponseTypeXML( 'message' );
 request.setCallbackSuccess( updateValidationCompleted );   
 
 request.send( url );
     
 return false;
}

function updateValidationCompleted( messageElement )
{
 var type = messageElement.getAttribute( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
	selectAllElementsById( 'selectedAttributeOptions', 'selectedCriterias' )
 	var form = document.getElementById( 'updateAggregateIndicatorForm' );        
     form.submit();
 }
 else if ( type == 'error' )
 {
     window.alert( i18n_saving_program_failed + ':' + '\n' + message );
 }
 else if ( type == 'input' )
 {
     document.getElementById( 'message' ).innerHTML = message;
     document.getElementById( 'message' ).style.display = 'block';
 }
}

//-----------------------------------------------------------------------------
//Report params
//-----------------------------------------------------------------------------

var paramOrganisationUnit = null;

function paramOrganisationUnitSet( id )
{
 paramOrganisationUnit = id;
}

//-----------------------------------------------------------------------------
//Validation
//-----------------------------------------------------------------------------

function validationError()
{
 if ( $( "#selectionTree" ).length && paramOrganisationUnit == null )
 {
     setMessage( i18n_please_select_unit );
     return true;
 }

 return false;
}

//-----------------------------------------------------------------------------
//Report
//-----------------------------------------------------------------------------

function generateReport()
{

 if ( validationError() )
 {
     return false;
 }

 //setWaitMessage( i18n_please_wait );
 
 var url = "aggregateIndicatorMapping.action?" + getUrlParams();
 
 window.location.href = url;
 
 //var request = new Request();
 //request.setCallbackSuccess( getReportStatus );
 //request.send( url );

}

function getReportStatus()
{
 var url = "getStatus.action";

 var request = new Request();
 request.setResponseTypeXML( "status" );
 request.setCallbackSuccess( reportStatusReceived );
 request.send( url );
}

function reportStatusReceived( xmlObject )
{
 var statusMessage = getElementValue( xmlObject, "statusMessage" );
 var finished = getElementValue( xmlObject, "finished" );

 if ( finished == "true" )
 {
     setMessage( i18n_process_completed );
     viewReport();
 } else
 {
     setTimeout( "getReportStatus();", 1500 );
 }
}


function getUrlParams()
{
 var url = "id=" + $( "#id" ).val();

 if ( paramOrganisationUnit != null )
 {
     url += "&organisationUnitId=" + paramOrganisationUnit;
 }

 return url;
}

