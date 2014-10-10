var selectedOrganisationUnit = null;

function organisationUnitSelected( organisationUnits )
{
    selectedOrganisationUnit = organisationUnits[0];
}

function validateRunAnalyseData()
{
    if ( analyseDataInvalid() )
    {
        $( '#startButton').attr( 'disabled', true );

        $.getJSON( "validateRunAnalysis.action", 
        {
            fromDate : getFieldValue( 'fromDate' ),
            toDate : getFieldValue( 'toDate' )
        }, 
        function( json )
        {
            if ( json.response == "success" )
            {
            	analyseData();
            }
            else
            {
            	setHeaderDelayMessage( json.message );
                $( '#startButton').removeAttr( 'disabled' );
            }
        } );
    }
}

function analyseDataInvalid()
{
    if ( $( "#fromDate" ).val().length == 0 )
    {
    	setHeaderDelayMessage( i18n_specify_a_start_date );
        return false;
    }

    if ( $( "#toDate" ).val().length == 0 )
    {
    	setHeaderDelayMessage( i18n_specify_an_ending_date );
        return false;
    }

    var dataSets = document.getElementById( "dataSets" );

    if ( dataSets.options.length == 0 )
    {
    	setHeaderDelayMessage( i18n_specify_dataset );
        return false;
    }

    return true;
}

function analyseData()
{
    setWaitMessage( i18n_analysing_please_wait );

    var url = "getAnalysis.action" + "?key=" + $( "#key" ).val() + "&toDate=" + $( "#toDate" ).val() + "&fromDate="
            + $( "#fromDate" ).val() + "&" + getParamString( "dataSets", "dataSets" );

    if ( byId( "standardDeviation" ) != null )
    {
        url += "&standardDeviation=" + $( "#standardDeviation" ).val();
    }

    $.get( url, function( data )
    {
    	hideMessage();
        $( "div#analysisInput" ).hide();
        $( "div#analysisResult" ).show();
        $( "div#analysisResult" ).html( data );

        $( '#startButton').removeAttr( 'disabled' );
    } );
}

function getFollowUpAnalysis()
{
    setWaitMessage( i18n_analysing_please_wait );

    var url = "getAnalysis.action?key=followup";

    $.get( url, function( data )
    {
        hideMessage();
        $( "div#analysisResult" ).show();
        $( "div#analysisResult" ).html( data );
    } );
}

function displayAnalysisInput()
{
	$( 'div#analysisInput' ).show();
    $( 'div#analysisResult' ).empty().hide();
}

function exportAnalysisResult( type )
{
    var url = 'exportAnalysisResult.action?type=' + type;
    window.location.href = url;
}
