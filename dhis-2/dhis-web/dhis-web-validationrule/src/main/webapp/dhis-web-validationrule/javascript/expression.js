function showEditLeftSideExpressionForm()
{
    var description = $( "#leftSideDescription" ).val();
    var expression = $( "#leftSideExpression" ).val();
    var textualExpression = $( "#leftSideTextualExpression" ).val();
    var periodTypeName = $( "#periodTypeName" ).val();

    showExpressionForm( "left", description, expression, textualExpression, periodTypeName );
}

function showEditRightSideExpressionForm()
{
    var description = $( "#rightSideDescription" ).val();
    var expression = $( "#rightSideExpression" ).val();
    var textualExpression = $( "#rightSideTextualExpression" ).val();
    var periodTypeName = $( "#periodTypeName" ).val();

    showExpressionForm( "right", description, expression, textualExpression, periodTypeName );
}

function showExpressionForm( side, description, expression, textualExpression, periodTypeName )
{
    $.post( "showEditExpressionForm.action", {
        side : side,
        description : description,
        expression : expression,
        textualExpression : textualExpression,
        periodTypeName : periodTypeName
    }, function( data )
    {
    	$( "#dynamicContent" ).html( data );
        showPopupWindowById( 'dynamicContent', 755, 450 );
    }, 'html' );
}

function insertText( inputAreaName, inputText )
{
    insertTextCommon( inputAreaName, inputText );

    updateTextualExpression( inputAreaName );
}

function filterDataElements( dataSetName, filterName )
{
    var dataSet = $( "#" + dataSetName ).val();
    var dataSetId = dataSet.options[dataSet.selectedIndex].value;
    var filter = $( "#" + filterName ).val();
    var periodTypeName = getFieldValue( 'periodTypeName' );

    var url = "getFilteredDataElements.action";

    $.getJSON( url, {
        "dataSetId" : dataSetId,
        "periodTypeName" : periodTypeName,
        "filter" : filter
    }, function( json )
    {
        clearListById( "dataElementId" );

        var objects = json.operands;

        for ( var i = 0; i < objects.length; i++ )
        {
            addOptionById( "dataElementId", "[" + objects[i].operandId + "]", objects[i].operandName );
        }

    } );
}

function updateTextualExpression( expressionFieldName )
{
    var expression = $( "#" + expressionFieldName ).val();

    jQuery.postJSON( '../dhis-web-commons-ajax-json/getExpressionText.action', {
        expression : expression
    }, function( json )
    {
    	$( "#textualExpression" ).html( json.message );
    } );
}

function checkNotEmpty( field, message )
{
    if ( field.value.length == 0 )
    {
        setInnerHTML( field.name + "Info", message );
        $( '#' + field.name ).css( "background-color", "#ffc5c5" );
        return false;
    } else
    {
        setInnerHTML( field.name + "Info", '' );
        $( '#' + field.name ).css( "background-color", "#ffffff" );
    }

    return true;
}

function validateExpression()
{
    var description = byId( "expDescription" ).value;
    var expression = byId( "expression" ).value;

    if ( checkNotEmpty( byId( "expDescription" ), i18n_description_not_null ) == false )
        return;
    if ( checkNotEmpty( byId( "expression" ), i18n_expression_not_null ) == false )
        return;

    jQuery.postJSON( '../dhis-web-commons-ajax-json/getExpressionText.action', {
        expression : expression
    }, function( json )
    {
        byId( "textualExpression" ).innerHTML = json.message;
        if ( json.response == 'error' )
        {
            $( '#expression' ).css( "background-color", "#ffc5c5" );
            return;
        }
        var description = byId( "expDescription" ).value;
        var expression = byId( "expression" ).value;
        var textualDescription = byId( "textualExpression" ).innerHTML;
        var side = byId( "side" ).value;
        saveExpression( side, description, expression, textualDescription );
        disable( 'periodTypeName' );
        return true;
    } );
}

function validateExpressionReceived( xmlObject )
{
    var type = xmlObject.getAttribute( 'type' );
    var message = xmlObject.firstChild.nodeValue;

    if ( type == "success" )
    {
        var description = byId( "expDescription" ).value;
        var expression = byId( "expression" ).value;
        var textualDescription = byId( "textualExpression" ).innerHTML;
        var side = byId( "side" ).value;
        saveExpression( side, description, expression, textualDescription );
        disable( 'periodTypeName' );
    } 
    else if ( type == "error" )
    {
        byId( "textualExpression" ).innerHTML = message;
    }
}

function saveExpression( side, description, expression, textualDescription )
{
    if ( side == "left" )
    {
        $( "#leftSideDescription" ).val( description );
        $( "#leftSideExpression" ).val( expression );
        $( "#leftSideTextualExpression" ).val( textualDescription );
    } 
    else if ( side == "right" )
    {
    	$( "#rightSideDescription" ).val( description );
    	$( "#rightSideExpression" ).val( expression );
    	$( "#rightSideTextualExpression" ).val( textualDescription );
    }

    hideById( 'dynamicContent' );
    unLockScreen();
}

// -----------------------------------------------------------------------------
// Set Null Expression
// -----------------------------------------------------------------------------

function clearRuleExpression()
{
    var description = $( "#leftSideDescription" ).val();
    $( "#leftSideExpression" ).val( "" );
    $( "#leftSideTextualExpression" ).val( "" );
    saveExpression( "left", description, "", "" );

    description = $( "#rightSideDescription" ).val();
    $( "#rightSideExpression" ).val( "" );
    $( "#rightSideTextualExpression" ).val( "" );
    saveExpression( "right", description, "", "" );

	enable( "ruleType" );
	enable( "periodTypeName" );
}
