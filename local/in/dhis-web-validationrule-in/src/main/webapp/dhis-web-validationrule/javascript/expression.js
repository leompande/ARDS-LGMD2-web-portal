
function showEditLeftSideExpressionForm()
{
    var description = htmlEncode( document.getElementById( "leftSideDescription" ).value );
    var expression = htmlEncode( document.getElementById( "leftSideExpression" ).value );
    var textualExpression = htmlEncode( document.getElementById( "leftSideTextualExpression" ).value );
	
    showExpressionForm( "left", description, expression, textualExpression );
}

function showEditRightSideExpressionForm()
{
    var description = htmlEncode( document.getElementById( "rightSideDescription" ).value );
    var expression = htmlEncode( document.getElementById( "rightSideExpression" ).value );
    var textualExpression = htmlEncode( document.getElementById( "rightSideTextualExpression" ).value );
	
    showExpressionForm( "right", description, expression, textualExpression );
}

function showExpressionForm( side, description, expression, textualExpression )
{
    var url = "showEditExpressionForm.action?side=" + side +
    "&description=" + description +
    "&expression=" + expression +
    "&textualExpression=" + textualExpression;
		
    var dialog = window.open( url, "_blank", "directories=no, \
    	height=560, width=790, location=no, menubar=no, status=no, \
    	toolbar=no, resizable=no");
}

function insertText( inputAreaName, inputText )
{
    var inputArea = document.getElementById( inputAreaName );
	
    var startPos = inputArea.selectionStart;
    var endPos = inputArea.selectionEnd;
	
    var existingText = inputArea.value;
    var textBefore = existingText.substring( 0, startPos );
    var textAfter = existingText.substring( endPos, existingText.length );
	
    inputArea.value = textBefore + inputText + textAfter;
	
    updateTextualExpression( inputAreaName );
}

function filterDataElements( dataElementGroupSelectName, filterName )
{
    var dataElementGroup = document.getElementById( dataElementGroupSelectName );
    var dataElementGroupId = dataElementGroup.options[ dataElementGroup.selectedIndex ].value;
    var filter = htmlEncode( document.getElementById( filterName ).value );
	
    //var url = "getFilteredDataElements.action?dataElementGroupId=" + dataElementGroupId + "&filter=" + filter;
    var request = new Request();
    request.setResponseTypeXML( 'operand' );
    request.setCallbackSuccess( getFilteredDataElementsReceived );
    //request.send( url );
    var requestString = "getFilteredDataElements.action";
    var params = "dataElementGroupId=" + dataElementGroupId + "&filter=" + filter;
    request.sendAsPost( params );
    request.send( requestString );
    
}

function getFilteredDataElementsReceived( xmlObject )
{
    var operandList = document.getElementById( "dataElementId" );
			
    operandList.options.length = 0;
	
    var operands = xmlObject.getElementsByTagName( "operand" );
	
    for ( var i = 0; i < operands.length; i++)
    {
        var id = operands[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var elementName = operands[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
        var option = document.createElement( "option" );
        option.value = "[" + id + "]";
        option.text = elementName;
        operandList.add( option, null );
    }
}

function updateTextualExpression( expressionFieldName )
{	
    var expression = htmlEncode( document.getElementById( expressionFieldName ).value );

    //var url = "getTextualExpression.action?expression=" + expression;
    var request = new Request();
    request.setCallbackSuccess( updateTextualExpressionReceived );
    //request.send( url );
    var requestString = "getTextualExpression.action";
    var params = "expression=" + expression;
    request.sendAsPost( params );
    request.send( requestString );
}

function updateTextualExpressionReceived( messageElement )
{
    document.getElementById( "textualExpression" ).innerHTML = messageElement;
}

function validateExpression()
{
    var description =  document.getElementById( "description" ).value ;
	if( ! /^[\w-.,()\/\s]+$/i.test(description) )
	{
		alert("Description is not valid");
		return ;
	}
	description = htmlEncode ( description );

    var expression = htmlEncode( document.getElementById( "expression" ).value );

    if(description.length < 4 || description.length > 150)
    {
        alert("Please enter Description between 4 and 150 alphanumeric characters long.");
        //field.value = "";
        //setTimeout(function(){
        //    field.focus();field.select();
        //},2);
        return ;
    }
    //var url = "validateExpression.action?description=" + description + "&expression=" + expression;
    var request = new Request();
    request.setResponseTypeXML( "message" );
    request.setCallbackSuccess( validateExpressionReceived );

    var requestString = "validateExpression.action";
    var params = "description=" + description + "&expression=" + expression;
    request.sendAsPost( params );
    request.send( requestString );
}

function descriptionValidation(description)
{
    for(var i=0;i<description.length;i++){
        if(description.length > 0 && !(/^[\w-.,()%\/\s]/).test(description.charAt(i)))
        {
            return true;
        }
    }
    return false;
}
function rangeOfDescription(description)
{
    for(var i=0;i<description.length;i++){
        if(description.length > 3 && description.length < 151)
        {
            return true;
        }
    }
    return false;
}

function validateExpressionReceived( xmlObject )
{
    var type = xmlObject.getAttribute( 'type' );
    var message = xmlObject.firstChild.nodeValue;
    
    if ( type == "success" )
    {
        saveExpression();
    }
    else if ( type == "error" )
    {
        document.getElementById( "textualExpression" ).innerHTML = message;
    }   
}

function saveExpression()
{
    var description = document.getElementById( "description" ).value;
    var expression = document.getElementById( "expression" ).value;
    var textualDescription = document.getElementById( "textualExpression" ).innerHTML;
    
    var side = htmlEncode( document.getElementById( "side" ).value );
    
    if ( window.opener && !window.opener.closed )
    {
        if ( side == "left" )
        {
            window.opener.document.getElementById( "leftSideDescription" ).value = description;
            window.opener.document.getElementById( "leftSideExpression" ).value = expression;
            window.opener.document.getElementById( "leftSideTextualExpression" ).value = textualDescription;
        }
        else if ( side == "right" )
        {
            window.opener.document.getElementById( "rightSideDescription" ).value = description;
            window.opener.document.getElementById( "rightSideExpression" ).value = expression;
            window.opener.document.getElementById( "rightSideTextualExpression" ).value = textualDescription;
        }
    }

    window.close();
}

function htmlEncode( str )
{   
    str = str.replace( /\(/g, "%28" );
    str = str.replace( /\)/g, "%29" );
    str = str.replace( /\*/g, "%2a" );
    str = str.replace( /\+/g, "%2b" );
    str = str.replace( /\-/g, "%2d" );
    str = str.replace( /\//g, "%2f" );
    
    return str;
}
