
// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validateAddValidationRule()
{
    var field = document.getElementById( 'name' );
	var resval = field.value;
	if (isFirstLetter(resval))
        {
            alert("Name field must start with a Letter");
            field.value = "";
      		setTimeout(function(){field.focus();field.select();},2);
            return false;
        }
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    request.send( 'validateValidationRule.action?name=' + getFieldValue( 'name' )  +
    '&operator=' + getFieldValue( 'operator' ) +
    '&leftSideExpression=' + getFieldValue( 'leftSideExpression' ) +
    '&leftSideDescription=' + getFieldValue( 'leftSideDescription' ) +
    '&rightSideExpression=' + getFieldValue( 'rightSideExpression' ) +
    '&rightSideDescription=' + getFieldValue( 'rightSideDescription' ) );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {        
        var form = document.getElementById( 'addValidationRuleForm' );
        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_validation_rule_failed + ':' + '\n' + message );
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

function validateUpdateValidationRule()
{
	var field = document.getElementById( 'name' );
	var resval = field.value;
	if (isFirstLetter(resval))
        {
            alert("Name field must start with a Letter" );
            field.value = "";
      		setTimeout(function(){field.focus();field.select();},2);
            return false;
        }
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.send( 'validateValidationRule.action?id=' + getFieldValue( 'id' ) +
    '&name=' + getFieldValue( 'name' )  +
	'&operator=' + getFieldValue( 'operator' ) +
    '&leftSideExpression=' + getFieldValue( 'leftSideExpression' ) +
    '&leftSideDescription=' + getFieldValue( 'leftSideDescription' ) +
    '&rightSideExpression=' + getFieldValue( 'rightSideExpression' ) +
    '&rightSideDescription=' + getFieldValue( 'rightSideDescription' ) );

    return false;
}

function updateValidationCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {   
        var form = document.getElementById( 'updateValidationRuleForm' );
        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_validation_rule_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

function isFirstLetter(s)
{
    var n = trim(s);
    return n.length > 0 && !(/^[A-Za-z]/).test(n);
}

function trim( stringToTrim ) 
{
    return stringToTrim.replace(/^\s+|\s+$/g,"");
}
