

function getLineListElements()
{
    var lineListGroupList = document.getElementById("selectedLineListGroupId");
    var lineListGroupId = lineListGroupList.options[ lineListGroupList.selectedIndex ].value;
    alert("inside getLineListElements groupid = "+lineListGroupId);
    if ( lineListGroupId != null )
    {
        var url = "getFilteredLineListElements.action?id=" + lineListGroupId;
        var request = new Request();
        request.setResponseTypeXML('lineListElement');
        request.setCallbackSuccess(getLineListElementsReceived);
        request.send(url);
    }
}

function getLineListElementsReceived( xmlObject )
{
    var availableLineListElements = document.getElementById("availableLineListElements");
    var selectedLineListElements = document.getElementById("selectedLineListElements");

    alert("inside getLineListElementsReceived ");
    clearList(availableLineListElements);

    var lineListElements = xmlObject.getElementsByTagName("lineListElement");

    alert("lineListElements.length " + lineListElements.length );
    for ( var i = 0; i < lineListElements.length; i++ )
    {
        var id = lineListElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var lineListElementName = lineListElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        if ( listContains(selectedLineListElements, id) == false )
        {
            var option = document.createElement("option");
            option.value = id;
            option.text = lineListElementName;
            option.title = lineListElementName;
            availableLineListElements.add(option, null);
        }
    }
}
// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validateAddValidationRule()
{
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
