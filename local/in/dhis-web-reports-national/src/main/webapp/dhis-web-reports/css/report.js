
var tempUrl = null;

function runAndViewReport( reportId, reportUrl )
{
   /* var url = "createTable.action?id=" + reportId + "&mode=report";
    
    if ( document.getElementById( "reportingPeriod" ) != null )
    {
        url += "&reportingPeriod=" + getListValue( "reportingPeriod" );
    }
    
    if ( document.getElementById( "parentOrganisationUnitId" ) != null )
    {
        url += "&parentOrganisationUnitId=" + getListValue( "parentOrganisationUnitId" );
    }
    
    if ( document.getElementById( "organisationUnitId" ) != null )
    {
        url += "&organisationUnitId=" + getListValue( "organisationUnitId" );
    }
    
	tempUrl = reportUrl;
    
    var request = new Request();
    request.setCallbackSuccess( runAndViewReportReceived );    
    request.send( url ); */
	
	$.post("createTable.action",
		{
			id : reportId,
			mode : report,
			reportingPeriod : getListValue( "reportingPeriod" ),
			parentOrganisationUnitId : getListValue( "parentOrganisationUnitId" ),
			organisationUnitId : getListValue( "organisationUnitId" )
		},
		function (data)
		{
			runAndViewReportReceived(data);
		},'xml');
}

function runAndViewReportReceived( messageElement )
{   
    getReportStatus();
}

function getReportStatus()
{   
    /* var url = "getStatus.action";
    
    var request = new Request();
    request.setResponseTypeXML( "status" );
    request.setCallbackSuccess( reportStatusReceived );    
    request.send( url ); */
	
	$.post("getStatus.action",
		{
		},
		function (data)
		{
			reportStatusReceived(data);
		},'xml');
}

function reportStatusReceived( xmlObject )
{
    var statusMessage = getElementValue( xmlObject, "statusMessage" );
    var finished = getElementValue( xmlObject, "finished" );
    
    if ( finished == "true" )
    {
        setMessage( i18n_process_completed );
        
        viewReport( tempUrl );        
    }
    else if ( statusMessage == null )
    {
        setMessage( i18n_please_wait );
        
        waitAndGetReportStatus( 2000 );
    }
    else
    {
        setMessage( i18n_please_wait + ". " + statusMessage + "..."  );
        
        waitAndGetReportStatus( 2000 );
    }
}

function waitAndGetReportStatus( millis )
{
    setTimeout( "getReportStatus();", millis );
}

function viewReport( url )
{
	var dialog = window.open( url, "_blank", "directories=no, height=800, width=800, \
		location=no, menubar=no, status=no, toolbar=no, resizable=yes, scrollbars=yes" );
}

function addReport()
{
    selectAllById( "selectedReportTables" );
    
    document.getElementById( "reportForm" ).submit();
}

function removeReport( id )
{
	var dialog = window.confirm( i18n_confirm_remove_report );
	
	if ( dialog )
	{
		window.location.href = "removeReport.action?id=" + id;
	}
}

function addToDashboard( id )
{
    var dialog = window.confirm( i18n_confirm_add_to_dashboard );
    
    if ( dialog )
    {
        var request = new Request(); 
        request.send( "addReportToDashboard.action?id=" + id );
    }
}
