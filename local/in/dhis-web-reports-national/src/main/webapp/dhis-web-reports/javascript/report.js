
var tempUrl = null;

function runAndViewReport( reportId, reportUrl )
{
    //var url = "createTable.action?id=" + reportId + "&mode=report";
    var params2;
    if ( document.getElementById( "reportingPeriod" ) != null )
    {
    	//url += "&reportingPeriod=" + getListValue( "reportingPeriod" );
        
		//params2 += "&reportingPeriod=" + getListValue( "reportingPeriod" );
		
		$.post("createTable.action",
			{
				id : reportId,
				mode : report,
				reportingPeriod : getListValue( "reportingPeriod" )
			},
			function (data)
			{
				runAndViewReportReceived(data);
			},'xml');
    }
    
    if ( document.getElementById( "parentOrganisationUnitId" ) != null )
    {
        //url += "&parentOrganisationUnitId=" + getListValue( "parentOrganisationUnitId" );
        //params2 += "&reportingPeriod=" + getListValue( "reportingPeriod" );
		$.post("createTable.action",
			{
				id : reportId,
				mode : report,
				parentOrganisationUnitId : getListValue( "parentOrganisationUnitId" )
			},
			function (data)
			{
				runAndViewReportReceived(data);
			},'xml');
    }
    
    if ( document.getElementById( "organisationUnitId" ) != null )
    {
        ///url += "&organisationUnitId=" + getListValue( "organisationUnitId" );
        //params2 += "&reportingPeriod=" + getListValue( "reportingPeriod" );
		$.post("createTable.action",
			{
				id : reportId,
				mode : report,
				organisationUnitId : getListValue( "organisationUnitId" )
			},
			function (data)
			{
				runAndViewReportReceived(data);
			},'xml');
    }
    
    /* tempUrl = reportUrl;
    
    var request = new Request();
    request.setCallbackSuccess( runAndViewReportReceived );    
    //request.send( url );

    var requestString = "createTable.action";
    var params = "id=" + reportId + "&mode=report" + params2;
    request.sendAsPost( params );
    request.send( requestString ); */

}

function runAndViewReportReceived( messageElement )
{   
    getReportStatus();
}

function getReportStatus()
{   
    /* //var url = "getStatus.action";
    
    var request = new Request();
    request.setResponseTypeXML( "status" );
    request.setCallbackSuccess( reportStatusReceived );    
    //request.send( url );

    var requestString = "getStatus.action";
    request.send( requestString ); */
	
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
        /* //window.location.href = "removeReport.action?id=" + id;
        var request = new Request();
        var requestString = "removeReport.action";
        var params = "id=" + id ;
        request.sendAsPost( params );
        request.send( requestString ); */
		
		$.post("removeReport.action",
		{
			id : id
		},
		function (data)
		{
			reportStatusReceived(data);
		},'xml');
    }
}

function addToDashboard( id )
{
    var dialog = window.confirm( i18n_confirm_add_to_dashboard );
    
    if ( dialog )
    {
        var request = new Request(); 
        //request.send( "addReportToDashboard.action?id=" + id );
        var requestString = "addReportToDashboard.action";
        var params = "id=" + id ;
        request.sendAsPost( params );
        request.send( requestString );
    }
}
