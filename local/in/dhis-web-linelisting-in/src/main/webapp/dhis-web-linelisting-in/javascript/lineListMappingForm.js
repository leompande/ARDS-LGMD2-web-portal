
// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validateLineListMapping()
{
    /*
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addLineListMappingCompleted );
    request.send( 'validateValidationRule.action?name=' + getFieldValue( 'name' )  +
    '&leftSideExpression=' + getFieldValue( 'leftSideExpression' ) +
    '&rightSideExpression=' + getFieldValue( 'rightSideExpression' ) +
    */
	
	$.post("validateValidationRule.action",
			{
				name : getFieldValue( 'name' ),
				leftSideExpression : getFieldValue( 'leftSideExpression' ),
				rightSideExpression : getFieldValue( 'rightSideExpression' )
			},
			function (data)
			{
				addLineListMappingCompleted(data);
			},'xml');
    
    
    return false;
}

function addLineListMappingCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {        
        var form = document.getElementById( 'addLineListDataElementMappingForm' );
        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( Adding Line List Data Element Mapping Failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update validation rule
// -----------------------------------------------------------------------------

function validateUpdateLineListMapping()
{
	/*
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateLineListMappingCompleted );
    request.send( 'validateValidationRule.action?id=' + getFieldValue( 'id' ) +
    '&name=' + getFieldValue( 'name' )  +
    '&leftSideExpression=' + getFieldValue( 'leftSideExpression' ) +
    '&rightSideExpression=' + getFieldValue( 'rightSideExpression' ) +
    */
	$.post("validateValidationRule.action",
			{
				id : getFieldValue( 'id' ),
				name : getFieldValue( 'name' ),
				leftSideExpression : getFieldValue( 'leftSideExpression' ),
				rightSideExpression : getFieldValue( 'rightSideExpression' )
			},
			function (data)
			{
				updateLineListMappingCompleted(data);
			},'xml');
    
    
    return false;
}

function updateLineListMappingCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {   
        var form = document.getElementById( 'updateLineListDataElementMappingForm' );
        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( Updating Line List Data Element Mapping Failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
