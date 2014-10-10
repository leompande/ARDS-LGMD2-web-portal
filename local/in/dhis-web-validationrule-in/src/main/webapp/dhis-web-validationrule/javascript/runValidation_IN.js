
function validateRunValidation()
{
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( runValidationCompleted );
    
    //request.send( 'validateRunValidation.action?startDate=' + getFieldValue( 'startDate' ) +
    //    '&endDate=' + getFieldValue( 'endDate' ) );

    var requestString = "validateRunValidation.action";
    var params = "startDate=" + getFieldValue( "startDate" ) + "&endDate=" + getFieldValue( "endDate" );
    request.sendAsPost( params );
    request.send( requestString );

    return false;
}

function runValidationCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'runValidationFormIN' );
        
        if (document.pressed == 'validate')
        	form.action = 'detailedValidationAnalysisResultAction.action';
        else
        	form.action = 'validationAnalysisByAverageAction.action';

        var sWidth = 850;
		var sHeight = 650;
    	var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    	var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    	window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
        		 
        form.submit();
        
        
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_validation_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
        
        setTimeout(
				function()
				{
					document.getElementById( 'message' ).style.display = 'none';
				},
				4000
		);

    }
}

function viewValidationResultDetails( validationRuleId, sourceId, periodId )
{
	var url = "viewValidationResultDetails.action?validationRuleId=" + validationRuleId +
		"&sourceId=" + sourceId + "&periodId=" + periodId;
	
	var dialog = window.open( url, "_blank", "directories=no, \
    		 height=500, width=500, location=no, menubar=no, status=no, \
    		 toolbar=no, resizable=yes");
}
